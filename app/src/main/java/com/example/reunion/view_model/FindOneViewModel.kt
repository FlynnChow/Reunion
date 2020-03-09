package com.example.reunion.view_model

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.customize.UploadRequestBody
import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.FaceRemoteModel
import okhttp3.MultipartBody
import java.io.File
import java.io.FileInputStream

class
FindOneViewModel:BaseViewModel() {
    private val remote = FaceRemoteModel()
    var path = ""
    val loadText = MutableLiveData("")
    val uploading = MutableLiveData(false)
    val faceList = MutableLiveData<ArrayList<FaceBean>>()

    fun initFindOne(){
        if (path.isNotEmpty()){
            loadText.value = MyApplication.resource().getString(R.string.picture_uploading)
            launch ({
                val file = File(path)
                val uploadBody = UploadRequestBody.getRequestBody(file,"image")
                val body = MultipartBody.Builder()
                    .addFormDataPart("file",file.name,uploadBody)
                    .build()
                val data = remote.searchFace(body)
                when(data.code){
                    200 ->{
                        faceList.value = data.data
                    }else->{
                        toast.value = "错误：" + data.code
                    }
                }
                uploading.value = false
            },{
                Log.d("测试",it.message)
                toast.value = "错误："+it.message
            })
        }else{
            uploading.value = false
        }
    }

    fun pathEmptyError(){
        uploading.value = false
    }
}