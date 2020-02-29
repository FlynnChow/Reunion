package com.example.reunion

import android.app.Application
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraXConfig
import com.google.firebase.FirebaseApp
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
        fun resource() = app.resources
    }

    override fun getCameraXConfig(): CameraXConfig {
        return Camera2Config.defaultConfig()
    }
}