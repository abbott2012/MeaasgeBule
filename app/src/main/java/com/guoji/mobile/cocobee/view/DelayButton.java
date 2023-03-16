package com.guoji.mobile.cocobee.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;

import com.bql.roundview.RoundTextView;
import com.bql.roundview.RoundViewDelegate;
import com.guoji.mobile.cocobee.R;


/**
 * 延迟的button，默认延迟60秒，用于获取验证码按钮上提示 <br>
 * Created by Cyarie on 2016/1/13.
 */
public class DelayButton extends RoundTextView {

    public static final String TAG = "DelayButton";

    /**
     * 设置的冷却时长
     */
    private int setDuration = 60;
    /**
     * 当前剩余冷却时长
     */
    private int remainTime;

    //    private OnClickListener mListener;
    // 正在计时时的背景色
    private int mGoBackground;
    // 父类设置属性的代理
    private RoundViewDelegate mDelegate;
    // 初始的背景色
    private int mBackgroundColor;

    public static final int MSG_GO = 0X0001;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GO:
                    if (remainTime > 0) {
                        setText("剩余" + remainTime + "秒");
                        remainTime--;
                        mHandler.sendEmptyMessageDelayed(MSG_GO, 1000);
                    } else {
                        mDelegate.setBackgroundColor(mBackgroundColor);
                        reset();
                    }
                    break;
            }
        }

    };


    public DelayButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DelayButton);
        setDuration = typedArray.getInteger(R.styleable.DelayButton_db_duration, 60);
        mGoBackground = typedArray.getColor(R.styleable.DelayButton_db_go_background, Color.GRAY);
        //切记recycle()
        typedArray.recycle();
        mDelegate = getDelegate();
        mBackgroundColor = mDelegate.getBackgroundColor();
        reset();
    }

    public DelayButton(Context context) {
        this(context, null);
    }

    @Override
    public RoundViewDelegate getDelegate() {
        return super.getDelegate();
    }

    /**
     * 重置状态
     */
    public void reset() {
        mHandler.removeMessages(MSG_GO);
        mHandler.removeCallbacksAndMessages(null);
        remainTime = setDuration;
        setText("获取验证码");
        setEnabled(true);
    }


    /**
     * 获取按钮点击的冷却时长
     */
    public int getDuration() {
        return remainTime;
    }

    /**
     * 设置按钮冷却时间, 默认冷却时间180秒 , 需要在点击之前设置时长。
     *
     * @param duration 冷却时长，单位毫秒
     */
    public void setDuration(int duration) {
        //        if (this.remainTime == setDuration)
        //            this.remainTime = duration;
        this.setDuration = duration;
    }


    @Override
    protected void onDetachedFromWindow() {
        mHandler.removeCallbacksAndMessages(null);
        remainTime = setDuration;

        super.onDetachedFromWindow();
    }

    public void startCountDown() {
        setEnabled(false);
        mDelegate.setBackgroundColor(mGoBackground);
        mHandler.sendEmptyMessage(MSG_GO);
    }

}
