package com.example.reunion.repostory.remote_resource

import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.bean.VCode
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ServerApi {
    @GET("tele/yzm")
    fun onSendMessage(@Query("tele") phone:String): Call<VCode>

    @GET("tele/teleLoginOrRegister")
    fun onLogin(@Query("tele") phone:String,@Query("code") vCode:String): Call<User>

    @GET("tele/teleLoginOrRegister")
    fun checkLogin(@Query("uid") uid:String,@Query("time") vCode:Long,@Query("md") md:String): Call<User>
}