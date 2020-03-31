package com.example.reunion.repostory.bean

import android.os.Parcel
import android.os.Parcelable
import android.util.Log

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class TopicBean() :Parcelable {
    var sId:Int = 0
    var uId:String? = null
    var sType:Int = 0
    var sTitle:String? = null
    var pictures:ArrayList<String>? = null//图片
    var sPageView:Int = 0//阅读量
    var sProvince:String? = null //省
    var sCity:String? = null
    var sDistrict:String? = null
    var sContent:String? = null   //发布内容
    var sPriority: Int? = null

    var sJW1:String? = null   //地点的经纬度
    var sJW2:String? = null
    var sJW3:String? = null

    var time:String? = null  //失联时间
    var sendTime:String? = null //发布时间
    var age:Int? = null  //年龄

    var header:String? = null  //头像
    var nickName:String? = null  //昵称

    constructor(parcel: Parcel) : this() {
        nickName = parcel.readString()
        header = parcel.readString()
        sId = parcel.readInt()
        uId = parcel.readString()
        sType = parcel.readInt()
        sTitle = parcel.readString()
        sPageView = parcel.readInt()
        sProvince = parcel.readString()
        sCity = parcel.readString()
        sDistrict = parcel.readString()
        sContent = parcel.readString()
        sPriority = parcel.readValue(Int::class.java.classLoader) as? Int
        sJW1 = parcel.readString()
        sJW2 = parcel.readString()
        sJW3 = parcel.readString()
        time = parcel.readString()
        sendTime = parcel.readString()
        age = parcel.readValue(Int::class.java.classLoader) as? Int
        pictures = ArrayList()
        parcel.readStringList(pictures)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nickName)
        parcel.writeString(header)
        parcel.writeInt(sId)
        parcel.writeString(uId)
        parcel.writeInt(sType)
        parcel.writeString(sTitle)
        parcel.writeInt(sPageView)
        parcel.writeString(sProvince)
        parcel.writeString(sCity)
        parcel.writeString(sDistrict)
        parcel.writeString(sContent)
        parcel.writeValue(sPriority)
        parcel.writeString(sJW1)
        parcel.writeString(sJW2)
        parcel.writeString(sJW3)
        parcel.writeString(time)
        parcel.writeString(sendTime)
        parcel.writeValue(age)
        parcel.writeStringList(pictures)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TopicBean> {
        override fun createFromParcel(parcel: Parcel): TopicBean {
            return TopicBean(parcel)
        }

        override fun newArray(size: Int): Array<TopicBean?> {
            return arrayOfNulls(size)
        }
    }

    class Bean{
        val code = 0
        var msg:String? = null
        var data:TopicBean? = null
    }

    class Beans{
        val code = 0
        var msg:String? = null
        var data:ArrayList<TopicBean>? = null
    }

    fun getReadString(num:Int):String{
        if (num >= 1000){
            val formatNum = num / 1000
            return "${formatNum/10f}万人看过"
        }
        return "${num}人看过"
    }

    fun getFirstPicture():String?{
        if (pictures != null&&pictures!!.size>0)
            return pictures!![0]
        else
            return null
    }
}