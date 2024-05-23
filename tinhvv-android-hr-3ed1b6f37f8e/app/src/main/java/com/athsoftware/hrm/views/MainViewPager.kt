package com.athsoftware.hrm.views

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.athsoftware.hrm.views.widget.ZoomView

/**
 * Created by tinhvv on 11/13/18.
 */

class MainViewPager: ViewPager {

    var touchDelegate: ((ev: MotionEvent?) -> Unit)? = null

    constructor(context: Context): super(context) {
    }
    constructor(context: Context, attrs: AttributeSet): super(context, attrs){
    }

//    override fun onTouchEvent(ev: MotionEvent?): Boolean {
//        touchDelegate?.invoke(ev)
//        if (ev?.pointerCount ?: 0 <= 1) {
//            super.onTouchEvent(ev)
//            return true
//        } else {
//            return super.onTouchEvent(ev)
//        }
//    }

//    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
////        children.forEach { it.onInterceptTouchEvent(ev) }
////        if (ev.pointerCount <= 1) {
////            return true
////        } else {
////            return super.onInterceptTouchEvent(ev)
////        }
//        super.onInterceptTouchEvent(ev)
//        return true
//    }

    override fun canScroll(v: View?, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is ZoomView) {
            return v.canScrollHorizontally(dx)
        }
        return super.canScroll(v, checkV, dx, x, y)
    }
}