package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.CommunityBean
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

class CommunityRemote:BaseRemoteResource() {

    suspend fun sendCommunityMain(body: RequestBody) = getServiceRemote().sendCommunityMain(body).await()

    suspend fun sendCommunityComment(id:String, comment:String, uId:String,toUId:String)
            = getServiceRemote().sendCommunityComment(id,comment,uId,toUId).await()

    suspend fun obtainCommunityFollow( uid:String,page: Int)
            = getServiceRemote().obtainCommunityFollow(uid,page).await()

    suspend fun obtainCommunityRecommend( page: Int)
            = getServiceRemote().obtainCommunityRecommend(page).await()

    suspend fun obtainCommunityUser( uid:String,  page: Int)
            = getServiceRemote().obtainCommunityUser(uid,page).await()

    suspend fun obtainCommunityComment(@Query("communityId") id:String, @Query("page") page: Int)
            = getServiceRemote().obtainCommunityComment(id,page).await()

}