package com.example.reunion.repostory.local_resource

import android.annotation.SuppressLint
import android.app.Application
import android.content.SharedPreferences
import android.util.Log
import com.example.reunion.MyApplication
import com.example.reunion.repostory.bean.User

object UserHelper {
    private var user:User? = null
    private var isLogin = false
    private var isFirst = true
    public fun getUser():User?{
        if(user == null && isFirst){
            synchronized(UserHelper::class.java){
                if(user == null){
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

    private fun loadUser():User?{
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val isLogin = userPre.getBoolean("isLogin",false)
        this.isLogin = isLogin
        if (isFirst)
            isFirst = false
        if(isLogin){
            user = User()
            return user
        }else{
            return null
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun logout(user: User){
        this.user = User()
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        editor.putBoolean("isLogin",false)
    }

    @SuppressLint("CommitPrefEdits")
    fun login(user: User){
        this.user = user
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        editor.putBoolean("isLogin",true)
    }

    private fun saveUser(user:User,editor:SharedPreferences.Editor){

        editor.apply()
    }
}