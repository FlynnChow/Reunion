package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.bean.VCode

class LoginResource:BaseRemoteResource() {
    suspend fun onPhoneLogin(phone:String,vCode:String):User?{
        return null
    }

    suspend fun onPhoneLogin(phone:String):VCode?{
        return null
    }
}