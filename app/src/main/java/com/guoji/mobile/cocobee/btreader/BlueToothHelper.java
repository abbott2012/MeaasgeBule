package com.guoji.mobile.cocobee.btreader;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.bql.convenientlog.CLog;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.model.Tag;
import com.guoji.mobile.cocobee.model.Transponder;
import com.guoji.mobile.cocobee.model.TriadInfo;
import com.guoji.mobile.cocobee.utils.Tools;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author _H_JY   2016-5-26下午2:53:04
 * This class does all the work for setting up and managing Blue tooth
 * connections with other devices. It has a thread for connecting with a device,
 * and a thread for performing data transmissions when connected.
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BlueToothHelper {

    // Debugging
    private static final String TAG = "BlueToothHelper";

    // Member fields
    private static BlueToothHelper mReader;
    private BluetoothAdapter mAdapter = null;
    private static Handler mHandler;
    private ConnectThread mConnectThread;
    //private ConnectedThread mConnectedThread;
    private int mState;

    private int bleCmdType = Constant.BLE_DO_NONE;
    private boolean isFirstPackage = false;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0; // we're doing nothing
    public static final int STATE_CONNECTING = 1; // now initiating an outgoing

    // connection
    public static final int STATE_CONNECTED = 2; // now connected to a remote
    // device

    private boolean read_flag = false;// reading flag
    // private boolean start_read_flag = false;//reading flag
    private BluetoothSocket mSocket;

    /*BLE4.0*/
    private BluetoothGatt mBluetoothGatt;
    private BluetoothGattCharacteristic controlCharacteristic;
    private int bluetoothType = BluetoothDevice.DEVICE_TYPE_UNKNOWN; //
    private int valueLength;
    private List<Byte> valueList = new ArrayList<>();

    private Context mContext;
    private ScanListener mScanListener;
    private boolean scanFlag = true;
    private boolean debugTXFlag = false;
    private boolean isNextTagDataCloudEnter = false;


    public static final byte[] SCAN_DEVICE_COMMAND = new byte[]{(byte) 0x0A, 0x00, 0x02, 0x14, (byte) 0xE0}; //读取设备信息指令
    public static final byte[] SCAN_DEVICE_COMMANDS = new byte[]{(byte) 0x0A, (byte) 0xFF, 0x02, 0x14, (byte) 0xE1}; //读取设备信息指令4.0

    public static final byte[] READ_IMEI_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x15, (byte) 0xE0}; //读设备IMEI指令
    public static final byte[] READ_FACNUM_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x24, (byte) 0xD1}; //读设备出厂编号指令

    public static final byte[] READ_TRIAD_PARAM = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x69, (byte) 0x8C}; //读取三元组信息指令

    public static final byte[] DEBUG_TX_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x18, (byte) 0xDD}; //调试天线指令

    public static final byte[] STOP_DEBUG_TX_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x03, 0x19, 0x00, (byte) 0xDB}; //停止调试天线指令

    public static final byte[] READ_SYS_TIME_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x17, (byte) 0xDE}; //读设备系统时间指令
    public static final byte[] READ_TX_INFO_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x28, (byte) 0xCD}; //读天线信息指令
    public static final byte[] READ_GPRS_NUM_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x0C, (byte) 0xE9}; //获取 GPRS 信号强度

    public static final byte[] OPEN_TAG_OUTPUT_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x04, 0x2A, 0x01, 0x00, (byte) 0xC8};

    public static final byte[] CLOSE_TAG_OUTPUT_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x04, 0x2A, 0x00, 0x01, (byte) 0xC8};

    public static final byte[] FILTER_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x04, 0x2A, 0x01, 0x01, (byte) 0xC7};

    public static final byte[] READ_GPRS_CONNECT_STATE_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x29, (byte) 0xCC};
    public static final byte[] READ_LAN_CONNECT_STATE_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x2B, (byte) 0xCA};
    public static final byte[] READ_CACHE_TAG_NUM_COMMAND = new byte[]{0x0A, (byte) 0xFF, 0x02, 0x25, (byte) 0xD0};
    public static final byte[] CARRIER_TESTING = new byte[]{0x0A, (byte) 0xFF, 0x03, (byte) 0x9F, 0x50, 0x05};//载波测试指令
    //读卡
    public static final byte[] OPEN_RF = new byte[]{0x0A, (byte) 0xFF, 0x02, (byte) 0x90, (byte) 0x65};//打开RF
    public static final byte[] CLOSE_RF = new byte[]{0x0A, (byte) 0xFF, 0x02, (byte) 0x91, (byte) 0x64};//关闭RF
    public static final byte[] CLOSE_DEVICE = new byte[]{0x0A, (byte) 0xFF, 0x02, (byte) 0x20, (byte) 0xd5};//关机命令
    public static final byte[] GET_COMMENT_DATA = new byte[]{0x0A, (byte) 0xFF, 0x02, (byte) 0x9A, (byte) 0x5B};//取出标签数据
    public static final byte[] SET_MODE_ONE = new byte[]{0x0A, (byte) 0xFF, 0x03, (byte) 0x23, 0x01, (byte) 0xD0};//设置标签查车模式
    public static final byte[] SET_MODE_TWO = new byte[]{0x0A, (byte) 0xFF, 0x03, (byte) 0x23, 0x02, (byte) 0xCF};//设置标签发卡模式

    //获取6个字节标签数据
    public static final byte[] GET_SIX_TAG_LABEL = new byte[]{0x0A, (byte) 0xFF, 0x02, (byte) 0x72, (byte) 0x83};

    // 开始搜索6个字节的标签
    public static final byte[] START_SET_SIX_BYTE_OF_SEARCH = new byte[]{0x0A, (byte) 0xFF, 0x04, (byte) 0x74, (byte) 0x01, (byte) 0x00, (byte) 0x7E};

    //停止搜索6个字节的标签
    public static final byte[] STOP_SET_SIX_BYTE_OF_SEARCH = new byte[]{0x0A, (byte) 0xFF, 0x04, (byte) 0x74, (byte) 0x00, (byte) 0x01, (byte) 0x7E};

    private byte[] data = new byte[187];
    public static final int MSG_SET_PARAMS_OK = 12232;
    public static final int MSG_SET_PARAMS_FAIL = 4404;
    public static final int MSG_READ_TAG_OK = 7225;
    public static final int MSG_READ_TAG_FAIL = 5227;
    public static final int MSG_READ_IMEI_OK = 2445;
    public static final int MSG_READ_IMEI_FAIL = 2446;
    public static final int MSG_READ_FACNUM_OK = 3445;
    public static final int MSG_READ_FACUNM_FAIL = 3446;
    public static final int MSG_DEBUG_TX_OK = 4100;
    public static final int MSG_DEBUG_TX_FAIL = 4101;
    public static final int MSG_READ_SYSTIME_OK = 5000;
    public static final int MSG_READ_SYSTIME_FAIL = 5001;
    public static final int MSG_SET_SYSTIME_OK = 6000;
    public static final int MSG_SET_SYSTIME_FAIL = 6001;
    public static final int MSG_READ_TX_INFO_OK = 6002;
    public static final int MSG_READ_TX_INFO_FAIL = 6003;
    public static final int MSG_READ_GPRS_OK = 6004;
    public static final int MSG_READ_GPRS_FAIL = 6005;
    public static final int MSG_SHOW_SEARCH_RESULT = 6006;    //刷新搜索结果
    public static final int MSG_READ_GPRS_CONNECT_STATE_OK = 6007;
    public static final int MSG_READ_GPRS_CONNECT_STATE_FAIL = 6008;
    public static final int MSG_READ_LAN_CONNECT_STATE_OK = 6009;
    public static final int MSG_READ_LAN_CONNECT_STATE_FAIL = 6010;
    public static final int MSG_DEVICE_UNSUPPORT_LAN = 6011;
    public static final int MSG_READ_CACHE_TAG_NUM_OK = 6012;
    public static final int MSG_READ_CACHE_TAG_NUM_FAIL = 6013;
    public static final int MSG_CLEAR_TASK = 6014;  //清除搜索tag

    public static final int MSG_NO_BLUETOOTH_SOCKET = 6100;
    public static final int MSG_CARRIER_TEST_OK = 6110;
    public static final int MSG_CARRIER_TEST_FIAL = 6120;

    public static final int MSG_SET_MODE_OK = 6130;//设置一档模式成功
    public static final int MSG_SET_MODE_FAIL = 6140;//设置一档模式失败
    public static final int MSG_OPEN_RF_OK = 6150;//设置打开成功
    public static final int MSG_OPEN_RF_FAIL = 6160;//设置打开失败
    public static final int MSG_CLOSE_RF_OK = 6170;//设置关闭成功
    public static final int MSG_CLOSE_RF_FAIL = 6180;//设置关闭失败
    public static final int MSG_GET_DATA_OK = 6190;//设置关闭成功
    public static final int MSG_GET_DATA_FAIL = 6200;//设置关闭失败
    public static final int MSG_GET_DATA_NONE = 6210;//未读取到数据

    public static final int READ_TRIAD_PARAM_OK = 6211;//读取三元组成功
    public static final int READ_TRIAD_PARAM_FAIL = 6212;//读取三元组失败

    public static final int SET_TRIAD_PARAM_OK = 6213;//设置三元组成功
    public static final int SET_TRIAD_PARAM_FAIL = 6214;//设置三元组失败

    private boolean mHasStart;//巡逻是否有包头
    private boolean mHasEnd;//巡逻是否有包尾
    private String mPakageStartString;
    private String mTotalPakageString;
    private boolean flag_end_0d;

    /**
     * Constructor. Prepares a new BlueToothHelper session.
     *
     * @param context The UI Activity Context
     * @param handler A Handler to send messages back to the UI Activity
     */
    public BlueToothHelper(Context context, Handler handler) {
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
        mHandler = handler;
        mContext = context;
    }


    public static BlueToothHelper getInstance(Context context, Handler handler) {
        if (mReader == null) {
            synchronized (BlueToothHelper.class) {
                if (mReader == null) {
                    mReader = new BlueToothHelper(context, handler);
                }
            }
        }
        mHandler = handler;

        return mReader;
    }


    /*********************** The Open and Close of Blue Tooth Operation *********************/

    /**
     * Open the blue tooth helper. Called by the Activity onResume()
     **/
    public synchronized void open() {
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }


    /**
     * Open the blue tooth helper. Stop all threads if the thread is running.
     * 关闭传统蓝牙
     */
    public synchronized void close() {
        setScanFlag(false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mConnectThread != null) {
                    mConnectThread.cancel();
                    mConnectThread = null;
                }

                try {
                    if (mSocket != null) {
                        mSocket.close();
                        mSocket = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        setState(STATE_NONE);


    }


    public boolean isDebugTXFlag() {
        return debugTXFlag;
    }

    public void setDebugTXFlag(boolean debugTXFlag) {
        this.debugTXFlag = debugTXFlag;
    }

    public BluetoothSocket getSocket() {
        return mSocket;
    }

    public void setSocket(BluetoothSocket mSocket) {
        this.mSocket = mSocket;
    }


    /*************************** The Operation of Blue Tooth Thread ****************************/


    /**
     * Set the current state of the chat connection
     *
     * @param state An integer defining the current connection state
     */
    public synchronized void setState(int state) {

        mState = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(Constant.MESSAGE_STATE_CHANGE, state, -1)
                .sendToTarget();

    }

    /**
     * Return the current connection state.
     */
    public synchronized int getState() {
        return mState;
    }


    public void setBluetoothPairingPin(BluetoothDevice device) {
        String TAG = "BT";
        byte[] pinBytes = ByteBuffer.allocate(4).putInt(1234).array();
        try {
            Log.d(TAG, "Try to set the PIN");
            Method m = device.getClass().getMethod("setPin", byte[].class);
            m.invoke(device, pinBytes);
            Log.d(TAG, "Success to add the PIN.");
            try {
                device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                Log.d(TAG, "Success to setPairingConfirmation.");
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     *
     * @param device The BluetoothDevice to connect
     */
    public synchronized void connect(final BluetoothDevice device, Context context) {
        // if (D) Log.d(TAG, "connect to: " + device);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED) { //如果未配对，注册广播进行配对
            /*增加自动配对功能*/
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_PAIRING_REQUEST);
            context.registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    if (intent.getAction().equals(BluetoothDevice.ACTION_PAIRING_REQUEST)) {

                        try {
                            byte[] pin = (byte[]) BluetoothDevice.class.getMethod("convertPinToBytes", String.class).invoke(BluetoothDevice.class, "1234");
                            Method m = device.getClass().getMethod("setPin", byte[].class);
                            m.invoke(device, pin);
                            device.getClass().getMethod("setPairingConfirmation", boolean.class).invoke(device, true);
                            System.out.println("PAIRED !");

                            //context.unregisterReceiver(this);
                            /*配对成功，中断广播的继续传递*/
                            abortBroadcast();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, filter);
        }


        // Cancel any thread attempting to make a connection
        if (mState == STATE_CONNECTING) {
            if (mConnectThread != null) {
                mConnectThread.cancel();
                mConnectThread = null;
            }
        }


        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device, context);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     *
     * @param socket The BluetoothSocket on which the connection was made
     * @param device The BluetoothDevice that has been connected
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, Context context) {
        // ILog.d(TAG, "connected");

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        mSocket = socket;

        bluetoothType = BluetoothDevice.DEVICE_TYPE_CLASSIC;
        setState(STATE_CONNECTED);


    }

    /**
     * This thread runs while attempting to make an outgoing connection with a
     * device. It runs straight through; the connection either succeeds or
     * fails.
     */
    private class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private Context context;

        private ConnectThread(BluetoothDevice device, Context context) {
            mmDevice = device;
            this.context = context;
            BluetoothSocket tmp = null;
            // Get a BluetoothSocket for a connection with the given
            // BluetoothDevice
            try {
               /* if (Build.VERSION.SDK_INT >= 10) {
                    tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                } else {
                    tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                }*/
                if (Build.VERSION.SDK_INT >= 10) {
                    Method m = device.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class});
                    tmp = (BluetoothSocket) m.invoke(device, 1);
                } else {
                    Method m = device.getClass().getMethod("createRfcommSocket", new Class[]{int.class});
                    tmp = (BluetoothSocket) m.invoke(device, 1);
                }

            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            mmSocket = tmp;
        }

        public void run() {

            //setName("ConnectThread");
            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();
            // Make a connection to the BluetoothSocket

            final int maxTries = 3; //最大尝试连接次数为3次,只要连上立刻终止循环
            for (int i = 0; i < maxTries; i++) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    mmSocket.connect();
                    break;
                    //Thread.sleep(500);
                } catch (IOException e) {
                    e.printStackTrace();
                    cancel();

                }
            }

            /*新增加*/
            if (!mmSocket.isConnected()) {
                mConnectThread = null;
                connectionFailed();
                return;
            } else {
                // Reset the ConnectThread because we're done
                synchronized (BlueToothHelper.this) {
                    mConnectThread = null;
                }

                // Start the connected thread
                connected(mmSocket, mmDevice, context);
                Log.d(TAG, "END Connect Thread");
            }


        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {

            }
        }
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     * 设备连接失败
     */
    private void connectionFailed() {
        setState(STATE_NONE);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constant.STATE_CONNECTE_ERROR);
        mHandler.sendMessage(msg);
    }

    /**
     * Indicate that the connection was lost and notify the UI Activity.
     * 设备连接丢失
     */
    private void connectionLost() {
        setState(STATE_NONE);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(Constant.STATE_CONNECTE_LOST);
        mHandler.sendMessage(msg);
    }


    public void cancel() {
        read_flag = false;

        connectionLost();
    }

    /**
     * 读取三元组信息
     */
    public void onReadTriad() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                synchronized (mSocket) {
                    InputStream is = null;
                    try {
                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();
                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);
                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_IMEI_COMMAND);
                        os.flush();
                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());
                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            byte[] addrLenStatus = new byte[93];
                            for (int i = 0; i < 93; i++) {
                                addrLenStatus[i] = dis.readByte();
                            }
                            if ((addrLenStatus[2] & 0xff) == 0x00) { //状态位，表示成功
                                byte[] mProductKeyByte = new byte[16];
                                byte[] mDeviceNameByte = new byte[32];
                                byte[] mDeviceSecretByte = new byte[40];
                                for (int i = 0; i < 16; i++) {
                                    mProductKeyByte[i] = addrLenStatus[i + 4];
                                }
                                for (int i = 0; i < 32; i++) {
                                    mDeviceNameByte[i] = addrLenStatus[i + 20];
                                }
                                for (int i = 0; i < 40; i++) {
                                    mDeviceSecretByte[i] = addrLenStatus[i + 52];
                                }
                                TriadInfo triadInfo = new TriadInfo();
                                String mProductKey = new String(mProductKeyByte);
                                String mDeviceName = new String(mDeviceNameByte);
                                String mDeviceSecret = new String(mDeviceSecretByte);
                                triadInfo.setProductKey(mProductKey);
                                triadInfo.setDeviceName(mDeviceName);
                                triadInfo.setDeviceSecret(mDeviceSecret);
                                Message message = Message.obtain();
                                message.obj = triadInfo;
                                message.what = READ_TRIAD_PARAM_OK;
                                mHandler.sendMessage(message);
                                return;
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mHandler.sendEmptyMessage(READ_TRIAD_PARAM_FAIL);
                }
            }
        }).start();

    }


    /**
     * 读设备Imei
     */
    public void readImei() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    InputStream is = null;

                    try {
                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_IMEI_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                       /* if(dis.available() <= 0){
                            mHandler.sendEmptyMessage(MSG_READ_IMEI_FAIL);
                            return;
                        }*/

                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            byte[] addrLenStatus = new byte[3];
                            for (int i = 0; i < 3; i++) {
                                addrLenStatus[i] = dis.readByte();
                            }
                            if ((addrLenStatus[2] & 0xff) == 0x00) { //状态位，表示成功
                                byte[] imei = new byte[16];
                                for (int i = 0; i < 16; i++) {
                                    imei[i] = dis.readByte();
                                }
                                String imei_id_str = new String(imei);
                                imei_id_str = imei_id_str.substring(0, imei_id_str.length() - 1);
                                if (!TextUtils.isEmpty(imei_id_str)) {
                                    Message message = Message.obtain();
                                    message.obj = imei_id_str;
                                    message.what = MSG_READ_IMEI_OK;
                                    mHandler.sendMessage(message);
                                    return;
                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(MSG_READ_IMEI_FAIL);

                }
            }
        }).start();
    }


    /**
     * 读设备缓存标签总数
     */
    public void readCacheTagNum() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    InputStream is = null;

                    try {
                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_CACHE_TAG_NUM_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                       /* if(dis.available() <= 0){
                            mHandler.sendEmptyMessage(MSG_READ_IMEI_FAIL);
                            return;
                        }*/

                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            int cmd = dis.readByte() & 0xff;
                            if (cmd == 0x25) {
                                int len = dis.readByte() & 0xff;
                                if (len == 0x04) {
                                    int status = dis.readByte() & 0xff;
                                    if (status == 0x00) { //成功
                                        byte[] data = new byte[2];
                                        dis.read(data);
                                        String cacheTagNumStr = Tools.bytesToHexString2(data);
                                        int cacheTagNum = Integer.parseInt(cacheTagNumStr, 16);
                                        Message message = Message.obtain();
                                        message.obj = cacheTagNum;
                                        message.arg1 = 200;
                                        message.what = MSG_READ_CACHE_TAG_NUM_OK;
                                        mHandler.sendMessage(message);
                                        return;
                                    }

                                } else if (len == 0x08) {
                                    int status = dis.readByte() & 0xff;
                                    if (status == 0x00) {
                                        String[] cacheTagNumStr = new String[3];
                                        int[] cacheTagNum = new int[3];
                                        byte[] data = new byte[2];
                                        int dataIndex = 0;
                                        for (int i = 0, j = 0; i < 6; i++) {
                                            data[dataIndex++] = dis.readByte();
                                            if (dataIndex == 2) {
                                                dataIndex = 0;
                                                cacheTagNumStr[j] = Tools.bytesToHexString2(data);
                                                cacheTagNum[j] = Integer.parseInt(cacheTagNumStr[j], 16);
                                                j++;
                                            }
                                        }

                                        // int cacheTagNum[3] = Integer.parseInt(cacheTagNumStr, 16);
                                        Message message = Message.obtain();
                                        message.obj = cacheTagNum;
                                        message.arg1 = 500;
                                        message.what = MSG_READ_CACHE_TAG_NUM_OK;
                                        mHandler.sendMessage(message);
                                        return;

                                    }
                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(MSG_READ_CACHE_TAG_NUM_FAIL);

                }
            }
        }).start();
    }


    /**
     * 读设备系统时间
     */
    public void readSysTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    InputStream is = null;

                    try {
                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_SYS_TIME_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                       /* if(dis.available() <= 0){
                            mHandler.sendEmptyMessage(MSG_READ_IMEI_FAIL);
                            return;
                        }*/

                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            byte[] addrLenStatus = new byte[3];
                            for (int i = 0; i < 3; i++) {
                                addrLenStatus[i] = dis.readByte();
                            }
                            if ((addrLenStatus[2] & 0xff) == 0x00) { //状态位，表示成功

                                byte[] sysTimeB = new byte[6];
                                for (int i = 0; i < 6; i++) {
                                    sysTimeB[i] = dis.readByte();
                                }

                                int year = 2000 + sysTimeB[0];
                                int month = sysTimeB[1];
                                int day = sysTimeB[2];
                                int hour = sysTimeB[3];
                                int minute = sysTimeB[4];
                                int second = sysTimeB[5];

                                String preMonth = "";
                                String preDay = "";
                                String preHour = "";
                                String preMinute = "";
                                String preSecond = "";
                                if (String.valueOf(month).length() < 2) {
                                    preMonth = "0";
                                }
                                if (String.valueOf(day).length() < 2) {
                                    preDay = "0";
                                }
                                if (String.valueOf(hour).length() < 2) {
                                    preHour = "0";
                                }
                                if (String.valueOf(minute).length() < 2) {
                                    preMinute = "0";
                                }
                                if (String.valueOf(second).length() < 2) {
                                    preSecond = "0";
                                }

                                String time = year + "." + preMonth + month + "." + preDay + day + "," + preHour + hour + ":" + preMinute + minute + ":" + preSecond + second;

                                Message message = Message.obtain();
                                message.obj = time;
                                message.what = MSG_READ_SYSTIME_OK;
                                mHandler.sendMessage(message);
                                return;

                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(MSG_READ_SYSTIME_FAIL);

                }
            }
        }).start();
    }


    /**
     * 设置设备系统时间
     */
    public void setSysTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }


                synchronized (mSocket) {
                    InputStream is = null;

                    try {
                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(getSysTimeBytes());
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());


                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            byte[] addrLenStatus = new byte[3];
                            for (int i = 0; i < 3; i++) {
                                addrLenStatus[i] = dis.readByte();
                            }
                            if ((addrLenStatus[2] & 0xff) == 0x00) { //状态位，表示成功

                                mHandler.sendEmptyMessage(MSG_SET_SYSTIME_OK);
                                return;

                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(MSG_SET_SYSTIME_FAIL);

                }
            }
        }).start();
    }


    public byte[] getSysTimeBytes() {

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR) - 2000;
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY); //24小时制
        int minute = c.get(Calendar.MINUTE);
        int second = c.get(Calendar.SECOND);

        byte[] yearB = Tools.int2byte(year);
        byte[] monthB = Tools.int2byte(month);
        byte[] dayB = Tools.int2byte(day);
        byte[] hourB = Tools.int2byte(hour);
        byte[] minuteB = Tools.int2byte(minute);
        byte[] secondB = Tools.int2byte(second);


        byte[] set_sys_time_cmd = new byte[11];
        set_sys_time_cmd[0] = 0x0A;
        set_sys_time_cmd[1] = (byte) 0xFF;
        set_sys_time_cmd[2] = 0x08;
        set_sys_time_cmd[3] = 0x16;
        set_sys_time_cmd[4] = yearB[0];
        set_sys_time_cmd[5] = monthB[0];
        set_sys_time_cmd[6] = dayB[0];
        set_sys_time_cmd[7] = hourB[0];
        set_sys_time_cmd[8] = minuteB[0];
        set_sys_time_cmd[9] = secondB[0];

        byte[] a = new byte[1];
        for (int i = 0; i < set_sys_time_cmd.length - 1; i++) {
            a[0] += set_sys_time_cmd[i];
        }
        a[0] = (byte) ((~a[0]) + 1);

        set_sys_time_cmd[10] = a[0];

        return set_sys_time_cmd;
    }


    /**
     * 读设备出厂编号
     */
    public void readFactoryNum() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    InputStream is = null;

                    try {
                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_FACNUM_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                        //防止阻塞
                        /*if(dis.available() <= 0){
                            mHandler.sendEmptyMessage(MSG_READ_FACUNM_FAIL);
                            return;
                        }*/

                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            byte[] addrLenStatus = new byte[3];
                            for (int i = 0; i < 3; i++) {
                                addrLenStatus[i] = dis.readByte();
                            }
                            if ((addrLenStatus[2] & 0xff) == 0x00) { //状态位，表示成功
                                byte[] fact_num = new byte[16];
                                for (int i = 0; i < 16; i++) {
                                    fact_num[i] = dis.readByte();
                                }
                                String fact_num_str = new String(fact_num);
                                fact_num_str = fact_num_str.substring(0, fact_num_str.length() - 1);
                                if (!TextUtils.isEmpty(fact_num_str)) {
                                    Message message = Message.obtain();
                                    message.obj = fact_num_str;
                                    message.what = MSG_READ_FACNUM_OK;
                                    mHandler.sendMessage(message);
                                    return;
                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(MSG_READ_FACUNM_FAIL);


                }
            }
        }).start();

    }


    /**
     * 读设备GPRS连接状态
     */
    public void readGPRSCntState() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    InputStream is = null;

                    try {
                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_GPRS_CONNECT_STATE_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());


                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            int cmd = dis.readByte() & 0xff;
                            if (cmd == 0x29) { //cmd
                                int len = dis.readByte() & 0xff;
                                if (len == 0x25) {
                                    int status = dis.readByte() & 0xff;
                                    if (status == 0x00) { //success
                                        byte[] gprsCntIp = new byte[32];
                                        byte[] gprsCntPort = new byte[4];
                                        int gprsCntIpIndex = 0, gprsCntPortIndex = 0, gprsCntState = 0;
                                        for (int i = 0; i < 35; i++) {
                                            if (i < 32) {
                                                gprsCntIp[gprsCntIpIndex++] = dis.readByte();
                                            } else if (i >= 32 && i < 34) {
                                                gprsCntPort[gprsCntPortIndex++] = dis.readByte();
                                            } else if (i == 34) {
                                                gprsCntState = dis.readByte() & 0xff;
                                            } else {
                                                dis.readByte();
                                            }
                                        }

                                        String gprsCntIpStr = new String(gprsCntIp);
                                        String gprsCntPortStr = String.valueOf(Tools.byte2int(gprsCntPort));
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("gprsCntIp", gprsCntIpStr);
                                        map.put("gprsCntPort", gprsCntPortStr);
                                        map.put("gprsCntState", gprsCntState);

                                        Message message = Message.obtain();
                                        message.what = MSG_READ_GPRS_CONNECT_STATE_OK;
                                        message.obj = map;
                                        mHandler.sendMessage(message);
                                        return;


                                    }
                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(MSG_READ_GPRS_CONNECT_STATE_FAIL);


                }
            }
        }).start();

    }


    /**
     * 读设备LAN连接状态
     */
    public void readLANCntState() {


        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    InputStream is = null;

                    try {
                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_LAN_CONNECT_STATE_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());


                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            int cmd = dis.readByte() & 0xff;
                            if (cmd == 0x2B) { //cmd
                                int len = dis.readByte() & 0xff;
                                if (len == 0x25) {
                                    int status = dis.readByte() & 0xff;
                                    if (status == 0x00) { //opt success
                                        byte[] lanCntIp = new byte[32];
                                        byte[] lanCntPort = new byte[4];
                                        int lanCntIpIndex = 0, lanCntPortIndex = 0, lanCntState = 0;
                                        for (int i = 0; i < 35; i++) {
                                            if (i < 32) {
                                                lanCntIp[lanCntIpIndex++] = dis.readByte();
                                            } else if (i >= 32 && i < 34) {
                                                lanCntPort[lanCntPortIndex++] = dis.readByte();
                                            } else if (i == 34) {
                                                lanCntState = dis.readByte() & 0xff;
                                            } else {
                                                dis.readByte();
                                            }
                                        }


                                        String gprsCntIpStr = new String(lanCntIp);
                                        String gprsCntPortStr = String.valueOf(Tools.byte2int(lanCntPort));
                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("lanCntIp", gprsCntIpStr);
                                        map.put("lanCntPort", gprsCntPortStr);
                                        map.put("lanCntState", lanCntState);

                                        Message message = Message.obtain();
                                        message.what = MSG_READ_LAN_CONNECT_STATE_OK;
                                        message.obj = map;
                                        mHandler.sendMessage(message);
                                        return;


                                    } else if (status == 0x80) { //设备不支持lan功能
                                        mHandler.sendEmptyMessage(MSG_DEVICE_UNSUPPORT_LAN);
                                        return;
                                    }
                                } else if (len == 2) { //设备不支持lan功能
                                    mHandler.sendEmptyMessage(MSG_DEVICE_UNSUPPORT_LAN);
                                    return;
                                }
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(MSG_READ_LAN_CONNECT_STATE_FAIL);


                }
            }
        }).start();

    }


    /**
     * 读设备信息
     */
    public void readDeviceInfo() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    // TODO Auto-generated method stub

                    try {

                        InputStream is = null;

                        is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(SCAN_DEVICE_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                        data[0] = dis.readByte();
                        int answer = data[0] & 0xff;

                        if (answer == 0x0B) {  //Transponder found
                            byte[] addrLenStatus = new byte[3];
                            for (int i = 0; i < 3; i++) {
                                addrLenStatus[i] = dis.readByte();
                            }
                            data[1] = addrLenStatus[0];  //地址
                            data[2] = addrLenStatus[1];  //长度
                            data[3] = addrLenStatus[2];  //状态位
                            int start = 4;
                            if ((addrLenStatus[2] & 0xff) == 0x00) { //状态位，表示成功

                                int beepFlag = 0;
                                byte[] filterWindow = new byte[4];
                                for (int i = 0; i < 8; i++) { //再读掉8个字节
                                    data[start++] = dis.readByte();
                                    if (i == 4) { //获取蜂鸣器开关值
                                        beepFlag = data[start - 1];
                                    }
                                    if (i == 6) {
                                        filterWindow[0] = data[start - 1];
                                    }

                                    if (i == 7) {
                                        filterWindow[1] = data[start - 1];
                                    }

                                }

                                //String device_id_str = new String();
                                //获取产品序列号

                               /* byte[] device_id = new byte[19];


                                for (int i = 0; i < 19; i++) {
                                    device_id[i] = dis.readByte();
                                    data[start++] = device_id[i];
                                }*/
                                byte[] device_id = new byte[16];


                                for (int i = 0; i < 16; i++) {
                                    device_id[i] = dis.readByte();
                                    data[start++] = device_id[i];
                                }
                                String device_id_str = new String(device_id);
                                //                                device_id_str = device_id_str.substring(0, device_id_str.length() - 1);


                                /*for (int i=0;i<16;i++){
                                    device_id_str += String.format("%02X", device_id[i] & 0xff);;
                                }*/

                                byte[] stopTimeWindow = new byte[4];
                                for (int i = 0; i < 2; i++) {
                                    stopTimeWindow[i] = dis.readByte();
                                    data[start++] = stopTimeWindow[i];
                                }

                                //获取dhcp开关
                                data[start++] = dis.readByte();
                                int dhcpFlag = data[start - 1];

                                for (int i = 0; i < 14; i++) {
                                    data[start++] = dis.readByte();
                                }

                                //lan ip
                                byte[] lanIpB = new byte[4];
                                lanIpB[0] = data[31];
                                lanIpB[1] = data[32];
                                lanIpB[2] = data[33];
                                lanIpB[3] = data[34];
                                String lanIpStr = "";
                                for (int i = 0; i < 4; i++) {
                                    lanIpStr += String.valueOf(lanIpB[i] & 0xff);
                                    if (i != 3) {
                                        lanIpStr += ".";
                                    }
                                }


                                //lan子网掩码
                                byte[] lanZwymB = new byte[4];
                                lanZwymB[0] = data[35];
                                lanZwymB[1] = data[36];
                                lanZwymB[2] = data[37];
                                lanZwymB[3] = data[38];
                                String lanZwymStr = "";
                                for (int i = 0; i < 4; i++) {
                                    lanZwymStr += String.valueOf(lanZwymB[i] & 0xff);
                                    if (i != 3) {
                                        lanZwymStr += ".";
                                    }
                                }


                                //lan 网关
                                byte[] lanWgB = new byte[4];
                                lanWgB[0] = data[39];
                                lanWgB[1] = data[40];
                                lanWgB[2] = data[41];
                                lanWgB[3] = data[42];
                                String lanWgStr = "";
                                for (int i = 0; i < 4; i++) {
                                    lanWgStr += String.valueOf(lanWgB[i] & 0xff);
                                    if (i != 3) {
                                        lanWgStr += ".";
                                    }
                                }

                                byte[] lanPortB = new byte[4];
                                lanPortB[0] = data[43];
                                lanPortB[1] = data[44];
                                String lanPortStr = String.valueOf(Tools.byte2int(lanPortB));


                                //获取ip地址
                                byte[] ip = new byte[32];
                               /*  ip[0] = data[start];
                                for (int i = 1; i < 32; i++) {
                                    ip[i] = dis.readByte();
                                    data[start++] = ip[i];
                                }*/
                                for (int i = 0; i < 32; i++) {
                                    ip[i] = dis.readByte();
                                    data[start++] = ip[i];
                                }
                                String ipStr = new String(ip);

                                //获取端口
                                byte[] port = new byte[4];
                                for (int i = 0; i < 2; i++) {
                                    port[i] = dis.readByte();
                                    data[start++] = port[i];
                                }

                                String portStr = String.valueOf(Tools.byte2int(port));

                                byte[] deviceRssi = new byte[4];
                                byte[] deviceGrain = new byte[4];
                                int deviceRssiIndex = 0, deviceGrainIndex = 0;
                                int netState = 0;
                                int drctFlag = 0;
                                int drctTimeNum = 0;
                                for (int i = 0; i < 107; i++) { //107
                                    data[start++] = dis.readByte();

                                    if (i >= 93 && i <= 96) {
                                        deviceRssi[deviceRssiIndex] = data[start - 1];
                                        deviceRssiIndex++;
                                    }

                                    if (i >= 97 && i <= 100) {
                                        deviceGrain[deviceGrainIndex] = data[start - 1];
                                        deviceGrainIndex++;
                                    }

                                    if (i == 102) {
                                        netState = data[start - 1] & 0xff;
                                    }
                                    if (i == 103) {
                                        drctFlag = data[start - 1] & 0xff;
                                    }

                                    if (i == 104) {
                                        drctTimeNum = data[start - 1] & 0xff;
                                    }
                                }

                                //获取lan服务器ip地址
                                byte[] lanFwIpB = new byte[32];
                                for (int i = 0; i < 32; i++) {
                                    lanFwIpB[i] = data[79 + i];
                                }
                                String lanFwIpStr = new String(lanFwIpB);


                                byte[] lanFwPortB = new byte[4];
                                lanFwPortB[0] = data[111];
                                lanFwPortB[1] = data[112];
                                String lanFwPortStr = String.valueOf(Tools.byte2int(lanFwPortB));


                                int[] deviceRssis = new int[4];
                                String[] deviceGrainStrs = new String[4];


                                //获取rssi
                                for (int i = 0; i < 4; i++) {
                                    deviceRssis[i] = (int) deviceRssi[i];
                                }


                                //获取增域
                                for (int i = 0; i < 4; i++) {
                                    deviceGrainStrs[i] = String.valueOf(deviceGrain[i]);
                                }


                                String versionStr = "";
                                if ((addrLenStatus[1] & 0xff) == 0xBA || ((addrLenStatus[1] & 0xff) == 0xCE)) { //获取版本号
                                    byte[] version = new byte[2];
                                    for (int i = 0; i < 2; i++) {
                                        version[i] = dis.readByte();
                                        versionStr += String.valueOf(version[i]);
                                        if (i == 0) {
                                            versionStr += ".";
                                        }
                                    }
                                }

                                String timeStr = null;
                                if ((addrLenStatus[1] & 0xff) == 0xCE) {
                                    byte[] time = new byte[20];
                                    for (int i = 0; i < 20; i++) {
                                        time[i] = dis.readByte();
                                    }
                                    timeStr = new String(time);
                                }

                                int filterNum = Tools.byte2int(filterWindow);
                                int stopTimeNum = Tools.byte2int(stopTimeWindow);

                                mScanListener.scanned(new Transponder(device_id_str, ipStr, portStr, data, versionStr, timeStr, deviceGrainStrs, deviceRssis, beepFlag, dhcpFlag, drctFlag, filterNum, netState, lanIpStr,
                                        lanPortStr, lanZwymStr, lanWgStr, lanFwIpStr, lanFwPortStr, stopTimeNum, drctTimeNum));
                                return;
                            }

                        } else {

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    mScanListener.scanned(null);
                }
            }
        }).start();


    }


    public boolean isScanFlag() {
        return scanFlag;
    }

    public void setScanFlag(boolean scanFlag) {
        this.scanFlag = scanFlag;
    }

    /*设置参数*/
    public void setParams(final byte[] command) {

        new Thread(new Runnable() {

            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    //Looper.prepare();
                    try {
                        InputStream is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();
                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(command);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());


                        int answer = dis.readByte() & 0xff;

                        if (answer == 0x0B) {  //Transponder found
                            int towB = dis.readByte() & 0xff;
                            if (towB == 0x13) {
                                int len = dis.readByte() & 0xff;
                                if (len == 0x02) {
                                    int status = dis.readByte() & 0xff;
                                    if (status == 0x00) { //设置参数成功
                                        mHandler.sendEmptyMessage(MSG_SET_PARAMS_OK);
                                        return;
                                    }
                                }
                            }

                        } else {

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    //Message message = Message.obtain();
                    //message.obj = reback;
                    //message.what = MSG_SET_PARAMS_FAIL;
                    mHandler.sendEmptyMessage(MSG_SET_PARAMS_FAIL);

                    //Looper.loop();
                }
            }
        }).start();


    }


    public void sendCarrierTest() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {

                    try {
                        InputStream is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);

                        OutputStream os = mSocket.getOutputStream();
                        os.write(CARRIER_TESTING);
                        os.flush();
                        os.close();
                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());


                        int answer = dis.readByte() & 0xff;

                        if (answer == 0x0B) {  //Transponder found
                            int addr = dis.readByte() & 0xff;
                            int len = dis.readByte() & 0xff;

                            int status = dis.readByte() & 0xff;
                            if (status == 0x00) { //发送载波指令成功
                                mHandler.sendEmptyMessage(MSG_CARRIER_TEST_OK);
                                return;
                            }

                        }
                        dis.close();
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    //Message message = Message.obtain();
                    //message.obj = reback;
                    //message.what = MSG_SET_PARAMS_FAIL;
                    mHandler.sendEmptyMessage(MSG_CARRIER_TEST_FIAL);

                    //Looper.loop();
                }
            }
        }).start();
    }


    /**
     * 调试天线
     */
    public void debugTX(final byte[] command) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                while (debugTXFlag) {
                    synchronized (mSocket) {
                        // TODO Auto-generated method stub

                        try {
                            InputStream is = mSocket.getInputStream();
                            int r = 1;
                            while (r > 0) { //清空输入流里面的数据
                                r = is.available();

                                if (r > 0) {
                                    byte[] b = new byte[r];
                                    r = is.read(b, 0, r);
                                }
                            }

                            Thread.sleep(200);  //间隔设置为0.2s

                            OutputStream os = mSocket.getOutputStream();
                            os.write(command);
                            os.flush();

                            DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                            //防止阻塞
                          /*  if(dis.available() <= 0){
                               continue;
                            }*/

                            int answer = dis.readByte() & 0xff;
                            if (answer == 0x0B) {
                                byte[] addrLenStatus = new byte[3];
                                dis.read(addrLenStatus);

                                if ((addrLenStatus[2] & 0xff) == 0x00) {//返回成功
                                    byte[] tagid = new byte[4];
                                    byte[] totalNumB = new byte[2];
                                    int pos = 0;
                                    int rssi = 0;
                                    int tagidIndex = 0;

                                    for (int i = 0; i < 11; i++) {
                                        if (i == 0) {
                                            pos = dis.readByte();
                                        } else if (i >= 2 && i <= 5) {
                                            tagid[tagidIndex++] = dis.readByte();
                                        } else if (i == 10) {
                                            rssi = dis.readByte();
                                        } else {
                                            dis.readByte();
                                        }

                                    }

                                    int totalNum = 0;
                                    if ((addrLenStatus[1] & 0xff) == 0x15) {
                                        byte[] other = new byte[8];
                                        dis.read(other);

                                        totalNumB[0] = other[6];
                                        totalNumB[1] = other[7];

                                        String totalNumStr = Tools.bytesToHexString2(totalNumB);
                                        totalNum = Integer.parseInt(totalNumStr, 16);
                                        //totalNum = Tools.byte2int(totalNumB);

                                    } else if ((addrLenStatus[1] & 0xff) == 0x13) {
                                        totalNum = -1;
                                    }


                                    String tadidStr = Tools.bytesToHexString2(tagid);
                                    Tag tag = new Tag(tadidStr.toUpperCase(), rssi, pos, totalNum);
                                    Message message = Message.obtain();
                                    message.obj = tag;
                                    message.what = MSG_DEBUG_TX_OK;
                                    mHandler.sendMessage(message);
                                    continue;

                                }

                            }


                        } catch (IOException e) {
                            e.printStackTrace();

                        } catch (InterruptedException e) {
                            e.printStackTrace();

                        }

                        mHandler.sendEmptyMessage(MSG_DEBUG_TX_FAIL);

                    }
                }
            }

        }).start();

    }


    /**
     * 读设备GPRS信号值
     */
    public void readGPRSNum() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    // TODO Auto-generated method stub

                    try {
                        InputStream is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);  //间隔设置为0.2s

                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_GPRS_NUM_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                        int sof = dis.readByte() & 0xff;
                        if (sof == 0x0B) {
                            int cmd = dis.readByte() & 0xff;
                            if (cmd == 0x0C) {
                                byte[] data = new byte[4];
                                dis.read(data);

                                if ((data[0] & 0xff) == 0x03 && (data[1] & 0xff) == 0x00) { //操作成功
                                    int gprsNum = data[2] & 0xff;
                                    Message message = Message.obtain();
                                    message.what = MSG_READ_GPRS_OK;
                                    message.obj = gprsNum;
                                    mHandler.sendMessage(message);
                                    return;
                                }
                            }

                        }


                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    mHandler.sendEmptyMessage(MSG_READ_GPRS_FAIL);

                }
            }
        }).start();

    }


    /**
     * 停止调试天线
     */
    public void stopDebugTx() {

        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    // TODO Auto-generated method stub

                    try {
                        InputStream is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        //Thread.sleep(200);  //间隔设置为0.2s

                        OutputStream os = mSocket.getOutputStream();
                        os.write(STOP_DEBUG_TX_COMMAND);
                        os.flush();

                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                }
            }
        }).start();

    }

    /**
     * 开启线程，不断扫描标签
     */
    public void scanTag() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                while (scanFlag) {
                    synchronized (mSocket) {
                        try {
                            if (mSocket == null) {
                                mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                                return;
                            }
                            InputStream is = mSocket.getInputStream();
                            int r = 1;
                            while (r > 0) { //清空输入流里面的数据
                                r = is.available();
                                if (r > 0) {
                                    byte[] b = new byte[r];
                                    r = is.read(b, 0, r);
                                }
                            }

                            Thread.sleep(50);  //间隔设置为0.05s

                            DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                            int ans, len = 0;
                            byte[] sof = new byte[1];
                            byte[] eof = new byte[2];
                            byte[] buff = new byte[1024];
                            byte b;
                            boolean readFlag = true;
                            int count = 0;

                            while (readFlag) {
                                try {

                                  /*  if(dis.available() <= 0){
                                       readFlag = false;
                                        continue;
                                    }*/

                                    count++;

                                    if (count > 30) {
                                        readFlag = false;
                                    }

                                    b = dis.readByte();

                                    ans = b & 0xff;
                                    if (ans == 0x02) {
                                        sof[0] = b;
                                    } else if (ans == 0x0D) {
                                        eof[0] = b;

                                    } else if (ans == 0x0A) {
                                        eof[1] = b;
                                        readFlag = false;
                                    } else {
                                        buff[len++] = b;
                                    }

                                } catch (EOFException e) {
                                    readFlag = false;
                                }
                            }


                            byte data[] = new byte[len];
                            for (int i = 0; i < len; i++) {
                                data[i] = buff[i];
                            }

                            if (((sof[0] & 0xff) == 0x02) && ((eof[0] & 0xff) == 0x0D) && ((eof[1] & 0xff) == 0x0A)) {

                                if (data != null && data.length > 4) {
                                    byte[] lenB = new byte[4];
                                    byte[] addr = new byte[4]; //通道
                                    addr[0] = data[0];
                                    addr[1] = data[1];

                                    lenB[0] = data[2];
                                    lenB[1] = data[3];


                                    int dataLen = Tools.bytesAscii2int(lenB);
                                    int pos = Tools.bytesAscii2int(addr); //通道  1（东）,2（南）,3（西）,4（北）

                                    if (data.length == dataLen * 2 + 6) {
                                        byte[] realData = new byte[dataLen * 2];
                                        int realDataIndex = 0;
                                        for (int i = 4; i < data.length - 2; i++) {
                                            realData[realDataIndex++] = data[i];
                                        }
                                        byte[] t = new byte[2];
                                        int[] realDataInt = new int[dataLen];
                                        int k = 0;
                                        for (int i = 0; i < (dataLen * 2); ) {
                                            t[0] = realData[i];
                                            t[1] = realData[i + 1];
                                            i += 2;
                                            realDataInt[k++] = Tools.bytesAscii2int(t);
                                        }

                                        String tagIdStr = "";
                                        //                                        if (realDataInt[0] == 0x20) {

                                        for (int i = 1; i < 5; i++) {
                                            String str = Integer.toHexString(realDataInt[i]);
                                            if (str != null && str.length() < 2) {
                                                str = "0" + str;
                                            }
                                            tagIdStr += str;

                                        }

                                        int[] warn = new int[1];
                                        warn[0] = realDataInt[8];
                                        int rssi = realDataInt[9];


                                        rssi = 0x0FFFFFF00 | rssi;
                                        Tag tag = new Tag();
                                        tag.setTagId(tagIdStr.toUpperCase());
                                        tag.setRssi(rssi);
                                        tag.setPosition(pos);
                                        tag.setTime(System.currentTimeMillis());


                                        Message message = Message.obtain();
                                        message.obj = tag;
                                        message.what = MSG_READ_TAG_OK;
                                        mHandler.sendMessage(message);

                                    } else {
                                        mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                                    }

                                } else {
                                    mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                                }


                            } else {
                                mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                        }

                    }
                }
            }
        }).start();

    }

    /**
     * 扫描6个字节的tag
     */
    public void scanSixByteTag() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                while (scanFlag) {
                    synchronized (mSocket) {
                        try {
                            if (mSocket == null) {
                                mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                                return;
                            }
                            InputStream is = mSocket.getInputStream();
                            int r = 1;
                            while (r > 0) { //清空输入流里面的数据
                                r = is.available();
                                if (r > 0) {
                                    byte[] b = new byte[r];
                                    r = is.read(b, 0, r);
                                }
                            }

                            Thread.sleep(50);  //间隔设置为0.05s

                            DataInputStream dis = new DataInputStream(mSocket.getInputStream());

                            int ans, len = 0;
                            byte[] sof = new byte[1];
                            byte[] eof = new byte[2];
                            byte[] buff = new byte[1024];
                            byte b;
                            boolean readFlag = true;
                            int count = 0;

                            while (readFlag) {
                                try {

                                  /*  if(dis.available() <= 0){
                                       readFlag = false;
                                        continue;
                                    }*/

                                    count++;

                                    if (count > 30) {
                                        readFlag = false;
                                    }

                                    b = dis.readByte();

                                    ans = b & 0xff;
                                    if (ans == 0x02) {
                                        sof[0] = b;
                                    } else if (ans == 0x0D) {
                                        eof[0] = b;

                                    } else if (ans == 0x0A) {
                                        eof[1] = b;
                                        readFlag = false;
                                    } else {
                                        buff[len++] = b;
                                    }

                                } catch (EOFException e) {
                                    readFlag = false;
                                }
                            }


                            byte data[] = new byte[len];
                            for (int i = 0; i < len; i++) {
                                data[i] = buff[i];
                            }

                            if (((sof[0] & 0xff) == 0x02) && ((eof[0] & 0xff) == 0x0D) && ((eof[1] & 0xff) == 0x0A)) {

                                if (data != null && data.length > 4) {
                                    byte[] lenB = new byte[4];
                                    byte[] addr = new byte[4]; //通道
                                    addr[0] = data[0];
                                    addr[1] = data[1];

                                    lenB[0] = data[2];
                                    lenB[1] = data[3];


                                    int dataLen = Tools.bytesAscii2int(lenB);
                                    int pos = Tools.bytesAscii2int(addr); //通道  1（东）,2（南）,3（西）,4（北）

                                    if (data.length == dataLen * 2 + 6) {
                                        byte[] realData = new byte[dataLen * 2];
                                        int realDataIndex = 0;
                                        for (int i = 4; i < data.length - 2; i++) {
                                            realData[realDataIndex++] = data[i];
                                        }
                                        byte[] t = new byte[2];
                                        int[] realDataInt = new int[dataLen];
                                        int k = 0;
                                        for (int i = 0; i < (dataLen * 2); ) {
                                            t[0] = realData[i];
                                            t[1] = realData[i + 1];
                                            i += 2;
                                            realDataInt[k++] = Tools.bytesAscii2int(t);
                                        }

                                        String tagIdStr = "";
                                        //                                        if (realDataInt[0] == 0x20) {

                                        for (int i = 1; i < 5; i++) {
                                            String str = Integer.toHexString(realDataInt[i]);
                                            if (str != null && str.length() < 2) {
                                                str = "0" + str;
                                            }
                                            tagIdStr += str;

                                        }

                                        int[] warn = new int[1];
                                        warn[0] = realDataInt[8];
                                        int rssi = realDataInt[9];


                                        rssi = 0x0FFFFFF00 | rssi;
                                        Tag tag = new Tag();
                                        tag.setTagId(tagIdStr.toUpperCase());
                                        tag.setRssi(rssi);
                                        tag.setPosition(pos);
                                        tag.setTime(System.currentTimeMillis());


                                        Message message = Message.obtain();
                                        message.obj = tag;
                                        message.what = MSG_READ_TAG_OK;
                                        mHandler.sendMessage(message);

                                    } else {
                                        mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                                    }

                                } else {
                                    mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                                }


                            } else {
                                mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                        }

                    }
                }
            }
        }).start();

    }

    /**
     * 开启线程，不断扫描标签
     */
    public void scanTagHuaWang() {

        new Thread(new Runnable() {

            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                while (scanFlag) {
                    synchronized (mSocket) {
                        // TODO Auto-generated method stub

                        try {
                            if (mSocket == null) {
                                mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                                return;
                            }
                            InputStream is = mSocket.getInputStream();
                            int r = 1;
                            while (r > 0) { //清空输入流里面的数据
                                r = is.available();

                                if (r > 0) {
                                    byte[] b = new byte[r];
                                    r = is.read(b, 0, r);
                                }
                            }

                            Thread.sleep(50);  //间隔设置为0.05s

                            DataInputStream dis = new DataInputStream(mSocket.getInputStream());


                            int ans, len = 0;
                            byte[] sof = new byte[1];
                            byte[] eof = new byte[2];
                            byte[] buff = new byte[1024];
                            byte b;
                            boolean readFlag = true;
                            int count = 0;

                            while (readFlag) {
                                try {

                                  /*  if(dis.available() <= 0){
                                       readFlag = false;
                                        continue;
                                    }*/

                                    count++;

                                    if (count > 30) {
                                        readFlag = false;
                                    }

                                    b = dis.readByte();

                                    ans = b & 0xff;
                                    if (ans == 0x02) {
                                        sof[0] = b;
                                    } else if (ans == 0x0D) {
                                        eof[0] = b;

                                    } else if (ans == 0x0A) {
                                        eof[1] = b;
                                        readFlag = false;
                                    } else {
                                        buff[len++] = b;
                                    }

                                } catch (EOFException e) {
                                    readFlag = false;
                                }
                            }


                            byte data[] = new byte[len];
                            for (int i = 0; i < len; i++) {
                                data[i] = buff[i];
                            }


                            if (((sof[0] & 0xff) == 0x02) && ((eof[0] & 0xff) == 0x0D) && ((eof[1] & 0xff) == 0x0A)) {

                                if (data != null && data.length > 4) {
                                    byte[] lenB = new byte[4];
                                    byte[] addr = new byte[4]; //通道
                                    addr[0] = data[0];
                                    addr[1] = data[1];

                                    lenB[0] = data[2];
                                    lenB[1] = data[3];


                                    int dataLen = Tools.bytesAscii2int(lenB);
                                    int pos = Tools.bytesAscii2int(addr); //通道  1（东）,2（南）,3（西）,4（北）

                                    if (data.length == dataLen * 2 + 6) {
                                        byte[] realData = new byte[dataLen * 2];
                                        int realDataIndex = 0;
                                        for (int i = 4; i < data.length - 2; i++) {
                                            realData[realDataIndex++] = data[i];
                                        }
                                        byte[] t = new byte[2];
                                        int[] realDataInt = new int[dataLen];
                                        int k = 0;
                                        for (int i = 0; i < (dataLen * 2); ) {
                                            t[0] = realData[i];
                                            t[1] = realData[i + 1];
                                            i += 2;
                                            realDataInt[k++] = Tools.bytesAscii2int(t);
                                        }

                                        String tagIdStr = "";
                                        String tagIdStr1 = "";

                                        for (int i = 1; i < 5; i++) {
                                            String str1 = Integer.toHexString(realDataInt[i]);
                                            str1 = surelength(str1, 2);
                                            tagIdStr1 += str1;

                                            //转成华为格式
                                            String str = Integer.toBinaryString(realDataInt[i]);
                                            //不够8位前面补0
                                            str = surelength(str, 8);
                                            tagIdStr += str;

                                        }

                                        CLog.e("LogInterceptor 二进制", tagIdStr);
                                        CLog.e("LogInterceptor 十六进制", tagIdStr1);
                                        String tagTypeString = tagIdStr.substring(0, 6);
                                        String mString = tagIdStr.substring(6, 11);
                                        String batchString = tagIdStr.substring(11, 22);
                                        String itemString = tagIdStr.substring(22, 32);

                                        //二进制转十进制
                                        String tagType = Tools.toD(tagTypeString, 2);
                                        //不足2位前面补0
                                        tagType = surelength(tagType, 2);

                                        String m = Tools.toD(mString, 2);
                                        m = surelength(m, 2);

                                        String batch = Tools.toD(batchString, 2);
                                        batch = surelength(batch, 4);

                                        String item = Tools.toD(itemString, 2);
                                        item = surelength(item, 4);

                                        tagIdStr = tagType + m + batch + item;
                                        CLog.e("LogInterceptor 十进制", tagIdStr);

                                        int[] warn = new int[1];
                                        warn[0] = realDataInt[8];
                                        int rssi = realDataInt[9];


                                        rssi = 0x0FFFFFF00 | rssi;
                                        Tag tag = new Tag();
                                        tag.setTagId(tagIdStr.toUpperCase());
                                        tag.setRssi(rssi);
                                        tag.setPosition(pos);


                                        Message message = Message.obtain();
                                        message.obj = tag;
                                        message.what = MSG_READ_TAG_OK;
                                        mHandler.sendMessage(message);

                                    } else {
                                        mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                                    }


                                } else {
                                    mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                                }


                            } else {
                                mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                        }

                    }
                }
            }
        }).start();


    }


    /**
     * 读设备天线信息
     */
    public void readTXInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                synchronized (mSocket) {
                    // TODO Auto-generated method stub

                    try {
                        InputStream is = mSocket.getInputStream();
                        int r = 1;
                        while (r > 0) { //清空输入流里面的数据
                            r = is.available();

                            if (r > 0) {
                                byte[] b = new byte[r];
                                r = is.read(b, 0, r);
                            }
                        }

                        Thread.sleep(200);  //间隔设置为0.2s
                        OutputStream os = mSocket.getOutputStream();
                        os.write(READ_TX_INFO_COMMAND);
                        os.flush();

                        DataInputStream dis = new DataInputStream(mSocket.getInputStream());
                        int answer = dis.readByte() & 0xff;
                        if (answer == 0x0B) {
                            int cmd = dis.readByte() & 0xff;
                            if (cmd == 0x28) {
                                int len = dis.readByte() & 0xff;
                                if (len == 0x12) {
                                    int status = dis.readByte() & 0xff;
                                    if (status == 0x00) {
                                        byte[] version = new byte[8];
                                        byte[] grain = new byte[4];
                                        int[] rssi = new int[4];

                                        //读取4根天线的版本
                                        for (int i = 0; i < 8; i++) {
                                            version[i] = dis.readByte();
                                        }

                                        //读取4根天线的增益
                                        for (int i = 0; i < 4; i++) {
                                            grain[i] = dis.readByte();
                                        }

                                        //读取4根天线的信号量
                                        for (int i = 0; i < 4; i++) {
                                            rssi[i] = dis.readByte();
                                        }


                                        String[] vStrArray = new String[4];

                                        String[] txGrainStrs = new String[4];

                                        //转换获取4根天线的版本
                                        for (int i = 0; i < 4; i++) {
                                            if (((version[2 * i] & 0xff) == 0xFF) && ((version[2 * i + 1] & 0xff) == 0xFF)) {
                                                vStrArray[i] = mContext.getResources().getString(R.string.none);
                                            } else {
                                                vStrArray[i] = String.valueOf(version[2 * i]) + "." + String.valueOf(version[2 * i + 1]);
                                            }

                                        }


                                        //转换获取增域
                                        for (int i = 0; i < 4; i++) {
                                            if ((grain[i] & 0xff) == 0xFF) {
                                                txGrainStrs[i] = mContext.getResources().getString(R.string.none);
                                            } else {
                                                txGrainStrs[i] = String.valueOf(grain[i]);
                                            }

                                        }


                                        Map<String, Object> map = new HashMap<String, Object>();
                                        map.put("versionArray", vStrArray);
                                        map.put("grainArray", txGrainStrs);
                                        map.put("rssiArray", rssi);

                                        Message message = Message.obtain();
                                        message.obj = map;
                                        message.what = MSG_READ_TX_INFO_OK;
                                        mHandler.sendMessage(message);
                                        return;

                                    }
                                }
                            }
                        }


                    } catch (IOException e) {
                        e.printStackTrace();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                mHandler.sendEmptyMessage(MSG_READ_TX_INFO_FAIL);
            }
        }).start();
    }


    /**
     * 开启标签过滤，提高搜索标签效率
     */
    public void openTagDataOutput() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                try {
                    OutputStream os = mSocket.getOutputStream();
                    os.write(OPEN_TAG_OUTPUT_COMMAND);
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }


    /**
     * 6个字节的标签数据
     */
    public void openTagDataOutputOfSixByte() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                try {
                    OutputStream os = mSocket.getOutputStream();
                    os.write(START_SET_SIX_BYTE_OF_SEARCH);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 开启RF
     */
    public void openRF() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                //synchronized (mSocket){
                // TODO Auto-generated method stub

                try {

                    OutputStream os = mSocket.getOutputStream();
                    os.write(OPEN_RF);
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();

                }
                //}
            }
        }).start();
    }

    /**
     * 关闭标签过滤，退出标签搜索界面时需调用此方法关闭
     */
    public void closeRF() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                //synchronized (mSocket){
                // TODO Auto-generated method stub

                try {

                    OutputStream os = mSocket.getOutputStream();
                    os.write(CLOSE_RF);
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();

                }
                //  }
            }
        }).start();
    }

    /**
     * 关闭标签过滤，退出标签搜索界面时需调用此方法关闭
     */
    public void closeTagDataOutput() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                try {
                    OutputStream os = mSocket.getOutputStream();
                    os.write(CLOSE_TAG_OUTPUT_COMMAND);
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }

    /**
     * 关闭6个字节的搜索
     */
    public void closeSixByteTagDataOutput() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                try {

                    OutputStream os = mSocket.getOutputStream();
                    os.write(STOP_SET_SIX_BYTE_OF_SEARCH);
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }

    /**
     * 过滤标签数,传统蓝牙使用
     */
    public void filterTagNumber(final byte[] cmd) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }

                //synchronized (mSocket){
                try {
                    OutputStream os = mSocket.getOutputStream();
                    os.write(cmd);
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
            // }
        }).start();
    }


    public void filter() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                //synchronized (mSocket){
                try {

                    OutputStream os = mSocket.getOutputStream();
                    os.write(FILTER_COMMAND);
                    os.flush();

                } catch (IOException e) {
                    e.printStackTrace();

                }
                // }
            }
        }).start();
    }

    public void setSixByteFilter() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                if (mSocket == null) {
                    mHandler.sendEmptyMessage(MSG_NO_BLUETOOTH_SOCKET);
                    return;
                }
                try {

                    OutputStream os = mSocket.getOutputStream();
                    os.write(START_SET_SIX_BYTE_OF_SEARCH);
                    os.flush();
                } catch (IOException e) {
                    e.printStackTrace();

                }
            }
        }).start();
    }


    public void setScanListener(ScanListener mScanListener) {
        this.mScanListener = mScanListener;
    }


    public interface ScanListener {
        void scanned(Transponder trp);

        void begin();
    }


    public int getBluetoothType() {
        return bluetoothType;
    }

    public void setBluetoothType(int bluetoothType) {
        this.bluetoothType = bluetoothType;
    }


    ////////////////////////////////////////////////////BLE/////////////////////////////////////////////////////////////////////////////////////

    /**
     * 设置参数
     * @param mCmdType
     * @param cmd
     */
    public synchronized void bleSetParams(int mCmdType, final byte[] cmd) {
        if (mBluetoothGatt != null && controlCharacteristic != null) {
            synchronized (controlCharacteristic) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setBleCmdType(mCmdType);
                        Object[] subAry = Tools.splitAry(cmd, 20);//分割后的子块数组
                        for (Object obj : subAry) {
                            byte[] aryItem = (byte[]) obj;
                            controlCharacteristic.setValue(aryItem);
                            mBluetoothGatt.writeCharacteristic(controlCharacteristic);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();
            }
        }
    }


    /**
     * 获取数据标签
     *
     * @param cmd
     */
    public synchronized void bleDebugTx(final byte[] cmd) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (debugTXFlag) {
                    if (mBluetoothGatt != null && controlCharacteristic != null) {
                        synchronized (controlCharacteristic) {
                            setBleCmdType(Constant.BLE_DEBUG_TX);
                            isFirstPackage = true;
                            controlCharacteristic.setValue(cmd);
                            mBluetoothGatt.writeCharacteristic(controlCharacteristic);

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }).start();

    }


    /**
     * 过滤标签数,低功耗蓝牙使用
     *
     * @param cmd
     */
    public synchronized void bleFilterTagNum(byte[] cmd) {
        if (mBluetoothGatt != null && controlCharacteristic != null) {
            synchronized (controlCharacteristic) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        setBleCmdType(Constant.BLE_FILTER_TAG_NUM);

                        Object[] subAry = Tools.splitAry(cmd, 20);//分割后的子块数组

                        for (Object obj : subAry) {//打印输出结果
                            byte[] aryItem = (byte[]) obj;
                            controlCharacteristic.setValue(aryItem);

                            mBluetoothGatt.writeCharacteristic(controlCharacteristic);
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }).start();

            }
        }
    }


    public synchronized void bleSendCmd(int cmdType, byte[] cmd, boolean isNeedSubPackageReceived) {
        if (mBluetoothGatt != null && controlCharacteristic != null) {
            synchronized (controlCharacteristic) {
                setBleCmdType(cmdType);
                if (isNeedSubPackageReceived) {
                    isFirstPackage = true;
                }
                controlCharacteristic.setValue(cmd);
                mBluetoothGatt.writeCharacteristic(controlCharacteristic);
            }
        }
    }


    public boolean isNextTagDataCloudEnter() {
        return isNextTagDataCloudEnter;
    }

    public void setIsNextTagDataCloudEnter(boolean isNextTagDataCloudEnter) {
        this.isNextTagDataCloudEnter = isNextTagDataCloudEnter;
    }

    public boolean isFirstPackage() {
        return isFirstPackage;
    }

    public void setIsFirstPackage(boolean isFirstPackage) {
        this.isFirstPackage = isFirstPackage;
    }

    public int getBleCmdType() {
        return bleCmdType;
    }

    public void setBleCmdType(int bleCmdType) {
        if (bleCmdType == Constant.BLE_SCAN_TAG) {
            isNextTagDataCloudEnter = true;
        }
        this.bleCmdType = bleCmdType;
    }


    /**
     * @param context
     * @param bluetoothDevice 要连接的设备
     */
    public void connect(Context context, BluetoothDevice bluetoothDevice) {
        mBluetoothGatt = bluetoothDevice.connectGatt(context, false, bluetoothGattCallback);
    }


    /**
     * 关闭经典蓝牙
     */
    public void closeBLE() {
        setScanFlag(false);
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
        setState(STATE_NONE); //状态还原
    }


    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override //当连接上设备或者失去连接时会回调该函数
        public void onConnectionStateChange(BluetoothGatt gatt, int status, final int newState) {
            super.onConnectionStateChange(gatt, status, newState);

            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED: //成功连接BLE设备
                    mState = STATE_CONNECTED;
                    bluetoothType = BluetoothDevice.DEVICE_TYPE_LE;
                    mBluetoothGatt.discoverServices();// 连接成功后寻找服务
                    mHandler.obtainMessage(Constant.MESSAGE_STATE_CHANGE, mState, -1)
                            .sendToTarget();

                    break;
                case BluetoothProfile.STATE_CONNECTING:
                    mState = STATE_CONNECTING;
                    mHandler.obtainMessage(Constant.MESSAGE_STATE_CHANGE, mState, -1)
                            .sendToTarget();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    mState = STATE_NONE;
                    bluetoothType = BluetoothDevice.DEVICE_TYPE_UNKNOWN;
                    mHandler.obtainMessage(Constant.MESSAGE_STATE_CHANGE, mState, -1)
                            .sendToTarget();
                    //蓝牙断开时,手动关闭蓝牙,防止乱码
                    closeBLE();
                    break;
                case BluetoothProfile.STATE_DISCONNECTING:

                    break;
                default:
                    break;
            }

        }

        public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
            if (mAdapter == null || mBluetoothGatt == null) {
                return;
            }
            mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
            //这里可以加入判断对指定的UUID值进行订阅
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }

        //发现服务
        @Override //当设备是否找到服务时，会回调该函数
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status == BluetoothGatt.GATT_SUCCESS) {
                List<BluetoothGattService> services = mBluetoothGatt.getServices();//获取通信管道的服务列表
                for (BluetoothGattService bluetoothGattService : services) {

                    List<BluetoothGattCharacteristic> characteristics = bluetoothGattService.getCharacteristics();
                    for (BluetoothGattCharacteristic bluetoothGattCharacteristic : characteristics) {

                        if (bluetoothGattCharacteristic.getUuid().toString().equals("0000ffe1-0000-1000-8000-00805f9b34fb")) {
                            controlCharacteristic = bluetoothGattCharacteristic;
                            if (mBluetoothGatt.getDevice().getName().startsWith("MR3052") || mBluetoothGatt.getDevice().getName().startsWith("MR7914")
                                    || mBluetoothGatt.getDevice().getName().startsWith("MR7901P") || mBluetoothGatt.getDevice().getName().startsWith("HT101")
                                    || mBluetoothGatt.getDevice().getName().startsWith("MR3051B-") || mBluetoothGatt.getDevice().getName().startsWith("MR7912C-V")) {//读卡器设备
                                setCharacteristicNotification(controlCharacteristic, true);
                            } else {//其他设备
                                mBluetoothGatt.setCharacteristicNotification(controlCharacteristic, true);
                            }
                        }
                    }
                }

            }
        }

        //当读取设备时会回调该函数
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
        }

        //当向设备Descriptor中写数据时，会回调该函数
        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            switch (getBleCmdType()) {
                case Constant.BLE_READ_DEVICE_INFO:
                    byte[] newValue = characteristic.getValue();
                    printData(newValue);
                    break;
                case Constant.BLE_OPEN_RF:
                    newValue = characteristic.getValue();
                    printData(newValue);
                    break;
                case Constant.BLE_CLOSE_RF: //关闭
                    newValue = characteristic.getValue();
                    printData(newValue);
                    break;
                case Constant.GET_DATA://取数据
                    newValue = characteristic.getValue();
                    printData(newValue);
                    break;
                case Constant.BLE_SET_MODE_ONE://设置查车模式
                    newValue = characteristic.getValue();
                    printData(newValue);
                    break;
                case Constant.BLE_SET_MODE_TWO://设置发卡模式
                    newValue = characteristic.getValue();
                    printData(newValue);
                    break;
            }
        }


        public byte[] collectData(byte[] value) {
            if (isFirstPackage) {
                valueLength = 3 + value[2];
                isFirstPackage = false;
                valueList.clear(); //填充前先清空集合
            }
            if (value != null) {
                for (byte b : value) {
                    valueList.add(b);
                }
            }
            if (valueList.size() < valueLength) {
                return null;
            }
            value = new byte[valueList.size()];
            for (int i = 0; i < value.length; i++) {
                value[i] = valueList.get(i);
            }
            return value;
        }


        List<Byte> sixByteValueList = new ArrayList<>();
        int sixByteValueLength;


        public byte[] collectSixByteData(byte[] value) {
            sixByteValueLength = 3 + value[2];
            if (sixByteValueList.size() >= sixByteValueLength) sixByteValueList.clear();
            for (byte b : value) {
                sixByteValueList.add(b);
            }
            value = new byte[sixByteValueList.size()];

            for (int i = 0; i < value.length; i++) {
                value[i] = sixByteValueList.get(i);
            }
            return value;
        }

        //设备发出通知时会调用到该接口
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            switch (getBleCmdType()) {
                case Constant.BLE_READ_DEVICE_INFO:  //读设备信息
                    ////////////////////数据分包处理//////////////////////////////////////////////////////
                    byte[] newValue = characteristic.getValue();
                    printData(newValue);
                    if (newValue != null && newValue.length == 1) {
                        closeBLE();
                        return;
                    }
                    if (newValue != null && newValue.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((newValue[0] & 0xff) == 0x0A && (newValue[1] & 0xff) == 0x22 && (newValue[2] & 0xff) == 0x04
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x01 && (newValue[5] & 0xff) == 0x03 && (newValue[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }
                    byte[] value = collectData(newValue);

                    if (value == null) {
                        return;
                    }

                    //////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    data[0] = value[0];
                    int answer = data[0] & 0xff;

                    if (answer == 0x0B) {  //Transponder found
                        data[1] = value[1];  //地址
                        data[2] = value[2];  //长度
                        data[3] = value[3];  //状态位
                        int start = 4;
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功

                            int beepFlag = 0;
                            int valueIndex = 4;
                            byte[] filterWindow = new byte[4];
                            for (int i = 0; i < 8; i++) { //再读掉8个字节
                                data[start++] = value[valueIndex++];
                                if (i == 4) { //获取蜂鸣器开关值
                                    beepFlag = data[start - 1];
                                }
                                if (i == 6) {
                                    filterWindow[0] = data[start - 1];
                                }

                                if (i == 7) {
                                    filterWindow[1] = data[start - 1];
                                }

                            }
                            //获取产品序列号

                            byte[] device_id = new byte[15];

                            for (int i = 0; i < 15; i++) {
                                device_id[i] = value[valueIndex++];
                                data[start++] = device_id[i];
                            }

                            String device_id_str = new String(device_id);
                            data[start++] = value[valueIndex++];

                            //停留时间
                            byte[] stopTime = new byte[4];
                            for (int i = 0; i < 2; i++) {
                                stopTime[i] = value[valueIndex++];
                                data[start++] = stopTime[i];
                            }

                            data[start++] = value[valueIndex++];
                            //获取dhcp开关
                            int dhcpFlag = data[start - 1] & 0xff;

                            for (int i = 0; i < 14; i++) {
                                data[start++] = value[valueIndex++];
                            }
                            //lan ip
                            byte[] lanIpB = new byte[4];
                            lanIpB[0] = data[31];
                            lanIpB[1] = data[32];
                            lanIpB[2] = data[33];
                            lanIpB[3] = data[34];
                            String lanIpStr = "";
                            for (int i = 0; i < 4; i++) {
                                lanIpStr += String.valueOf(lanIpB[i] & 0xff);
                                if (i != 3) {
                                    lanIpStr += ".";
                                }
                            }

                            //lan子网掩码
                            byte[] lanZwymB = new byte[4];
                            lanZwymB[0] = data[35];
                            lanZwymB[1] = data[36];
                            lanZwymB[2] = data[37];
                            lanZwymB[3] = data[38];
                            String lanZwymStr = "";
                            for (int i = 0; i < 4; i++) {
                                lanZwymStr += String.valueOf(lanZwymB[i] & 0xff);
                                if (i != 3) {
                                    lanZwymStr += ".";
                                }
                            }
                            //lan 网关
                            byte[] lanWgB = new byte[4];
                            lanWgB[0] = data[39];
                            lanWgB[1] = data[40];
                            lanWgB[2] = data[41];
                            lanWgB[3] = data[42];
                            String lanWgStr = "";
                            for (int i = 0; i < 4; i++) {
                                lanWgStr += String.valueOf(lanWgB[i] & 0xff);
                                if (i != 3) {
                                    lanWgStr += ".";
                                }
                            }

                            byte[] lanPortB = new byte[4];
                            lanPortB[0] = data[43];
                            lanPortB[1] = data[44];
                            String lanPortStr = String.valueOf(Tools.byte2int(lanPortB));

                            //                            valueIndex--;
                            //获取ip地址
                            byte[] ip = new byte[32];
                            for (int i = 0; i < 32; i++) {
                                ip[i] = value[valueIndex++];
                                data[start++] = ip[i];
                            }
                            String ipStr = new String(ip);
                            //获取端口
                            byte[] port = new byte[4];
                            for (int i = 0; i < 2; i++) {
                                port[i] = value[valueIndex++];
                                data[start++] = port[i];
                            }

                            String portStr = String.valueOf(Tools.byte2int(port));

                            byte[] deviceRssi = new byte[4];
                            byte[] deviceGrain = new byte[4];
                            int deviceRssiIndex = 0, deviceGrainIndex = 0;
                            int netState = 0;
                            int drctFlag = 0;
                            int drctTimeNum = 0;
                            for (int i = 0; i < 107; i++) { //107
                                data[start++] = value[valueIndex++];

                                if (i >= 93 && i <= 96) {
                                    deviceRssi[deviceRssiIndex] = data[start - 1];
                                    deviceRssiIndex++;
                                }

                                if (i >= 97 && i <= 100) {
                                    deviceGrain[deviceGrainIndex] = data[start - 1];
                                    deviceGrainIndex++;
                                }
                                if (i == 102) {
                                    netState = data[start - 1] & 0xff;
                                }
                                if (i == 103) {
                                    drctFlag = data[start - 1] & 0xff;
                                }

                                if (i == 104) {
                                    drctTimeNum = data[start - 1] & 0xff;
                                }
                            }

                            //获取lan服务器ip地址
                            byte[] lanFwIpB = new byte[32];
                            for (int i = 0; i < 32; i++) {
                                lanFwIpB[i] = data[79 + i];
                            }
                            String lanFwIpStr = new String(lanFwIpB);

                            byte[] lanFwPortB = new byte[4];
                            lanFwPortB[0] = data[111];
                            lanFwPortB[1] = data[112];
                            String lanFwPortStr = String.valueOf(Tools.byte2int(lanFwPortB));

                            int[] deviceRssis = new int[4];
                            String[] deviceGrainStrs = new String[4];

                            //获取rssi
                            for (int i = 0; i < 4; i++) {
                                deviceRssis[i] = (int) deviceRssi[i];
                            }

                            //获取增域
                            for (int i = 0; i < 4; i++) {
                                deviceGrainStrs[i] = String.valueOf(deviceGrain[i]);
                            }
                            String versionStr = "";
                            if (((value[2] & 0xff) == 0xBA) || ((value[2] & 0xff) == 0xCE)) { //获取版本号
                                byte[] version = new byte[2];
                                for (int i = 0; i < 2; i++) {
                                    version[i] = value[valueIndex++];
                                    versionStr += String.valueOf(version[i]);
                                    if (i == 0) {
                                        versionStr += ".";
                                    }
                                }
                            }
                            String timeStr = null;
                            if ((value[2] & 0xff) == 0xCE) {
                                byte[] time = new byte[20];
                                for (int i = 0; i < 20; i++) {
                                    if (value.length > valueIndex) {
                                        time[i] = value[valueIndex++];
                                    } else {
                                        continue;
                                    }
                                }
                                timeStr = new String(time);
                            }
                            int filterNum = Tools.byte2int(filterWindow);
                            int stopTimeNum = Tools.byte2int(stopTime);
                            mScanListener.scanned(new Transponder(device_id_str, ipStr, portStr, data, versionStr, timeStr, deviceGrainStrs, deviceRssis, beepFlag, dhcpFlag, drctFlag, filterNum, netState, lanIpStr,
                                    lanPortStr, lanZwymStr, lanWgStr, lanFwIpStr, lanFwPortStr, stopTimeNum, drctTimeNum));
                            return;
                        }
                    }
                    mScanListener.scanned(null);
                    break;
                case Constant.READ_TRIAD_PARAM:
                    //读取三元组信息
                    newValue = characteristic.getValue();
                    Log.e("xy", "读取三元组 ：" + Tools.bytesToHexString1(newValue));
                    value = collectData(newValue);
                    if (value == null) {
                        return;
                    }
                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            byte[] mProductKeyByte = new byte[16];
                            byte[] mDeviceNameByte = new byte[32];
                            byte[] mDeviceSecretByte = new byte[40];
                            for (int i = 0; i < 16; i++) {
                                mProductKeyByte[i] = value[i + 4];
                            }
                            for (int i = 0; i < 32; i++) {
                                mDeviceNameByte[i] = value[i + 20];
                            }
                            for (int i = 0; i < 40; i++) {
                                mDeviceSecretByte[i] = value[i + 52];
                            }
                            TriadInfo triadInfo = new TriadInfo();
                            String mProductKey = new String(mProductKeyByte);
                            String mDeviceName = new String(mDeviceNameByte);
                            String mDeviceSecret = new String(mDeviceSecretByte);
                            triadInfo.setProductKey(mProductKey);
                            triadInfo.setDeviceName(mDeviceName);
                            triadInfo.setDeviceSecret(mDeviceSecret);
                            Message message = Message.obtain();
                            message.obj = triadInfo;
                            message.what = READ_TRIAD_PARAM_OK;
                            mHandler.sendMessage(message);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(READ_TRIAD_PARAM_FAIL);
                    break;

                case Constant.SET_TRIAD_PARAM:
                    //设置三元组信息
                    newValue = characteristic.getValue();
                    Log.e("xy", "设置三元组 ：" + Tools.bytesToHexString1(newValue));
                    //0B 68 02 00 8B    0x00 (操作成功, 失败是 0x80)
                    if (newValue != null && newValue.length == 5) {
                        if ((newValue[0] & 0xff) == 0x0B && (newValue[1] & 0xff) == 0x68 && (newValue[2] & 0xff) == 0x02
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x8B) {
                            Message message = Message.obtain();
                            message.obj = "三元组参数设置成功";
                            message.what = SET_TRIAD_PARAM_OK;
                            mHandler.sendMessage(message);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(SET_TRIAD_PARAM_FAIL);
                    break;

                case Constant.BLE_READ_IMEI:
                    newValue = characteristic.getValue();
                    if (newValue != null && newValue.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((newValue[0] & 0xff) == 0x0A && (newValue[1] & 0xff) == 0x22 && (newValue[2] & 0xff) == 0x04
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x01 && (newValue[5] & 0xff) == 0x03 && (newValue[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }
                    value = collectData(newValue);

                    if (value == null) {
                        return;
                    }

                    if ((value[0] & 0xff) == 0x0B) {

                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            byte[] imei = new byte[16];
                            for (int i = 0; i < 16; i++) {
                                imei[i] = value[i + 4];
                            }
                            String imei_id_str = new String(imei);
                            imei_id_str = imei_id_str.substring(0, imei_id_str.length() - 1);
                            if (!TextUtils.isEmpty(imei_id_str)) {
                                Message message = Message.obtain();
                                message.obj = imei_id_str;
                                message.what = MSG_READ_IMEI_OK;
                                mHandler.sendMessage(message);
                                return;
                            }
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_READ_IMEI_FAIL);

                    break;


                case Constant.BLE_READ_FACTORY_NUM:
                    newValue = characteristic.getValue();

                    if (newValue != null && newValue.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((newValue[0] & 0xff) == 0x0A && (newValue[1] & 0xff) == 0x22 && (newValue[2] & 0xff) == 0x04
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x01 && (newValue[5] & 0xff) == 0x03 && (newValue[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }
                    value = collectData(newValue);

                    if (value == null) {
                        return;
                    }

                    if ((value[0] & 0xff) == 0x0B) {

                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            byte[] fact_num = new byte[16];
                            for (int i = 0; i < 16; i++) {
                                fact_num[i] = value[i + 4];
                            }
                            String fact_num_str = new String(fact_num);
                            fact_num_str = fact_num_str.substring(0, fact_num_str.length() - 1);
                            if (!TextUtils.isEmpty(fact_num_str)) {
                                Message message = Message.obtain();
                                message.obj = fact_num_str;
                                message.what = MSG_READ_FACNUM_OK;
                                mHandler.sendMessage(message);
                                return;
                            }
                        }
                    }

                    mHandler.sendEmptyMessage(MSG_READ_FACUNM_FAIL);
                    break;


                case Constant.BLE_READ_GPRS_NUM:
                    value = characteristic.getValue(); //接收数据少于20个字节，无需分包接收数据


                    if (value != null && value.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((value[0] & 0xff) == 0x0A && (value[1] & 0xff) == 0x22 && (value[2] & 0xff) == 0x04
                                && (value[3] & 0xff) == 0x00 && (value[4] & 0xff) == 0x01 && (value[5] & 0xff) == 0x03 && (value[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }


                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[1] & 0xff) == 0x0C) {
                            if ((value[2] & 0xff) == 0x03 && (value[3] & 0xff) == 0x00) { //操作成功
                                int gprsNum = value[4] & 0xff;
                                Message message = Message.obtain();
                                message.what = MSG_READ_GPRS_OK;
                                message.obj = gprsNum;
                                mHandler.sendMessage(message);
                                return;
                            }
                        }

                    }
                    mHandler.sendEmptyMessage(MSG_READ_GPRS_FAIL);
                    break;


                case Constant.BLE_READ_SYS_TIME:
                    value = characteristic.getValue();

                    if (value != null && value.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((value[0] & 0xff) == 0x0A && (value[1] & 0xff) == 0x22 && (value[2] & 0xff) == 0x04
                                && (value[3] & 0xff) == 0x00 && (value[4] & 0xff) == 0x01 && (value[5] & 0xff) == 0x03 && (value[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }


                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功

                            byte[] sysTimeB = new byte[6];
                            for (int i = 0; i < 6; i++) {
                                sysTimeB[i] = value[4 + i];
                            }

                            int year = 2000 + sysTimeB[0];
                            int month = sysTimeB[1];
                            int day = sysTimeB[2];
                            int hour = sysTimeB[3];
                            int minute = sysTimeB[4];
                            int second = sysTimeB[5];

                            String preMonth = "";
                            String preDay = "";
                            String preHour = "";
                            String preMinute = "";
                            String preSecond = "";
                            if (String.valueOf(month).length() < 2) {
                                preMonth = "0";
                            }
                            if (String.valueOf(day).length() < 2) {
                                preDay = "0";
                            }
                            if (String.valueOf(hour).length() < 2) {
                                preHour = "0";
                            }
                            if (String.valueOf(minute).length() < 2) {
                                preMinute = "0";
                            }
                            if (String.valueOf(second).length() < 2) {
                                preSecond = "0";
                            }

                            String time = year + "." + preMonth + month + "." + preDay + day + "," + preHour + hour + ":" + preMinute + minute + ":" + preSecond + second;

                            Message message = Message.obtain();
                            message.obj = time;
                            message.what = MSG_READ_SYSTIME_OK;
                            mHandler.sendMessage(message);
                            return;

                        }
                    }

                    mHandler.sendEmptyMessage(MSG_READ_SYSTIME_FAIL);
                    break;


                case Constant.BLE_SET_SYS_TIME:
                    value = characteristic.getValue();


                    if (value != null && value.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((value[0] & 0xff) == 0x0A && (value[1] & 0xff) == 0x22 && (value[2] & 0xff) == 0x04
                                && (value[3] & 0xff) == 0x00 && (value[4] & 0xff) == 0x01 && (value[5] & 0xff) == 0x03 && (value[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }


                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            mHandler.sendEmptyMessage(MSG_SET_SYSTIME_OK);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_SET_SYSTIME_FAIL);
                    break;


                case Constant.BLE_SET_PARAMS:
                    value = characteristic.getValue();

                    if (value != null && value.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((value[0] & 0xff) == 0x0A && (value[1] & 0xff) == 0x22 && (value[2] & 0xff) == 0x04
                                && (value[3] & 0xff) == 0x00 && (value[4] & 0xff) == 0x01 && (value[5] & 0xff) == 0x03 && (value[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }

                    bleCmdType = Constant.BLE_DO_NONE;


                    if ((value[0] & 0xff) == 0x0B && (value[1] & 0xff) == 0x13 && (value[2] & 0xff) == 0x02 && (value[3] & 0xff) == 0x00) {  //Transponder found
                        mHandler.sendEmptyMessage(MSG_SET_PARAMS_OK);
                        closeBLE();
                    } else {
                        mHandler.sendEmptyMessage(MSG_SET_PARAMS_FAIL);
                    }
                    break;


                case Constant.BLE_DEBUG_TX:

                    newValue = characteristic.getValue();

                    if (newValue != null && newValue.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((newValue[0] & 0xff) == 0x0A && (newValue[1] & 0xff) == 0x22 && (newValue[2] & 0xff) == 0x04
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x01 && (newValue[5] & 0xff) == 0x03 && (newValue[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }


                    value = collectData(newValue);

                    if (value == null) {
                        return;
                    }


                    if ((value[0] & 0xff) == 0x0B) {

                        if ((value[3] & 0xff) == 0x00) {//返回成功
                            byte[] tagid = new byte[4];
                            byte[] totalNumB = new byte[2];
                            int pos = value[4];
                            int rssi = value[14];

                            for (int i = 0; i < 4; i++) {
                                tagid[i] = value[6 + i];
                            }


                            int totalNum = 0;
                            if ((value[2] & 0xff) == 0x15) {

                                totalNumB[0] = value[21];
                                totalNumB[1] = value[22];

                                String totalNumStr = Tools.bytesToHexString2(totalNumB);
                                totalNum = Integer.parseInt(totalNumStr, 16);
                                //totalNum = Tools.byte2int(totalNumB);

                            } else if ((value[2] & 0xff) == 0x13) {
                                totalNum = -1;
                            }


                            String tadidStr = Tools.bytesToHexString2(tagid);
                            Tag tag = new Tag(tadidStr.toUpperCase(), rssi, pos, totalNum);
                            Message message = Message.obtain();
                            message.obj = tag;
                            message.what = MSG_DEBUG_TX_OK;
                            mHandler.sendMessage(message);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_DEBUG_TX_FAIL);
                    break;


                case Constant.BLE_READ_TX_INFO:
                    newValue = characteristic.getValue();

                    if (newValue != null && newValue.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((newValue[0] & 0xff) == 0x0A && (newValue[1] & 0xff) == 0x22 && (newValue[2] & 0xff) == 0x04
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x01 && (newValue[5] & 0xff) == 0x03 && (newValue[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }
                    value = collectData(newValue);

                    if (value == null) {
                        return;
                    }


                    if ((value[0] & 0xff) == 0x0B && (value[1] & 0xff) == 0x28 && (value[2] & 0xff) == 0x12 && (value[3] & 0xff) == 0x00) {

                        byte[] version = new byte[8];
                        byte[] grain = new byte[4];
                        int[] rssi = new int[4];

                        //读取4根天线的版本
                        for (int i = 0; i < 8; i++) {
                            version[i] = value[4 + i];
                        }

                        //读取4根天线的增益
                        for (int i = 0; i < 4; i++) {
                            grain[i] = value[12 + i];
                        }

                        //读取4根天线的信号量
                        for (int i = 0; i < 4; i++) {
                            rssi[i] = value[16 + i];
                        }


                        String[] vStrArray = new String[4];

                        String[] txGrainStrs = new String[4];

                        //转换获取4根天线的版本
                        for (int i = 0; i < 4; i++) {
                            if (((version[2 * i] & 0xff) == 0xFF) && ((version[2 * i + 1] & 0xff) == 0xFF)) {
                                vStrArray[i] = mContext.getResources().getString(R.string.none);
                            } else {
                                vStrArray[i] = String.valueOf(version[2 * i]) + "." + String.valueOf(version[2 * i + 1]);
                            }

                        }


                        //转换获取增域
                        for (int i = 0; i < 4; i++) {
                            if ((grain[i] & 0xff) == 0xFF) {
                                txGrainStrs[i] = mContext.getResources().getString(R.string.none);
                            } else {
                                txGrainStrs[i] = String.valueOf(grain[i]);
                            }

                        }


                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("versionArray", vStrArray);
                        map.put("grainArray", txGrainStrs);
                        map.put("rssiArray", rssi);

                        Message message = Message.obtain();
                        message.obj = map;
                        message.what = MSG_READ_TX_INFO_OK;
                        mHandler.sendMessage(message);
                        return;
                    }
                    mHandler.sendEmptyMessage(MSG_READ_TX_INFO_FAIL);
                    break;
                case Constant.BLE_SCAN_TAG:

                    if (scanFlag) {
                        //需要进行数据的分包处理
                        byte[] newSixByteValue = characteristic.getValue();
                        printData(newSixByteValue);
                        if (newSixByteValue != null && newSixByteValue.length == 1) {
                            closeBLE();
                            return;
                        }
                        //0B 74 03 00 01 7D  剔除
                        if (newSixByteValue != null && newSixByteValue.length == 6) {
                            if ((newSixByteValue[0] & 0xff) == 0x0B && (newSixByteValue[1] & 0xff) == 0x74 && (newSixByteValue[2] & 0xff) == 0x03
                                    && (newSixByteValue[3] & 0xff) == 0x00 && (newSixByteValue[4] & 0xff) == 0x01 && (newSixByteValue[5] & 0xff) == 0x7D) {
                                return;
                            }
                        }
                        byte[] valueOfSixByte = collectData(newSixByteValue);

                        if (valueOfSixByte == null) {
                            return;
                        }
                        data[0] = valueOfSixByte[0];
                        int answerOfSixByte = data[0] & 0xff;
                        if (answerOfSixByte == 0x0B && valueOfSixByte.length > 20) {  // 帧头
                            data[1] = valueOfSixByte[1];  //地址
                            data[2] = valueOfSixByte[2];  //长度
                            data[3] = valueOfSixByte[3];  //状态位
                            if ((valueOfSixByte[3] & 0xff) == 0x00) { //状态位，表示成功
                                int start = 6;
                                int valueIndex = 6;
                                int tagType = 0; //天线
                                //id是6个字节的
                                byte[] tagId = new byte[6];
                                int tagRssi = 0;
                                //温度
                                byte[] sixByteTemp = new byte[2];
                                for (int i = 0; i < 16; i++) {
                                    //天线
                                    if (i == 0) {
                                        tagType = valueOfSixByte[start - 1];
                                    }
                                    data[start++] = valueOfSixByte[valueIndex++];
                                    //Id
                                    if (i == 1) {
                                        tagId[0] = data[start - 1];
                                    }
                                    if (i == 2) {
                                        tagId[1] = data[start - 1];
                                    }
                                    if (i == 3) {
                                        tagId[2] = data[start - 1];
                                    }
                                    if (i == 4) {
                                        tagId[3] = data[start - 1];
                                    }
                                    if (i == 5) {
                                        tagId[4] = data[start - 1];
                                    }
                                    if (i == 6) {
                                        tagId[5] = data[start - 1];
                                    }
                                    //温度
                                    if (i == 11) {
                                        sixByteTemp[0] = data[start - 1];
                                    }
                                    if (i == 12) {
                                        sixByteTemp[1] = data[start - 1];
                                    }
                                    //rssi
                                    if (i == 15) {
                                        tagRssi = data[start - 1];
                                    }
                                }
                                String tagIds = Tools.bytesToHexString2(tagId);

                                String tempText = Tools.bytesToHexString2(sixByteTemp);

                                int tempInt = SixByteParsingTool.get10HexNum(tempText);
                                double temp = (double) tempInt / 100;

                                tagRssi = 0x0FFFFFF00 | tagRssi;
                                Tag tag = new Tag();
                                tag.setTagId(tagIds.toUpperCase());
                                tag.setRssi(tagRssi);
                                tag.setPosition(tagType);
                                tag.setTime(System.currentTimeMillis());
                                tag.setTemp(temp);

                                Message message = Message.obtain();
                                message.obj = tag;
                                message.what = MSG_READ_TAG_OK;
                                mHandler.sendMessage(message);
                                return;
                            }
                            mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                        }


//                        newValue = characteristic.getValue();
//                        value = searchTagCollectData(newValue);
//                        if (value == null) {
//                            return;
//                        }
//
//                        if (((value[0] & 0xff) == 0x02) && ((value[27] & 0xff) == 0x0D) && ((value[28] & 0xff) == 0x0A)) {
//
//                            byte data[] = new byte[26];
//                            for (int i = 0; i < 26; i++) {
//                                data[i] = value[1 + i];
//                            }
//                            if (data != null && data.length > 4) {
//                                byte[] lenB = new byte[4];
//                                byte[] addr = new byte[4]; //通道
//                                addr[0] = data[0];
//                                addr[1] = data[1];
//
//                                lenB[0] = data[2];
//                                lenB[1] = data[3];
//
//
//                                int dataLen = Tools.bytesAscii2int(lenB);
//                                int pos = Tools.bytesAscii2int(addr); //通道  1（东）,2（南）,3（西）,4（北）
//
//                                if (data.length == dataLen * 2 + 6) {
//                                    byte[] realData = new byte[dataLen * 2];
//                                    int realDataIndex = 0;
//                                    for (int i = 4; i < data.length - 2; i++) {
//                                        realData[realDataIndex++] = data[i];
//                                    }
//                                    byte[] t = new byte[2];
//                                    int[] realDataInt = new int[dataLen];
//                                    int k = 0;
//                                    for (int i = 0; i < (dataLen * 2); ) {
//                                        t[0] = realData[i];
//                                        t[1] = realData[i + 1];
//                                        i += 2;
//                                        realDataInt[k++] = Tools.bytesAscii2int(t);
//                                    }
//
//                                    String tagIdStr = "";
//
//                                    for (int i = 1; i < 5; i++) {
//                                        String str = Integer.toHexString(realDataInt[i]);
//                                        if (str != null && str.length() < 2) {
//                                            str = "0" + str;
//                                        }
//                                        tagIdStr += str;
//
//                                    }
//
//                                    int[] warn = new int[1];
//                                    warn[0] = realDataInt[8];
//                                    int rssi = realDataInt[9];
//
//                                    rssi = 0x0FFFFFF00 | rssi;
//                                    Tag tag = new Tag();
//                                    tag.setTagId(tagIdStr.toUpperCase());
//                                    tag.setRssi(rssi);
//                                    tag.setPosition(pos);
//                                    tag.setTime(System.currentTimeMillis());
//
//                                    Message message = Message.obtain();
//                                    message.obj = tag;
//                                    message.what = MSG_READ_TAG_OK;
//                                    mHandler.sendMessage(message);
//                                    return;
//                                }
//                            }
//                        }

                    }

                    break;
                case Constant.BLE_SCAN_TAG_TEMP: //温度的信息
                    if (scanFlag) {
                        //需要进行数据的分包处理
                        byte[] newSixByteValue = characteristic.getValue();
                        printData(newSixByteValue);
                        if (newSixByteValue != null && newSixByteValue.length == 1) {
                            closeBLE();
                            return;
                        }
                        //0B 74 03 00 01 7D  剔除
                        if (newSixByteValue != null && newSixByteValue.length == 6) {
                            if ((newSixByteValue[0] & 0xff) == 0x0B && (newSixByteValue[1] & 0xff) == 0x74 && (newSixByteValue[2] & 0xff) == 0x03
                                    && (newSixByteValue[3] & 0xff) == 0x00 && (newSixByteValue[4] & 0xff) == 0x01 && (newSixByteValue[5] & 0xff) == 0x7D) {
                                return;
                            }
                        }
                        byte[] valueOfSixByte = collectData(newSixByteValue);

                        if (valueOfSixByte == null) {
                            return;
                        }
                        data[0] = valueOfSixByte[0];
                        int answerOfSixByte = data[0] & 0xff;
                        if (answerOfSixByte == 0x0B && valueOfSixByte.length > 20) {  // 帧头
                            data[1] = valueOfSixByte[1];  //地址
                            data[2] = valueOfSixByte[2];  //长度
                            data[3] = valueOfSixByte[3];  //状态位
                            if ((valueOfSixByte[3] & 0xff) == 0x00) { //状态位，表示成功
                                int start = 6;
                                int valueIndex = 6;
                                int tagType = 0; //天线
                                //id是6个字节的
                                byte[] tagId = new byte[6];
                                int tagRssi = 0;
                                //温度
                                byte[] sixByteTemp = new byte[2];
                                for (int i = 0; i < 16; i++) {
                                    //天线
                                    if (i == 0) {
                                        tagType = valueOfSixByte[start - 1];
                                    }
                                    data[start++] = valueOfSixByte[valueIndex++];
                                    //Id
                                    if (i == 1) {
                                        tagId[0] = data[start - 1];
                                    }
                                    if (i == 2) {
                                        tagId[1] = data[start - 1];
                                    }
                                    if (i == 3) {
                                        tagId[2] = data[start - 1];
                                    }
                                    if (i == 4) {
                                        tagId[3] = data[start - 1];
                                    }
                                    if (i == 5) {
                                        tagId[4] = data[start - 1];
                                    }
                                    if (i == 6) {
                                        tagId[5] = data[start - 1];
                                    }
                                    //温度
                                    if (i == 11) {
                                        sixByteTemp[0] = data[start - 1];
                                    }
                                    if (i == 12) {
                                        sixByteTemp[1] = data[start - 1];
                                    }
                                    //rssi
                                    if (i == 15) {
                                        tagRssi = data[start - 1];
                                    }
                                }

                                String tagIds = Tools.bytesToHexString2(tagId);

                                String tempText = Tools.bytesToHexString2(sixByteTemp);

                                int tempInt = SixByteParsingTool.get10HexNum(tempText);
                                double temp = (double) tempInt / 100;

                                tagRssi = 0x0FFFFFF00 | tagRssi;
                                Tag tag = new Tag();
                                tag.setTagId(tagIds.toUpperCase());
                                tag.setRssi(tagRssi);
                                tag.setPosition(tagType);
                                tag.setTime(System.currentTimeMillis());
                                tag.setTemp(temp);

                                Message message = Message.obtain();
                                message.obj = tag;
                                message.what = MSG_READ_TAG_OK;
                                mHandler.sendMessage(message);
                                return;
                            }
                        }
                        mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
//                        newValue = characteristic.getValue();
//                        value = searchTagCollectData(newValue);
//                        if (value == null) {
//                            return;
//                        }
//                        printData(value);
//                        if (((value[0] & 0xff) == 0x02) && ((value[27] & 0xff) == 0x0D) && ((value[28] & 0xff) == 0x0A)) {
//
//                            byte data[] = new byte[26];
//                            for (int i = 0; i < 26; i++) {
//                                data[i] = value[1 + i];
//                            }
//
//                            if (data != null && data.length > 4) {
//                                byte[] lenB = new byte[4]; //长度
//                                byte[] addr = new byte[4]; //通道
//
//                                addr[0] = data[1];
//                                addr[1] = data[2];
//
//                                lenB[0] = data[2];
//                                lenB[1] = data[3];
//
//                                int dataLen = Tools.bytesAscii2int(lenB);
//                                System.out.println("dataLen:" + dataLen);
//
//                                int pos = Tools.bytesAscii2int(addr); //通道  1（东）,2（南）,3（西）,4（北）
//
//                                if (data.length == dataLen * 2 + 6) {
//
//                                    byte[] realData = new byte[dataLen * 2];
//                                    int realDataIndex = 0;
//
//                                    for (int i = 4; i < data.length - 2; i++) {
//                                        realData[realDataIndex++] = data[i];
//                                    }
//                                    byte[] t = new byte[2];
//                                    int[] realDataInt = new int[dataLen];
//                                    int k = 0;
//                                    for (int i = 0; i < (dataLen * 2); ) {
//                                        t[0] = realData[i];
//                                        t[1] = realData[i + 1];
//                                        i += 2;
//                                        realDataInt[k++] = Tools.bytesAscii2int(t);
//                                    }
//
//                                    String realDataText = ArrayTool.toString(realDataInt);
//                                    System.out.println("realDataInt:" + realDataText);
//                                    String tagIdStr = "";
//                                    for (int i = 1; i < 5; i++) {
//                                        String str = Integer.toHexString(realDataInt[i]);
//                                        if (str != null && str.length() < 2) {
//                                            str = "0" + str;
//                                        }
//                                        tagIdStr += str;
//                                    }
//                                    System.out.println("标签Id:" + tagIdStr);
//
//                                    int temp1 = realDataInt[6];//温度小数部分
//                                    int temp2 = realDataInt[7];//温度整数部分
//
//                                    System.out.println("温度整数:" + temp2 + "<---温度小数：" + temp1);
//
//                                    String temp = "";
//                                    if (temp1 == 0) {
//                                        temp = temp2 + "";
//                                    } else {
//                                        temp = temp2 + "." + temp1;
//                                    }
//                                    System.out.println("#########温度:" + temp);
//
//                                    int[] warn = new int[1];
//                                    warn[0] = realDataInt[8];
//                                    int rssi = realDataInt[9];
//
//                                    rssi = 0x0FFFFFF00 | rssi;
//                                    Tag tag = new Tag();
//                                    tag.setTagId(tagIdStr.toUpperCase());
//                                    tag.setRssi(rssi);
//                                    tag.setPosition(pos);
//                                    tag.setTime(System.currentTimeMillis());
//                                    tag.setTemp(temp);
//
//                                    Message message = Message.obtain();
//                                    message.obj = tag;
//                                    message.what = MSG_READ_TAG_OK;
//                                    mHandler.sendMessage(message);
//                                    return;
//                                }
//                            }
//                        }
//                        mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                    }
                    break;

                case Constant.BLE_SCAN_TAG_HUA_WEI:

                    if (scanFlag) {
                        newValue = characteristic.getValue();
                        System.out.print("###########");
                        for (int i = 0; i < newValue.length; i++) {
                            System.out.print(newValue[i] + " ");
                        }
                        System.out.println("");
                        value = collectData(newValue);
                        if (value == null) {
                            return;
                        }

                        if (((value[0] & 0xff) == 0x02) && ((value[27] & 0xff) == 0x0D) && ((value[28] & 0xff) == 0x0A)) {

                            byte data[] = new byte[26];
                            for (int i = 0; i < 26; i++) {
                                data[i] = value[1 + i];
                            }
                            if (data != null && data.length > 4) {
                                byte[] lenB = new byte[4];
                                byte[] addr = new byte[4]; //通道
                                addr[0] = data[0];
                                addr[1] = data[1];

                                lenB[0] = data[2];
                                lenB[1] = data[3];


                                int dataLen = Tools.bytesAscii2int(lenB);
                                int pos = Tools.bytesAscii2int(addr); //通道  1（东）,2（南）,3（西）,4（北）

                                if (data.length == dataLen * 2 + 6) {
                                    byte[] realData = new byte[dataLen * 2];
                                    int realDataIndex = 0;
                                    for (int i = 4; i < data.length - 2; i++) {
                                        realData[realDataIndex++] = data[i];
                                    }
                                    byte[] t = new byte[2];
                                    int[] realDataInt = new int[dataLen];
                                    int k = 0;
                                    for (int i = 0; i < (dataLen * 2); ) {
                                        t[0] = realData[i];
                                        t[1] = realData[i + 1];
                                        i += 2;
                                        realDataInt[k++] = Tools.bytesAscii2int(t);
                                    }

                                    String tagIdStr = "";
                                    String tagIdStr1 = "";

                                    for (int i = 1; i < 5; i++) {
                                        String str1 = Integer.toHexString(realDataInt[i]);
                                        str1 = surelength(str1, 2);
                                        tagIdStr1 += str1;

                                        //转成华为格式
                                        String str = Integer.toBinaryString(realDataInt[i]);
                                        //不够8位前面补0
                                        str = surelength(str, 8);
                                        tagIdStr += str;

                                    }

                                    CLog.e("LogInterceptor 二进制", tagIdStr);
                                    CLog.e("LogInterceptor 十六进制", tagIdStr1);
                                    String tagTypeString = tagIdStr.substring(0, 6);
                                    String mString = tagIdStr.substring(6, 11);
                                    String batchString = tagIdStr.substring(11, 22);
                                    String itemString = tagIdStr.substring(22, 32);

                                    //二进制转十进制
                                    String tagType = Tools.toD(tagTypeString, 2);
                                    //不足2位前面补0
                                    tagType = surelength(tagType, 2);

                                    String m = Tools.toD(mString, 2);
                                    m = surelength(m, 2);

                                    String batch = Tools.toD(batchString, 2);
                                    batch = surelength(batch, 4);

                                    String item = Tools.toD(itemString, 2);
                                    item = surelength(item, 4);

                                    tagIdStr = tagType + m + batch + item;
                                    CLog.e("LogInterceptor 十进制", tagIdStr);

                                    int[] warn = new int[1];
                                    warn[0] = realDataInt[8];
                                    int rssi = realDataInt[9];


                                    rssi = 0x0FFFFFF00 | rssi;
                                    Tag tag = new Tag();
                                    tag.setTagId(tagIdStr.toUpperCase());
                                    tag.setRssi(rssi);
                                    tag.setPosition(pos);

                                    Message message = Message.obtain();
                                    message.obj = tag;
                                    message.what = MSG_READ_TAG_OK;
                                    mHandler.sendMessage(message);
                                    return;
                                }
                            }
                        }

                        mHandler.sendEmptyMessage(MSG_READ_TAG_FAIL);
                    }

                    break;


                case Constant.BLE_READ_GPRS_CONNECT_STATE:
                    newValue = characteristic.getValue();

                    if (newValue != null && newValue.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((newValue[0] & 0xff) == 0x0A && (newValue[1] & 0xff) == 0x22 && (newValue[2] & 0xff) == 0x04
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x01 && (newValue[5] & 0xff) == 0x03 && (newValue[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }
                    value = collectData(newValue);

                    if (value == null) {
                        return;
                    }

                    if ((value[0] & 0xff) == 0x0B && (value[1] & 0xff) == 0x29 && (value[2] & 0xff) == 0x25 && (value[3] & 0xff) == 0x00) {//success

                        byte[] gprsCntIp = new byte[32];
                        byte[] gprsCntPort = new byte[4];
                        int gprsCntIpIndex = 0, gprsCntPortIndex = 0, gprsCntState = 0;
                        for (int i = 0; i < 35; i++) {
                            if (i < 32) {
                                gprsCntIp[gprsCntIpIndex++] = value[4 + i];
                            } else if (i >= 32 && i < 34) {
                                gprsCntPort[gprsCntPortIndex++] = value[4 + i];
                            } else if (i == 34) {
                                gprsCntState = value[4 + i] & 0xff;
                            } else {

                            }
                        }


                        String gprsCntIpStr = new String(gprsCntIp);
                        String gprsCntPortStr = String.valueOf(Tools.byte2int(gprsCntPort));
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("gprsCntIp", gprsCntIpStr);
                        map.put("gprsCntPort", gprsCntPortStr);
                        map.put("gprsCntState", gprsCntState);

                        Message message = Message.obtain();
                        message.what = MSG_READ_GPRS_CONNECT_STATE_OK;
                        message.obj = map;
                        mHandler.sendMessage(message);

                    } else {
                        mHandler.sendEmptyMessage(MSG_READ_GPRS_CONNECT_STATE_FAIL);
                    }

                    break;


                case Constant.BLE_READ_LAN_CONNECT_STATE:
                    newValue = characteristic.getValue();

                    if (newValue != null && newValue.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((newValue[0] & 0xff) == 0x0A && (newValue[1] & 0xff) == 0x22 && (newValue[2] & 0xff) == 0x04
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x01 && (newValue[5] & 0xff) == 0x03 && (newValue[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }
                    value = collectData(newValue);

                    if (value == null) {
                        return;
                    }


                    if ((value[0] & 0xff) == 0x0B && (value[1] & 0xff) == 0x2B) {//success

                        if ((value[2] & 0xff) == 0x25 && (value[3] & 0xff) == 0x00) { //opt success
                            byte[] lanCntIp = new byte[32];
                            byte[] lanCntPort = new byte[4];
                            int lanCntIpIndex = 0, lanCntPortIndex = 0, lanCntState = 0;
                            for (int i = 0; i < 35; i++) {
                                if (i < 32) {
                                    lanCntIp[lanCntIpIndex++] = value[4 + i];
                                } else if (i >= 32 && i < 34) {
                                    lanCntPort[lanCntPortIndex++] = value[4 + i];
                                } else if (i == 34) {
                                    lanCntState = value[4 + i] & 0xff;
                                } else {
                                }
                            }


                            String gprsCntIpStr = new String(lanCntIp);
                            String gprsCntPortStr = String.valueOf(Tools.byte2int(lanCntPort));
                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("lanCntIp", gprsCntIpStr);
                            map.put("lanCntPort", gprsCntPortStr);
                            map.put("lanCntState", lanCntState);

                            Message message = Message.obtain();
                            message.what = MSG_READ_LAN_CONNECT_STATE_OK;
                            message.obj = map;
                            mHandler.sendMessage(message);


                        } else if ((value[2] & 0xff) == 0x02 && (value[3] & 0xff) == 0x80) { //设备不支持lan功能
                            mHandler.sendEmptyMessage(MSG_DEVICE_UNSUPPORT_LAN);
                        } else {
                            mHandler.sendEmptyMessage(MSG_READ_LAN_CONNECT_STATE_FAIL);
                        }
                    } else {
                        mHandler.sendEmptyMessage(MSG_READ_LAN_CONNECT_STATE_FAIL);
                    }
                    break;


                case Constant.BLE_READ_CACHE_TAG_NUM:
                    value = characteristic.getValue();
                    System.out.print("###########");
                    for (int i = 0; i < value.length; i++) {
                        System.out.print(value[i] + " ");
                    }
                    System.out.println("");
                    if (value != null && value.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((value[0] & 0xff) == 0x0A && (value[1] & 0xff) == 0x22 && (value[2] & 0xff) == 0x04
                                && (value[3] & 0xff) == 0x00 && (value[4] & 0xff) == 0x01 && (value[5] & 0xff) == 0x03 && (value[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }

                    bleCmdType = Constant.BLE_DO_NONE;

                    if ((value[0] & 0xff) == 0x0B && (value[1] & 0xff) == 0x25 && (value[2] & 0xff) == 0x04 && (value[3] & 0xff) == 0x00) {  //Transponder found
                        byte[] data = new byte[2];
                        data[0] = value[4];
                        data[1] = value[5];
                        String cacheTagNumStr = Tools.bytesToHexString2(data);
                        int cacheTagNum = Integer.parseInt(cacheTagNumStr, 16);
                        Message message = Message.obtain();
                        message.obj = cacheTagNum;
                        message.arg1 = 200;
                        message.what = MSG_READ_CACHE_TAG_NUM_OK;
                        mHandler.sendMessage(message);
                    } else if ((value[0] & 0xff) == 0x0B && (value[1] & 0xff) == 0x25 && (value[2] & 0xff) == 0x08 && (value[3] & 0xff) == 0x00) {

                        String[] cacheTagNumStr = new String[3];
                        int[] cacheTagNum = new int[3];
                        byte[] data = new byte[2];
                        int dataIndex = 0;
                        for (int i = 0, j = 0; i < 6; i++) {
                            data[dataIndex++] = value[4 + i];
                            if (dataIndex == 2) {
                                dataIndex = 0;
                                cacheTagNumStr[j] = Tools.bytesToHexString2(data);
                                cacheTagNum[j] = Integer.parseInt(cacheTagNumStr[j], 16);
                                j++;
                            }
                        }

                        // int cacheTagNum[3] = Integer.parseInt(cacheTagNumStr, 16);
                        Message message = Message.obtain();
                        message.obj = cacheTagNum;
                        message.arg1 = 500;
                        message.what = MSG_READ_CACHE_TAG_NUM_OK;
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendEmptyMessage(MSG_READ_CACHE_TAG_NUM_FAIL);
                    }

                    break;
                case Constant.BLE_SEND_CARRIER_TEST://发送载波测试
                    newValue = characteristic.getValue();

                    if (newValue != null && newValue.length == 7) { //由于设备每隔3秒会发送一条无关数据到这里，先做丢包处理
                        if ((newValue[0] & 0xff) == 0x0A && (newValue[1] & 0xff) == 0x22 && (newValue[2] & 0xff) == 0x04
                                && (newValue[3] & 0xff) == 0x00 && (newValue[4] & 0xff) == 0x01 && (newValue[5] & 0xff) == 0x03 && (newValue[6] & 0xff) == 0xCC) {
                            return;
                        }
                    }
                    value = collectData(newValue);

                    if (value == null) {
                        return;
                    }

                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            mHandler.sendEmptyMessage(MSG_CARRIER_TEST_OK);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_CARRIER_TEST_FIAL);
                    break;
                case Constant.GET_DATA://取数据
                    newValue = characteristic.getValue();
                    value = collectData(newValue);

                    if (value == null) {
                        return;
                    }
                    printData(value);
                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功

                            byte b = Tools.calCheck(value);
                            if (b == value[value.length - 1]) {//校验通过
                                if (value[4] != 0) {//读到标签数据
                                    byte data[] = new byte[4 * value[4]];
                                    for (int i = 0; i < data.length; i++) {
                                        data[i] = value[i + 5];
                                    }
                                    List<Tag> list = new ArrayList<>();
                                    for (int i = 0; i < value[4]; i++) {
                                        String tagId = "";
                                        for (int j = i * 4; j < i * 4 + 4; j++) {
                                            String str = Integer.toHexString(data[j] & 0xff);
                                            if (str != null && str.length() < 2) {
                                                str = "0" + str;
                                            }
                                            tagId += str;
                                        }
                                        Tag tag = new Tag();
                                        tag.setTagId(tagId.toUpperCase());
                                        list.add(tag);
                                    }

                                    Message message = Message.obtain();
                                    message.obj = list;
                                    message.what = MSG_GET_DATA_OK;
                                    mHandler.sendMessage(message);

                                } else {//未读到标签数据
                                    mHandler.sendEmptyMessage(MSG_GET_DATA_NONE);
                                }

                            }
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_GET_DATA_FAIL);
                    break;
                case Constant.BLE_OPEN_RF:
                    value = characteristic.getValue();
                    //                    value = collectData(newValue);
                    if (value == null) {
                        return;
                    }
                    printData(value);
                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            mHandler.sendEmptyMessage(MSG_OPEN_RF_OK);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_OPEN_RF_FAIL);
                    break;
                case Constant.BLE_CLOSE_RF:
                    value = characteristic.getValue();
                    //                    value = collectData(newValue);
                    if (value == null) {
                        return;
                    }
                    printData(value);
                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            mHandler.sendEmptyMessage(MSG_CLOSE_RF_OK);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_CLOSE_RF_FAIL);
                    break;
                case Constant.BLE_SET_MODE_ONE://设置1档
                    value = characteristic.getValue();
                    //                    value = collectData(newValue);
                    if (value == null) {
                        return;
                    }
                    printData(value);
                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            mHandler.sendEmptyMessage(MSG_SET_MODE_OK);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_SET_MODE_FAIL);

                    break;
                case Constant.BLE_SET_MODE_TWO://设置2档
                    value = characteristic.getValue();
                    //                    value = collectData(newValue);
                    if (value == null) {
                        return;
                    }
                    printData(value);

                    if ((value[0] & 0xff) == 0x0B) {
                        if ((value[3] & 0xff) == 0x00) { //状态位，表示成功
                            mHandler.sendEmptyMessage(MSG_SET_MODE_OK);
                            return;
                        }
                    }
                    mHandler.sendEmptyMessage(MSG_SET_MODE_FAIL);
                    break;
            }

        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                     int status) {
            super.onDescriptorRead(gatt, descriptor, status);

        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor,
                                      int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);

        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);

        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);

        }
    };

    private String surelength(String batchString, int length) {
        String s = batchString;
        if (batchString != null && batchString.length() < length) {
            for (int j = 0; j < length - batchString.length(); j++) {
                s = "0" + s;
            }
        }
        return s;
    }


    /**
     * 4个字节搜索判断
     *
     * @return
     */
    private byte[] searchTagCollectData(byte[] value) {
        if (value == null) {
            return null;
        }
        String valueString = Tools.bytesToHexString1(value);
        System.out.println("valueString:" + valueString);
        if (!mHasStart) {//没有包头,判断是否有包头
            pakageData(valueString);
            return null;
        } else {//有包头,判断是否有包尾
            if (flag_end_0d) {//有包头,且上一个包以0d结尾 ,判断是否以0a开头
                if (valueString.startsWith("0a")) {
                    mTotalPakageString = mPakageStartString + "0a";//拼接包头包尾数据
                    if (valueString.indexOf("0a") < (valueString.length() - 2)) {//这包0a之后还有数据
                        String lastString = valueString.substring(valueString.indexOf("0a") + 2, valueString.length());//保存剩余的数据
                        //判断剩余的数据是否包含包头
                        pakageData(lastString);
                    } else {//这包包尾之后没有数据了
                        mHasStart = false;
                        mPakageStartString = "";
                    }
                    if (mTotalPakageString.length() == 58) {//完整包的长度是否58,是返回完整包,不是抛掉
                        byte[] totalByte = Tools.hexStringToByteArray(mTotalPakageString);
                        return totalByte;
                    }
                    return null;
                } else {//上一个包以0d结尾,这包不以0a开头,丢掉之前数据,置空,重走本方法
                    mHasStart = false;
                    flag_end_0d = false;
                    mPakageStartString = "";
                    mTotalPakageString = "";
                    byte[] totalByte = Tools.hexStringToByteArray(mTotalPakageString);
                    searchTagCollectData(totalByte);
                    return null;
                }
            } else {//有包头,且上一个包不以0d结尾
                mHasEnd = hasPakageEnd(valueString);
                if (mHasEnd) {//有包尾
                    mTotalPakageString = mPakageStartString + valueString.substring(0, valueString.indexOf("0d0a") + 4);//拼接包头包尾数据
                    if (valueString.indexOf("0d0a") < (valueString.length() - 4)) {//这包包尾之后还有数据
                        String lastString = valueString.substring(valueString.indexOf("0d0a") + 4, valueString.length());//保存剩余的数据
                        //判断剩余的数据是否包含包头
                        pakageData(lastString);
                    } else {//这包包尾之后没有数据了
                        mHasStart = false;
                        mPakageStartString = "";
                    }
                    if (mTotalPakageString.length() == 58) {//完整包的长度是否29,是返回完整包,不是抛掉
                        byte[] totalByte = Tools.hexStringToByteArray(mTotalPakageString);
                        return totalByte;
                    }
                    return null;
                } else {//没有包尾
                    //没有包尾两分种情况 1. 没有0d0a  2. 以0d结尾
                    mPakageStartString = mPakageStartString + valueString;
                    if (valueString.endsWith("0d")) {
                        flag_end_0d = true;
                    } else {
                        flag_end_0d = false;
                    }
                    return null;
                }
            }

        }
    }

    /**
     * 处理包头数据
     *
     * @param valueString
     */
    private void pakageData(String valueString) {
        mHasStart = hasPakageStart(valueString);
        if (mHasStart) {//有包头,截取包头之后的数据保留,没有包头,抛掉
            //从包头截取之后的字符串
            mPakageStartString = valueString.substring(valueString.indexOf("02"), valueString.length());
        } else {
            mPakageStartString = "";
        }
    }

    /**
     * 判断是否有包头
     *
     * @param strArr
     * @return
     */
    private boolean hasPakageStart(String strArr) {
        return strArr.contains("02");
    }


    /**
     * 判断是否有包尾
     *
     * @param strArr
     * @return
     */
    private boolean hasPakageEnd(String strArr) {
        return strArr.contains("0d0a");
    }


    private void printData(byte[] newValue) {
        String s = Tools.bytesToHexString1(newValue);
        System.out.println("接收到的byte数据：" + s);
    }
}
