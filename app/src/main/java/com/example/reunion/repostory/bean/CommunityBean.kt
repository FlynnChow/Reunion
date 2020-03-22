package com.example.reunion.repostory.bean

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class CommunityBean() :Parcelable {

    var communityId:String? = null // 主题的 id 主键
    var header:String? = null // 头像URL
    var nickName:String? = null // 昵称
    var uId:String? = null // UID
    var time:Long = 0L // 时间 ->System.currentTimeMillis()

    var content:String? = null // 内容文字
    var locate:String? = null // 位置信息 [省市]
    var images:ArrayList<String>? = null // 内容图片URL
    var commentNum = 0 //评论总数

    /**
     * 首次获取主题时候默认会包含主题的 1 - 3 楼的评论
     * 没有评论，则为null
     */
    var comments:ArrayList<Comment>? = null

    constructor(parcel: Parcel) : this() {
        communityId = parcel.readString()
        header = parcel.readString()
        nickName = parcel.readString()
        uId = parcel.readString()
        time = parcel.readLong()
        content = parcel.readString()
        locate = parcel.readString()
        commentNum = parcel.readInt()
        images = ArrayList()
        parcel.readStringList(images)
    }

    fun getImageFromIndex(index:Int):String{
        return if (images != null && index < images?.size?:0)
            images!![index]
        else
            ""
    }

    fun getImageVisible(index:Int):Int{
        return if (images != null && index < images?.size?:0)
            View.VISIBLE
        else
            View.GONE
    }

    fun getCommentContent(index:Int):String{
        if (comments == null || comments!!.size <= index){
            return ""
        }else{
            var toName = comments!![index].toName
            if (toName == null)
                toName = ""
            else
                toName = "回复@${toName}"
            return "${comments!![index].nickName}${toName}: ${comments!![index].comment}"
        }
    }

    fun getCommentVisible(index:Int):Int{
        return if (comments == null || comments!!.size <= index){
            View.GONE
        }else
            View.VISIBLE
    }

    fun getFormatCommentNum():String{
        if (commentNum < 10000)
            return "${commentNum}条评论"
        else{
            var num = commentNum/1000
            return "${num/10f}万条评论"
        }
    }

    fun getFirstImage():String?{
        if (images !=null && images!!.size >0){
            return images!![0]
        }else
            return null
    }

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


    class ResponseBean{
        var code:Int = 0
        var msg:String = ""
        var data:CommunityBean? = null
    }

    class ResponseBeans{
        var code:Int = 0
        var msg:String = ""
        var data:ArrayList<CommunityBean>? = null
    }

    class CommentBeans{
        var code:Int = 0
        var msg:String = ""
        var data:ArrayList<Comment>? = null
    }

    class CommentBean{
        var code:Int = 0
        var msg:String = ""
        var data:Comment? = null
    }

    class Comment{
        var communityId:String? = null // 主题的id
        var header:String? = null // 头像URL
        var nickName:String? = null // 昵称
        var uId:String? = null // UID
        var time:Long = 0L // 时间 ->System.currentTimeMillis()

        var cId:String? = null //  评论的id 主键
        var comment:String? = null // 评论内容

        var toUId:String? = null // 被评论用户的UID,如果为null表示评论的是主题
        var toName:String? = null // 被评论用户的昵称,如果为null表示评论的是主题
        var floor:Int = 1 //评论的楼层 从 1 开始计数

        fun formatComment():String{
            val name = if (toName == null)
                ""
            else
                "@${toName}: "
            return "${name}${comment}"
        }

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
                format = SimpleDateFormat("yyyy-MM-dd HH:mm")
            }
            val date = Date(time)

            return format.format(date)
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(communityId)
        parcel.writeString(header)
        parcel.writeString(nickName)
        parcel.writeString(uId)
        parcel.writeLong(time)
        parcel.writeString(content)
        parcel.writeString(locate)
        parcel.writeInt(commentNum)
        parcel.writeStringList(images)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CommunityBean> {
        override fun createFromParcel(parcel: Parcel): CommunityBean {
            return CommunityBean(parcel)
        }

        override fun newArray(size: Int): Array<CommunityBean?> {
            return arrayOfNulls(size)
        }
    }
}