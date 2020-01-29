package com.example.reunion.repostory.bean


class User {
    val code = 0
    val msg = ""
    val time:Long = 0L
    val data:Data? = null
    class Data{
        var uId = ""
        var uName = ""
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
        var uUpdateTime = ""

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
            uUpdateTime = $uUpdateTime
            """.trimIndent()
        }
    }
}