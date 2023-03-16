package com.bql.pulltorefreshandloadmore.loadmoreview;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.bql.pulltorefreshandloadmore.R;

import java.lang.reflect.Constructor;

/**
 * ClassName: LoadMoreGridView <br>
 * Description: 加载更多的GridView<br>
 * Author: Cyarie <br>
 * Created: 2016/4/14 16:26 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class LoadMoreGridView extends GridViewSupport implements OnScrollBottomListener {

    /**
     * 加载更多接口
     */
    private ILoadMoreView mLoadMoreView;

    /**
     * 加载更多方式，默认滑动到底部加载更多
     */
    private LoadMoreMode mLoadMoreMode = LoadMoreMode.SCROLL;

    /**
     * 加载更多lock
     */
    private boolean mLoadMoreLock;

    /**
     * 是否可以加载跟多
     */
    private boolean mCanLoadMore = true;

    /**
     * 是否加载失败
     */
    private boolean mIsLoadFail;

    /**
     * 加载更多事件回调
     */
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * 没有更多是否隐藏loadingview
     */
    private boolean mHideLoadingView;

    /**
     * 添加LoadingView标志
     */
    private boolean mAddLoadMoreFooterFlag;

    /**
     * 加载更多视图 Padding
     */
    private int mLoadMoreViewBottom, mLoadMoreViewTop, mLoadMoreViewLeft, mLoadMoreViewRight;

    /**
     * 加载更多视图是否显示
     */
    private boolean mHasLoadMoreViewShowState;

    public LoadMoreGridView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadMoreGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadMoreGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    /**
     * 初始化
     *
     * @param context 上下文
     * @param attrs   属性
     */
    private void init(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingView);
        mLoadMoreMode = LoadMoreMode.int2Value(a.getInt(R.styleable.LoadingView_loadMoreMode, 0x01));
        mHideLoadingView = a.getBoolean(R.styleable.LoadingView_hideLoadingView, false);
        if (a.hasValue(R.styleable.LoadingView_loadMoreView)) {
            try {
                String loadMoreViewName = a.getString(R.styleable.LoadingView_loadMoreView);
                Class clazz = Class.forName(loadMoreViewName);
                Constructor c = clazz.getConstructor(Context.class);
                ILoadMoreView loadMoreView = (ILoadMoreView) c.newInstance(context);
                mLoadMoreView = loadMoreView;
            } catch (Exception e) {
                e.printStackTrace();
                mLoadMoreView = new DefaultLoadMoreView(context);
            }
        } else {
            mLoadMoreView = new DefaultLoadMoreView(context);
        }
        mLoadMoreView.getFooterView().setOnClickListener(new OnMoreViewClickListener());
        addOnScrollListener(new GridViewOnScrollListener());
        a.recycle();
    }

    @Override
    public void onScrollBottom() {
        if (mCanLoadMore && mLoadMoreMode == LoadMoreMode.SCROLL) {
            executeLoadMore();
        }
    }

    /**
     * 布局加载完成
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        hideLoadMoreView();
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (!mAddLoadMoreFooterFlag) {
            mAddLoadMoreFooterFlag = true;
            addFooterView(mLoadMoreView.getFooterView());
        }
        super.setAdapter(adapter);
        if (adapter instanceof BaseAdapter) {
            try {
                adapter.unregisterDataSetObserver(mDataObserver);
            } catch (Exception e) {
            }

            adapter.registerDataSetObserver(mDataObserver);
        }
    }

    /**
     * 显示没有更多UI
     */
    void showNoMoreUI() {
        mLoadMoreLock = false;
        mLoadMoreView.showNoMore();
    }

    /**
     * 显示失败UI
     */
    public void showFailUI() {
        mIsLoadFail = true;
        mLoadMoreLock = false;
        mLoadMoreView.showFail();
    }

    /**
     * 显示默认UI
     */
    void showNormalUI() {
        mLoadMoreLock = false;
        mLoadMoreView.showNormal();
    }

    /**
     * 显示加载中UI
     */
    void showLoadingUI() {
        mIsLoadFail = false;
        mLoadMoreView.showLoading();
    }

    /**
     * 设置是否有加载更多
     *
     * @param canLoadMore
     */
    public void setCanLoadMore(boolean canLoadMore) {
        mCanLoadMore = canLoadMore;
        if (!mHasLoadMoreViewShowState) {
            showLoadMoreView();
        }
        if (!mCanLoadMore) {
            showNoMoreUI();
            if (mHideLoadingView && mHasLoadMoreViewShowState) {
                hideLoadMoreView();
            }
        } else {
            showNormalUI();
        }
    }

    /**
     * 设置LoadMoreView,在setAdapter之前调用该方法
     *
     * @param loadMoreView
     */
    public void setLoadMoreView(ILoadMoreView loadMoreView) {
        mLoadMoreView = loadMoreView;
        mLoadMoreView.getFooterView().setOnClickListener(new OnMoreViewClickListener());
    }

    /**
     * 设置加载更多模式
     *
     * @param mode
     */
    public void setLoadMoreMode(LoadMoreMode mode) {
        mLoadMoreMode = mode;
    }

    /**
     * 设置没有更多数据，是否隐藏footer view
     *
     * @param hide
     */
    public void setHideLoadingView(boolean hide) {
        this.mHideLoadingView = hide;
    }

    /**
     * 完成加载更多
     */
    public void onLoadMoreComplete() {
        if (mIsLoadFail) {
            showFailUI();
        } else if (mCanLoadMore) {
            showNormalUI();
        }
    }

    /**
     * 设置加载更多事件回调
     *
     * @param loadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener loadMoreListener) {
        this.mOnLoadMoreListener = loadMoreListener;
    }

    /**
     * 点击more view加载更多
     */
    class OnMoreViewClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            if (mCanLoadMore) {
                executeLoadMore();
            }
        }
    }

    /**
     * 加载更多
     */
    void executeLoadMore() {
        if (!mLoadMoreLock && mCanLoadMore) {
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMore();
            }
            mLoadMoreLock = true;//上锁
            showLoadingUI();
        }
    }

    /**
     * 隐藏加载更多视图
     */
    private void hideLoadMoreView() {
        mHasLoadMoreViewShowState = false;
        mLoadMoreViewBottom = mLoadMoreView.getFooterView().getPaddingBottom();
        mLoadMoreViewTop = mLoadMoreView.getFooterView().getPaddingTop();
        mLoadMoreViewLeft = mLoadMoreView.getFooterView().getPaddingLeft();
        mLoadMoreViewRight = mLoadMoreView.getFooterView().getPaddingRight();
        mLoadMoreView.getFooterView().setVisibility(View.GONE);
        mLoadMoreView.getFooterView().setPadding(0, -mLoadMoreView.getFooterView().getHeight(), 0, 0);
    }

    /**
     * 显示加载更多视图
     */
    private void showLoadMoreView() {
        mHasLoadMoreViewShowState = true;
        mLoadMoreView.getFooterView().setVisibility(View.VISIBLE);
        mLoadMoreView.getFooterView().setPadding(mLoadMoreViewLeft, mLoadMoreViewTop, mLoadMoreViewRight, mLoadMoreViewBottom);
    }

    /**
     * 滚动到底部自动加载更多数据
     */
    private class GridViewOnScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView listView, int scrollState) {
            if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE
                    && listView.getLastVisiblePosition() + 1 == listView.getCount()) {// 如果滚动到最后一行
                onScrollBottom();
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        }
    }

    /**
     * 刷新数据时停止滑动,避免出现数组下标越界问题
     */
    private DataSetObserver mDataObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
        }

        @Override
        public void onInvalidated() {
        }
    };

    public ILoadMoreView getLoadMoreView() {
        return mLoadMoreView;
    }
}
