package com.example.reunion.view_model

import android.graphics.drawable.Drawable
import android.util.Log
import android.util.SparseArray
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.repostory.bean.CommentBean
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.remote_resource.TopicRemoteModel
import retrofit2.HttpException
import java.net.UnknownHostException

class TopicViewModel: BaseViewModel() {
    val remoteModel = TopicRemoteModel()

    val beanData = MutableLiveData<TopicBean>()

    val pictureNum = MutableLiveData<Int>()

    val commentContent = MutableLiveData("")

    /**
     * 0.可以加载评论
     * 1.正在加载中
     * 2.没有评论了
     */
    val commentLoadType = MutableLiveData(0)
    lateinit var id:String
    private var currentPage = 1
    val commentNum = MutableLiveData<Int>(0)
    val readNum = MutableLiveData<Int>(0)
    val comments = MutableLiveData<ArrayList<CommentBean.Comment>>()
    val replyFloor = MutableLiveData(0)
    val replyBean = MutableLiveData<CommentBean.Comment>() //点击后回复的主楼
    var sendBean:CommentBean.Comment? = null //回复后新增的回复

    val comment = MutableLiveData<CommentBean.Comment>()

    val replyArray = ArrayList<CommentBean.Comment>() //主楼中新增的回复

    val replys = MutableLiveData<ArrayList<CommentBean.Comment>>() //主楼中新增的回复
    val reply = MutableLiveData<CommentBean.Comment>() //回复后新增的回复
    val replyLoadType = MutableLiveData(0)
    val showEdit = MutableLiveData(false)
    var replyPage = 1
    val replyEdit = MutableLiveData("")
    val replyContent = SparseArray<String>()

    val replySum = MutableLiveData(0)

    fun initData(bean:TopicBean?){
        if (bean != null){
            beanData.value = bean
            pictureNum.value =
                if (bean.pictures == null||bean.pictures!!.size == 0)
                    0
                else
                    bean.pictures!!.size
        }

        id = bean?.sId.toString()
    }

    fun getPictureUrl(bean: TopicBean?,index:Int):String?{
        if (bean?.pictures == null ||bean.pictures!!.size == 0){
            return null
        }else{
            if (bean.pictures!!.size > index){
                return bean.pictures!![index]
            }else{
                return null
            }
        }
    }

    fun getReadNum(bean: TopicBean?):String?{
        if (bean == null||bean.sPageView <= 10000){
            return "${bean?.sPageView?:0}阅读"
        }else{
            val readNum = bean.sPageView/1000
            val num = readNum / 10f
            return "${num}万阅读"
        }
    }

    fun getCommentNum(num: Int):String?{
        if (num <= 10000){
            return "$num 条评论"
        }else{
            val readNum = num/1000
            val num = readNum / 10f
            return "$num 万条评论"
        }
    }

    fun getReadString(commentNum:Int):String{
        var num = commentNum
        if (num >= 10000){
            num = (num - num%1000)/1000
            return num.toString()+"万阅读"
        }
        return num.toString()+"阅读"
    }

    fun isCanSend(commentContent:String?):Boolean{
        if (commentContent == null) return false
        return commentContent.isNotEmpty()&& UserHelper.isLogin()
    }

    fun getSendBg(commentContent:String?): Drawable {
        if (commentContent == null)
            return MyApplication.resource().getDrawable(R.drawable.comment_no_send_bg)
        return if (commentContent.isNotEmpty()&& UserHelper.isLogin()){
            MyApplication.resource().getDrawable(R.drawable.comment_send_bg)
        }else{
            MyApplication.resource().getDrawable(R.drawable.comment_no_send_bg)
        }
    }

    fun getNewsComment(view: View? = null){
        launch({
            commentLoadType.value = 1
            val data = remoteModel.getComment(id,currentPage)
            if (data.data != null){
                if (currentPage > data.data!!.page){
                    commentLoadType.value = 2
                }else{
                    commentLoadType.value = 0
                    if (data.data!!.records != null){
                        if (data.data?.records?.size?:0>0){
                            commentNum.value = data.data?.records!![0].commentSum
                        }
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

                        val sum = commentNum.value?:0
                        commentNum.value = sum + 1
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
                Log.d("测试",it.message.toString())
                toast.value = "评论失败 " + it.message
        })
    }

    fun getReplyNumString(num:Int):String{
        return num.toString()+"条回复"
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
                        showEdit.value = false
                        if (data.data!=null)
                            reply.value = data.data
                        replyContent.put(floor,"")
                        replyFloor.value = 0
                        val sum = replySum.value?:0
                        replySum.value = sum + 1
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

}