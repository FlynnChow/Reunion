package com.example.reunion.view_model

import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.CommunityBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.CommunityRemote
import java.lang.Exception

class CommunityViewModel:BaseViewModel() {
    private val remote = CommunityRemote()

    private var isLoadAllComment = false
    val data:MutableLiveData<CommunityBean> = MutableLiveData()

    private var sending = false
    private var delete = false
    private var nextPage = 1
    val editContent = MutableLiveData("")
    val loading = MutableLiveData<Int>()
    val comments = MutableLiveData<ArrayList<CommunityBean.Comment>>()
    val comment = MutableLiveData<CommunityBean.Comment>()
    val deleteResult = MutableLiveData<Boolean>()

    private val editContentArray = SparseArray<String>()
    private var lastFloor = 0
    private var toUId:String? = null

    fun getMenuVisible(uid:String?):Int{
        val mUid = UserHelper.getUser()?.uId?:""
        return if (uid == mUid)
            View.VISIBLE
        else
            View.GONE
    }

    fun isCanSend(editContent:String?):Boolean{
        if (editContent == null) return false
        return editContent.isNotEmpty() && UserHelper.isLogin()
    }

    fun getSendBg(editContent:String?): Drawable {
        if (editContent == null)
            return MyApplication.resource().getDrawable(R.drawable.comment_no_send_bg)
        return if (editContent.isNotEmpty()&& UserHelper.isLogin()){
            MyApplication.resource().getDrawable(R.drawable.comment_send_bg)
        }else{
            MyApplication.resource().getDrawable(R.drawable.comment_no_send_bg)
        }
    }

    fun initData(data:CommunityBean){
        this.data.value = data
        onLoadingComment(true)
    }

    fun onSendComment(view:View){
        if (editContent.value == null || editContent.value!!.isEmpty()||sending){
            return
        }
        sending = true
        val floor = lastFloor
        launch({
            val bean
                    = remote.sendCommunityComment(data.value?.communityId?:throw Exception("id is null"),
                editContent.value?:throw Exception("comment is empty"),
                UserHelper.getUser()?.uId?:throw Exception("uid is null"),
                toUId)
            when(bean.code){
                200 ->{
                    toast.value = "评论成功"
                    comment.value = bean.data
                    editContentArray.put(floor,"")
                    editContent.value = ""
                }
                else->{
                    throw Exception(bean.msg)
                }
            }
        },{
            toast.value = "发布失败：" + it.message
        },{
            sending = false
        })
    }

    fun onLoadingComment(first:Boolean = false){
        if (loading.value == 1) return
        loading.value = 1
        launch({
            val bean
                    = remote.obtainCommunityComment(data.value?.communityId?:throw Exception("id is null"),nextPage)
            isLoadAllComment = false
            when(bean.code){
                200 ->{
                    comments.value = bean.data
                    nextPage += 1
                }
                300 ->{
                    if (!first){
                        toast.value = "已经没有更多评论了"
                    }
                    isLoadAllComment = true
                }
                else->{
                    throw Exception(bean.msg)
                }
            }
        },{
            toast.value = "加载失败：" + it.message
        },{
            if (isLoadAllComment)
                loading.value = 2
            else
                loading.value = 0
        })
    }

    fun onDeleteCommunity(){
        if (delete||UserHelper.getUser()?.uId?:"" != data.value?.uId?:"-1"){
            return
        }
        delete = true
        launch({
            val bean =
                remote.deleteCommunity(data.value?.communityId?:throw Exception("id is null"),
                    UserHelper.getUser()?.uId?:throw Exception("uid is null"))
            when(bean.code){
                200 ->{
                    toast.value = "成功删除"
                    MyApplication.app.sendBroadcast(Intent("reunion.delete.community").apply {
                        putExtra("id",this@CommunityViewModel.data.value?.communityId?:"")
                    })
                    deleteResult.value = true
                }
                else ->{
                    throw Exception(bean.msg)
                }
            }
        },{
            toast.value = "删除失败：" + it.message
        },{
            delete = false
        })
    }

    fun setEditText(floor:Int = 0,uid:String? = null):MutableLiveData<String>{
        editContentArray.put(lastFloor,editContent.value)
        val text = editContentArray.get(floor)
        lastFloor = floor
        toUId = uid
        editContent.value = text
        return editContent
    }
}