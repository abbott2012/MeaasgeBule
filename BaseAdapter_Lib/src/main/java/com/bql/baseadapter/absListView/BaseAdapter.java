package com.bql.baseadapter.absListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * <p>adapter基类 减少代码 </p>
 * Created by Cyarie on 2016/3/30.
 */
public abstract class BaseAdapter<T> extends android.widget.BaseAdapter {

    protected List<T> mList;
    protected Context mContext;
    protected LayoutInflater mLInflater;
    protected int[] layoutIds;
    private BaseViewHolder holder = new QuickViewHolder();

    /**
     * @param data      数据源
     * @param context   上下文
     * @param layoutIds 布局Id
     */
    public BaseAdapter(Context context, List<T> data, int... layoutIds) {
        this.mList = data;
        this.layoutIds = layoutIds;
        this.mContext = context;
        this.mLInflater = LayoutInflater.from(mContext);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int layoutId = getViewCheckLayoutId(position);
        holder = holder.get(mContext, position, convertView, parent, layoutId);
        convert(holder, position, mList.get(position));
        return holder.getConvertView(layoutId);
    }

    /**
     * 获取layoutId
     *
     * @param position
     * @return
     */
    protected int getViewCheckLayoutId(int position) {
        int layoutId;
        if (layoutIds == null) {
            throw new NullPointerException("not layoutId");
        } else {
            if (layoutIds.length == 0) {
                throw new NullPointerException("not layoutId");
            }
            layoutId = layoutIds[getLayoutIdIndex(position, mList.get(position))];
        }
        return layoutId;
    }

    /**
     * 实现具体控件的获取和赋值等业务
     *
     * @param viewHolder viewHolder
     * @param position   position
     * @param t          数据源中,当前对应的bean
     */
    public abstract <BH extends BaseViewHolder> void convert(BH viewHolder, int position, T t);

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


    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return getLayoutIdIndex(position, mList.get(position));
    }

    @Override
    public int getViewTypeCount() {
        return layoutIds == null ? 0 : layoutIds.length;
    }
}
