package com.example.reunion.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

object DataBindingAdapter {
    @JvmStatic
    @BindingAdapter("url")
    fun loadImageView(imageView:ImageView,url:String?){
        if (url!=null){
            Glide.with(imageView).load(url).apply(NormalUtil.getGlideOption()).into(imageView)
        }else{
            //url空时候加载的临时图片
        }
    }
}