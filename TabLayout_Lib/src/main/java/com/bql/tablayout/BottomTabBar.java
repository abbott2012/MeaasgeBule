package com.bql.tablayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.tablayout.listener.CustomTabEntity;
import com.bql.tablayout.listener.OnTabClickListener;
import com.bql.tablayout.listener.OnTabSelectListener;
import com.bql.tablayout.utils.UnReadMsgUtils;
import com.bql.tablayout.widget.MsgView;

import java.util.ArrayList;

/**
 * ClassName: BottomTabBar <br>
 * Description: 自定义底部Tab<br>
 * Author: Cyarie <br>
 * Created: 2016/7/15 14:05 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class BottomTabBar extends FrameLayout {

    private Context mContext;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private LinearLayout mTabsContainer;
    private int mCurrentTab;
    private int mLastTab;
    private int mTabCount;

    private Paint mRectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mDividerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint mTrianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Path mTrianglePath = new Path();


    /**
     * underline
     */
    private int mUnderlineColor;
    private float mUnderlineHeight;


    /**
     * title
     */
    private float mTextsize;
    private int mTextSelectColor;
    private int mTextUnselectColor;

    /**
     * icon
     */
    private float mIconMargin;

    private int mHeight;

    public BottomTabBar(Context context) {
        this(context, null, 0);
    }

    public BottomTabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BottomTabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);//重写onDraw方法,需要调用这个方法来清除flag
        setClipChildren(false);
        setClipToPadding(false);
        this.mContext = context;
        mTabsContainer = new LinearLayout(context);
        addView(mTabsContainer);
        obtainAttributes(context, attrs);
        //get layout_height
        String height = attrs.getAttributeValue("http://schemas.android.com/apk/res/android", "layout_height");
        if (height.equals(ViewGroup.LayoutParams.MATCH_PARENT + "")) {
        } else if (height.equals(ViewGroup.LayoutParams.WRAP_CONTENT + "")) {
        } else {
            int[] systemAttrs = {android.R.attr.layout_height};
            TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);
            mHeight = a.getDimensionPixelSize(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            a.recycle();
        }
    }


    /**
     * 获取XML设置的属性
     *
     * @param context
     * @param attrs
     */
    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CommonTabLayout);
        mUnderlineColor = ta.getColor(R.styleable.CommonTabLayout_tl_underline_color, Color.parseColor("#ffffff"));
        mUnderlineHeight = ta.getDimension(R.styleable.CommonTabLayout_tl_underline_height, dp2px(0));
        mTextsize = ta.getDimension(R.styleable.CommonTabLayout_tl_textsize, sp2px(13f));
        mTextSelectColor = ta.getColor(R.styleable.CommonTabLayout_tl_textSelectColor, Color.parseColor("#ffffff"));
        mTextUnselectColor = ta.getColor(R.styleable.CommonTabLayout_tl_textUnselectColor, Color.parseColor("#AAffffff"));
        mIconMargin = ta.getDimension(R.styleable.CommonTabLayout_tl_iconMargin, dp2px(2.5f));
        ta.recycle();
    }

    /**
     * 设置Tab数据  此处特殊  共有5个Tab  0  1  2  3  4  中间的Tab只有图标没有文字说明 且点击进行页面跳转
     *
     * @param tabEntities
     */
    public void setTabData(ArrayList<CustomTabEntity> tabEntities) {
        if (tabEntities == null || tabEntities.size() == 0)
            throw new IllegalStateException("TabEntities can not be NULL or EMPTY !");
        if (tabEntities.size() > 5)
            throw new IllegalStateException("TabEntities's size must be 5");
        this.mTabEntities.clear();
        this.mTabEntities.addAll(tabEntities);
        notifyDataSetChanged();
    }

    /**
     * 更新Tab数据
     */
    public void notifyDataSetChanged() {
        mTabsContainer.removeAllViews();
        this.mTabCount = mTabEntities.size();
        View tabView;
        for (int i = 0; i < mTabCount; i++) {
            tabView = View.inflate(mContext, R.layout.layout_tab_top, null);
            tabView.setTag(i);
            addTab(i, tabView);
        }

        updateTabStyles();
    }

    /**
     * 创建并添加tab
     */
    private void addTab(final int position, View tabView) {
        TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
        tv_tab_title.setText(mTabEntities.get(position).getTabTitle());
        if (TextUtils.isEmpty(mTabEntities.get(position).getTabTitle()))
            tv_tab_title.setVisibility(View.GONE);
        ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);

        if (position == 2) {
            //中间Tab 设置点击selector
            iv_tab_icon.setBackgroundResource(mTabEntities.get(position).getTabSelector());
        } else {
            //其他Tab设置图片资源
            iv_tab_icon.setImageResource(mTabEntities.get(position).getTabUnselectedIcon());
        }


        tabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = (Integer) v.getTag();
                //中间Tab
                if (position == 2) {
                    if (mListener != null) {
                        mListener.onTabClick(position, mLastTab);
                    }
                }
                //其他Tab
                else {
                    if (mCurrentTab != position) {
                        setCurrentTab(position);
                        if (mListener != null) {
                            mListener.onTabClick(position, mLastTab);
                        }
                    } else {
                        if (mListener != null) {
                            mListener.onTabReClick(position);
                        }
                    }
                }

            }
        });
        LinearLayout.LayoutParams lp_tab = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        if (position == 2) {
            //中间Tab所占权重
            lp_tab = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.1f);

        } else {
            //其他Tab所占权重
            lp_tab = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
        }
        mTabsContainer.addView(tabView, position, lp_tab);
    }

    /**
     * 设置当前的Tab 中间Tab除外
     *
     * @param currentTab
     */
    public void setCurrentTab(int currentTab) {
        this.mLastTab = this.mCurrentTab;
        this.mCurrentTab = currentTab;
        updateTabSelection(currentTab);

    }

    /**
     * 更新Tab样式
     */
    private void updateTabStyles() {
        for (int i = 0; i < mTabCount; i++) {
            if (i == 2)
                continue;
            View tabView = mTabsContainer.getChildAt(i);
            TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            tv_tab_title.setTextColor(i == mCurrentTab ? mTextSelectColor : mTextUnselectColor);
            tv_tab_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextsize);
            ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
            iv_tab_icon.setVisibility(View.VISIBLE);
            CustomTabEntity tabEntity = mTabEntities.get(i);
            iv_tab_icon.setImageResource(i == mCurrentTab ? tabEntity.getTabSelectedIcon() : tabEntity.getTabUnselectedIcon());
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.bottomMargin = (int) mIconMargin;
            iv_tab_icon.setLayoutParams(lp);

        }
    }


    /**
     * 更新Tab选中
     *
     * @param position
     */
    private void updateTabSelection(int position) {
        for (int i = 0; i < mTabCount; i++) {
            if (i == 2)
                continue;
            View tabView = mTabsContainer.getChildAt(i);
            final boolean isSelect = i == position;
            TextView tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            tab_title.setTextColor(isSelect ? mTextSelectColor : mTextUnselectColor);
            ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
            CustomTabEntity tabEntity = mTabEntities.get(i);
            iv_tab_icon.setImageResource(isSelect ? tabEntity.getTabSelectedIcon() : tabEntity.getTabUnselectedIcon());
        }
    }

    private OnTabClickListener mListener;

    public void setOnTabClickListener(OnTabClickListener listener) {
        this.mListener = listener;
    }


    protected int dp2px(float dp) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    protected int sp2px(float sp) {
        final float scale = this.mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isInEditMode() || mTabCount <= 0) {
            return;
        }
        int paddingLeft = getPaddingLeft();
        // draw underline
        if (mUnderlineHeight > 0) {
            mRectPaint.setColor(mUnderlineColor);
            canvas.drawRect(paddingLeft, 0, mTabsContainer.getWidth() + paddingLeft, mUnderlineHeight, mRectPaint);

        }
    }


    public void setUnderlineColor(int underlineColor) {
        this.mUnderlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineHeight(float underlineHeight) {
        this.mUnderlineHeight = dp2px(underlineHeight);
        invalidate();
    }


    public void setTextSize(float textSize) {
        this.mTextsize = sp2px(textSize);
        updateTabStyles();
    }

    public void setTextSelectColor(int textSelectColor) {
        this.mTextSelectColor = textSelectColor;
        updateTabStyles();
    }

    public void setTextUnselectColor(int textUnselectColor) {
        this.mTextUnselectColor = textUnselectColor;
        updateTabStyles();
    }


    public void setIconMargin(float iconMargin) {
        this.mIconMargin = dp2px(iconMargin);
        updateTabStyles();
    }


    public int getTabCount() {
        return mTabCount;
    }

    public int getCurrentTab() {
        return mCurrentTab;
    }

    public int getUnderlineColor() {
        return mUnderlineColor;
    }

    public float getUnderlineHeight() {
        return mUnderlineHeight;
    }

    public float getTextSize() {
        return mTextsize;
    }

    public int getTextSelectColor() {
        return mTextSelectColor;
    }

    public int getTextUnselectColor() {
        return mTextUnselectColor;
    }

    public float getIconMargin() {
        return mIconMargin;
    }


    // show MsgTipView
    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private SparseArray<Boolean> mInitSetMap = new SparseArray<>();

    /**
     * 显示未读消息
     *
     * @param position 显示tab位置
     * @param num      num小于等于0显示红点,num大于0显示数字
     */
    public void showMsg(int position, int num) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            UnReadMsgUtils.show(tipView, num);
            if (mInitSetMap.get(position) != null && mInitSetMap.get(position)) {
                return;
            }
            setMsgMargin(position, 0, 0);
            mInitSetMap.put(position, true);
        }
    }

    /**
     * 显示未读红点
     *
     * @param position 显示tab位置
     */
    public void showDot(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        showMsg(position, 0);
    }


    /**
     * 隐藏未读消息
     *
     * @param position
     */
    public void hideMsg(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }

        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            tipView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置提示红点偏移,注意
     * 1.控件为固定高度:参照点为tab内容的右上角
     * 2.控件高度不固定(WRAP_CONTENT):参照点为tab内容的右上角,此时高度已是红点的最高显示范围,所以这时bottomPadding其实就是topPadding
     */
    public void setMsgMargin(int position, float leftPadding, float bottomPadding) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        if (tipView != null) {
            TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
            mTextPaint.setTextSize(mTextsize);
            float textWidth = mTextPaint.measureText(tv_tab_title.getText().toString());
            float textHeight = mTextPaint.descent() - mTextPaint.ascent();
            MarginLayoutParams lp = (MarginLayoutParams) tipView.getLayoutParams();

            float iconH = 0;
            float margin = 0;
            if (iconH <= 0) {
                iconH = mContext.getResources().getDrawable(mTabEntities.get(position).getTabSelectedIcon()).getIntrinsicHeight();
            }
            margin = mIconMargin;
            lp.leftMargin = dp2px(leftPadding);
            lp.topMargin = mHeight > 0 ? (int) (mHeight - textHeight - iconH - margin) / 2 - dp2px(bottomPadding) : dp2px(bottomPadding);
            tipView.setLayoutParams(lp);
        }
    }

    /**
     * 当前类只提供了少许设置未读消息属性的方法,可以通过该方法获取MsgView对象从而各种设置
     */
    public MsgView getMsgView(int position) {
        if (position >= mTabCount) {
            position = mTabCount - 1;
        }
        View tabView = mTabsContainer.getChildAt(position);
        MsgView tipView = (MsgView) tabView.findViewById(R.id.rtv_msg_tip);
        return tipView;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("instanceState", super.onSaveInstanceState());
        bundle.putInt("mCurrentTab", mCurrentTab);
        return bundle;
    }

    public ImageView getIconView(int tab) {
        View tabView = mTabsContainer.getChildAt(tab);
        ImageView iv_tab_icon = (ImageView) tabView.findViewById(R.id.iv_tab_icon);
        return iv_tab_icon;
    }

    public TextView getTitleView(int tab) {
        View tabView = mTabsContainer.getChildAt(tab);
        TextView tv_tab_title = (TextView) tabView.findViewById(R.id.tv_tab_title);
        return tv_tab_title;
    }


    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mCurrentTab = bundle.getInt("mCurrentTab");
            state = bundle.getParcelable("instanceState");
            if (mCurrentTab != 0 && mTabsContainer.getChildCount() > 0) {
                updateTabSelection(mCurrentTab);
            }
        }
        super.onRestoreInstanceState(state);
    }
}
