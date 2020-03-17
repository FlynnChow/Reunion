package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource

class UserRemoteModel:BaseRemoteResource() {

    suspend fun userSearch(keyword:String,page:Int) = getServiceRemote().userSearch(keyword,page).await()

    suspend fun userFollow( uid:String) = getServiceRemote().userFollow(uid).await()

    suspend fun userFan( uid:String) = getServiceRemote().userFan(uid).await()

    suspend fun userFriends( uid:String) = getServiceRemote().userFriends(uid).await()
}