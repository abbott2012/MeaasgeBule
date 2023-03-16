package com.guoji.mobile.cocobee.common;

public class AppConstants {

    /**
     * 用户登录信息
     */
    public static final String USER_LOGIN_INFO = "user_login_info";

    /**
     * 用户输入登录账号
     */
    public static final String USER_LOGIN_NUM = "user_login_num";
    /**
     * share preference文件名
     */
    public static final String PREFERS_CONFIG = "prefers_electr_config";
    /**
     * 用户是否第一次进入应用
     */
    public static final String IS_USER_FIRST = "is_user_first";

    /**
     * 用户选择的语言
     */

    public final static String LANGUAGE_ID = "languageId";



    /**
     * Glide缓存大小
     */
    public static final int DISK_MAX_CACHE_SIZE = 80 * 1024 * 1024;//100MB

    //标签绑定成功
    public static final int LABEL_BIND_SUCCESS = 10000 * 3;
    //车辆信息录入成功
    public static final int CAR_INFO_UPDATE_SUCCESS = 10000 * 4;
    //购买保险成功
    public static final int BUY_POLICY_SUCCESS = 10000 * 5;
    //头像上传成功
    public static final int AVAR_PIC_UPLOAD_SUCCESS = 11000 * 6;

    //微信支付成功
    public static final int WX_PAY_SUCCESS = 11000 * 7;
    //微信支付取消
    public static final int WX_PAY_CANCEL = 11000 * 8;
    //支付完成
    public static final int PAY_FINISH = 11000 * 9;


    //邀请家人完成
    public static final int YAO_QING_FAMILY_SUCCESS = 11000 * 10;
    //修改关系成功
    public static final int CHANG_ROLE_SUCCESS = 11000 * 11;
    //删除监护人成功
    public static final int DELETE_ROLE_SUCCESS = 11000 * 12;
    //登录成功
    public static final int LOGIN_SUCCESS = 11000 * 13;
    //退出登录成功
    public static final int OUT_LOGIN_SUCCESS = 11000 * 15;
    //邀请家人中
    public static final int WAIT_YAO_QING_FAMILY = 11000 * 14;
    //个人信息录入成功
    public static final int INFO_PUT_SUCCESS = 11000 * 16;

    //用户报警成功
    public static final int ALERM_SUCCESS = 11000 * 17;

    //组织机构设置成功
    public static final int ORG_SET_SUCCESS = 11000 * 18;

    //targitId 38 为车 39为人
    public static final String TYPE_PEPOLE = "39";
    public static final String TYPE_CAR = "38";

    //卡的类型
    public static final String TYPE_CARD_ANXIN = "33";//安心卡
    public static final String TYPE_CARD_TIYAN = "40";//体验卡
    public static final String TYPE_CARD_DINZHI = "35";//定制卡
    //线上线下卡
    public static final String TYPE_CARD_ONLINE = "0";//线上卡
    public static final String TYPE_CARD_OFFLINE = "1";//线下卡

    //扫描标签
    public static final int SCAN_LABEL_REQUEST_CODE = 1666;//请求码
    public static final int SCAN_LABEL_RESULT_CODE = 1667;//结果码


}
