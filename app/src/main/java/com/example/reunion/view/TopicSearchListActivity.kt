package com.example.reunion.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityTopicSearchListBinding
import com.example.reunion.util.ViewUtil
import com.example.reunion.view.adapter.TopicItemAdapter
import com.example.reunion.view_model.SearchViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout

class TopicSearchListActivity : BaseActivity() {

    private lateinit var mBinding:ActivityTopicSearchListBinding
    private val mViewModel by lazy { setViewModel(this, SearchViewModel::class.java) }

    private val adapter = TopicItemAdapter{
        startActivity(Intent(this,TopicActivity::class.java).apply {
            putExtra("data",it)
        })
    }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_topic_search_list)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.activity = this

        val keyword = intent.getStringExtra("keyword")?:""
        mViewModel.keyword.value = keyword
        mViewModel.onSearch()

        initView()
    }

    private fun initIntoAnim(){
        ViewCompat.setTransitionName(mBinding.searchEdit, "search")
        ViewCompat.setTransitionName(mBinding.searchImg, "searchImg")

        val set = TransitionSet()
        set.addTransition(ChangeBounds())
        set.addTransition(ChangeImageTransform())
        set.addTransition(ChangeTransform())
        set.addTarget(mBinding.searchImg)
        set.addTarget(mBinding.searchEdit)

        window.sharedElementEnterTransition = set
    }

    private fun initView(){
        initIntoAnim()
        mBinding.searchEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                mViewModel.onSearch()
            }
            true
        }

        val manager = GridLayoutManager(this,2)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter
        initRefreshView()
    }

    fun onBack(view: View) = onBackPressed()

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
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
}
