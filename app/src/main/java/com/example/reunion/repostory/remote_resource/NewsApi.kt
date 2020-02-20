package com.example.reunion.repostory.remote_resource

import com.example.reunion.repostory.bean.NewsBean
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("get")
    fun getNews(@Query("channel") channel:String, @Query("start") start:Int, @Query("num") num:Int = 40, @Query("appkey") appkey:String = "87a6dd9d7006d94e"): Call<NewsBean>

    @GET("search")
    fun getPublicNews(@Query("keyword") keyword:String, @Query("appkey") appkey:String = "770609a551c4fa83"): Call<NewsBean>

}