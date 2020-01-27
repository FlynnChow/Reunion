package com.example.reunion.base

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

abstract class BaseFragment:Fragment() {

    //使用懒加载
    var isLazyLoad = false

    var isViewCreated = false

    var savedInstanceState: Bundle? = null

    @Suppress("UNCHECKED_CAST")
    protected fun <T : BaseViewModel> setViewModel(owner: ViewModelStoreOwner, modelClass: Class<T>):T{
        val mViewModel = ViewModelProvider(owner).get(modelClass)
        mViewModel.error.observe(this, Observer {
            toast("异常: "+it.message)
        })
        return mViewModel
    }


    protected fun toast(msg:String?,duration:Int = Toast.LENGTH_SHORT){
        Toast.makeText(activity,msg,duration).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        if (getUserVisibleHint()&&isLazyLoad&&isViewCreated)
            onLazyLoad(savedInstanceState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser&&isLazyLoad&&isViewCreated)
            onLazyLoad(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        isViewCreated = true
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    protected fun onLazyLoad(savedInstanceState: Bundle?){

    }
}