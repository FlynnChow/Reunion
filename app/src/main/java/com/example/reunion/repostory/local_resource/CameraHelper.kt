package com.example.reunion.repostory.local_resource

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import com.example.reunion.MyApplication
import com.example.reunion.base.BaseActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okio.buffer
import okio.sink
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList

class CameraHelper(private val activity:BaseActivity,private val textureView: TextureView) {

    companion object{
        const val PREVIEW_WIDTH = 720
        const val PREVIEW_HEIGHT = 1280
        const val SAVE_WIDTH = 720
        const val SAVE_HEIGHT = 1280
    }
    private val savePath = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + File.separator + "cachePicture/"
    private var takePcListener:((String)->Unit)? = null

    private var mCameraId = ""

    private lateinit var mCameraCharacteristics:CameraCharacteristics
    private lateinit var mCameraManager:CameraManager
    private var mImageReader:ImageReader? = null
    private var mCameraDevice:CameraDevice? = null
    private var mCameraCaptureSession:CameraCaptureSession? = null

    var mCameraFacing = CameraCharacteristics.LENS_FACING_BACK //摄像头方向
    private var mCameraOrientation = 0 //摄像头角度
    private var mDisplayRotation = activity.windowManager.defaultDisplay.rotation //手机屏幕方向

    private var mPreviewSize = Size(
        PREVIEW_WIDTH,
        PREVIEW_HEIGHT
    )
    private var mSaveSize = Size(
        SAVE_WIDTH,
        SAVE_HEIGHT
    )

    private val handlerThread = HandlerThread("CameraThread")
    private lateinit var mCameraHandler: Handler

    private var isCanTakePic = false
    private var isCanExchangeFac = false
    private var isInitCamera = false

    init {
        handlerThread.start()
        mCameraHandler = Handler(handlerThread.looper)

        textureView.surfaceTextureListener = object :TextureView.SurfaceTextureListener{
            override fun onSurfaceTextureSizeChanged(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
            }

            override fun onSurfaceTextureUpdated(surface: SurfaceTexture?) {
            }

            override fun onSurfaceTextureDestroyed(surface: SurfaceTexture?): Boolean {
                releaseCamera()
                return true
            }

            override fun onSurfaceTextureAvailable(
                surface: SurfaceTexture?,
                width: Int,
                height: Int
            ) {
                if (isInitCamera)
                    initCamera()
            }
        }
    }

    fun startCamera(){
        if (textureView.isAvailable)
            initCamera()
        else
            isInitCamera = true
    }

    private fun isExchangeWidthAndHeight(displayRotation:Int,sensorOrientation:Int)
        = when(displayRotation){
            Surface.ROTATION_0 ,Surface.ROTATION_180 ->{
                sensorOrientation == 90||sensorOrientation == 270
            }
            Surface.ROTATION_90,Surface.ROTATION_270 ->{
                sensorOrientation == 0||sensorOrientation == 180
            }
            else ->false
        }

    private fun getBestSize(targetWidth:Int,targetHeight:Int,maxWidth:Int,maxHeight:Int,sizeList:List<Size>):Size{
        val largerSize = ArrayList<Size>()
        val smallerSize = ArrayList<Size>()

        for (size in sizeList){
            if (size.width <= maxWidth && size.height <= maxHeight && size.width == size.height*maxWidth/maxHeight){
                if (size.width >= targetWidth && size.height >= targetHeight)
                    largerSize.add(size)
                else
                    smallerSize.add(size)
            }
        }

        return when{
            largerSize.size > 0 -> Collections.min(largerSize,comparatorSize)
            smallerSize.size > 0 -> Collections.max(smallerSize,comparatorSize)
            else ->sizeList[0]
        }
    }

    private val comparatorSize = object :Comparator<Size>{
        override fun compare(size1: Size?, size2: Size?): Int {
            if (size1 != null&&size2!=null){
                return (size1.width*size1.height).compareTo((size2.width*size2.height))
            }
            return -1
        }
    }

    private val onImageAvailableListener = ImageReader.OnImageAvailableListener{
        val image = it.acquireNextImage()
        val byteBuffer = image.planes[0].buffer
        val byteArray = ByteArray(byteBuffer.remaining())
        byteBuffer.get(byteArray)
        it.close()

        val saveDir = File(savePath)
        if (!saveDir.exists()) saveDir.mkdirs()
        val pictureName = "${System.currentTimeMillis()}.jpg"

        GlobalScope.launch(Dispatchers.IO) {
            val tempBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
            val resultBitmap = dealBitmap(tempBitmap,mCameraOrientation)
            val saveFile = File(savePath + pictureName)
            if (saveFile.exists()) saveFile.delete()
            val outStream = FileOutputStream(File(savePath + pictureName))
            resultBitmap.compress(Bitmap.CompressFormat.JPEG,100,outStream)
            outStream.close()
            takePcListener?.invoke(saveFile.absolutePath)
        }
    }

    fun takePicture(listener:((String)->Unit)? = null){
        takePcListener = listener
        if (mCameraDevice == null || !isCanTakePic||!textureView.isAvailable) return
        mCameraDevice?.apply {
            val captureBuilder = createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            captureBuilder.addTarget(mImageReader?.surface!!)
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
            captureBuilder.set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            mCameraCaptureSession?.capture(captureBuilder.build(),null,mCameraHandler)?:activity.toast("拍照失败")
        }
    }

    private fun dealBitmap(bitmap: Bitmap,rotate:Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotate.toFloat())
        if(mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT){
            val matrix = Matrix()
            matrix.postScale(-1f,1f)
        }
        return Bitmap.createBitmap(bitmap,0,0,bitmap.width,bitmap.height,matrix,true)
    }


    private fun initCamera(){
        mCameraManager = activity.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraIdList = mCameraManager.cameraIdList
        if (cameraIdList.isEmpty()){
            activity.toast("没有可用的相机")
        }

        for (id in cameraIdList){
            val cameraCharacteristics = mCameraManager.getCameraCharacteristics(id)
            val facing = cameraCharacteristics.get(CameraCharacteristics.LENS_FACING)

            if (facing == mCameraFacing){
                mCameraId = id
                mCameraCharacteristics = cameraCharacteristics
                break//设备有时候有多个摄像头。。真坑
            }
        }
        val supperLevel = mCameraCharacteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
        if (supperLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LEGACY){
            activity.toast("硬件不支持新特性")
        }

        mCameraOrientation = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)?:0

        val configurationMap = mCameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
        val savePicSize = configurationMap?.getOutputSizes(ImageFormat.JPEG)
        val previewSize = configurationMap?.getOutputSizes(SurfaceTexture::class.java)
        val exchange = isExchangeWidthAndHeight(mDisplayRotation,mCameraOrientation)

        mSaveSize = getBestSize(
            if (exchange) mSaveSize.height else mSaveSize.width,
            if (exchange) mSaveSize.width else mSaveSize.height,
            if (exchange) textureView.height else textureView.width,
            if (exchange) textureView.width else textureView.height,
            savePicSize!!.toList())
        mPreviewSize = getBestSize(
            if (exchange) mPreviewSize.height else mPreviewSize.width,
            if (exchange) mPreviewSize.width else mPreviewSize.height,
            if (exchange) textureView.height else textureView.width,
            if (exchange) textureView.width else textureView.height,
            previewSize?.toList()!!)

        val rotate = activity.windowManager.defaultDisplay.rotation
        if (rotate == Surface.ROTATION_90 || rotate == Surface.ROTATION_270){
            textureView.surfaceTexture.setDefaultBufferSize(mPreviewSize.height,mPreviewSize.width)
        }else{
            textureView.surfaceTexture.setDefaultBufferSize(mPreviewSize.width,mPreviewSize.height)
        }
        setTransform()

        mImageReader = ImageReader.newInstance(mSaveSize.width,mSaveSize.height,ImageFormat.JPEG,1)
        mImageReader?.setOnImageAvailableListener(onImageAvailableListener,mCameraHandler)

        openCamera()

    }

    private fun setTransform(){
        if (activity == null||textureView==null) return
        val rotate = activity.windowManager.defaultDisplay.rotation
        val matrix = Matrix()
        val viewRect = RectF(0f,0f,textureView.width.toFloat(),textureView.height.toFloat())
        val bufferRect = RectF(0f,0f,mPreviewSize.width.toFloat(),mPreviewSize.height.toFloat())
        if (rotate == Surface.ROTATION_90 || rotate == Surface.ROTATION_270){
            bufferRect.offset(viewRect.centerX()-bufferRect.centerX(),viewRect.centerY()-bufferRect.centerY())
            matrix.setRectToRect(viewRect,bufferRect,Matrix.ScaleToFit.FILL)
            val scale = (textureView.height / viewRect.height()).coerceAtLeast(textureView.width / viewRect.width())
            matrix.postScale(scale,scale,viewRect.centerX(),viewRect.height())
            matrix.postRotate(90 * (rotate-2).toFloat(),viewRect.centerX(),viewRect.centerY())
        }else if(rotate == Surface.ROTATION_180){
            matrix.postRotate(180f,viewRect.centerX(),viewRect.centerY())
        }
        textureView.setTransform(matrix)
    }

    private fun openCamera(){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activity.toast("没有相机权限！")
            return
        }
        mCameraManager.openCamera(mCameraId,object :CameraDevice.StateCallback(){
            override fun onOpened(camera: CameraDevice) {
                mCameraDevice = camera
                createCaptureSession(camera)
            }

            override fun onDisconnected(camera: CameraDevice) {

            }

            override fun onError(camera: CameraDevice, error: Int) {
                activity.toast("相机打开失败")
            }
        },mCameraHandler)
    }

    private fun createCaptureSession(mCameraDevice: CameraDevice){
        val requestCaptureBuilder = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)

        val surface = Surface(textureView.surfaceTexture)
        requestCaptureBuilder.apply {
            addTarget(surface)
            set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
            set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
        }

        mCameraDevice.createCaptureSession(arrayListOf(surface,mImageReader?.surface),object :CameraCaptureSession.StateCallback(){
            override fun onConfigureFailed(session: CameraCaptureSession) {
                activity.toast("开启预览会话失败")
            }

            override fun onConfigured(session: CameraCaptureSession) {
                mCameraCaptureSession = session
                session.setRepeatingRequest(requestCaptureBuilder.build(),mCaptureCallback,mCameraHandler)
            }
        },mCameraHandler)
    }

    fun exchangeCamera(){
        if (mCameraFacing == null|| !isCanExchangeFac||!textureView.isAvailable) return
        mCameraFacing = if(mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT) CameraCharacteristics.LENS_FACING_BACK
        else CameraCharacteristics.LENS_FACING_FRONT

        mPreviewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        releaseCamera()
        initCamera()
    }

    private val mCaptureCallback = object :CameraCaptureSession.CaptureCallback(){
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            isCanExchangeFac = true
            isCanTakePic = true
        }

        override fun onCaptureFailed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            failure: CaptureFailure
        ) {
            super.onCaptureFailed(session, request, failure)
            activity.toast("开启预览失败")
        }
    }

    private fun releaseCamera(){
        mCameraCaptureSession?.close()
        mCameraCaptureSession = null

        mCameraDevice?.close()
        mCameraDevice = null

        mImageReader?.close()
        mImageReader = null

        isCanExchangeFac = false
        isCanTakePic = false
    }

    fun onDestory(){
        releaseCamera()
        handlerThread.quitSafely()
    }
}