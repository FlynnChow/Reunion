package com.example.reunion.view

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityEditStBinding
import com.example.reunion.view_model.EditStViewModel
import kotlinx.android.synthetic.main.activity_edit_st.*

class EditStActivity : BaseActivity() {
    companion object{
        val MODE_NAME = 0
        val MODE_REAL_NAME = 1
        val MODE_SIGNATURE = 2
    }
    lateinit var mBinding:ActivityEditStBinding
    private var mode = -1
    val mViewModel by lazy { setViewModel(this,EditStViewModel::class.java) }

    override fun create(savedInstanceState: Bundle?) {
        mBinding = DataBindingUtil.setContentView(this,R.layout.activity_edit_st)
        mBinding.viewModel = mViewModel
        mBinding.activity = this
        mBinding.lifecycleOwner = this
        init(intent.getIntExtra("mode",-1),intent.getStringExtra("content"))
    }

    fun init(mode:Int,content:String?){
        this.mode = mode
        mViewModel.mode = mode
        mViewModel.length.observe(this, Observer {
            stEdit.filters = arrayOf(InputFilter.LengthFilter(it))
        })
        mViewModel.content.observe(this, Observer {
            mViewModel.change.value = (it != mViewModel.contentLast)
        })
        when(mode){
            MODE_NAME->{
                mViewModel.length.value = 20
                mViewModel.title.value = "更改${resources.getString(R.string.setting_name)}"
            }
            MODE_REAL_NAME->{
                mViewModel.length.value = 20
                mViewModel.title.value = "更改${resources.getString(R.string.setting_real_name)}"
            }
            MODE_SIGNATURE->{
                mViewModel.length.value = 80
                mViewModel.title.value = "更改${resources.getString(R.string.signature)}"
            }
        }
        mViewModel.content.value = content?:""
        mViewModel.contentLast = content?:""
    }

    fun onBackClick(view:View){
        finish()
    }

    fun onClickSave(view:View){
        val data = Intent()
        data.putExtra("content",mViewModel.content.value?:"")
        setResult(Activity.RESULT_OK,data)
        finish()
    }
}
