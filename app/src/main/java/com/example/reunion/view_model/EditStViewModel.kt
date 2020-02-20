package com.example.reunion.view_model

import android.graphics.drawable.Drawable
import androidx.lifecycle.MutableLiveData
import com.example.reunion.MyApplication
import com.example.reunion.R
import com.example.reunion.base.BaseViewModel
import com.example.reunion.view.EditStActivity

class EditStViewModel:BaseViewModel() {
    val length = MutableLiveData(0)
    val content = MutableLiveData("")
    var contentLast = ""
    val title = MutableLiveData("")
    val change = MutableLiveData(false)
    var mode = 0

    fun updateNum(content:String,length:Int):String{
        val num = length - content.length
        return num.toString()
    }

    fun getSaveBg(change:Boolean):Drawable{
        if (mode == EditStActivity.MODE_NAME&&(content.value==null || content.value!!.isEmpty())){
            return MyApplication.resource().getDrawable(R.drawable.ripple_button_save_no)
        }
        if (change){
            return MyApplication.resource().getDrawable(R.drawable.ripple_button_save)
        }else
            return MyApplication.resource().getDrawable(R.drawable.ripple_button_save_no)
    }

    fun getSaveTextColor(change:Boolean):Int{
        if (mode == EditStActivity.MODE_NAME&&(content.value==null || content.value!!.isEmpty())){
            return MyApplication.resource().getColor(R.color.setting_save)
        }
        if (change){
            return MyApplication.resource().getColor(R.color.white)
        }else
            return MyApplication.resource().getColor(R.color.setting_save)
    }

    fun isSaveClickable(change:Boolean):Boolean{
        if (mode == EditStActivity.MODE_NAME&&(content.value==null || content.value!!.isEmpty())){
            return false
        }
        return change
    }
}