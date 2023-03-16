package com.guoji.mobile.cocobee.view;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**禁止上下滑动的gridview
 * Created by _H_JY on 2016/3/17.
 */
public class MySpinner extends android.support.v7.widget.AppCompatSpinner {

    public MySpinner(Context context) {
        super(context);
    }

    public MySpinner(Context context, int mode) {
        super(context, mode);
    }

    public MySpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MySpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }
}
