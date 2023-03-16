package com.guoji.mobile.cocobee.adapter;


import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.model.TagSearch;

import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Administrator on 2017/5/5.
 */

public class ExcelAdapter extends QuickRcvAdapter<TagSearch> {

    private final Context mContext;

    public ExcelAdapter(Context context, List data, int... layoutId) {
        super(context, data, R.layout.item_excel_list);
        mContext = context;
    }


    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, TagSearch item) {
        viewHolder.setText(R.id.tv_id, item.getTagId());
        viewHolder.setText(R.id.tv_name, item.getDesc());
        viewHolder.setText(R.id.tv_sex, item.getIsRead());
        if (CheckUtils.equalsString(item.getIsRead(), "N")) {
            viewHolder.setTextColor(R.id.tv_sex, mContext.getResources().getColor(R.color.color_ff0000));
        } else if (CheckUtils.equalsString(item.getIsRead(), "Y")) {
            viewHolder.setTextColor(R.id.tv_sex, mContext.getResources().getColor(R.color.color_025602));

        }
        TextView tvName = viewHolder.getView(R.id.tv_name);
        tvName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(mContext, SweetAlertDialog.NORMAL_TYPE);
                sweetAlertDialog.setTitleText("资产描述");
                sweetAlertDialog.setContentText(item.getDesc());
                sweetAlertDialog.show();
            }
        });
    }
}
