package com.guoji.mobile.cocobee.adapter;

import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.utils.ImageUtil;

import java.util.List;

/**
 * 查询的recycleView的adapter
 */

public class HomeAdapter extends QuickRcvAdapter<HomeRecResponse> {


    private final Context mContext;
    private List<HomeRecResponse> mData;

    public HomeAdapter(Context context, List<HomeRecResponse> data, int... layoutId) {
        super(context, data, R.layout.home_car_person);
        mContext = context;
        mData = data;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, final int position, HomeRecResponse item) {
        if (item.getFlag() == position) {
            viewHolder.setVisible(R.id.iv_out_line, true);
            viewHolder.setTextColor(R.id.tv_car_name, mContext.getResources().getColor(R.color.color_3270ed));
        } else {
            viewHolder.setVisible(R.id.iv_out_line, false);
            viewHolder.setTextColor(R.id.tv_car_name, mContext.getResources().getColor(R.color.color_404040));
        }
        if (CheckUtils.equalsString(item.getTarget_id(), "38")) {//车
            String firstPic = getFirstPic(item.getCcarpicurl());
            ImageUtil.loadCarSmallAvatar(mContext,Path.IMG_BASIC_PATH + firstPic, viewHolder.getView(R.id.iv_car_pic));
            if (CheckUtils.equalsString(item.getCard_id(), "40")) {//体验卡
                viewHolder.setText(R.id.tv_car_name, item.getRemarkname());
            } else {
                viewHolder.setText(R.id.tv_car_name, item.getCno());
            }
        } else {//人
            String firstPic = getFirstPic(item.getPhotourl());
            ImageUtil.loadPersonSmallAvatar(mContext,Path.IMG_BASIC_PATH + firstPic, viewHolder.getView(R.id.iv_car_pic));
            viewHolder.setText(R.id.tv_car_name, item.getPname());
        }
        if (CheckUtils.equalsString(item.getFinish(), "no")) {//未完成订单
            viewHolder.setText(R.id.tv_car_name, "未完成");
            viewHolder.setTextColor(R.id.tv_car_name, mContext.getResources().getColor(R.color.color_ff0000));
        }

    }

    private String getFirstPic(String photourl) {
        if (!CheckUtils.isEmpty(photourl)) {
            String[] carPic = photourl.split(",");
            if (carPic != null && carPic.length > 0) {
                return carPic[0];
            }
        }
        return null;
    }

}
