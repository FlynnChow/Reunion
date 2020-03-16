package com.example.reunion.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet
import android.view.View
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.base.BaseActivity
import com.example.reunion.databinding.ActivityMoreMessageBinding
import com.example.reunion.repostory.bean.User
import com.example.reunion.view_model.MoreMessageViewModel

class MoreMessageActivity : BaseActivity() {

    override fun create(savedInstanceState: Bundle?) {
        val mBinding:ActivityMoreMessageBinding = DataBindingUtil.setContentView(this, R.layout.activity_more_message)
        mBinding.lifecycleOwner = this
        mBinding.activity = this
        mBinding.viewModel = setViewModel(this,MoreMessageViewModel::class.java)

        val bean: User.Data = intent.getParcelableExtra("userBean")?:return
        mBinding.viewModel!!.initUser(bean)
    }

    fun onBack(view: View) = onBackPressed()
}
