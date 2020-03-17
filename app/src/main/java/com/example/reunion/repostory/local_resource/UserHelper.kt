package com.example.reunion.repostory.local_resource

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.util.Base64
import com.example.reunion.MyApplication
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.server.WebSocketServer
import com.example.reunion.util.StringDealerUtil
import java.security.Key
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


object UserHelper {
    @JvmStatic
    private var user:User.Data? = null
    private var isLogin = false
    private var isFirst = true
    var enCode:String = ""
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
            WebSocketServer.setUid(user?.uId?:"")
            return user
        }else{
            return null
        }
    }

    @SuppressLint("CommitPrefEdits")
    fun logout(){
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        this.isLogin = false
        user = null
        WebSocketServer.setUid("")
        userPre.edit().clear().apply()
    }

    @SuppressLint("CommitPrefEdits")
    fun login(user: User){
        this.user = user.data
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        editor.putBoolean("isLogin",true)
        this.isLogin = true
        if (isFirst)
            isFirst = false

        WebSocketServer.setUid(user.data?.uId?:"")
        saveUser(user,editor)
    }

    fun updateTime(time:Long,enCode:String?){
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        if (time > 0L){
            val timeEncode = getEnCodeFromTime(time)
            editor.putString("timeEncode",timeEncode)
            this.time = time
        }
        if (enCode != null){
            this.enCode = enCode
            editor.putString("enCode",enCode)
        }
        editor.apply()
    }

    private fun saveUser(user:User,editor:SharedPreferences.Editor){
        if (user.time > 0L){
            val timeEncode = getEnCodeFromTime(user.time)
            editor.putString("timeEncode",timeEncode)
            this.time = user.time
        }
        this.enCode = user.enCode?:""
        editor.putString("enCode",user.enCode?:"")
        editor.putString("address",user.data?.uAddress?:"")
        editor.putString("birthday",user.data?.uBirthday?:"")
        editor.putString("city",user.data?.uCity?:"")
        editor.putInt("sex",user.data?.uSex?:0)
        editor.putString("district",user.data?.uDistrict?:"")
        editor.putString("headPortrait",user.data?.uHeadPortrait?:"")
        editor.putString("uid",user.data?.uId?:"")
        editor.putString("microblog",user.data?.uMicroblog?:"")
        editor.putString("name",user.data?.uName?:"")
        editor.putString("realName",user.data?.uRealName?:"")
        editor.putString("province",user.data?.uProvince?:"")
        editor.putString("pw",user.data?.uPw?:"")
        editor.putString("signature",user.data?.uSignature?:"")
        editor.putString("qq",user.data?.uQq?:"")
        editor.putString("tele",user.data?.uTele?:"")
        editor.putInt("volunteer",user.data?.uVolunteer?:0)
        editor.putString("weChat",user.data?.uWeChat?:"")
        editor.apply()
    }

    fun saveUser(user:User){
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        if (user.time > 0L){
            val timeEncode = getEnCodeFromTime(user.time)
            editor.putString("timeEncode",timeEncode)
            this.time = user.time
        }
        if (user.enCode!=null&&user.enCode!!.isNotEmpty()){
            this.enCode = user.enCode?:""
            editor.putString("enCode",user.enCode)
        }
        if (user.data?.uBirthday!=null&&user.data?.uBirthday!!.isNotEmpty()){
            editor.putString("birthday",user.data?.uBirthday)
        }
        if (user.data?.uAddress!=null&&user.data?.uAddress!!.isNotEmpty()){
            editor.putString("address",user.data?.uAddress)
        }
        if (user.data?.uCity!=null&&user.data?.uCity!!.isNotEmpty()){
            editor.putString("city",user.data?.uCity)
        }
        if (user.data?.uSex!=null&&user.data?.uSex!=-1){
            editor.putInt("sex",user.data?.uSex!!)
        }
        if (user.data?.uDistrict!=null&&user.data?.uDistrict!!.isNotEmpty()){
            editor.putString("district",user.data?.uDistrict)
        }
        if (user.data?.uHeadPortrait!=null&&user.data?.uHeadPortrait!!.isNotEmpty()){
            editor.putString("headPortrait",user.data?.uHeadPortrait)
        }
        if (user.data?.uId!=null&&user.data?.uId!!.isNotEmpty()){
            editor.putString("uid",user.data?.uId)
        }
        if (user.data?.uMicroblog!=null&&user.data?.uMicroblog!!.isNotEmpty()){
            editor.putString("microblog",user.data?.uMicroblog)
        }
        if (user.data?.uName!=null&&user.data?.uName!!.isNotEmpty()){
            editor.putString("name",user.data?.uName)
        }
        if (user.data?.uRealName!=null&&user.data?.uRealName!!.isNotEmpty()){
            editor.putString("realName",user.data?.uRealName)
        }
        if (user.data?.uProvince!=null&&user.data?.uProvince!!.isNotEmpty()){
            editor.putString("province",user.data?.uProvince)
        }
        if (user.data?.uPw!=null&&user.data?.uPw!!.isNotEmpty()){
            editor.putString("pw",user.data?.uPw)
        }
        if (user.data?.uSignature!=null){
            editor.putString("signature",user.data?.uSignature)
        }
        if (user.data?.uQq!=null&&user.data?.uQq!!.isNotEmpty()){
            editor.putString("qq",user.data?.uQq)
        }
        if (user.data?.uTele!=null&&user.data?.uTele!!.isNotEmpty()){
            editor.putString("tele",user.data?.uTele)
        }
        if (user.data?.uVolunteer!=null&&user.data?.uVolunteer!=-1){
            editor.putInt("volunteer",user.data?.uVolunteer!!)
        }
        if (user.data?.uWeChat!=null&&user.data?.uWeChat!!.isNotEmpty()){
            editor.putString("weChat",user.data?.uWeChat)
        }
        editor.apply()
    }

    fun saveHeader(headUrl:String?,time: Long,enCode: String?){
        val userPre = MyApplication.app.getSharedPreferences("userFile",0)
        val editor = userPre.edit()
        if (time > 0L){
            val timeEncode = getEnCodeFromTime(time)
            editor.putString("timeEncode",timeEncode)
            this.time = time
        }
        if (enCode!=null&&enCode.isNotEmpty()){
            this.enCode = enCode
            editor.putString("enCode",enCode)
        }
        if (headUrl !=null &&headUrl.isNotEmpty()){
            editor.putString("headPortrait",headUrl)
            user?.uHeadPortrait = headUrl
        }
        editor.apply()
    }

    private fun getUserFromLocal(userPre:SharedPreferences):User.Data{
        val userData = User.Data()
        val timeEncode = userPre.getString("timeEncode","")?:""
        time = getTimeFromEncode(timeEncode)
        enCode = userPre.getString("enCode","")?:""
        userData.uAddress = userPre.getString("address","")?:""
        userData.uRealName = userPre.getString("realName","")?:""
        userData.uBirthday = userPre.getString("birthday","")?:""
        userData.uCity = userPre.getString("city","")?:""
        userData.uSex = userPre.getInt("sex",0)
        userData.uDistrict = userPre.getString("district","")?:""
        userData.uHeadPortrait = userPre.getString("headPortrait","")?:""
        userData.uId = userPre.getString("uid","")?:""
        userData.uMicroblog = userPre.getString("microblog","")?:""
        userData.uName = userPre.getString("name","")?:""
        userData.uProvince = userPre.getString("province","")?:""
        userData.uPw = userPre.getString("pw","")?:""
        userData.uQq = userPre.getString("qq","")?:""
        userData.uTele = userPre.getString("tele","")?:""
        userData.uVolunteer = userPre.getInt("volunteer",0)
        userData.uWeChat = userPre.getString("weChat","")?:""
        userData.uSignature = userPre.getString("signature","")?:""
        return userData
    }

    private fun getEnCodeFromTime(time:Long):String{
        val cipher: Cipher = Cipher.getInstance("DES")
        val key: Key = SecretKeySpec(StringDealerUtil.getAndroidIDKey().toByteArray(), "DES")
        cipher.init(Cipher.ENCRYPT_MODE, key)
        val relBytes = cipher.doFinal(time.toString().toByteArray())
        val relBase = Base64.encode(relBytes, Base64.DEFAULT)
        return String(relBase)
    }

    private fun getTimeFromEncode(encode:String):Long{
        val cipher: Cipher = Cipher.getInstance("DES")
        val key: Key = SecretKeySpec(StringDealerUtil.getAndroidIDKey().toByteArray(), "DES")
        cipher.init(Cipher.DECRYPT_MODE, key)
        val decode: ByteArray = Base64.decode(encode, Base64.DEFAULT)
        val bytes = cipher.doFinal(decode)
        val decodeStr = String(bytes)
        return decodeStr.toLong()
    }

}