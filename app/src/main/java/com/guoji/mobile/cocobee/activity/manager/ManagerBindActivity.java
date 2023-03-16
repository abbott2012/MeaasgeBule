package com.guoji.mobile.cocobee.activity.manager;

import android.os.Bundle;


import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by Administrator on 2017/6/28.
 */

public class ManagerBindActivity extends BaseActivity {
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_container;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
//            loadRootFragment(R.id.fl_container, ManagerBindFragment.getInstance());
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }
}
