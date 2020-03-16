package com.example.reunion.repostory.local_resource

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.reunion.repostory.server.WebSocketServer

class ClearReceiver(private val listener:(Int)->Unit):BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            "reunion.message.im.clear" ->{
                listener.invoke(0)
            }
            "reunion.message.sys.clear" ->{
                listener.invoke(1)
            }
        }
    }
}