package com.guoji.mobile.cocobee.utils;

import android.content.Context;
import android.content.res.TypedArray;


import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;


/**
 * ClassName: DataProviderUtils <br>
 * Description: 数据提供类<br>
 * Author: Cyarie <br>
 * Created: 2016/7/15 14:05 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class DataProviderUtils {


    /**
     * 获取主页Tab标题
     *
     * @return
     */
    public static String[] getMainTabTitles() {

        return ElectricVehicleApp.getApp().getResources().getStringArray(R.array.main_items);
    }


    /**
     * 获取ID数组
     *
     * @param context
     * @param id
     * @return
     */
    public static int[] getIds(Context context, int id) {
        TypedArray ar = context.getResources().obtainTypedArray(id);
        int len = ar.length();
        int[] resIds = new int[len];
        for (int i = 0; i < len; i++) {
            resIds[i] = ar.getResourceId(i, 0);
        }
        ar.recycle();
        return resIds;
    }


    /**
     * 获取主页Tab未选中的ID数组
     *
     * @param context
     * @return
     */
    public static int[] getMainIconNorIds(Context context) {
        return getIds(context, R.array.main_icon_normal_res_ids);

    }

    /**
     * 获取主页Tab选中的ID数组
     *
     * @param context
     * @return
     */
    public static int[] getMainIconSelIds(Context context) {
        return getIds(context, R.array.main_icon_selected_res_ids);

    }

}
