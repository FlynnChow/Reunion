package com.example.reunion.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.TopicKeyword
import com.example.reunion.repostory.local_resource.AppDataBase

class SearchUserViewModel:BaseViewModel() {

    var keyword = MutableLiveData<String>()

    val loading = MutableLiveData<Boolean>()

    fun onLoading(){
        if (loading.value == true){
            return
        }
        loading.value = true
        launch ({

        },{
            toast.value = it.message
        },{
            loading.value = false
        })
    }

    fun onSearch(){
        if (keyword.value != null && keyword.value!!.isNotEmpty()){

        }else{
            toast.value = "关键词不能为空"
        }
    }

}