package com.example.reunion.view_model

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Build
import android.text.Html
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.CommentBean
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.NewsCommentRemoteModel
import retrofit2.HttpException
import java.net.UnknownHostException

class NewsActivityViewModel:BaseViewModel() {
    val remoteModel by lazy { NewsCommentRemoteModel() }

    val src = MutableLiveData("")
    val title = MutableLiveData("")
    val time = MutableLiveData("")
    val commentNum = MutableLiveData<Int>(0)
    val readNum = MutableLiveData<Int>(0)
    val commentContent = MutableLiveData("")
    val comments = MutableLiveData<ArrayList<CommentBean.Comment>>()
    val comment = MutableLiveData<CommentBean.Comment>()
    val imageUrl = MutableLiveData("")
    val newsBean = MutableLiveData<NewsBean.News>()
    val content =  MutableLiveData("")

    val replyFloor = MutableLiveData(0)
    val replyBean = MutableLiveData<CommentBean.Comment>() //点击后回复的主楼
    val reply = MutableLiveData<CommentBean.Comment>() //回复后新增的回复
    val replys = MutableLiveData<ArrayList<CommentBean.Comment>>() //主楼中新增的回复
    val replyContent = SparseArray<String>()
    val replyEdit = MutableLiveData("")
    val replyArray = ArrayList<CommentBean.Comment>() //主楼中新增的回复
    var sendBean:CommentBean.Comment? = null //回复后新增的回复

    val showEdit = MutableLiveData(false)
    /**
     * 0.可以加载评论
     * 1.正在加载中
     * 2.没有评论了
     */
    val commentLoadType = MutableLiveData(0)
    val replyLoadType = MutableLiveData(0)
    lateinit var id:String
    private var currentPage = 1
    var replyPage = 1

    fun getCommentString(commentNum:Int):String{
        var num = commentNum
        if (num >= 10000){
            num = (num - num%1000)/1000
            return num.toString()+"万评论"
        }
        return num.toString()+"评论"
    }

    fun getReadString(commentNum:Int):String{
        var num = commentNum
        if (num >= 10000){
            num = (num - num%1000)/1000
            return num.toString()+"万阅读"
        }
        return num.toString()+"阅读"
    }

    fun getReplyNumString(num:Int):String{
        return num.toString()+"条回复"
    }

    fun isCanSend(commentContent:String?):Boolean{
        if (commentContent == null) return false
        return commentContent.isNotEmpty()&&UserHelper.isLogin()
    }

    fun getSendBg(commentContent:String?):Drawable{
        if (commentContent == null)
            return MyApplication.resource().getDrawable(R.drawable.comment_no_send_bg)
        return if (commentContent.isNotEmpty()&&UserHelper.isLogin()){
            MyApplication.resource().getDrawable(R.drawable.comment_send_bg)
        }else{
            MyApplication.resource().getDrawable(R.drawable.comment_no_send_bg)
        }
    }


    fun onSendClick(view: View){
        launch ({
            if (UserHelper.isLogin()){
                val data = remoteModel.onInsertComment(id,UserHelper.getUser()!!.uId,commentContent.value!!)
                when(data.code){
                    200->{
                        toast.value = MyApplication.resource().getString(R.string.comment_success)
                        if (data.data!=null)
                            comment.value = data.data
                        commentContent.value = ""
                    }
                    202->{
                        toast.value = "错误"
                    }
                }
            }
        },{
            if (it is HttpException || it is UnknownHostException)
                toast.value = it.message
            else
                toast.value = "评论失败"
        })
    }

    fun onReplySendClick(view: View){
        if (!UserHelper.isLogin()){
            return
        }
        val floor = replyFloor.value!!
        val content = replyContent[replyFloor.value!!]
        if (content!=null&&sendBean!=null){
            launch ({
                val data = remoteModel.onInsertReply(
                    replyBean.value!!.xwcId,
                    if(replyFloor.value == 0) 0 else sendBean!!.rFloor,
                    UserHelper.getUser()!!.uId,
                    sendBean!!.fromUid,content)
                when(data.code){
                    200->{
                        toast.value = MyApplication.resource().getString(R.string.comment_success)
                        if (data.data!=null)
                            reply.value = data.data
                        replyContent.put(floor,"")
                    }
                    202->{
                        toast.value = "错误"
                    }
                }
            },{
                if (it is HttpException || it is UnknownHostException)
                    toast.value = it.message
                else
                    toast.value = "评论失败"
            })
        }
    }


    fun initLiveData(bean:NewsBean.News?){
        if (bean == null) {
            toast.value = "错误，内容为空"
            return
        }
        newsBean.value = bean
        src.value = bean.src
        title.value = bean.title
        time.value = bean.time
        imageUrl.value = bean.pic
        content.value = bean.content
        id = bean.getId()
    }

    fun getNewsComment(view:View? = null){
        launch({
            commentLoadType.value = 1
            val data = remoteModel.getNewsComment(id,currentPage)
            if (data.data != null){
                if (currentPage > data.data!!.page){
                    commentLoadType.value = 2
                }else{
                    commentLoadType.value = 0
                    if (data.data!!.records != null){
                        comments.value = data.data!!.records
                        currentPage +=1
                    }
                }
            }else{
                toast.value = "获取评论失败"
            }
        },{
            if (it is HttpException || it is UnknownHostException)
                toast.value = it.message
            else
                toast.value = "获取评论异常:"
            commentLoadType.value = 0
        })
    }

    fun getReplyComment(isFirst:Boolean = false){
        launch({
            replyLoadType.value = 1
            val data = remoteModel.getReplyComment(replyBean.value?.xwcId!!,replyPage)
            if (data.data != null){
                if (replyPage > data.data!!.page){
                    replyLoadType.value = 2
                }else{
                    replyLoadType.value = 0
                    if (data.data!!.records != null){
                        replys.value = data.data!!.records
                        replyPage += 1
                    }
                }
                if (isFirst&&data.data!!.records?.size==0){
                    showEdit.value = true
                }
            }else{
                toast.value = "获取评论失败"
            }
        },{
            if (it is HttpException || it is UnknownHostException)
                toast.value = it.message
            else
                toast.value = "获取评论异常:"
            replyLoadType.value = 0
        })
    }
}