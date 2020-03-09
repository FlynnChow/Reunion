package com.example.reunion.repostory.bean

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import com.example.reunion.MyApplication
import com.example.reunion.R
import kotlin.math.roundToInt

class FaceBean() :Parcelable {

    var url:String? = null //面部图片URL地址

    var uid:String? = null //用户ID

    var faceId:String? = null //面部ID

    var header:String? = null //头像Url

    var nickName:String? = null //昵称

    var introduce:String? = null //简介

    var probability:Float = 0f //概率 0 - 100

    /**
     * 下面属性用于view
     */
    var flagDelete = false //标记删除

    fun getProString(num:Float):String{
        val num2 =(((num * 100).roundToInt()) /100f)
        return "匹配度 ${num2}%"
    }


    constructor(parcel: Parcel) : this() {
        url = parcel.readString()
        uid = parcel.readString()
        faceId = parcel.readString()
        header = parcel.readString()
        nickName = parcel.readString()
        introduce = parcel.readString()
        probability = parcel.readFloat()
    }

    override fun toString(): String {
        return """
            url:${url}
            uid:${uid}
            faceId:${faceId}
            header:${header}
            nickName:${nickName}
            introduce:${introduce}
            probability:${probability}
        """.trimIndent()
    }

    class normalBean{
        var code = 0

        var msg:String? = null

        var data:FaceBean? = null
    }

    class ListBean{
        var code = 0

        var msg:String? = null

        var data:ArrayList<FaceBean>? = null
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(uid)
        parcel.writeString(faceId)
        parcel.writeString(header)
        parcel.writeString(nickName)
        parcel.writeString(introduce)
        parcel.writeFloat(probability)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<FaceBean> {
        override fun createFromParcel(parcel: Parcel): FaceBean {
            return FaceBean(parcel)
        }

        override fun newArray(size: Int): Array<FaceBean?> {
            return arrayOfNulls(size)
        }
    }

    fun getDeleteBg(delete:Boolean):Drawable{
        return if (delete)
            MyApplication.resource().getDrawable(R.drawable.face_delete_pressed)
        else
            MyApplication.resource().getDrawable(R.drawable.face_delete_normal)
    }
}