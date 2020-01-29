package com.example.reunion.viewmodel

import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.HomeRemoteModel
import java.net.ConnectException

class HomeViewModel:BaseViewModel() {
    val remoteModel:HomeRemoteModel by lazy { HomeRemoteModel() }

    /**
     * 检查登录状态，未登录过返回false，否则登录并验证缓存
     */
    fun checkLogin():Boolean{
        if (!UserHelper.isLogin())
            return false
        launch ({
            val userBody = remoteModel.checkLogin()
            when(userBody.code){
                200->{//登录成功但是缓存不可用
                    if (userBody.data!=null){
                        val data = userBody.data
                        UserHelper.login(data,userBody.time)
                    }
                }//缓存可用
                304->{
                    UserHelper.updateTime(userBody.time)
                }
                401->{
                    toast.value = "登录过期，请重新登录"
                }
                402->{
                    toast.value = "登录异常，请重新登录"
                }
            }
        },{
            if (it !is ConnectException){
                UserHelper.logout()//如果不是网络相关的异常就注销
            }
        })
        return true
    }
}