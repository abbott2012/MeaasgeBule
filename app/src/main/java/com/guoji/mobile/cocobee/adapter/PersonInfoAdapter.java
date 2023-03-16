package com.guoji.mobile.cocobee.adapter;

import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.PersonInfoResponse;

import java.util.List;

/**
 * 查看车辆信息的recycleView的adapter
 */

public class PersonInfoAdapter extends QuickRcvAdapter<PersonInfoResponse> {


    private final Context mContext;
    private List<PersonInfoResponse> mData;

    public PersonInfoAdapter(Context context, List<PersonInfoResponse> data, int... layoutId) {
        super(context, data, R.layout.car_info_item);
        mContext = context;
        mData = data;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, final PersonInfoResponse item) {

    }


}
