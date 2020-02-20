package com.example.reunion.util

import android.app.Service
import android.util.Log
import com.example.reunion.MyApplication
import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.remote_resource.ServerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import okhttp3.Request
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.lang.Exception

object DownLoadUtil:BaseRemoteResource() {
    val cachePath by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MyApplication.app.getExternalFilesDir("cache")?.absolutePath + File.separator
    }

    val downloadPath by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
        MyApplication.app.getExternalFilesDir("download")?.absolutePath + File.separator
    }

    /**
     * 单线程非断点下载
     */
    fun download(url:String,path:String,listener:((Int)->Unit)? = null){
            val request:Request
            try {
                request = Request.Builder()
                    .url(url)
                    .build()
            }catch (e:Exception){
                return
            }
            val call = client.newCall(request)
            call.enqueue(object :okhttp3.Callback{
                override fun onFailure(call: okhttp3.Call, e: IOException) {
                    listener?.invoke(-1)
                }

                override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                    onDownloadToCache(path,url,listener,response)
                }
            })
    }

    @JvmStatic
    @Synchronized
    fun onDownloadToCache(path: String,url: String,listener:((Int)->Unit)? = null,response: okhttp3.Response){
        val file = File(path)
        if (!file.exists())
            file.mkdirs()
        val inStream = response.body?.byteStream()
        val outStream = BufferedOutputStream(FileOutputStream(
            path+StringDealerUtil.getStringToMD5(url)+".jpg"))
        try {
            val data = ByteArray(1024)
            var length: Int
            val total = response.body?.contentLength()
            var current = 0L
            while (true){
                length = inStream!!.read(data)
                if (length == -1) break
                current += length
                outStream.write(data,0,length)
                listener?.invoke((length/total!!.toFloat()).toInt()*100)
            }
            runBlocking(Dispatchers.Main) {
                listener?.invoke(100)
            }
        }catch (e:Exception){
            listener?.invoke(-1)
        }finally {
            inStream?.close()
            outStream.close()
        }
    }
}