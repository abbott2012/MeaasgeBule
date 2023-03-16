package com.guoji.mobile.cocobee.activity.me;

import android.content.Intent;
import android.os.Bundle;


import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.fragment.home.AlermFragment;
import com.guoji.mobile.cocobee.response.HomeRecResponse;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by Administrator on 2017/6/28.
 */

public class AlermActivity extends BaseActivity {
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_container;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        Intent intent = getIntent();
        HomeRecResponse homeRecResponse = (HomeRecResponse) intent.getSerializableExtra("homeRecResponse");
        String address = intent.getStringExtra("address");
        String longitude = intent.getStringExtra("longitude");
        String latitude = intent.getStringExtra("latitude");
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, AlermFragment.getInstance(homeRecResponse, address, latitude, longitude));
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }
}
