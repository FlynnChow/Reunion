package com.example.reunion.base

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

abstract class BaseActivity: AppCompatActivity() {
    protected lateinit var mViewModel:BaseViewModel

    private fun <T : BaseViewModel> setViewModel(owner: ViewModelStoreOwner,modelClass: Class<T>){
        mViewModel = ViewModelProvider(owner).get(modelClass)
    }

    private fun <T : BaseApplicationViewModel> setAppViewModel(owner: ViewModelStoreOwner,modelClass: Class<T>){
        mViewModel = ViewModelProvider(owner).get(modelClass)
    }

    private fun toast(msg:String?,duration:Int = Toast.LENGTH_SHORT){
        Toast.makeText(this,msg,duration).show()
    }

    private fun initActivity(){
        showError()
        checkPermission()
    }

    /**
     * 可选权限，拒绝不会被finish
     */
    private fun checkPermission(){

    }

    /**
     * 必须权限，拒绝会被finish
     */
    private fun checkMustPermission(permissions:String){

    }

    /**
     * 直接显示抛出的一些异常
     */
    private fun showError(){
        mViewModel.error.observe(this, Observer {
            toast("异常："+it.message)
        })
    }

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        create(savedInstanceState)
    }

    abstract fun create(savedInstanceState: Bundle?)
}