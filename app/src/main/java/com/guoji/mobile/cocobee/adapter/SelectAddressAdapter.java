package com.guoji.mobile.cocobee.adapter;

import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.SelectAddressResponse;

import java.util.List;

/**
 * 查看车辆信息的recycleView的adapter
 */

public class SelectAddressAdapter extends QuickRcvAdapter<SelectAddressResponse> {

    public SelectAddressAdapter(Context context, List<SelectAddressResponse> data, int... layoutId) {
        super(context, data, R.layout.select_address_item);
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, final SelectAddressResponse item) {
        viewHolder.setText(R.id.tv_selected,item.getOrgname());
    }

}
