package com.guoji.mobile.cocobee.adapter;


import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.PersonTypeResponse;

import java.util.List;

/**
 * Created by Administrator on 2017/5/5.
 */

public class PersonTypeAdapter extends QuickRcvAdapter<PersonTypeResponse> {

    public PersonTypeAdapter(Context context, List data, int... layoutId) {
        super(context, data, R.layout.person_type_item);
    }


    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, PersonTypeResponse item) {
        viewHolder.setText(R.id.tv_label,item.getDname());
    }
}
