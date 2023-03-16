package com.bql.tablayout.listener;

/**
 * Tab选中监听接口
 * Created by Cyarie on 2015/12/25.
 */
public interface OnTabSelectListener {

    /**
     * Tab 选中监听
     *
     * @param position
     */
    void onTabSelect(int position);

    /**
     * Tab 重复选中监听
     *
     * @param position
     */
    void onTabReselect(int position);
}
