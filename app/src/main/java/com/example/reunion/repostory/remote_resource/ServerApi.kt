package com.example.reunion.repostory.remote_resource

import com.example.reunion.repostory.bean.NormalBean
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.bean.VCode
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface ServerApi {
    @GET("tele/yzm")
    fun onSendMessage(@Query("tele") phone:String): Call<VCode>

    @GET("tele/teleLoginOrRegister")
    fun onLogin(@Query("tele") phone:String,@Query("code") vCode:String): Call<User>

    @GET("verificationLogin")
    fun checkLogin(@Query("uid") uid:String,@Query("time") vCode:Long,@Query("enCode") md:String): Call<User>

    @Multipart
    @POST("updateHeadPhoto")
    fun uploadHeader(@Field("uid") uid:String,@Field("time")time:String,@Field("enCode")enCode:String,@Part("header") header:MultipartBody.Part):Call<NormalBean>
}