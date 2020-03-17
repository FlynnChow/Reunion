package com.example.reunion.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.HomeRemoteModel

class TopicFragViewModel:BaseViewModel() {
    private val remote = HomeRemoteModel()
    // 寻人和寻物
    var type = MutableLiveData(0)
    var province = MutableLiveData("")
    var city = MutableLiveData("")
    var district = MutableLiveData("")
    var ageView = MutableLiveData("")
    var areaView = MutableLiveData("")

    var age = MutableLiveData<String>(null)
    var time = MutableLiveData("")
    var area = MutableLiveData<String>()

    var timeSelected = false

    // 普通混杂的
    val newData = MutableLiveData<ArrayList<TopicBean>>()

    val refreshing = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()
    private var nextPage = 1

    var locate = "0,0"

    fun updateItems(type:String,first:Boolean = false){
        if (loading.value == true) return
        loading.value = true
        launch({
            when(type){
                "follow" -> updateFollow(first)
                "recommend" -> updateRecommend(first)
                "nearby" -> updateNearby(first)
                "people" -> updatePeople(first)
                "body" -> updateBody(first)
            }
        },{
            toast.value = it.message
        },{
            loading.value = false
            if (refreshing.value == true)
                refreshing.value = false
        })
    }

    fun onRefresh(type:String){
        refreshing.value = true
        nextPage = 1
        updateItems(type)
    }

    private suspend fun updateFollow(first:Boolean = false){
        if (UserHelper.isLogin()){
            val bean = remote.obtainFollowTopic(UserHelper.getUser()?.uId!!,nextPage)
            when(bean.code){
                200 ->{
                    if (bean.data != null){
                        newData.value = bean.data
                        nextPage += 1
                    }
                }
                300 ->{
                    if (!first)
                        toast.value = "已经没有内容了"
                }
                400 ->{
                    toast.value = "UID异常"
                }
            }
        }else{
            //没有登录，什么都不做
        }
    }

    private suspend fun updateRecommend(first:Boolean = false){
        val bean = remote.obtainRecommendTopic(nextPage)
        when(bean.code){
            200 ->{
                if (bean.data != null){
                    newData.value = bean.data
                    nextPage += 1
                }
            }
            300 ->{
                if (!first)
                    toast.value = "已经没有内容了"
            }
        }
    }

    private suspend fun updateNearby(first:Boolean = false){
        val bean = remote.obtainNearbyTopic(locate,nextPage)
        when(bean.code){
            200 ->{
                if (bean.data != null){
                    newData.value = bean.data
                    nextPage += 1
                }
            }
            300 ->{
                if (!first)
                    toast.value = "已经没有内容了"
            }
        }
    }

    private suspend fun updatePeople(first:Boolean = false){
        val bean = remote.obtainPeopleTopic(
            nextPage,
            if (time.value != null&&time.value != ""&&timeSelected) time.value else null,
            if (age.value != null&&age.value != "") age.value else null,
            if (province.value != null&&province.value != "") city.value else null,
            if (city.value != null&&city.value != "") city.value else null,
            if (district.value != null&&district.value != "") district.value else null
        )
        when(bean.code){
            200 ->{
                if (bean.data != null){
                    newData.value = bean.data
                    nextPage += 1
                }
            }
            300 ->{
                if (!first)
                    toast.value = "已经没有内容了"
            }
            400 ->{
                toast.value = "UID异常"
            }
        }
    }

    private suspend fun updateBody(first:Boolean = false){
        val bean = remote.obtainBodyTopic(
            nextPage,
            if (time.value != null&&time.value != ""&&timeSelected) time.value else null,
            if (province.value != null&&province.value != "") city.value else null,
            if (city.value != null&&city.value != "") city.value else null,
            if (district.value != null&&district.value != "") district.value else null
        )
        when(bean.code){
            200 ->{
                if (bean.data != null){
                    newData.value = bean.data
                    nextPage += 1
                }
            }
            300 ->{
                if (!first)
                    toast.value = "已经没有内容了"
            }
            400 ->{
                toast.value = "UID异常"
            }
        }
    }
}