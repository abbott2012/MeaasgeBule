package com.guoji.mobile.cocobee.utils;


import com.bql.tablayout.listener.CustomTabEntity;

/**
 * ClassName: MainTabEntity <br>
 * Description: 主页TabEntity<br>
 * Author: Cyarie <br>
 * Created: 2016/4/19 10:08 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class MainTabEntity implements CustomTabEntity {

    private String title;
    private int normalIcon;
    private int selectedIcon;

    /**
     * @param title        标题
     * @param normalIcon   未选中Icon
     * @param selectedIcon 选中Icon
     */
    public MainTabEntity(String title, int normalIcon, int selectedIcon) {
        this.title = title;
        this.normalIcon = normalIcon;
        this.selectedIcon = selectedIcon;
    }

    @Override
    public String getTabTitle() {
        return title;
    }

    @Override
    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    @Override
    public int getTabUnselectedIcon() {
        return normalIcon;
    }

    @Override
    public int getTabSelector() {
        return 0;
    }
}
