package com.example.reunion.view_model

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.MessageBean
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.local_resource.AppDataBase
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.HomeRemoteModel
import com.example.reunion.repostory.remote_resource.ImRemoteModel
import com.example.reunion.repostory.server.WebSocketServer
import java.lang.Exception

class ImViewModel:BaseViewModel() {

    var imId:String = ""

    val editContent = MutableLiveData("")

    val hideInput = MutableLiveData<Boolean>()

    private val local = AppDataBase.instance.getImMessageDao()
    private val indexLocal = AppDataBase.instance.getIndexDao()

    private val remote = ImRemoteModel()

    private val homeRemote = HomeRemoteModel()

    val loading = MutableLiveData<Boolean>()

    val messages = MutableLiveData<List<ImMessageBean>>()

    val newMessage = MutableLiveData<ImMessageBean>() // 新增加的消息 //只用来通知即时通讯界面

    val newMessageArray = MutableLiveData<List<ImMessageBean>>() // 新增加的消息 //只用来通知即时通讯界面

    val updateMessage = MutableLiveData<ImMessageBean>() //修改消息 im_id 和 time

    private var isNoMessage = false

    private var start = 0

    private var end = 25

    val user = MutableLiveData<User.Data>()

    val toUser  = MutableLiveData<User.Data>()



    fun isCanSend(editContent:String?):Boolean{
        if (editContent == null) return false
        return editContent.isNotEmpty() && UserHelper.isLogin()
    }

    fun getSendBg(editContent:String?): Drawable {
        if (editContent == null)
            return MyApplication.resource().getDrawable(R.drawable.comment_no_send_bg)
        return if (editContent.isNotEmpty()&& UserHelper.isLogin()){
            MyApplication.resource().getDrawable(R.drawable.comment_send_bg)
        }else{
            MyApplication.resource().getDrawable(R.drawable.comment_no_send_bg)
        }
    }

    private fun upPage(){
        start = end
        end += 10
    }

    private suspend fun updateToUser(uid:String,header:String,nickName:String){
        val last = toUser.value
        if (last == null ||header != last.uHeadPortrait||nickName != last.uName){
            val user = User.Data()
            user.uId = uid
            user.uHeadPortrait = header
            user.uName = nickName
            toUser.value = user
            local.updateUserMessage(uid,user.uHeadPortrait,user.uName)
            indexLocal.updateMessage(imId,this.user.value?.uId?:"",user.uHeadPortrait,user.uName)
        }
        WebSocketServer.startIm(uid,imId)
    }

    private suspend fun updateToUser(user: User.Data){
        updateToUser(user.uId,user.uHeadPortrait,user.uName)
    }

    private suspend fun updateUser(){
        user.value = UserHelper.getUser()?:return
        local.updateUserMessage(user.value!!.uId,user.value?.uHeadPortrait?:"",user.value!!.uName)
    }

    fun initImId(fromUid:String,toUid:String){
        if (fromUid < toUid){
            imId = "${fromUid}${toUid}"
        }else{
            imId = "${toUid}${fromUid}"
        }
    }

    fun initData(toUid:String){//发送对象的uid
        var user:User.Data? = null
        launch ({
            val data = homeRemote.obtainSingleUserMessage(toUid) // 先从网络获取
            if (data.code == 200){
                user = data.data
            }else{
                throw Exception("obtain user from net error")
            }
        },{
            //没有网络就从本地获取
            user = User.Data()
            val firstMessage = local.obtainMyFirstMessage(toUid)
            if (firstMessage.isNotEmpty()){
                user!!.uName = firstMessage[0].nickName?:""
                user!!.uHeadPortrait = firstMessage[0].header?:""
            }else{
                val index = indexLocal.obtainMessage(imId,UserHelper.getUser()?.uId?:"")
                if (index.isNotEmpty()){
                    user!!.uName = index[0].nickName
                    user!!.uHeadPortrait = index[0].header
                }
            }
        }) {
            if (user == null)
                user = User.Data()
            user?.uId = toUid
            initData(user!!)
        }
    }

    fun initData(toUser:User.Data){ //发送对象的user
        launch {
            updateUser()
            updateToUser(toUser)
            obtainMessages(true)
        }
    }

    //从数据库获取聊天记录
    fun obtainMessages(isFirst:Boolean = false){
        if (!isFirst && isNoMessage){
            toast.value = "没有更多消息了"
        }
        if (loading.value == true){
            return
        }
        launch ({
            val msgData = local.obtainMessages(imId,user.value?.uId?:"",start,end)
            if (msgData.isEmpty()){
                isNoMessage = true
                if (!isFirst)
                    toast.value = "没有更多消息了"
            }else{
                upPage()
                messages.value = msgData
            }
        },{
            toast.value = it.message
        },{
            loading.value = false
        })
    }

    fun onSendMessage(view: View? = null){
        if (editContent.value == null || editContent.value == ""){
            return
        }
        launch {
            val newMsg = ImMessageBean().apply {
                content = editContent.value
                uId = user.value?.uId?:""
                toUid = toUser.value?.uId?:""
                time = System.currentTimeMillis()
                nickName = user.value?.uName
                header = user.value?.uHeadPortrait
                imId = this@ImViewModel.imId
                targetUid = uId
            }
            //先存数据库
            editContent.value = ""
            indexLocal.insertIndex(ImMessageIndex(newMsg.imId?:"",
                newMsg.targetUid?:"",
                newMsg.toUid?:"",newMsg.time?:0,
                toUser.value?.uHeadPortrait?:"",toUser.value?.uName?:""))
            local.insertMessage(newMsg)
            newMessage.value = newMsg

            launch ({
                //进行网络逻辑 发送
                val bean = MessageBean()
                bean.type = 1
                bean.imMessage = newMsg
                val result = WebSocketServer.onSendMessage(bean)
                if (!result)
                    throw Exception("send message fail")
            },{//发送失败
                toast.value = "发送失败:"+it.message
                newMsg.sendSuccess = false
                updateMessage.value = newMsg
                local.setMessageSendFail(newMsg.imId!!,user.value?.uId?:"",newMsg.time?:0L)
            })
            //更新消息列表
            MyApplication.app.sendBroadcast(Intent("reunion.im.update.message"))
        }
    }

    fun retrySend(newMsg:ImMessageBean){
        launch {
            //先更新数据库
            editContent.value = ""
            newMsg.sendSuccess = true
            updateMessage.value = newMsg
            launch ({
                //进行网络逻辑 发送
                val bean = MessageBean()
                bean.type = 1
                bean.imMessage = newMsg
                val result = WebSocketServer.onSendMessage(bean)
                if (!result)
                    throw Exception("send message fail")
                local.setMessageSendSuccess(newMsg.imId?:"",newMsg.targetUid?:"",newMsg.time?:0)
            },{//发送失败
                toast.value = "发送失败:"+it.message
                newMsg.sendSuccess = false
                updateMessage.value = newMsg
                local.setMessageSendFail(newMsg.imId!!,user.value?.uId?:"",newMsg.time?:0L)
            })
        }
    }

}