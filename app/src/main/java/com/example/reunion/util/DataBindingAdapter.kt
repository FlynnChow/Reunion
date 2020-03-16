package com.example.reunion.util

import android.graphics.Bitmap
import android.media.Image
import android.net.Uri
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
    @BindingAdapter("image")
    fun loadImageViewCanNull(imageView:ImageView,url:String?){
        if (url!=null){
            Glide.with(imageView).load(url).apply(NormalUtil.getGlideOption()).into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("resource")
    fun loadImageFromResource(imageView:ImageView,url:Int?){
        if (url!=null){
            Glide.with(imageView).load(url).apply(NormalUtil.getGlideOption()).into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("head")
    fun loadHeaderView(imageView:ImageView,url:String?){
        if (url!=null &&url.isNotEmpty()){
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

    @JvmStatic
    @BindingAdapter("bitmap")
    fun loadImageView(imageView:ImageView,bitmap:Bitmap?){
        if (bitmap!=null){
            Glide.with(imageView).load(bitmap).apply(NormalUtil.getGlideHeaderOption()).into(imageView)
        }
    }

    @JvmStatic
    @BindingAdapter("uri")
    fun loadImageView(imageView:ImageView,uri:Uri?){
        if (uri!=null){
            Glide.with(imageView).load(uri).apply(NormalUtil.getGlideHeaderOption()).into(imageView)
        }
    }
}