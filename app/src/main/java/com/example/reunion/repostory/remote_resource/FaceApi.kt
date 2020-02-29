package com.example.reunion.repostory.remote_resource

import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.local_resource.UserHelper
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface FaceApi {

    @POST("")
    fun createNewFace(@Body body: RequestBody):Call<FaceBean.normalBean>

    @GET("")
    fun deleteFace(@Body body: RequestBody):Call<FaceBean.normalBean>

    @POST("")
    fun researchFace(@Query("groupId") groupId:String,@Query("uId") uid:String? = UserHelper.getUser()?.uId):Call<FaceBean.ListBean>

    @GET("")
    fun getFaceList(@Query("uId") uid:String? = UserHelper.getUser()?.uId):Call<FaceBean.ListBean>

}