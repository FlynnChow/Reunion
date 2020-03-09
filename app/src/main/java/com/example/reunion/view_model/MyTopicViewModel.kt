package com.example.reunion.view_model

import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.remote_resource.HomeRemoteModel

class MyTopicViewModel:BaseViewModel() {
    val remote = HomeRemoteModel()

    var nextPeoplePage = 1
    var nextBodyPage = 1
    var refreshingBody = MutableLiveData<Boolean>()
    var loadingBody = MutableLiveData<Boolean>()
    var refreshingPeople = MutableLiveData<Boolean>()
    var loadingPeople = MutableLiveData<Boolean>()

    var uid = "" //User Uid

    var user:User.Data? = null

    val header = MutableLiveData<String>()

    val nickName = MutableLiveData<String>()

    val signature = MutableLiveData<String>()

    val peopleData = MutableLiveData<ArrayList<TopicBean>>()

    val bodyData = MutableLiveData<ArrayList<TopicBean>>()

    var stateFollow = MutableLiveData(false)

    fun initUserMessage(uid:String){
        launch ({
            val bean = remote.obtainSingleUserMessage(uid)
            when(bean.code){
                200 ->{
                    user = bean.data
                    header.value = user?.uHeadPortrait
                    nickName.value = user?.uName
                    signature.value = user?.uSignature

                    loadPeopleData()
                    loadBodyData()
                }
                else ->{
                    toast.value = "获取用户信息失败。\n" + bean.msg
                }
            }
        },{
            toast.value = "获取用户信息失败。\n" + it.message
        })
    }

    fun loadPeopleData(){
        if (loadingPeople.value == true) return
        launch ({
            val bean = remote.obtainUserTopic(1,uid,nextPeoplePage)
            when(bean.code){
                200 ->{
                    nextPeoplePage += 1
                    if (bean.data != null)
                        peopleData.value = bean.data
                }
                300 ->{
                    toast.value = "已经没有内容可以加载了"
                }
                400 ->{
                    toast.value = "加载错误："+ bean.msg
                }
            }
            loadingPeople.value = false
            refreshingPeople.value = false
        },{
            toast.value = "加载错误："+it.message
            loadingPeople.value = false
            refreshingPeople.value = false
        })
    }

    fun loadBodyData(){
        if (loadingBody.value == true) return
        launch ({
            val bean = remote.obtainUserTopic(1,uid,nextPeoplePage)
            when(bean.code){
                200 ->{
                    nextBodyPage += 1
                    if (bean.data != null)
                        bodyData.value = bean.data
                }
                300 ->{
                    toast.value = "已经没有内容可以加载了"
                }
                400 ->{
                    toast.value = "加载错误："+ bean.msg
                }
            }
            loadingBody.value = false
            refreshingBody.value = false
        },{
            toast.value = "加载错误："+it.message
            loadingBody.value = false
            refreshingBody.value = false
        })
    }

    fun onRefreshBody(){
        if (refreshingBody.value == true) return
        refreshingBody.value = true
        nextBodyPage = 1
        loadBodyData()
    }

    fun onRefreshPeople(){
        if (refreshingPeople.value == true) return
        refreshingPeople.value = true
        nextPeoplePage = 1
        loadPeopleData()
    }

    fun getFollowString(followState:Boolean):String{
        if (followState){
            return MyApplication.app.getString(R.string.my_topic_follow_cancel)
        }else{
            return MyApplication.app.getString(R.string.my_topic_follow)
        }
    }
}