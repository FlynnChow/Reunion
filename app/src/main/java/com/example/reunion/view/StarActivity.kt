package com.example.reunion.view

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityStarBinding
import com.example.reunion.view.adapter.TopicItemAdapter
import com.example.reunion.view_model.StarViewModel
import com.example.reunion.view_model.TopicFragViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout

class StarActivity : BaseActivity() {

    private val adapter = TopicItemAdapter{
        startActivity(Intent(this,TopicActivity::class.java).apply {
            putExtra("data",it)
        })
    }

    private lateinit var mBinding:ActivityStarBinding

    private val mViewModel by lazy { setViewModel(this,StarViewModel::class.java) }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_star)
        mBinding.activity = this
        mBinding.lifecycleOwner = this

        initView()
        createReceiver()
        mViewModel.onRefresh(true)
    }

    private fun createReceiver(){
        mViewModel.receiver = TopicFragViewModel.Receiver {
            mViewModel.deleteData.value = it
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction("reunion.delete.topic")
        registerReceiver(mViewModel.receiver,intentFilter)
    }

    override fun onDestroy() {
        unregisterReceiver(mViewModel.receiver)
        super.onDestroy()
    }

    fun onBack(view: View) = onBackPressed()

    private fun initView(){
        val manager = GridLayoutManager(this,2)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter

        initRefreshView()
    }

    private fun initRefreshView(){
        val footView:View = View.inflate(this,R.layout.view_more_load,null)
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
                mViewModel.onRefresh()
            }
        })

        mViewModel.refreshing.observe(this, androidx.lifecycle.Observer {
            if (!it)
                mBinding.newsRefresh.isRefreshing = false
            else{
                adapter.list.clear()
                adapter.notifyDataSetChanged()
            }
        })

        mViewModel.topicData.observe(this, Observer {
            for (item in it){
                adapter.list.add(item)
                adapter.notifyItemInserted(adapter.list.size - 1)
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
                mViewModel.onLoading()
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

    private fun setFootViewState(state:Int,footView:View){
        (footView.findViewById<View>(R.id.moreLoadNo)).visibility = if (state == 0) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadOk)).visibility = if (state == 1) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadStart)).visibility = if (state == 2) View.VISIBLE else View.GONE
    }
}
