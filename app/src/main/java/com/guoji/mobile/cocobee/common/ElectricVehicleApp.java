package com.guoji.mobile.cocobee.common;

import android.content.Context;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;
import android.widget.Toast;

import com.bql.convenientlog.CLog;
import com.bql.utils.DataKeeperUtils;
import com.bql.utils.ThreadPool;
import com.guoji.mobile.cocobee.BuildConfig;
import com.guoji.mobile.cocobee.model.AlarmInfo;
import com.guoji.mobile.cocobee.model.ConveniencePoint;
import com.guoji.mobile.cocobee.model.Point;
import com.guoji.mobile.cocobee.model.Policy;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.view.GlideImagesLoader;
import com.guoji.mobile.cocobee.view.GlidePauseOnScrollListener;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.store.PersistentCookieStore;
import com.lzy.okgo.model.HttpParams;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import cn.finalteam.galleryfinal.CoreConfig;
import cn.finalteam.galleryfinal.FunctionConfig;
import cn.finalteam.galleryfinal.GalleryFinal;
import cn.finalteam.galleryfinal.ThemeConfig;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by Marktrace003 on 2016/6/27.
 */
public class ElectricVehicleApp extends MultiDexApplication {

    private static ElectricVehicleApp app;
    private static DataKeeperUtils sDataKeeperUtils;
    private static boolean blueFlag;

    private int checkVersionResult = 0;// 检测软件版本结果

    private int updateType; //是否升级1升级 0不升级 2强制升级

    private boolean isDownload = false;// 判断是否正在下载新版本

    private List<Point> points = new ArrayList<>();

    private List<ConveniencePoint> conveniencePoints = new ArrayList<>();

    private List<AlarmInfo> alarmInfos = new ArrayList<>();

    private List<Policy> policyInfos = new ArrayList<>();
    public static IWXAPI sApi;

    @Override
    public void onCreate() {
        super.onCreate();
        setApp(this);
        setFlag(true);
        //多进程导致onCreate执行多次
        String processName = Utils.getProcessName(this, android.os.Process.myPid());
        if (processName != null) {
            boolean defaultProcess = processName.equals("com.guoji.mobile.cocobee");//默认进程
            if (defaultProcess) {
                //当前应用的初始化
                initApp();
            }
        }
        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);

    }

    private void initApp() {
        //另开线程,加快桌面启动速度
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                //Log打印工具初始化
                CLog.init(BuildConfig.LOG_DEBUG);
                //数据储存工具
                sDataKeeperUtils = new DataKeeperUtils(ElectricVehicleApp.this, AppConstants.PREFERS_CONFIG);
                //初始化网络请求框架OkGo
                initOkGo();
                initPhotoPick();
                //初始化百度地图SDK
//                SDKInitializer.initialize(ElectricVehicleApp.this);

                //APP版本迭代
//                UpdateManager.init(ElectricVehicleApp.this).checkVersion();


                //腾讯Bugly异常收集器
                CrashReport.initCrashReport(getApplicationContext(), "900059714", false);

                initWeiXin();
            }
        }).start();

        //线程池初始化
        ThreadPool.startup();

    }

    private void initPhotoPick() {
        //设置主题
        ThemeConfig theme = ThemeConfig.CYAN;
//        ThemeConfig theme = new ThemeConfig.Builder()
//                .build();
        //配置功能
        FunctionConfig functionConfig = new FunctionConfig.Builder()
                .setEnableCamera(true)//开启相机功能
                .setEnableCrop(true)//开启裁剪功能
                .setCropSquare(true)//裁剪正方形
                .setEnablePreview(true)//是否开启预览功能
                .setEnableEdit(true)//开启编辑功能
//                .setEnableRotate(true)//开启旋转功能
//                .setMutiSelectMaxSize(3)//配置多选数量
//        setMutiSelect(true)//配置是否多选
//        setCropWidth(int width)//裁剪宽度
//        setCropHeight(int height)//裁剪高度
//        setSelected(List)//添加已选列表,只是在列表中默认呗选中不会过滤图片
//        setFilter(List list)//添加图片过滤，也就是不在GalleryFinal中显示
//        takePhotoFolter(File file)//配置拍照保存目录，不做配置的话默认是/sdcard/DCIM/GalleryFinal/
//        setRotateReplaceSource(boolean)//配置选择图片时是否替换原始图片，默认不替换
//        setCropReplaceSource(boolean)//配置裁剪图片时是否替换原始图片，默认不替换
//        setForceCrop(boolean)//启动强制裁剪功能,一进入编辑页面就开启图片裁剪，不需要用户手动点击裁剪，此功能只针对单选操作
//        setForceCropEdit(boolean)//在开启强制裁剪功能时是否可以对图片进行编辑（也就是是否显示旋转图标和拍照图标）
                .build();
        CoreConfig coreConfig = new CoreConfig.Builder(this, new GlideImagesLoader(), theme)
                .setFunctionConfig(functionConfig)
                .setPauseOnScrollListener(new GlidePauseOnScrollListener(false, true))
                .build();
        GalleryFinal.init(coreConfig);
    }


    //初始化微信
    private void initWeiXin() {
        sApi = initWeiXin(this, PayConfig.WX_APP_ID);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static ElectricVehicleApp getApp() {
        return app;
    }

    public static void setApp(ElectricVehicleApp app) {
        ElectricVehicleApp.app = app;
    }

    public static void setFlag(boolean flag) {
        ElectricVehicleApp.blueFlag = flag;
    }

    public static boolean getFlag() {
        return blueFlag;
    }

    public int getCheckVersionResult() {
        return checkVersionResult;
    }

    public void setCheckVersionResult(int checkVersionResult) {
        this.checkVersionResult = checkVersionResult;
    }

    public int getUpdateType() {
        return updateType;
    }

    public void setUpdateType(int updateType) {
        this.updateType = updateType;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setIsDownload(boolean isDownload) {
        this.isDownload = isDownload;
    }


    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<AlarmInfo> getAlarmInfos() {
        return alarmInfos;
    }

    public void setAlarmInfos(List<AlarmInfo> alarmInfos) {
        this.alarmInfos = alarmInfos;
    }


    public List<ConveniencePoint> getConveniencePoints() {
        return conveniencePoints;
    }

    public void setConveniencePoints(List<ConveniencePoint> conveniencePoints) {
        this.conveniencePoints = conveniencePoints;
    }


    public List<Policy> getPolicyInfos() {
        return policyInfos;
    }

    public void setPolicyInfos(List<Policy> policyInfos) {
        this.policyInfos = policyInfos;
    }


    public void initOkGo() {
        //必须调用初始化
        OkGo.init(this);
        HttpParams params = new HttpParams();
        params.put("platform_type", "2");
        //以下设置的所有参数是全局参数,同样的参数可以在请求的时候再设置一遍,那么对于该请求来讲,请求中的参数会覆盖全局参数
        //好处是全局参数统一,特定请求可以特别定制参数
        try {
            //以下都不是必须的，根据需要自行选择,一般来说只需要 debug,缓存相关,cookie相关的 就可以了
            OkGo.getInstance()

                    // 打开该调试开关,打印级别INFO,并不是异常,是为了显眼,不需要就不要加入该行
                    // 最后的true表示是否打印okgo的内部异常，一般打开方便调试错误
                    .debug("OkGo", Level.INFO, BuildConfig.LOG_DEBUG)

                    //如果使用默认的 60秒,以下三行也不需要传
                    .setConnectTimeout(60000)  //全局的连接超时时间
                    .setReadTimeOut(60000)     //全局的读取超时时间
                    .setWriteTimeOut(60000)    //全局的写入超时时间

                    //可以全局统一设置缓存模式,默认是不使用缓存,可以不传,具体其他模式看 github 介绍 https://github.com/jeasonlzy/
                    .setCacheMode(CacheMode.NO_CACHE)

                    //可以全局统一设置缓存时间,默认永不过期,具体使用方法看 github 介绍
                    .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)

                    //可以全局统一设置超时重连次数,默认为三次,那么最差的情况会请求4次(一次原始请求,三次重连请求),不需要可以设置为0
                    .setRetryCount(0)
                    .addCommonParams(params)
                    //如果不想让框架管理cookie（或者叫session的保持）,以下不需要
//              .setCookieStore(new MemoryCookieStore())            //cookie使用内存缓存（app退出后，cookie消失）
                    .setCookieStore(new PersistentCookieStore())        //cookie持久化存储，如果cookie不过期，则一直有效

                    //可以设置https的证书,以下几种方案根据需要自己设置
                    .setCertificates();                               //方法一：信任所有证书,不安全有风险
//              .setCertificates(new SafeTrustManager())            //方法二：自定义信任规则，校验服务端证书
//              .setCertificates(getAssets().open("srca.cer"))      //方法三：使用预埋证书，校验服务端证书（自签名证书）
//              //方法四：使用bks证书和密码管理客户端证书（双向认证），使用预埋证书，校验服务端证书（自签名证书）
//               .setCertificates(getAssets().open("xxx.bks"), "123456", getAssets().open("yyy.cer"))//

            //配置https的域名匹配规则，详细看demo的初始化介绍，不需要就不要加入，使用不当会导致https握手失败
//               .setHostnameVerifier(new SafeHostnameVerifier())

            //可以添加全局拦截器，不需要就不要加入，错误写法直接导致任何回调不执行
//                .addInterceptor(new Interceptor() {
//                    @Override
//                    public Response intercept(Chain chain) throws IOException {
//                        return chain.proceed(chain.request());
//                    }
//                })


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static DataKeeperUtils getDataKeeper() {
        return sDataKeeperUtils;
    }


    /**
     * 微信组件注册初始化
     *
     * @param context       上下文
     * @param weixin_app_id appid
     * @return 微信组件api对象
     */
    public static IWXAPI initWeiXin(Context context, @NonNull String weixin_app_id) {
        if (TextUtils.isEmpty(weixin_app_id)) {
            Toast.makeText(context.getApplicationContext(), "app_id 不能为空", Toast.LENGTH_SHORT).show();
        }
        IWXAPI api = WXAPIFactory.createWXAPI(context, weixin_app_id, true);
        api.registerApp(weixin_app_id);
        return api;
    }
}
