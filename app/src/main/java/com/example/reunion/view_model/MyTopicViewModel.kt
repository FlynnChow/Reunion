package com.example.reunion.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.HomeRemoteModel
import java.lang.Exception

class MyTopicViewModel:BaseViewModel() {
    val remote = HomeRemoteModel()

    private var nextPeoplePage = 1
    private var nextBodyPage = 1
    var refreshingBody = MutableLiveData<Boolean>()
    var loadingBody = MutableLiveData<Boolean>()
    var refreshingPeople = MutableLiveData<Boolean>()
    var loadingPeople = MutableLiveData<Boolean>()

    private var following = false

    var uid = MutableLiveData<String>("") //User Uid

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
                    this.uid.value = uid
                    header.value = user?.uHeadPortrait
                    nickName.value = user?.uName
                    signature.value = user?.uSignature

                    initFollowState()
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

    fun initFollowState(){
        if (!UserHelper.isLogin()){
            return
        }
        launch ({
            val followBean = remote.isFollowUser(UserHelper.getUser()?.uId?:"",uid.value?:"")
            when(followBean.code){
                200 ->{
                    stateFollow.value = followBean.data
                }
                400 ->{
                    throw Exception(followBean.msg)
                }
            }
        },{
            toast.value = "获取关注信息失败："+it.message
        })
    }

    fun loadPeopleData(){
        if (loadingPeople.value == true) return
        launch ({
            val bean = remote.obtainUserTopic(0,uid.value?:"",nextPeoplePage)
            when(bean.code){
                200 ->{
                    nextPeoplePage += 1
                    if (bean.data != null){
                        peopleData.value = bean.data
                    }
                }
                300 ->{
                    if (nextPeoplePage > 1){
                        toast.value = "已经没有内容可以加载了"
                    }
                }
                400 ->{
                    toast.value = "加载错误："+ bean.msg
                }
            }
        },{
            toast.value = "加载错误："+it.message
        },{
            loadingPeople.value = false
            refreshingPeople.value = false
        })
    }

    fun loadBodyData(){
        if (loadingBody.value == true) return
        launch ({
            val bean = remote.obtainUserTopic(1,uid.value?:"",nextBodyPage)
            when(bean.code){
                200 ->{
                    nextBodyPage += 1
                    if (bean.data != null)
                        bodyData.value = bean.data
                }
                300 ->{
                    if (nextBodyPage > 1){
                        toast.value = "已经没有内容可以加载了"
                    }
                }
                400 ->{
                    toast.value = "加载错误："+ bean.msg
                }
            }
        },{
            toast.value = "加载错误："+it.message
        },{
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

    fun onFollow(){
        if (!UserHelper.isLogin()||following){
            return
        }
        following = true
        val follow = !(stateFollow.value?:false)
        stateFollow.value = follow
        launch({
            val followBean = remote.onFollowUser(UserHelper.getUser()?.uId?:"",uid.value?:"",follow)
            when(followBean.code){
                200 ->{

                }
                else ->{
                    throw Exception(followBean.msg)
                }
            }
        },{
            val msg = if (follow) "关注失败：" else "取消关注失败："
            stateFollow.value = !follow
            toast.value = msg + it.message
        },{
            following = false
        })
    }

    fun isMine(uid:String):Int{
        val myUid = UserHelper.getUser()?.uId?:"-1"
        return if (uid == myUid||!UserHelper.isLogin())
            View.GONE
        else
            View.VISIBLE
    }
}