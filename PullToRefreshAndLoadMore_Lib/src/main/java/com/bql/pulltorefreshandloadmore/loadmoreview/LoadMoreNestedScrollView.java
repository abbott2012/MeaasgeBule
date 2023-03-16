package com.bql.pulltorefreshandloadmore.loadmoreview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.bql.pulltorefreshandloadmore.R;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassName: LoadMoreNestedScrollView <br>
 * Description: 加载更多的NestedScrollView<br>
 * Author: Cyarie <br>
 * Created: 2016/4/14 16:57 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class LoadMoreNestedScrollView extends NestedScrollView implements OnScrollBottomListener {

    /**
     * 加载更多UI
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
    boolean mCanLoadMore = true;
    /**
     * 是否加载失败
     */
    private boolean mIsLoadFail;

    /**
     * 加载更多事件回调
     */
    private OnLoadMoreListener mOnLoadMoreListener;

    /**
     * 添加LoadingView标志
     */
    private boolean mAddLoadMoreFooterFlag;

    /**
     * FooterView集合
     */
    private List<View> mFooterViewTempList = new ArrayList<>();

    /**
     * HeaderView集合
     */
    private List<View> mHeaderViewTempList = new ArrayList<>();

    /**
     * ScrollView的ContentView LinearLayout
     */
    private LinearLayout mSvContentView;

    /**
     * 没有更多是否隐藏loadingview
     */
    private boolean mHideLoadingView;


    public LoadMoreNestedScrollView(Context context) {
        super(context);
        init(context, null);
    }

    public LoadMoreNestedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public LoadMoreNestedScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

        mLoadMoreView.getFooterView().
                setOnClickListener(new OnMoreViewClickListener());
        a.recycle();
        setCanLoadMore(false);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t + getHeight() >= computeVerticalScrollRange()) {
            //ScrollView滑动到底部了
            onScrollBottom();
        }
    }


    @Override
    public void onScrollBottom() {
        if (mCanLoadMore && mLoadMoreMode == LoadMoreMode.SCROLL) {
            executeLoadMore();
        }
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
    public void setNoLoadMoreHideView(boolean hide) {
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
        mCanLoadMore = canLoadMore;
        if (!mAddLoadMoreFooterFlag) {
            mAddLoadMoreFooterFlag = true;
            addFooterView(mLoadMoreView.getFooterView());
        }
        if (!mCanLoadMore) {
            showNoMoreUI();
            if (mHideLoadingView) {
                mAddLoadMoreFooterFlag = false;
                removeFooterView(mLoadMoreView.getFooterView());
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

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        View view = getChildAt(0);
        if (view instanceof LinearLayout) {
            mSvContentView = (LinearLayout) view;
            for (int i = 0; i < mHeaderViewTempList.size(); i++) {
                View headerView = mHeaderViewTempList.get(i);
                mSvContentView.addView(headerView, i, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }

            for (int i = 0; i < mFooterViewTempList.size(); i++) {
                View footerView = mFooterViewTempList.get(i);
                mSvContentView.addView(footerView, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            }
        } else {
            throw new IllegalStateException("ScrollView child view must be a LinearLayout");
        }
    }


    /**
     * 添加FooterView
     *
     * @param view
     */
    public void addFooterView(View view) {
        mFooterViewTempList.add(view);
        if (mSvContentView != null) {
            int childCount = mSvContentView.getChildCount();
            mSvContentView.addView(view, childCount);
        }
    }

    /**
     * 移除FooterView
     *
     * @param view
     */
    public void removeFooterView(View view) {
        mFooterViewTempList.remove(view);
        if (mSvContentView != null) {
            mSvContentView.removeView(view);
        }
    }

    /**
     * 添加HeaderView
     *
     * @param view
     */
    public void addHeaderView(View view) {
        mHeaderViewTempList.add(view);
        if (mSvContentView != null) {
            mSvContentView.addView(view, mHeaderViewTempList.size());
        }
    }

    /**
     * 移除HeaderView
     *
     * @param view
     */
    public void removeHeaderView(View view) {
        mHeaderViewTempList.remove(view);
        if (mSvContentView != null) {
            mSvContentView.removeView(view);
        }
    }

    /**
     * 点击more view加载更多
     */
    class OnMoreViewClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            if (mCanLoadMore) {
                executeLoadMore();
            }
        }
    }
}
