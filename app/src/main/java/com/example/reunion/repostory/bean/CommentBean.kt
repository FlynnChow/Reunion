package com.example.reunion.repostory.bean

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import android.text.Html
import android.util.TypedValue
import android.widget.TextView
import com.example.reunion.MyApplication
import com.example.reunion.util.StringDealerUtil
import java.text.SimpleDateFormat
import java.util.*
import java.util.logging.SimpleFormatter
import kotlin.collections.ArrayList

class CommentBean {
    var code = 0
    var msg = ""
    var data:Data? = null

    class Comment{
        var uId = ""
        var xwId = ""
        var xwcId = "" //新闻评论id
        var xwcComment = ""
        var xwcFloor = 0
        var time:Long = 0 //时间，两个都用到
        var uName = ""
        get() {
            if (field !=null)
                return field
            else return ""
        }

        var uHeadPortrait = "" //头像 2个都用到
        var commentSum = 0

        var replySum = 0

        //自评论的
        var fromUid = ""
        var toUid = ""
        var rComment = ""
        var rFloor = 0
        var fromUname = ""
        var toUname = ""




        fun getReplyStr(num:Int):String{
            if (num == 0) return "回复"
            else return "${num}回复"
        }

        @SuppressLint("SimpleDateFormat")
        fun getTimeFormat(time:Long):String{
            val date = Date(time.toLong())
            val format = SimpleDateFormat("yyyy MM-dd HH:mm")
            return format.format(date)
        }

    }

    class Data{
        var page = 0
        var total = 0
        var records:ArrayList<Comment> ?= null
        var current = 0
    }
}