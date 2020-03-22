package com.example.reunion.repostory.local_resource

import android.R.attr
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import top.zibin.luban.Luban
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream


class PictureHelper {

    companion object{

        @JvmStatic
        val REQUEST_DEFAULT = 25550

        @JvmStatic
        val REQUEST_CROUP = CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PictureHelper() }

        fun getCachePath():String{
            val path = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath + File.separator + "cache/"
            if (!File(path).exists())
                File(path).mkdirs()
            return path
        }
    }





    fun openPhoto(activity:Activity,maxSelectable:Int = 1,type:Set<MimeType> = MimeType.ofImage(),requestCode: Int = REQUEST_DEFAULT){
        Matisse.from(activity)
            .choose(type)
            .countable(true)
            .maxSelectable(maxSelectable)
            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
            .thumbnailScale(0.85f)
            .imageEngine(GlideEngine())
            .showPreview(false) // Default is `true`
            .theme(R.style.Matisse_Dracula)
            .forResult(requestCode)
    }

    fun obtainUrisFromPhoto(data:Intent?) = Matisse.obtainResult(data)

    fun obtainUriFromPhoto(data:Intent?) = Matisse.obtainResult(data)[0]

    fun obtainPathFromUri(uri:Uri,name:String = "cache"): String?{
        val cachePath = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+File.separator+name+File.separator
        if (!File(cachePath).exists()){
            File(cachePath).mkdirs()
        }
        val cacheFile = File(cachePath + System.currentTimeMillis() + ".jpg")
        val inStream: InputStream = MyApplication.app.contentResolver.openInputStream(uri)?:return null
        val outStream: OutputStream = FileOutputStream(cacheFile)
        val bytes = ByteArray(1024)
        var len = -1
        return try {
            while ({len = inStream.read(bytes);len}()!=-1)
                outStream.write(bytes,0,len)
            cacheFile.absolutePath
        } catch (e: Exception) {
            null
        } finally {
            inStream.close()
            outStream.close()
        }
    }

    fun obtainPathsFromUris(uris:ArrayList<Uri>,name:String = "cache"): ArrayList<String>{
        val arrays = ArrayList<String>()
        for (uri in uris){
            val cachePath = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+File.separator+name+File.separator
            if (!File(cachePath).exists()){
                File(cachePath).mkdirs()
            }
            val cacheFile = File(cachePath + System.currentTimeMillis() + ".jpg")
            val inStream: InputStream = MyApplication.app.contentResolver.openInputStream(uri)?:return arrays
            val outStream: OutputStream = FileOutputStream(cacheFile)
            val bytes = ByteArray(1024)
            var len = -1
            try {
                while ({len = inStream.read(bytes);len}()!=-1)
                    outStream.write(bytes,0,len)
                arrays.add(cacheFile.absolutePath)
            } catch (e: Exception) {
                null
            } finally {
                inStream.close()
                outStream.close()
            }
        }
        return arrays
    }

    fun cropImage(activity: Activity,uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(activity)
    }

    fun cropCircleImage(activity: Activity,uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(activity)
    }

    fun cropImage(fragment:Fragment,uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(MyApplication.app,fragment)
    }

    fun cropCircleImage(fragment:Fragment,uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setAspectRatio(1,1)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(MyApplication.app,fragment)
    }

    fun obtainCropUri(data: Intent?):Uri{
        val result = CropImage.getActivityResult(data)
        return result.uri
    }

    fun compressImage(activity: Activity,source:String?,name:String? = "cache",size:Int = 150):String{
        val cachePath = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+File.separator+name+File.separator
        if (!File(cachePath).exists()){
            File(cachePath).mkdirs()
        }
        return Luban.with(activity).load(source).ignoreBy(size).setTargetDir(cachePath).get()[0].absolutePath
    }

    suspend fun compressImage(app: Application,source:String?,name:String? = "cache",size:Int = 150):String{
        val cachePath = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+File.separator+name+File.separator
        if (!File(cachePath).exists()){
            File(cachePath).mkdirs()
        }
        return Luban.with(app).load(source).ignoreBy(size).setTargetDir(cachePath).get()[0].absolutePath
    }

    suspend fun compressImages(activity: Activity,sources:ArrayList<File>,name:String = "cache",size:Int = 150):ArrayList<String>{
        val cachePath = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+File.separator+name+File.separator
        if (!File(cachePath).exists()){
            File(cachePath).mkdirs()
        }
        val list = Luban.with(activity).load(sources).ignoreBy(size).setTargetDir(cachePath).get()
        val paths = ArrayList<String>()
        for (file in list){
            paths.add(file.absolutePath)
        }
        return paths
    }

    suspend fun compressImageFromUri(activity: Activity,source:Uri?,name:String? = "cache",size:Int = 150):String{
        val cachePath = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+File.separator+name+File.separator
        if (!File(cachePath).exists()){
            File(cachePath).mkdirs()
        }
        return Luban.with(activity).load(source).ignoreBy(size).setTargetDir(cachePath).get()[0].absolutePath
    }

    suspend fun compressImagesFromUris(activity: Activity,sources:ArrayList<Uri>,name:String = "cache",size:Int = 150):ArrayList<String>{
        val cachePath = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+File.separator+name
        val list = Luban.with(activity).load(sources).ignoreBy(size).setTargetDir(cachePath).get()
        val paths = ArrayList<String>()
        for (file in list){
            paths.add(file.absolutePath)
        }
        return paths
    }

    suspend fun compressImagesFromPaths(activity: Activity,sources:ArrayList<String>,name:String = "cache",size:Int = 150):ArrayList<String>{
        val cachePath = MyApplication.app.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.absolutePath+File.separator+name+File.separator
        if (!File(cachePath).exists()){
            File(cachePath).mkdirs()
        }
        val list = Luban.with(activity).load(sources).ignoreBy(size).setTargetDir(cachePath).get()
        val paths = ArrayList<String>()
        for (file in list){
            paths.add(file.absolutePath)
        }
        return paths
    }

}