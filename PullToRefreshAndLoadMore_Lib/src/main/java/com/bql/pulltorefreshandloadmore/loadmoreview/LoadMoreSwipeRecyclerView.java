package com.bql.pulltorefreshandloadmore.loadmoreview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.SystemClock;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.bql.pulltorefreshandloadmore.R;
import com.bql.recyclerview.swipe.SwipeMenuRecyclerView;

import java.lang.reflect.Constructor;

/**
 * ClassName: LoadMoreSwipeRecyclerView <br>
 * Description: 侧滑 加载更多 RecyclerView<br>
 * Author: Cyarie <br>
 * Created: 2016/10/8 15:19 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class LoadMoreSwipeRecyclerView extends SwipeMenuRecyclerView implements OnScrollBottomListener {

    /**
     * 加载更多View
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
    boolean mCanLoadMore;

    /**
     * 是否加载失败
     */
    private boolean mIsLoadFail;

    /**
     * 加载更多事件回调
     */
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * emptyview
     */
    private View mEmptyView;

    /**
     * 没有更多是否隐藏loadmoreview
     */
    private boolean mHideLoadingView;

    /**
     * 添加头部和底部的RecyclerViewAdapter
     */
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter;

    /**
     * 添加LoadingView标志
     */
    private boolean mAddLoadMoreFooterFlag;

    public LoadMoreSwipeRecyclerView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadMoreSwipeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadMoreSwipeRecyclerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter();
        super.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
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

        mLoadMoreView.getFooterView().setOnClickListener(new LoadMoreSwipeRecyclerView.OnMoreViewClickListener());
        //        setCanLoadMore(false);
        a.recycle();
        addOnScrollListener(new LoadMoreSwipeRecyclerView.RecyclerViewOnScrollListener());
    }

    @Override
    public void setAdapter(RecyclerView.Adapter adapter) {
        //        super.setAdapter(adapter);
        try {
            adapter.unregisterAdapterDataObserver(mDataObserver);
        } catch (Exception e) {
        }
        adapter.registerAdapterDataObserver(mDataObserver);
        mHeaderAndFooterRecyclerViewAdapter.setAdapter(adapter);
        setMenuAdapter(adapter);

    }

    @Override
    public void onScrollBottom() {
        if (mCanLoadMore && mLoadMoreMode == LoadMoreMode.SCROLL) {
            executeLoadMore();
        }
    }

    /**
     * 设置recyclerview emptyview
     *
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {
        this.mEmptyView = emptyView;
    }

    /**
     * 设置LoadMoreView
     *
     * @param loadMoreView
     */
    public void setLoadMoreView(ILoadMoreView loadMoreView) {
        if (mLoadMoreView != null) {
            try {
                removeFooterView(mLoadMoreView.getFooterView());
                mAddLoadMoreFooterFlag = false;
            } catch (Exception e) {
            }
        }
        mLoadMoreView = loadMoreView;
        mLoadMoreView.getFooterView().setOnClickListener(new LoadMoreSwipeRecyclerView.OnMoreViewClickListener());
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
     * 设置是否加载更多
     *
     * @param canLoadMore
     */
    public void setCanLoadMore(boolean canLoadMore) {
        if (!mAddLoadMoreFooterFlag) {
            mAddLoadMoreFooterFlag = true;
            addFooterView(mLoadMoreView.getFooterView());
//            SkinManager.getInstance().injectSkin(mLoadMoreView.getFooterView());
        }
        mCanLoadMore = canLoadMore;
        if (!mCanLoadMore) {
            showNoMoreUI();
            if (mHideLoadingView) {
                removeFooterView(mLoadMoreView.getFooterView());
                mAddLoadMoreFooterFlag = false;
            }
        } else {
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
     * 添加footer view
     *
     * @param footerView
     */
    public void addFooterView(View footerView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        footerView.setLayoutParams(params);
        mHeaderAndFooterRecyclerViewAdapter.addFooterView(footerView);
    }

    /**
     * 添加header view
     *
     * @param headerView
     */
    public void addHeaderView(View headerView) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headerView.setLayoutParams(params);
        mHeaderAndFooterRecyclerViewAdapter.addHeaderView(headerView);
    }

    public void removeFooterView(View footerView) {
        mHeaderAndFooterRecyclerViewAdapter.removeFooter(footerView);
    }

    public void removeHeaderView(View headerView) {
        mHeaderAndFooterRecyclerViewAdapter.removeHeader(headerView);
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
     * 设置OnItemClickListener
     *
     * @param listener
     */
    public void setOnItemClickListener(HeaderAndFooterRecyclerViewAdapter.OnItemClickListener listener) {
        mHeaderAndFooterRecyclerViewAdapter.setOnItemClickListener(listener);
    }

    /**
     * 设置OnItemLongClickListener
     *
     * @param listener
     */
    public void setOnItemLongClickListener(HeaderAndFooterRecyclerViewAdapter.OnItemLongClickListener listener) {
        mHeaderAndFooterRecyclerViewAdapter.setOnItemLongClickListener(listener);
    }

    /**
     * 滚动到底部自动加载更多数据
     */
    private class RecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {

        /**
         * 最后一个的位置
         */
        private int[] lastPositions;

        /**
         * 最后一个可见的item的位置
         */
        private int lastVisibleItemPosition;

        /**
         * 当前滑动的状态
         */
        private int currentScrollState = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

            if (layoutManager instanceof LinearLayoutManager) {
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof GridLayoutManager) {
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            currentScrollState = newState;
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            if ((visibleItemCount > 0 && currentScrollState == RecyclerView.SCROLL_STATE_IDLE && (lastVisibleItemPosition) >= totalItemCount - 1)) {
                onScrollBottom();
            }
        }

        /**
         * 取数组中最大值
         *
         * @param lastPositions
         * @return
         */
        private int findMax(int[] lastPositions) {
            int max = lastPositions[0];
            for (int value : lastPositions) {
                if (value > max) {
                    max = value;
                }
            }

            return max;
        }
    }

    /**
     * 刷新数据时停止滑动,避免出现数组下标越界问题
     */
    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            Adapter<?> adapter = getAdapter();
            if (adapter != null && mEmptyView != null) {
                if (adapter.getItemCount() == 0) {
                    mEmptyView.setVisibility(View.VISIBLE);
                    setVisibility(View.GONE);
                } else {
                    mEmptyView.setVisibility(View.GONE);
                    setVisibility(View.VISIBLE);
                }
            }

            dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_CANCEL, 0, 0, 0));
        }
    };

    public ILoadMoreView getLoadMoreView() {
        return mLoadMoreView;
    }

    public HeaderAndFooterRecyclerViewAdapter getHeaderAndFooterRecyclerViewAdapter() {
        return mHeaderAndFooterRecyclerViewAdapter;
    }
}
