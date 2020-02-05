package com.example.reunion.repostory.local_resource

import android.app.Activity
import androidx.fragment.app.Fragment
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.util.PictureEngine
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.view_login_start.view.*

class PictureSelectHelper {

    companion object{
        //最小压缩大小
        @JvmStatic
        val MIN_SIZE = 150

        @JvmStatic
        val CHECK_ALL_CODE = 100000

        @JvmStatic
        val CHECK_VIDEO_CODE = 100001

        @JvmStatic
        val CHECK_PHOTO_CODE = 100002

        @JvmStatic
        val CHECK_HEADER_CODE = 100003

    }

    fun createSelector(activity:Activity)
            = initHeaderSelector(PictureSelector.create(activity))

    fun createSelector(fragment:Fragment)
            = initHeaderSelector(PictureSelector.create(fragment))

    fun createHeaderSelector(activity:Activity,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(activity),num = num,isCamera = isCamera)

    fun createHeaderSelector(fragment:Fragment,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(fragment),num = num,isCamera = isCamera)

    fun createPhotoSelector(activity:Activity,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(activity),PictureMimeType.ofImage(), CHECK_VIDEO_CODE,num,isCamera)

    fun createPhotoSelector(fragment:Fragment,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(fragment),PictureMimeType.ofImage(),CHECK_VIDEO_CODE,num,isCamera)

    fun createVideoSelector(activity:Activity,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(activity),PictureMimeType.ofVideo(), CHECK_PHOTO_CODE,num,isCamera)

    fun createVideoSelector(fragment:Fragment,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(fragment),PictureMimeType.ofVideo(),CHECK_PHOTO_CODE,num,isCamera)

    private fun initPhotoSelector(selector: PictureSelector,type:Int = PictureMimeType.ofAll(),requestCode:Int = CHECK_ALL_CODE,num:Int = 1,isCamera:Boolean = false) = selector.openGallery(type).run {
        theme(R.style.PictureSelectorStyle)
        isWeChatStyle(true)
        loadImageEngine(PictureEngine.createGlideEngine())
        isWithVideoImage(false)
        isOriginalImageControl(true)
        maxSelectNum(num)
        minSelectNum(1)
        if (num == 1){
            selectionMode(PictureConfig.SINGLE)
        }
        isCamera(isCamera)
        enableCrop(true)
        compress(true)
        isGif(true)
        compressSavePath(MyApplication.app.getExternalFilesDir("cache")?.absolutePath)
        freeStyleCropEnabled(true)
        showCropFrame(true)
        minimumCompressSize(MIN_SIZE)
        rotateEnabled(true)
        scaleEnabled(true)
        forResult(requestCode)
    }

    private fun initHeaderSelector(selector: PictureSelector) = selector.openGallery(PictureMimeType.ofImage()).run {
        theme(R.style.PictureSelectorStyle)
        isWeChatStyle(true)
        loadImageEngine(PictureEngine.createGlideEngine())
        isWithVideoImage(false)
        isOriginalImageControl(false)
        selectionMode(PictureConfig.SINGLE)
        isCamera(true)
        enableCrop(true)
        compress(true)
        isGif(false)
        compressSavePath(MyApplication.app.getExternalFilesDir("cache")?.absolutePath)
        freeStyleCropEnabled(true)
        circleDimmedLayer(true)
        minimumCompressSize(MIN_SIZE)
        rotateEnabled(true)
        scaleEnabled(true)
        cropImageWideHigh(1,1)
        forResult(CHECK_HEADER_CODE)
    }
}