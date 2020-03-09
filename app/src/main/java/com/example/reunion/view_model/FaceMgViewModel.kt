package com.example.reunion.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.FaceRemoteModel
import com.google.gson.Gson
import kotlinx.coroutines.delay
import okhttp3.MultipartBody

class FaceMgViewModel:BaseViewModel() {
    val remote by lazy { FaceRemoteModel() }

    val isShowDelete = MutableLiveData(false)

    val isAllSelect = MutableLiveData(false)

    val isShowDialog = MutableLiveData(false)

    val deleteCount = MutableLiveData(0)

    val isShowLoading = MutableLiveData(false)

    val deleteSuccess = MutableLiveData<Boolean>()

    val faceList = MutableLiveData<ArrayList<FaceBean>>()

    val newFace = MutableLiveData<FaceBean>()

    fun getDeleteButtonText(delete:Boolean) =
        if (delete)
            MyApplication.resource().getString(R.string.face_delete_cancel)
        else
            MyApplication.resource().getString(R.string.face_delete_button)

    fun getAllSelectText(all:Boolean) =
        if (all)
            MyApplication.resource().getString(R.string.face_delete_all_cancel)
        else
            MyApplication.resource().getString(R.string.face_delete_all)

    fun deleteFace(items:ArrayList<FaceBean>){
        launch ({
            isShowLoading.value = true
            delay(500)
            val faceList = ArrayList<String>()
            for (item in items){
                faceList.add(item.faceId.toString())
            }
            val body = MultipartBody.Builder()
                .addFormDataPart("uId", UserHelper.getUser()?.uId?:"")
                .addFormDataPart("faceIdListJson",Gson().toJson(faceList))
                .build()
            val bean = remote.deleteFace(body)
            when(bean.code){
                200 ->{
                    deleteSuccess.value = true
                    toast.value = "删除成功"
                }
                else ->{
                    deleteSuccess.value = false
                    toast.value = "删除失败"
                }
            }
            isShowLoading.value = false
        },{
            isShowLoading.value = false
            deleteSuccess.value = false
            toast.value = it.message
        })
    }

    fun initFaceList(){
        launch ({
            val data = remote.getFaceList()
            when(data.code){
                200 ->{
                    faceList.value = data.data
                    Log.d("测试,"," ${data.data?.size}")
                }
                else->{
                    toast.value = "获取面部信息失败："+data.msg
                }
            }
        },{
            toast.value = "错误："+it.message
        })
    }
}