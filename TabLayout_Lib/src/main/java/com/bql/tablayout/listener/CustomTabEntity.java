package com.bql.tablayout.listener;

/**
 * 普通的Tab实体类
 * Created by Cyarie on 2015/12/25.
 */
public interface CustomTabEntity {

    /**
     * 获取Tab的标签文字
     *
     * @return
     */
    String getTabTitle();

    /**
     * 获取Tab选中的图标
     *
     * @return
     */
    int getTabSelectedIcon();

    /**
     * @return 获取Tab未选中的图标
     */
    int getTabUnselectedIcon();

    /**
     * @return 获取Tab点击Selector
     */
    int getTabSelector();
}
