package com.example.reunion.view_model

import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.UserRemoteModel
import java.lang.Exception

class UserViewModel:BaseViewModel() {
    private val remoteModel = UserRemoteModel()

    val userData = MutableLiveData<ArrayList<User.Data>>()

    val refreshing = MutableLiveData<Boolean>()

    fun onRefresh(type:String,first:Boolean = false){
        if (refreshing.value == true){
            return
        }
        refreshing.value = true

        launch({
            if (!UserHelper.isLogin()){
                if (!first){
                    toast.value = "需要登录才能查看该信息"
                }
            }
            else
                when(type){
                    "follow"->{
                        onLoadFollow()
                    }
                    "fans"->{
                        onLoadFans()
                    }
                    "friend"->{
                        onLoadFriend()
                    }
                }
        },{
            toast.value = "错误："+it.message
        },{
            refreshing.value = false
        })
    }

    private suspend fun onLoadFans(){
        val bean = remoteModel.userFan(UserHelper.getUser()?.uId?:throw Exception("uid异常"))
        when(bean.code){
            200 ->{
                userData.value = bean.data
            }
            300 ->{}
            else -> throw Exception(bean.msg)
        }
    }

    private suspend fun onLoadFollow(){
        val bean = remoteModel.userFollow(UserHelper.getUser()?.uId?:throw Exception("uid异常"))
        when(bean.code){
            200 ->{
                userData.value = bean.data
            }
            300 ->{}
            else -> throw Exception(bean.msg)
        }
    }

    private suspend fun onLoadFriend(){
        val bean = remoteModel.userFriends(UserHelper.getUser()?.uId?:throw Exception("uid异常"))
        when(bean.code){
            200 ->{
                userData.value = bean.data
            }
            300 ->{}
            else -> throw Exception(bean.msg)
        }
    }
}