package com.example.reunion.repostory.remote_resource

import android.content.ContentResolver
import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.bean.VCode

class LoginRemoteModel:BaseRemoteResource() {
    suspend fun onPhoneLogin(phone:String,vCode:String):User{
        return serverRetrofit.create(ServerApi::class.java).onLogin(phone,vCode).await()
    }

    suspend fun onSendMessage(phone:String):VCode{
        return serverRetrofit.create(ServerApi::class.java).onSendMessage(phone).await()
    }
}