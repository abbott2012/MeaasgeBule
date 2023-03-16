package com.bql.pulltorefreshandloadmore.loadmoreview;

/**
 * ClassName: LoadMoreMode <br>
 * Description: 加载更多方式<br>
 * Author: Cyarie <br>
 * Created: 2016/4/14 15:39 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public enum LoadMoreMode {
    /**
     * 点击加载更多
     */
    CLICK,
    /**
     * 滑动到底部加载更多
     */
    SCROLL;

    static LoadMoreMode int2Value(int modeInt) {
        switch (modeInt) {
            case 0x0:
            default:
                return CLICK;
            case 0x1:
                return SCROLL;
        }
    }

}
