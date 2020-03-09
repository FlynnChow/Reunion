package com.example.reunion.view

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityTopicBinding
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.util.HtmlImageGetter
import com.example.reunion.util.ViewUtil
import com.example.reunion.view.adapter.NewsCommentAdapter
import com.example.reunion.view_model.TopicViewModel

class TopicActivity : BaseActivity() {
    private var isShowReply = false //记录回复是否被打开
    private lateinit var mBinding:ActivityTopicBinding
    private val mViewModel:TopicViewModel by lazy {
        setViewModel(this,TopicViewModel::class.java)
    }
    private lateinit var replyFragment: TopicCommentFragment

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_topic)
        mBinding.activity = this
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = this

        initView()
        initReplyComment()
        mViewModel.initData(intent.getParcelableExtra("data"))

        mViewModel.getNewsComment()
    }

    private fun initView(){
        val manager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        val adapter = NewsCommentAdapter { index,comment->
            when(index){
                NewsCommentAdapter.LOAD_COMMENT->{
                    mViewModel.getNewsComment()
                }
                NewsCommentAdapter.LOAD_REPLY->{
                    mViewModel.replyBean.value = comment
                    mViewModel.replyBean.value?.fromUid = mViewModel.replyBean.value!!.uId
                    mViewModel.replyBean.value?.fromUname = mViewModel.replyBean.value!!.uName
                    mViewModel.replySum.value = comment?.replySum?:0
                    mViewModel.replyFloor.value = 0
                    mViewModel.sendBean = mViewModel.replyBean.value
                    ViewUtil.hideInput(this)
                    showReplyComment()
                    mViewModel.getReplyComment(true)
                }
            }
        }

        mViewModel.commentLoadType.observe(this, Observer {
            if (it != adapter.type){
                adapter.type = it
                adapter.notifyDataSetChanged()
            }
        })

        mBinding.newsRecyclerView.layoutManager = manager
        mBinding.newsRecyclerView.adapter = adapter



        mViewModel.comments.observe(this, Observer {
            adapter.comments.addAll(it)
            adapter.notifyDataSetChanged()
        })

        mViewModel.comment.observe(this, Observer {
            adapter.comments.add(it)
            adapter.notifyDataSetChanged()
            ViewUtil.hideInput(this)
            mBinding.nestedView.fullScroll(NestedScrollView.FOCUS_DOWN)
        })

        mViewModel.commentLoadType.observe(this, Observer {
            if (it != adapter.type){
                adapter.type = it
                adapter.notifyDataSetChanged()
            }
        })
    }

    private fun initReplyComment(){
        replyFragment = TopicCommentFragment{
            when(it){
                CommentFragment.MODE_CLOSE->{
                    hideReplyComment()
                    mViewModel.replyArray.clear()
                    mViewModel.replyContent.clear()
                    mViewModel.replyLoadType.value = 0
                    mViewModel.replyPage = 1
                }
                CommentFragment.MODE_HIDE_INPUT->{
                    mViewModel.replyFloor.value = 0
                    mViewModel.sendBean = mViewModel.replyBean.value
                    ViewUtil.hideInput(this)
                }
            }
        }
        mViewModel.replyEdit.observe(this, Observer {
            mViewModel.replyContent.put(mViewModel.replyFloor.value!!,it)
        })

        mViewModel.replyFloor.observe(this, Observer {
            var content = mViewModel.replyContent[it]
            if (content == null){
                mViewModel.replyContent.put(it,"")
                content = mViewModel.replyContent[it]
            }
            mViewModel.replyEdit.value = content
        })
        supportFragmentManager.beginTransaction().replace(R.id.fragmentView,replyFragment).commit()
        supportFragmentManager.beginTransaction().hide(replyFragment).commit()
    }

    fun onBack(view:View){
        onBackPressed()
    }

    fun showReplyComment(){
        isShowReply = true
        supportFragmentManager.beginTransaction().show(replyFragment).commit()
        replyFragment.onInto()
    }

    fun hideReplyComment(){
        isShowReply = false
        supportFragmentManager.beginTransaction().hide(replyFragment).commit()
    }

    private fun setHtmlContent(textView: TextView, content:String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            textView.text = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, HtmlImageGetter( this) {
                textView.text = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY, HtmlImageGetter(this),null)
            },null)
        }else{
            textView.text = Html.fromHtml(content)
        }
    }

    override fun onBackPressed() {
        if (!isShowReply)
            super.onBackPressed()
        else
            replyFragment.onClose()
    }
}
