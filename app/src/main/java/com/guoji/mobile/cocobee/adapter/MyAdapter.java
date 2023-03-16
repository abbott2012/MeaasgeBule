package com.guoji.mobile.cocobee.adapter;


import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.model.CarScallResponse;

import java.util.List;

/**
 * Created by Administrator on 2017/5/5.
 */

public class MyAdapter extends QuickRcvAdapter<CarScallResponse> {

    public MyAdapter(Context context, List data, int... layoutId) {
        super(context, data, R.layout.item_list);
    }


    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, CarScallResponse item) {
        viewHolder.setText(R.id.tv_label,item.getGrade());
        viewHolder.setText(R.id.tv_price,item.getService_price() + "元");
        viewHolder.setText(R.id.tv_life,item.getLife() + "年");
    }
}
