package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.response.QueryRecResponse;
import com.guoji.mobile.cocobee.view.GridItemDecoration;

import java.util.List;

/**
 * 查询的recycleView的adapter
 */

public class QueryAdapter extends QuickRcvAdapter<QueryRecResponse> {


    private final Context mContext;
    private List<QueryRecResponse> mData;

    public QueryAdapter(Context context, List<QueryRecResponse> data, int... layoutId) {
        super(context, data, R.layout.query_rec_item);
        mContext = context;
        mData = data;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, final int position, QueryRecResponse item) {
        String title = item.getTitle();
        List<QueryRecResponse.QueryResponse> queryResponseList = item.getQueryResponseList();
        viewHolder.setText(R.id.recycle_title, title);
        RecyclerView recyclerView = viewHolder.getView(R.id.recycle_view);
        QueryItemAdapter queryItemAdapter = new QueryItemAdapter(mContext, queryResponseList);
        int itemViewType = queryItemAdapter.getItemViewType(position);
        if (itemViewType == 0) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 1));
        }else if (itemViewType == 1) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
        }else if (itemViewType == 2) {
            recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
        }

//        if (TextUtils.equals(mFragment_tag, AppConstants.QUERY_FRAGMENT)) {
//            //2种显示类型
//            if (itemViewType == 0) {
//                recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
//            }
//
//        } else if (TextUtils.equals(mFragment_tag, AppConstants.MANAGER_FRAGMENT)) {
//            //2种显示类型
//            if (itemViewType == 1) {
//                recyclerView.setLayoutManager(new GridLayoutManager(mContext, 3));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(mContext, 2));
//            }
//        }
        recyclerView.addItemDecoration(new GridItemDecoration(mContext));
        recyclerView.setAdapter(queryItemAdapter);
        queryItemAdapter.setOnItemClickListener(new OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewholder, int position1) {
                mOnAdapterClick.onAdapterClick(position1, position);
            }
        });
    }


    private OnAdapterClick mOnAdapterClick;

    public void setOnAdapterClick(OnAdapterClick onAdapterClick) {
        this.mOnAdapterClick = onAdapterClick;
    }

    public interface OnAdapterClick {
        void onAdapterClick(int position1, int position);
    }


}
