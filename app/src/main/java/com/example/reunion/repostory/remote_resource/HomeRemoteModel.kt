package com.example.reunion.repostory.remote_resource

import android.util.Log
import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.bean.User
import com.google.gson.Gson
import okhttp3.RequestBody

class HomeRemoteModel:BaseRemoteResource() {

    suspend fun obtainSingleUserMessage(uid:String):User{
        val list = arrayListOf(uid)
        val data = obtainUserMessage(Gson().toJson(list))
        val changeData = User()
        changeData.code = data.code
        changeData.msg = data.msg
        changeData.data = data.getFirstUser()
        return changeData
    }

    suspend fun obtainUserMessage(userJson:String) = getServiceRemote().obtainUserMessage(userJson).await()

    suspend fun checkLogin(uid:String,time:Long,enCode:String?)
        = getServiceRemote().checkLogin(uid,time,enCode).await()

    suspend fun obtainFollowTopic(uid:String,page:Int = 1)
            = getServiceRemote().obtainFollowTopic(uid,page).await()

    suspend fun obtainNearbyTopic(locate:String,page:Int = 1)
            = getServiceRemote().obtainNearbyTopic(locate,page).await()

    suspend fun obtainRecommendTopic(page:Int = 1)
            = getServiceRemote().obtainRecommendTopic(page).await()

    suspend fun obtainUserTopic(type:Int,uid:String,page:Int = 1)
            = getServiceRemote().obtainUserTopic(type,uid,page).await()

    suspend fun obtainPeopleTopic(page:Int = 1,time:String?,age:String?,province:String?,city:String?,district:String?)
            = getServiceRemote().obtainPeopleTopic(page,time,age,province,city,district).await()

    suspend fun obtainBodyTopic(page:Int = 1,time:String?,province:String?,city:String?,district:String?)
            = getServiceRemote().obtainBodyTopic(page,time,province,city,district).await()

    suspend fun onFollowUser(uid:String,targetUid:String,follow:Boolean)
            = getServiceRemote().onFollowUser(uid,targetUid,if (follow) 0 else 1).await()

    suspend fun isFollowUser(uid:String,targetUid:String)
            = getServiceRemote().isFollowUser(uid,targetUid).await()
}