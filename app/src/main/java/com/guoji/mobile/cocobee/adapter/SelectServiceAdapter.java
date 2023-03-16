package com.guoji.mobile.cocobee.adapter;

import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.response.SelectServiceResponse;

import java.util.List;

/**
 * 查看车辆信息的recycleView的adapter
 */

public class SelectServiceAdapter extends QuickRcvAdapter<SelectServiceResponse> {


    private final Context mContext;
    private List<SelectServiceResponse> mData;

    public SelectServiceAdapter(Context context, List<SelectServiceResponse> data, int... layoutId) {
        super(context, data, R.layout.select_service_item);
        mContext = context;
        mData = data;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, final SelectServiceResponse item) {
        viewHolder.setText(R.id.tv_card_title,item.getCard_name());
        viewHolder.setText(R.id.tv_card_content,item.getCard_ddesc());
        if (CheckUtils.equalsString(item.getCard_id(), AppConstants.TYPE_CARD_ANXIN)){//安心卡
            viewHolder.setBackgroundColorRes(R.id.ll_card_type,R.drawable.card_bg_orange);
        }else if (CheckUtils.equalsString(item.getCard_id(), AppConstants.TYPE_CARD_TIYAN)){//体验卡
            viewHolder.setBackgroundColorRes(R.id.ll_card_type,R.drawable.card_bg_purple);
        }else if (CheckUtils.equalsString(item.getCard_id(), AppConstants.TYPE_CARD_DINZHI)){//定制卡
            viewHolder.setBackgroundColorRes(R.id.ll_card_type,R.drawable.corner_rec_blue);
        }
    }


}
