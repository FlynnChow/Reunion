package com.example.reunion.repostory.remote_resource

import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.local_resource.UserHelper
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface FaceApi {

    @POST("face/addFace")
    fun createNewFace(@Body body: RequestBody):Call<FaceBean.normalBean>

    @POST("face/searchFace")
    fun searchFace(@Body body: RequestBody):Call<FaceBean.ListBean>

    @POST("face/deleteFace")
    fun deleteFace(@Body body: RequestBody):Call<FaceBean.normalBean>


    @GET("face/selectUserId")
    fun getFaceList(@Query("userId") uid:String? = UserHelper.getUser()?.uId):Call<FaceBean.ListBean>

}