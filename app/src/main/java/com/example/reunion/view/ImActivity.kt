package com.example.reunion.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityImBinding
import com.example.reunion.repostory.bean.User
import com.example.reunion.repostory.local_resource.UserHelper
import com.example.reunion.repostory.server.WebSocketServer
import com.example.reunion.util.ViewUtil
import com.example.reunion.view.adapter.ImAdapter
import com.example.reunion.view_model.ImViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout

class ImActivity : BaseActivity() {

    private lateinit var mBinding:ActivityImBinding

    private val mViewModel by lazy { setViewModel(this,ImViewModel::class.java) }

    private val adapter = ImAdapter{
        mViewModel.retrySend(it)
    }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_im)
        mBinding.activity  = this
        mBinding.viewModel = mViewModel
        mBinding.lifecycleOwner = this

        initView()
        initViewModel()

        var uid = intent.getStringExtra("uid")
        if (uid == null){
            val user = intent.getParcelableExtra<User.Data>("user")?:return
            mViewModel.initImId(UserHelper.getUser()?.uId?:"",user.uId)
            mViewModel.initData(user)
        }else{
            mViewModel.initImId(UserHelper.getUser()?.uId?:"",uid)
            mViewModel.initData(uid)
        }
    }

    fun onBack(view: View) = onBackPressed()

    fun onClickShowUserMenu(view: View){
        startActivity(Intent(this,MyTopicActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("uid",mViewModel.toUser.value?.uId)
        })
    }

    private fun initView(){
        val manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        manager.reverseLayout = true
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter

        mBinding.newsRefresh.isTargetScrollWithLayout = false
        mBinding.newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))
        mBinding.newsRefresh.setOnPullRefreshListener(object : SuperSwipeRefreshLayout.OnPullRefreshListener{
            override fun onPullEnable(p0: Boolean) {
            }

            override fun onPullDistance(p0: Int) {
                //下拉距离
            }

            override fun onRefresh() {
                mViewModel.obtainMessages()
            }
        })

        mBinding.imEdit.setOnKeyListener { v, keyCode, event ->
            if (event != null && KeyEvent.KEYCODE_ENTER == keyCode
                && KeyEvent.ACTION_DOWN == event.action
                && mViewModel.editContent.value != null && mViewModel.editContent.value!!.isNotEmpty()){
                mViewModel.onSendMessage()
                true
            }
                false
        }

    }

    private fun initViewModel(){
        mViewModel.loading.observe(this, androidx.lifecycle.Observer {
            if (!it){
                mBinding.newsRefresh.isRefreshing = false
            }
        })

        mViewModel.messages.observe(this, Observer {
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()
        })

        mViewModel.newMessage.observe(this, Observer {
            adapter.list.add(0,it)
            mBinding.recyclerView.smoothScrollToPosition(0)
            adapter.notifyItemInserted(0)
        })

        mViewModel.updateMessage.observe(this, Observer {
            for (index in 0 until adapter.itemCount){
                if (it.imId == adapter.list[index].imId && it.time == adapter.list[index].time){
                    adapter.list[index] = it
                    adapter.notifyItemChanged(index)
                    break
                }
            }
        })

        mViewModel.hideInput.observe(this, Observer {
            if (it)
                ViewUtil.hideInput(this)
        })

        WebSocketServer.imMessage.observe(this, Observer {
            if (it != null){
                mViewModel.newMessage.value = it
            }
        })

        WebSocketServer.imMessageArray.observe(this, Observer {
            if (it != null){
                mViewModel.newMessageArray.value = it
            }
        })

        mViewModel.newMessageArray.observe(this, Observer {
            for (message in it){
                adapter.list.add(0,message)
                adapter.notifyItemInserted(0)
            }
        })
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.y?:0f <= mBinding.imEditLayout.top){
            mViewModel.hideInput.value = true
        }
        return super.dispatchTouchEvent(ev)
    }

    override fun onDestroy() {
        WebSocketServer.exitIm()
        super.onDestroy()
    }
}
