package com.example.reunion.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.LoginRemoteModel
import kotlinx.coroutines.delay
import java.sql.Time
import java.util.*

class LoginViewModel:BaseViewModel() {

    private val remoteModel by lazy { LoginRemoteModel() }

    //checkbox 选中状态
    val checked = MutableLiveData<Boolean>(false)
    val startVisibility = MutableLiveData<Int>(View.VISIBLE)
    val phoneVisibility = MutableLiveData<Int>(View.INVISIBLE)

    var mobileNumber = MutableLiveData<String>("") //手机号码

    var vCode = MutableLiveData<String>("") //验证码

    var areaCode = MutableLiveData<String>("+86") //区号

    val replyNum = MutableLiveData<Int>(60) //重新获取倒计时

    val isReply = MutableLiveData<Boolean>(true)
    /**
     * 当前页面：0.开始 1.手机登录 2.手机验证码
     */
    val currentPage = MutableLiveData(0)

    fun showStartView(view: View? = null){
        startVisibility.value = View.VISIBLE
        phoneVisibility.value = View.INVISIBLE
        currentPage.value = 0
    }

    fun showPhoneView(view: View? = null){
        startVisibility.value = View.INVISIBLE
        phoneVisibility.value = View.VISIBLE
        currentPage.value = 1
    }

    fun getTimeText(num:Int) =
        if(num in 1..59){
            num.toString()+"秒后可重新获取"
        }else {
            "获取验证码"
        }


    fun onPhoneLogin(){
        launch {
            val userBody = remoteModel.onPhoneLogin(mobileNumber.value!!,vCode.value!!)
            when(userBody.code){
                200->{
                    if(userBody.data!=null){
                        val data = userBody.data
                        UserHelper.login(data,userBody.time)
                    }else{
                        toast.value = "登录失败，请重试"
                    }
                }
                400->{
                    toast.value = "验证码错误！"
                }else ->{
                    toast.value = "发生未知错误，请重试"
                }
            }
        }
    }

    //判断是否可以登录
    fun isLogin(checked:Boolean,isRetry:Boolean)= checked&&!isRetry

    fun clearNumber(view:View){
        mobileNumber.value = ""
    }

    fun onGetVCode(view:View ?= null){
        if (isReply.value == true){
            onSendMessage(mobileNumber.value!!)
        }else{
            toast.value = "请${replyNum.value}秒后再重试"
        }
        isReply.value = false
    }

    private fun onSendMessage(phone:String){
        launch {
            val codeMessage = remoteModel.onSendMessage(phone)
            when (codeMessage.return_code) {
                "00000" -> {
                    isReply.value = false
                    toast.value = "发送短信成功"
                    val job = launch {
                        repeat(60){
                            replyNum.value = 60 - it - 1
                            delay(1000L)
                        }
                    }
                    job.join()
                    isReply.value = true
                }
                "10007" -> {
                    toast.value = "未找到号码归属地"
                }
                else -> {
                    toast.value = "服务出错了"
                }
            }
        }
    }
}