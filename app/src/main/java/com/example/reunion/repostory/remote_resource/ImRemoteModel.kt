package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart

class ImRemoteModel:BaseRemoteResource() {

    fun getUserMessage(toUid:String) = User.Data().apply {
        uId = toUid
    }
}