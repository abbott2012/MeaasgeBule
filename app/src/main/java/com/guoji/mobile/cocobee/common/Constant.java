package com.guoji.mobile.cocobee.common;

public class Constant {

    // Key names received from the BluetoothHelper Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    public static final int REQUEST_BLUETOOTH_ENABLE = 0;
    public static final int REQUEST_BLUETOOTH_CONNECT = 1;
    public static final int REQUEST_IMAGE = 2;
    // Message types sent from the BluetoothHelper Handler
    public static final int MESSAGE_STATE_CHANGE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Constants that indicate the current connection state

    public static final int STATE_CONNECTE_ERROR = 9;  // now connected error to a remote device
    public static final int STATE_CONNECTE_LOST = 10;  // now connected lost a remote device

    /*图片压缩大小限制*/
    public final static long PIC_SIZE_LIMIT = /*1024 * 1024 * 1*/153600; //150KB

    /*地图所需参数*/
    public final static String LONGITUDE = "longitude";
    public final static String LATITUDE = "latitude";
    public final static String RADIUS = "radius";
    public final static String DIRECTION = "direction";

    public final static int SDCD = 1111;
    public final static int AZTS = 2222;
    public final static int DWGL = 3333;
    public final static int BMFW = 4444;
    public final static int SELECT_TAG_ACT = 1368;
    public final static int SELECT_POLICE_ACT = 1379;
    public final static int SELECT_CNO_ACT = 1380;
    public final static int SELECT_PTYPE_ACT = 1381;
    public final static int SELECT_CAR_STALL_ACT = 1382;
    public final static int AFTER_UPLOAD_DEVICE_INFO_SUCCESS = 1383;
    public final static int AFTER_CHOOSE_LOOK_DEVICE_INFO = 1384;


    public final static String NEED_REFRESH_POINTLIST = "refresh_point_list";
    public final static String NEED_REFRESH_CONVENIENCE_LIST = "refresh_convenience_point_list";


    /*查询对象*/
    public final static int SEARCH_PEOPLE_POSITION = 1250;
    public final static int SEARCH_CAR_POSITION = 1251;
    public final static int SEARCH_PEOPLE_TRACE = 1252;
    public final static int SEARCH_CAR_TRACE = 1253;


    /*BLE指令类型*/
    public static final int BLE_DO_NONE = 7100;
    public static final int BLE_READ_DEVICE_INFO = 7101;
    public static final int BLE_READ_IMEI = 7102;
    public static final int BLE_READ_FACTORY_NUM = 7103;
    public static final int BLE_READ_GPRS_NUM = 7104;
    public static final int BLE_READ_SYS_TIME = 7105;
    public static final int BLE_SET_SYS_TIME = 7106;
    public static final int BLE_SET_PARAMS = 7107;
    public static final int BLE_DEBUG_TX = 7108;
    public static final int BLE_STOP_DEBUG_TX = 7109;
    public static final int BLE_READ_TX_INFO = 7110;
    public static final int OPEN_TAG_DATA_OUTPUT = 7111;
    public static final int BLE_SCAN_TAG = 7112;
    public static final int BLE_FILTER_TAG = 7113;
    public static final int BLE_FILTER_TAG_NUM = 7114;
    public static final int BLE_CLOSE_TAG_DATA_OUTPUT = 7115;
    public static final int BLE_READ_GPRS_CONNECT_STATE = 7116;
    public static final int BLE_READ_LAN_CONNECT_STATE = 7117;
    public static final int BLE_READ_CACHE_TAG_NUM = 7118;
    public static final int BLE_SEND_CARRIER_TEST = 7119;//发送载波测试指令
    public static final int BLE_SCAN_TAG_HUA_WEI = 7121;
    public static final int BLE_SCAN_TAG_TEMP = 7122;

    public static final int READ_TRIAD_PARAM = 7123; //读取三元组信息
    public static final int SET_TRIAD_PARAM = 7124; //设置三元组信息


    /*共享文件及其参数*/
    public final static String SP_CONNECT_STATUS = "bluetooth";
    public final static String CONNECT_STATUS = "status";


    /*短信验证*/
    public final static String SMS_APPKEY = "16b46178daa4b";
    public final static String SMS_APPSECRET = "d5e4ac722b1cb161a45012c0bcb437b6";


    /**
     * 条形码： REQUEST_SCAN_MODE_BARCODE_MODE
     */
    public static final int REQUEST_SCAN_MODE_BARCODE_MODE = 0X100;


    //角色管理
    public final static int POLICE = 1;
    public final static int NORMAL_USER = 2;
    public final static int TECHNICIAN = 3;
    public final static int BUSINESS = 4;
    public final static int ADMINISTRATOR = 5;


    public static final int BLE_OPEN_RF = 7120;
    public static final int GET_DATA = 7130;
    public static final int BLE_CLOSE_RF = 7140;
    public static final int BLE_SET_MODE_ONE = 7150;
    public static final int BLE_SET_MODE_TWO = 7160;
    public static final int BLE_CLOSE_DEVICE = 7170;


    /*语言环境参数*/
    public final static int LANGUAGE_SIMPLE_CHINESE = 1; //简体中文
    public final static int LANGUAGE_ENGLISH = 2; //英文
}
