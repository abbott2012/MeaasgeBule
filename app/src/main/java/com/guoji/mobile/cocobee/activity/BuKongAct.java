package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.RecordAdapter;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.TagRecord;
import com.guoji.mobile.cocobee.utils.XToastUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * 搜索标签布控数据页面
 * Created by _H_JY on 16/11/1.
 */
public class BuKongAct extends BaseAct implements View.OnClickListener, AdapterView.OnItemClickListener {

    private Context context;
    private ImageButton back_ib;
    private EditText tag_id_et;
    private Button add_btn, sure_btn, clear_btn;
    private RecordAdapter adapter;
    private ListView mListView;
    private ImageView clear_iv;
    private List<TagRecord> records = new ArrayList<>();
    private final String RECORD_FILE_NAME = "/input_record_file.txt";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_bukong);
        context = this;

        records.clear();
        List<TagRecord> tagRecords = (List<TagRecord>) getIntent().getSerializableExtra("netWorkTags"); //从后台获取的数据
        if (tagRecords != null && tagRecords.size() > 0) {
            for (int i = 0; i < tagRecords.size(); i++) {
                records.add(tagRecords.get(i));
            }
        }

        initViewWithData();
    }

    private void initViewWithData() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        tag_id_et = (EditText) findViewById(R.id.tag_id_et);
        add_btn = (Button) findViewById(R.id.add_btn);
        sure_btn = (Button) findViewById(R.id.sure_btn);
        mListView = (ListView) findViewById(R.id.tag_record_lv);
        clear_btn = (Button) findViewById(R.id.clean_btn);
        clear_iv = (ImageView) findViewById(R.id.clear_iv);

        back_ib.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        sure_btn.setOnClickListener(this);
        clear_btn.setOnClickListener(this);
        clear_iv.setOnClickListener(this);

        adapter = new RecordAdapter(context, records);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(this);

        initBTRecordData(); //获取本地记录数据

        if (records != null && records.size() > 0) {
            clear_btn.setVisibility(View.VISIBLE);
        } else {
            clear_btn.setVisibility(View.GONE);
        }

        tag_id_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence text, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(text)) {
                    clear_iv.setVisibility(View.VISIBLE);
                } else {
                    clear_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                finish();
                break;

            case R.id.add_btn:
                //添加
                add();
                break;

            case R.id.sure_btn:
                //确定
                sure();
                break;
            case R.id.clean_btn:
                clean();
                break;
            case R.id.clear_iv:
                tag_id_et.setText("");
                break;
        }
    }


    private void sure() {
        /*String tagId = tag_id_et.getText().toString().trim();



        if (TextUtils.isEmpty(tagId) && records.size() == 0) {
            Toast.makeText(context,"请输入标签号",Toast.LENGTH_SHORT).show();
            return;
        }

        if(tagId.length() < 8 && records.size() == 0){
            Toast.makeText(context,"标签长度为8个字符",Toast.LENGTH_SHORT).show();
            return;
        }*/

        if (records.size() == 0) {
            XToastUtils.showShortToast(getString(R.string.not_add_data));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra("bkList", (Serializable) records);
        setResult(2444, intent);
        finish();
    }


    private void clean() {
        SweetAlertDialog sd = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        sd.setTitleText(getString(R.string.notice));
        sd.setContentText(getString(R.string.if_you_sure_clear));
        sd.showCancelButton(true).setCancelText(getString(R.string.cancel));
        sd.setConfirmText(getString(R.string.confirm));
        sd.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                if (records != null && records.size() > 0) { //清除内存中的数据
                    records.clear();
                    adapter.notifyDataSetChanged();
                    clear_btn.setVisibility(View.GONE);
                }
                File file = new File(Path.RECORD_FILE_PATH + RECORD_FILE_NAME); //删掉记录文件
                if (file.exists()) {
                    file.delete();
                }
                if (!file.exists()) {
                    XToastUtils.showShortToast(getString(R.string.has_clear));
                }
                sweetAlertDialog.dismissWithAnimation();
            }
        }).show();
    }


    private void initBTRecordData() { //获取蓝牙记录数据，数据保存在本地.txt文件,并且保存在内存records集合中
        File file = new File(Path.RECORD_FILE_PATH + RECORD_FILE_NAME);
        StringBuffer sb = new StringBuffer();
        String line = "";

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            //BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"gbk"));
            try {
                while ((line = br.readLine()) != null && !line.trim().equals("")) {
                    sb.append(line);
                    String[] temp = line.split(",");
                    TagRecord tagRecord = new TagRecord(temp[0], temp[1]);
                    boolean flag = false;
                    for (int i = 0; i < records.size(); i++) {
                        if (tagRecord.getLno().equals(records.get(i).getLno())) {
                            flag = true;
                        }
                    }

                    if (flag == false) {
                        tagRecord.setIsLocalRecord(true); //本地记录的标识
                        records.add(tagRecord);
                    }

                }
                adapter.notifyDataSetChanged();

                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }


    private void add() {

        String tagId = tag_id_et.getText().toString().trim();

        if (TextUtils.isEmpty(tagId)) {
            XToastUtils.showShortToast(getString(R.string.input_tag));
            return;
        }

        if (tagId.length() < 12) {
            XToastUtils.showShortToast(getString(R.string.tag_length));
            return;
        }


        if (records != null && records.size() > 20) {
            XToastUtils.showShortToast(getString(R.string.bukong_length));
            return;
        }

        tagId = tagId.toUpperCase();

        /*添加数据到记录集合中*/
        boolean flag = false;
        for (int i = 0; i < records.size(); i++) {
            if (tagId.equals(records.get(i).getLno())) {
                flag = true;
            }
        }

        if (flag == false) { //记录中没有该数据，添加
            TagRecord tagRecord = new TagRecord(tagId);
            tagRecord.setIsLocalRecord(true);
            records.add(tagRecord);

            adapter.notifyDataSetChanged();
            if (records != null && records.size() > 0) {
                clear_btn.setVisibility(View.VISIBLE);
            }
            File file = new File(Path.RECORD_FILE_PATH);
            try {
                if (!file.exists()) {
                    file.mkdirs();
                }
                File recordFile = new File(Path.RECORD_FILE_PATH + RECORD_FILE_NAME);
                if (!recordFile.exists()) {
                    recordFile.createNewFile();
                }
                FileWriter fw = new FileWriter(recordFile, true);

                fw.write(tagRecord.getLno() + "," + tagRecord.getDate() + "\r\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(new String[]{getString(R.string.check), getString(R.string.delete)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which) {
                    case 0:
                        TagRecord tagRecord = records.get(position);
                        if (tagRecord.isLocalRecord()) { //本地记录
                            XToastUtils.showShortToast(getString(R.string.no_detail_info));
                        } else {
                            startActivity(new Intent(context, TagDetailAct.class).putExtra("tagRecord", tagRecord));
                        }

                        break;

                    case 1:
                        records.remove(position);
                        /*删除旧文件，重新建文件，把新内容写进去*/
                        File fileRecord = new File(Path.RECORD_FILE_PATH + RECORD_FILE_NAME);
                        // if (fileRecord.exists()) {
                        //    fileRecord.delete();
                        // }
                        FileWriter fw = null;
                        try {
                            File file = new File(Path.RECORD_FILE_PATH);
                            if (!file.exists()) {
                                file.mkdirs();
                            }
                            if (!fileRecord.exists()) {
                                fileRecord.createNewFile();
                            }

                            fw = new FileWriter(fileRecord, false); //重新写入内容，而不是以追加的方式
                            if (records != null && records.size() > 0) {
                                for (int i = 0; i < records.size(); i++) {
                                    TagRecord tag = records.get(i);
                                    if (tag.isLocalRecord()) { //如果是本地记录，才写进本地文件
                                        fw.write(tag.getLno() + "," + tag.getDate() + "\r\n");
                                    }
                                }

                            }

                            adapter.notifyDataSetChanged();
                            XToastUtils.showShortToast(getString(R.string.delete_success));

                            if (records == null || records.size() == 0) {
                                clear_btn.setVisibility(View.GONE);
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (fw != null) {
                                    fw.close();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                        break;

                }


            }
        });
        builder.create().show();

    }


}
