package com.example.reunion.util

import android.app.Notification
import android.app.Notification.VISIBILITY_SECRET
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.reunion.MyApplication
import com.example.reunion.R

object NotificationUtil {

    const val PROGRESS_CHANEL_ID = "progress_id"
    const val MESSAGE_CHANEL_ID = "message_id"

    fun getNotificationBuilder(title:String,content:String,chanelId:String):Notification.Builder{
        val builder = Notification.Builder(MyApplication.app)
            .setAutoCancel(true)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(R.drawable.temp_icon)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val chanel = NotificationChannel(chanelId,MyApplication.app.packageName,
                NotificationManager.IMPORTANCE_HIGH).apply {
                canBypassDnd()//绕过请勿打扰
                lockscreenVisibility = VISIBILITY_SECRET//锁屏显示通知
                enableVibration(false) //是否震动
            }
            MyApplication.manager.createNotificationChannel(chanel)
            return builder.setChannelId(chanelId)
        }
        return builder
    }

    fun getProgressNotification(title: String,content: String,progress:Int):Notification{
        val builder = getNotificationBuilder(title,content, PROGRESS_CHANEL_ID)
        builder.setOnlyAlertOnce(true)
        builder.setProgress(100, progress, false)
        builder.setWhen(System.currentTimeMillis())
        return builder.build()
    }

}