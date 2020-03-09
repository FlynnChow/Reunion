package com.example.reunion.util

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.databinding.DataBindingUtil
import com.example.reunion.R
import com.example.reunion.databinding.DialogConfirmNormalBinding

object ViewUtil {

    fun hideInput(activity: Activity) {
        val inputMM = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val v = activity.window.peekDecorView()
        if (null != v) {
            inputMM.hideSoftInputFromWindow(v.windowToken, 0)
        }
    }

    fun showInput(activity: Activity,edit:EditText) {
        edit.setSelection(edit.text.toString().length)
        edit.requestFocus()
        val inputMM = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMM.showSoftInput(edit,0)
    }

    fun createNormalDialog(activity: Activity,message:String,ok:String,okListener:()->Unit,cancelListener:()->Unit):Dialog{
        val binding:DialogConfirmNormalBinding = DataBindingUtil.inflate(activity.layoutInflater, R.layout.dialog_confirm_normal,null,false)
        binding.message = message
        binding.ok = ok
        binding.dialogCancel.setOnClickListener {
            cancelListener.invoke()
        }
        binding.dialogOk.setOnClickListener {
            okListener.invoke()
        }

        val dialog =  Dialog(activity,R.style.CustomizeDialog)
        dialog.setContentView(binding.root)

        val window = dialog.window
        val layoutParams = window?.attributes
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        layoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        layoutParams?.gravity = Gravity.BOTTOM
        layoutParams?.y = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,20f,activity.resources.displayMetrics).toInt()
        window?.setWindowAnimations(R.style.DialogAnim)
        dialog.show()
        dialog.dismiss()
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }
}