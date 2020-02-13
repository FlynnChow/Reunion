package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart

class UpLoadRemoteModel:BaseRemoteResource() {

    suspend fun uploadHeader(body:RequestBody)
    = getServiceRemote().uploadHeader(body).await()
}