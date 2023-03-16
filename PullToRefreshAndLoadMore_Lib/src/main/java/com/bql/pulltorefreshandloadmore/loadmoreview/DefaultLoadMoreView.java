package com.bql.pulltorefreshandloadmore.loadmoreview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.materialprogressbar.MaterialProgressBar;
import com.bql.pulltorefreshandloadmore.R;


/**
 * ClassName: LoadMoreView <br>
 * Description: 默认加载更多布局<br>
 * Author: Cyarie <br>
 * Created: 2016/4/14 15:29 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class DefaultLoadMoreView extends LinearLayout implements ILoadMoreView {

    /**
     * 加载文字
     */
    private TextView mTvMessage;
    /**
     * 进度条
     */
    private MaterialProgressBar mPbLoading;


    public DefaultLoadMoreView(Context context) {
        super(context);
        init(context);
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DefaultLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.loading_view_footer_default, this);
        mPbLoading = (MaterialProgressBar) findViewById(R.id.pb_loading);
        mTvMessage = (TextView) findViewById(R.id.tv_loading_msg);
    }


    @Override
    public void showNormal() {
        if (mPbLoading != null)
            mPbLoading.setVisibility(View.GONE);
        if (mTvMessage != null)
            mTvMessage.setText(R.string.click_to_load_more);
    }

    @Override
    public void showNoMore() {
        if (mPbLoading != null)
            mPbLoading.setVisibility(View.GONE);
        if (mTvMessage != null)
            mTvMessage.setText(R.string.no_more);
    }

    @Override
    public void showLoading() {
        if (mPbLoading != null)
            mPbLoading.setVisibility(View.VISIBLE);
        if (mTvMessage != null)
            mTvMessage.setText(R.string.loading_data);
    }

    @Override
    public void showFail() {
        if (mPbLoading != null)
            mPbLoading.setVisibility(View.GONE);
        if (mTvMessage != null)
            mTvMessage.setText(R.string.net_error);
    }

    @Override
    public View getFooterView() {
        return this;
    }

    /**
     * 停止ProgressBar动画
     */
    public void stop() {
        if (mPbLoading != null) {
            mPbLoading.cancel();
            mPbLoading = null;
        }
    }
}
