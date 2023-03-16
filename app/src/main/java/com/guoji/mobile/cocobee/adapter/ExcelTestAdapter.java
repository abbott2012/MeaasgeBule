package com.guoji.mobile.cocobee.adapter;


import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.ExcelResponse;

import java.util.List;

/**
 * Created by Administrator on 2017/5/5.
 */

public class ExcelTestAdapter extends QuickRcvAdapter<ExcelResponse> {

    public ExcelTestAdapter(Context context, List data, int... layoutId) {
        super(context, data, R.layout.item_excel_list);
    }


    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, ExcelResponse item) {
        viewHolder.setText(R.id.tv_id, item.getId());
        viewHolder.setText(R.id.tv_name, item.getName());
        viewHolder.setText(R.id.tv_sex, item.getSex());
//        viewHolder.setText(R.id.tv_age, item.getAge());
    }
}
