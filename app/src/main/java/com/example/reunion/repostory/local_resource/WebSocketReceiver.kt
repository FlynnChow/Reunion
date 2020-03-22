package com.example.reunion.repostory.local_resource

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.reunion.repostory.server.WebSocketServer

class WebSocketReceiver:BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        when(intent?.action){
            "android.intent.action.SCREEN_ON" ->{
                context?.startService(Intent(context, WebSocketServer::class.java))
            }
            "android.intent.action.SCREEN_OFF" ->{
                context?.stopService(Intent(context, WebSocketServer::class.java))
            }
            "reunion.application.intoForeground" ->{
                if (!WebSocketServer.isLife){
                    context?.startService(Intent(context, WebSocketServer::class.java))
                }
            }
        }
    }
}