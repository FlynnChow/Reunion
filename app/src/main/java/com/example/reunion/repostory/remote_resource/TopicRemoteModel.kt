package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import okhttp3.RequestBody
import retrofit2.http.Query

class TopicRemoteModel:BaseRemoteResource() {

    suspend fun sendTopic(body:RequestBody) =
        getServiceRemote().sendTopic(body).await()

    suspend fun getComment(id:String,page:Int)
            = getServiceRemote().getTopicComment(id,page).await()

    suspend fun onInsertComment(id:String,uid:String,content:String)
            = getServiceRemote().onInsertTopicComment(id,uid,content).await()

    suspend fun getReplyComment(commentId:String,page: Int)
            = getServiceRemote().getReplyTopicComment(commentId,page).await()

    suspend fun onInsertReply(commentId: String,replyFloor:Int,fromId:String,toId:String,content: String)
            = getServiceRemote().onInsertTopicReply(commentId,replyFloor,fromId,toId,content).await()

    suspend fun topicStar(id:String,uid: String,star:Boolean) = getServiceRemote().topicStar(id,uid,if (star) 0 else 1).await()

    suspend fun topicStarWhether(uid:String,sId:String) = getServiceRemote().topicStarWhether(uid,sId).await()

    suspend fun topicDelete(uId:String,sId:String) = getServiceRemote().topicDelete(uId,sId).await()

    suspend fun topicSearch(keyword:String,page:Int) = getServiceRemote().topicSearch(keyword,page).await()

    suspend fun topicStarSearch(uid:String,page:Int) = getServiceRemote().topicStarSearch(uid,page).await()
}