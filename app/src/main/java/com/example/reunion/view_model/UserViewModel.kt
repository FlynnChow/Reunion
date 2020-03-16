package com.example.reunion.view_model

import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel

class UserViewModel:BaseViewModel() {
    val refreshing = MutableLiveData<Boolean>()

    fun onRefresh(type:String){

    }
}