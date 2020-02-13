package com.example.reunion.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.LoginRemoteModel
import kotlinx.coroutines.delay
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

    val isSendSuccess = MutableLiveData<Boolean>(false)
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
            Log.d("测试code:",""+userBody.code)
            when(userBody.code){
                200->{
                    if(userBody.data!=null){
                        UserHelper.login(userBody)
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
    fun isLogin(checked:Boolean,isRetry:Boolean,isSendSuccess:Boolean)= (checked&&!isRetry)||(checked&&isSendSuccess)

    fun clearNumber(view:View){
        mobileNumber.value = ""
    }

    fun onGetVCode(view:View ?= null){
        isSendSuccess.value = false
        if (isReply.value == true){
            onSendMessage(mobileNumber.value!!)
        }else{
            toast.value = "请${replyNum.value}秒后再重试"
        }
    }

    private fun onSendMessage(phone:String){
        launch {
            val codeMessage = remoteModel.onSendMessage(phone)
            when (codeMessage.code) {
                0L -> {
                    isReply.value = false
                    isSendSuccess.value = true
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
                10007L -> {
                    toast.value = "未找到号码归属地"
                }
                else -> {
                    toast.value = "发送失败"
                }
            }
        }
    }
}