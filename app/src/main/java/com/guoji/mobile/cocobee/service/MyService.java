package com.guoji.mobile.cocobee.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
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

/**
 * 获取gps位置信息的service
 *
 * @author king
 */
public class MyService extends Service {

    private LocationManager locationManager;

    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub  
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        //创建LocationManger对象(LocationMangager，位置管理器。要想操作定位相关设备，必须先定义个LocationManager)  
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //利用Criteria选择最优的位置服务  
        Criteria criteria = new Criteria();
        //设置定位精确度 Criteria.ACCURACY_COARSE比较粗略，Criteria.ACCURACY_FINE则比较精细   
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //设置是否需要海拔信息  
        criteria.setAltitudeRequired(false);
        //设置是否需要方位信息  
        criteria.setBearingRequired(false);
        // 设置是否允许运营商收费    
        criteria.setCostAllowed(true);
        // 设置对电源的需求    
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        //获取最符合要求的provider  
        String provider = locationManager.getBestProvider(criteria, true);

        //绑定监听，有4个参数      
        //参数1，设备：有GPS_PROVIDER和NETWORK_PROVIDER两种  
        //参数2，位置信息更新周期，单位毫秒      
        //参数3，位置变化最小距离：当位置距离变化超过此值时，将更新位置信息      
        //参数4，监听      
        //备注：参数2和3，如果参数3不为0，则以参数3为准；参数3为0，则通过时间来定时更新；两者为0，则随时刷新   
        locationManager.requestLocationUpdates(provider, 30000, 10, locationListener);// 2000,10
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub  
        super.onStart(intent, startId);
        //创建PowerManager对象  
        pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        //保持cpu一直运行，不管屏幕是否黑屏  
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "CPUKeepRunning");
        wakeLock.acquire();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Utils.getUserLoginInfo() == null) {
            stopSelf();
        } else {
            getLoc();
//            setRepeatmethod();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void setRepeatmethod() {
        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int halfSecond = 30000;   // 这是30秒的毫秒数

        Intent i = new Intent(this, AlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        //开始时间
        long firstime = System.currentTimeMillis() + halfSecond;
        manager.set(AlarmManager.RTC_WAKEUP, firstime, pi);
    }


    /**
     * 实现一个位置变化的监听器
     */
    private final LocationListener locationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            // TODO Auto-generated method stub
            if (location != null) {
                Log.e("Map", "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude());
                latitude = location.getLatitude(); // 纬度
                longitude = location.getLongitude(); // 经度
            }
            getLoc();
        }

        // 当位置信息不可获取时  
        @Override
        public void onProviderDisabled(String provider) {
            // TODO Auto-generated method stub  
            XToastUtils.showShortToast("位置不可获取");
        }

        @Override
        public void onProviderEnabled(String provider) {
            // TODO Auto-generated method stub  

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            // TODO Auto-generated method stub  

        }

    };

    private double latitude = 0.0;
    private double longitude = 0.0;


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


    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub  
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
        }
        wakeLock.release();
        super.onDestroy();
    }

} 