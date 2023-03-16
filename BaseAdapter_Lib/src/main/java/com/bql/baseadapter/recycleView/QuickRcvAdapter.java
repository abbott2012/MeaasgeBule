package com.bql.baseadapter.recycleView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.bql.baseadapter.DataHelper;

import java.util.List;


/**
 * <p>提供便捷操作的RecycleViewAdapter</p>
 * Created by Cyarie on 2016/3/30.
 */
public abstract class QuickRcvAdapter<T> extends BaseRcvAdapter<T>
        implements DataHelper<T> {

    public QuickRcvAdapter(Context context, List<T> data, int... layoutId) {
        super(context, data, layoutId);
    }

    @Override
    public BH onCreateViewHolder(ViewGroup parent, int viewType) {
        QuickRcvHolder holder;
        if (viewType < 0 || viewType > mLayoutId.length) {
            throw new ArrayIndexOutOfBoundsException("checkLayout > LayoutId.length");
        }
        if (mLayoutId.length == 0) {
            throw new NullPointerException("not layoutId");
        }
        int layoutId = mLayoutId[viewType];
        View view = inflateItemView(layoutId, parent);
        holder = (QuickRcvHolder) view.getTag();
        if (holder == null || holder.getLayoutId() != layoutId) {
            holder = new QuickRcvHolder(mContext, layoutId, view);
        }
        return holder;
    }


    @Override
    protected void onBindData(final BH viewHolder, final int position, T item) {
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(viewHolder,position);
                }
            }
        });
        QuickRcvHolder helperViewHolder = (QuickRcvHolder) viewHolder;
        bindDataHelper(helperViewHolder, position, item);
    }

    protected abstract void bindDataHelper(QuickRcvHolder viewHolder, int position, T item);


    @Override
    public boolean isEnabled(int position) {
        return position < mList.size();
    }

    @Override
    public void addToHead(T data) {
        add(0, data);
    }


    @Override
    public boolean addToLast(T data) {
        boolean result = mList.add(data);
        notifyDataSetChanged();
        return result;
    }


    @Override
    public boolean addToHead(List<T> datas) {
        return add(0, datas);
    }


    @Override
    public boolean addToLast(List<T> datas) {
        boolean result = mList.addAll(datas);
        notifyDataSetChanged();
        return result;
    }


    @Override
    public boolean add(int startPosition, List<T> datas) {
        boolean result = mList.addAll(startPosition, datas);
        notifyDataSetChanged();
        return result;
    }


    @Override
    public void add(int startPosition, T data) {
        mList.add(startPosition, data);
        notifyDataSetChanged();
    }

    @Override
    public T getData(int index) {
        return getItemCount() == 0 ? null : mList.get(index);
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
    public void replaceAll(List<T> data) {
        mList.clear();
        add(0, data);
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


    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(BH viewHolder , int position);
    }
}
