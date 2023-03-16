package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bql.recyclerview.swipe.SwipeMenuAdapter;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.MsgResponse;

import java.util.List;

/**
 * Created by liu on 2016/10/29.
 * 优惠券消息adapter
 */
public class MessageAdapter extends SwipeMenuAdapter<MessageAdapter.MessageAdapterViewHolder> {
    private List<MsgResponse> mList;
    private Context mContext;

    public MessageAdapter(Context context, List<MsgResponse> list) {
        mList = list;
        this.mContext = context;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
    }

    @Override
    public MessageAdapterViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new MessageAdapterViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(MessageAdapterViewHolder holder, int position) {
        holder.setData(mContext, mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    static class MessageAdapterViewHolder extends RecyclerView.ViewHolder {

        private final TextView time;
        private final TextView content;
        private final TextView title;

        public MessageAdapterViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_mes_title);
            content = (TextView) itemView.findViewById(R.id.tv_mes_content);
            time = (TextView) itemView.findViewById(R.id.tv_mes_time);
        }

        public void setData(Context context, MsgResponse msgResponse) {
            if (CheckUtils.equalsString(msgResponse.getPost_type(), "1")) {
                title.setText("移动报警");
            } else if (CheckUtils.equalsString(msgResponse.getPost_type(), "2")) {
                title.setText("防拆报警");
            }else if (CheckUtils.equalsString(msgResponse.getPost_type(), "3")){
                title.setText("低电报警");
            }
            content.setText(msgResponse.getPost_content());
            time.setText(msgResponse.getPost_time());
        }
    }
}