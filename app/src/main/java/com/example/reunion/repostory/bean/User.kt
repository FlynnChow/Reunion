package com.example.reunion.repostory.bean

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
    class Data{
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

        fun getName(name:String?):String{
            if (name==null||name.isEmpty()){
                return MyApplication.resource().getString(R.string.no_login)
            }
            return name
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