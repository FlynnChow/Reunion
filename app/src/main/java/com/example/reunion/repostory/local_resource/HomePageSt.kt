package com.example.reunion.repostory.local_resource

import android.util.Log
import com.example.reunion.MyApplication

class HomePageSt {
    companion object {
        val instance: HomePageSt by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { HomePageSt() }
    }

    private var recommendStatus: Boolean = true
    private var nearbyStatus: Boolean = true
    private var newsStatus: Boolean = true
    private var followStatus: Boolean = true

    init {
        val userPre = MyApplication.app.getSharedPreferences("homePageSetting", 0)
        recommendStatus = userPre.getBoolean("recommend", true)
        nearbyStatus = userPre.getBoolean("nearby", true)
        newsStatus = userPre.getBoolean("news", true)
        followStatus = userPre.getBoolean("follow", true)
    }

    fun save(recommend: Boolean, nearby: Boolean, news: Boolean,follow:Boolean) {
        recommendStatus = recommend
        nearbyStatus = nearby
        newsStatus = news
        followStatus = follow
        val userPre = MyApplication.app.getSharedPreferences("homePageSetting",0)
        val editor = userPre.edit()
        editor.putBoolean("recommend",recommend)
        editor.putBoolean("nearby",nearby)
        editor.putBoolean("news",news)
        editor.putBoolean("follow",followStatus)
        editor.apply()
    }

    fun getRecommendStatus() = recommendStatus

    fun getNearbyStatus() = nearbyStatus

    fun getNewsStatus() = newsStatus

    fun getFollowStatus() = followStatus

}