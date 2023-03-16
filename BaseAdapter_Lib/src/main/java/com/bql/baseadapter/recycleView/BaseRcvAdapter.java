package com.bql.baseadapter.recycleView;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * <p>RecycleAdapter基类,减少代码</p>
 * Created by Cyarie on 2016/3/30.
 */
public abstract class BaseRcvAdapter<T> extends RecyclerView.Adapter<BH> {

    protected List<T> mList;
    protected Context mContext;
    protected LayoutInflater mLInflater;
    protected int[] mLayoutId;
    private SparseArray<View> mConvertViews = new SparseArray<View>();

    /**
     * @param data     数据源
     * @param context  上下文
     * @param layoutId 布局Id
     */
    public BaseRcvAdapter(Context context, List<T> data, int... layoutId) {
        this.mList = data;
        this.mLayoutId = layoutId;
        this.mContext = context;
        this.mLInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdIndex(position, mList.get(position));
    }

    @Override
    public BH onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0 || viewType > mLayoutId.length) {
            throw new ArrayIndexOutOfBoundsException("checkLayout > LayoutId.length");
        }
        if (mLayoutId.length == 0) {
            throw new NullPointerException("not layoutId");
        }
        int layoutId = mLayoutId[viewType];
        View view = inflateItemView(layoutId, parent);
        BaseRcvHolder viewHolder = (BaseRcvHolder) view.getTag();
        if (viewHolder == null || viewHolder.getLayoutId() != layoutId) {
            viewHolder = new BaseRcvHolder(mContext, layoutId, view);
            return viewHolder;
        }
        return viewHolder;
    }

    /**
     * 解析布局资源
     *
     * @param layoutId
     * @param viewGroup
     * @return
     */
    protected View inflateItemView(int layoutId, ViewGroup viewGroup) {
        View convertView = mConvertViews.get(layoutId);
        if (convertView == null) {
            convertView = mLInflater.inflate(layoutId,
                    viewGroup, false);
        }
        return convertView;
    }

    @Override
    public void onBindViewHolder(BH holder, int position) {
        final T item = mList.get(position);
        // 绑定数据
        onBindData(holder, position, item);
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    /**
     * 绑定数据到Item View上
     *
     * @param viewHolder
     * @param position   数据的位置
     * @param item       数据项
     */
    protected abstract void onBindData(BH viewHolder, int position, T item);

    /**
     * 根据业务逻辑确定layoutId位置,使用多种样式 必须重写此方法，获取layout
     *
     * @param position 所在位置
     * @param item     对应数据
     * @return 默认使用第一个, 返回下标, 从0开始
     */
    public int getLayoutIdIndex(int position, T item) {
        return 0;
    }
}
