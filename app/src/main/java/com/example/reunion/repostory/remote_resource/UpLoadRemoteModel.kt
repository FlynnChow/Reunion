package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import okhttp3.MultipartBody
import retrofit2.http.Multipart

class UpLoadRemoteModel:BaseRemoteResource() {

    suspend fun uploadHeader(uid:String,time:String,enCode:String,part:MultipartBody.Part)
    = getServiceRemote().uploadHeader(uid,time,enCode,part).await()
}