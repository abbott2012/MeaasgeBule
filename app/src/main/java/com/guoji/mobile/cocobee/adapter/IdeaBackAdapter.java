package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.baseadapter.recycleView.QuickRcvHolder;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.utils.ImageUtil;

import java.util.List;

import cn.finalteam.galleryfinal.model.PhotoInfo;

/**
 * 查询的recycleView的adapter
 */

public class IdeaBackAdapter extends QuickRcvAdapter<PhotoInfo> {

    private final Context mContext;

    public IdeaBackAdapter(Context context, List<PhotoInfo> data, int... layoutId) {
        super(context, data, R.layout.item_idea_back);
        mContext = context;
    }

    @Override
    protected void bindDataHelper(QuickRcvHolder viewHolder, final int position, PhotoInfo item) {
        ImageView view = viewHolder.getView(R.id.iv_add_pic);
//        Glide.with(mContext).load(item.getPhotoPath()).asBitmap().centerCrop().placeholder(R.drawable.add_pic).error(R.drawable.add_pic).into(view);
        ImageUtil.loadSelectPic(mContext,item.getPhotoPath(),view);
    }


}
