package com.example.reunion.view

import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentHomeTopicBinding
import com.example.reunion.view.adapter.TopicItemAdapter
import com.example.reunion.view_model.TopicFragViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import kotlinx.android.synthetic.main.view_recycler_view.*


class TopicItemFragment():BaseFragment() {
    private var type  = ""
    companion object{
        @JvmStatic
        fun getInstance(arg:String):TopicItemFragment{
            val fragment = TopicItemFragment()
            val args = Bundle()
            args.putString("type",arg)
            fragment.arguments = args
            return fragment
        }

    }
    private lateinit var mBinding: FragmentHomeTopicBinding
    private val adapter = TopicItemAdapter{
        startActivity(Intent(activity,TopicActivity::class.java).apply {
            putExtra("data",it)
        })
    }
    private lateinit var mViewModel:TopicFragViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home_topic,container,false)
        mBinding.lifecycleOwner = this
        type = arguments?.getString("type").toString()
        mViewModel = setViewModel(TopicFragViewModel::class.java,type)
        createReceiver()
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        initViewModel()
        if (type == "nearby"){
            initLocate()
        }else{
            mViewModel.updateItems(type,true)
        }
        super.onViewCreated(view, savedInstanceState)
    }

    private fun createReceiver(){
        mViewModel.receiver = TopicFragViewModel.Receiver {
            mViewModel.deleteData.value = it
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("reunion.delete.topic")
        activity?.registerReceiver(mViewModel.receiver,intentFilter)
    }

    override fun onDestroy() {
        activity?.unregisterReceiver(mViewModel.receiver)
        super.onDestroy()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.recyclerView
        mBinding.viewModel = mViewModel
    }

    private fun initView(){
        val manager = GridLayoutManager(context,2)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter

        initRefreshView()
    }

    private fun initViewModel(){
        mViewModel.newData.observe(this, Observer {
            if (it != null){
                adapter.list.addAll(it)
                mViewModel.newData.value = null
                adapter.notifyDataSetChanged()
            }
        })
        mViewModel.deleteData.observe(this, androidx.lifecycle.Observer {
            for (index in 0 until adapter.list.size){
                val id = adapter.list[index].sId
                if (id == it){
                    adapter.list.remove(adapter.list[index])
                    adapter.notifyItemRemoved(index)
                    break
                }
            }
        })
    }

    private fun initRefreshView(){
        val footView:View = View.inflate(context,R.layout.view_more_load,null)
        newsRefresh.isTargetScrollWithLayout = false
        newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))
        newsRefresh.setOnPullRefreshListener(object : SuperSwipeRefreshLayout.OnPullRefreshListener{
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
            if (!it)
                newsRefresh.isRefreshing = false
            else{
                adapter.list.clear()
                adapter.notifyDataSetChanged()
            }
        })

        newsRefresh.setOnPushLoadMoreListener(object : SuperSwipeRefreshLayout.OnPushLoadMoreListener{
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
                mViewModel.updateItems(type)
            }
        })
        newsRefresh.setFooterView(footView)
        mViewModel.loading.observe(this, androidx.lifecycle.Observer {
            if (!it){
                newsRefresh.setLoadMore(false)
                setFootViewState(3,footView)
            }
        })
    }

    private fun initLocate(){
        var mLocationClient: AMapLocationClient? = null
        val mLocationListener = AMapLocationListener {
            if (it.longitude != 0.0||it.latitude != 0.0){
                mViewModel.locate = "${it.longitude},${it.latitude}"
                mViewModel.updateItems(type,true)
                mLocationClient?.stopLocation()
                mLocationClient?.onDestroy()
            }
        }
        mLocationClient = AMapLocationClient(context)
        mLocationClient.setLocationListener(mLocationListener)

        var mLocationOption: AMapLocationClientOption? = null
        mLocationOption = AMapLocationClientOption()
        mLocationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        mLocationOption.interval = 2000

        mLocationClient.setLocationOption(mLocationOption)
        mLocationClient.startLocation()
    }

    private fun setFootViewState(state:Int,footView:View){
        (footView.findViewById<View>(R.id.moreLoadNo)).visibility = if (state == 0) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadOk)).visibility = if (state == 1) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadStart)).visibility = if (state == 2) View.VISIBLE else View.GONE
    }


}