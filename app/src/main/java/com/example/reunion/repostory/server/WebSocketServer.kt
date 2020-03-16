package com.example.reunion.repostory.server

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.base.BaseRemoteResource
import com.example.reunion.repostory.bean.ImMessageBean
import com.example.reunion.repostory.bean.ImMessageIndex
import com.example.reunion.repostory.bean.MessageBean
import com.example.reunion.repostory.bean.SystemMessageBean
import com.example.reunion.repostory.local_resource.AppDataBase
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.util.NotificationUtil
import com.example.reunion.util.StringDealerUtil
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import java.lang.Exception

class WebSocketServer : Service() {
    companion object{
        @JvmStatic private var webSocket:WebSocket? = null
        @JvmStatic private var uid:String = ""
        @JvmStatic private var toUid:String = ""
        @JvmStatic private var imid:String = ""

        @JvmStatic  var imMessage = MutableLiveData<ImMessageBean?>() //即时通讯 消息
        @JvmStatic  var imMessageArray = MutableLiveData<ArrayList<ImMessageBean>?>() //即时通讯 消息
        @JvmStatic  var imIndex = MutableLiveData<ImMessageIndex?>()
        @JvmStatic  var imIndexArray = MutableLiveData<ArrayList<ImMessageIndex>?>()

        @JvmStatic  var systemMessage = MutableLiveData<SystemMessageBean?>()
        @JvmStatic  var systemMessageArray = MutableLiveData<ArrayList<SystemMessageBean>?>()

        @JvmStatic  var isLife = false //server 是否存活


        @JvmStatic fun setUid(uid:String){
            Companion.uid = uid
        }

        @JvmStatic fun exitIm(){
            Companion.toUid = ""
            Companion.imid = ""
            imMessage.value = null
            imMessageArray.value = null
            imIndex.value = null
            imIndexArray.value = null
        }

        @JvmStatic fun startIm(toUid:String,imId:String){
            Companion.toUid = toUid
            Companion.imid = imId
        }

        @JvmStatic fun exitIndex(){
            imIndex.value = null
            imIndexArray.value = null
        }

        @JvmStatic fun exitSystem(){
            systemMessage.value = null
            systemMessageArray.value = null
        }

        fun onSendMessage(message:MessageBean):Boolean{
            if (uid.isEmpty()){
                throw Exception("uid is Empty Exception")
            }else if(toUid.isEmpty()){
                throw Exception("toUid is Empty Exception")
            }
            return webSocket?.send(Gson().toJson(message))?:false
        }

        fun onSendMessageNotify(message:MessageBean):Boolean{
            if (!UserHelper.isLogin()||UserHelper.getUser()?.uId == null){
                throw Exception("uid is Empty Exception")
            }
            return webSocket?.send(Gson().toJson(message))?:false
        }
    }
    private var daemonIsLife = true //守护线程是否存活

    private var daemonCycleTime = 5000L //守护线程检查周期

    private var isConnected = 0 //是否是链接状态 0:不是 1:正在链接 2:成功

    private lateinit var url:String

    private val imLocal = AppDataBase.instance.getImMessageDao()
    private val indexLocal = AppDataBase.instance.getIndexDao()
    private val sysLocal = AppDataBase.instance.getSysMessageDao()

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        url = "wss://reunion.yulinzero.xyz:443/websocket/" + StringDealerUtil.getDeviceId(this)
        isLife = true
        startDaemonTask()
        super.onCreate()
    }

    override fun onDestroy() {
        stopTask()
        webSocket?.send(Gson().toJson(MessageBean("heart@")))
        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun startDaemonTask(){
        daemonIsLife = true
        GlobalScope.launch(Dispatchers.Default) {
            while (daemonIsLife){
                if (isConnected !=2){
                    if (daemonCycleTime == 2500L)
                        daemonCycleTime = 5000L
                    startSocketWork()
                }else{
                    if (daemonCycleTime == 5000L)
                        daemonCycleTime = 2500L
                    webSocket?.send(Gson().toJson(MessageBean("heart@$uid")))
                }
                delay(daemonCycleTime)
            }
        }
    }

    private fun stopTask(){
        daemonIsLife = false
        isLife = false
    }

    private fun startSocketWork(){
        if (isConnected == 1||isConnected == 2){
            return
        }
        val client = BaseRemoteResource.client
        val request = Request.Builder().get().url(url).build()
        isConnected = 1
        webSocket = client.newWebSocket(request,object : WebSocketListener(){
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Companion.webSocket?.send(Gson().toJson(MessageBean("heart@$uid")))
                isConnected = 2
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                GlobalScope.launch(Dispatchers.Main) {
                    val bean:MessageBean = Gson().fromJson(text,MessageBean::class.java)
                    when(bean.type){
                        0 ->{

                        }
                        1 ->{
                            val im = bean.imMessage?:return@launch
                            isImMessage(im)
                        }
                        2 ->{
                            val ims = bean.imBeansArray?:return@launch
                            isImMessageArray(ims)
                        }
                        3 ->{
                            val sysMs = bean.systemMessage?:return@launch
                            isSysMessage(sysMs)
                        }
                        4 ->{
                            val sysMsArray = bean.systemMessageArray?:return@launch
                            isSysMessageArray(sysMsArray)
                        }
                    }
                    super.onMessage(webSocket, text)
                }
            }

            private suspend fun isImMessage(msg:ImMessageBean){
                if (msg.uId == toUid && toUid.isNotEmpty()){
                    msg.isRead = true
                    imMessage.value = msg
                }else{
                    if (UserHelper.isLogin() && UserHelper.getUser()?.uId == uid){
                        GlobalScope.launch {
                            val notify = NotificationUtil.
                            getImNotification(this@WebSocketServer,
                                msg.nickName?:"",
                                msg.header?:"",
                                msg.content?:"",
                                msg.time?:0L,
                                msg.toUid?:return@launch,
                                msg.uId?:return@launch,
                                UserHelper.getUser()?.uName?:"",
                                msg.nickName?:"",
                                UserHelper.getUser()?.uHeadPortrait?:"",
                                msg.header?:"")
                            MyApplication.manager.notify(ImNotificationService.IM_NOTIFICATION_ID,notify)
                        }
                    }
                }
                val index = ImMessageIndex(
                    msg.imId?:"",
                    msg.targetUid?:"",
                    msg.uId?:"",
                    msg.time?:0L,
                    msg.header?:"",
                    msg.nickName?:""
                ).apply {
                    initData(msg.content?:"",if (msg.isRead) 0 else 1)
                }
                imIndex.value = index


                GlobalScope.launch {
                    imLocal.insertMessage(msg)
                    indexLocal.insertIndex(index)
                }
            }

            fun isImMessageArray(msgArray:ArrayList<MessageBean.ImBeans>){
                val arrayMessage = ArrayList<ImMessageBean>()
                val arrayIndex = ArrayList<ImMessageIndex>()
                for (imBean in msgArray){
                    if (imid == imBean.imId){
                        imMessageArray.value = imBean.array
                        if (imBean.array != null){
                            for (message in imBean.array){
                                message.isRead = true
                            }
                        }
                    }
                    if (imBean.array != null&&imBean.array.size>0){
                        arrayMessage.addAll(imBean.array)
                        val index = ImMessageIndex(
                            imBean.imId?:"",
                            imBean.array[0].targetUid?:"",
                            imBean.array[0].uId?:"",
                            imBean.array[0].time?:0L,
                            imBean.array[0].header?:"",
                            imBean.array[0].nickName?:""
                        ).apply {
                            initData(imBean.array[imBean.array.size - 1].content?:"",if (imBean.array[0].isRead) 0 else 1)
                        }
                        arrayIndex.add(index)
                    }
                }

                imIndexArray.value = arrayIndex

                GlobalScope.launch {
                    imLocal.insertMessageArray(arrayMessage)
                    indexLocal.insertIndexArray(arrayIndex)
                }

            }

            fun isSysMessage(sysMs:SystemMessageBean){
                sysMs.setFormatContent()
                sysMs.tableId = if (sysMs.uid?:"" < sysMs.targetUid?:"") "${sysMs.uid}${sysMs.targetUid}" else "${sysMs.targetUid}${sysMs.uid}"
                systemMessage.value = sysMs
                GlobalScope.launch {
                    sysLocal.insertSysMessage(sysMs)
                }
            }

            fun isSysMessageArray(sysMsArray:ArrayList<SystemMessageBean>){
                for (item in sysMsArray){
                    item.tableId = if (item.uid?:"" < item.targetUid?:"") "${item.uid}${item.targetUid}@${item.type}" else "${item.targetUid}${item.uid}@${item.type}"
                    item.setFormatContent()
                }
                systemMessageArray.value = sysMsArray
                GlobalScope.launch {
                    sysLocal.insertSysMessageArray(sysMsArray)
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                isConnected = 0
                super.onFailure(webSocket, t, response)
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = 0
                super.onClosed(webSocket, code, reason)
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                isConnected = 0
                super.onClosing(webSocket, code, reason)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
            }
        })
    }
}
