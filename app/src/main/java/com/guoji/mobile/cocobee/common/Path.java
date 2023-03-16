package com.guoji.mobile.cocobee.common;

import android.os.Environment;


import java.io.File;

/**
 * @author _H_JY
 *         2016-5-26下午2:53:04
 */
public class Path {

    /*项目总文件夹路径*/
    public static final String PROJECT_FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "com.hsjc.electricar" + File.separator;

    /*存放记录信息的文件夹路径*/
    public static final String RECORD_FILE_PATH = PROJECT_FILE_PATH + "record" + File.separator;

    /*用于存放临时图片的文件夹路径*/
    public static final String IMAGE_TEMP_FILE_PATH = PROJECT_FILE_PATH + "temp_image" + File.separator;

    /***********************************
     * 接口路径
     **************************************************/

    public static final String SERVER_BASIC_PATH = "http://www.intrace.cn/electrocarAPP/";  //云服务器地址
    public static final String IMG_BASIC_PATH = "http://image.intrace.cn/"; //图片云地址前缀

//    public static final String SERVER_BASIC_PATH = "http://218.17.157.214:8988/electrocarAPP/"; //测试服务器地址
//    public static final String IMG_BASIC_PATH = "http://electrocar.oss-cn-hangzhou.aliyuncs.com/"; //图片测试地址前缀

//    public static final String SERVER_BASIC_PATH = "http://218.17.157.214:8989/electrocarAPP/"; //测试服务器地址
//    public static final String IMG_BASIC_PATH = "http://electrocar.oss-cn-hangzhou.aliyuncs.com/"; //图片测试地址前缀

//    public static final String SERVER_BASIC_PATH = "http://218.17.157.214:5677/electrocarAPP/"; //测试服务器地址
//    public static final String IMG_BASIC_PATH = "http://electrocar.oss-cn-hangzhou.aliyuncs.com/"; //图片测试地址前缀

//    public static final String SERVER_BASIC_PATH = "http://test.intrace.cn/electrocarAPP/"; //测试服务器地址
//    public static final String IMG_BASIC_PATH = "http://electrocar.oss-cn-hangzhou.aliyuncs.com/"; //图片测试地址前缀

//    public static final String SERVER_BASIC_PATH = "http://222.87.128.250:8899/electrocarAPP/"; //测试服务器地址
//    public static final String IMG_BASIC_PATH = "http://electrocar.oss-cn-hangzhou.aliyuncs.com/"; //图片测试地址前缀

//    public static final String SERVER_BASIC_PATH = "http://10.10.100.42:4600/electrocarAPP/"; //测试服务器地址
//    public static final String IMG_BASIC_PATH = "http://electrocar.oss-cn-hangzhou.aliyuncs.com/"; //图片测试地址前缀


//    public static final String SERVER_BASIC_PATH = "http://192.168.1.158:8080/electrocarAPP/"; //测试服务器地址
//    public static final String IMG_BASIC_PATH = "http://electrocar.oss-cn-hangzhou.aliyuncs.com/"; //图片测试地址前缀


//    public static final String SERVER_BASIC_PATH = "http://192.168.1.121:8080/electrocarAPP/"; //测试服务器地址
//    public static final String IMG_BASIC_PATH = "http://electrocar.oss-cn-hangzhou.aliyuncs.com/"; //图片测试地址前缀

//    192.168.1.158:8080

    /*登录接口*/
    public static final String LOGIIN_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=1&&app_res=App_Marktrace";//新接口

    public static final String CAR_LOCATION_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=24&&app_res=App_interface";

    //人员位置查询
    public static final String PEOPLE_LOCATION_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=64&&app_res=App_interface";//新接口

    public static final String UPLOAD_DEVICE_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=4&&app_res=App_interface";

    public static final String UPLOAD_CAR_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=5&&app_res=App_interface";

    public static final String PEOPLE_TRACE_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=65&&app_res=App_interface";

    public static final String CAR_TRACE_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=22&&app_res=App_interface";

    public static final String NROMALUSER_ALARM_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=2&&app_res=App_interface";

    public static final String ADMINISTRATOR_ALARM_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=19&&app_res=App_interface";

    //apk下载地址
    public static final String APK_DOWNLOAD_URL = SERVER_BASIC_PATH + "APK/base.apk";

    public static final String APK_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=7&&app_res=App_interface";

    public static final String UPLOAD_CD_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=8&&app_res=App_interface";

    public static final String POS_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=9&&app_res=App_interface";


    public static final String LOCK_CAR_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=10&&app_res=App_interface";

    public static final String UNLOCK_CAR_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=11&&app_res=App_interface";


    public static final String CHANGE_POINT_STATE = SERVER_BASIC_PATH + "servlet/showpage?eventID=14&&app_res=App_interface";


    public static final String CHANGE_SINGLEPOINT_DATA = SERVER_BASIC_PATH + "servlet/showpage?eventID=15&&app_res=App_interface";

    public static final String GET_USABLE_TAGS = SERVER_BASIC_PATH + "servlet/showpage?eventID=16&&app_res=App_interface";

    //选择组织机构
    public static final String GET_POLICE_STATIONS = SERVER_BASIC_PATH + "servlet/showpage?eventID=18&&app_res=App_interface";

    public static final String GET_CNOS = SERVER_BASIC_PATH + "servlet/showpage?eventID=23&&app_res=App_interface";

    public static final String UPLOAD_PEOPLE_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=17&&app_res=App_interface";

    public static final String GET_ALARM_TAG_INFOS = SERVER_BASIC_PATH + "servlet/showpage?eventID=25&&app_res=App_interface";

    public static final String GET_CONVENIENCE_POINTS = SERVER_BASIC_PATH + "servlet/showpage?eventID=27&&app_res=App_interface";

    public static final String UPLOAD_CONVENIENCE_POINT = SERVER_BASIC_PATH + "servlet/showpage?eventID=26&&app_res=App_interface";

    public static final String UPDATE_CONVENIENCE_POINT = SERVER_BASIC_PATH + "servlet/showpage?eventID=28&&app_res=App_interface";

    //管理员查询保单信息
    public static final String GET_ALL_POLICY_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=62&&app_res=App_interface";//新接口

    //普通用户查询保单信息
    public static final String GET_SINGLE_USER_POLICY_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=61&&app_res=App_interface";//新接口

    //验证标签
    public static final String CHECK_TAG_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=17&&app_res=App_Marktrace";

    public static final String NORMAL_USER_UPLOAD_ALARM_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=31&&app_res=App_interface";

    public static final String ADMINISTRATOR_UPLOAD_CAR_ALARM_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=35&&app_res=App_interface";

    public static final String ADMINISTRATOR_UPLOAD_PERSON_ALARM_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=34&&app_res=App_interface";

    public static final String CHANGE_PWD_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=33&&app_res=App_interface";

    public static final String CHECK_GUARDIAN_EXIST_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=36&&app_res=App_interface";

    public static final String EQU_WHETHER_EXIST_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=37&&app_res=App_interface";

    public static final String GET_GUARDIANS_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=38&&app_res=App_interface";

    //保单信息上传
    public static final String UPLOAD_POLICY_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=39&&app_res=App_interface";

    public static final String CHECK_PHONE_EXIST_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=41&&app_res=App_interface";

    public static final String GET_CHECK_CODE_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=40&&app_res=App_interface";

    public static final String FIND_PWD_CHECK_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=42&&app_res=App_interface";

    public static final String RESET_PWD_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=43&&app_res=App_interface";

    public static final String CHECK_WHETHER_BUY_INSURANCE_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=44&&app_res=App_interface";

    //车辆档次
    public static final String CAR_STALL_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=5&&app_res=Pay_interface";

    public static final String HANDLE_ALARM_INFO_PATH = SERVER_BASIC_PATH + "servlet/showpage?eventID=46&&app_res=App_interface";

    //用户注册
    public static final String USER_REGISTER = SERVER_BASIC_PATH + "servlet/showpage?eventID=2&&app_res=App_Marktrace";//新接口

    //查询各级组织架构
    public static final String GET_USER_ORG = SERVER_BASIC_PATH + "servlet/showpage?eventID=53&&app_res=App_interface";

    //车主车辆录入信息上报接口
    public static final String CAR_INFO_PUT_IN = SERVER_BASIC_PATH + "servlet/showpage?eventID=50&&app_res=App_interface";

    //车主标签自主绑定
    public static final String CAR_LABEL_BIND = SERVER_BASIC_PATH + "servlet/showpage?eventID=5&&app_res=App_Marktrace";

    //车辆信息查询
    public static final String CAR_INFO_QUEREY = SERVER_BASIC_PATH + "servlet/showpage?eventID=60&&app_res=App_interface";//新接口

    //获取广告信息
    public static final String GET_AD_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=47&&app_res=App_interface";

    //用户头像上传
    public static final String USER_AVAR_UPLOAD = SERVER_BASIC_PATH + "servlet/showpage?eventID=21&&app_res=App_Marktrace";//新接口

    //生成订单
    public static final String PAY_ORDER_ADD = SERVER_BASIC_PATH + "servlet/showpage?eventID=11&&app_res=Pay_interface";//新接口

    //获取支付信息
    public static final String GET_PAY_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=7&&app_res=Pay_interface";

    //支付成功后查询状态
    public static final String CHECK_PAY_STATE = SERVER_BASIC_PATH + "servlet/showpage?eventID=8&&app_res=Pay_interface";

    //民警用户实时位置上传
    public static final String POLICE_NOW_LOCATION = SERVER_BASIC_PATH + "servlet/showpage?eventID=56&&app_res=App_interface";

    //获取系统消息
    public static final String GET_SYS_MSG = SERVER_BASIC_PATH + "servlet/showpage?eventID=57&&app_res=App_interface";

    //删除系统消息
    public static final String DELETE_SYS_MSG = SERVER_BASIC_PATH + "servlet/showpage?eventID=58&&app_res=App_interface";

    //人员信息查询
    public static final String PERSON_INFO_SEARCH = SERVER_BASIC_PATH + "servlet/showpage?eventID=63&&app_res=App_interface";

    //卡片信息查询
    public static final String GET_CARD_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=9&&app_res=Pay_interface";//卡片信息

    //查询车和人的信息查询
    public static final String GET_CAR_AND_PERSON_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=4&&app_res=App_Marktrace";

    //获取卡档位价格
    public static final String GET_CAR_LABEL_PRICE = SERVER_BASIC_PATH + "servlet/showpage?eventID=10&&app_res=Pay_interface";

    //获取位置
    public static final String GET_LABEL_POSITION = SERVER_BASIC_PATH + "servlet/showpage?eventID=10&&app_res=App_Marktrace";

    //个人信息完善
    public static final String UPDATE_USE_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=3&&app_res=App_Marktrace";

    //人员标签绑定
    public static final String PERSON_UPDATE_USE_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=6&&app_res=App_Marktrace";

    //查询人员类型
    public static final String GET_PERSON_TYPE = SERVER_BASIC_PATH + "servlet/showpage?eventID=13&&app_res=App_Marktrace";

    //上传组织架构
    public static final String UPDATE_USER_ORGID = SERVER_BASIC_PATH + "servlet/showpage?eventID=9&&app_res=App_Marktrace";

    //锁车解锁操作
    public static final String LOCK_UNLOCK_CAR = SERVER_BASIC_PATH + "servlet/showpage?eventID=15&&app_res=App_Marktrace";

    //轨迹查询
    public static final String CAR_PERSON_TRACE = SERVER_BASIC_PATH + "servlet/showpage?eventID=11&&app_res=App_Marktrace";

    //邀请家人
    public static final String ADD_FAMILY = SERVER_BASIC_PATH + "servlet/showpage?eventID=14&&app_res=App_Marktrace";

    //查询所有监护家庭成员
    public static final String CHECK_ALL_FAMILY = SERVER_BASIC_PATH + "servlet/showpage?eventID=18&&app_res=App_Marktrace";

    //修改家庭成员
    public static final String CHANG_FAMILY_PERSON = SERVER_BASIC_PATH + "servlet/showpage?eventID=19&&app_res=App_Marktrace";

    //删除家庭成员
    public static final String DELETE_FAMILY_PERSON = SERVER_BASIC_PATH + "servlet/showpage?eventID=20&&app_res=App_Marktrace";

    //体验卡升级安心卡
    public static final String CAR_TIYAN_UPDATE_ANXIN = SERVER_BASIC_PATH + "servlet/showpage?eventID=22&&app_res=App_Marktrace";

    //查询保单信息
    public static final String GET_POLICY_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=12&&app_res=Pay_interface";

    //定制卡标签校验
    public static final String BIND_LABEL_DINZHI = SERVER_BASIC_PATH + "servlet/showpage?eventID=26&&app_res=App_Marktrace";

    //定制卡人员标签绑定
    public static final String BIND_LABEL_DINZHI_PERSON = SERVER_BASIC_PATH + "servlet/showpage?eventID=27&&app_res=App_Marktrace";

    //定制卡车辆绑定
    public static final String BIND_LABEL_DINZHI_CAR = SERVER_BASIC_PATH + "servlet/showpage?eventID=28&&app_res=App_Marktrace";

    //报警记录信息获取
    public static final String GET_USER_ALARM_INFO = SERVER_BASIC_PATH + "servlet/showpage?eventID=29&&app_res=App_Marktrace";

    //意见反馈
    public static final String USER_IDEA_BACK = SERVER_BASIC_PATH + "servlet/showpage?eventID=30&&app_res=APP_Marktrace";

    //获取类型
    public static final String GET_TYPE = SERVER_BASIC_PATH + "servlet/showpage?eventID=104&&app_res=App_Marktrace";

    //获取用户下面所有组织机构
    public static final String GET_USER_ALL_ORG = SERVER_BASIC_PATH + "servlet/showpage?eventID=18&&app_res=App_interface";

    //用户保存选择的组织机构
    public static final String SAVE_SELECT_ORG = SERVER_BASIC_PATH + "servlet/showpage?eventID=105&&app_res=App_Marktrace";

    //查车模式下载
    public static final String SEARCH_CAR_DOWNLOAD = SERVER_BASIC_PATH + "servlet/showpage?eventID=106&&app_res=App_Marktrace";

}
