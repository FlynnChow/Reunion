package com.example.reunion.view_model

import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel

class ComunityViewModel:BaseViewModel() {
     val refreshing = MutableLiveData<Boolean>()

     val loading = MutableLiveData<Boolean>()
}