package com.bql.pulltorefreshandloadmore.loadmorestyle;

import android.content.Context;

/**
 * ClassName: LoadMoreStyle <br>
 * Description: LoadMore样式<br>
 * Author: Cyarie <br>
 * Created: 2016/4/15 10:53 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class LoadMoreStyle {

    /**
     * 获取自定义加载更多的LoadMoreView
     *
     * @param context 上下文
     * @return
     */
    public static AnimLoadMoreView getAnimLoadMoreView(Context context) {
        return new AnimLoadMoreView(context);
    }
}
