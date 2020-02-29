package com.example.reunion.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.View
import androidx.appcompat.view.menu.MenuView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityRegisterFaceBinding
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.view_model.RegisterFaceViewModel
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.lljjcoder.style.citylist.CityListSelectActivity
import com.lljjcoder.style.citylist.bean.CityInfoBean
import kotlinx.android.synthetic.main.activity_register_face.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@Suppress("IMPLICIT_CAST_TO_ANY")
class RegisterFaceActivity : BaseActivity() {
    companion object{
        const val QUEQUEST_CODE = 20124
    }
    private lateinit var mBinding:ActivityRegisterFaceBinding
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    private lateinit var imagePreview: Preview
    private lateinit var imageCapture: ImageCapture
    private lateinit var imageAnalysis: ImageAnalysis
    private val executor = Executors.newSingleThreadExecutor()
    private val mViewModel by lazy {
        setViewModel(this,RegisterFaceViewModel::class.java)
    }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_register_face)
        mBinding.lifecycleOwner = this
        mBinding.activity = this
        mBinding.viewModel = mViewModel
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        if (allPermissionsGranted()) {
            facePreview.post { startCamera() }
        }
        mViewModel.usePhoto.value = intent.getBooleanExtra("usePhoto",false)
        if (mViewModel.usePhoto.value!!){
            initPhoto()
        }
        initObserve()

    }

    private fun initPhoto(){
        mViewModel.useImageView.value = true
        openPhoto()
    }

    fun openPhoto(view: View? =  null){
        if (!mViewModel.uploading&&(mViewModel.usePhoto.value == true||mViewModel.state.value == 15))
            PictureHelper.instance.openPhoto(this)
    }

    fun onBack(view: View? =  null){
        onBackPressed()
    }

    override fun onBackPressed() {
        if (mViewModel.uploadSuccess){
            val intent = Intent()
            intent.putExtra("newFace",mViewModel.newBean)
            setResult(Activity.RESULT_OK,intent)
            super.onBackPressed()
        }else{
            super.onBackPressed()
        }
    }

    private fun initObserve(){
        mViewModel.onTakePicture.observe(this, Observer {
            if (it)
                takePicture()
        })

        mViewModel.compressPicture.observe(this, Observer {
            if (it.isNotEmpty()){
                GlobalScope.launch(Dispatchers.IO) {
                    mViewModel.path =
                        PictureHelper.instance.compressImage(this@RegisterFaceActivity,it,size = 400)
                    mViewModel.onUploadFaceToTencent()
                }
            }
        })

        mViewModel.finish.observe(this, Observer {
            if (it)
                onBackPressed()
        })
    }

    override fun permissionCheckPass() {
        startCamera()
    }

    override fun insertMustPermission(): ArrayList<String>? {
        return ArrayList<String>().apply {
            add(Manifest.permission.CAMERA)
        }
    }

    @SuppressLint("RestrictedApi")
    private fun startCamera() {
        if (mViewModel.usePhoto.value == true){
            return
        }
        imagePreview = Preview.Builder().apply {
            setTargetResolution(Size(500,500))
            setTargetRotation(facePreview.display.rotation)
        }.build()
        imagePreview.setSurfaceProvider(facePreview.previewSurfaceProvider)

        imageCapture = ImageCapture.Builder().apply {
            setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            setTargetAspectRatioCustom(Rational(1,1))
        }.build()

        imageAnalysis = ImageAnalysis.Builder().apply {
            setImageQueueDepth(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        }.build()
        val faceAnalysis = FaceAnalyzer{ num,eulerAngleY->
            mViewModel.faceNum.value = num
            when(num){
                0 ->{
                    mViewModel.state.value = 0
                }
                1 ->{
                    if (eulerAngleY in -12f .. 12f){
                        mViewModel.takePicture()
                    }else{
                        mViewModel.state.value = 6
                    }
                }
                else->{
                    mViewModel.state.value = 3
                }
            }
        }
        mViewModel.takePictureEnd.observe(this, Observer {
            faceAnalysis.onFaceAnalyze = !it
        })
        mViewModel.useImageView.observe(this, Observer {
            if (it&&!faceAnalysis.onFaceAnalyze)
                faceAnalysis.onFaceAnalyze = false
        })
        mViewModel.state.observe(this, Observer {
            if (it == 15){
                mViewModel.useImageView.value = false
                mViewModel.takePictureEnd.value = false
            }
        })
        imageAnalysis.setAnalyzer(executor, faceAnalysis)

        val cameraSelector = CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.bindToLifecycle(this, cameraSelector, imagePreview,imageCapture,imageAnalysis)
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePicture() {
        var path = PictureHelper.getCachePath()
        path += "${System.currentTimeMillis()}.jpg"
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(File(path)).build()
        imageCapture.takePicture(outputFileOptions,executor, object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                mViewModel.takePictureSuccess(path)
            }

            override fun onError(exception: ImageCaptureException) {
                mViewModel.takePictureFaild(exception)
            }
        })
    }

    fun setCameraBg(state:Int) = when(state){
        1,4,8,14 ->{//等待拍摄
            resources.getDrawable(R.drawable.face_register_waiting)
        }
        2,5,11->{//拍摄完成
            resources.getDrawable(R.drawable.face_register_success)
        }
        -1,3,6,9,10,12,13,15 ->{//错误
            resources.getDrawable(R.drawable.face_register_error)
        }
        else ->{//
            resources.getDrawable(R.drawable.face_register)
        }
    }

    class FaceAnalyzer(private val listener:(Int,Float)->Unit):ImageAnalysis.Analyzer{
        private var lastAnalyzedTimestamp = 0L
        val faceOpts = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .build()
        var onFaceAnalyze = true

        @SuppressLint("UnsafeExperimentalUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            val currentTimestamp = System.currentTimeMillis()
            if (currentTimestamp - lastAnalyzedTimestamp >= TimeUnit.SECONDS.toMillis(1)&&onFaceAnalyze){
                val mediaImage = imageProxy.image?:return
                val imageRotation = degreesToFirebaseRotation(imageProxy.imageInfo.rotationDegrees)
                val image = FirebaseVisionImage.fromMediaImage(mediaImage, imageRotation)
                val detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(faceOpts)
                detector.detectInImage(image).addOnSuccessListener {
                    if (it.size>0){
                        listener.invoke(it.size,it[0].headEulerAngleY)
                    }else{
                        listener.invoke(it.size,0f)
                    }
                }.addOnFailureListener{
                    listener.invoke(-1,0f)
                }
                lastAnalyzedTimestamp = currentTimestamp
            }
            imageProxy.close()
        }

        private fun degreesToFirebaseRotation(degrees: Int): Int = when(degrees) {
            0 -> FirebaseVisionImageMetadata.ROTATION_0
            90 -> FirebaseVisionImageMetadata.ROTATION_90
            180 -> FirebaseVisionImageMetadata.ROTATION_180
            270 -> FirebaseVisionImageMetadata.ROTATION_270
            else -> throw Exception("Rotation must be 0, 90, 180, or 270.")
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PictureHelper.REQUEST_DEFAULT){
            if (resultCode == Activity.RESULT_OK){
                val uri = PictureHelper.instance.obtainUriFromPhoto(data)
                PictureHelper.instance.cropCircleImage(this,uri)
            }else{
                if (mViewModel.isFirst)
                    finish()
            }
        }
        else if (requestCode == PictureHelper.REQUEST_CROUP){
            if (resultCode == Activity.RESULT_OK){
                mViewModel.isFirst = false
                mViewModel.analyzeImage(PictureHelper.instance.obtainCropUri(data))
            }else{
                if (mViewModel.isFirst)
                    finish()
            }
        }
    }
}
