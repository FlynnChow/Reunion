package com.example.reunion.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.CommunityBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.CommunityRemote
import kotlin.math.E

class CommunityItemViewModel:BaseViewModel() {
     private val remote = CommunityRemote()

     val refreshing = MutableLiveData<Boolean>()

     val loading = MutableLiveData<Boolean>()

     val communityData = MutableLiveData<ArrayList<CommunityBean>>()

     private var nextPage = 1

     private fun onLoadFollow(first:Boolean){
          if (loading.value == true)
               return
          loading.value = true
          launch ({
               if (!UserHelper.isLogin()){
                    if (!first)
                         throw java.lang.Exception("需要登录后才可获取关注用户发布的信息")
                    return@launch
               }

               val bean
                       = remote.obtainCommunityFollow(
                    UserHelper.getUser()?.uId?:throw Exception("uid is null"),nextPage)
               when(bean.code){
                    200 ->{
                         communityData.value = bean.data
                         nextPage += 1
                    }
                    300 ->{
                         if (!first)
                              toast.value = "没有更多内容了"
                    }
                    else ->{
                         throw Exception(bean.msg)
                    }
               }
          },{
               toast.value = "加载错误："+it.message
          },{
               loading.value = false
               refreshing.value = false
          })
     }

     private fun onLoadRecommend(first:Boolean){
          if (loading.value == true)
               return
          loading.value = true
          launch ({
               val bean
                       = remote.obtainCommunityRecommend(nextPage)
               when(bean.code){
                    200 ->{
                         communityData.value = bean.data
                         nextPage += 1
                    }
                    300 ->{
                         if (!first)
                              toast.value = "没有更多内容了"
                    }
                    else ->{
                         throw Exception(bean.msg)
                    }
               }
          },{
               toast.value = "加载失败："+it.message
          },{
               loading.value = false
               refreshing.value = false
          })
     }

     private fun onLoadMine(first:Boolean){
          if (loading.value == true)
               return
          loading.value = true
          launch ({
               if (!UserHelper.isLogin()){
                    if (!first)
                         throw java.lang.Exception("登录后才可获取到自己发布的信息")
                    return@launch
               }

               val bean
                       = remote.obtainCommunityUser(
                    UserHelper.getUser()?.uId?:throw Exception("uid is null"),nextPage)
               when(bean.code){
                    200 ->{
                         communityData.value = bean.data
                         nextPage += 1
                    }
                    300 ->{
                         if (!first)
                              toast.value = "没有更多内容了"
                    }
                    else ->{
                         throw Exception(bean.msg)
                    }
               }
          },{
               toast.value = "加载失败："+it.message
          },{
               loading.value = false
               refreshing.value = false
          })
     }

     fun onLoadCommunity(type:String,first:Boolean = false){
          when(type){
               "follow"->{
                    onLoadFollow(first)
               }
               "community"->{
                   onLoadRecommend(first)
               }
               "mine"->{
                    onLoadMine(first)
               }
          }
     }

     fun onRefresh(type:String){
          if (refreshing.value == true)
               return
          refreshing.value = true
          nextPage = 1
          onLoadCommunity(type)
     }
}