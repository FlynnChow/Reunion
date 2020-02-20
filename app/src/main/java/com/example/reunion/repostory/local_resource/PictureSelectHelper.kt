package com.example.reunion.repostory.local_resource

import android.app.Activity
import androidx.fragment.app.Fragment
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.util.PictureEngine
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.fragment_login_start.view.*
import java.io.File

class PictureSelectHelper {

    companion object{
        //最小压缩大小
        @JvmStatic
        val MIN_SIZE = 150

        @JvmStatic
        val CHECK_ALL_CODE = 10000

        @JvmStatic
        val CHECK_VIDEO_CODE = 10001

        @JvmStatic
        val CHECK_PHOTO_CODE = 10002

        @JvmStatic
        val CHECK_HEADER_CODE = 10003

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PictureSelectHelper() }

    }

    fun createSelector(activity:Activity)
            = initPhotoSelector(PictureSelector.create(activity))

    fun createSelector(fragment:Fragment)
            = initPhotoSelector(PictureSelector.create(fragment))

    fun createHeaderSelector(activity:Activity,num:Int = 1,isCamera:Boolean = false)
            = initHeaderSelector(PictureSelector.create(activity))

    fun createHeaderSelector(fragment:Fragment,num:Int = 1,isCamera:Boolean = false)
            = initHeaderSelector(PictureSelector.create(fragment))

    fun createPhotoSelector(activity:Activity,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(activity),PictureMimeType.ofImage(), CHECK_PHOTO_CODE,num,isCamera)

    fun createPhotoSelector(fragment:Fragment,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(fragment),PictureMimeType.ofImage(),CHECK_PHOTO_CODE,num,isCamera)

    fun createVideoSelector(activity:Activity,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(activity),PictureMimeType.ofVideo(), CHECK_VIDEO_CODE,num,isCamera)

    fun createVideoSelector(fragment:Fragment,num:Int = 1,isCamera:Boolean = false)
            = initPhotoSelector(PictureSelector.create(fragment),PictureMimeType.ofVideo(),CHECK_VIDEO_CODE,num,isCamera)

    private fun initPhotoSelector(selector: PictureSelector,type:Int = PictureMimeType.ofAll(),requestCode:Int = CHECK_ALL_CODE,num:Int = 1,isCamera:Boolean = false) = selector.openGallery(type).run {
        theme(R.style.PictureSelectorStyle)
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
        loadImageEngine(PictureEngine.createGlideEngine())
        isWithVideoImage(false)
        isOriginalImageControl(false)
        selectionMode(PictureConfig.SINGLE)
        isCamera(true)
        enableCrop(true)
        compress(true)
        circleDimmedLayer(false)
        isGif(false)
        compressSavePath(MyApplication.app.getExternalFilesDir("cache")?.absolutePath+ File.separator)
        freeStyleCropEnabled(true)
        minimumCompressSize(MIN_SIZE)
        rotateEnabled(true)
        scaleEnabled(true)
        withAspectRatio(1,1)
        forResult(CHECK_HEADER_CODE)
    }
}