package com.example.reunion.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivitySearchUserBinding
import com.example.reunion.util.ViewUtil
import com.example.reunion.view.adapter.SearchUserAdapter
import com.example.reunion.view_model.SearchUserViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout

class SearchUserActivity : BaseActivity() {

    private lateinit var mBinding:ActivitySearchUserBinding
    private val mViewModel by lazy { setViewModel(this, SearchUserViewModel::class.java) }

    private val adapter = SearchUserAdapter(){
        startActivity(Intent(this,MyTopicActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("uid",it)
        })
    }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_search_user)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.activity = this

        val keyword = intent.getStringExtra("keyword")?:""
        mViewModel.keyword.value = keyword

        initView()
    }

    private fun initView(){
        mBinding.searchEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                adapter.users.clear()
                adapter.notifyDataSetChanged()
                mViewModel.onSearch()
            }
            true
        }

        val manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter

        initRefreshView()

        ViewUtil.showInput(this,mBinding.searchEdit)
        mBinding.searchEdit.requestFocus()

        mViewModel.userData.observe(this, Observer {
            for (item in it){
                adapter.users.add(item)
                adapter.notifyItemInserted(adapter.users.size - 1)
            }
        })
    }

    private fun initRefreshView(){
        val footView:View = View.inflate(this,R.layout.view_more_load,null)
        mBinding.newsRefresh.isTargetScrollWithLayout = false
        mBinding.newsRefresh.setDefaultCircleProgressColor(resources.getColor(R.color.mainColor))

        mBinding.newsRefresh.isEnabled = false

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
    }

    private fun setFootViewState(state:Int,footView:View){
        (footView.findViewById<View>(R.id.moreLoadNo)).visibility = if (state == 0) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadOk)).visibility = if (state == 1) View.VISIBLE else View.GONE
        (footView.findViewById<View>(R.id.moreLoadStart)).visibility = if (state == 2) View.VISIBLE else View.GONE
    }

    fun onBack(view: View) = onBackPressed()
}
