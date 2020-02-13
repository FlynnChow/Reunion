package com.example.reunion.repostory.local_resource

import android.annotation.SuppressLint
import android.content.SharedPreferences
import com.example.reunion.MyApplication
import com.example.reunion.repostory.bean.User
import com.example.reunion.util.StringDealerUtil
import java.time.ZoneId


object UserHelper {
    private var user:User.Data? = null
    private var isLogin = false
    private var isFirst = true
    var enCode = ""
    var time = 0L
    var timeMD = ""
    fun getUser():User.Data?{
        if(user == null && isFirst){
            synchronized(UserHelper::class.java){
                if(user == null && isFirst){
                    user = loadUser()
                }
            }
        }
        return user
    }

    fun isLogin():Boolean{
        getUser()
        return isLogin
    }

    private fun loadUser():User.Data?{
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val isLogin = userPre.getBoolean("isLogin",false)
        this.isLogin = isLogin
        if (isFirst)
            isFirst = false
        if(isLogin){
            user = getUserFromLocal(userPre)
            return user
        }else{
            return null
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun logout(){
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        this.isLogin = false
        userPre.edit().clear().apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun login(user: User){
        this.user = user.data
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        editor.putBoolean("isLogin",true)
        this.isLogin = true
        saveUser(user,editor)
    }

    fun updateTime(time:Long){
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        editor.putLong("time",time)
        editor.apply()
    }

    private fun saveUser(user:User,editor:SharedPreferences.Editor){
        val timeMD = StringDealerUtil.getEncryptString(user.time)
        editor.putString("timeMD",timeMD)
        editor.putLong("time",user.time)
        editor.putString("enCode",user.enCode)
        editor.putString("address",user.data?.uAddress)
        editor.putString("birthday",user.data?.uBirthday)
        editor.putString("city",user.data?.uCity)
        editor.putInt("sex",user.data?.uSex!!)
        editor.putString("district",user.data?.uDistrict)
        editor.putString("headPortrait",user.data?.uHeadPortrait)
        editor.putString("uid",user.data?.uId)
        editor.putString("microblog",user.data?.uMicroblog)
        editor.putString("name",user.data?.uName)
        editor.putString("province",user.data?.uProvince)
        editor.putString("pw",user.data?.uPw)
        editor.putString("qq",user.data?.uQq)
        editor.putString("tele",user.data?.uTele)
        editor.putInt("volunteer",user.data?.uVolunteer!!)
        editor.putString("weChat",user.data?.uWeChat)
        editor.apply()
    }

    private fun getUserFromLocal(userPre:SharedPreferences):User.Data{
        val userData = User.Data()
        time = userPre.getLong("time",0L)
        timeMD = userPre.getString("timeMD","")!!
        enCode = userPre.getString("enCode","")!!
        userData.uAddress = userPre.getString("address","")!!
        userData.uBirthday = userPre.getString("birthday","")!!
        userData.uCity = userPre.getString("city","")!!
        userData.uSex = userPre.getInt("sex",0)
        userData.uDistrict = userPre.getString("district","")!!
        userData.uHeadPortrait = userPre.getString("headPortrait","")!!
        userData.uId = userPre.getString("uid","")!!
        userData.uMicroblog = userPre.getString("microblog","")!!
        userData.uName = userPre.getString("name","")!!
        userData.uProvince = userPre.getString("province","")!!
        userData.uPw = userPre.getString("pw","")!!
        userData.uQq = userPre.getString("qq","")!!
        userData.uTele = userPre.getString("tele","")!!
        userData.uVolunteer = userPre.getInt("volunteer",0)
        userData.uWeChat = userPre.getString("weChat","")!!
        return userData
    }

}