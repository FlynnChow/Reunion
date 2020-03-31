@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.example.reunion.util

import android.app.Notification
import android.app.Notification.VISIBILITY_SECRET
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.bumptech.glide.Glide
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.repostory.server.ImNotificationService
import com.example.reunion.view.ImActivity
import com.example.reunion.view.TopicActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat


object NotificationUtil {

    const val PROGRESS_CHANEL_ID = "progress_id"
    const val MESSAGE_CHANEL_ID = "message_id"

    const val IM_MESSAGE_CHANEL_ID = "im_message_id"

    fun getNotificationBuilder(title:String?,content:String,chanelId:String,import:Int? = null):NotificationCompat.Builder{
        val builder = NotificationCompat.Builder(MyApplication.app)
            .setAutoCancel(true)
            .setContentText(content)
            .setSmallIcon(R.mipmap.logo_r)
        if (title != null)
            builder.setContentTitle(title)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val chanel = NotificationChannel(chanelId,MyApplication.app.packageName,
                import ?: NotificationManager.IMPORTANCE_HIGH
            ).apply {
                canBypassDnd()//绕过请勿打扰
                lockscreenVisibility = VISIBILITY_SECRET//锁屏显示通知
                enableVibration(false) //是否震动
                enableLights(true)
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

    suspend fun getImNotification(context:Context,name:String,url: String,content:String,time:Long,
                                  uid:String,toUid:String,nickName:String,toNickName:String,header:String,toHeader:String):Notification{
        var input:Bitmap = try {
            Glide.with(context).asBitmap()
                .load(url)
                .centerCrop()
                .submit(500, 500)
                .get()
        } catch (e: Exception) {
            BitmapFactory.decodeResource(context.resources, R.drawable.user)
        }

        val bitmap = Bitmap.createBitmap(input.width, input.height, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(bitmap)
        val paint = Paint()
        val rect = Rect(0, 0, input.width, input.height)
        val rectF = RectF(rect)
        paint.isAntiAlias = true;
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawOval(rectF,paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(input, rect, rect, paint);

        return getImNotification(context,name,bitmap,content,time, uid, toUid,nickName,toNickName,header,toHeader)
    }

    fun getImNotification(context:Context,name:String,bitmap: Bitmap,content:String,time:Long,
                          uid:String,toUid:String,nickName:String,toNickName:String,header:String,toHeader:String):Notification{
        val builder:NotificationCompat.Builder

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            val remoteInput = RemoteInput.Builder("imMessage").setLabel("回复消息").build()
            val intent = Intent(context,ImNotificationService::class.java)
            intent.putExtra("uid",uid)
            intent.putExtra("toUid",toUid)
            intent.putExtra("header",header)
            intent.putExtra("toHeader",toHeader)
            intent.putExtra("name",nickName)
            intent.putExtra("toName",toNickName)
            val pendingIntent =
                PendingIntent.getService(context,1,
                    intent,PendingIntent.FLAG_CANCEL_CURRENT)
            val action = NotificationCompat.Action.Builder(R.drawable.common_google_signin_btn_icon_dark,"回复消息",pendingIntent)
                .addRemoteInput(remoteInput).build()

            builder = getNotificationBuilder(name,content, IM_MESSAGE_CHANEL_ID,NotificationManager.IMPORTANCE_MAX)
                .setLargeIcon(bitmap)
                .setAutoCancel(true)
                .setWhen(time)
                .setCategory(Notification.CATEGORY_MESSAGE)
                .addAction(action)
        }else{
            builder = getNotificationBuilder(name,content, IM_MESSAGE_CHANEL_ID)
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(Notification.PRIORITY_MAX)
        }
        val intent = Intent(context, ImActivity::class.java)
        intent.putExtra("uid",toUid)
        val pi = PendingIntent.getActivity(context,0,intent,0)
        builder.setContentIntent(pi)
        return builder.build()
    }

    fun getImResultNotification(context: Context,toUid:String,result:Boolean):Notification{
        val builder = getNotificationBuilder(if (result) "回复成功" else "回复失败",if (result) "" else "消息回复失败，点击查看详细或重试。", PROGRESS_CHANEL_ID)
        val intent = Intent(context, ImActivity::class.java)
        intent.putExtra("uid",toUid)
        val pi = PendingIntent.getActivity(context,0,intent,0)
        builder.setContentIntent(pi)
        return builder.build()
    }

    fun getDayTime(time: Long):Long{
        val format = SimpleDateFormat("yyyy-MM-dd")
        val formatTime = format.format(time)
        return format.parse(formatTime).time
    }

}