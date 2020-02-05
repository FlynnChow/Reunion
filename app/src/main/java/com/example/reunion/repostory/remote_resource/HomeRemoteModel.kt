package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.User

class HomeRemoteModel:BaseRemoteResource() {

    suspend fun checkLogin(uid:String,time:Long,enCode:String):User{
        return getServiceRemote().checkLogin(uid,time,enCode).await()
    }
}