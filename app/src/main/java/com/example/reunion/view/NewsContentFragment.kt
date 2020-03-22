package com.example.reunion.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.ViewRecyclerViewBinding
import com.example.reunion.view.adapter.NewsAdapter
import com.example.reunion.view.adapter.NewsItemAdapter
import com.example.reunion.view_model.NewsViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import kotlinx.android.synthetic.main.view_recycler_view.*
import java.util.*

class NewsContentFragment():BaseFragment() {
    private var contentIndex = 0

    companion object{
        const val HEALTHY = 0
        const val Child = 1
        const val PUBLIC_WELFARE = 2
        @JvmStatic
        fun getInstance(arg:Int):NewsContentFragment{
            val fragment = NewsContentFragment()
            val args = Bundle()
            args.putInt("type",arg)
            fragment.arguments = args
            return fragment
        }
    }
    private lateinit var mViewModel: NewsViewModel
    private lateinit var mBinding:ViewRecyclerViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.view_recycler_view,container,false)
        mBinding.lifecycleOwner = this
        contentIndex = arguments?.getInt("type")?:0
        mViewModel = setViewModel(NewsViewModel::class.java,contentIndex.toString())
        return mBinding.root
    }

    private fun initRefreshView(){
        val footView:View = View.inflate(context,R.layout.view_more_load,null)
        newsRefresh.isTargetScrollWithLayout = false
        newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))
        newsRefresh.setOnPullRefreshListener(object :SuperSwipeRefreshLayout.OnPullRefreshListener{
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
        mViewModel.isRefreshEnd.observe(this, androidx.lifecycle.Observer {
            if (it)
                newsRefresh.isRefreshing = false
        })

        newsRefresh.setOnPushLoadMoreListener(object :SuperSwipeRefreshLayout.OnPushLoadMoreListener{
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
                mViewModel.onLoadNews()
            }
        })
        newsRefresh.setFooterView(footView)
        mViewModel.isLoadEnd.observe(this, androidx.lifecycle.Observer {
            if (it){
                newsRefresh.setLoadMore(false)
                setFootViewState(3,footView)
            }
        })
    }


    private fun initView(){
        initRefreshView()
        val manager = LinearLayoutManager(activity!!,LinearLayoutManager.VERTICAL,false)
        val adapter = NewsItemAdapter()
        recyclerView.layoutManager = manager
        recyclerView.adapter = adapter
        mViewModel.clearList.observe(this, androidx.lifecycle.Observer {
            adapter.newsList.clear()
            adapter.notifyDataSetChanged()
        })
        mViewModel.news.observe(this, androidx.lifecycle.Observer {
            for (item in it){
                adapter.newsList.add(item)
                adapter.notifyItemInserted(adapter.newsList.size - 1)
            }
        })
    }

    override fun onLazyLoad() {
        super.onLazyLoad()
        mViewModel.contentTypeIndex = contentIndex
        mViewModel.onLoadNews()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }


    /**
     * 0.距离不够
     * 1.松手加载
     * 2.正在加载
     * else.隐藏
     */
    private fun setFootViewState(state:Int,footView:View){
        (footView.findViewById<View>(R.id.moreLoadNo)).visibility = if (state == 0) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadOk)).visibility = if (state == 1) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadStart)).visibility = if (state == 2) View.VISIBLE else View.GONE
    }
}