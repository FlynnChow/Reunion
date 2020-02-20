package com.example.reunion.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.example.reunion.R
import de.hdodenhof.circleimageview.CircleImageView

object DataBindingAdapter {
    @JvmStatic
    @BindingAdapter("url")
    fun loadImageView(imageView:ImageView,url:String?){
        if (url!=null){
            Glide.with(imageView).load(url).apply(NormalUtil.getGlideOption()).into(imageView)
        }else{
            Glide.with(imageView).load(R.drawable.load_error).into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("head")
    fun loadHeaderView(imageView:ImageView,url:String?){
        if (url!=null){
            Glide.with(imageView).load(url).apply(NormalUtil.getGlideHeaderOption()).into(imageView)
        }else{
            Glide.with(imageView).load(R.drawable.user).into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("head")
    fun loadHeaderView(imageView:CircleImageView,url:String?){
        if (url!=null){
            Glide.with(imageView).load(url).apply(NormalUtil.getGlideHeaderOption()).into(imageView)
        }else{
            Glide.with(imageView).load(R.drawable.user).into(imageView)
        }
    }
}