package com.example.reunion.view_model

import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.customize.UploadRequestBody
import com.example.reunion.repostory.bean.CommunityBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.CommunityRemote
import okhttp3.MultipartBody
import java.io.File
import java.lang.Exception

class CommunitySendViewModel:BaseViewModel() {
    private val remote = CommunityRemote()

    val contentEdit = MutableLiveData<String>()

    val locateMessage = MutableLiveData<String>(null)

    val picturePaths = MutableLiveData<ArrayList<String>>()

    val showDialog = MutableLiveData<Boolean>()

    val onCompress = MutableLiveData<Boolean>()

    val responseData = MutableLiveData<CommunityBean>()

    fun sendToCommunity(view:View){
        if (!UserHelper.isLogin()){
            toast.value = "需要登录才可以发布"
            return
        }
        if (contentEdit.value == null||contentEdit.value!!.isEmpty()){
            toast.value = "未填写内容"
            return
        }
        onCompress.value = true
        showDialog.value = true
    }

    fun onStartSend(){
        launch({
            val bodyBuilder = MultipartBody.Builder()
                .addFormDataPart("uId",UserHelper.getUser()?.uId?:"")
                .addFormDataPart("content", contentEdit.value.toString())
            if (locateMessage.value != null)
                bodyBuilder.addFormDataPart("locate",locateMessage.value.toString())
            val pictures = picturePaths.value
            if (pictures != null){
                for (index in pictures.indices){
                    val file = File(pictures[index])
                    val uploadBody = UploadRequestBody.getRequestBody(file,"image")
                    bodyBuilder.addFormDataPart("file${index}",file.name,uploadBody)
                }
            }
            val bean = remote.sendCommunityMain(bodyBuilder.build())
            when(bean.code){
                200 ->{
                    responseData.value = bean.data
                }
                else ->{
                    throw Exception(bean.msg)
                }
            }
        },{
            toast.value = "发布失败: "+it.message
        },{
            showDialog.value = false
        })

    }

    fun getLocateString(locate:String?) = locate?:"未获取到位置信息"

}