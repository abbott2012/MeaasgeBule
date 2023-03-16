package com.guoji.mobile.cocobee.service;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.MainActivity1;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.utils.DownFileThread;
import com.guoji.mobile.cocobee.utils.NotificationDownload;
import com.guoji.mobile.cocobee.utils.UpdateManager;

/**
 * @author _H_JY
 * 2015-10-26下午4:00:05
 * 
 * 功能：在后台下载软件新版本安装包
 */
public class DownloadService extends Service {

	private final static int DOWNLOAD_COMPLETE = -2;
	private final static int DOWNLOAD_FAIL = -1;
	private ElectricVehicleApp app;

	// 自定义通知栏类
	NotificationDownload myNotification;
	String filePathString; // 下载文件绝对路径(包括文件名)
	// 通知栏跳转Intent
	private Intent updateIntent = null;
	private PendingIntent updatePendingIntent = null;
	DownFileThread downFileThread; // 自定义文件下载线程

	private Handler updateHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD_COMPLETE:
				// 点击安装PendingIntent
				// Uri uri = Uri.fromFile(downFileThread.getApkFile());
				if (downFileThread.getApkFile() == null){
					return;
				}
				Uri uri = Uri.parse("file://" + downFileThread.getApkFile().toString());
				Intent installIntent = new Intent(Intent.ACTION_VIEW);

				//打开安装包是正常的，并提示“要覆盖原有的程序”，但是安装后不出现选择“完成，打开”的窗口，但程序已经更新的了.调用此行代码可解决这个问题
				installIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

				installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
				updatePendingIntent = PendingIntent.getActivity(DownloadService.this, 0, installIntent, 0);
				myNotification.changeContentIntent(updatePendingIntent);
				myNotification.notification.defaults = Notification.DEFAULT_SOUND;// 铃声提醒
				myNotification.changeNotificationText("下载完成，请点击安装！");
				app.setIsDownload(false);

				// 需要手动停止服务
				stopSelf();
				break;
			case DOWNLOAD_FAIL:
				// 下载失败
				// myNotification.changeProgressStatus(DOWNLOAD_FAIL);
				myNotification.changeNotificationText("文件下载失败！");
				app.setIsDownload(false);
				stopSelf();
				break;
			default: // 下载中
				Log.i("service", "default" + msg.what);
				// myNotification.changeNotificationText(msg.what+"%");
				myNotification.changeProgressStatus(msg.what);
			}
		}
	};

	public DownloadService() {
		Log.i("service", "DownloadServices1");

	}

	@Override
	public void onCreate() {
		Log.i("service", "onCreate");
		super.onCreate();
		app = (ElectricVehicleApp) getApplication();
	}

	@Override
	public void onDestroy() {
		Log.i("service", "onDestroy");
		if (downFileThread != null)
			downFileThread.interuptThread();
		stopSelf();
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("test", "onStartCommand");
		app.setIsDownload(true);
		updateIntent = new Intent(this, MainActivity1.class);
		PendingIntent updatePendingIntent = PendingIntent.getActivity(this, 0, updateIntent, 0);
		myNotification = new NotificationDownload(this, updatePendingIntent, 1);

		myNotification.showCustomizeNotification(R.drawable.app_logo, "平安城市.apk", R.layout.download_view);

		filePathString = UpdateManager.SAVE_FILE_NAME;

		// 开启一个新的线程下载，如果使用Service同步下载，会导致ANR问题，Service本身也会阻塞
		downFileThread = new DownFileThread(updateHandler, UpdateManager.apkUrl, filePathString);
		new Thread(downFileThread).start();

//		return super.onStartCommand(intent, flags, startId);
		return START_REDELIVER_INTENT;
	}

	@Override
	@Deprecated
	public void onStart(Intent intent, int startId) {
		Log.i("service", "onStart");
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		Log.i("service", "onBind");
		return null;
	}
}
