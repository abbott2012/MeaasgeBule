package com.guoji.mobile.cocobee.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.view.CNRecogViewfinderView;
import com.guoji.mobile.cocobee.view.SettingWindow;
import com.yunmai.cc.smart.eye.controler.CameraManager;
import com.yunmai.cc.smart.eye.controler.OcrManager;
import com.yunmai.cc.smart.eye.util.DisplayUtil;
import com.yunmai.cc.smart.eye.util.UtilApp;


/**
 * 视频识别类
 *
 * @author fangcm 2012-08-31
 */

public class CameraCarNumAct extends BaseAct implements SurfaceHolder.Callback {

    private final String TAG = "cc_smart";
    private int rateTime = 1000;// 识别频率(间隔时间)
    private int startTime = 1000;// 打开相机1000毫秒后开始对焦
    private int goonTime = 500; // 暂停后继续、识别失败后继续
    private int rightNow = 100; // 结果为空 或者模糊时 立刻进行下次识别
    private SurfaceView sv_preview;
    private SurfaceHolder surfaceHolder;
    private CameraManager cameraManager;
    private EditText et_result;
    private boolean autoFoucs = true;// 截取预览图前是否先对焦
    private boolean preview = true;// 预览状态
    private ImageButton bt_pause, bt_up, bt_down, bt_left,
            bt_right;
    private Button setting_btn, back_btn, sure_btn;
    private CNRecogViewfinderView finderView;
    private String ocrResult = "";
    private OcrManager ocrManager;
    private SettingAdapter settingAdapter;
    private String[] setting_item;
    private Resources resources;
    private SettingWindow settingView;
    private PopupWindow settingWindow;
    private RelativeLayout rl_right_bar;
    private boolean focus_manaul = false;
    private SharedPreferences preferences;
    private float distance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_camera_carnum);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        preferences = getSharedPreferences("CC_EYE", 0);
        focus_manaul = preferences.getBoolean("focus_model", false);
        cameraManager = new CameraManager(getBaseContext(), mHandler);
        resources = getResources();
        setting_item = resources.getStringArray(R.array.camera_setting_item);
        actualWH();
        initViews();
    }


    public class RecognitionThread extends Thread {

        private Handler mHandler;
        private OcrManager ocrManager;
        private byte data[] = null;
        private int dataType = 1;//1视频数据data[] YUV  2拍照数据data[] jpg

        /**
         * @param handler
         * @param data       支持yuv格式
         * @param ocrManager
         */
        public RecognitionThread(Handler handler, byte data[], OcrManager ocrManager) {
            this.mHandler = handler;
            this.data = data;
            this.ocrManager = ocrManager;
            dataType = 1;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String result = ocrManager.getTextByOcr(data, dataType, false);
            if (result == null || result.equals("") || result.equals("*cancel*")) {
                mHandler.sendEmptyMessage(UtilApp.RECOGN_FAIL);
                return;
            }
            Message msg = mHandler.obtainMessage();
            msg.what = UtilApp.RECOGN_OK;
            msg.obj = result;
            mHandler.sendMessage(msg);
        }


    }


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UtilApp.RECOGN_TAKE_PIC_OK:
                    Log.d(TAG, "---start-recogn---------->>");
                    if (!initOcr()) {
                        return;
                    }
                    byte[] data_p = (byte[]) msg.obj;// 相机返回数据 yuv格式
                    if (data_p != null && data_p.length > 0) {
                        new RecognitionThread(mHandler, data_p, ocrManager).start();
                    } else {
                        Toast.makeText(getBaseContext(), "相机出现问题，请重启手机！",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case UtilApp.RECOGN_OK:
                    if (preview) {
                        if (msg.obj == null
                                || String.valueOf(msg.obj).trim().equals("")) {
                            mHandler.sendMessageDelayed(
                                    mHandler.obtainMessage(UtilApp.START_AUTOFOCUS),
                                    rightNow);
                        } else if (msg.obj.equals(UtilApp.ISBLUR)) {// 图像过于模糊 立刻进行对焦
                            autoFoucs = true;
                            mHandler.sendMessageDelayed(
                                    mHandler.obtainMessage(UtilApp.START_AUTOFOCUS),
                                    rightNow);
                        } else {
                            mHandler.sendMessageDelayed(
                                    mHandler.obtainMessage(UtilApp.START_AUTOFOCUS),
                                    rateTime);
                            et_result.setText(String.valueOf(msg.obj).trim());
                            Log.d(TAG, "---end-recogn---------->>"
                                    + et_result.getText().toString().trim());
                            if (!ocrResult.equals(et_result.getText().toString()
                                    .trim())) {
                                ocrResult = et_result.getText().toString().trim();
                            }
                        }
                    }
                    break;
                case UtilApp.RECOGN_FAIL:
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(UtilApp.START_AUTOFOCUS),
                            goonTime);
                    if (msg.obj == null
                            || String.valueOf(msg.obj).trim().equals("")) {
                        autoFoucs = true;
                    }
                    break;
                case UtilApp.START_AUTOFOCUS:
                    if (preview) {
                        if (autoFoucs && !focus_manaul) {
                            cameraManager.autoFocus();
                            autoFoucs = false;
//						mHandler.sendEmptyMessageDelayed(
//								UtilApp.START_AUTOFOCUS, 1200);// 兼容低端机，对焦时间较长
                            mHandler.sendMessageDelayed(mHandler.obtainMessage(UtilApp.START_AUTOFOCUS), 1200);
                        } else {
                            Log.d(TAG, "---start-getPreview---------->>");
                            cameraManager.getPreviewCallback(); // 开始获取图源
                        }
                    }
                    break;
                case UtilApp.ONLY_AUTOFOCUS:
                    cameraManager.autoFocus();
                    break;

                default:
                    mHandler.sendMessageDelayed(
                            mHandler.obtainMessage(UtilApp.START_AUTOFOCUS),
                            rateTime);
                    autoFoucs = true;
                    break;
            }
        }

    };

    private boolean initOcr() {
        if (ocrManager == null) {
            ocrManager = new OcrManager(getBaseContext());
            int ret = ocrManager.initEngineSingle();
            if (ret != 1) {
                ocrManager.closeEngine();
                if (ret == 100) {
                    Toast.makeText(getBaseContext(), "引擎过期,无法识别！！ ",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getBaseContext(), "引擎初始化失败！！ " + ret,
                            Toast.LENGTH_LONG).show();
                }
                return false;
            }
        }
        if (ocrManager.isSetYuvWH()) {
            ocrManager.setYuvWidthAndHeight(cameraManager.getPreviewWidth(),
                    cameraManager.getPreviewHeight());
        }
        if (ocrManager.isSetViewfinderRect()) {
            Rect r = cameraManager.getViewfinder(finderView.getFinder());// 如果取景器的位置
            // 大小改变
            // 该方法需重新调用
            ocrManager.setViewfinderRect(r);
        }
        return true;
    }

    private void initViews() {
        distance = DisplayUtil.dip2px(CameraCarNumAct.this, 60);
        sv_preview = (SurfaceView) findViewById(R.id.camera_sv_preview);
        rl_right_bar = (RelativeLayout) findViewById(R.id.camera_ll_right_bar);
        bt_pause = (ImageButton) findViewById(R.id.camera_bt_pause);
        bt_pause.setOnClickListener(listener);
        finderView = (CNRecogViewfinderView) findViewById(R.id.camera_fv);
        surfaceHolder = (SurfaceHolder) sv_preview.getHolder();
        surfaceHolder.addCallback(CameraCarNumAct.this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        et_result = (EditText) findViewById(R.id.camera_et_text);
        setting_btn = (Button) findViewById(R.id.camera_bt_setting);
        back_btn = (Button) findViewById(R.id.back_btn);
        sure_btn = (Button) findViewById(R.id.sure_btn);
        bt_up = (ImageButton) findViewById(R.id.ib_up);
        bt_down = (ImageButton) findViewById(R.id.ib_down);
        bt_left = (ImageButton) findViewById(R.id.ib_left);
        bt_right = (ImageButton) findViewById(R.id.ib_right);
        setting_btn.setOnClickListener(listener);
        bt_up.setOnClickListener(listener);
        bt_down.setOnClickListener(listener);
        bt_left.setOnClickListener(listener);
        bt_right.setOnClickListener(listener);
        back_btn.setOnClickListener(listener);
        sure_btn.setOnClickListener(listener);
        finderView.setHandler(mHandler);
    }

    private OnClickListener listener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.camera_bt_pause:
                    if (UtilApp.getInstance().isDoubleClick(300)) {
                        return;
                    }
                    if (settingWindow != null && settingWindow.isShowing()) {
                        closePWSET();
                    }
                    if (preview) {// 锁定
                        bt_pause.setImageResource(R.drawable.camera_bt_start);
                        finderView.setVisibility(View.GONE);
                    } else {// 解锁
                        mHandler.sendMessageDelayed(
                                mHandler.obtainMessage(UtilApp.START_AUTOFOCUS),
                                goonTime);
                        bt_pause.setImageResource(R.drawable.camera_bt_pause);
                        finderView.setVisibility(View.VISIBLE);
                    }
                    preview = !preview;
                    break;
                case R.id.camera_bt_setting:
                    if (settingAdapter == null) {
                        settingAdapter = new SettingAdapter();
                    }

                    if (settingView == null) {
                        settingView = new SettingWindow(CameraCarNumAct.this,
                                settingAdapter, getText(
                                R.string.camera_setting_length).toString(),
                                getText(R.string.camera_setting_title).toString());
                    }
                    if (settingWindow == null) {
                        settingWindow = new PopupWindow(settingView.settingInit(),
                                LayoutParams.WRAP_CONTENT,
                                LayoutParams.WRAP_CONTENT);
                        settingWindow.setBackgroundDrawable(resources
                                .getDrawable(R.drawable.camera_setting_options_bg));
                    }
                    if (settingWindow.isShowing()) {
                        closePWSET();
                    } else {
                /*	setting_btn
							.setImageResource(R.drawable.camera_bt_setting_press);*/
                        settingWindow.showAtLocation(setting_btn, Gravity.BOTTOM
                                | Gravity.RIGHT, rl_right_bar.getWidth() + 4, 4);
                    }
                    break;

                case R.id.ib_up:
                    if (settingWindow != null && settingWindow.isShowing()) {
                        closePWSET();
                    }
                    if (finderView.getVisibility() == View.VISIBLE) {
                        finderView.setUp(distance);
                        setViewFinderWAndH("WIDTH", "HEIGHT");
                        finderView.postInvalidate();
                    }
                    break;
                case R.id.ib_down:
                    if (settingWindow != null && settingWindow.isShowing()) {
                        closePWSET();
                    }
                    if (finderView.getVisibility() == View.VISIBLE) {
                        finderView.setDown(distance);
                        setViewFinderWAndH("WIDTH", "HEIGHT");
                        finderView.postInvalidate();
                    }
                    break;
                case R.id.ib_left:
                    if (settingWindow != null && settingWindow.isShowing()) {
                        closePWSET();
                    }
                    if (finderView.getVisibility() == View.VISIBLE) {
                        finderView.setLeft(distance);
                        setViewFinderWAndH("WIDTH", "HEIGHT");
                        finderView.postInvalidate();
                    }
                    break;
                case R.id.ib_right:
                    if (settingWindow != null && settingWindow.isShowing()) {
                        closePWSET();
                    }
                    if (finderView.getVisibility() == View.VISIBLE) {
                        finderView.setRight(distance);
                        setViewFinderWAndH("WIDTH", "HEIGHT");
                        finderView.postInvalidate();
                    }
                    break;


                case R.id.back_btn:
                    finish();
                    break;


                case R.id.sure_btn: //点击确定返回识别结果
                    Intent i = new Intent();
                    i.putExtra("cnum", et_result.getText().toString().trim());
                    setResult(6614, i);
                    finish();
                    break;
            }
        }
    };

    /**
     * 保存ViewFinder的宽高
     *
     * @param WIDTH
     * @param HEIGHT
     */
    public void setViewFinderWAndH(String WIDTH, String HEIGHT) {
        Editor editor = preferences.edit();
        editor.putFloat(WIDTH, finderView.getLineRight());
        editor.putFloat(HEIGHT, finderView.getLineTop());
        editor.commit();
    }

    private void closePWSET() {
        if (settingWindow != null && settingWindow.isShowing()) {
            settingWindow.dismiss();
            //setting_btn.setImageResource(R.drawable.camera_bt_setting);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated");
        try {
            cameraManager.openCamera(holder);
            cameraManager.setCameraFlashModel(Camera.Parameters.FLASH_MODE_OFF);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(CameraCarNumAct.this, "照相机无法启动！请开机重启",
                    Toast.LENGTH_SHORT).show();
            CameraCarNumAct.this.finish();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        Log.d(TAG, "surfaceChanged");
        cameraManager.setPreviewSize();// 设置最佳预览大小
        cameraManager.initDisplay();
        mHandler.sendEmptyMessageDelayed(UtilApp.START_AUTOFOCUS, startTime);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed");
        mHandler.removeMessages(UtilApp.START_AUTOFOCUS);
        cameraManager.closeCamera();
    }

    private void actualWH() {
        Rect rect = new Rect();
        getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        UtilApp.actualHeight = rect.bottom;
        UtilApp.actualWidth = rect.right;
    }

    private class SettingViewHolder {
        private TextView tv_setting;
        private TextView tv_value;
    }

    class SettingAdapter extends BaseAdapter {
        private LinearLayout ll_setting;
        private SettingViewHolder holder;

        @Override
        public int getCount() {
            return setting_item.length;
        }

        @Override
        public Object getItem(int position) {
            return setting_item[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                holder = new SettingViewHolder();
                ll_setting = new LinearLayout(getBaseContext());
                // LinearLayout.LayoutParams params = new
                // LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                // LayoutParams.WRAP_CONTENT);
                ll_setting.setOrientation(LinearLayout.HORIZONTAL);
                holder.tv_setting = new TextView(getBaseContext());
                holder.tv_setting.setTextColor(getResources().getColor(
                        R.color.snow));
                holder.tv_setting.setPadding(10, 8, 8, 8);
                holder.tv_setting.setTextSize(resources
                        .getDimension(R.dimen.public_8_sp));

                LinearLayout.LayoutParams layoutParams = new LayoutParams(
                        LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                holder.tv_value = new TextView(getBaseContext());
                holder.tv_value.setTextColor(getResources().getColor(
                        R.color.use));
                holder.tv_value.setPadding(12, 8, 8, 8);
                holder.tv_value.setTextSize(resources
                        .getDimension(R.dimen.public_8_sp));
                holder.tv_value.setLayoutParams(layoutParams);
                holder.tv_value.setGravity(Gravity.RIGHT);

                ll_setting.addView(holder.tv_setting);
                ll_setting.addView(holder.tv_value);

                convertView = ll_setting;
                convertView.setTag(holder);
            } else {
                holder = (SettingViewHolder) convertView.getTag();
            }
            holder.tv_setting.setText(setting_item[position]);
            if (focus_manaul) {
                holder.tv_value.setText(getSpannable(
                        getText(R.string.camera_setting_focus_manaul)
                                .toString(), true));
            } else {
                holder.tv_value.setText(getSpannable(
                        getText(R.string.camera_setting_focus_manaul)
                                .toString(), false));
            }
            convertView
                    .setBackgroundResource(R.drawable.camera_window_item_selector);
            convertView.setId(position);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (preferences.edit().putBoolean("focus_model", !focus_manaul).commit()) {
                        focus_manaul = !focus_manaul;
                        if (focus_manaul) {
                            holder.tv_value.setText(getSpannable(getText(R.string.camera_setting_focus_manaul).toString(), true));
                        } else {
                            holder.tv_value.setText(getSpannable(getText(R.string.camera_setting_focus_manaul).toString(), false));
                        }
                    }
                    settingAdapter.notifyDataSetChanged();
                }
            });
            return convertView;
        }

        public SpannableString getSpannable(String text, boolean b) {
            if (b) {
                return getSpannable1(text,
                        resources.getColor(R.color.qgreen), 0,
                        text.indexOf("/"));
            } else {
                return getSpannable1(text,
                        resources.getColor(R.color.qgreen),
                        text.indexOf("/") + 1, text.length());
            }
        }

    }

    public SpannableString getSpannable1(String text, int color, int start, int end) {
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(color), start, end, 33);
        return spannable;
    }

    @Override
    protected void onDestroy() {
        if (cameraManager != null) {
            cameraManager.closeCamera();
        }
        if (ocrManager != null) {
            ocrManager.closeEngine();
        }

        super.onDestroy();
    }

}
