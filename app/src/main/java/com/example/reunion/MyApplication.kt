package com.example.reunion

import android.app.Application

class MyApplication:Application() {
    override fun onCreate() {
        super.onCreate()
        app = this
    }
    companion object{
        @JvmStatic
        lateinit var app:Application

        @JvmStatic
        fun resource() = app.resources
    }
}