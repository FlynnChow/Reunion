package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.User

class HomeRemoteModel:BaseRemoteResource() {

    suspend fun checkLogin():User{
        return User()
    }
}