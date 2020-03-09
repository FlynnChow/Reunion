package com.example.reunion.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityFindOneBinding
import com.example.reunion.repostory.bean.FaceBean
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.view.adapter.FindOneAdapter
import com.example.reunion.view_model.FindOneViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FindOneActivity : BaseActivity() {
    private val minTime = 500L

    private lateinit var mBinding:ActivityFindOneBinding
    private val adapter = FindOneAdapter()

    private val mViewModel by lazy {
        setViewModel(this,FindOneViewModel::class.java)
    }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_find_one)
        mBinding.viewModel = mViewModel
        mBinding.activity = this
        mBinding.lifecycleOwner = this

        initView()
        initData()
    }

    private fun initData(){
        val path = intent.getStringExtra("picture")?:""
        var time = System.currentTimeMillis()
        if (path.isNotEmpty()){
            mViewModel.uploading.value = true
            mViewModel.loadText.value = resources.getString(R.string.picture_compressing)
            GlobalScope.launch(Dispatchers.IO) {
                val compressPath =
                    PictureHelper.instance.compressImage(this@FindOneActivity,path,size = 300)
                launch(Dispatchers.Main) {
                    time = minTime - System.currentTimeMillis() - time
                    if (time>0) delay(time)
                    mViewModel.path = compressPath
                    mViewModel.initFindOne()
                }
            }
        }else{
            mViewModel.pathEmptyError()
        }
    }

    private fun initView(){
        val manager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        mBinding.recyclerView.layoutManager = manager
        mBinding.recyclerView.adapter = adapter
        adapter.listener = { uid ->
            startActivity(Intent(this,MyTopicActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                putExtra("uid",uid)
            })
        }

        mViewModel.faceList.observe(this, Observer {
            adapter.list.addAll(it)
            adapter.notifyDataSetChanged()
        })
    }

    fun onBack(view:View){
        finish()
    }
}
