package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource

class HomeRemoteModel:BaseRemoteResource() {

    suspend fun checkLogin(uid:String,time:Long,enCode:String?)
        = getServiceRemote().checkLogin(uid,time,enCode).await()
}