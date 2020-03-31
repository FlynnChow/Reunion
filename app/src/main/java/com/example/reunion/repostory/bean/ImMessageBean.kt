package com.example.reunion.repostory.bean

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.reunion.util.NotificationUtil
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "im_message")
data class ImMessageBean(
    @PrimaryKey(autoGenerate = true) var tableId:Long? = null
){
    //这条记录是属于哪个用户的
    var targetUid:String? = null

    //聊天id : uid1.append(uid2) [uid1 < uid2]
    var imId:String? = null

    //time :System.currentTimeMillis
    //由客户端生成
    var time:Long? = null

    //发出消息用户的uid
    var uId:String? = null

    //接收消息用户的uid
    var toUid:String? = null

    //聊天信息
    var content:String? = null

    //默认true 由客户端控制
    //是否已被读
    var isRead:Boolean = false

    //发送消息时 发送者的头像URL
    var header:String? = null

    //发送消息时 发送者的昵称
    var nickName:String? = null

    //用于记录客户端是否发送成功，服务器不需要接收
    var sendSuccess:Boolean = true

    @Ignore
    var isMine = true
    @Ignore
    fun setMine(myUId:String){
        isMine =  myUId == uId
    }

    @Ignore
    var isShowTime = true

    @Ignore
    fun setTimeVisible(lastTime:Long){
        var time = 0L
        if (this.time != null)
            time = this.time!!
        isShowTime = lastTime == 0L|| (time - lastTime) >= 1000 * 60 * 20
    }

    @SuppressLint("SimpleDateFormat")
    @Ignore
    fun getFormatTime():String{
        val format: SimpleDateFormat
        val time = this.time?:0L
        val nowTime = System.currentTimeMillis()
        val lTime = NotificationUtil.getDayTime(time)
        if (nowTime - lTime <= 1000 * 60 * 60 * 24){
            format = SimpleDateFormat("今天HH:mm")
        }else if (nowTime - lTime <= 1000 * 60 * 60 * 24 * 2){
            format = SimpleDateFormat("昨天HH:mm")
        }else if (nowTime - lTime <= 1000 * 60 * 60 * 24 * 3){
            format = SimpleDateFormat("前天HH:mm")
        }else{
            format = SimpleDateFormat("yyyy年MM月dd日HH:mm")
        }
        val date = Date(time)

        return format.format(date)
    }

    @Ignore
    fun getCircleVisible():Int{
        return if (sendSuccess)
            View.INVISIBLE
        else
            View.VISIBLE
    }
}