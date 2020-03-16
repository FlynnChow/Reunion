package com.example.reunion.repostory.bean

import android.annotation.SuppressLint
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.reunion.R
import com.example.reunion.util.StringDealerUtil
import java.text.SimpleDateFormat
import java.util.*


@Entity(tableName = "system_message")
data class SystemMessageBean(
    @PrimaryKey var tableId:String = "null"
){

    //消息类型
    // 0：扫脸
    // 1：用户 被 用户A 关注，之前用户没有关注 用户A
    // 2：用户 被 用户A 关注，之前用户也关注了 用户A
    var type:Int = 0

    //time :System.currentTimeMillis
    var time:Long? = null

    //消息 目标用户的uid
    var targetUid:String? = null

    //消息 这个消息被推送用户的uid
    var uid:String? = null

    //消息标题
    // type = 0：系统通知
    // type = 1：好友关注
    // type = 2：好友关注
    var title:String? = null

    //消息内容
    var content:String? = null

    //是否已经被读，服务器默认发送false
    var isRead:Boolean = false

    //目标用户的头像URL
    var header:String? = null

    //目标用户的昵称
    var nickName:String? = null

    @SuppressLint("SimpleDateFormat")
    @Ignore
    fun getFormatTime():String{
        val format: SimpleDateFormat
        var time = this.time?:0
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

        return format.format(date)
    }

    @Ignore
    fun getTypeImage():Int{
        when(type){
            0 ->{
                return R.drawable.xiton
            }
            1,2 ->{
                return R.drawable.follow_user
            }
        }
        return 0
    }

    @Ignore
    fun setFormatContent(){
        var str = ""
        var nickName = this.nickName?:""
        if (StringDealerUtil.length(nickName) >= 5){
            nickName = nickName.substring(0,5) + ".."
            val chinese = "[\u0391-\uFFE5]"
            for (i in nickName.indices) {
                val temp: String = nickName.substring(i, i + 1)
                if (StringDealerUtil.isChinese(temp)) {
                    nickName = nickName.substring(0,3) + ".."
                    break
                }
            }
        }
        when(type){
            0 ->{
                str = "<font color='#FF6868'>${nickName}</font>扫到了你，点击查看他的资料"
            }
            1 ->{
                str = "<font color='#FF6868'>${nickName}</font>关注了你，关注他后便加入联系人"
            }
            2 ->{
                str = "<font color='#FF6868'>${nickName}</font>关注了你，你们成为了联系人"
            }
        }
        content = str
    }

}