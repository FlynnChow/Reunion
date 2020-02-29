package com.example.reunion.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityFindOneBinding
import com.example.reunion.repostory.local_resource.PictureHelper
import com.example.reunion.view_model.FindOneViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FindOneActivity : BaseActivity() {

    private lateinit var mBinding:ActivityFindOneBinding

    private val mViewModel by lazy {
        setViewModel(this,FindOneViewModel::class.java)
    }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_find_one)
        mBinding.viewModel = mViewModel
        mBinding.activity = this
        mBinding.lifecycleOwner = this

        initData()
    }

    private fun initData(){
        val path = intent.getStringExtra("picture")?:""
        if (path.isNotEmpty()){
            mViewModel.uploading.value = true
            mViewModel.loadText.value = resources.getString(R.string.picture_compressing)
            GlobalScope.launch(Dispatchers.IO) {
                val compressPath =
                    PictureHelper.instance.compressImage(this@FindOneActivity,path,size = 300)
                launch(Dispatchers.Main) {
                    delay(500)
                    mViewModel.path = compressPath
                    mViewModel.group = intent.getStringExtra("group")?:"other"
                    mViewModel.initFindOne()
                }
            }
        }else{
            mViewModel.pathEmptyError()
        }
    }

    fun onBack(view:View){
        finish()
    }
}
