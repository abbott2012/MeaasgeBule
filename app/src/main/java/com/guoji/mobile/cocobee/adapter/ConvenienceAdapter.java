package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.text.Html;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.model.ConveniencePoint;

import java.util.List;

/**
 * Created by Administrator on 2017/6/14.
 */

public class ConvenienceAdapter extends QuickRcvAdapter<ConveniencePoint> {


    private final Context mContext;
    private List<ConveniencePoint> mData;

    public ConvenienceAdapter(Context context, List<ConveniencePoint> data, int... layoutId) {
        super(context, data, R.layout.pos_info_lv_item);
        mContext = context;
        mData = data;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, final ConveniencePoint item) {
        viewHolder.setVisible(R.id.online_status_tv, false);
        viewHolder.setText(R.id.recogcode_tv, Html.fromHtml("便民点名称：<font color='black'>" + item.getName() + "</font>") + "");
        viewHolder.setText(R.id.coordinates_tv, "经纬度： ( " + item.getLongitude() + " , " + item.getLatitude() + " )");
        viewHolder.setVisible(R.id.coordinates_tv, false);
        switch (item.getCategory()) {
            case "1":
                viewHolder.setText(R.id.coordinates_tv, "类别：加油站");
                viewHolder.setVisible(R.id.type_iv, true);
                viewHolder.setImageResource(R.id.type_iv, R.drawable.gas);
                break;
            case "2":
                viewHolder.setText(R.id.coordinates_tv, "类别：充电站");
                viewHolder.setVisible(R.id.type_iv, true);
                viewHolder.setImageResource(R.id.type_iv, R.drawable.charge);
                break;
            case "3":
                viewHolder.setText(R.id.coordinates_tv, "类别：维修站");
                viewHolder.setVisible(R.id.type_iv, true);
                viewHolder.setImageResource(R.id.type_iv, R.drawable.repair);
                break;
            default:
                viewHolder.setVisible(R.id.type_iv, false);
                viewHolder.setText(R.id.coordinates_tv, "类别：未定义");
                break;

        }

        viewHolder.setText(R.id.address_tv, "地址：" + item.getAddress());

    }


}
