package com.zhilianbao.marqueeview;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bql.utils.CheckUtils;
import com.bql.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: MarqueeView <br>
 * Description: 垂直跑马灯翻页广告View  <br>
 * Author: Cyarie <br>
 * Created: 2016/10/17 20:25 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class MarqueeView extends ViewFlipper {

    private Context mContext;
    private List<String> notices;
    private boolean isSetAnimDuration = false;
    private OnItemClickListener onItemClickListener;

    private int interval = 3000;
    private int animDuration = 500;
    private int textSize = 14;
    private int textColor = 0xffffffff;

    private boolean singleLine = true;
    private int gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
    private static final int TEXT_GRAVITY_LEFT = 0, TEXT_GRAVITY_CENTER = 1, TEXT_GRAVITY_RIGHT = 2;

    private boolean isShowAnim = true;

    private int inAnimResId;

    private int outAnimResId;

    public MarqueeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mContext = context;
        if (notices == null) {
            notices = new ArrayList<>();
        }

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.MarqueeView, defStyleAttr, 0);
        interval = typedArray.getInteger(R.styleable.MarqueeView_mv_interval, interval);
        isShowAnim = typedArray.getBoolean(R.styleable.MarqueeView_mv_showAnim, isShowAnim);
        inAnimResId = typedArray.getResourceId(R.styleable.MarqueeView_mv_inAnim, R.anim.anim_marquee_in);
        outAnimResId = typedArray.getResourceId(R.styleable.MarqueeView_mv_outAnim, R.anim.anim_marquee_out);
        isSetAnimDuration = typedArray.hasValue(R.styleable.MarqueeView_mv_animDuration);
        singleLine = typedArray.getBoolean(R.styleable.MarqueeView_mv_singleLine, singleLine);
        animDuration = typedArray.getInteger(R.styleable.MarqueeView_mv_animDuration, animDuration);
        if (typedArray.hasValue(R.styleable.MarqueeView_mv_textSize)) {
            textSize = (int) typedArray.getDimension(R.styleable.MarqueeView_mv_textSize, textSize);
            textSize = DeviceUtils.px2sp(mContext, textSize);
        }
        textColor = typedArray.getColor(R.styleable.MarqueeView_mv_textColor, textColor);
        int gravityType = typedArray.getInt(R.styleable.MarqueeView_mv_gravity, TEXT_GRAVITY_LEFT);
        switch (gravityType) {
            case TEXT_GRAVITY_CENTER:
                gravity = Gravity.CENTER;
                break;
            case TEXT_GRAVITY_RIGHT:
                gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
                break;
        }
        typedArray.recycle();

        setFlipInterval(interval);
        if (isShowAnim) {
            Animation animIn = AnimationUtils.loadAnimation(mContext, inAnimResId);
            if (isSetAnimDuration)
                animIn.setDuration(animDuration);
            setInAnimation(animIn);

            Animation animOut = AnimationUtils.loadAnimation(mContext, outAnimResId);
            if (isSetAnimDuration)
                animOut.setDuration(animDuration);
            setOutAnimation(animOut);
        } else {
            setInAnimation(null);
            setOutAnimation(null);
        }
        setAnimateFirstView(false);

    }

    /**
     * 根据广告字符串启动轮播
     *
     * @param notice
     */
    public void startWithText(final String notice) {
        if (CheckUtils.isEmpty(notice))
            return;
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                startWithFixedWidth(notice, getWidth());
            }
        });
    }


    /**
     * 根据广告字符串列表启动轮播
     *
     * @param notices
     */
    public void startWithList(List<String> notices) {
        setNotices(notices);
        start();
    }


    /**
     * 根据宽度和广告字符串启动轮播
     *
     * @param notice
     * @param width
     */
    private void startWithFixedWidth(String notice, int width) {
        int noticeLength = notice.length();
        int dpW = DeviceUtils.px2dp(mContext, width);
        int limit = dpW / textSize;
        if (dpW == 0) {
            throw new RuntimeException("Please set MarqueeView width !");
        }

        if (noticeLength <= limit) {
            notices.add(notice);
        } else {
            int size = noticeLength / limit + (noticeLength % limit != 0 ? 1 : 0);
            for (int i = 0; i < size; i++) {
                int startIndex = i * limit;
                int endIndex = ((i + 1) * limit >= noticeLength ? noticeLength : (i + 1) * limit);
                notices.add(notice.substring(startIndex, endIndex));
            }
        }
        start();
    }

    /**
     * 启动轮播
     *
     * @return
     */
    public boolean start() {
        if (CheckUtils.isEmpty(notices))
            return false;
        removeAllViews();
        for (int i = 0; i < notices.size(); i++) {
            final TextView textView = createTextView(notices.get(i), i);
            final int finalI = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(finalI, textView);
                    }
                }
            });
            addView(textView);
        }
        if (notices.size() > 1) {
            startFlipping();
        }
        return true;
    }

    public void stop() {
        if (isFlipping()) {
            stopFlipping();
        }
    }

    /**
     * 创建ViewFlipper下的TextView
     *
     * @param text
     * @param position
     * @return
     */
    private TextView createTextView(String text, int position) {
        TextView tv = new TextView(mContext);
        tv.setGravity(gravity);
        tv.setText(text);
        tv.setTextColor(textColor);
        tv.setTextSize(textSize);
        tv.setSingleLine(singleLine);
        if (singleLine)
            tv.setEllipsize(TextUtils.TruncateAt.END);
        tv.setTag(position);
        return tv;
    }

    public int getPosition() {
        return (int) getCurrentView().getTag();
    }

    public List<String> getNotices() {
        return notices;
    }

    public void setNotices(List<String> notices) {
        this.notices = notices;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, TextView textView);
    }
}
