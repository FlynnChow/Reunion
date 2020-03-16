package com.example.reunion.base
import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

abstract class BaseActivity: AppCompatActivity() {

    private val MUST_PERMISSION = 1000

    private val NORM_PERMISSION = 1001

    val mustPermissions:ArrayList<String> = ArrayList()


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

    fun toast(msg:String?,duration:Int = Toast.LENGTH_SHORT){
        Toast.makeText(this,msg,duration).show()
    }

    private fun initActivity(){

    }

    /**
     * 必须的权限，拒绝会被finish
     */
    private fun checkMustPermission(){
        val perTable = arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.READ_PHONE_STATE)
        for (permission in perTable){
            if (ContextCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
                mustPermissions.add(permission)
        }
        if (mustPermissions.isNotEmpty())
            ActivityCompat.requestPermissions(this,mustPermissions.toTypedArray(),MUST_PERMISSION)
    }

    protected open fun insertMustPermission():ArrayList<String>?{ return null }

    private fun insertMustPermission(mPermissions:ArrayList<String>?){
        mPermissions?.apply {
            val mustPermissions = ArrayList<String>()
            for (permission in mPermissions){
                if (ContextCompat.checkSelfPermission(this@BaseActivity,permission) != PackageManager.PERMISSION_GRANTED)
                    mustPermissions.add(permission)
            }
            this@BaseActivity.mustPermissions.addAll(mustPermissions)
        }
    }


    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        insertMustPermission(insertMustPermission())
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
            if (allPermissionsGranted()){
                permissionCheckPass()
            }
        }
    }

    protected open fun onMustPermissionFail(permission:String){
        toast("必须接受权限才能使用该功能")
        finish()
    }

    protected fun allPermissionsGranted() = mustPermissions.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onResume() {
        sendBroadcast(Intent("reunion.application.intoForeground"))
        super.onResume()
    }

    protected open fun onNormalPermissionFail(permission:String){}

    protected open fun permissionCheckPass(){}

    abstract fun create(savedInstanceState: Bundle?)
}