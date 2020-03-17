package com.example.reunion.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.bean.TopicKeyword
import com.example.reunion.repostory.local_resource.AppDataBase
import com.example.reunion.repostory.remote_resource.TopicRemoteModel
import java.lang.Exception

class SearchViewModel:BaseViewModel() {

    private val remote = TopicRemoteModel()

    val topicData = MutableLiveData<ArrayList<TopicBean>>()

    var keyword = MutableLiveData<String>()

    private var lastKeyword = ""

    var keywords = MutableLiveData<List<String>>()

    var clearKeywords = MutableLiveData<Boolean>()

    val loading = MutableLiveData<Boolean>()

    private var nextPage = 1

    fun onLoading(){
        if (loading.value == true){
            return
        }
        loading.value = true
        launch ({
            val bean = remote.topicSearch(lastKeyword,nextPage)
            when(bean.code){
                200 ->{
                    topicData.value = bean.data
                    nextPage += 1
                }
                300 ->{
                    if (nextPage ==1){
                        toast.value = "未搜索到相关内容"
                    }else{
                        toast.value = "已经没有更多内容了"
                    }
                }
                else ->{
                    throw Exception(bean.msg)
                }
            }
        },{
            toast.value = "加载错误："+it.message
        },{
            loading.value = false
        })
    }

    fun onSearch(){
        if (keyword.value != null && keyword.value!!.isNotEmpty()){
            onSaveKeywordToDb()
            nextPage = 1
            lastKeyword = keyword.value?:""
            onLoading()
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