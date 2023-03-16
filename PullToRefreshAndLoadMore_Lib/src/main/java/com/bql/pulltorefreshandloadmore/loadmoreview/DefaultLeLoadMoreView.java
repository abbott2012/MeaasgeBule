package com.bql.pulltorefreshandloadmore.loadmoreview;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.pulltorefreshandloadmore.R;

/**
 * ClassName: DefaultLeLoadMoreView <br>
 * Description: 乐摇Go加载更多<br>
 * Author: Cyarie <br>
 * Created: 2016/11/21 15:14 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class DefaultLeLoadMoreView extends LinearLayout implements ILoadMoreView {

    /**
     * 加载文字
     */
    private TextView mTvMessage;
    /**
     * 加载图片
     */
    private ImageView mIvLoading;

    private AnimationDrawable loadingMoreDrawable;


    public DefaultLeLoadMoreView(Context context) {
        super(context);
        init(context);
    }

    public DefaultLeLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public DefaultLeLoadMoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflate(context, R.layout.loading_view_footer_le, this);
        mIvLoading = (ImageView) findViewById(R.id.iv_loading_more);
        mTvMessage = (TextView) findViewById(R.id.tv_loading_msg);
        mIvLoading.setBackgroundResource(R.drawable.loading_more);
        loadingMoreDrawable = (AnimationDrawable) mIvLoading.getBackground();
        loadingMoreDrawable.mutate();
    }


    @Override
    public void showNormal() {
        if (mIvLoading != null)
            mIvLoading.setVisibility(View.GONE);
        if (loadingMoreDrawable != null && loadingMoreDrawable.isRunning())
            loadingMoreDrawable.stop();
        if (mTvMessage != null)
            mTvMessage.setText(R.string.click_to_load_more);
    }

    @Override
    public void showNoMore() {
        if (mIvLoading != null)
            mIvLoading.setVisibility(View.GONE);
        if (loadingMoreDrawable != null && loadingMoreDrawable.isRunning())
            loadingMoreDrawable.stop();
        if (mTvMessage != null)
            mTvMessage.setText(R.string.no_more);
    }

    @Override
    public void showLoading() {
        if (mIvLoading != null)
            mIvLoading.setVisibility(View.VISIBLE);
        if (loadingMoreDrawable != null && !loadingMoreDrawable.isRunning())
            loadingMoreDrawable.start();
        if (mTvMessage != null)
            mTvMessage.setText(R.string.loading_data);
    }

    @Override
    public void showFail() {
        if (mIvLoading != null)
            mIvLoading.setVisibility(View.GONE);
        if (loadingMoreDrawable != null && loadingMoreDrawable.isRunning())
            loadingMoreDrawable.stop();
        if (mTvMessage != null)
            mTvMessage.setText(R.string.net_error);
    }

    @Override
    public View getFooterView() {
        return this;
    }

    /**
     * 停止动画
     */
    public void stop() {
        if (mIvLoading != null && loadingMoreDrawable.isRunning()) {
            loadingMoreDrawable.stop();
            loadingMoreDrawable = null;
        }
    }
}
