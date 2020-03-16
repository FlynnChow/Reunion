package com.example.reunion.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentLoginPhoneBinding
import com.example.reunion.databinding.ViewRecyclerView2Binding
import com.example.reunion.databinding.ViewRecyclerViewBinding
import com.example.reunion.view.adapter.CommunityItemAdapter
import com.example.reunion.view.adapter.TopicItemAdapter
import com.example.reunion.view_model.ComunityViewModel
import com.example.reunion.view_model.LoginViewModel
import com.example.reunion.view_model.MyTopicViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout

class CommunityRecyFragment(private val type:String = ""):BaseFragment() {
    private val mAdapter = CommunityItemAdapter()
    private lateinit var mBinding: ViewRecyclerView2Binding
    private val mViewModel by lazy {
        setViewModel(this,ComunityViewModel::class.java)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.lifecycleOwner = this

        initRefresh()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.view_recycler_view2,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    private fun initRefresh(){

        val manager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = mAdapter

        val footView:View = View.inflate(activity,R.layout.view_more_load,null)
        mBinding.newsRefresh.isTargetScrollWithLayout = false
        mBinding.newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))
        mBinding.newsRefresh.setOnPullRefreshListener(object : SuperSwipeRefreshLayout.OnPullRefreshListener{
            override fun onPullEnable(p0: Boolean) {
                //下拉距离是否满足刷新
            }

            override fun onPullDistance(p0: Int) {
                //下拉距离
            }

            override fun onRefresh() {

            }
        })
        mViewModel.refreshing.observe(this, androidx.lifecycle.Observer {
            if (!it)
                mBinding.newsRefresh.isRefreshing = false
        })

        mBinding.newsRefresh.setOnPushLoadMoreListener(object : SuperSwipeRefreshLayout.OnPushLoadMoreListener{
            override fun onPushDistance(p0: Int) {
            }

            override fun onPushEnable(isLoad: Boolean) {
                if (isLoad){
                    setFootViewState(1,footView)
                }else{
                    setFootViewState(0,footView)
                }
            }

            override fun onLoadMore() {
                setFootViewState(2,footView)
            }
        })
        mBinding.newsRefresh.setFooterView(footView)
        mViewModel.loading.observe(this, androidx.lifecycle.Observer {
            if (!it){
                mBinding.newsRefresh.setLoadMore(false)
                setFootViewState(3,footView)
            }
        })
    }

    private fun setFootViewState(state:Int,footView: View){
        (footView.findViewById<View>(R.id.moreLoadNo)).visibility = if (state == 0) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadOk)).visibility = if (state == 1) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadStart)).visibility = if (state == 2) View.VISIBLE else View.GONE
    }
}