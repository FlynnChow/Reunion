package com.example.reunion.repostory.local_resource

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.reunion.MyApplication
import com.example.reunion.repostory.bean.User

object UserHelper {
    private var user:User.Data? = null
    private var isLogin = false
    private var isFirst = true
    var time = 0L
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
    fun login(user: User.Data,time:Long){
        this.user = user
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        editor.putBoolean("isLogin",true)
        this.isLogin = true
        saveUser(user,time,editor)
    }

    fun updateTime(time:Long){
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        editor.putLong("time",time)
        editor.apply()
    }

    private fun saveUser(user:User.Data,time:Long,editor:SharedPreferences.Editor){
        editor.putLong("time",time)
        editor.putString("address",user.uAddress)
        editor.putString("birthday",user.uBirthday)
        editor.putString("city",user.uCity)
        editor.putInt("sex",user.uSex)
        editor.putString("district",user.uDistrict)
        editor.putString("headPortrait",user.uHeadPortrait)
        editor.putString("uid",user.uId)
        editor.putString("microblog",user.uMicroblog)
        editor.putString("name",user.uName)
        editor.putString("province",user.uProvince)
        editor.putString("pw",user.uPw)
        editor.putString("qq",user.uQq)
        editor.putString("tele",user.uTele)
        editor.putString("updateTime",user.uUpdateTime)
        editor.putInt("volunteer",user.uVolunteer)
        editor.putString("weChat",user.uWeChat)
        editor.apply()
    }

    private fun getUserFromLocal(userPre:SharedPreferences):User.Data{
        val userData = User.Data()
        time = userPre.getLong("time",0L)
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
        userData.uUpdateTime = userPre.getString("updateTime","")!!
        userData.uVolunteer = userPre.getInt("volunteer",0)
        userData.uWeChat = userPre.getString("weChat","")!!
        return userData
    }

}