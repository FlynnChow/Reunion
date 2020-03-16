package com.example.reunion.repostory.bean

import android.annotation.SuppressLint
import android.view.View
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import retrofit2.http.Url
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "im_index")
data class ImMessageIndex(
    @PrimaryKey var tableId:String,
    var uid:String,
    var toUid:String,
    var time:Long,
    var header:String,
    var nickName:String
){

    //时间
    @Ignore
    var formatTime:String = ""

    //聊天信息
    @Ignore
    var content:String = ""

    //未读消息数
    @Ignore
    var num:Int = 0


    @SuppressLint("SimpleDateFormat")
    @Ignore
    fun initData(content:String,nickName:String,num:Int,header:String){
        val format:SimpleDateFormat
        val nowTime = System.currentTimeMillis()
        if (nowTime - time <= 1000 * 60 * 60 * 24){
            format = SimpleDateFormat("今天HH:mm")
        }else if (nowTime - time <= 1000 * 60 * 60 * 24 * 2){
            format = SimpleDateFormat("昨天HH:mm")
        }else if (nowTime - time <= 1000 * 60 * 60 * 24 * 3){
            format = SimpleDateFormat("前天HH:mm")
        }else{
            format = SimpleDateFormat("yyyy年MM月dd日HH:mm")
        }

        val date = Date(time)
        this.formatTime = format.format(date)
        this.num = num
        this.header = header
        this.nickName = nickName
        this.content = content
        if (content.length >= 20){
            this.content = content.substring(0,19)+".."
        }
    }

    @SuppressLint("SimpleDateFormat")
    @Ignore
    fun initData(content:String,num:Int){
        val format:SimpleDateFormat
        val nowTime = System.currentTimeMillis()
        if (nowTime - time <= 1000 * 60 * 60 * 24){
            format = SimpleDateFormat("今天HH:mm")
        }else if (nowTime - time <= 1000 * 60 * 60 * 24 * 2){
            format = SimpleDateFormat("昨天HH:mm")
        }else if (nowTime - time <= 1000 * 60 * 60 * 24 * 3){
            format = SimpleDateFormat("前天HH:mm")
        }else{
            format = SimpleDateFormat("yyyy年MM月dd日HH:mm")
        }

        val date = Date(time)
        this.formatTime = format.format(date)
        this.num = num
        this.content = content
        if (content.length >= 20){
            this.content = content.substring(0,19)+".."
        }
    }

    @Ignore
    fun getFormatNum(num:Int):String{
        if (num <= 99)
            return num.toString()
        return "99"
    }

    @Ignore
    fun getNumVisible(num:Int):Int{
        if (num > 0)
            return View.VISIBLE
        return View.GONE
    }
}