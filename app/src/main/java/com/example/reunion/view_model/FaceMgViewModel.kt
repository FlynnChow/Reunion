package com.example.reunion.view_model

import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.remote_resource.FaceRemoteModel
import kotlinx.coroutines.delay

class FaceMgViewModel:BaseViewModel() {
    val remote by lazy { FaceRemoteModel() }

    val isShowDelete = MutableLiveData(false)

    val isAllSelect = MutableLiveData(false)

    val isShowDialog = MutableLiveData(false)

    val deleteCount = MutableLiveData(0)

    val isShowLoading = MutableLiveData(false)

    val deleteSuccess = MutableLiveData<Boolean>()

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

    fun deleteFace(item:ArrayList<FaceBean>){
        launch ({
            isShowLoading.value = true
            delay(4000)
            isShowLoading.value = false
            deleteSuccess.value = true
        },{
            isShowLoading.value = false
            toast.value = it.message
        })
    }
}