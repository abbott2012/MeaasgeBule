package com.guoji.mobile.cocobee.adapter;

import android.content.Context;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.QueryRecResponse;

import java.util.List;

/**
*查询的recycleView的条目的recycleView的adapter
 */

public class QueryItemAdapter extends QuickRcvAdapter<QueryRecResponse.QueryResponse> {


    private  List<QueryRecResponse.QueryResponse> mData;

    public QueryItemAdapter(Context context, List<QueryRecResponse.QueryResponse> data, int... layoutId) {
        super(context, data, R.layout.query_item_adapter,R.layout.query_item_adapter,R.layout.query_item_adapter);
        mData = data;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, int position, QueryRecResponse.QueryResponse item) {
        int pic = item.getPic();
        String text = item.getText();
        viewHolder.setImageResource(R.id.itemImage,pic);
        viewHolder.setText(R.id.itemText,text);
    }

    @Override
    public int getItemViewType(int position) {
        if (mData != null && mData.size() == 1) {
            return 0;
        }else if (mData != null && mData.size() == 3){
            return 1;
        }else {
            return 2;
        }
    }

}
