package com.bql.pulltorefreshandloadmore.loadmorestyle;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.pulltorefreshandloadmore.R;
import com.bql.pulltorefreshandloadmore.loadmoreview.ILoadMoreView;

/**
 * ClassName: AnimLoadMoreView <br>
 * Description: 自定义加载动画进度条的LoreMoreView<br>
 * Author: Cyarie <br>
 * Created: 2016/4/15 11:00 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class AnimLoadMoreView extends LinearLayout implements ILoadMoreView {

    /**
     * 提示信息
     */
    private TextView mTvMessage;

    /**
     * IndicatorView
     */
    private AnimLoadingIndicatorView mAnimLoadingIndicator;

    public AnimLoadMoreView(Context context) {
        super(context);
        init(context);
    }

    public AnimLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnimLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.loading_view_footer_style, this);
        mAnimLoadingIndicator = (AnimLoadingIndicatorView) findViewById(R.id.anim_loading_indicator);
        mTvMessage = (TextView) findViewById(R.id.tv_loading_msg);
    }

    public void setIndicatorId(@AnimLoadingIndicatorView.Indicator int indicatorId) {
        mAnimLoadingIndicator.setIndicatorId(indicatorId);
    }

    public void setIndicatorColor(@ColorInt int indicatorColor) {
        mAnimLoadingIndicator.setIndicatorColor(indicatorColor);
    }

    @Override
    public void showNormal() {
        mAnimLoadingIndicator.setVisibility(View.GONE);
        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setText("点击加载更多");
    }

    @Override
    public void showNoMore() {
        mAnimLoadingIndicator.setVisibility(View.GONE);
        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setText("没有更多");
    }

    @Override
    public void showLoading() {
        mAnimLoadingIndicator.setVisibility(View.VISIBLE);
        mTvMessage.setVisibility(View.GONE);
        mTvMessage.setText("加载中…");
    }

    @Override
    public void showFail() {
        mAnimLoadingIndicator.setVisibility(View.GONE);
        mTvMessage.setVisibility(View.VISIBLE);
        mTvMessage.setText("网络异常，点击重试");
    }

    @Override
    public View getFooterView() {
        return this;
    }
}
