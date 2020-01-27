package com.example.reunion.viewmodel

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import kotlinx.coroutines.delay
import java.sql.Time
import java.util.*

class LoginViewModel():BaseViewModel() {
    //checkbox 选中状态
    val checked = MutableLiveData<Boolean>(false)

    val startVisibility = MutableLiveData<Int>(View.VISIBLE)
    val phoneVisibility = MutableLiveData<Int>(View.INVISIBLE)

    var mobileNumber = MutableLiveData<String>("") //手机号码

    var vCode = MutableLiveData<String>("") //验证码

    var areaCode = MutableLiveData<String>("+86") //区号

    var replyText = MutableLiveData<String>("60秒后重新获取") //重新获取文本

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
        if(num>0){
            num.toString()+"秒后可重新获取"
        }else{
            "获取验证码"
        }

    private fun onStartTask (){
        launch {
            val job = launch {
                repeat(60){
                    replyNum.value = 60 - it - 1
                    delay(1000L)
                }
            }
            job.join()
            isReply.value = true
        }
    }

    fun onPhoneLogin(){

    }

    fun clearNumber(view:View){
        mobileNumber.value = ""
    }

    fun onGetVCode(view:View ?= null){
        if (isReply.value == true){
            isReply.value = false
            onStartTask()
        }
    }
}