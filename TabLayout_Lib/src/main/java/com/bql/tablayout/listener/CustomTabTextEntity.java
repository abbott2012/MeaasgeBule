package com.bql.tablayout.listener;

/**
 *
 * Created by ben on 2016/7/15.
 */
public interface CustomTabTextEntity {

    /**
     * 获取Tab的标签上标题
     *
     * @return
     */
    String getTabTitle();

    /**
     * 获取Tab的标签的下标题
     */
    String getTabSubTitle();


    /**
     * 获取Tab选中的文字颜色
     *
     * @return
     */
    int getTabSelectedColor();

    /**
     * @return 获取Tab未选中的文字颜色
     */
    int getTabUnselectedIColor();
}
