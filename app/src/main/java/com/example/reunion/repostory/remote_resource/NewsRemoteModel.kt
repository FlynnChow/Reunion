package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource

class NewsRemoteModel:BaseRemoteResource() {
    suspend fun getHealthyNews(start:Int) = getNewsRemote().getNews("健康",start).await()

    suspend fun getChildNews(start:Int) = getNewsRemote().getNews("育儿",start).await()

    suspend fun getPublicNews() = getNewsRemote().getPublicNews("公益").await()
}