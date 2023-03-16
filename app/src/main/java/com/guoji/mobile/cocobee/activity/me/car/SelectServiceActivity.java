package com.guoji.mobile.cocobee.activity.me.car;

import android.content.Intent;
import android.os.Bundle;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.fragment.car.SelectServiceFragment;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * 车辆信息
 * Created by Administrator on 2017/4/23.
 */

public class SelectServiceActivity extends BaseActivity{
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_container;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        Intent intent = getIntent();
        String target_id = intent.getStringExtra("target_id");
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, SelectServiceFragment.getInstance(target_id));
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }
}

