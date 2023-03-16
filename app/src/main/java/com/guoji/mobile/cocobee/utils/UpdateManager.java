package com.guoji.mobile.cocobee.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.callback.JsonCallback;
import com.guoji.mobile.cocobee.common.AppManager;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.SoftWare;
import com.guoji.mobile.cocobee.service.DownloadService;
import com.lzy.okgo.OkGo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class UpdateManager {

    private Context mContext;

    private ElectricVehicleApp app;

    // 提示消息
    private String updateMsg = "发现新版本的软件包，请您下载！";

    // 下载安装包的网络路径
    public static String apkUrl = Path.APK_DOWNLOAD_URL;

    private Dialog noticeDialog; // 提示有软件更新的对话框

    public static final String SAVE_FILE_NAME = Path.PROJECT_FILE_PATH + "平安城市.apk";

    private PackageInfo packageInfo = null;// 包的信息

    private String localVsName = null;// 本地版本名称

    private int localVsCode;          //本地版本号

    private boolean checkResult = true; // 版本号检测结果，true表示没有新版本

    public final static String VERSION_INFO_URL = Path.APK_INFO_PATH; // 版本信息在服务器上的路径

    private final int FORCED_UPGRADE = 2; //需要“强制升级”的标识

    //public final static String VERSION_INFO_URL = "http://192.168.2.101:8080/gdrc_201512/SoftWare_date.action";

//    private SoftWare software;

    public UpdateManager(Context context) {
        this.mContext = context;
        app = ElectricVehicleApp.getApp();
    }

    public static UpdateManager init(Context context) {
        return new UpdateManager(context);
    }

    // 显示更新程序对话框，供主程序调用
    public void checkUpdateInfo(boolean isAutoRemind) {
        showNoticeDialog(isAutoRemind);
    }

    private void showNoticeDialog(final boolean isAutoRemind) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);// Builder，可以通过此builder设置改变AleartDialog的默认的主题样式及属性相关信息
        builder.setTitle("检查更新").setIcon(R.drawable.ic_update);
        if (app.getUpdateType() == FORCED_UPGRADE && isAutoRemind) {
            builder.setMessage("发现新版本的软件包，为避免影响您正常使用本软件，请立即更新！");
        } else {
            builder.setMessage(updateMsg);
        }
        if (isAutoRemind && app.getUpdateType() != FORCED_UPGRADE) { //登录软件后自动提醒更新，强制升级状态下不提供“不再提醒”功能

            View checkBoxView = LayoutInflater.from(mContext).inflate(R.layout.unremind_checkbox, null);
            CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkbox);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                    if (checked) { //不再提醒
                        SharedPreferences sp = mContext.getSharedPreferences("vc", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putBoolean("unRemind", true);
                        editor.putString("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                        editor.commit();
                        noticeDialog.dismiss();
                    }
                }
            });
            builder.setView(checkBoxView);

        }
        builder.setPositiveButton("立即下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (app.isDownload() == true) {
                    Toast.makeText(mContext, "新版本已经在下载中", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                } else {
                    dialog.dismiss();// 取消对话框
                    AlertDialog.Builder itemBuilder = new AlertDialog.Builder(mContext);
                    itemBuilder.setTitle("请选择下载方式：");
                    itemBuilder.setItems(new String[]{"官方下载"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0:
                                    Intent intent = new Intent(mContext, DownloadService.class);
                                    mContext.startService(intent);// 开启服务，下载新版本
                                    itemBuilder.setTitle("正在下载中,请稍后");
                                    dialogInterface.dismiss();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                    /*******强制升级*******/
                    if (app.getUpdateType() == FORCED_UPGRADE && isAutoRemind) {
                        Dialog itemDialog = itemBuilder.create();
                        itemDialog.setCanceledOnTouchOutside(false);
                        itemDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            @Override
                            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                                    return true;
                                }
                                return false;
                            }
                        });
                        itemDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                AppManager.getAppManager().AppExit(mContext);
                            }
                        });
                        itemDialog.show();
                    } else {
                        /***********/
                        itemBuilder.create().show();
                    }
                }
            }
        });


        if (app.getUpdateType() == FORCED_UPGRADE && isAutoRemind) { //需要强制升级
            builder.setNegativeButton("", null);
            noticeDialog = builder.create();
            noticeDialog.setCanceledOnTouchOutside(false);
            noticeDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                    if (i == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                        return true;
                    }
                    return false;
                }
            });
            noticeDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    AppManager.getAppManager().AppExit(mContext);
                }
            });
        } else {
            builder.setNegativeButton("以后再说",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            noticeDialog = builder.create();
        }

        noticeDialog.show();
    }

    // 检测版本号，判断软件是否需要更新
    public void checkVersion() {
        PackageManager packageManager = mContext.getPackageManager();

        try {// 获取包名和版本信息，0表示版本号
            packageInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            localVsName = packageInfo.versionName;// 获取本地软件版本名称
            localVsCode = packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        /*
         * 从服务器获取版本名称，并与本地版本进行匹配
		 */
        Map<String, String> map = new HashMap<String, String>();
        map.put("platform", "0");
        OkGo.post(VERSION_INFO_URL).tag(this).params(map).execute(new JsonCallback<SoftWare>(mContext) {
            @Override
            public void onSuccess(SoftWare softWare, Call call, Response response) {
                if (softWare != null) {
                    if (TextUtils.isEmpty(softWare.getApp_version())
                            || softWare.getApp_version().equals(localVsName)
                            || localVsCode >= Integer.valueOf(softWare.getApp_version_id())) {
                        checkResult = true;
                    } else {
                        checkResult = false;
                    }

                    if (checkResult == true) {
                        app.setCheckVersionResult(0);// 没有新版本
                        app.setUpdateType(softWare.getType());
                    } else {
                        app.setCheckVersionResult(1);// 有最新版本
                        app.setUpdateType(softWare.getType());
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);

            }

        });
      /*  OkGo.post(VERSION_INFO_URL).tag(this).params(map).execute(new StringCallback() {
            @Override
            public void onSuccess(String result, Call call, Response response) {

                if (!TextUtils.isEmpty(result)) {
                    Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();// 日期需要格式化
                    JsonResult jsonResult = gson.fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.isFlag() && jsonResult.getStatusCode() == 200) {
                        String jsonStr = jsonResult.getResult();
                        if (!TextUtils.isEmpty(jsonStr)) {
                            software = gson.fromJson(jsonStr, new TypeToken<SoftWare>() {
                            }.getType());
                            if (software != null) {
                                if (TextUtils.isEmpty(software.getApp_version())
                                        || software.getApp_version().equals(localVsName)
                                        || localVsCode >= Integer.valueOf(software.getApp_version_id())) {
                                    checkResult = true;
                                } else {
                                    checkResult = false;
                                }

                                if (checkResult == true) {
                                    app.setCheckVersionResult(0);// 没有新版本
                                    app.setUpdateType(software.getType());
                                } else {
                                    app.setCheckVersionResult(1);// 有最新版本
                                    app.setUpdateType(software.getType());
                                }
                            }

                        }
                    }
                }
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);

            }

        });*/


    }


}
