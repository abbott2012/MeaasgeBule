package com.guoji.mobile.cocobee.activity.base;


import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.common.Path;
import com.lzy.okgo.OkGo;

import java.io.File;

import me.yokeyword.fragmentation.anim.DefaultHorizontalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * ClassName: BaseActivity <br>
 * Description: 基类Activity<br>
 * Author: Cyarie <br>
 * Created: 2016/7/15 15:50 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class BaseActivity extends BaseAppActivity {


    @Override
    protected void onHandleEvent(EventManager eventManager) {

    }

    @Override
    protected boolean isBindEventBusHere() {
        return true;
    }

    @Override
    protected boolean toggleOverridePendingTransition() {
        return false;
    }

    @Override
    protected TransitionMode getOverridePendingTransitionMode() {
        return TransitionMode.LEFT;
    }

    @Override
    protected ScreenOrientationMode getScreenOrientationMode() {
        return ScreenOrientationMode.PORTRAIT;
    }

    @Override
    protected boolean isApplyStatusBarTranslucent() {
        return false;
    }

    @Override
    protected boolean isHideSmartBar() {
        return true;
    }

    @Override
    protected boolean isFullScreen() {
        return false;
    }

    @Override
    protected boolean enableChangeSKin() {
        return true;
    }

    @Override
    protected boolean isStatusDarkMode() {
        return false;
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultHorizontalAnimator();
    }

    @Override
    protected void onDestroy() {

        //当前页面销毁时，如果临时图片文件夹中有图片，则清空
        File f = new File(Path.IMAGE_TEMP_FILE_PATH);
        File[] files = f.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                file.delete();
            }
        }

        OkGo.getInstance().cancelTag(this);

        super.onDestroy();
    }
}
