package com.example.reunion.view_model

import android.content.IntentFilter
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.TopicRemoteModel

class StarViewModel:BaseViewModel() {
    lateinit var receiver:TopicFragViewModel.Receiver

    private val remote = TopicRemoteModel()

    private var nextPage = 1

    val topicData = MutableLiveData<ArrayList<TopicBean>>()

    val refreshing = MutableLiveData<Boolean>()

    val loading = MutableLiveData<Boolean>()

    val deleteData = MutableLiveData<Int>()

    fun onRefresh(first:Boolean = false){
        if (refreshing.value == true){
            return
        }
        nextPage = 1
        refreshing.value = true
        onLoading()
    }


    fun onLoading(first:Boolean = false){
        if (loading.value == true||!UserHelper.isLogin()){
            return
        }
        loading.value = true
        launch ({
            val bean = remote.topicStarSearch(UserHelper.getUser()?.uId?:throw Exception("uid异常"),nextPage)
            when(bean.code){
                200 ->{
                    topicData.value = bean.data
                    nextPage += 1
                }
                300 ->{
                    if (!first)
                        toast.value = "已经没有更多内容了"
                }
                else ->{
                    throw Exception(bean.msg)
                }
            }
        },{
            toast.value = it.message
        },{
            refreshing.value = false
            loading.value = false
        })
    }
}