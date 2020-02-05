package com.example.reunion.base
import android.Manifest
import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

abstract class BaseActivity: AppCompatActivity() {

    private val MUST_PERMISSION = 1000

    private val NORM_PERMISSION = 1001


    @Suppress("UNCHECKED_CAST")
    protected fun <T : BaseViewModel> setViewModel(owner: BaseActivity,modelClass: Class<T>):T{
        val mViewModel = ViewModelProvider(owner).get(modelClass)
        mViewModel.error.observe(owner , Observer {
            toast(it.message)
        })
        mViewModel.toast.observe(owner, Observer {
            toast(it)
        })
        return mViewModel
    }

    protected fun toast(msg:String?,duration:Int = Toast.LENGTH_SHORT){
        Toast.makeText(this,msg,duration).show()
    }

    private fun initActivity(){
        checkPermission()
    }

    /**
     * 可选的权限，拒绝不会被finish
     */
    private fun checkPermission(){
        val permissions = ArrayList<String>()
        val perTable = arrayOf<String>()
        for (permission in perTable){
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
                permissions.add(permission)
        }
        if (permissions.isNotEmpty())
            ActivityCompat.requestPermissions(this,permissions.toTypedArray(),NORM_PERMISSION)
    }

    /**
     * 必须的权限，拒绝会被finish
     */
    private fun checkMustPermission(){
        val permissions = ArrayList<String>()
        val perTable = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
        for (permission in perTable){
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
                permissions.add(permission)
        }
        if (permissions.isNotEmpty())
            ActivityCompat.requestPermissions(this,permissions.toTypedArray(),MUST_PERMISSION)
    }


    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initActivity()
        checkMustPermission()
        create(savedInstanceState)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty()){
            for (index in permissions.indices){
                if (grantResults[index] != PackageManager.PERMISSION_GRANTED && requestCode == MUST_PERMISSION){
                   onMustPermissionFail(permissions[index])
                }else if(grantResults[index] != PackageManager.PERMISSION_GRANTED){
                    onNormalPermissionFail(permissions[index])
                }
            }
        }
    }

    protected open fun onMustPermissionFail(permission:String){
        toast("必须申请权限才能使用该功能")
        finish()
    }

    protected open fun onNormalPermissionFail(permission:String){}

    abstract fun create(savedInstanceState: Bundle?)
}