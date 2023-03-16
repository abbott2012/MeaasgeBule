package com.bql.tablayout.listener;

/**
 * ClassName: OnTabSelectListener2 <br>
 * Description: <br>
 * Author: Cyarie <br>
 * Created: 2016/9/28 11:09 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public interface OnTabSelectListener2 {

    /**
     * Tab 选中监听
     *
     * @param position
     * @param lastPos
     */
    void onTabSelect(int position, int lastPos);

    /**
     * Tab 重复选中监听
     *
     * @param position
     */
    void onTabReselect(int position);
}
