package com.guoji.mobile.cocobee.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.pulltorefreshandloadmore.loadmoreview.DefaultLeLoadMoreView;
import com.bql.pulltorefreshandloadmore.loadmoreview.LoadMoreRecyclerView;
import com.bql.pulltorefreshandloadmore.loadmoreview.OnLoadMoreListener;
import com.bql.pulltorefreshandloadmore.swiperefresh.SwipeToRefreshLayout;
import com.bql.pulltorefreshandloadmore.ultraptr.OnDefaultRefreshListener;
import com.bql.pulltorefreshandloadmore.ultraptr.PtrClassicFrameLayout;
import com.bql.pulltorefreshandloadmore.ultraptr.PtrFrameLayout;
import com.bql.pulltorefreshandloadmore.ultraptr.indicator.PtrIndicator;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.LoadingViewCallback;
import com.guoji.mobile.cocobee.view.animator.NoAlphaItemAnimator;
import com.guoji.mobile.cocobee.view.springview.SpringView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * ClassName: RefreshAndLoadFragment <br>
 * Description: 下拉刷新 加载更多的Fragment<br>
 * Author: Cyarie <br>
 * Created: 2016/7/18 15:46 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class RefreshAndLoadFragment<T extends Object> extends BaseFragment implements OnLoadMoreListener {

    //数据集合
    public List<T> mList = new ArrayList<>();

    @BindView(R.id.rcv_load_more)
    public LoadMoreRecyclerView mRcvLoadMore;

    //下拉刷新 上拉加载 风格
    @BindView(R.id.prt_layout)
    @Nullable
    public PtrClassicFrameLayout mPrtLayout;

    //推拽View
    @BindView(R.id.springView)
    @Nullable
    public SpringView mSpringView;

    //swipeToRefreshLayout 风格
    @BindView(R.id.swipe_to_refresh)
    @Nullable
    public SwipeToRefreshLayout mSwipeToRefreshLayout;

    //当前页数
    public int mCurPage = 1;

    //每页大小
    public int pageSize = 20;


    @Override
    protected void initLazyViewsAndEvents(@Nullable Bundle savedInstanceState) {
        super.initLazyViewsAndEvents(savedInstanceState);
        initLazyViews(savedInstanceState);
        initView();

    }

    /**
     * 懒加载 如果继承该类 要实现懒加载效果 重写该方法进行初始化
     *
     * @param savedInstanceState
     */
    protected void initLazyViews(@Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onEnterAnimationEnd(Bundle savedInstanceState) {
        super.onEnterAnimationEnd(savedInstanceState);
        if (!lazyLoadMode()) {
            initView();
        }
    }


    /**
     * 初始化View
     */
    private void initView() {
        if (getLayoutManager() == null) {
            throw new NullPointerException("The getLayoutManager() method must not be null");
        }
        mRcvLoadMore.setLayoutManager(getLayoutManager());
        if (getAdapter() == null) {
            throw new NullPointerException("The getAdapter() method must not be null");
        }
        // 设置自定义的animator，解决闪烁问题
        mRcvLoadMore.setItemAnimator(new NoAlphaItemAnimator());

        mRcvLoadMore.setAdapter(getAdapter());

        // 设置headerView
        if (isAddHeaderView()) {
            mRcvLoadMore.addHeaderView(getHeaderView());
        }

        if (getItemDecoration() != null)
            mRcvLoadMore.addItemDecoration(getItemDecoration());
        mRcvLoadMore.setOnLoadMoreListener(this);
        mRcvLoadMore.setHideLoadingView(hideLoadMoreView());
        //        if (mSpringView != null)
        //            mSpringView.setPullEnable(isSpringMode());
        if (!isSwipeToRefresh() && mPrtLayout != null) {
            mPrtLayout.disableWhenHorizontalMove(true);
            mPrtLayout.setOnRefreshListener(new OnDefaultRefreshListener() {
                @Override
                public void onRefreshBegin(PtrFrameLayout frame) {
                    onRefresh();
                }

                @Override
                public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                    return canRefresh() ? super.checkCanDoRefresh(frame, content, header) : false;
                }
            });
            mPrtLayout.setOnUIScrollChangeListener((frame, isUnderTouch, status, ptrIndicator) -> onUIScrollChange(frame, isUnderTouch, status, ptrIndicator));

        } else {
            if (mSwipeToRefreshLayout != null) {
                mSwipeToRefreshLayout.setOnRefreshListener(() -> onRefresh());
                mSwipeToRefreshLayout.setCanRefresh(canRefresh());
            }
        }
        mRcvLoadMore.setOnItemClickListener((holder, position) -> onRcvItemClick(holder, position));
        loadDataList(1, false);
    }

    @Override
    protected int getContentViewLayoutID() {
        return isSwipeToRefresh() ? R.layout.fragment_swipe_to_refresh_load : (isSpringMode() ? R.layout.fragment_spring_load : R.layout.fragment_ptr_load);
    }


    public abstract void onRcvItemClick(RecyclerView.ViewHolder holder, int position);

    /**
     * 适配器 继承自{@link QuickRcvAdapter}
     *
     * @return
     */
    public abstract QuickRcvAdapter<T> getAdapter();

    /**
     * 添加分割线 {@link android.support.v7.widget.RecyclerView.ItemDecoration}
     *
     * @return
     */
    public abstract RecyclerView.ItemDecoration getItemDecoration();

    /**
     * 布局管理器 {@link android.support.v7.widget.RecyclerView.LayoutManager}
     *
     * @return
     */
    public abstract RecyclerView.LayoutManager getLayoutManager();


    @Override
    public void onDestroyView() {
        if (mRcvLoadMore != null && mRcvLoadMore.getLoadMoreView() instanceof DefaultLeLoadMoreView) {
            ((DefaultLeLoadMoreView) mRcvLoadMore.getLoadMoreView()).stop();
            mRcvLoadMore = null;
        }
        super.onDestroyView();
    }


    /**
     * 是否需要添加HeaderView
     *
     * @return
     */
    protected boolean isAddHeaderView() {
        return false;
    }

    /**
     * 获取HeaderView控件
     *
     * @return
     */
    protected View getHeaderView() {
        return null;
    }

    /**
     * 刷新数据
     */
    public abstract void onRefresh();

    /**
     * 滑动监听
     */
    public void onUIScrollChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

    }


    /**
     * 是否可以下拉刷新 默认 true
     */
    public boolean canRefresh() {
        return true;
    }


    /**
     * 加载数据列表
     *
     * @param curPage         当前页
     * @param isPullToRefresh 是否下拉刷新
     */
    public abstract void loadDataList(int curPage, boolean isPullToRefresh);


    public int getCurPage() {
        return mCurPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setCurPage(int curPage) {
        mCurPage = curPage;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * 结束刷新和加载
     */
    public void completeRefreshAndLoad() {
        if (mPrtLayout != null) {
            mPrtLayout.onRefreshComplete();
        }
        if (mSwipeToRefreshLayout != null) {
            mSwipeToRefreshLayout.onRefreshComplete();
        }
        if (mRcvLoadMore != null) {
            mRcvLoadMore.onLoadMoreComplete();
        }

    }


    /**
     * 处理刷新加载得到的数据
     *
     * @param total
     * @param list
     */
    //    public void handleRefreshAndLoadListData(LoadingViewCallback loadingViewCallback, int total, List<T> list) {
    //        if (list.size() == 0) {
    //            loadingViewCallback.showEmpty();
    //        } else {
    //            if (mCurPage == 1) {
    //                this.total = total;
    //                getAdapter().clear();
    //            }
    //            mCurPage = mCurPage + 1;
    //            getAdapter().addToLast(list);
    //            if (mRcvLoadMore != null) {
    //                if (getAdapter().getItemCount() < this.total) {
    //                    mRcvLoadMore.setCanLoadMore(true);
    //                } else {
    //                    mRcvLoadMore.setCanLoadMore(false);
    //                }
    //            }
    //        }
    //
    //    }

    /**
     * 处理刷新加载得到的数据
     *
     * @param curPage             当前页
     * @param loadingViewCallback 加载视图
     * @param list                数据集合
     */
    public void handleRefreshAndLoadListData(int curPage, LoadingViewCallback loadingViewCallback, List<T> list) {
        mCurPage = curPage;
        if (list.size() == 0 && mCurPage == 1) {
            loadingViewCallback.showEmpty();
        } else {
            if (mCurPage == 1) {
                getAdapter().clear();
            }
            mCurPage = mCurPage + 1;
            getAdapter().addToLast(list);
            if (mRcvLoadMore != null) {
                if (list.size() < pageSize || list.size() == 0) {
                    mRcvLoadMore.setCanLoadMore(false);
                } else {
                    mRcvLoadMore.setCanLoadMore(true);
                }
            }
        }
    }


    /**
     * 处理刷新得到的数据
     *
     * @param list
     */
    //    public void handleRefreshListData(LoadingViewCallback loadingViewCallback, List<T> list, String tips) {
    //        if (list.size() == 0) {
    //            if (tips == null) {
    //                loadingViewCallback.showEmpty();
    //            } else {
    //                loadingViewCallback.showEmpty(tips);
    //            }
    //        } else {
    //            if (mCurPage == 1) {
    //                getAdapter().clear();
    //            }
    //            getAdapter().addToLast(list);
    //        }
    //
    //    }


    /**
     * 刷新加载回调
     *
     * @param <T>
     */
    public abstract class RefreshAndLoadCallback<T> extends LoadingViewCallback<T> {

        public RefreshAndLoadCallback(boolean isPullToRefresh) {
            super(_mActivity, getView(), isPullToRefresh);
        }


        @Override
        public void onAfter(T t, Exception e) {
            super.onAfter(t, e);
            if (isPullToRefresh)
                completeRefreshAndLoad();
        }
    }

    //是否是 swipeToRefreshLayout 风格的 刷新控件 默认false
    public boolean isSwipeToRefresh() {
        return false;
    }

    public boolean hideLoadMoreView() {
        return true;
    }

    public boolean isSpringMode() {
        return false;
    }
}
