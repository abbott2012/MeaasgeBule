package com.guoji.mobile.cocobee.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.guoji.mobile.cocobee.broadcast.AlarmReceiver;
import com.guoji.mobile.cocobee.callback.StringComCallback;
import com.guoji.mobile.cocobee.common.JsonResult;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lzy.okgo.OkGo;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Response;

public class LongRunningService extends Service {

    private void setRepeatmethod() {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int halfSecond = 30000;   // 这是30秒的毫秒数

        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //开始时间
        long firstime = System.currentTimeMillis() + halfSecond;
        manager.set(AlarmManager.RTC_WAKEUP, firstime, pi);
        //        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//api大于等于19
        //            manager.setWindow(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, halfSecond, pi);
        //        } else {
        //            // 使用AlarmManger的setRepeating方法设置定期执行的时间间隔（seconds秒）和需要执行的Service
        //            manager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstime, halfSecond, pi);
        //        }
    }

    PowerManager.WakeLock mWakeLock;// 电源锁

    /**
     * onCreate时,申请设备电源锁
     */
    private void acquireWakeLock() {
        if (null == mWakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, "myService");
            //保持cpu一直运行，不管屏幕是否黑屏
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
            mWakeLock.acquire();

        }
    }

    /**
     * onDestroy时，释放设备电源锁
     */
    private void releaseWakeLock() {
        if (null != mWakeLock) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void onDestroy() {
        releaseWakeLock();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        acquireWakeLock();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //监听
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);

        setRepeatmethod();
        if (Utils.getUserLoginInfo() == null) {
            stopSelf();
        } else {
            new Thread(new Runnable() {

                @Override

                public void run() {
                    getLoc();
                    setRepeatmethod();
                }

            }).start();
        }
//        return super.onStartCommand(intent, flags, startId);
        return START_REDELIVER_INTENT;

    }


    private double latitude = 0.0;
    private double longitude = 0.0;
    private LocationManager locationManager;

    //民警用户30秒上传一次位置信息
    public void getLoc() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //gps已打开
            getLocation();
        } else {
            XToastUtils.showShortToast("请打开gps");
        }
    }

    private void getLocation() {
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        updateLocation(longitude, latitude);
    }

    //上传位置信息到服务器
    private void updateLocation(double lat, double lon) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("pid", Utils.getUserLoginInfo().getPid());//民警用户主键pid
        params.put("plng", "" + lat);//民警当前位置经度
        params.put("plat", "" + lon);//民警当前位置纬度

        OkGo.post(Path.POLICE_NOW_LOCATION).tag(this).params(params).execute(new StringComCallback(){
            @Override
            public void onSuccess(String result, Call call, Response response) {

                if (!TextUtils.isEmpty(result)) {
                    JsonResult jsonResult = new Gson().fromJson(result, new TypeToken<JsonResult>() {
                    }.getType());
                    if (jsonResult != null && jsonResult.getStatusCode() == 200) {//上传成功

                    }
                }
            }
        });
    }

    LocationListener locationListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
            XToastUtils.showShortToast("Gps被打开");
            getLocation();
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            XToastUtils.showShortToast("Gps被关闭");
        }

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.e("Map", "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
                latitude = location.getLatitude(); // 纬度
                longitude = location.getLongitude(); // 经度
            }
        }
    };

}