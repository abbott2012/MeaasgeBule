package com.guoji.mobile.cocobee.fragment.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bql.pulltorefreshandloadmore.loadmoreview.DefaultLeLoadMoreView;
import com.bql.pulltorefreshandloadmore.loadmoreview.HeaderAndFooterRecyclerViewAdapter;
import com.bql.pulltorefreshandloadmore.loadmoreview.LoadMoreSwipeRecyclerView;
import com.bql.pulltorefreshandloadmore.loadmoreview.OnLoadMoreListener;
import com.bql.pulltorefreshandloadmore.ultraptr.OnDefaultRefreshListener;
import com.bql.pulltorefreshandloadmore.ultraptr.PtrClassicFrameLayout;
import com.bql.pulltorefreshandloadmore.ultraptr.PtrFrameLayout;
import com.bql.recyclerview.swipe.Closeable;
import com.bql.recyclerview.swipe.OnSwipeMenuItemClickListener;
import com.bql.recyclerview.swipe.SwipeMenuAdapter;
import com.bql.recyclerview.swipe.SwipeMenuCreator;
import com.bql.recyclerview.swipe.SwipeMenuRecyclerView;
import com.bql.utils.ThreadPool;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.LoadingViewCallback;
import com.guoji.mobile.cocobee.view.springview.SpringView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * ClassName: SwipeRefreshAndLoadFragment <br>
 * Description: 侧滑 下拉刷新 加载更多 RecyclerView<br>
 * Author: Cyarie <br>
 * Created: 2016/10/8 15:14 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class SwipeRefreshAndLoadFragment<T extends Object> extends BaseFragment implements OnLoadMoreListener {

    //数据集合
    public List<T> mList = new ArrayList<>();

    @BindView(R.id.rcv_load_more_swipe)
    public LoadMoreSwipeRecyclerView mRcvLoadMoreSwipe;

    @BindView(R.id.prt_layout)
    @Nullable
    public PtrClassicFrameLayout mPrtLayout;

    //推拽View
    @BindView(R.id.springView)
    @Nullable
    public SpringView mSpringView;

    //当前页数
    public int mCurPage = 1;

    //每页大小
    public int pageSize = 20;

    public int total;


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
            initLazyViews(savedInstanceState);
            initView();
        }

    }


    /**
     * 初始化view
     */
    private void initView() {
        if (getLayoutManager() == null) {
            throw new NullPointerException("The getLayoutManager() method must not be null");
        }
        mRcvLoadMoreSwipe.setLayoutManager(getLayoutManager());
        if (getAdapter() == null) {
            throw new NullPointerException("The getAdapter() method must not be null");
        }

        // 设置headerView
        if (isAddHeaderView()) {
            mRcvLoadMoreSwipe.addHeaderView(getHeaderView());
        }

        // 设置菜单创建器
        if (getSwipeMenuCreator() != null) {
            mRcvLoadMoreSwipe.setSwipeMenuCreator(getSwipeMenuCreator());
        }
        // 设置菜单Item点击监听
        mRcvLoadMoreSwipe.setSwipeMenuItemClickListener(new OnSwipeMenuItemClickListener() {
            @Override
            public void onItemClick(final Closeable closeable, int adapterPosition, int menuPosition, @SwipeMenuRecyclerView.DirectionMode int direction) {
                ThreadPool.postOnUiDelayed(new Runnable() {
                    @Override
                    public void run() {
                        closeable.smoothCloseMenu();// 关闭被点击的菜单。
                    }
                }, 300);

                if (direction == SwipeMenuRecyclerView.RIGHT_DIRECTION) {
                    onRightSwipeMenuClick(adapterPosition, menuPosition);
                } else if (direction == SwipeMenuRecyclerView.LEFT_DIRECTION) {
                    onLeftSwipeMenuClick(adapterPosition, menuPosition);
                }
            }
        });
        mRcvLoadMoreSwipe.setAdapter(getAdapter());
        if (getItemDecoration() != null)
            mRcvLoadMoreSwipe.addItemDecoration(getItemDecoration());
        mRcvLoadMoreSwipe.setOnLoadMoreListener(this);
        mRcvLoadMoreSwipe.setHideLoadingView(hideLoadMoreView());
        if (mPrtLayout != null) {
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
        }

        mRcvLoadMoreSwipe.setOnItemClickListener(new HeaderAndFooterRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder holder, int position) {
                onRcvItemClick(holder, position);
            }
        });
        loadDataList(1, false);
    }

    @Override
    protected int getContentViewLayoutID() {
        return isSpringMode() ? R.layout.fragment_spring_load_swipe_menu : R.layout.fragment_ptr_load_swipe_menu;
    }

    public abstract void onRcvItemClick(RecyclerView.ViewHolder holder, int position);

    /**
     * 适配器 继承自{@link SwipeMenuAdapter}
     *
     * @return
     */
    public abstract SwipeMenuAdapter getAdapter();

    /**
     * 添加分割线 {@link RecyclerView.ItemDecoration}
     *
     * @return
     */
    public abstract RecyclerView.ItemDecoration getItemDecoration();

    /**
     * 布局管理器 {@link RecyclerView.LayoutManager}
     *
     * @return
     */
    public abstract RecyclerView.LayoutManager getLayoutManager();


    /**
     * 侧滑菜单  {@link SwipeMenuCreator}
     *
     * @return
     */
    public abstract SwipeMenuCreator getSwipeMenuCreator();

    /**
     * 左侧菜单点击
     *
     * @return
     */
    public abstract void onLeftSwipeMenuClick(int adapterPosition, int menuPosition);

    /**
     * 右侧菜单点击
     *
     * @return
     */
    public abstract void onRightSwipeMenuClick(int adapterPosition, int menuPosition);

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRcvLoadMoreSwipe != null && mRcvLoadMoreSwipe.getLoadMoreView() instanceof DefaultLeLoadMoreView) {
            ((DefaultLeLoadMoreView) mRcvLoadMoreSwipe.getLoadMoreView()).stop();
            mRcvLoadMoreSwipe = null;
        }
    }

    public boolean hideLoadMoreView() {
        return true;
    }

    /**
     * 刷新数据
     */
    public abstract void onRefresh();

    /**
     * 是否可以下拉刷新 默认 true
     */
    public boolean canRefresh() {
        return true;
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


    /**
     * 结束刷新和加载
     */
    public void completeRefreshAndLoad() {
        if (mPrtLayout != null) {
            mPrtLayout.onRefreshComplete();
        }
        if (mRcvLoadMoreSwipe != null) {
            mRcvLoadMoreSwipe.onLoadMoreComplete();
        }

    }
    /**
     * 处理刷新得到的数据
     *
     * @param list
     */
    public void handleRefreshListData(LoadingViewCallback loadingViewCallback, List<T> list) {
        if (list.size() == 0 && loadingViewCallback != null) {
            loadingViewCallback.showEmpty();
        } else {
            if (mCurPage == 1) {
                mList.clear();
                getAdapter().notifyDataSetChanged();
            }
            mList.addAll(list);
            getAdapter().notifyDataSetChanged();
        }

    }

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
                mList.clear();
                getAdapter().notifyDataSetChanged();
            }
            mCurPage = mCurPage + 1;
            mList.addAll(list);
            getAdapter().notifyDataSetChanged();
            if (mRcvLoadMoreSwipe != null) {
                if (list.size() < pageSize || list.size() == 0) {
                    mRcvLoadMoreSwipe.setCanLoadMore(false);
                } else {
//                    mRcvLoadMoreSwipe.setCanLoadMore(true);
                    mRcvLoadMoreSwipe.setCanLoadMore(false);
                }
            }
        }

    }

    /**
     * 刷新加载回调
     *
     * @param <T>
     */
    public abstract class RefreshAndLoadCallback<T> extends LoadingViewCallback<T> {

        public RefreshAndLoadCallback(boolean isPullToRefresh) {
            super(_mActivity, getView(), isPullToRefresh);
        }

        public RefreshAndLoadCallback(Context context, boolean isPullToRefresh) {
            super(_mActivity, isPullToRefresh);
        }

        @Override
        public void onAfter(T t, Exception e) {
            super.onAfter(t, e);
            if (isPullToRefresh)
                completeRefreshAndLoad();
        }

    }

    public boolean isSpringMode() {
        return false;
    }

}
