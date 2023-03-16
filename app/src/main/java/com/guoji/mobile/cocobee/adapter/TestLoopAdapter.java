package com.guoji.mobile.cocobee.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.response.AdResponse;
import com.guoji.mobile.cocobee.utils.ImageUtil;
import com.jude.rollviewpager.RollPagerView;
import com.jude.rollviewpager.adapter.LoopPagerAdapter;

import java.util.List;

public class TestLoopAdapter extends LoopPagerAdapter {

    private final List<AdResponse> mList;
    private final Context mContext;

    private int[] imgs = {R.drawable.appbanner01, R.drawable.appbanner02};

    public TestLoopAdapter(Context context, RollPagerView viewPager, List<AdResponse> list) {
        super(viewPager);
        mContext = context;
        mList = list;
    }

    @Override
    public View getView(ViewGroup container, int position) {
        ImageView view = new ImageView(container.getContext());
        if (mList == null || mList.size() == 0) {
            view.setImageResource(imgs[position]);
        } else {
            ImageUtil.loadPic(mContext, Path.IMG_BASIC_PATH + mList.get(position).getAd_pic_url(), view);
        }
        view.setScaleType(ImageView.ScaleType.FIT_XY);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        return view;
    }

    @Override
    public int getRealCount() {
        if (mList == null || mList.size() == 0) {
            return imgs.length;
        }
        return mList.size();
    }
}