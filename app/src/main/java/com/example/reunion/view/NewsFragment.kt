package com.example.reunion.view

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.FragmentNewsBinding
import com.example.reunion.view.adapter.NewsAdapter
import com.example.reunion.view_model.HomeViewModel
import com.example.reunion.view_model.NewsViewModel
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment:BaseFragment() {
    private lateinit var mBinding:FragmentNewsBinding
    private val mViewModel: NewsViewModel by lazy {
        setViewModel(this, NewsViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_news,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = mViewModel
    }

    private fun initFragments(){
        val healthFragment = NewsContentFragment(NewsContentFragment.HEALTHY)
        val childFragment = NewsContentFragment(NewsContentFragment.Child)
        val publicFragment = NewsContentFragment(NewsContentFragment.PUBLIC_WELFARE)
        val fragments:Array<Fragment> = arrayOf<Fragment>(healthFragment,childFragment,publicFragment)
        val adapter = NewsAdapter(fragmentManager!!,fragments,resources.getStringArray(R.array.newsTitle))
        newsViewPager.adapter = adapter
        newsTab.setupWithViewPager(newsViewPager)
        if (newsViewPager.currentItem != mViewModel.currentIndex){
            newsViewPager.currentItem = mViewModel.currentIndex
        }
    }

    override fun onLazyLoad() {
        initFragments()
    }

    override fun onDestroyView() {
        mViewModel.currentIndex = newsViewPager.currentItem
        super.onDestroyView()
    }
}