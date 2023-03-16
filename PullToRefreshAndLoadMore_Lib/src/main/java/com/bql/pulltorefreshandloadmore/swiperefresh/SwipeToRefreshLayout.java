package com.bql.pulltorefreshandloadmore.swiperefresh;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.bql.pulltorefreshandloadmore.R;

import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: SwipeToRefreshLayout <br>
 * Description: 下拉刷新的SwipeRefreshLayout<br>
 * Author: Cyarie <br>
 * Created: 2016/4/14 14:21 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class SwipeToRefreshLayout extends SwipeRefreshLayout {

    /**
     * 刷新接口
     */
    private OnRefreshListener mOnRefreshListener;

    /**
     * Handler
     */
    private Handler mHandler = new Handler();

    /**
     * 可滑动的所有子View集合
     */
    private List<View> mScrollChildren = new ArrayList<>();

    private int mTouchSlop;

    private float mPrevX;
    /**
     * 是否禁止滑动事件
     */
    private boolean mDeclined;

    //是否可以刷新
    private boolean canRefresh = true;

    public SwipeToRefreshLayout(Context context) {
        super(context);
        init(context, null);
    }

    public SwipeToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param attrs   属性
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SwipeToRefreshLayout);
        //获取swipeRefresh的进度条动画颜色 默认黑色
        int color = a.getColor(R.styleable.SwipeToRefreshLayout_refreshLoadingColor, Color.BLACK);
        //设置swipeRefresh的进度条动画颜色
        setColorSchemeColors(color);
        a.recycle();
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    /**
     * 设置刷新监听
     *
     * @param listener
     */
    @Override
    public void setOnRefreshListener(OnRefreshListener listener) {
        super.setOnRefreshListener(listener);
        this.mOnRefreshListener = listener;
    }

    /**
     * 自动刷新
     */
    public void autoRefresh() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRefreshing(true);
                if (mOnRefreshListener != null) {
                    mOnRefreshListener.onRefresh();
                }
            }
        }, 200);
    }

    /**
     * 刷新完成
     */
    public void onRefreshComplete() {
        setRefreshing(false);
    }

    /**
     * 布局加载完成
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getViewGroup(this);
    }

    /**
     * 获取可滑动的子View
     *
     * @param viewGroup
     */
    public void getViewGroup(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                if (view instanceof ScrollView || view instanceof ListView
                        || view instanceof GridView || view instanceof RecyclerView) {
                    mScrollChildren.add(view);
                } else {
                    getViewGroup((ViewGroup) view);
                }
            }
        }
    }

    /**
     * 子View是否可滑动
     *
     * @return
     */
    @Override
    public boolean canChildScrollUp() {
        // check if any supplied swipeable children can scroll up
        for (View view : mScrollChildren) {
            if (view.isShown() && ViewCompat.canScrollVertically(view, -1)) {
                // prevent refresh gesture
                return true;
            }
        }
        return false;
    }

    /**
     * 拦截事件
     *
     * @param event
     * @return
     */
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPrevX = MotionEvent.obtain(event).getX();
                mDeclined = false; // New action
                break;

            case MotionEvent.ACTION_MOVE:
                final float eventX = event.getX();
                float xDiff = Math.abs(eventX - mPrevX);

                if (mDeclined || xDiff > mTouchSlop || !canRefresh) {
                    mDeclined = true; // Memorize
                    return false;
                }
        }

        return super.onInterceptTouchEvent(event);
    }

    public void setCanRefresh(boolean canRefresh) {
        this.canRefresh = canRefresh;
    }
}
