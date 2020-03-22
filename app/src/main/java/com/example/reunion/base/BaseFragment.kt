package com.example.reunion.base

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.reunion.view.FindFragment

abstract class BaseFragment:Fragment() {

    private var isFirstLoad = true

    var savedInstanceState: Bundle? = null

    @Suppress("UNCHECKED_CAST")
    protected fun <T : BaseViewModel> setViewModel(owner: BaseActivity, modelClass: Class<T>):T{
        val mViewModel = ViewModelProvider(owner).get(modelClass)
        return mViewModel
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T : BaseViewModel> setViewModel(modelClass: Class<T>,key:String):T{
        val mViewModel = ViewModelProvider(requireActivity()).get(key,modelClass)
        mViewModel.error.observe(this, Observer {
            toast(it.message)
        })
        mViewModel.toast.observe(this, Observer {
            toast(it)
        })
        return mViewModel
    }


    protected fun toast(msg:String?,duration:Int = Toast.LENGTH_SHORT){
        Toast.makeText(activity,msg,duration).show()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        this.savedInstanceState = savedInstanceState
    }


    override fun onResume() {
        super.onResume()
        if (isFirstLoad){
            isFirstLoad = false
            onLazyLoad()
        }
    }

    /**
     * 这里只能进行有关数据的操作
     * 不能在这里初始化view，否则会发生异常
     */
    protected open fun onLazyLoad(){

    }
}