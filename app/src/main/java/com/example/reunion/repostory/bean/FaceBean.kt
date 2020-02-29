package com.example.reunion.repostory.bean

import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import com.example.reunion.MyApplication
import com.example.reunion.R

class FaceBean() :Parcelable {

    var url:String? = null //面部图片URL地址

    var uid:String? = null //用户ID

    var faceId:String? = null //面部ID

    var header:String? = null //头像Url

    var nickName:String? = null //昵称

    var introduce:String? = null //简介

    var groupId:String? = null //分组ID，地区码

    var groupName:String? = null //分组名，地区名

    var probability:Int = 0 //概率 0 - 100

    /**
     * 下面属性用于view
     */
    var flagDelete = false //标记删除


    constructor(parcel: Parcel) : this() {
        url = parcel.readString()
        uid = parcel.readString()
        faceId = parcel.readString()
        header = parcel.readString()
        nickName = parcel.readString()
        introduce = parcel.readString()
        groupId = parcel.readString()
        groupName = parcel.readString()
        probability = parcel.readInt()
    }

    override fun toString(): String {
        return """
            url:${url}
            uid:${uid}
            faceId:${faceId}
            header:${header}
            nickName:${nickName}
            introduce:${introduce}
            groupId:${groupId}
            groupName:${groupName}
            probability:${probability}
        """.trimIndent()
    }

    class normalBean{
        var code = 0

        var data:FaceBean? = null
    }

    class ListBean{
        var code = 0

        var data:ArrayList<FaceBean>? = null
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(url)
        parcel.writeString(uid)
        parcel.writeString(faceId)
        parcel.writeString(header)
        parcel.writeString(nickName)
        parcel.writeString(introduce)
        parcel.writeString(groupId)
        parcel.writeString(groupName)
        parcel.writeInt(probability)
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