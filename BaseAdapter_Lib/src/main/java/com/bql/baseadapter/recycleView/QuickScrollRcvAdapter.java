package com.bql.baseadapter.recycleView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.List;

/**
 * ClassName: QuickScrollRcvAdapter <br>
 * Description: <br>
 * Author: Cyarie <br>
 * Created: 2016/8/10 15:02 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class QuickScrollRcvAdapter<T> extends QuickRcvAdapter<T> {

    protected RecyclerView mRecyclerView;

    protected boolean isScrolling;

    public QuickScrollRcvAdapter(RecyclerView recyclerView, Context context, List<T> data, int... layoutId) {
        super(context, data, layoutId);
        this.mRecyclerView = recyclerView;
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                // 设置是否滚动的状态
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isScrolling = false;
                    notifyDataSetChanged();
                } else {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }


    @Override
    protected void onBindData(BH viewHolder, int position, T item) {
        QuickRcvHolder helperViewHolder = (QuickRcvHolder) viewHolder;
        bindDataHelper(helperViewHolder, position, item, isScrolling);
    }

    protected abstract void bindDataHelper(QuickRcvHolder viewHolder, int position, T item, boolean isScrolling);


}
