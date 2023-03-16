package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.me.LabelBinding;
import com.guoji.mobile.cocobee.activity.pay.BuyPoliceActivity;
import com.guoji.mobile.cocobee.model.Car;
import com.guoji.mobile.cocobee.response.CarInfoResponse;
import com.guoji.mobile.cocobee.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 查看车辆信息的recycleView的adapter
 */

public class CarInfoAdapter extends QuickRcvAdapter<CarInfoResponse> {


    private final Context mContext;
    private List<CarInfoResponse> mData;

    public CarInfoAdapter(Context context, List<CarInfoResponse> data, int... layoutId) {
        super(context, data, R.layout.car_info_item);
        mContext = context;
        mData = data;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, final CarInfoResponse item) {
        viewHolder.setText(R.id.tv_car_num, item.getCno());
        TextView bindBiaoQian = viewHolder.getView(R.id.tv_bind_biaoqian);
        TextView tvBuyPol = viewHolder.getView(R.id.tv_buy_pol);
        LinearLayout carInfoItem = viewHolder.getView(R.id.car_info_item);
        LinearLayout llBindBiaoqian = viewHolder.getView(R.id.ll_bind_biaoqian);
        LinearLayout llBuyBaoxian = viewHolder.getView(R.id.ll_buy_baoxian);

        String lno = item.getLno();
        String orderId = item.getOrder_id();
        String configsafe = Utils.getUserLoginInfo().getConfigsafe();
        if (!CheckUtils.equalsString(configsafe, "0")) {
            if (CheckUtils.isEmpty(lno)) {//未绑定标签
                bindBiaoQian.setText("未绑定标签");
                tvBuyPol.setText("未购买保险");
                bindBiaoQian.setTextColor(mContext.getResources().getColor(R.color.color_595959));
                tvBuyPol.setTextColor(mContext.getResources().getColor(R.color.color_595959));
                carInfoItem.setBackgroundResource(R.drawable.corner_rec_yellow);
                llBindBiaoqian.setVisibility(View.VISIBLE);
                llBuyBaoxian.setVisibility(View.GONE);
            } else if (!CheckUtils.isEmpty(lno) && CheckUtils.isEmpty(orderId)) {//已绑定标签,,未购买保险
                bindBiaoQian.setText("已绑定标签");
                tvBuyPol.setText("未购买保险");
                bindBiaoQian.setTextColor(mContext.getResources().getColor(R.color.white));
                tvBuyPol.setTextColor(mContext.getResources().getColor(R.color.color_595959));
                carInfoItem.setBackgroundResource(R.drawable.corner_rec_yellow);
                llBindBiaoqian.setVisibility(View.GONE);
                llBuyBaoxian.setVisibility(View.VISIBLE);
            } else {//已购买保险
                bindBiaoQian.setText("已绑定标签");
                tvBuyPol.setText("已购买保险");
                bindBiaoQian.setTextColor(mContext.getResources().getColor(R.color.white));
                tvBuyPol.setTextColor(mContext.getResources().getColor(R.color.white));
                carInfoItem.setBackgroundResource(R.drawable.corner_rec_blue);
                llBindBiaoqian.setVisibility(View.GONE);
                llBuyBaoxian.setVisibility(View.GONE);
            }
        } else {
            tvBuyPol.setVisibility(View.GONE);
            llBuyBaoxian.setVisibility(View.GONE);
            carInfoItem.setBackgroundResource(R.drawable.corner_rec_yellow);
            if (CheckUtils.isEmpty(lno)) {//未绑定标签
                bindBiaoQian.setText("未绑定标签");
                bindBiaoQian.setTextColor(mContext.getResources().getColor(R.color.color_595959));
                llBindBiaoqian.setVisibility(View.VISIBLE);
            } else {
                bindBiaoQian.setText("已绑定标签");
                bindBiaoQian.setTextColor(mContext.getResources().getColor(R.color.white));
                llBindBiaoqian.setVisibility(View.GONE);
            }
        }

        llBindBiaoqian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Car car = new Car();
                car.setCid(item.getCid());
                car.setCno(item.getCno());
                car.setIslocked(item.getIslocked());
                if (!CheckUtils.isEmpty(item.getLno())) {
                    car.setLabelid(item.getLno());
                }
                car.setOrder_id(item.getOrder_id());
                car.setCbuyprice(item.getCbuyprice());
                car.setCbuytime(item.getCbuytime());
                car.setCbuytype(item.getCbuytype());
                Intent intent = new Intent(mContext, LabelBinding.class);
                intent.putExtra("car_info", car);
                mContext.startActivity(intent);
            }
        });
        llBuyBaoxian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, BuyPoliceActivity.class);
                intent.putExtra("from", 2146);
                intent.putExtra("tagid", item.getLno());//标签号
                intent.putExtra("name", item.getPname());//车主姓名
                intent.putExtra("phone", item.getMobile());//
                intent.putExtra("idcard", item.getIdcard());//身份证号码
                intent.putExtra("grade", item.getCno());//车牌号
                intent.putExtra("model", item.getCbuytype());//品牌型号
                intent.putExtra("motor", item.getCdevice());//发动机号
                intent.putExtra("date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                mContext.startActivity(intent);
            }
        });
    }


}
