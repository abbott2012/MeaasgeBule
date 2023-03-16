package com.guoji.mobile.cocobee.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.guoji.mobile.cocobee.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author _H_JY
 *         2016-5-26下午2:53:04
 *         备注：本地Sqlite数据库
 */
public class DbController extends SQLiteOpenHelper {

    private SQLiteDatabase database;
    private static DbController instance = null;


    public DbController(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public static DbController getInstance(Context context) {
        if (instance == null) {
            synchronized (DbController.class) {
                if (instance == null) {
                    instance = new DbController(context, DbInfoConstant.DB_NAME, null, DbInfoConstant.DB_VERSION);
                }
            }
        }
        return instance;
    }

    //在activity启动时打开
    public void openDatabase() {
        try {
            database = this.getWritableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
            database = this.getReadableDatabase();
        }
    }

    //在activity停止时关闭
    public void closeDatabase() {
        if (database != null && database.isOpen())
            database.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {


        /*用户表*/
        String createUserTable = "CREATE TABLE IF NOT EXISTS " + DbInfoConstant.T_USER + "  ( "
                + DbInfoConstant.F_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + DbInfoConstant.F_USERID + " TEXT , "
                + DbInfoConstant.F_USERNAME + " TEXT , "
                + DbInfoConstant.F_PASSWORD + " TEXT , "
                + DbInfoConstant.F_USERTYPE + " INTEGER , "
                + DbInfoConstant.F_PHONE + " TEXT , "
                + DbInfoConstant.F_EMAIL + "  TEXT ,"
                + DbInfoConstant.F_ADDRESS + "  TEXT ,"
                + DbInfoConstant.F_HEADIMG + "  TEXT ,"
                + DbInfoConstant.F_POLICE + "  TEXT ,"
                + DbInfoConstant.F_ORGID + "  TEXT ,"
                + DbInfoConstant.F_GENDER + " TEXT ,"
                + DbInfoConstant.F_STATUS + " TEXT ,"
                + DbInfoConstant.F_IDCARD + " TEXT ,"
                + DbInfoConstant.F_LABLEID + " TEXT ,"
                + DbInfoConstant.F_ORGIDS + " TEXT ,"
                + DbInfoConstant.F_PTYPE + " TEXT ) ";


        db.execSQL(createUserTable); //创建用户表

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            if(oldVersion < DbInfoConstant.DB_VERSION){
                db.execSQL("drop table if exists " + DbInfoConstant.T_USER);
                onCreate(db);
            }


    }


    /*添加新用户*/
    public long addUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DbInfoConstant.F_USERID, user.getPid());
        values.put(DbInfoConstant.F_USERNAME, user.getUsername());
        values.put(DbInfoConstant.F_PASSWORD, user.getPassword());
        values.put(DbInfoConstant.F_USERTYPE,user.getApproleid());
        values.put(DbInfoConstant.F_PHONE, user.getMobile());
        values.put(DbInfoConstant.F_EMAIL, user.getEmail());
        values.put(DbInfoConstant.F_GENDER, user.getSex());
        values.put(DbInfoConstant.F_ADDRESS, user.getAddress());
        values.put(DbInfoConstant.F_HEADIMG, "");
        values.put(DbInfoConstant.F_POLICE,"");
        values.put(DbInfoConstant.F_ORGID,user.getOrgid());
        values.put(DbInfoConstant.F_PTYPE,user.getPtype());
        values.put(DbInfoConstant.F_STATUS,user.getStatus());
        values.put(DbInfoConstant.F_IDCARD,user.getIdcard());
        values.put(DbInfoConstant.F_LABLEID,user.getLabelid());
        values.put(DbInfoConstant.F_ORGIDS,user.getOrgids());
        //返回新插入数据的id值；如果i>0表示插入成功；
        long i = database.insert(DbInfoConstant.T_USER, null, values);
        return i;
    }


    /*更新用户信息*/
    public int updateUser(User user) {
        ContentValues values = new ContentValues();
        values.put(DbInfoConstant.F_USERID, user.getPid());
        values.put(DbInfoConstant.F_USERNAME, user.getUsername());
        values.put(DbInfoConstant.F_PASSWORD, user.getPassword());
        values.put(DbInfoConstant.F_USERTYPE,user.getApproleid());
        values.put(DbInfoConstant.F_PHONE, user.getMobile());
        values.put(DbInfoConstant.F_EMAIL, user.getEmail());
        values.put(DbInfoConstant.F_GENDER, user.getSex());
        values.put(DbInfoConstant.F_ADDRESS, user.getAddress());
        values.put(DbInfoConstant.F_HEADIMG, "");
        values.put(DbInfoConstant.F_POLICE,"");
        values.put(DbInfoConstant.F_ORGID,user.getOrgid());
        values.put(DbInfoConstant.F_PTYPE,user.getPtype());
        values.put(DbInfoConstant.F_STATUS,user.getStatus());
        values.put(DbInfoConstant.F_IDCARD,user.getIdcard());
        values.put(DbInfoConstant.F_LABLEID,user.getLabelid());
        values.put(DbInfoConstant.F_ORGIDS,user.getOrgids());
        int i = database.update(DbInfoConstant.T_USER, values, DbInfoConstant.F_USERID + " = ? ", new String[]{user.getPid()});

        return i;
    }


    /*更新用户头像*/
    public int updateHeadimg(String img, String phone) {
        ContentValues values = new ContentValues();
        values.put(DbInfoConstant.F_HEADIMG, img);
        int i = database.update(DbInfoConstant.T_USER, values, DbInfoConstant.F_PHONE + "  = ? ", new String[]{phone});
        return i;
    }

    /*更新用户名*/
    public int updateUserName(String username, String phone) {
        ContentValues values = new ContentValues();
        values.put(DbInfoConstant.F_USERNAME, username);
        int i = database.update(DbInfoConstant.T_USER, values, DbInfoConstant.F_PHONE + "  = ? ", new String[]{phone});
        return i;
    }

    /*更新邮箱*/
    public int updateEmail(String email, String phone) {
        ContentValues values = new ContentValues();
        values.put(DbInfoConstant.F_EMAIL, email);
        int i = database.update(DbInfoConstant.T_USER, values, DbInfoConstant.F_PHONE + "  = ? ", new String[]{phone});
        return i;
    }


    /*更新地址*/
    public int updateAddress(String address, String phone) {
        ContentValues values = new ContentValues();
        values.put(DbInfoConstant.F_ADDRESS, address);
        int i = database.update(DbInfoConstant.T_USER, values, DbInfoConstant.F_PHONE + "  = ? ", new String[]{phone});
        return i;
    }


    /*更新性别*/
    public int updateGender(String gender, String phone) {
        ContentValues values = new ContentValues();
        values.put(DbInfoConstant.F_GENDER, gender);
        int i = database.update(DbInfoConstant.T_USER, values, DbInfoConstant.F_PHONE + "  = ? ", new String[]{phone});
        return i;
    }


    /*查询所有用户*/
    public List<User> findAllUserInfo() {
        String sql = "select * from " + DbInfoConstant.T_USER;
        Cursor cursor = database.rawQuery(sql, null);

        List<User> userInfos = new ArrayList<User>();
        while (cursor.moveToNext()) {
            String userId = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_USERID));
            String username = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_USERNAME));
            String password = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_PASSWORD));
            Integer userType = cursor.getInt(cursor.getColumnIndex(DbInfoConstant.F_USERTYPE));
            String phone = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_PHONE));
            String email = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_EMAIL));
            String gender = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_GENDER));
            String address = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_ADDRESS));
            String headimg = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_HEADIMG));
            String police = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_POLICE));
            String orgid = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_ORGID));
            String ptype = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_PTYPE));
            String status = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_STATUS));
            String idcard = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_IDCARD));
            String labelid = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_LABLEID));
            String orgids = cursor.getString(cursor.getColumnIndex(DbInfoConstant.F_ORGIDS));
            User user = new User();
            user.setPid(userId);
            user.setUsername(username);
            user.setPassword(password);
            user.setApproleid(userType);
            user.setMobile(phone);
            user.setEmail(email);
            user.setSex(gender);
            user.setAddress(address);
            user.setOrgid(orgid);
            user.setPtype(ptype);
            user.setStatus(status);
            user.setIdcard(idcard);
            user.setLabelid(labelid);
            user.setOrgids(orgids);

            userInfos.add(user);
        }
        return userInfos;
    }


    /*删除所有用户*/
    public int deleteAllUser() {
        //返回删除的数据的数量
        int i = database.delete(DbInfoConstant.T_USER, null, null);
        return i;
    }


}
