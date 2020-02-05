package com.example.reunion.util

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.reunion.R

object NormalUtil {
    fun isMobile(phone:String?):Boolean{
        if (phone==null||phone.length != 11)
            return false
        for (index in 0..1){
            val num = phone[index]
            if (index == 0&&num == '1')
                continue
            else if (num=='3'||num=='5'||num=='7'||num=='8')
                continue
            return false
        }
        return true
    }

    fun getGlideOption(skipMemoryCache:Boolean = true,diskCacheStrategy:Boolean = true) =
         RequestOptions()
            .placeholder(R.drawable.temp_icon)//占位图
            .error(R.drawable.temp_icon)//加载错误图
            .skipMemoryCache(!skipMemoryCache)
            .diskCacheStrategy(if (diskCacheStrategy) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE)
}