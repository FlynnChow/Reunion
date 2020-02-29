package com.example.reunion.view_model

import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.local_resource.PictureHelper

class
FindOneViewModel:BaseViewModel() {
    var path = ""
    var group = ""
    val loadText = MutableLiveData("")
    val uploading = MutableLiveData(false)

    fun initFindOne(){
        if (path.isNotEmpty()){
            loadText.value = MyApplication.resource().getString(R.string.picture_uploading)
        }else{
            uploading.value = false
        }
    }

    fun pathEmptyError(){
        uploading.value = false
    }
}