package com.example.reunion.repostory.local_resource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.SystemMessageBean
import com.example.reunion.repostory.bean.TopicKeyword

@Dao
interface SystemMessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSysMessage(message:SystemMessageBean)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSysMessageArray(array:List<SystemMessageBean>)

    @Query("SELECT * FROM system_message WHERE uid = :uid ORDER BY time DESC")
    suspend fun obtainMessages(uid:String):List<SystemMessageBean>

    @Query("DELETE FROM system_message WHERE uid = :uid")
    suspend fun clearIndex(uid:String)

    @Query("DELETE FROM system_message WHERE uid = :uid AND tableId = :tableId")
    suspend fun deleteIndex(tableId:String,uid:String)

    @Query("UPDATE system_message SET isRead = :read WHERE tableId = :tableId AND uid = :uid")
    suspend fun updateSysMessageClickable(tableId:String,uid:String,read:Boolean = true)

}