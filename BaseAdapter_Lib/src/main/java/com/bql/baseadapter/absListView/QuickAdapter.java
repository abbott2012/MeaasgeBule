package com.bql.baseadapter.absListView;

import android.content.Context;

import com.bql.baseadapter.DataHelper;

import java.util.List;

/**
 * <p>便捷操作的Adapter</p>
 * Created by Cyarie on 2016/3/30.
 */
public abstract class QuickAdapter<T> extends BaseAdapter<T> implements DataHelper<T> {


    public QuickAdapter(Context context, List<T> data, int... layoutIds) {
        super(context, data, layoutIds);
    }

    @Override
    public <BH extends BaseViewHolder> void convert(BH viewHolder, int position, T t) {
        QuickViewHolder holder = (QuickViewHolder) viewHolder;
        convertHelper(holder, position, t);
    }

    /**
     * 实现具体控件的获取和赋值等业务
     */
    public abstract void convertHelper(QuickViewHolder viewHolder, int position, T t);


    @Override
    public boolean isEnabled(int position) {
        return position < mList.size();
    }

    @Override
    public boolean addToHead(List<T> list) {
        return add(0, list);
    }

    @Override
    public boolean addToLast(T data) {
        boolean result = mList.add(data);
        notifyDataSetChanged();
        return result;
    }

    @Override
    public void addToHead(T data) {
        add(0, data);
    }

    @Override
    public boolean addToLast(List<T> list) {
        boolean result = mList.addAll(list);
        notifyDataSetChanged();
        return result;
    }

    @Override
    public boolean add(int startPos, List<T> list) {
        boolean result = mList.addAll(startPos, list);
        notifyDataSetChanged();
        return result;
    }

    @Override
    public void add(int startPos, T data) {
        mList.add(startPos, data);
        notifyDataSetChanged();
    }

    @Override
    public T getData(int index) {
        return getCount() == 0 ? null : mList.get(index);
    }

    @Override
    public void updateItem(T oldData, T newData) {
        updateItem(mList.indexOf(oldData), newData);
    }

    @Override
    public void updateItem(int index, T data) {
        mList.set(index, data);
        notifyDataSetChanged();
    }

    @Override
    public boolean remove(T data) {
        boolean result = mList.remove(data);
        notifyDataSetChanged();
        return result;
    }

    @Override
    public void remove(int index) {
        mList.remove(index);
        notifyDataSetChanged();
    }

    @Override
    public void replaceAll(List<T> list) {
        mList.clear();
        add(0, list);
    }

    @Override
    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public boolean contains(T data) {
        return mList.contains(data);
    }
}
