package com.example.reunion.repostory.remote_resource

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.example.reunion.MyApplication
import com.example.reunion.customize.UploadRequestBody
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.util.NotificationUtil
import com.example.reunion.view.TopicActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.File

class UploadServer:Service() {
    companion object{
        const val UPLOADING = 1
        const val UPLOAD_SUCCESS = 2
        const val UPLOAD_FAILD = 3
        const val SENDTOPIC_CHANNEL_ID = 1
        const val SENDTOPIC_MANAGER_ID = 1
        const val SENDTOPIC_FOGROUND_ID = 1
    }
    private var progressListener:((Int,Int,TopicBean?)->Unit)? = null
    private var isShowNotification = false
    private var totalProgress = 0f
    private val remote = TopicRemoteModel()
    private var title = "发布话题"
    private var data:TopicBean? = null

    private val mBinder = UpLoadBinder()

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()
    }

    fun startUploading(paths:ArrayList<String>,
                           requestBuilder: MultipartBody.Builder,
                           uploadListener:((Int,Int,TopicBean?)->Unit)){

        this.progressListener = uploadListener

        val size = paths.size // 要上传的文件数量
        val partNum = 100f / size //每一个文件占比
        for (index in 0 until size){
            val file = File(paths[index])
            val uploadBody = UploadRequestBody.getRequestBody(file,"image${index}"){
                Log.d("测试进度",""+(partNum / 100f).toString())
                totalProgress += it * partNum / 100f
                uploadListener.invoke(totalProgress.toInt(),UPLOADING,null)
                if (isShowNotification){
                    MyApplication.manager.notify(
                        SENDTOPIC_MANAGER_ID,
                        NotificationUtil.getProgressNotification(
                            title,"正在上传图片中",totalProgress.toInt()))
                }else{
                    uploadListener.invoke(totalProgress.toInt(),UPLOADING,null)
                }
            }
            requestBuilder.addFormDataPart("file",file.name,uploadBody)
        }
        val requestBody = requestBuilder.build()
        GlobalScope.launch {
            try {
                val bean = remote.sendTopic(requestBody)

                stopForeground(true)
                when(bean.code){
                    200 ->{
                        data = bean.data
                        if (isShowNotification){
                            val notification =
                                createMessageNotification(true,
                                    "${title}成功",
                                    "点击查看你新发布的话题",
                                    bean.data)
                            MyApplication.manager.notify(SENDTOPIC_MANAGER_ID,notification)
                        }else{
                            uploadListener.invoke(100,UPLOAD_SUCCESS,bean.data)
                        }
                        stopSelf()
                    }else ->{
                        if (isShowNotification){
                            val notification =
                            createMessageNotification(true,
                                "上传失败",
                                "发生异常："+bean.msg,
                                null)
                            MyApplication.manager.notify(SENDTOPIC_MANAGER_ID,notification)
                        }else{
                            Log.e("发布话题失败",bean.code.toString()+"  "+bean.msg.toString())
                            uploadListener.invoke(0, UPLOAD_FAILD,null)
                        }
                    }
                }
            }catch (e:Exception){
                stopForeground(true)
                if (isShowNotification){
                    val notification =
                        createMessageNotification(true,
                            "上传失败",
                            "发生错误：${e.message}",
                            null)
                    MyApplication.manager.notify(SENDTOPIC_MANAGER_ID,notification)
                }else{
                    Log.e("发生错误",e.message.toString())
                    uploadListener.invoke(0, UPLOAD_FAILD,null)
                }
            }
        }
    }

    private fun createMessageNotification(success:Boolean,title:String,content:String,data: TopicBean?): Notification {
        val builder =
            NotificationUtil.getNotificationBuilder(title,content, NotificationUtil.MESSAGE_CHANEL_ID)

        builder.setAutoCancel(true)
        if (success&&data!=null){
            val intent = Intent(this,TopicActivity::class.java)
            intent.putExtra("data",data)
            val pi = PendingIntent.getActivity(this,0,intent,0)
            builder.setContentIntent(pi)
        }
        return builder.build()
    }


    inner class UpLoadBinder: Binder() {
        fun startUploadPicture(paths:ArrayList<String>,
                               requestBuilder: MultipartBody.Builder,
                               uploadListener:((Int,Int,TopicBean?)->Unit)){
            startUploading(paths,requestBuilder,uploadListener)
        }

        fun switchBackground(){
            isShowNotification = true
            if (totalProgress == 100f){
                progressListener?.invoke(100,UPLOAD_SUCCESS,data)
            }else{
                startForeground(
                    SENDTOPIC_FOGROUND_ID,
                    NotificationUtil.getProgressNotification(
                        title,"正在上传图片中，请稍等",totalProgress.toInt()))
            }
        }

        fun setTitle(newTitle:String){
            title = newTitle
        }

        fun getCurrentProgress() = totalProgress
    }
}