package com.example.reunion.view

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityTopicBinding
import com.example.reunion.databinding.DialogTopicBinding
import com.example.reunion.util.EasyImageGetter
import com.example.reunion.util.ViewUtil
import com.example.reunion.view.adapter.NewsCommentAdapter
import com.example.reunion.view_model.TopicViewModel

class TopicActivity : BaseActivity() {
    private var isShowReply = false //记录回复是否被打开
    private lateinit var mBinding:ActivityTopicBinding
    private lateinit var mDialogBinding:DialogTopicBinding
    private val mViewModel:TopicViewModel by lazy {
        setViewModel(this,TopicViewModel::class.java)
    }
    private lateinit var replyFragment: TopicCommentFragment

    private val dialog by lazy {
        initDialog()
    }

    private lateinit var deleteDialog:Dialog

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_topic)
        mBinding.activity = this
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = this

        mDialogBinding = DataBindingUtil.inflate(layoutInflater,R.layout.dialog_topic,null,false)
        mDialogBinding.lifecycleOwner = this
        mDialogBinding.activity = this
        mDialogBinding.viewModel = mViewModel
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

        mViewModel.deleteResult.observe(this, Observer {
            if (it){
                finish()
            }
        })

        deleteDialog = ViewUtil.createNormalDialog(this,
            "是否确定删除您发布的话题？删除后无法再恢复。","删除",{
                deleteDialog.dismiss()
                mViewModel.onDeleteTopic()
            },{
                deleteDialog.dismiss()
            })
    }

    private fun initDialog(): Dialog {
        val dialog =  Dialog(this,R.style.CustomizeDialog)
        dialog.setContentView(mDialogBinding.root)

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.BOTTOM
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.dismiss()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
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
        EasyImageGetter.create().setPlaceHolder(R.drawable.loading).setPlaceHolder(R.drawable.load_error).loadHtml(content,textView)
    }

    override fun onBackPressed() {
        if (!isShowReply)
            super.onBackPressed()
        else
            replyFragment.onClose()
    }

    fun onClickStar(view:View){
        dialog.dismiss()
        mViewModel.onStarTopic()
    }

    fun onClickDelete(view:View){
        dialog.dismiss()
        deleteDialog.show()
    }

    fun onClickCancel(view:View){
        dialog.dismiss()
    }

    fun onClickShowDialog(view:View){
        dialog.show()
    }
}
