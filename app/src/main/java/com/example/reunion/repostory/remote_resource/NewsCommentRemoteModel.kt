package com.example.reunion.repostory.remote_resource

import android.util.Log
import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.CommentResult
import com.example.reunion.repostory.local_resource.UserHelper


class NewsCommentRemoteModel: BaseRemoteResource() {

    suspend fun getNewsComment(id:String,page:Int,limit:Int = 5)
            = getServiceRemote().getNewsComment(id,page,limit).await()

    suspend fun onInsertComment(id:String,uid:String,content:String)
            = getServiceRemote().onInsertComment(id,uid,content).await()

    suspend fun getReplyComment(commentId:String,page: Int,limit: Int = 5)
            = getServiceRemote().getReplyComment(commentId,page,limit).await()

    suspend fun onInsertReply(commentId: String,replyFloor:Int,fromId:String,toId:String,content: String):CommentResult = getServiceRemote().onInsertReply(commentId,replyFloor,fromId,toId,content).await()
}