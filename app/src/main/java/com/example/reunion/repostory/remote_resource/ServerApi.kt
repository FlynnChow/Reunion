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
    fun getReplyComment(@Query("commentId") id:String,@Query("page") page:Int,@Query("limit") limit:Int):Call<CommentBean>

    @GET("news/addReply")
    fun onInsertReply(
        @Query("xwcId") newsCommentId:String,
        @Query("xwrFloor") newsCommentFloor:Int,
        @Query("fromUid") fromUid:String,
        @Query("toUid") toUid:String,
        @Query("comment") content: String):Call<CommentResult>

    @POST("search/add")
    fun sendTopic(@Body body:RequestBody):Call<TopicBean.Bean>

    @GET("search/findComment")
    fun getTopicComment(@Query("sId") id:String,@Query("page") page:Int):Call<CommentBean>

    @GET("search/addComment")
    fun onInsertTopicComment(@Query("sId") id:String,@Query("uId") uid:String,@Query("comment") content:String):Call<CommentResult>

    @GET("search/findReply")
    fun getReplyTopicComment(@Query("cId") id:String,@Query("page") page:Int):Call<CommentBean>

    @GET("search/addReply")
    fun onInsertTopicReply(
        @Query("cId") newsCommentId:String,
        @Query("toFloor") newsCommentFloor:Int,
        @Query("fromUid") fromUid:String,
        @Query("toUid") toUid:String,
        @Query("comment") content: String):Call<CommentResult>

    @POST("feedBack/addOne")
    fun insertFeedBack(@Body body:RequestBody):Call<FeedBack>

    @GET("home/follow")
    fun obtainFollowTopic(@Query("uId") uid:String,@Query("page") page:Int):Call<TopicBean.Beans>

    @GET("home/recommend")
    fun obtainRecommendTopic(@Query("page") page:Int):Call<TopicBean.Beans>

    @GET("home/nearby")
    fun obtainNearbyTopic(@Query("locate") locate:String,@Query("page") page:Int):Call<TopicBean.Beans>

    @GET("home/people")
    fun obtainPeopleTopic(@Query("page") page:Int,
                          @Query("time") time:String?,
                          @Query("age") age:String?,
                          @Query("province") province:String?,
                          @Query("city") city:String?,
                          @Query("district") district:String?):Call<TopicBean.Beans>

    @GET("home/body")
    fun obtainBodyTopic(@Query("page") page:Int,
                        @Query("time") time:String?,
                        @Query("province") province:String?,
                        @Query("city") city:String?,
                        @Query("district") district:String?):Call<TopicBean.Beans>

    @GET("search/user")
    fun obtainUserTopic(@Query("type") type:Int,@Query("uId") uid:String,@Query("page") page:Int):Call<TopicBean.Beans>

    @FormUrlEncoded
    @POST("user/publicInfo")
    fun obtainUserMessage(@Field("uIdListJson") body:String):Call<User.UserBeans>

    @GET("user/follow")
    fun onFollowUser(@Query("uId") uid: String,@Query("targetUId") targetUId: String,@Query("follow") follow: Int):Call<FollowBean>

    @GET("follow/whether")
    fun isFollowUser(@Query("uId") uid: String,@Query("targetUId") targetUId: String):Call<FollowBean>

    @POST("community/sendMain")
    fun sendCommunityMain(@Body body:RequestBody):Call<CommunityBean.ResponseBean>

    @GET("community/sendComment")
    fun sendCommunityComment(@Query("communityId") id:String,@Query("comment") comment:String,@Query("uId") uId:String,@Query("toUId") toUId:String?):Call<CommunityBean.CommentBean>

    @GET("community/obtainFollow")
    fun obtainCommunityFollow(@Query("uId") uid:String,@Query("page") page: Int):Call<CommunityBean.ResponseBeans>

    @GET("community/obtainRecommend")
    fun obtainCommunityRecommend(@Query("page") page: Int):Call<CommunityBean.ResponseBeans>

    @GET("community/obtainUser")
    fun obtainCommunityUser(@Query("uId") uid:String,@Query("page") page: Int):Call<CommunityBean.ResponseBeans>

    @GET("community/obtainComment")
    fun obtainCommunityComment(@Query("communityId") id:String,@Query("page") page: Int):Call<CommunityBean.CommentBeans>

    @GET("community/delete")
    fun deleteCommunity(@Query("communityId") id:String,@Query("uId") uid: String):Call<CommunityBean.ResponseBean>

    @GET("search/star")
    fun topicStar(@Query("sId") id:String,@Query("uId") uid: String,@Query("star") star:Int):Call<NormalBean>

    @GET("home/search")
    fun topicSearch(@Query("keyword") keyword:String,@Query("page") page:Int):Call<TopicBean.Beans>

    @GET("search/delete")
    fun topicDelete(@Query("uId") uId:String,@Query("sId") sId:String):Call<NormalBean>

    @GET("user/search")
    fun userSearch(@Query("keyword") keyword:String,@Query("page") page:Int):Call<User.UserBeans>

    @GET("follow/search")
    fun userFollow(@Query("uId") uid:String):Call<User.UserBeans>

    @GET("fan/search")
    fun userFan(@Query("uId") uid:String):Call<User.UserBeans>

    @GET("friend/search")
    fun userFriends(@Query("uId") uid:String):Call<User.UserBeans>

    @GET("star/search")
    fun topicStarSearch(@Query("uId") uid:String,@Query("page") page:Int):Call<TopicBean.Beans>

    @GET("star/search")
    fun topicStarWhether(@Query("uId") uid:String,@Query("sId") sId:String):Call<FollowBean>

}