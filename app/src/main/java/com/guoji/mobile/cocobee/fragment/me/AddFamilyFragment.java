package com.guoji.mobile.cocobee.fragment.me;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bql.baseadapter.recycleView.BH;
import com.bql.baseadapter.recycleView.QuickRcvAdapter;
import com.bql.utils.CheckUtils;
import com.bql.utils.EventManager;
import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.MyRelationAdapter;
import com.guoji.mobile.cocobee.callback.DialogCallback;
import com.guoji.mobile.cocobee.common.AppConstants;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.fragment.base.BaseFragment;
import com.guoji.mobile.cocobee.model.User;
import com.guoji.mobile.cocobee.response.HomeRecResponse;
import com.guoji.mobile.cocobee.response.RelationResponse;
import com.guoji.mobile.cocobee.utils.Utils;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Response;

/**
 * Created by Administrator on 2017/6/29.
 */

public class AddFamilyFragment extends BaseFragment {

    private static HomeRecResponse homeRecResponse;
    private static String familyRole;
    @BindView(R.id.tv_relation)
    EditText mTvRelation;
    @BindView(R.id.tv_select)
    TextView mTvSelect;
    @BindView(R.id.tv_phone_num)
    EditText mTvPhoneNum;
    @BindView(R.id.tv_address_list)
    TextView mTvAddressList;
    @BindView(R.id.tv_add_now)
    TextView mTvAddNow;
    private User mUserLoginInfo;

    public static AddFamilyFragment getInstance(HomeRecResponse homeRecResponse, String familyRole) {
        AddFamilyFragment.homeRecResponse = homeRecResponse;
        AddFamilyFragment.familyRole = familyRole;
        AddFamilyFragment fragment = new AddFamilyFragment();
        return fragment;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        mUserLoginInfo = Utils.getUserLoginInfo();
        initView();
    }

    private void initView() {
        mTvRelation.setText(familyRole);
    }

    @Override
    protected void initToolbarHere() {
        initToolbarWithLeftText("邀请家人", "返回");
    }

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.fragment_add_family;
    }

    @OnClick({R.id.tv_select, R.id.tv_address_list, R.id.tv_add_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_select://选择关系
                getToMyRelation();
                break;
            case R.id.tv_address_list://通讯录
                checkPomision();
                break;
            case R.id.tv_add_now://立即邀请加入
                String relation = mTvRelation.getText().toString().trim();
                String tvPhoneNum = mTvPhoneNum.getText().toString().trim();
                String phone = tvPhoneNum.replaceAll(" ", "");
                if (CheckUtils.isEmpty(relation)) {
                    XToastUtils.showShortToast("请输入与监护人的关系");
                } else if (CheckUtils.isEmpty(tvPhoneNum)) {
                    XToastUtils.showShortToast("请输入监护人的手机号码");
                } else if (!CheckUtils.isMobilePhone(phone)) {
                    XToastUtils.showShortToast("请输入正确的手机号");
                } else {
                    addNow(relation, phone);
                }
                break;
        }
    }

    private void addNow(String relation, String phoneNum) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", phoneNum);
        map.put("user_mobile", mUserLoginInfo.getMobile());
        map.put("remarkrelation", relation);
        map.put("peid", homeRecResponse.getPeid());
        map.put("pname", homeRecResponse.getPname());

        OkGo.post(Path.ADD_FAMILY).tag(this).params(map).execute(new DialogCallback<Object>(_mActivity, "邀请中...") {

            @Override
            public void onSuccess(Object o, Call call, Response response) {
                EventBus.getDefault().post(new EventManager(AppConstants.YAO_QING_FAMILY_SUCCESS));
                XToastUtils.showShortToast("邀请成功");
                pop();
            }

            @Override
            public void onError(Call call, Response response, Exception e) {
                super.onError(call, response, e);
                if (CheckUtils.equalsString(e.getMessage(), "300")) {
                    EventBus.getDefault().post(new EventManager(AppConstants.WAIT_YAO_QING_FAMILY));
                    pop();
                }
            }
        });
    }

    private void checkPomision() {
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(_mActivity, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                //申请权限  第二个参数是一个 数组 说明可以同时申请多个权限
//            ActivityCompat.requestPermissions(_mActivity, new String[]{android.Manifest.permission.READ_CONTACTS}, 1);//Activity申请
                requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS}, 1);//fragment申请
            } else {//已授权
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
            }
//        } else {
//            startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
//        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //权限申请成功
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), 0);
            } else {
                XToastUtils.showShortToast("联系人权限申请失败");
            }
        }
    }

    private void getToMyRelation() {

    }

    //弹选择框
    private void initDialog(List<RelationResponse> relationResponses) {
        View view = View.inflate(_mActivity, R.layout.label_list, null);
        final AlertDialog alertDialog = Utils.showCornerDialog(_mActivity, view, 270, 270);
        RecyclerView listView = (RecyclerView) alertDialog.findViewById(R.id.listView);
        TextView tvTitleCarLabel = (TextView) alertDialog.findViewById(R.id.tv_title_car_label);
        tvTitleCarLabel.setText("选择与被监护人关系");
        tvTitleCarLabel.setTextColor(getResources().getColor(R.color.color_3270ed));
        MyRelationAdapter adapter = new MyRelationAdapter(_mActivity, relationResponses);
        listView.setAdapter(adapter);
        listView.addItemDecoration(new ListLineDecoration());
        listView.setLayoutManager(new LinearLayoutManager(_mActivity));
        adapter.setOnItemClickListener(new QuickRcvAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(BH viewHolder, int position) {
                alertDialog.dismiss();
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (0): {
                if (resultCode == Activity.RESULT_OK) {

                    Uri contactData = data.getData();

                    Cursor c = _mActivity.managedQuery(contactData, null, null, null, null);

                    c.moveToFirst();

                    String phoneNum = getContactPhone(c);
                    mTvPhoneNum.setText(phoneNum);
                }

                break;

            }

        }
    }

    //获取联系人电话
    private String getContactPhone(Cursor cursor) {
        if (!cursor.moveToFirst()) {
            return "";
        }
        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String phoneResult = "";
        //System.out.print(phoneNum);
        if (phoneNum > 0) {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人的电话号码的cursor;
            Cursor phones = _mActivity.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            //int phoneCount = phones.getCount();
            //allPhoneNum = new ArrayList<String>(phoneCount);
            if (phones.moveToFirst()) {
                // 遍历所有的电话号码
                for (; !phones.isAfterLast(); phones.moveToNext()) {
                    int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phone_type = phones.getInt(typeindex);
                    String phoneNumber = phones.getString(index);
                    switch (phone_type) {
                        case 2:
                            phoneResult = phoneNumber;
                            break;
                    }
                    //allPhoneNum.add(phoneNumber);
                }
                if (!phones.isClosed()) {
                    phones.close();
                }
            }
        }
        return phoneResult;
    }
}
