package com.bql.baseadapter.recycleView;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

/**
 * <p>RecycleViewHolder基类,减少代码</p>
 * Created by Cyarie on 2016/3/30.
 */
public class BaseRcvHolder extends BH {

    private SparseArray<View> mViews = new SparseArray<>();
    private View mConvertView;
    private int mLayoutId;
    protected Context mContext;

    public BaseRcvHolder(Context context, int layoutId, View itemView) {
        super(itemView);
        this.mContext = context;
        this.mLayoutId = layoutId;
        mConvertView = itemView;
        mConvertView.setTag(this);
    }

    public <R extends View> R getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (R) view;
    }

    public int getLayoutId() {
        return mLayoutId;
    }

    public View getItemView() {
        return mConvertView;
    }
}
