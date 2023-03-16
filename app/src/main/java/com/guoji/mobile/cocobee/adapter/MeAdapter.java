package com.guoji.mobile.cocobee.adapter;

import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.utils.ImageUtil;

import java.util.List;

/**
 * 查询的recycleView的adapter
 */

public class MeAdapter extends QuickRcvAdapter<HomeRecResponse> {

    public MeAdapter(Context context, List<HomeRecResponse> data, int... layoutId) {
        super(context, data, R.layout.me_car_person);
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, final int position, HomeRecResponse item) {
        if (CheckUtils.equalsString(item.getTarget_id(), AppConstants.TYPE_CAR)) {//车
            String firstPic = getFirstPic(item.getCcarpicurl());
            ImageUtil.loadCarSmallAvatar(mContext,Path.IMG_BASIC_PATH + firstPic, viewHolder.getView(R.id.iv_car_pic));
            if (CheckUtils.equalsString(item.getCard_id(), AppConstants.TYPE_CARD_TIYAN)) {//体验卡
                viewHolder.setText(R.id.tv_car_name, item.getRemarkname());
            } else {
                viewHolder.setText(R.id.tv_car_name, item.getCno());
            }
        } else {//人
            String firstPic = getFirstPic(item.getPhotourl());
            ImageUtil.loadPersonSmallAvatar(mContext,Path.IMG_BASIC_PATH + firstPic, viewHolder.getView(R.id.iv_car_pic));
            viewHolder.setText(R.id.tv_car_name, item.getPname());
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
