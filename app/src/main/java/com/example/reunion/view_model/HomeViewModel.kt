package com.example.reunion.view_model

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.local_resource.HomePageSt
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.HomeRemoteModel
import com.example.reunion.util.StringDealerUtil
import com.example.reunion.view.*
import java.net.ConnectException
import java.net.UnknownHostException
import java.security.Key
import javax.crypto.spec.SecretKeySpec


class HomeViewModel:BaseViewModel() {
    var currentIndex = 0 //用于保存当前viewpager的index

    private val remoteModel:HomeRemoteModel by lazy { HomeRemoteModel() }


    val user: MutableLiveData<User.Data> = MutableLiveData<User.Data>()

    //以下为homePager的adapter所有
    val isHomeUpdate = MutableLiveData(false)
    val followFragment = FollowFragment()
    val recommendFragment = RecommendFragment()
    val nearbyFragment = NearbyFragment()
    val findFragment = FindFragment()
    val newsFragment = NewsFragment()
    val claimFragment = ClaimFragment()
    val fragmentList by lazy { ArrayList<Fragment>().apply {
        add(followFragment)
        add(recommendFragment)
        add(nearbyFragment)
        add(findFragment)
        add(claimFragment)
        add(newsFragment)
    }}
    var ids:ArrayList<Long> = ArrayList()
    //以下为homePager的adapter所有 ids：用于改变viewpager

    /**
     * 检查登录状态，未登录过返回false，否则登录并验证缓存
     */
    fun checkLogin():Boolean{
        if (!UserHelper.isLogin()){
            user.value = User.Data()
            return false
        }
        val user = UserHelper.getUser()!!
        this.user.value = user
        launch ({
            val userBody = remoteModel.checkLogin(UserHelper.getUser()?.uId?:"",UserHelper.time,UserHelper.enCode)
            when(userBody.code){
                200->{//登录成功但是缓存不可用
                    if (userBody.data!=null){
                        UserHelper.login(userBody)
                    }
                }//缓存可用
                300->{
                    UserHelper.updateTime(userBody.time,userBody.enCode)
                }
                301->{
                    toast.value = "登录过期，请重新登录"
                    UserHelper.logout()
                }
                304->{
                    toast.value = "信息异常，请重新登录"
                    UserHelper.logout()
                }else ->{
                    toast.value = "登录失败，请重新登录"
                    UserHelper.logout()
                }
            }
            updateUser()
        },{
            if (!(it is ConnectException||it is UnknownHostException)){
                toast.value = "未知异常,请重新登录"
                UserHelper.logout()//如果不是网络相关的异常就注销
                updateUser()
            }
        })
        return true
    }

    fun updateUser(){
        if(UserHelper.isLogin())
            user.value = UserHelper.getUser()
        else
            user.value = User.Data()
    }

    /**
     * 检查并更新首页fragment的可选状况选择
     * 并且更新到首页到viewpager中
     */
    fun updateFragment(){
        ids.clear()
        if (HomePageSt.instance.getFollowStatus()){
            ids.add(0L)
        }
        if (HomePageSt.instance.getRecommendStatus()){
            ids.add(1L)
        }
        if (HomePageSt.instance.getNearbyStatus()){
            ids.add(2L)
        }
        ids.add(3L)
        ids.add(4L)
        if (HomePageSt.instance.getNewsStatus()){
            ids.add(5L)
        }
        isHomeUpdate.value = true
    }
}