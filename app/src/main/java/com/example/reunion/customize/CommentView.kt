package com.example.reunion.customize

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.NestedScrollView

class CommentView @JvmOverloads constructor(context:Context, attrs:AttributeSet? = null, defStyleAttr:Int = 0): FrameLayout(context,attrs,defStyleAttr) {
    var intercepted = false
    var listener:((Float,Boolean)->Unit)? = null
    var scrollView: NestedScrollView? = null
    var listener2:((MotionEvent?)->Unit)? = null
    /**
     * 0:不拦截
     * 1：拦截
     * 2：不确定
     */
    var isIntercept = 0
    var startY = 0f

    var moveY = 0f

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        when(ev?.action){
            MotionEvent.ACTION_DOWN->{
                intercepted = false
                if (scrollView == null||scrollView!!.scrollY!=0){
                    isIntercept = 0
                }else{
                    isIntercept = 2
                }
                startY = ev.y
                return intercepted
            }
            MotionEvent.ACTION_MOVE->{
                moveY = ev.y
                if (isIntercept == 0){
                    return super.onInterceptTouchEvent(ev)
                }else if (isIntercept == 2){
                    if (moveY > startY){
                        isIntercept = 1
                        return true
                    }else{
                        isIntercept = 0
                        return super.onInterceptTouchEvent(ev)
                    }
                }else if(isIntercept == 1){
                    return true
                }
            }
            MotionEvent.ACTION_UP->{
                return false
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        val dispatch = super.dispatchTouchEvent(ev)
        listener2?.invoke(ev)
        when (ev?.action){
            MotionEvent.ACTION_MOVE->{
                moveY = ev.y
                if (isIntercept == 1){
                    var move = moveY-startY
                    if (move <= 0) move = 0f
                    listener?.invoke(move,false)
                }
            }
            MotionEvent.ACTION_UP->{
                moveY = ev.y
                if (isIntercept == 1){
                    var move = moveY-startY
                    if (move <= 0) move = 0f
                    listener?.invoke(move,true)
                }
            }
        }
        return dispatch
    }

    fun setScrollListener(scrollView: NestedScrollView, listener:(Float, Boolean)->Unit){
        this.listener = listener
        this.scrollView = scrollView
    }

    fun setDispatchListener(listener:(ev: MotionEvent?)->Unit){
        this.listener2 = listener
    }
}