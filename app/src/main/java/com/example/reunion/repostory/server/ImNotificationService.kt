package com.example.reunion.repostory.server

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.core.app.RemoteInput
import com.example.reunion.MyApplication
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.MessageBean
import com.example.reunion.repostory.local_resource.AppDataBase
import com.example.reunion.util.NotificationUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ImNotificationService : Service() {
    companion object{
        const val IM_NOTIFICATION_ID = 2
    }
    override fun onBind(intent: Intent): IBinder? {

        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val result: Bundle = RemoteInput.getResultsFromIntent(intent)

        val mUid = intent?.getStringExtra("uid")
        val mToUid = intent?.getStringExtra("toUid")
        val mName = intent?.getStringExtra("name")
        val mToName = intent?.getStringExtra("toName")
        val mHeader = intent?.getStringExtra("header")
        val mToHeader = intent?.getStringExtra("toHeader")
        val mImId = if (mUid?:"" < mToUid?:"") "${mUid}${mToUid}" else "${mToUid}${mUid}"
        val mContent = result.getString("imMessage")
        val mTime = System.currentTimeMillis()
        GlobalScope.launch {
            try {
                //先更新数据库
                val newMsg = ImMessageBean().apply {
                    content = mContent
                    uId = mUid?:throw java.lang.Exception("uid is null")
                    toUid = mToUid?:throw java.lang.Exception("toUid is null")
                    time = mTime
                    nickName = mName?:""
                    header = mHeader?:""
                    imId = mImId
                    targetUid = uId
                }

                AppDataBase.instance.getIndexDao().insertIndex(ImMessageIndex(newMsg.imId?:"",
                    newMsg.targetUid?:"",
                    newMsg.toUid?:"",newMsg.time?:0,
                    mToHeader?:"",
                    mToName?:""))
                AppDataBase.instance.getImMessageDao().insertMessage(newMsg)

                //进行网络逻辑 发送
                val bean = MessageBean()
                bean.type = 1
                bean.imMessage = newMsg
                val senResult = WebSocketServer.onSendMessageNotify(bean)
                if (!senResult)
                    throw Exception("send message fail")
                AppDataBase.instance.getImMessageDao().setMessageSendSuccess(newMsg.imId?:"",newMsg.targetUid?:"",newMsg.time?:0)
                val notify = NotificationUtil.getImResultNotification(this@ImNotificationService,mToUid?:return@launch,true)
                MyApplication.manager.notify(IM_NOTIFICATION_ID,notify)
                delay(2000)
                MyApplication.manager.cancel(IM_NOTIFICATION_ID)
            }catch (e:java.lang.Exception){
                AppDataBase.instance.getImMessageDao().setMessageSendFail(mImId,mUid?:"",mTime)
                val notify = NotificationUtil.getImResultNotification(this@ImNotificationService,mToUid?:return@launch,true)
                MyApplication.manager.notify(IM_NOTIFICATION_ID,notify)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }
}
