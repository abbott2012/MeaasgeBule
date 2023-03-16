package com.guoji.mobile.cocobee.activity.me;

import android.os.Bundle;


import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.fragment.MessageFragment;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * Created by Administrator on 2017/5/11.
 */

public class MessageActivity extends BaseActivity{
    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_container;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            loadRootFragment(R.id.fl_container, MessageFragment.getInstance());
        }
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }
}
