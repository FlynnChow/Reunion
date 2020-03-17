package com.example.reunion.view_model

import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.TopicKeyword
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.local_resource.AppDataBase
import com.example.reunion.repostory.remote_resource.UserRemoteModel
import java.lang.Exception

class SearchUserViewModel:BaseViewModel() {

    private val remote = UserRemoteModel()

    private var lastKeyword = ""

    private var nextPage = 1

    val userData = MutableLiveData<ArrayList<User.Data>>()

    var keyword = MutableLiveData<String>()

    val loading = MutableLiveData<Boolean>()

    fun onLoading(){
        if (loading.value == true){
            return
        }
        loading.value = true
        launch ({
            val bean = remote.userSearch(lastKeyword,nextPage)
            when(bean.code){
                200 ->{
                    userData.value = bean.data
                }
                300 ->{
                    if (nextPage ==1)
                        toast.value = "未搜索到用户"
                    else
                        toast.value = "已经没有了"
                }
                else ->{
                    throw Exception(bean.msg)
                }
            }
        },{
            toast.value = it.message
        },{
            loading.value = false
        })
    }

    fun onSearch(){
        if (keyword.value != null && keyword.value!!.isNotEmpty()){
            lastKeyword = keyword.value?:""
            nextPage = 1
            onLoading()
        }else{
            toast.value = "关键词不能为空"
        }
    }

}