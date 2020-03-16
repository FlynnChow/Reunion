package com.example.reunion.repostory.local_resource

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.reunion.repostory.bean.TopicKeyword

@Dao
interface TopicKeywordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKeyword(keyword:TopicKeyword)

    @Query("DELETE FROM topic_keyword")
    suspend fun clearKeyword()

    @Query("SELECT keyword FROM topic_keyword ORDER BY time DESC")
    suspend fun obtainKeywords():List<String>
}