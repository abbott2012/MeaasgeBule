package com.guoji.mobile.cocobee.activity.base;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.bql.utils.EventManager;
import com.bql.utils.SmartBarUtils;
import com.bql.utils.StatusBarUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.KeyboardUtils;
import com.guoji.mobile.cocobee.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportActivity;

/**
 * ClassName: BaseAppActivity <br>
 * Description: 基类属性大全Activity<br>
 * See {@link SupportActivity} Fragment管理器
 * Author: Cyarie <br>
 * Created: 2016/7/15 15:18 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class BaseAppActivity extends SupportActivity {

    /**
     * Log 名称
     */
    protected static String TAG = BaseAppActivity.class.getSimpleName();

    protected ElectricVehicleApp app;
    protected User user;

    /**
     * Activity切换动画模式
     */
    public enum TransitionMode {
        LEFT, RIGHT, TOP, BOTTOM, SCALE, FADE, NONE, MAIN
    }

    /**
     * 屏幕方向
     */
    public enum ScreenOrientationMode {
        PORTRAIT, LANDSCAPE
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppManager.getAppManager().addActivity(this);
        app = (ElectricVehicleApp) this.getApplication();
        user = Utils.getUserLoginInfo();

        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case NONE:
                    overridePendingTransition(0, 0);
                    break;
                case MAIN:
                    overridePendingTransition(0, 0);
                    break;
            }
        }
        if (getScreenOrientationMode() == ScreenOrientationMode.PORTRAIT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        if (isFullScreen()) {
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
        }
        super.onCreate(savedInstanceState);

        if (enableChangeSKin()) {
//                        SkinManager.getInstance().register(this);
        }

        if (isBindEventBusHere()) {
            EventBus.getDefault().register(this);
        }
        //状态栏透明
        setTranslucentStatus(isApplyStatusBarTranslucent());

        if (isStatusDarkMode()) {
            //状态栏黑色
            StatusBarUtils.setStatusBarDarkIcon(this, true);
        }

        //隐藏SmartBar
        if (SmartBarUtils.hasSmartBar() && isHideSmartBar()) {
            SmartBarUtils.hide(getWindow().getDecorView());
        }
        //setContentView ButterKnife绑定控件
        if (getContentViewLayoutID() >= 0) {
            if (getContentViewLayoutID() > 0) {
                setContentView(getContentViewLayoutID());
                ButterKnife.bind(this);
            }
        } else {
            throw new IllegalArgumentException("You must return a right contentView layout resource Id");
        }

        //初始化
        initViewsAndEvents(savedInstanceState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        KeyboardUtils.hideKeyboard(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (enableChangeSKin()) {
//            SkinManager.getInstance().unregister(this);
        }
        if (isBindEventBusHere()) {
            EventBus.getDefault().unregister(this);
        }
        AppManager.getAppManager().finishActivity(this);
    }


    @Override
    public void finish() {
        super.finish();
        if (toggleOverridePendingTransition()) {
            switch (getOverridePendingTransitionMode()) {
                case LEFT:
                    overridePendingTransition(R.anim.left_in, R.anim.left_out);
                    break;
                case RIGHT:
                    overridePendingTransition(R.anim.right_in, R.anim.right_out);
                    break;
                case TOP:
                    overridePendingTransition(R.anim.top_in, R.anim.top_out);
                    break;
                case BOTTOM:
                    overridePendingTransition(R.anim.bottom_in, R.anim.bottom_out);
                    break;
                case SCALE:
                    overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                    break;
                case FADE:
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    break;
                case NONE:
                    overridePendingTransition(0, 0);
                    break;
            }
        }
    }

    /**
     * set status bar translucency
     *
     * @param on
     */
    protected void setTranslucentStatus(boolean on) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            if (on) {
                winParams.flags |= bits;
            } else {
                winParams.flags &= ~bits;
            }
            win.setAttributes(winParams);
        }
    }

    @Subscribe
    public void onEventMainThread(EventManager eventManager) {
        if (null != eventManager) {
            onHandleEvent(eventManager);
        }
    }


    /**
     * bind layout resource file
     *
     * @return id of layout resource
     */
    protected abstract int getContentViewLayoutID();


    /**
     * init all views and add events
     */
    protected abstract void initViewsAndEvents(Bundle savedInstanceState);


    /**
     * is bind eventBus
     *
     * @return
     */
    protected abstract boolean isBindEventBusHere();


    /**
     * handle something when event coming
     *
     * @return
     */
    protected abstract void onHandleEvent(EventManager eventManager);

    /**
     * toggle overridePendingTransition
     *
     * @return
     */
    protected abstract boolean toggleOverridePendingTransition();

    /**
     * get the overridePendingTransition mode
     */
    protected abstract TransitionMode getOverridePendingTransitionMode();


    /**
     * get the screenOrientation mode
     */
    protected abstract ScreenOrientationMode getScreenOrientationMode();


    /**
     * is applyStatusBarTranslucency
     *
     * @return
     */
    protected abstract boolean isApplyStatusBarTranslucent();


    /**
     * is hide smart bar
     *
     * @return
     */
    protected abstract boolean isHideSmartBar();

    /**
     * is full screen
     *
     * @return
     */
    protected abstract boolean isFullScreen();

    /**
     * enable change skin
     *
     * @return
     */
    protected abstract boolean enableChangeSKin();

    /**
     * is dark status bar
     *
     * @return
     */
    protected abstract boolean isStatusDarkMode();

}
