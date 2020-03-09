package com.example.reunion

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.content.res.Resources
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.lljjcoder.style.citylist.utils.CityListLoader


class MyApplication:Application(),CameraXConfig.Provider {
    override fun onCreate() {
        super.onCreate()
        CityListLoader.getInstance().loadCityData(this);
        app = this
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
}