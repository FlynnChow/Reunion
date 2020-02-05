package com.example.reunion.repostory.bean

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.*
import java.io.File

class UploadRequestBody(private val requestBody: RequestBody,private val listener:((Float)->Unit)? = null):RequestBody() {
    companion object{
        @JvmStatic
        fun getRequestBody(file: File,name:String,listener:((Float)->Unit)? = null):MultipartBody.Part{
            val body = file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            val uploadBody =
                UploadRequestBody(
                    body,
                    listener
                )
            return MultipartBody.Part.createFormData(name,file.name,uploadBody)
        }
    }

    private var byteBufferedSink:BufferedSink? = null

    override fun contentType(): MediaType? {
        return requestBody.contentType()
    }

    override fun writeTo(sink: BufferedSink) {
        if (byteBufferedSink == null)
            byteBufferedSink = sink(sink).buffer()
        requestBody.writeTo(sink)
        byteBufferedSink?.flush()
    }

    override fun contentLength(): Long {
        return requestBody.contentLength()
    }

    private fun sink(sink:Sink) = object :ForwardingSink(sink){
        private var total = -1L
        private var count = 0L
        override fun write(source: Buffer, byteCount: Long) {
            super.write(source, byteCount)
            if (total == -1L)
                total = contentLength()
            count += byteCount
            val current = ((count/total.toFloat())*100)
            listener?.invoke(current)
        }
    }
}