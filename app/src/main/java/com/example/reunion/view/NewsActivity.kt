package com.example.reunion.view

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityNewsBinding
import com.example.reunion.util.EasyImageGetter
import com.example.reunion.util.ViewUtil
import com.example.reunion.view.adapter.NewsCommentAdapter
import com.example.reunion.view_model.NewsActivityViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.activity_news.*
import kotlinx.android.synthetic.main.view_news_content.*

class NewsActivity : BaseActivity() {
    private var isShowReply = false //记录回复是否被打开
    private lateinit var mBinding: ActivityNewsBinding
    private val mViewModel
            by lazy { setViewModel(this, NewsActivityViewModel::class.java) }
    private lateinit var replyFragment: CommentFragment

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_news)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.activity = this
        mViewModel.initLiveData(intent.getParcelableExtra("news"))
        initView()
        initReplyComment()
        mViewModel.getNewsComment()
    }

    private fun initView(){
        val manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        val adapter = NewsCommentAdapter { index,comment->
            when(index){
                NewsCommentAdapter.LOAD_COMMENT->{
                    mViewModel.getNewsComment()
                }
                NewsCommentAdapter.LOAD_REPLY->{
                    mViewModel.replyBean.value = comment
                    mViewModel.replyBean.value?.fromUid = mViewModel.replyBean.value!!.uId
                    mViewModel.replyBean.value?.fromUname = mViewModel.replyBean.value!!.uName
                    mViewModel.replyFloor.value = 0
                    mViewModel.sendBean = mViewModel.replyBean.value
                    mViewModel.replySum.value = comment?.replySum?:0
                    ViewUtil.hideInput(this)
                    showReplyComment()
                    mViewModel.getReplyComment(true)
                }
            }
        }
        newsRecyclerView.layoutManager = manager
        newsRecyclerView.adapter = adapter
        newsAppbar.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, distance ->
            titleView.translationY = distance.toFloat()
            roundView.translationY = distance.toFloat()
        })



        mViewModel.comments.observe(this, Observer {
            adapter.comments.addAll(it)
            adapter.notifyDataSetChanged()
        })

        mViewModel.content.observe(this, Observer {
            setHtmlContent(newsContent,it)
        })

        mViewModel.comment.observe(this, Observer {
            adapter.comments.add(it)
            adapter.notifyDataSetChanged()
            ViewUtil.hideInput(this)
            newsAppbar.setExpanded(false)
            nestedView.fullScroll(NestedScrollView.FOCUS_DOWN)
        })

        mViewModel.commentLoadType.observe(this, Observer {
           if (it != adapter.type){
               adapter.type = it
               adapter.notifyDataSetChanged()
           }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.y!! <= mBinding.newsEditView.top){
            ViewUtil.hideInput(this)
            newsEdit.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    fun onBackClick(view: View){
        finish()
    }

    private fun setHtmlContent(textView:TextView, content:String){
        EasyImageGetter.create().setPlaceHolder(R.drawable.loading).setPlaceHolder(R.drawable.load_error).loadHtml(content,textView)
    }

    private fun initReplyComment(){
        replyFragment = CommentFragment{
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

    fun showReplyComment(){
        isShowReply = true
        supportFragmentManager.beginTransaction().show(replyFragment).commit()
        replyFragment.onInto()
    }

    fun hideReplyComment(){
        isShowReply = false
        supportFragmentManager.beginTransaction().hide(replyFragment).commit()
    }

    override fun onBackPressed() {
        if (!isShowReply)
            super.onBackPressed()
        else
            replyFragment.onClose()
    }
}
