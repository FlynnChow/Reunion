package com.example.reunion.repostory.remote_resource

import com.example.reunion.base.BaseRemoteResource
import okhttp3.RequestBody


class FaceRemoteModel:BaseRemoteResource() {

    suspend fun createNewFace(body: RequestBody) = getFaceRemote().createNewFace(body).await()

    suspend fun deleteFace(body: RequestBody) = getFaceRemote().deleteFace(body).await()

    suspend fun getFaceList() = getFaceRemote().getFaceList().await()

    suspend fun researchFace(groupId: String) = getFaceRemote().researchFace(groupId).await()
}