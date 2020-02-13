package com.example.reunion.repostory.remote_resource

import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.NormalBean
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.bean.VCode
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ServerApi {
    @GET("tele/yzm")
    fun onSendMessage(@Query("tele") phone:String): Call<VCode>

    @GET("tele/teleLoginOrRegister")
    fun onLogin(@Query("tele") phone:String,@Query("code") vCode:String): Call<User>

    @GET("verificationLogin")
    fun checkLogin(@Query("uid") uid:String,@Query("time") vCode:Long,@Query("enCode") md:String): Call<User>

    @POST("updateHeadPhoto")
    fun uploadHeader(@Body body:RequestBody ):Call<NormalBean>

    @GET("get")
    fun getNews(@Query("channel") channel:String,@Query("start") start:Int,@Query("num") num:Int = 40,@Query("appkey") appkey:String = "770609a551c4fa83"):Call<NewsBean>

    @GET("search")
    fun getPublicNews(@Query("keyword") keyword:String,@Query("appkey") appkey:String = "770609a551c4fa83"):Call<NewsBean>
}