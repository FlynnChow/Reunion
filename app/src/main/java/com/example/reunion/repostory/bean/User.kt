package com.example.reunion.repostory.bean

import android.os.Parcel
import android.os.Parcelable
import com.example.reunion.MyApplication
import com.example.reunion.R
import kotlinx.android.synthetic.main.fragment_news.*
import java.time.LocalDateTime


class User {
    var code = 0
    var msg = ""
    var time:Long = 0L
    var enCode:String? = null
    get() {
        return if (field == null)
            ""
        else
            field
    }
    var data:Data? = null
    class Data() :Parcelable{
        var uId = ""
        var uName = ""
        var uRealName = ""
        var uPw = ""
        var uSex = 0
        var uBirthday = ""
        var uHeadPortrait = ""
        var uTele = ""
        var uProvince = ""
        var uCity = ""
        var uDistrict = ""
        var uAddress = ""
        var uQq = ""
        var uWeChat = ""
        var uMicroblog = ""
        var uVolunteer = 0
        var uSignature = ""

        var namePY = ""

        constructor(parcel: Parcel) : this() {
            uId = parcel.readString()?:""
            uName = parcel.readString()?:""
            uRealName = parcel.readString()?:""
            uPw = parcel.readString()?:""
            uSex = parcel.readInt()
            uBirthday = parcel.readString()?:""
            uHeadPortrait = parcel.readString()?:""
            uTele = parcel.readString()?:""
            uProvince = parcel.readString()?:""
            uCity = parcel.readString()?:""
            uDistrict = parcel.readString()?:""
            uAddress = parcel.readString()?:""
            uQq = parcel.readString()?:""
            uWeChat = parcel.readString()?:""
            uMicroblog = parcel.readString()?:""
            uVolunteer = parcel.readInt()
            uSignature = parcel.readString()?:""
        }

        override fun toString(): String {
            return """
            uId = $uId
            uName = $uName
            uPw = $uPw
            uSex = $uSex
            uBirthday = $uBirthday
            uHeadPortrait = $uHeadPortrait
            uTele = $uTele
            uProvince = $uProvince
            uCity = $uCity
            uDistrict = $uDistrict
            uAddress = $uAddress
            uQq = $uQq
            uWeChat = $uWeChat
            uMicroblog = $uMicroblog
            uVolunteer = $uVolunteer
            """.trimIndent()
        }

        fun getSignature(signature:String?):String{
            return "${MyApplication.resource().getString(R.string.signature)}：${signature?:""}"
        }

        fun getSimpleSignature(signature:String?):String{
            if (signature == null||signature.length<20){
                return signature?:""
            }else{
                val simple = signature.substring(0,19)
                return "$simple..."
            }
        }

        fun getName(name:String?):String{
            if (name==null||name.isEmpty()){
                return MyApplication.resource().getString(R.string.no_login)
            }
            return name
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(uId)
            parcel.writeString(uName)
            parcel.writeString(uRealName)
            parcel.writeString(uPw)
            parcel.writeInt(uSex)
            parcel.writeString(uBirthday)
            parcel.writeString(uHeadPortrait)
            parcel.writeString(uTele)
            parcel.writeString(uProvince)
            parcel.writeString(uCity)
            parcel.writeString(uDistrict)
            parcel.writeString(uAddress)
            parcel.writeString(uQq)
            parcel.writeString(uWeChat)
            parcel.writeString(uMicroblog)
            parcel.writeInt(uVolunteer)
            parcel.writeString(uSignature)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Data> {
            override fun createFromParcel(parcel: Parcel): Data {
                return Data(parcel)
            }

            override fun newArray(size: Int): Array<Data?> {
                return arrayOfNulls(size)
            }
        }
    }

    class UserJson{
        var uName = ""
        var uRealName = ""
        var uSex = 0
        var uProvince = ""
        var uCity = ""
        var uDistrict = ""
        var uSignature = ""
        var uBirthday = ""
    }

    class UserBeans{
        var code = 0
        var msg = ""
        var data:ArrayList<User.Data>? = null

        fun getFirstUser():User.Data?{
            if (data != null && data!!.size > 0){
                return data!![0]
            }
            return null
        }
    }
}