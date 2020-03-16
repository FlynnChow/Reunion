package com.example.reunion

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.example.reunion.repostory.local_resource.WebSocketReceiver
import com.lljjcoder.style.citylist.utils.CityListLoader


class MyApplication:Application(),CameraXConfig.Provider {
    override fun onCreate() {
        super.onCreate()
        CityListLoader.getInstance().loadCityData(this);
        app = this

        registerSocketReceiver()
        sendBroadcast(Intent("reunion.application.launch"))
    }


    companion object{
        @JvmStatic
        lateinit var app:Application

        @JvmStatic
        fun resource(): Resources = app.resources

        val manager by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            app.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }

    private fun registerSocketReceiver(){
        val mReceiver = WebSocketReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.SCREEN_ON")
        intentFilter.addAction("android.intent.action.SCREEN_OFF")
        intentFilter.addAction("reunion.application.launch")
        intentFilter.addAction("reunion.application.intoForeground")
        registerReceiver(mReceiver,intentFilter)
    }
}