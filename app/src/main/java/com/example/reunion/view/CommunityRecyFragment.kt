package com.example.reunion.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.ViewRecyclerView2Binding
import com.example.reunion.view.adapter.CommunityItemAdapter
import com.example.reunion.view_model.CommunityItemViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CommunityRecyFragment():BaseFragment() {
    private var type = ""

    companion object{
        @JvmStatic
        fun getInstance(arg:String):CommunityRecyFragment{
            val fragment = CommunityRecyFragment()
            val args = Bundle()
            args.putString("type",arg)
            fragment.arguments = args
            return fragment
        }

    }

    private val mAdapter = CommunityItemAdapter{
        startActivity(Intent(activity,CommunityActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("data",it)
        })
    }
    private lateinit var mBinding: ViewRecyclerView2Binding
    private lateinit var mViewModel:CommunityItemViewModel
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.lifecycleOwner = this

        initView()
        initViewModel()
        mViewModel.onLoadCommunity(type,true)
    }

    private fun initViewModel(){
        mViewModel.communityData.observe(this, Observer {
            mAdapter.datas.addAll(it)
            mAdapter.notifyDataSetChanged()
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater,R.layout.view_recycler_view2,container,false)
        mBinding.lifecycleOwner = this
        type = arguments?.getString("type")?:""
        mViewModel = setViewModel(CommunityItemViewModel::class.java,type)
        return mBinding.root
    }

    private fun initView(){

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
                mViewModel.onRefresh(type)
            }
        })
        mViewModel.refreshing.observe(this, androidx.lifecycle.Observer {
            if (!it){
                mBinding.newsRefresh.isRefreshing = false
            }
            else{
                mAdapter.datas.clear()
                mAdapter.notifyDataSetChanged()
            }
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
                mViewModel.onLoadCommunity(type)
            }
        })
        mBinding.newsRefresh.setFooterView(footView)

        mViewModel.loading.observe(this, androidx.lifecycle.Observer {
            if (!it){
                mBinding.newsRefresh.setLoadMore(false)
                setFootViewState(3,footView)
            }
        })

        mViewModel.deleteData.observe(this, androidx.lifecycle.Observer {
            for (index in 0 until mAdapter.datas.size){
                val id = mAdapter.datas[index].communityId
                if (id == it){
                    mAdapter.datas.remove(mAdapter.datas[index])
                    mAdapter.notifyItemRemoved(index)
                    mAdapter.notifyDataSetChanged()
                    break
                }
            }
        })
    }

    private fun setFootViewState(state:Int,footView: View){
        (footView.findViewById<View>(R.id.moreLoadNo)).visibility = if (state == 0) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadOk)).visibility = if (state == 1) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadStart)).visibility = if (state == 2) View.VISIBLE else View.GONE
    }
}