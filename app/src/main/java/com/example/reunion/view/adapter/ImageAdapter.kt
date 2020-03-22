package com.example.reunion.view.adapter

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reunion.R
import com.example.reunion.base.BaseViewHolder
import com.example.reunion.databinding.ItemHomeBinding
import com.example.reunion.databinding.ItemImageBinding
import com.example.reunion.databinding.ItemNewsBinding
import com.example.reunion.repostory.bean.NewsBean
import com.example.reunion.repostory.bean.TopicBean
import com.example.reunion.view.NewsActivity

class ImageAdapter():RecyclerView.Adapter<BaseViewHolder<ItemImageBinding>>() {
    val images:ArrayList<Image> = ArrayList()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ItemImageBinding> {
        val mBinding:ItemImageBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context),
            R.layout.item_image,parent,false)
        return BaseViewHolder(mBinding)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<ItemImageBinding>, position: Int) {
        val mBinding = holder.mBinding
        val image = images[position]
        if (image.type == Image.NORMAL_IMAGE){
            mBinding.isBigImage = false
            mBinding.normalImage.setImageBitmap(image.bitmap)
            mBinding.normalImage.scaleX = image.scale
            mBinding.normalImage.scaleY = image.scale
            mBinding.normalImage.pivotX = mBinding.normalImage.width*image.x
            mBinding.normalImage.pivotY = mBinding.normalImage.height*image.y
        }else if (image.type == Image.BIG_IMAGE){
            mBinding.isBigImage = true
            mBinding.bigImage.setImageBitmap(image.bitmap)
        }
        mBinding.executePendingBindings()
    }

    fun setScale(position:Int,newScale:Float){
        if (position < images.size){
            var scale = newScale
            if (scale > 2f) scale = 2f
            else if(scale < 1f) scale = 1f
            images[position].scale = scale
            notifyItemChanged(position)
        }
    }

    fun setPivot(position:Int,x:Float,y:Float){
        if (position < images.size){
            if (images[position].scale in 0.8f .. 1f){
                images[position].x = if (x<=0.25f) 0f else if (x>=0.75f) 1f else 0.5f
                images[position].y = if (y<=0.25f) 0f else if (y>=0.75f) 1f else 0.5f
                notifyItemChanged(position)
            }
        }
    }

    fun getScale(position:Int):Float{
        if (position > images.size)
            return 1f
        return images[position].scale
    }

    class Image{
        companion object{
            const val BIG_IMAGE = 0
            const val NORMAL_IMAGE = 1
        }
        var bitmap:Bitmap ?= null
        var scale = 1f
        var type:Int = 0
        var x = 0.5f
        var y = 0.5f
    }
}