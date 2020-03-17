package com.example.reunion.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.base.BaseFragment
import com.example.reunion.databinding.ActivityFollowBinding
import com.example.reunion.databinding.FragmentUserListBinding
import com.example.reunion.view.adapter.UserAdapter
import com.example.reunion.view_model.UserViewModel
import com.github.nuptboyzhb.lib.SuperSwipeRefreshLayout
import kotlinx.android.synthetic.main.view_recycler_view.*

class UserFragment(private val type:String = "") : BaseFragment() {
    private lateinit var mBinding:FragmentUserListBinding
    private val mViewModel by lazy { setViewModel(this,UserViewModel::class.java) }
    private val adapter = UserAdapter{
        startActivity(Intent(activity,MyTopicActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            putExtra("uid",it)
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user_list,container,false)
        mBinding.lifecycleOwner = this
        return mBinding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initView()
    }

    private fun initView(){
        val manager = LinearLayoutManager(activity,LinearLayoutManager.VERTICAL,false)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter

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

        mViewModel.userData.observe(this, Observer {
            adapter.addNewUser(it)
        })

        mViewModel.refreshing.observe(this, androidx.lifecycle.Observer {
            if (!it)
                newsRefresh.isRefreshing = false
            else{
                adapter.users.clear()
                adapter.notifyDataSetChanged()
            }
        })

        mBinding.sideBar.setOnStrSelectCallBack { index, selectStr ->
            val position = adapter.findPingYinPosition(selectStr)
            mBinding.recyclerView.scrollToPosition(position)
        }
    }

}
