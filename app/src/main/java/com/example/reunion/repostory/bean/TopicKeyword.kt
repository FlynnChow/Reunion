package com.example.reunion.repostory.bean

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "topic_keyword")
data class TopicKeyword(
    @PrimaryKey var keyword:String,
    var time:Long
)