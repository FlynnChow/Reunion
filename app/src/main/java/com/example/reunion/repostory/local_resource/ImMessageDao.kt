package com.example.reunion.repostory.local_resource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.TopicKeyword

@Dao
interface ImMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message:ImMessageBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageArray(array:List<ImMessageBean>)

    @Query("DELETE FROM im_message WHERE targetUid = :uid")
    suspend fun clearMessages(uid:String)

    @Query("SELECT * FROM im_message WHERE imId = :imId AND targetUid = :targetUId ORDER BY time DESC LIMIT :start,:end")
    suspend fun obtainMessages(imId: String,targetUId:String,start:Int,end:Int):List<ImMessageBean>

    @Query("SELECT * FROM im_message WHERE uId = :uid AND targetUid = :uid ORDER BY time DESC LIMIT 1")
    suspend fun obtainMyFirstMessage(uid:String):List<ImMessageBean>

    @Query("SELECT content FROM im_message WHERE imId = :imId AND targetUid = :uid ORDER BY time DESC LIMIT 1")
    suspend fun obtainMyFirstContent(imId:String,uid:String):String

    @Query("UPDATE im_message SET sendSuccess = :result WHERE imId = :id AND time = :time AND targetUid = :uid")
    suspend fun setMessageSendFail(id:String,uid:String,time:Long,result:Boolean = false)

    @Query("UPDATE im_message SET sendSuccess = :result WHERE imId = :id AND time = :time AND targetUid = :uid")
    suspend fun setMessageSendSuccess(id:String,uid:String,time:Long,result:Boolean = true)

    @Query("UPDATE im_message SET header = :header,nickName = :name WHERE uId = :id")
    suspend fun updateUserMessage(id:String,header:String,name:String)

    @Query("SELECT COUNT(*) FROM im_message WHERE imId = :imId AND uId = :toUid AND targetUid = :uid AND isRead = :read")
    suspend fun obtainNotReadNum(imId:String,uid:String,toUid:String,read:Boolean = false):Int

    @Query("UPDATE  im_message SET isRead = :read WHERE imId = :imId AND uId = :toUid AND targetUid = :uid AND isRead = :findRead")
    suspend fun setRead(imId:String,uid:String,toUid:String,read:Boolean = true,findRead:Boolean = false):Int

}