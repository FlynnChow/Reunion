package com.example.reunion.util

import android.annotation.SuppressLint
import com.example.reunion.MyApplication
import com.example.reunion.R
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

object Util {

    @SuppressLint("DefaultLocale")
    fun formatUrlParam(sourceMap:HashMap<String,String>, encode:String, isLower:Boolean):String{


        var params = ""
        val map = sourceMap
        val items = ArrayList<Map.Entry<String,String>>(map.entries)
        Collections.sort(items,object : Comparator<Map.Entry<String, String>> {
            override fun compare(
                o1: Map.Entry<String, String>?,
                o2: Map.Entry<String, String>?
            ): Int {
                return (o1?.key.toString().compareTo(o2?.key.toString()))
            }
        })

        val sb = StringBuilder()
        for (item in items){
            val key = item.key
            var value = item.value
            value = URLEncoder.encode(value,encode)
            if (isLower) {
                sb.append(key.toLowerCase().toString() + "=" + value)
            } else {
                sb.append("$key=$value")
            }
            sb.append("&")
        }
        params = sb.toString()
        if (!params.isEmpty()) {
            params = params.substring(0, params.length - 1)
        }
        return params
    }

    fun getSign(sourceMap:HashMap<String,String>,appkey:String = "ptWTkIRx0yPw3J5A"):String{
        var sign = formatUrlParam(sourceMap,"utf-8",true)
        sign = "${sign}&app_key=${appkey}"
        return StringDealerUtil.getStringToMD5(sign)
    }
}