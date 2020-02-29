package com.example.reunion.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityCameraBinding
import com.example.reunion.repostory.local_resource.CameraHelper
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.remote_resource.CameraViewModel
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class CameraActivity : BaseActivity() {
    private lateinit var mBinding:ActivityCameraBinding
    private lateinit var cameraHelper:CameraHelper
    private val mViewModel:CameraViewModel by lazy {
        setViewModel(this,CameraViewModel::class.java)
    }
    override fun create(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_camera)
        mBinding.lifecycleOwner = this
        mBinding.activity = this

        cameraHelper = CameraHelper(this, cameraView)
        faceDrawView.post {
            cameraHelper.setFaceView(faceDrawView)
        }

        cameraHelper.addFaceListener {
            faceDrawView?.updateFaceList(it)
        }

        cameraSeekBar?.setOnSeekBarChangeListener(object :SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                cameraHelper.setZoom(progress/100f)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }


    override fun insertMustPermission(): ArrayList<String>? {
        return ArrayList<String>().apply {
            add(Manifest.permission.CAMERA)
        }
    }

    override fun onResume() {
        super.onResume()
        cameraHelper.mCameraFacing = mViewModel.mCameraFacing
        cameraHelper.zoomLevel = mViewModel.zoomLevel.value!!
        if (allPermissionsGranted()){
           cameraView.post {
               cameraHelper.startCamera()
           }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraHelper.onDestroy()
    }


    override fun onStop() {
        mViewModel.mCameraFacing = cameraHelper.mCameraFacing
        mViewModel.zoomLevel.value = cameraHelper.zoomLevel
        super.onStop()
    }

    override fun permissionCheckPass() = cameraHelper.startCamera()

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        ev?.apply {
            cameraHelper.onTouch(this){
                cameraSeekBar?.progress = (it*100).toInt()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    fun takePicture(view: View){
        cameraHelper.takePicture{
            val intent = Intent(this,FaceImageActivity::class.java)
            intent.putExtra("picture",it)
            startActivity(intent)
            finish()
        }
    }

    fun exchangeCamera(view: View){
        cameraHelper.exchangeCamera()
        cameraSeekBar?.progress = 0
    }

    fun onBack(view:View){
        finish()
    }

    fun addZoom(view:View){
        cameraHelper.changeZoom(1){
            cameraSeekBar?.progress = (it*100).toInt()
        }
    }

    fun subZoom(view:View){
        cameraHelper.changeZoom(-1){
            cameraSeekBar?.progress = (it*100).toInt()
        }
    }

    fun openPhoto(view:View){
        PictureHelper.instance.openPhoto(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PictureHelper.REQUEST_DEFAULT&&resultCode == Activity.RESULT_OK){
            val uri = PictureHelper.instance.obtainUriFromPhoto(data)
            PictureHelper.instance.cropImage(this,uri)
        }else if (requestCode == PictureHelper.REQUEST_CROUP&&resultCode == Activity.RESULT_OK){
            val uri = PictureHelper.instance.obtainCropUri(data)
            val oriPath = PictureHelper.instance.obtainPathFromUri(uri)
            val intent = Intent(this,FaceImageActivity::class.java)
            intent.putExtra("picture",oriPath)
            startActivity(intent)
            finish()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
