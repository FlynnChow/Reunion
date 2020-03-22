package com.example.reunion.util

import android.annotation.SuppressLint
import android.util.Log
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.reunion.MyApplication
import com.example.reunion.R
import java.io.File
import java.math.BigInteger
import java.net.URLEncoder
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.DecimalFormat
import java.util.*
import kotlin.collections.ArrayList


object NormalUtil {
    fun isMobile(phone:String?):Boolean{
        if (phone==null||phone.length != 11)
            return false
        for (index in 0..1){
            val num = phone[index]
            if (index == 0&&num == '1')
                continue
            else if (num=='3'||num=='5'||num=='7'||num=='8')
                continue
            return false
        }
        return true
    }

    //默认使用三级缓存
    fun getGlideOption(skipMemoryCache:Boolean = true,diskCacheStrategy:Boolean = true) =
         RequestOptions()
            .placeholder(R.drawable.loading)//占位图
            .error(R.drawable.load_error)//加载错误图
            .skipMemoryCache(!skipMemoryCache)
            .diskCacheStrategy(if (diskCacheStrategy) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE)

    //默认使用三级缓存
    fun getGlideOptionNoLoad(skipMemoryCache:Boolean = true,diskCacheStrategy:Boolean = true) =
        RequestOptions()
            .error(R.drawable.load_error)//加载错误图
            .skipMemoryCache(!skipMemoryCache)
            .diskCacheStrategy(if (diskCacheStrategy) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE)

    fun getGlideHeaderOption(skipMemoryCache:Boolean = true,diskCacheStrategy:Boolean = true) =
        RequestOptions()
            .error(R.drawable.user)//加载错误图
            .skipMemoryCache(!skipMemoryCache)
            .diskCacheStrategy(if (diskCacheStrategy) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE)

    @SuppressLint("DefaultLocale")
    fun formatUrlParam(sourceMap:HashMap<String,String>, encode:String, isLower:Boolean):String{


        var params = ""
        val map = sourceMap
        val items = ArrayList<Map.Entry<String,String>>(map.entries)
        Collections.sort(items,object :Comparator<Map.Entry<String,String>>{
            override fun compare(
                o1: Map.Entry<String, String>?,
                o2: Map.Entry<String, String>?
            ): Int {
                return (o1?.key.toString().compareTo(o2?.key.toString()))
            }
        })

        val sb = StringBuilder()
        for (item in items){
            val key = item.key
            var value = item.value
            value = URLEncoder.encode(value,encode)
            if (isLower) {
                sb.append(key.toLowerCase().toString() + "=" + value)
            } else {
                sb.append("$key=$value")
            }
            sb.append("&")
        }
        params = sb.toString()
        if (!params.isEmpty()) {
            params = params.substring(0, params.length - 1)
        }
        return params
    }

    fun getSign(sourceMap:HashMap<String,String>,appkey:String = MyApplication.resource().getString(R.string.tencent_ai_app_key)):String{
        var sign = formatUrlParam(sourceMap,"utf-8",true)
        sign = "${sign}&app_key=${appkey}"
        return StringDealerUtil.getStringToMD5(sign)
    }

    fun stringToMD5(plainText: String): String? {
        var secretBytes: ByteArray? = null
        secretBytes = try {
            MessageDigest.getInstance("md5").digest(
                plainText.toByteArray()
            )
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("没有这个md5算法！")
        }
        var md5code: String = BigInteger(1, secretBytes).toString(16)
        for (i in 0 until 32 - md5code.length) {
            md5code = "0$md5code"
        }
        return md5code
    }

    fun getFormatDirSize(mFile:File):String{
        val size = getDirSize(mFile)
        val df = DecimalFormat("#.00")
        var formatSize = ""
        if (size == 0L){
            return "0B"
        }
        else if (size < 1024L){
            formatSize = df.format(size) + "B"
        }
        else if(size < 1024L*1024L){
            formatSize = df.format(size/1024.0) + "KB"
        }
        else if(size < 1024L*1024L*1024L){
            formatSize = df.format(size/(1024.0*1024.0)) + "MB"
        }else{
            formatSize = df.format(size/(1024.0*1024.0*1024.0)) + "GB"
        }
        return formatSize
    }

    fun getDirSize(mFile:File):Long{
        var size = 0L
        val files = mFile.listFiles()?:return 0
        for (file in files){
            size += if (file.isDirectory){
                getDirSize(file)
            }else{
                file.length()
            }
        }
        return size
    }

    fun deleteDirOrFile(path:String){
        val file = File(path)
        if (!file.exists()){
            return
        }
        if (file.isFile){
            file.delete()
        }else{
            var tempFile:File? = null
            val files = file.list()?:return
            for (sonPath in files){
                if (path.endsWith(File.separator)){
                    tempFile = File(path + sonPath)
                }else{
                    tempFile = File(path + File.separator + sonPath)
                }
                if (tempFile.isFile)
                    tempFile.delete()
                else{
                    deleteDirOrFile(tempFile.absolutePath)
                    tempFile.delete()
                }
            }
        }
    }
}