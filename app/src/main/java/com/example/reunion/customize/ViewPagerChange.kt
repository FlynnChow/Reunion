package com.example.reunion.customize

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import kotlin.math.abs

class ViewPagerChange @JvmOverloads constructor(context:Context, attributeSet: AttributeSet? = null):ViewPager(context,attributeSet) {

    var downX = 0f
    var downY = 0f
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action){
            MotionEvent.ACTION_DOWN->{
                downX = ev.x
                downY = ev.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE->{
                val disX = abs(ev.x - downX)
                val disY = abs(ev.y - downY)
                if (currentItem>=1){
                    parent.requestDisallowInterceptTouchEvent(true)
                }else if(disY > disX){
                    parent.requestDisallowInterceptTouchEvent(true)
                }else if(ev.x < downX){
                    parent.requestDisallowInterceptTouchEvent(true)
                }else{
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            MotionEvent.ACTION_UP ->{

            }
            MotionEvent.ACTION_CANCEL->{
                parent.requestDisallowInterceptTouchEvent(false)
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}