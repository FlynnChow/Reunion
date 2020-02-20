package com.example.reunion.repostory.remote_resource

import com.example.reunion.repostory.bean.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface ServerApi {
    @GET("tele/yzm")
    fun onSendMessage(@Query("tele") phone:String): Call<VCode>

    @GET("tele/teleLoginOrRegister")
    fun onLogin(@Query("tele") phone:String,@Query("code") vCode:String): Call<User>

    @GET("verificationLogin")
    fun checkLogin(@Query("uid") uid:String,@Query("time") vCode:Long,@Query("enCode") md:String?): Call<User>

    @POST("updateHeadPhoto")
    fun uploadHeader(@Body body:RequestBody ):Call<NormalBean>

    @POST("user/update")
    fun updateUserInformation(@Body body:RequestBody ):Call<User>

    @GET("news/comment")
    fun getNewsComment(@Query("newsId") id:String,@Query("page") page:Int,@Query("limit") limit:Int):Call<CommentBean>

    @GET("news/addComment")
    fun onInsertComment(@Query("newsId") id:String,@Query("uId") uid:String,@Query("comment") content:String):Call<CommentResult>

    @GET("news/reply")
    fun getReplyComment(@Query("commentId") id:String,@Query("") page:Int,@Query("limit") limit:Int):Call<CommentBean>

    @GET("news/addReply")
    fun onInsertReply(
        @Query("xwcId") newsCommentId:String,
        @Query("xwrFloor") newsCommentFloor:Int,
        @Query("fromUid") fromUid:String,
        @Query("toUid") toUid:String,
        @Query("comment") content: String):Call<CommentResult>
}