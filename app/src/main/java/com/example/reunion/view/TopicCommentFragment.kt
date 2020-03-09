package com.example.reunion.view

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Point
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.customize.CommentView
import com.example.reunion.databinding.ViewCommentDialogBinding
import com.example.reunion.databinding.ViewCommentTopicBinding
import com.example.reunion.util.ViewUtil
import com.example.reunion.view.adapter.NewsReplyAdapter
import com.example.reunion.view_model.NewsActivityViewModel
import com.example.reunion.view_model.TopicViewModel
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.view_comment_dialog.*

/**
 * 听说复制暂停容易
 * 你tm的把它复制一个给我看看有多容易
 */
class TopicCommentFragment(private val listener:(Int)->Unit):BaseFragment() {
    companion object{
        val MODE_CLOSE = 0
        val MODE_HIDE_INPUT = 1
    }
    private lateinit var mBinding:ViewCommentTopicBinding
    private lateinit var mView:CommentView
    private lateinit var nestedView:NestedScrollView
    private lateinit var editView:View
    private val maxScroll by lazy { resources.getDimension(R.dimen.comment_scroll_max) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.view_comment_topic,container,false)
        mBinding.lifecycleOwner = this
        mView = mBinding.root.findViewById(R.id.commentView)
        nestedView = mBinding.nestedView
        editView = mBinding.root.findViewById(R.id.replySendLayout)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = setViewModel(activity as BaseActivity,TopicViewModel::class.java)
        mBinding.fragment = this
        mView.setScrollListener(nestedView) { y, isUp->
            if (!isUp){
                nestedView.translationY = y
            }else{
                if (y>=maxScroll){
                    onClose()
                }else{
                    val animation = ObjectAnimator.ofFloat(nestedView,"translationY",nestedView.translationY,0f)
                    animation.duration = 300
                    animation.start()
                }
            }
        }
        mView.setDispatchListener {
            if (it?.y!! < editView.top){
                listener.invoke(MODE_HIDE_INPUT)
                replySend.clearFocus()
            }
        }
        init(mBinding.viewModel!!)
    }

    private fun init(viewModel:TopicViewModel){
        val manager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        val adapter = NewsReplyAdapter { mode, comment->
            when(mode){
                NewsReplyAdapter.LOAD_REPLY->{
                    viewModel.getReplyComment()
                }
                NewsReplyAdapter.SHOW_EDIT->{
                    viewModel.sendBean = comment
                    viewModel.replyFloor.value = comment?.rFloor!! + 1
                    ViewUtil.showInput(activity!!,replySend)
                }
            }
        }
        adapter.comments = viewModel.replyArray
        replyRecycler.layoutManager = manager
        replyRecycler.adapter = adapter
        viewModel.replys.observe(this, Observer {
            for (comment in it){
                if (adapter.comments!!.size==0||comment.rFloor > adapter.comments!![adapter.comments!!.size -1].rFloor){
                    adapter.comments!!.add(comment)
                }
            }
            adapter.notifyDataSetChanged()
        })
        viewModel.replyLoadType.observe(this, Observer {
            adapter.type = it
            adapter.notifyDataSetChanged()
        })
        viewModel.reply.observe(this, Observer {
            adapter.comments!!.add(it)
            adapter.notifyItemInserted(adapter.comments!!.size - 1)
            nestedView.fullScroll(NestedScrollView.FOCUS_DOWN)
        })
        viewModel.showEdit.observe(this, Observer {
            if (it)
                ViewUtil.showInput(activity!!,replySend)
            else{
                ViewUtil.hideInput(activity!!)
                replySend.clearFocus()
            }
        })
        replySend.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus){
                replySend.hint = "${MyApplication.resource().getString(R.string.reply_user)} ${viewModel.sendBean?.fromUname}:"
            }else{
                replySend.hint = MyApplication.resource().getString(R.string.comment_edit)
            }
        }
    }

    private fun getDisplayHeight():Int{
        val display = activity?.windowManager?.defaultDisplay
        val point = Point()
        display?.getSize(point)
        return point.y
    }

    fun onClose(view:View? = null){
        val animation = ObjectAnimator.ofFloat(nestedView,"translationY",nestedView.translationY,getDisplayHeight().toFloat())
        animation.duration = 300
        animation.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                listener.invoke(MODE_CLOSE)
            }
        })
        animation.start()
    }

    fun onInto(view:View? = null){
        val animation = ObjectAnimator.ofFloat(nestedView,"translationY",getDisplayHeight().toFloat(),0f)
        animation.duration = 300
        animation.addListener(object :Animator.AnimatorListener{
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationCancel(animation: Animator?) {}
            override fun onAnimationStart(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {

            }
        })
        animation.start()
    }
}