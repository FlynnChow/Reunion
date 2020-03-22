package com.example.reunion.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.SystemMessageBean
import com.example.reunion.repostory.local_resource.AppDataBase
import com.example.reunion.repostory.local_resource.UserHelper
import java.lang.Exception

class MessageViewModel:BaseViewModel() {


    private val sysLocal = AppDataBase.instance.getSysMessageDao()
    private val indexLocal = AppDataBase.instance.getIndexDao()
    private val imLocal = AppDataBase.instance.getImMessageDao()

    var deleteIndex :ImMessageIndex? = null

    var updateMessage = MutableLiveData<Int>()

    val imMessages = MutableLiveData<List<ImMessageIndex>>()

    val imMessage = MutableLiveData<ImMessageIndex>()

    val imUpdateMessage = MutableLiveData<ImMessageIndex>()

    val imDeleteMessage = MutableLiveData<ImMessageIndex>()

    val imRefresh = MutableLiveData<Boolean>()

    fun onRefreshMessageIndex(){
        if (imRefresh.value == true) return
        imRefresh.value = true
        launch ({
            if (!UserHelper.isLogin()) throw Exception("user no login")
            val uid = UserHelper.getUser()?.uId?:""
            val indexArray = indexLocal.obtainMessages(uid)
            for (index in indexArray){
                val count = imLocal.obtainNotReadNum(index.tableId,index.uid,index.toUid) //获取未读数
                val content = imLocal.obtainMyFirstContent(index.tableId,uid) //获取内容
                val firstMsg = imLocal.obtainMyFirstMessage(index.toUid) //获取最新对方读信息
                if (firstMsg.isNotEmpty()){
                    index.initData(content,firstMsg[0].nickName?:"",count,firstMsg[0].header?:"")
                }else{
                    index.initData(content,count)
                }
            }
            imMessages.value = indexArray
        },{
        },{
            imRefresh.value = false
        })
    }

    fun setAlreadyRead(index:ImMessageIndex){
        if (index.num == 0) return
        index.num = 0
        launch {
            imLocal.setRead(index.tableId,index.uid,index.toUid)
            imUpdateMessage.value = index
        }
    }

    fun deleteIndex(){
        launch {
            if (deleteIndex==null) return@launch
            imDeleteMessage.value = deleteIndex
            indexLocal.deleteIndex(deleteIndex!!.tableId,UserHelper.getUser()?.uId?:"")
        }
    }



    val sysRefresh = MutableLiveData<Boolean>()
    val sysMessages = MutableLiveData<List<SystemMessageBean>>()
    var deleteSystemMessage :SystemMessageBean? = null
    val deleteMessage = MutableLiveData<SystemMessageBean?>()

    fun onRefreshSystemMessage(){
        if (sysRefresh.value == true) return
        sysRefresh.value = true
        launch ({
            if (!UserHelper.isLogin()) throw Exception("user no login")
            val uid = UserHelper.getUser()?.uId?:""
            val sysArray = sysLocal.obtainMessages(uid)
            sysMessages.value = sysArray
        },{
        },{
            sysRefresh.value = false
        })
    }

    fun deleteSystemMessage(){
        launch {
            if (deleteSystemMessage==null) return@launch
            deleteMessage.value = deleteSystemMessage
            sysLocal.deleteIndex(deleteSystemMessage?.tableId?:"",deleteSystemMessage?.uid?:"")
        }
    }

    fun updateIsRead(id:String,read:Boolean){
        if (!read){
            launch {
                sysLocal.updateSysMessageClickable(id,UserHelper.getUser()?.uId?:"")
            }
        }
    }

}