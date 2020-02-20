package com.example.reunion.util

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

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
}