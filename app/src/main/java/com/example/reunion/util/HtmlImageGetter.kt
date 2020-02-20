package com.example.reunion.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LevelListDrawable
import android.text.Html
import android.util.Log
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.example.reunion.MyApplication
import com.example.reunion.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.internal.wait
import java.io.File


class HtmlImageGetter(private val activity: Activity? = null, private val downloadListener:((Int)->Unit)?=null): Html.ImageGetter {
    override fun getDrawable(source: String?): Drawable {
        val point = Point()
        val path =
            DownLoadUtil.cachePath + StringDealerUtil.getStringToMD5(source)+".jpg"
        var bitmap: Bitmap?
        if (!File(path).exists()){
            bitmap = BitmapFactory.decodeResource(MyApplication.resource(),R.drawable.loading)
            if (source!=null){
                DownLoadUtil.download(source,DownLoadUtil.cachePath,downloadListener)
            }
        }
        else if (activity == null){
            bitmap = BitmapFactory.decodeFile(path)
        }else{
            val display = activity.windowManager.defaultDisplay
            display.getSize(point)
            val option = BitmapFactory.Options()
            option.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path,option)
            option.inSampleSize = calculateInSampleSize(option,point.x)
            option.inJustDecodeBounds = false
            bitmap = BitmapFactory.decodeFile(path,option)
            if (bitmap == null){
                bitmap = BitmapFactory.decodeResource(MyApplication.resource(),R.drawable.loading)
                if (source!=null){
                    runBlocking(Dispatchers.IO) {
                        DownLoadUtil.download(source,DownLoadUtil.cachePath,downloadListener)
                    }
                }
            }
        }
        val drawable = BitmapDrawable(MyApplication.resource(),bitmap)

        val width = point.x * 3/4
        val height = width/drawable.intrinsicWidth*drawable.intrinsicHeight
        drawable.setBounds(point.x/8, 0, width, height)
        return  drawable
    }

    private fun calculateInSampleSize(option:BitmapFactory.Options, reWidth:Int):Int{
        val width = option.outWidth
        var sampleSize = 1
        while (width/sampleSize >=reWidth*2){
            sampleSize *=2
        }
        return sampleSize
    }
}