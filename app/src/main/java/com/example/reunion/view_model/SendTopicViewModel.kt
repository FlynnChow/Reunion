package com.example.reunion.view_model

import android.net.Uri
import android.util.Log
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.customize.UploadRequestBody
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.repostory.local_resource.UserHelper
import com.google.gson.Gson
import okhttp3.MultipartBody
import java.io.File

class SendTopicViewModel:BaseViewModel() {
    lateinit var type:String

    val title = MutableLiveData<String>()

    val time = MutableLiveData<String>()

    val arae = MutableLiveData<String>()

    val age = MutableLiveData<String>()

    val topicTitle = MutableLiveData<String>()

    val topicContent = MutableLiveData<String>()

    val topicTime = MutableLiveData<String>()

    val topicArea = MutableLiveData<String>()

    val province = MutableLiveData<String>()

    val city = MutableLiveData<String>()

    val district = MutableLiveData<String>()

    val topicAgeView = MutableLiveData<String>()

    val topicAge = MutableLiveData<Int>()

    var topicUris = ArrayList<Uri>()

    var topicPaths = ArrayList<String>()

    val onCompress = MutableLiveData<Boolean>()

    val showLoadDialog = MutableLiveData<Boolean>()

    val requestBuilder = MutableLiveData<MultipartBody.Builder>()

    val markLocate = MutableLiveData<Boolean>(false)

    var arrayLocate:Array<String>? = null


    fun onSendTopic(view:View){
        if (!UserHelper.isLogin()){
            toast.value = "登录后才能使用该功能"
            return
        }
        if (topicTitle.value == null || topicTitle.value!!.isEmpty()){
            toast.value = "请输入标题"
            return
        }
        if (topicContent.value == null || topicContent.value!!.isEmpty()){
            toast.value = "请输入内容"
            return
        }
        if (topicTime.value == null || topicTime.value!!.isEmpty()){
            toast.value = "请输入时间"
            return
        }
        if (topicArea.value == null || topicArea.value!!.isEmpty()){
            toast.value = "请输入地区"
            return
        }
        if (topicAgeView.value == null || topicAgeView.value!!.isEmpty()){
            toast.value = "请输入年龄"
            return
        }
        if (topicUris.size == 0){
            toast.value = "请至少包含一张图片"
            return
        }
        if (markLocate.value == false){
            toast.value = "请选择寻找范围"
            return
        }

        onCompress.value = true
    }

    fun onUploading(){
        if ("people" == type)
            uploadPeopleTopic()
        else
            uploadBodyTopic()
    }

    private fun uploadPeopleTopic(){
        val requestBuilder =  MultipartBody.Builder()
        val data = TopicBean()
        data.age = topicAge.value
        data.sCity = city.value
        data.sDistrict = district.value
        data.sProvince = province.value
        data.sContent = topicContent.value
        data.uId = UserHelper.getUser()?.uId
        data.sTitle = topicTitle.value
        data.sType = 0
        data.time = topicTime.value
        data.sJW1 = arrayLocate?.get(0)
        data.sJW2 = arrayLocate?.get(1)
        data.sJW3 = arrayLocate?.get(2)

        requestBuilder.addFormDataPart("searchJson",Gson().toJson(data))
        this.requestBuilder.value = requestBuilder
    }

    private fun uploadBodyTopic(){
        val requestBuilder =  MultipartBody.Builder()
        val data = TopicBean()
        data.age = topicAge.value
        data.sCity = city.value
        data.sDistrict = district.value
        data.sProvince = province.value
        data.sContent = topicContent.value
        data.uId = UserHelper.getUser()?.uId
        data.sTitle = topicTitle.value
        data.sType = 1
        data.time = topicTime.value
        data.sJW1 = arrayLocate?.get(0)
        data.sJW2 = arrayLocate?.get(1)
        data.sJW3 = arrayLocate?.get(2)

        requestBuilder.addFormDataPart("searchJson",Gson().toJson(data))
        this.requestBuilder.value = requestBuilder
    }

    fun getTextColor(mark:Boolean):Int{
        if(mark)
            return MyApplication.resource().getColor(R.color.comment_text_color)
        else
            return MyApplication.resource().getColor(R.color.comment_text_name)
    }

    fun getText(mark:Boolean):String{
        if(mark)
            return "已标记范围"
        else
            return "点击标记范围"
    }
}