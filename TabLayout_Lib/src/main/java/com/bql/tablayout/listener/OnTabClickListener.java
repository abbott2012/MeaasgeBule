package com.bql.tablayout.listener;

/**
 * ClassName: OnTabClickListener <br>
 * Description: Tab点击选中监听<br>
 * Author: Cyarie <br>
 * Created: 2016/7/18 11:16 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public interface OnTabClickListener {

    /**
     * Tab 选中监听
     *
     * @param curPos 当前位置
     * @param prePos 先前位置
     */
    void onTabClick(int curPos, int prePos);

    /**
     * Tab 重复选中监听
     *
     * @param position
     */
    void onTabReClick(int position);
}
