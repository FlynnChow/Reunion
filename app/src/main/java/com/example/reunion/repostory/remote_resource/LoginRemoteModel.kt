package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.bean.VCode

class LoginRemoteModel:BaseRemoteResource() {
    suspend fun onPhoneLogin(phone:String,vCode:String):User{
        return getServiceRemote().onLogin(phone,vCode).await()
    }

    suspend fun onSendMessage(phone:String):VCode{
        return getServiceRemote().onSendMessage(phone).await()
    }
}