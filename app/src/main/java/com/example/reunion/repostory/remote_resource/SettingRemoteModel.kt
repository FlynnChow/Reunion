package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart

class SettingRemoteModel:BaseRemoteResource() {

    suspend fun uploadHeader(body:RequestBody)
    = getServiceRemote().uploadHeader(body).await()

    suspend fun upInformation(body:RequestBody)
            = getServiceRemote().updateUserInformation(body).await()

    suspend fun insertFeedBack(body:RequestBody) = getServiceRemote().insertFeedBack(body).await()
}