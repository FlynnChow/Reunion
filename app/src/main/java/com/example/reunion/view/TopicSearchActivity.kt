package com.example.reunion.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityTopicSearchBinding
import com.example.reunion.databinding.ItemSearchBinding
import com.example.reunion.view_model.SearchViewModel

class TopicSearchActivity : BaseActivity() {

    private lateinit var mBinding: ActivityTopicSearchBinding
    private val mViewModel by lazy { setViewModel(this, SearchViewModel::class.java) }
    private var isOpen = false

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_topic_search)
        mBinding.lifecycleOwner = this
        mBinding.viewModel = mViewModel
        mBinding.activity = this

        initView()
        mViewModel.obtainKeywords()

        mBinding.searchEdit.requestFocus()
    }

    private fun initView(){

        mBinding.searchEdit.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                onStartSearch()
            }
            true
        }

        mViewModel.keywords.observe(this, Observer {
            for (keyword in it){
                val textBinding: ItemSearchBinding = DataBindingUtil.inflate(layoutInflater,R.layout.item_search,null,false)
                textBinding.activity = this
                textBinding.textView.text = keyword
                mBinding.flowLayout.addView(textBinding.root)
            }
        })

        mViewModel.clearKeywords.observe(this, Observer {
            for (index in mBinding.flowLayout.childCount - 1 downTo 0){
                mBinding.flowLayout.removeViewAt(index)
            }
        })
    }


    fun setMaxRow(view:View){
        if (isOpen){
            mBinding.flowLayout.maxRows = 2
            mBinding.textHistory.text = resources.getString(R.string.search_open)
        }else{
            mBinding.flowLayout.maxRows = Int.MAX_VALUE
            mBinding.textHistory.text = resources.getString(R.string.search_close)
        }
        isOpen = !isOpen
    }

    fun onBack(view:View) = onBackPressed()

    private fun onStartSearch(){
        if (mViewModel.keyword.value == null || mViewModel.keyword.value!!.isEmpty()){
            toast("搜索关键词不能为空")
        }else{
            mViewModel.onSaveKeywordToDb()

            val pair = Pair<View,String>(mBinding.searchEdit,"search")
            val pair2 = Pair<View,String>(mBinding.searchImg,"searchImg")
            val compat: ActivityOptionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair,pair2)
            startActivity(Intent(this,TopicSearchListActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra("keyword",mViewModel.keyword.value)
            },compat.toBundle())
        }
    }

    fun onClickSearch(view:View){
        val keyword = (view as TextView).text.toString()
        mViewModel.keyword.value = keyword
        onStartSearch()
    }
}
