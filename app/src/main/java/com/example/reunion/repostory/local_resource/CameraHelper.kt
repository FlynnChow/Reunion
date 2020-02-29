package com.example.reunion.repostory.local_resource

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Environment
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.util.Size
import android.view.MotionEvent
import android.view.Surface
import android.view.TextureView
import androidx.core.content.ContextCompat
import com.example.reunion.base.BaseActivity
import com.example.reunion.customize.FaceDrawView
import com.example.reunion.customize.MyTextureView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt

class CameraHelper(private val activity:BaseActivity,private val textureView: MyTextureView) {

    companion object{
        const val PREVIEW_WIDTH = 2160
        const val PREVIEW_HEIGHT = 2880
        const val SAVE_WIDTH = 720
        const val SAVE_HEIGHT = 1280
    }
    private val savePath = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!.absolutePath + File.separator + "cachePicture/"
    private var takePcListener:((String)->Unit)? = null
    private var faceListener:((ArrayList<RectF>)->Unit)? = null
    private var faceView:FaceDrawView ? = null

    private var mCameraId = ""

    private lateinit var mCameraCharacteristics:CameraCharacteristics
    private lateinit var mCameraManager:CameraManager
    private var mImageReader:ImageReader? = null
    private var mCameraDevice:CameraDevice? = null
    private var mCameraCaptureSession:CameraCaptureSession? = null

    var mCameraFacing = CameraCharacteristics.LENS_FACING_BACK //摄像头方向
    private var mCameraOrientation = 0 //摄像头角度
    private var mDisplayRotation = activity.windowManager.defaultDisplay.rotation //手机屏幕方向

    private var mFaceMode = CaptureResult.STATISTICS_FACE_DETECT_MODE_OFF
    private var openFace = false
    private var mFaceMatrix = Matrix()
    private var mFaceList = ArrayList<RectF>()


    private var mPreviewSize = Size(
        PREVIEW_WIDTH,
        PREVIEW_HEIGHT
    )
    private var mSaveSize = Size(
        SAVE_WIDTH,
        SAVE_HEIGHT
    )

    private var cPixelSize = Size(
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
        GlobalScope.launch(Dispatchers.Default) {
            delay(500)
            launch(Dispatchers.Main) {
                resetCamera()
            }
        }
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
                return java.lang.Long.signum(size1.width.toLong() * size1.height - size2.width.toLong() * size2.height)
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
            mCameraCaptureSession?.stopRepeating()
            mCameraCaptureSession?.abortCaptures()
            mCameraCaptureSession?.capture(captureBuilder.build(),mCaptureCallback,mCameraHandler)?:activity.toast("拍照失败")
        }
    }

    private fun dealBitmap(bitmap: Bitmap,rotate:Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotate.toFloat())
        if(mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT){
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

        val activeArraySizeRect = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)!!
        val width = activeArraySizeRect.width()
        val height = activeArraySizeRect.width()*mPreviewSize.height/mPreviewSize.width
        mPreviewSize = Size(width,height)

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

        textureView.surfaceTexture.setDefaultBufferSize(mPreviewSize.width, mPreviewSize.height)
        val orientation = activity.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            textureView.setAspectRatio(mPreviewSize.width,mPreviewSize.height)
        }else{
            textureView.setAspectRatio(mPreviewSize.height,mPreviewSize.width)
        }
        setTransform()
        mImageReader = ImageReader.newInstance(mSaveSize.width,mSaveSize.height,ImageFormat.JPEG,1)
        mImageReader?.setOnImageAvailableListener(onImageAvailableListener,mCameraHandler)
        if (openFace){
            initFaceDetect()
        }
        openCamera()
    }

    fun setFaceView(faceView:FaceDrawView){
        this.faceView = faceView
        this.faceView!!.setAspectRatio(textureView.width,textureView.height)
    }

    private fun initFaceDetect(){
        val faceCount = mCameraCharacteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT)
        val faceModes = mCameraCharacteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES)?:IntArray(0)
        mFaceMode = when {
            faceModes.contains(CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL) -> CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL
            faceModes.contains(CaptureRequest.STATISTICS_FACE_DETECT_MODE_SIMPLE) -> CaptureRequest.STATISTICS_FACE_DETECT_MODE_FULL
            else -> CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF
        }
        if (mFaceMode == CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF) {
            return
        }

        val activeArraySizeRect = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE) //获取成像区域
        val scaledWidth = mPreviewSize.width / activeArraySizeRect?.width()?.toFloat()!!
        val scaledHeight = mPreviewSize.height / activeArraySizeRect.height().toFloat()
        val mirror = mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT

        mFaceMatrix.setRotate(mCameraOrientation.toFloat())
        mFaceMatrix.postScale(if (mirror) -scaledWidth else scaledWidth, scaledHeight)
        if (isExchangeWidthAndHeight(mDisplayRotation, mCameraOrientation))
            mFaceMatrix.postTranslate(mPreviewSize.height.toFloat(), mPreviewSize.width.toFloat())

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
        val surface = Surface(textureView.surfaceTexture)
        mCameraDevice.createCaptureSession(arrayListOf(surface,mImageReader?.surface),object :CameraCaptureSession.StateCallback(){
            override fun onConfigureFailed(session: CameraCaptureSession) {
                activity.toast("开启预览会话失败")
            }

            override fun onConfigured(session: CameraCaptureSession) {
                mCameraCaptureSession = session
                setZoom()
            }
        },mCameraHandler)
    }

    fun exchangeCamera(){
        if (mCameraFacing == null|| !isCanExchangeFac||!textureView.isAvailable) return
        setZoom()
        mCameraFacing = if(mCameraFacing == CameraCharacteristics.LENS_FACING_FRONT) CameraCharacteristics.LENS_FACING_BACK
        else CameraCharacteristics.LENS_FACING_FRONT
        mPreviewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        releaseCamera()
        initCamera()
    }

    private fun resetCamera(){
        if (mCameraFacing == null|| !isCanExchangeFac||!textureView.isAvailable) return
        mCameraFacing = CameraCharacteristics.LENS_FACING_BACK
        mPreviewSize = Size(PREVIEW_WIDTH, PREVIEW_HEIGHT)
        openFace = true
        releaseCamera()
        initCamera()
    }

    private val mRepeatingCallback = object :CameraCaptureSession.CaptureCallback(){
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            if (openFace && mFaceMode != CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF)
                handleFace(result)
            isCanExchangeFac = true
            isCanTakePic = true
        }
    }

    private val mCaptureCallback = object :CameraCaptureSession.CaptureCallback(){
        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            val requestCaptureBuilder = getRestoreRepeating()
            if(requestCaptureBuilder!=null)
                mCameraCaptureSession?.setRepeatingRequest(requestCaptureBuilder.build(),mRepeatingCallback,mCameraHandler)
            super.onCaptureCompleted(session, request, result)
        }
        override fun onCaptureFailed(
            session: CameraCaptureSession,
            request: CaptureRequest,
            failure: CaptureFailure
        ) {
            val requestCaptureBuilder = getRestoreRepeating()
            if(requestCaptureBuilder!=null)
                mCameraCaptureSession?.setRepeatingRequest(requestCaptureBuilder.build(),mRepeatingCallback,mCameraHandler)
            super.onCaptureFailed(session, request, failure)
        }
    }

    private fun getRestoreRepeating():CaptureRequest.Builder?{
        if (mCameraDevice == null||!textureView.isAvailable) return null
        try {
            val requestCaptureBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
            return requestCaptureBuilder?.apply {
                addTarget(Surface(textureView.surfaceTexture))
                set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
                set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                if (openFace && mFaceMode != CaptureRequest.STATISTICS_FACE_DETECT_MODE_OFF)
                    set(CaptureRequest.STATISTICS_FACE_DETECT_MODE, CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE)
            }!!
        } catch (e: Exception) {
            return null
        }
    }

    private fun handleFace(result: TotalCaptureResult){
        val faces = result.get(CaptureResult.STATISTICS_FACES)?:return
        mFaceList.clear()

        for (face in faces){
            val bounds = face.bounds
            val left = bounds.left
            val top = bounds.top
            val right = bounds.right
            val bottom = bounds.bottom

            val rawFaceRect = RectF(left.toFloat(), top.toFloat(), right.toFloat(), bottom.toFloat())
            mFaceMatrix.mapRect(rawFaceRect)

            val resultFaceRect = if (mCameraFacing == CaptureRequest.LENS_FACING_FRONT){
                if (android.os.Build.MANUFACTURER == "samsung"||android.os.Build.MODEL == "SM-N9760"){
                    RectF(rawFaceRect.left+rawFaceRect.width()/6,rawFaceRect.top+rawFaceRect.height()/6,rawFaceRect.right+rawFaceRect.width()*2/3,rawFaceRect.bottom+rawFaceRect.height()*2/3)
                }else{
                    rawFaceRect
                }
            }
            else{
                if (android.os.Build.MANUFACTURER == "samsung"||android.os.Build.MODEL == "SM-N9760"){
                    val tempR = RectF(rawFaceRect.left, rawFaceRect.top - mPreviewSize.width, (rawFaceRect.right), (rawFaceRect.bottom - mPreviewSize.width))
                    RectF(tempR.left+tempR.width()/4,tempR.top+tempR.height()/4,tempR.right+tempR.width(),tempR.bottom+tempR.width())
                }else{
                    RectF(rawFaceRect.left, rawFaceRect.top - mPreviewSize.width, (rawFaceRect.right), (rawFaceRect.bottom - mPreviewSize.width))
                }
            }
            mFaceList.add(resultFaceRect)
        }
        GlobalScope.launch(Dispatchers.Main) {
            faceListener?.invoke(mFaceList)
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


    fun onDestroy(){
        releaseCamera()
        handlerThread.quitSafely()
    }

    private var fingerSpacing = 0f
    var zoomLevel = 1
    fun onTouch(event:MotionEvent,listener:((Float)->Unit)? = null){
        if (!textureView.isAvailable||mCameraDevice==null){
            return
        }
        val maxZoom = (mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)?:1f)*10
        val currentZoom = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        val currentFingerSpacing:Float
        if (event.pointerCount >1){
            currentFingerSpacing = getSpacing(event)
            if (fingerSpacing != 0f){
                if (currentFingerSpacing - fingerSpacing > 10)
                    zoomLevel ++
                else if (currentFingerSpacing - fingerSpacing< -10)
                    zoomLevel --
                if (zoomLevel <=0) zoomLevel = 0
                else if (zoomLevel >= maxZoom/2) zoomLevel = (maxZoom).toInt()/2
                listener?.invoke(zoomLevel / (maxZoom/2))
                val zoom = getNewZoom(currentZoom!!,maxZoom,zoomLevel)
                mCameraCaptureSession?.stopRepeating()
                mCameraCaptureSession?.abortCaptures()
                val requestCaptureBuilder = getRestoreRepeating()
                requestCaptureBuilder?.apply {
                    set(CaptureRequest.CONTROL_AE_MODE,CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
                    set(CaptureRequest.CONTROL_AF_MODE,CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
                    requestCaptureBuilder.set(CaptureRequest.SCALER_CROP_REGION,zoom)
                    mCameraCaptureSession?.setRepeatingRequest(requestCaptureBuilder.build(),mRepeatingCallback,mCameraHandler)
                }
            }
            fingerSpacing = currentFingerSpacing
        }
    }

    fun setZoom(current:Float = -1f){
        if (current==-1f){}
        else if (current !in 0f..1f||!textureView.isAvailable||mCameraDevice==null) return
        val maxZoom = (mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)?:1f)*10
        val currentZoom = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        val maxbugZoom = maxZoom / 2
        if (current != -1f) zoomLevel = (maxbugZoom*current).toInt()
        val zoom = getNewZoom(currentZoom!!,maxZoom,zoomLevel)
        mCameraCaptureSession?.stopRepeating()
        mCameraCaptureSession?.abortCaptures()
        val requestCaptureBuilder = getRestoreRepeating()
        if (requestCaptureBuilder !=null){
            requestCaptureBuilder.set(CaptureRequest.SCALER_CROP_REGION,zoom)
            mCameraCaptureSession?.setRepeatingRequest(requestCaptureBuilder.build(),mRepeatingCallback,mCameraHandler)
        }
    }

    fun changeZoom(target:Int,listener:((Float)->Unit)? = null){
        if (!textureView.isAvailable||mCameraDevice==null) return
        val maxZoom = (mCameraCharacteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM)?:1f)*10
        val currentZoom = mCameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        val maxbugZoom = maxZoom / 2
        val changeNum = maxbugZoom.toInt()/20
        if(target ==1){
            zoomLevel += changeNum
            if (zoomLevel >= maxbugZoom) zoomLevel = maxbugZoom.toInt()
        }else{
            zoomLevel -= changeNum
            if (zoomLevel <= 1) zoomLevel = 1
        }
        listener?.invoke(zoomLevel / (maxZoom/2))
        val zoom = getNewZoom(currentZoom!!,maxZoom,zoomLevel)
        mCameraCaptureSession?.stopRepeating()
        mCameraCaptureSession?.abortCaptures()
        val requestCaptureBuilder = getRestoreRepeating()
        if (requestCaptureBuilder != null){
            requestCaptureBuilder.set(CaptureRequest.SCALER_CROP_REGION,zoom)
            mCameraCaptureSession?.setRepeatingRequest(requestCaptureBuilder.build(),mRepeatingCallback,mCameraHandler)
        }
    }

    private fun getNewZoom(cZoom:Rect,max:Float,level:Int):Rect{
        val minW = (cZoom.width() / max).toInt()
        val minH = (cZoom.height() / max).toInt()
        val disW = cZoom.width() - minW
        val disH = cZoom.height() - minH
        var cropW = disW / 100 * level
        var cropH = disH / 100 * level
        cropW -= cropW and 3
        cropH -= cropH and 3
        return Rect(cropW,cropH,cZoom.width() - cropW,cZoom.height() - cropH)
    }

    private fun getSpacing(ev:MotionEvent):Float{
        val x = ev.getX(0) - ev.getX(1)
        val y = ev.getY(0) - ev.getY(1)
        return sqrt(x*x+y*y)
    }

    fun addFaceListener(listener: ((ArrayList<RectF>) -> Unit)?){
        faceListener = listener
    }
}