package com.example.reunion.view_model
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.util.Log
import android.view.View
import androidx.camera.core.ImageCaptureException
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.customize.UploadRequestBody
import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.FaceRemoteModel
import com.example.reunion.util.NormalUtil
import com.example.reunion.util.StringDealerUtil
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.vansuita.gaussianblur.GaussianBlur
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import java.io.File
import java.io.IOException
import java.lang.Exception


class RegisterFaceViewModel:BaseViewModel() {
    val remote by lazy { FaceRemoteModel() }

    //使用相册
    val usePhoto = MutableLiveData(false)
    //拍照成功
    val takePictureEnd = MutableLiveData(false)

    //拍照
    val onTakePicture = MutableLiveData(false)

    //显示imageView
    val useImageView = MutableLiveData(false)

    val bitmap = MutableLiveData<Bitmap>()

    val uri = MutableLiveData<Uri>()

    //倒计时
    val timingNum = MutableLiveData(3)

    //拍照等待
    val waiting = MutableLiveData(false)

    //结束
    val finish = MutableLiveData(false)

    val compressPicture = MutableLiveData("")

    //识别出人脸的数量
    val faceNum = MutableLiveData(0)

    //结束倒计时
    val endNum = MutableLiveData(3)

    //拍照状态
    var lastState = -1000

    val state = MutableLiveData(-1000)

    var path = ""

    var isFirst = true

    //正在录入中
    var uploading = false

    //录入成功
    var uploadSuccess = false

    var newBean:FaceBean? = null

    @Synchronized
    fun takePicture(){
        if (takePictureEnd.value == true ||faceNum.value != 1||waiting.value == true)
            return
        state.value = 1
        timingNum.value = 3
        waiting.value = true
        launch {
            for (index in 1 .. 18){
                delay(250)
                if (faceNum.value != 1||state.value == 6){
                    timingNum.value = 3
                    waiting.value = false
                    return@launch
                }
                if (index % 6 == 0){
                    timingNum.value = 3 - index/6
                }
            }
            launchUI{
                if (takePictureEnd.value != true&&faceNum.value == 1&&state.value != 6){
                    onTakePicture.value = true
                }else{
                    waiting.value = false
                }
            }
        }
    }

    fun takePictureSuccess(path: String){
        launchUI {
            useImageView.value = true
            takePictureEnd.value = true
            waiting.value = false
            state.value = 8
            this.path = path

            launchIO{
                val fileBitmap = BitmapFactory.decodeFile(path)
                val matrix = Matrix()
                matrix.postRotate(-90f)
                matrix.postScale(-1f,1f)
                val changeBitmap =  Bitmap.createBitmap(fileBitmap,0,0,fileBitmap.width,fileBitmap.height,matrix,true)
                val blurredBitmap: Bitmap = GaussianBlur.with(MyApplication.app).render(changeBitmap)
                launchUI {
                    useImageView.value = true
                    bitmap.value = blurredBitmap
                    onCompress()
                }
            }
        }
    }

    fun analyzeImage(uri: Uri){
        this.uri.value = uri
        val image: FirebaseVisionImage
        state.value = 14
        try {
            image = FirebaseVisionImage.fromFilePath(MyApplication.app, uri)
            val faceOpts = FirebaseVisionFaceDetectorOptions.Builder()
                .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                .build()
            val detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(faceOpts)
            detector.detectInImage(image).addOnSuccessListener {
                faceNum.value = it.size
                when(it.size){
                    0 ->{
                        state.value = 10
                    }
                    1 ->{
                        if (it[0].headEulerAngleY in -18f .. 18f){
                            loadUri(uri)
                        }else{
                            state.value = 12
                        }
                    }
                    else ->{
                        state.value = 13
                    }
                }
            }.addOnFailureListener{
                state.value = -2
            }
        } catch (e: IOException) {
            toast.value = "图像分析错误"
            state.value = -2
        }
    }

    private fun loadUri(uri:Uri){
        state.value = 8
        launchIO {
            val path = PictureHelper.instance.obtainPathFromUri(uri)
            this.path = path?:""
            if (path != null){
                val fileImage = BitmapFactory.decodeFile(path)
                val blurredBitmap: Bitmap = GaussianBlur.with(MyApplication.app).render(fileImage)
                launchUI{
                    bitmap.value = blurredBitmap
                    onCompress()
                }
            }else{
                state.value = -2
            }
        }
    }

    fun onCompress(){
        uploading = true
        if (usePhoto.value == true){
            state.value = 11
        }else{
            state.value = 2
        }

        compressPicture.value = path
    }

    fun onUploadFaceToTencent(){
        launch ({
            if(lastState != -1000){
                state.value = lastState
                lastState = -1000
            }
            val file = File(path)
            val uploadBody = UploadRequestBody.getRequestBody(file,"image")
            val body = MultipartBody.Builder()
                .addFormDataPart("uId",UserHelper.getUser()?.uId?:"")
                .addFormDataPart("file",file.name,uploadBody)
                .build()
            val data = remote.createNewFace(body)
            launchUI {
                when(data.code){
                    200 ->{
                        toast.value = "录入成功！"
                        uploadSuccess = true
                        newBean = data.data
                        endTime()
                    }
                    405 ->{
                        uploading = false
                        state.value = 15
                        toast.value = "面部录入失败：录入的面部与之前的差距过大"
                    }
                    else->{
                        uploading = false
                        state.value = 15
                        toast.value = "面部录入失败："+data.msg
                    }
                }
            }
        },{
            lastState = state.value?:-1000
            state.value = 9
            toast.value = it.message
        })
    }

    fun retryUpload(view: View? = null){
        if (lastState != -1000)
            onUploadFaceToTencent()
    }

    private suspend fun endTime(){
        state.value = 5
        for (index in 3 downTo 0){
            endNum.value = index
            delay(1000)
        }
        finish.value = true
    }


    fun takePictureFaild(exception: ImageCaptureException){
        toast.value = "拍照失败："+exception.message
        state.value = -1
        takePictureEnd.value = true
    }

    fun setContentText(state:Int,time:Int,num:Int,endTime:Int) = when(state){
        1 ->{//等待拍摄
            "请保持稳定，${time}秒后拍摄"
        }
        4 ->{//正在拍摄中
            "正在拍摄中"
        }
        2 ->{//拍摄完成
            "拍摄成功！等待录入"
        }
        0 ->{
            "请确保人脸在圆圈内"
        }
        -1 ->{
            "拍照发生错误"
        }
        3 ->{
            "检测出${num}张人脸\n请确保圆圈只有一张人脸"
        }
        5->{
            "录入完成\n${endTime}后自动退出"
        }
        6->{
            "请调整面部\n保证正脸对准镜头"
        }
        8->{
            "正在处理中"
        }
        9->{
            "上传失败，点我重试"
        }
        15->{
            "录入失败，图片不符合要求"
        }
        10 ->{//相册，没有检测人脸
            "照片未检测出人脸\n请尝试重新选择"
        }
        11 ->{//相册，检测成功，正在录入
            "检测成功！等待录入"
        }
        12 ->{//相册，角度不合适
            "面部角度过偏\n点击图片重新选择"
        }
        13 ->{//相册，检测到太多人
            "检测出${num}张人脸,请确保只包含一张人脸\n点击图片重新选择"
        }
        14 ->{//检测...
            "正在检测中.."
        }
        -2 ->{//相册，检测错误
            "检测过程发生错误,点击图片重新选择"
        }
        else ->{
            ""
        }
    }
}