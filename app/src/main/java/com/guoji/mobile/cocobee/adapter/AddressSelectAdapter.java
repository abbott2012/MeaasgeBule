package com.guoji.mobile.cocobee.adapter;

import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.GetUserOrg;

import java.util.List;

/**
 * 查询的recycleView的adapter
 */

public class AddressSelectAdapter extends QuickRcvAdapter<GetUserOrg> {

    private final Context mContext;
    private List<GetUserOrg> mData;

    public AddressSelectAdapter(Context context, List<GetUserOrg> data, int... layoutId) {
        super(context, data, R.layout.address_select_item);
        mContext = context;
        mData = data;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, final int position, GetUserOrg item) {
        viewHolder.setText(R.id.tv_name, item.getOrgname());
        if (CheckUtils.equalsString(item.getSelectPosition(),position + "") ) {//被选中的条目
            viewHolder.setVisible(R.id.iv_select, true);
            viewHolder.setTextColor(R.id.tv_name, mContext.getResources().getColor(R.color.color_3270ed));
        } else {
            viewHolder.setTextColor(R.id.tv_name,mContext.getResources().getColor( R.color.color_000000));
            viewHolder.setVisible(R.id.iv_select, false);

        }
    }


}
