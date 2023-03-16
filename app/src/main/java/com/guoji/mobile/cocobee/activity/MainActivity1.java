package com.guoji.mobile.cocobee.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bql.tablayout.CommonTabLayout;
import com.bql.tablayout.listener.CustomTabEntity;
import com.bql.tablayout.listener.OnTabSelectListener2;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseActivity;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.QueryFragment;
import com.guoji.mobile.cocobee.utils.DataProviderUtils;
import com.guoji.mobile.cocobee.utils.MainTabEntity;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.yokeyword.fragmentation.SupportFragment;

/**
 * 主界面
 */

public class MainActivity1 extends BaseActivity {
    public static final int REQUEST_LOCATION_CODE = 11;
    public static final int REQUEST_WRITE_CODE = 22;
    @BindView(R.id.main_tab_layout)
    CommonTabLayout mMainTabLayout;
    @BindView(R.id.rl_content)
    RelativeLayout mRlContent;
    private Context context;

    public static final int POSITION_QUERY = 0;//查询Position

    //主页Tab未选中ID数组
    private int[] mMainIconNorIds;

    //主页Tab选中ID数组
    private int[] mMainIconSelIds;

    //Tab标题数组
    private String[] mMainTitles;

    //主页Fragment数组
    private SupportFragment[] mMainFragments;

    //Tab集合
    private ArrayList<CustomTabEntity> mMainTabs = new ArrayList<>();

    //Tab动画
    private Animation mTabAnimation = null;


    private boolean exitFlag = false;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.act_main;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        //切换语言
        Utils.setLanguageConfig(this,  Utils.getUserLuanguage());
        ButterKnife.bind(this);
        context = this;
        makeProjectFileDir();
        //初始化tab
        initMainTabs();
        mMainTabLayout.setVisibility(View.GONE);
        initFragment(savedInstanceState);
        checkPermission();
    }


    /**
     * 申请定位权限
     */
    private void checkPermission() {
        if (requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_LOCATION_CODE, "权限申请：\n我们需要您开启地理位置权限")) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_CODE, "权限申请：\n我们需要您开启设备存储权限");
        }
    }


    /**
     * 返回true表示已经有权限了
     *
     * @param permissions
     * @param requestCode
     * @return
     */
    private boolean requestPermission(String permissions, int requestCode, String info) {
        /*使用6.0的SDK（23）在6.0的手机上，对于危险权限需要在代码中动态申请*/
        if (ContextCompat.checkSelfPermission(MainActivity1.this, permissions) != PackageManager.PERMISSION_GRANTED) {
            //进入到这里代表没有获取权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity1.this, permissions)) {
                //用户拒绝授权
                new AlertDialog.Builder(MainActivity1.this)
                        .setMessage(info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity1.this, new String[]{permissions}, requestCode);
                            }
                        })
                        .show();
            } else {
                ActivityCompat.requestPermissions(MainActivity1.this, new String[]{permissions}, requestCode);
            }
            return false;
        }

        return true;
    }


    /**
     * 使用6.0的SDK（23）在6.0的手机上，对于危险权限需要在代码中动态申请
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意授权
                    requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_WRITE_CODE, "权限申请：\n我们需要您开启设备存储权限");
                } else {
                    //用户拒绝授权
                    sureIfNotNotifiy(Manifest.permission.ACCESS_FINE_LOCATION, "app需要开启地理位置权限,是否去设置?", "用户拒绝地理位置授权");
                }
                break;
            case REQUEST_WRITE_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //用户同意授权
                    requestPermission(Manifest.permission.ACCESS_FINE_LOCATION, REQUEST_LOCATION_CODE, "权限申请：\n我们需要您开启地理位置权限");
                } else {
                    //用户拒绝授权
                    sureIfNotNotifiy(Manifest.permission.WRITE_EXTERNAL_STORAGE, "app需要开启设备存储权限,是否去设置?", "用户拒绝设备存储授权");
                }
                break;
            default:
                break;
        }
    }

    /**
     * 判断用户是否点击过不再提醒
     *
     * @param
     * @param permission
     */
    private void sureIfNotNotifiy(String permission, String msg, String toast) {
        //点击了不在提醒
        if (!ActivityCompat.shouldShowRequestPermissionRationale(MainActivity1.this, permission)) {
            new AlertDialog.Builder(MainActivity1.this)
                    .setMessage(msg)
                    .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
        } else {
            XToastUtils.showShortToast(toast);
        }
    }


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

    /**
     * 初始化Tab
     */
    private void initMainTabs() {
        mTabAnimation = AnimationUtils.loadAnimation(this, R.anim.anim_main_tab);
        mMainIconNorIds = DataProviderUtils.getMainIconNorIds(this);
        mMainIconSelIds = DataProviderUtils.getMainIconSelIds(this);
        mMainTitles = DataProviderUtils.getMainTabTitles();
        for (int i = 0; i < mMainTitles.length; i++) {
            mMainTabs.add(new MainTabEntity(mMainTitles[i], mMainIconNorIds[i], mMainIconSelIds[i]));
        }
        mMainTabLayout.setTabData(mMainTabs);
        mMainTabLayout.setOnTabSelectListener(new OnTabSelectListener2() {
            @Override
            public void onTabSelect(int position, int lastPos) {
                switchPage(position, lastPos);
                switch (position) {
                    //查询
                    case POSITION_QUERY:
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void initFragment(Bundle savedInstanceState) {
        mMainFragments = new SupportFragment[1];
        if (savedInstanceState == null) {
            mMainFragments[0] = QueryFragment.getInstance();
//            mMainFragments[1] = MeFragment.getInstance();
            loadMultipleRootFragment(R.id.fl_content, POSITION_QUERY, mMainFragments);
        } else {
            mMainFragments[0] = findFragment(QueryFragment.class);
//            mMainFragments[1] = findFragment(MeFragment.class);
        }
    }

    /**
     * 切换页面
     *
     * @param curPos
     * @param lastPos
     */
    public void switchPage(int curPos, int lastPos) {
        startTabAnim(curPos, lastPos);
        showHideFragment(mMainFragments[curPos], mMainFragments[lastPos]);
    }

    /**
     * 点击Tab动画效果
     *
     * @param curPos
     * @param prePos
     */
    public void startTabAnim(int curPos, int prePos) {
        mMainTabLayout.getIconView(prePos).clearAnimation();
        mMainTabLayout.getIconView(curPos).startAnimation(mTabAnimation);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!exitFlag) {
                XToastUtils.showShortToast(getResources().getString(R.string.out));
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
}
