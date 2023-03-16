package com.bql.baseadapter.absListView;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>ViewHolder基类 减少代码</p>
 * Created by Cyarie on 2016/3/30.
 */
public class BaseViewHolder {

    /**
     * 保存控件的集合
     */
    private SparseArray<View> mViews = new SparseArray<View>();
    /**
     * 保存布局view的集合
     */
    private SparseArray<View> mConvertViews = new SparseArray<View>();

    /**
     * 位置
     */
    private int mPosition;

    /**
     * 单个布局view
     */
    private View mConvertView;

    /**
     * layoutId
     */
    protected int mLayoutId;

    /**
     * 上下文
     */
    protected Context mContext;


    /**
     * 构造函数
     *
     * @param context
     * @param position
     * @param parent
     * @param layoutId
     */
    public BaseViewHolder(Context context, int position, ViewGroup parent,
                          int layoutId) {
        mConvertView = mConvertViews.get(layoutId);
        mPosition = position;
        mContext = context;
        mLayoutId = layoutId;
        if (mConvertView == null) {
            mConvertView = LayoutInflater.from(context).inflate(layoutId, parent,
                    false);
            mConvertViews.put(layoutId, mConvertView);
            mConvertView.setTag(this);
        }
    }

    public BaseViewHolder() {

    }

    /**
     * 获取BaseViewHolder实例
     *
     * @param context  上下文
     * @param position 位置
     * @param parent   viewGroup
     * @param layoutId 对应的布局Id
     * @return BaseViewHolder实例
     */
    public <BH extends BaseViewHolder> BH get(Context context, int position,
                                              View convertView, ViewGroup parent, int layoutId) {
        if (convertView == null) {
            return (BH) new BaseViewHolder(context, position, parent, layoutId);
        } else {
            BaseViewHolder bHolder = (BaseViewHolder) convertView.getTag();
            if (bHolder.mLayoutId != layoutId) {
                return (BH) new BaseViewHolder(context, position, parent, layoutId);
            }
            bHolder.setPosition(position);
            return (BH) bHolder;
        }
    }

    /**
     * 获取viewId对应的控件
     *
     * @param viewId
     * @param <R>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <R extends View> R getView(int viewId) {
        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (R) view;
    }


    /**
     * 当没有指定layoutId的时候，返回队列中的第一个
     *
     * @return
     */
    public View getConvertView() {
        return mConvertViews.valueAt(0);
    }

    /**
     * 返回队列中指定layoutId对应的view
     *
     * @param layoutId
     * @return
     */
    public View getConvertView(int layoutId) {
        return mConvertViews.get(layoutId);
    }

    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public int getLayoutId() {
        return mLayoutId;
    }
}
