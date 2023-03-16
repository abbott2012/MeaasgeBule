package com.guoji.mobile.cocobee.callback;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.bql.convenientlog.CLog;
import com.bql.utils.CheckUtils;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.ElectricVehicleApp;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.utils.EncryptionUtils;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.HttpParams;
import com.lzy.okgo.request.BaseRequest;

import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * ClassName: JsonCallback <br>
 * Description: 默认将返回的数据解析成需要的Bean,可以是 Bean，String，List，Map<br>
 * Author: Cyarie <br>
 * Created: 2016/7/7 16:43 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public abstract class JsonCallback<T> extends AbsCallback<T> {


    private static final int WHAT_FLAG_FALSE = 1;
    protected boolean isShowNetToast = true;//是否显示服务异常Toast通知 默认true

    private Context mContext;

    public JsonCallback(Context context, boolean isShowNetToast) {
        this(context);
        this.isShowNetToast = isShowNetToast;
    }

    public JsonCallback(Context context) {
        this.mContext = context;
    }

    @Override
    public void onBefore(BaseRequest request) {
        super.onBefore(request);
        User userLoginInfo = Utils.getUserLoginInfo();
        if (userLoginInfo != null) {
            HttpParams params = new HttpParams();
            String encoding = EncryptionUtils.encoding(userLoginInfo.getPid(), System.currentTimeMillis() + "");
            params.put("identity", encoding);
            request.params(params);
        }
        CLog.json("LogInterceptor", request.getParams().toString());
    }

    @Override
    public T convertSuccess(Response response) throws Exception {
        //以下代码是通过泛型解析实际参数,泛型必须传
        Type genType = getClass().getGenericSuperclass();
        if (!(genType instanceof ParameterizedType))
            throw new IllegalStateException("Callback没有填写泛型参数");
        //从上述的类中取出真实的泛型参数，有些类可能有多个泛型，所以是数组
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        Type type = params[0];

        //Callback中的泛型类型 可能是Class 也有可能是带有泛型类型的泛型 例如 java.util.List<E>  分别做不同处理

        Type rawType;
        //Callback中的泛型为泛型类型
        if (type instanceof ParameterizedType) {
            //rawType的类型实际上是 Class，但 Class 实现了Type接口，所以我们用Type接收
            rawType = ((ParameterizedType) type).getRawType();
        } else { //Callback中的泛型为Class
            rawType = type;
        }


        //这里我们既然都已经拿到了泛型的真实类型，即对应的 class ，那么当然可以开始解析数据了，我们采用 Gson 解析
        //以下代码是根据泛型解析数据，返回对象，返回的对象自动以参数的形式传递到 onSuccess 中，可以直接使用
        String body = response.body().string();
        response.close();
        CLog.e("LogInterceptor", body);
        CLog.json("LogInterceptor", body);
        if (CheckUtils.isEmpty(body))
            return null;
        /**
         * 服务器返回的响应都包含 flag,message,statusCode，有些还包含result部分
         */
        JSONObject jsonObject = new JSONObject(body);
        boolean success = jsonObject.optBoolean("flag", true);
        final String msg = jsonObject.optString("message", "");
        int msgCode = jsonObject.optInt("statusCode", 200);
        String result = jsonObject.optString("result", null);

        switch (msgCode) {
            case 200:
                /**
                 * statusCode = 200 代表成功，默认实现Gson解析成相应的实体Bean返回，可以自己替换成fastjson等
                 * 对于返回参数，先支持 String，然后优先支持class类型的字节码，最后支持type类型的参数
                 */
                if (success) {
                    if (rawType == String.class) {
                        return (T) result;
                    }
                    return GsonConvert.fromJson(result, type);
                } else {
                    Message message = handler.obtainMessage();
                    message.obj = msg;
                    message.what = WHAT_FLAG_FALSE;
                    handler.sendMessage(message);
                }

                CLog.e("LogInterceptor", "错误代码：" + msgCode + "，错误信息：" + msg);
                throw new IllegalStateException(msgCode + "");
            default:

                Message message = handler.obtainMessage();
                message.obj = msg;
                message.what = WHAT_FLAG_FALSE;
                handler.sendMessage(message);

                CLog.e("LogInterceptor", "错误代码：" + msgCode + "，错误信息：" + msg);
//                throw new IllegalStateException(msgCode + "");
                throw new IllegalStateException(msg);
        }

    }


    @Override
    public void onError(Call call, Response response, Exception e) {
        super.onError(call, response, e);
        //未连接网络 或者 服务器异常情况
        if (e != null && !CheckUtils.isEmpty(e.toString()))
            CLog.e("LogInterceptor", e.toString());

        if (response == null && isShowNetToast) {
            XToastUtils.showShortToast(Utils.isNetConnected() ? ElectricVehicleApp.getApp().getString(R.string.server_error) : ElectricVehicleApp.getApp().getString(R.string.net_error));
        } else if (response != null && response.code() >= 400 && response.code() <= 599 && isShowNetToast) {
            XToastUtils.showShortToast(ElectricVehicleApp.getApp().getString(R.string.server_error));
            return;
        } else if (isShowNetToast) {
            XToastUtils.showShortToast(e.getMessage());
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WHAT_FLAG_FALSE:
                    if (isShowNetToast) {
                        String message = (String) msg.obj;
                        XToastUtils.showShortToast(message);
                    }
                    break;
            }
        }
    };
}
