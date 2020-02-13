package com.example.reunion.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.customize.UploadRequestBody
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.HomeRemoteModel
import com.example.reunion.repostory.remote_resource.UpLoadRemoteModel
import com.example.reunion.util.StringDealerUtil
import okhttp3.MultipartBody
import java.io.File
import java.net.ConnectException
class HomeViewModel:BaseViewModel() {
    var currentIndex = 0 //用于保存当前viewpager的index

    private val remoteModel:HomeRemoteModel by lazy { HomeRemoteModel() }

    private val uploadModel:UpLoadRemoteModel by lazy { UpLoadRemoteModel() }

    var header = MutableLiveData<String>("")

    /**
     * 检查登录状态，未登录过返回false，否则登录并验证缓存
     */
    fun checkLogin():Boolean{
        if (!UserHelper.isLogin())
            return false
        val user = UserHelper.getUser()!!
        val time = UserHelper.time
        val timeMD = UserHelper.timeMD
        val checkMD = StringDealerUtil.getEncryptString(time)
        if(timeMD != checkMD){
            toast.value = "登录信息异常，请重新登录"
            UserHelper.logout()
            return false
        }
        launch ({
            val userBody = remoteModel.checkLogin(user.uId,time,UserHelper.enCode)
            when(userBody.code){
                200->{//登录成功但是缓存不可用
                    if (userBody.data!=null){
                        UserHelper.login(userBody)
                    }
                }//缓存可用
                304->{
                    UserHelper.updateTime(userBody.time)
                }
                301->{
                    toast.value = "登录失效，请重新登录"
                    UserHelper.logout()
                }
                300->{
                    toast.value = "登录信息异常，请重新登录"
                    UserHelper.logout()
                }else ->{
                    toast.value = "登录失败，请重新登录"

                    UserHelper.logout()
                }
            }
        },{
            if (it !is ConnectException){
                toast.value = "为止异常，请重新登录"
                UserHelper.logout()//如果不是网络相关的异常就注销
            }
        })
        return true
    }

    fun uploadHeader(){
        if (!UserHelper.isLogin())
            return
        val file = File(header.value!!)
        val uploadBody = UploadRequestBody.getRequestBody(file,"header"){
            Log.d("上传进度测试：",it.toString())
        }
        val body = MultipartBody.Builder()
            .addFormDataPart("uId",UserHelper.getUser()!!.uId)
            .addFormDataPart("time",UserHelper.time.toString())
            .addFormDataPart("enCode",UserHelper.enCode)
            .addFormDataPart("headPhoto",file.name,uploadBody)
            .build()
        launch {
            val bean = uploadModel.uploadHeader(body)
        }
    }
}