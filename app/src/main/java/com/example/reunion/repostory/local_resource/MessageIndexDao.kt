package com.example.reunion.repostory.local_resource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.TopicKeyword

@Dao
interface MessageIndexDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIndex(message:ImMessageIndex)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIndexArray(array:List<ImMessageIndex>)

    @Query("SELECT * FROM im_index WHERE uid = :uid ORDER BY time DESC")
    suspend fun obtainMessages(uid:String):List<ImMessageIndex>

    @Query("SELECT * FROM im_index WHERE tableId = :imId And uid = :uid")
    suspend fun obtainMessage(imId:String,uid:String):List<ImMessageIndex>

    @Query("UPDATE im_index SET header = :header,nickName = :name WHERE tableId = :imId AND uid = :uid")
    suspend fun updateMessage(imId:String,uid:String,header:String,name:String)

    @Query("DELETE FROM im_index WHERE uid = :uid")
    suspend fun clearIndex(uid:String)

    @Query("DELETE FROM im_index WHERE tableId = :imId AND uid = :uid")
    suspend fun deleteIndex(imId:String,uid:String)

}