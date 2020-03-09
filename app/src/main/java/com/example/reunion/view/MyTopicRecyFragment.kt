package com.example.reunion.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentLoginPhoneBinding
import com.example.reunion.databinding.ViewRecyclerView2Binding
import com.example.reunion.databinding.ViewRecyclerViewBinding
import com.example.reunion.view.adapter.TopicItemAdapter
import com.example.reunion.view_model.LoginViewModel
import com.example.reunion.view_model.MyTopicViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout

class MyTopicRecyFragment(private val type:String):BaseFragment() {
    private val mAdapter = TopicItemAdapter()
    private lateinit var mBinding: ViewRecyclerView2Binding
    private lateinit var mViewModel:MyTopicViewModel

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = setViewModel(activity as BaseActivity,MyTopicViewModel::class.java)
        mBinding.lifecycleOwner = this
        when(type){
            "people" ->{
                initPeopleView(mBinding)
            }
            else ->{
                initBodyView(mBinding)
            }
        }
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

    private fun initPeopleView(mPeopleBinding:ViewRecyclerView2Binding){

        val manager = GridLayoutManager(activity,2)
        mPeopleBinding.recyclerView.layoutManager = manager
        mPeopleBinding.recyclerView.adapter = mAdapter

        val footView:View = View.inflate(activity,R.layout.view_more_load,null)
        mPeopleBinding.newsRefresh.isTargetScrollWithLayout = false
        mPeopleBinding.newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))
        mPeopleBinding.newsRefresh.setOnPullRefreshListener(object : SuperSwipeRefreshLayout.OnPullRefreshListener{
            override fun onPullEnable(p0: Boolean) {
                //下拉距离是否满足刷新
            }

            override fun onPullDistance(p0: Int) {
                //下拉距离
            }

            override fun onRefresh() {
                mViewModel.onRefreshPeople()
            }
        })
        mViewModel.refreshingBody.observe(this, androidx.lifecycle.Observer {
            if (!it)
                mPeopleBinding.newsRefresh.isRefreshing = false
        })

        mPeopleBinding.newsRefresh.setOnPushLoadMoreListener(object : SuperSwipeRefreshLayout.OnPushLoadMoreListener{
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
                mViewModel.loadPeopleData()
            }
        })
        mPeopleBinding.newsRefresh.setFooterView(footView)
        mViewModel.loadingBody.observe(this, androidx.lifecycle.Observer {
            if (!it){
                mPeopleBinding.newsRefresh.setLoadMore(false)
                setFootViewState(3,footView)
            }
        })
    }

    private fun initBodyView(mBodyBinding:ViewRecyclerView2Binding){

        val manager = GridLayoutManager(activity,2)
        mBodyBinding.recyclerView.layoutManager = manager
        mBodyBinding.recyclerView.adapter = mAdapter

        val footView:View = View.inflate(activity,R.layout.view_more_load,null)
        mBodyBinding.newsRefresh.isTargetScrollWithLayout = false
        mBodyBinding.newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))
        mBodyBinding.newsRefresh.setOnPullRefreshListener(object : SuperSwipeRefreshLayout.OnPullRefreshListener{
            override fun onPullEnable(p0: Boolean) {
                //下拉距离是否满足刷新
            }

            override fun onPullDistance(p0: Int) {
                //下拉距离
            }

            override fun onRefresh() {
                mViewModel.onRefreshBody()
            }
        })
        mViewModel.refreshingBody.observe(this, androidx.lifecycle.Observer {
            if (!it)
                mBodyBinding.newsRefresh.isRefreshing = false
        })

        mBodyBinding.newsRefresh.setOnPushLoadMoreListener(object : SuperSwipeRefreshLayout.OnPushLoadMoreListener{
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
                mViewModel.loadBodyData()
            }
        })
        mBodyBinding.newsRefresh.setFooterView(footView)
        mViewModel.loadingBody.observe(this, androidx.lifecycle.Observer {
            if (!it){
                mBodyBinding.newsRefresh.setLoadMore(false)
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