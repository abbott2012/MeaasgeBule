package com.guoji.mobile.cocobee.adapter;


import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.model.PoliceStation;

import java.util.List;

/**
 * Created by Administrator on 2017/5/5.
 */

public class SelectTypeAdapter extends QuickRcvAdapter<PoliceStation> {

    public SelectTypeAdapter(Context context, List data, int... layoutId) {
        super(context, data, R.layout.person_type_item);
    }


    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, PoliceStation item) {
        viewHolder.setText(R.id.tv_label,item.getOrgname());
    }
}
