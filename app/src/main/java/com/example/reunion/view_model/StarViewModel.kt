package com.example.reunion.view_model

import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel

class StarViewModel:BaseViewModel() {

    val refreshing = MutableLiveData<Boolean>()

    val loading = MutableLiveData<Boolean>()

    fun onRefresh(){
        if (refreshing.value == true){
            return
        }

        refreshing.value = true
        onLoading()
    }

    fun onLoading(){
        if (loading.value == true){
            return
        }
        loading.value = true
        launch ({

        },{
            toast.value = it.message
        },{
            refreshing.value = false
            loading.value = false
        })
    }
}