package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bql.recyclerview.swipe.SwipeMenuAdapter;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.AlarmInfoResponse;
import com.guoji.mobile.cocobee.utils.Utils;

import java.util.List;

/**
 * Created by liu on 2016/10/29.
 * 报警记录adapter
 */
public class AlarmInfoAdapter extends SwipeMenuAdapter<AlarmInfoAdapter.AlarmInfoAdapterViewHolder> {
    private List<AlarmInfoResponse> mList;
    private Context mContext;

    public AlarmInfoAdapter(Context context, List<AlarmInfoResponse> list) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_alarm_info, parent, false);
    }

    @Override
    public AlarmInfoAdapterViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new AlarmInfoAdapterViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(AlarmInfoAdapterViewHolder holder, int position) {
        holder.setData(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    static class AlarmInfoAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTvAlarmCar;
        private final TextView mTvAlarmTime;
        private final TextView mTvPoliceBack;

        public AlarmInfoAdapterViewHolder(View itemView) {
            super(itemView);
            mTvAlarmCar = (TextView) itemView.findViewById(R.id.tv_alarm_car);
            mTvAlarmTime = (TextView) itemView.findViewById(R.id.tv_alarm_time);
            mTvPoliceBack = (TextView) itemView.findViewById(R.id.tv_police_back);
        }

        public void setData(AlarmInfoResponse alarmInfoResponse) {
            mTvAlarmCar.setText(alarmInfoResponse.getCbuytype() + "(" + alarmInfoResponse.getCno() + ")");
            mTvAlarmTime.setText(alarmInfoResponse.getAtime());
            mTvPoliceBack.setText(Utils.getAlarmStatus(alarmInfoResponse.getStatus()));
        }
    }
}