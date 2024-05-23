package com.athsoftware.hrm.views.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

public class FixBugsLayout extends RelativeLayout {
    public FixBugsLayout(Context context) {
        super(context);
    }

    public FixBugsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FixBugsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            //uncomment if you really want to see these errors
            //e.printStackTrace();
            return false;
        }
    }
}
