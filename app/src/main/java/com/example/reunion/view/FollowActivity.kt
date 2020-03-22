package com.example.reunion.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityFollowBinding
import com.example.reunion.view.adapter.FollowAdapter
import com.example.reunion.view_model.UserViewModel
import kotlinx.android.synthetic.main.activity_follow.view.*

class FollowActivity : BaseActivity() {
    private lateinit var mBinding:ActivityFollowBinding
    private val mViewModel by lazy { setViewModel(this,UserViewModel::class.java) }
    private val followFragment by lazy { UserFragment.getInstance("follow") }
    private val fansFragment by lazy { UserFragment.getInstance("fans") }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_follow)
        mBinding.viewModel = mViewModel
        mBinding.activity = this
        mBinding.lifecycleOwner = this

        initView()
    }

    private fun initView(){
        val adapter = FollowAdapter(supportFragmentManager, arrayListOf(followFragment,fansFragment))
        mBinding.viewPager.adapter = adapter
        mBinding.tabLayout.setViewPager(mBinding.viewPager)
    }

    fun onBack(view: View) = onBackPressed()

}
