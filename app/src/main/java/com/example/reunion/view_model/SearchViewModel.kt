package com.example.reunion.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.TopicKeyword
import com.example.reunion.repostory.local_resource.AppDataBase

class SearchViewModel:BaseViewModel() {

    var keyword = MutableLiveData<String>()

    var keywords = MutableLiveData<List<String>>()

    var clearKeywords = MutableLiveData<Boolean>()

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
            onSaveKeywordToDb()
        }else{
            toast.value = "关键词不能为空"
        }
    }

    fun obtainKeywords(){
        val dao = AppDataBase.instance.getTopicKeywordDao()
        launch {
            keywords.value = dao.obtainKeywords()
        }
    }

    fun onSaveKeywordToDb(){
        val keyword = keyword.value?:""
        if (keyword.isNotEmpty()){
            launch {
                AppDataBase.instance.getTopicKeywordDao()
                    .insertKeyword(TopicKeyword(keyword,System.currentTimeMillis()))
            }
        }
    }

    fun onClearKeyword(view:View){
        clearKeywords.value = true
        launch {
            AppDataBase.instance.getTopicKeywordDao().clearKeyword()
        }
    }
}