package com.guoji.mobile.cocobee.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.me.MeActivity;
import com.guoji.mobile.cocobee.activity.me.MessageActivity;
import com.guoji.mobile.cocobee.activity.me.QueryActivity;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.home.HomeFragment;
import com.guoji.mobile.cocobee.fragment.home.LeftFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.Tools;
import com.guoji.mobile.cocobee.utils.UpdateManager;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.dialog.AddressSelectDialog;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * 主界面
 */

public class MainActivity extends SlidingFragmentActivity {
    @BindView(R.id.fl_content)
    FrameLayout mFrameLayout;
    @BindView(R.id.topButton)
    ImageView mTopButton;
    @BindView(R.id.topTv)
    TextView mTopTv;
    @BindView(R.id.iv_top_mes)
    ImageView mIvTopMes;
    @BindView(R.id.iv_top_manager)
    ImageView mIvTopManager;
    private Context context;
    private Fragment mContent;

    /*极光推送*/
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "com.example.jpushdemo.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    private boolean exitFlag = false;

    private boolean mIsExit;
    private ElectricVehicleApp app;
    private User user;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE); // 无标题
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AppManager.getAppManager().addActivity(this);

        context = this;
        app = (ElectricVehicleApp) this.getApplication();
        user = Utils.getUserLoginInfo();
        makeProjectFileDir();
        initSlidingMenu(savedInstanceState);
        //极光推送
        loadJPush();

        //版本检查
        versionCheck();
        checkPomision();
        checkOrg();
        initManagerIcon();
    }

    private void initManagerIcon() {
        if (user.getApproleid() == Constant.NORMAL_USER) {//普通用户
            mIvTopManager.setVisibility(View.GONE);
        } else {
            mIvTopManager.setVisibility(View.VISIBLE);
        }
    }

    //检查用户是否设置过组织机构
    private void checkOrg() {
        if (CheckUtils.isEmpty(user.getOrgid())) {
            AddressSelectDialog dialog = new AddressSelectDialog();
            dialog.setCancelable(false);
            dialog.show(getSupportFragmentManager(), "address");
        }

//        AddressSelectDialog dialog = new AddressSelectDialog();
//        dialog.setCancelable(false);
//        dialog.show(getSupportFragmentManager(), "address");

    }

    //申请定位权限
    private void checkPomision() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有获取定位权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //已经禁止提示了
                Toast.makeText(this, "您已禁止该权限，需要重新开启", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 11);
            }
        }
    }

    /**
     * 初始化侧边栏
     */
    private void initSlidingMenu(Bundle savedInstanceState) {
        // 如果保存的状态不为空则得到之前保存的Fragment，否则实例化MyFragment
        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
        }

        if (mContent == null) {
            mContent = HomeFragment.getInstance();
            switchConent(mContent, "平安城市");
        }

        // 设置左侧滑动菜单
        setBehindContentView(R.layout.menu_frame_left);
        getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new LeftFragment()).commit();

        // 实例化滑动菜单对象
        SlidingMenu sm = getSlidingMenu();
        // 设置可以左右滑动的菜单
        sm.setMode(SlidingMenu.LEFT);
        // 设置滑动阴影的宽度
        sm.setShadowWidthRes(R.dimen.size15);
        // 设置滑动菜单阴影的图像资源
        sm.setShadowDrawable(null);
        // 设置滑动菜单视图的宽度
        sm.setBehindOffsetRes(R.dimen.size80);
        // 设置渐入渐出效果的值
        sm.setFadeDegree(0.35f);
        // 设置触摸屏幕的模式,这里设置为全屏/边缘
//        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
//        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);//边缘
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);//边缘
        // 设置下方视图的在滚动时的缩放比例
        sm.setBehindScrollScale(0.0f);

    }

    /**
     * 切换Fragment
     *
     * @param fragment
     */
    public void switchConent(Fragment fragment, String title) {
        mContent = fragment;
        getSupportFragmentManager().beginTransaction().replace(R.id.fl_content, fragment).commit();
        getSlidingMenu().showContent();
        mTopTv.setText(title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    //版本检查
    private void versionCheck() {
        SharedPreferences sp = getSharedPreferences("vc", Context.MODE_PRIVATE);
        boolean unRemind = sp.getBoolean("unRemind", false);
        String lastSaveDateStr = sp.getString("date", null); //获得上次点击“不再提醒”的时间字符串
        long diffDays = 0;
        if (!TextUtils.isEmpty(lastSaveDateStr)) {
            try {
                Date lastSaveDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastSaveDateStr); //转换成日期对象
                Date todayDate = new Date(System.currentTimeMillis());
                long diffTime = todayDate.getTime() - lastSaveDate.getTime(); //计算时间间距：毫秒
                diffDays = diffTime / (1000 * 60 * 60 * 24); //计算时间间距(单位：天)

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if (app.getCheckVersionResult() == 1 && (!unRemind || (diffDays >= 2))) { //即使选择“不再提醒”，过两天还是会弹出对话框提示更新
            UpdateManager updateManager = new UpdateManager(context);
            updateManager.checkUpdateInfo(true);
        }

    }


    //加载极光推送
    private void loadJPush() {
        SharedPreferences sharedPreferences = getSharedPreferences("jpush_setalis_state", Context.MODE_PRIVATE);
        boolean aliaHaveSet = sharedPreferences.getBoolean("status", false);
        String alias = sharedPreferences.getString("alias", "");


        if (user != null && (!aliaHaveSet || !TextUtils.equals(user.getPid(), alias))) {
            //极光推送，设置别名，可以指定用户推送
            String pid = user.getPid();
            if (!TextUtils.isEmpty(pid)) {
                if (Tools.isValidTagAndAlias(pid)) {
                    // 调用 Handler 来异步设置别名
                    handler.sendMessage(handler.obtainMessage(MSG_SET_ALIAS, pid));
                }
            }
        }

    }

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    SharedPreferences sharedPreferences = getSharedPreferences("jpush_setalis_state", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("status", true);
                    editor.putString("alias", alias);
                    editor.commit();
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    // 延迟 60 秒来调用 Handler 设置别名
                    handler.sendMessageDelayed(handler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
            }

        }
    };


    private static final int MSG_SET_ALIAS = 1001;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    // 调用 JPush 接口来设置别名。
                    JPushInterface.setAliasAndTags(context, (String) msg.obj, null, mAliasCallback);
                    break;
                default:
                    break;

            }
        }
    };

    /*预先创建好项目所需文件目录*/
    private void makeProjectFileDir() {

        //检查手机上是否有外部存储卡
        boolean sdCardExist = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);

        if (!sdCardExist) {
            Toast.makeText(context, "请插入外部存储设备", Toast.LENGTH_SHORT).show();
        } else {


            /*********************创建扫描记录文件夹*******************/
            File recordFile = new File(Path.RECORD_FILE_PATH);
            if (!recordFile.exists()) {
                recordFile.mkdirs();
            }

            /*********************创建临时图片文件夹*******************/
            File tempImageFile = new File(Path.IMAGE_TEMP_FILE_PATH);
            if (!tempImageFile.exists()) {
                tempImageFile.mkdirs();
            }


        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!exitFlag) {
                Toast.makeText(context, "再点一次退出软件", Toast.LENGTH_SHORT).show();
                exitFlag = true;
                final Timer mTimer = new Timer();
                TimerTask mTimerTask = new TimerTask() {
                    @Override
                    public void run() {
                        exitFlag = false;
                        mTimer.cancel();
                    }
                };
                mTimer.schedule(mTimerTask, 3000, 10000);
            } else {
                if (app == null) {
                    app = (ElectricVehicleApp) this.getApplication();
                }
                AppManager.getAppManager().AppExit(context);
            }
        }

        return false;
    }


    @OnClick({R.id.topButton, R.id.iv_top_mes, R.id.iv_top_manager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.topButton:
//                toggle();
                Utils.startActivity(MainActivity.this, MeActivity.class);
                break;
            case R.id.iv_top_mes:
                startActivity(new Intent(MainActivity.this, MessageActivity.class));
                break;
            case R.id.iv_top_manager://管理员
                Utils.startActivity(MainActivity.this, QueryActivity.class);
                break;
        }
    }

}
