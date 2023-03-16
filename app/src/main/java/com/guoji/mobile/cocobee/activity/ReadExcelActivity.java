package com.guoji.mobile.cocobee.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.activity.base.BaseToolbarActivity;
import com.guoji.mobile.cocobee.adapter.ExcelTestAdapter;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.response.ExcelResponse;
import com.guoji.mobile.cocobee.view.ListLineDecoration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ReadExcelActivity extends BaseToolbarActivity {

    @BindView(R.id.tv_read)
    TextView mTvRead;
    @BindView(R.id.tv_out)
    TextView mOut;
    @BindView(R.id.listView)
    RecyclerView mListView;
    private String filePath = Path.RECORD_FILE_PATH + "hello.xls";
    private List<ExcelResponse> list = new ArrayList();
    private ExcelTestAdapter mAdapter;

    @Override
    protected int getContentViewLayoutID() {
        return R.layout.activity_read_excel;
    }

    @Override
    protected void initViewsAndEvents(Bundle savedInstanceState) {
        initToolbar("读取Excel");
        initListView();
    }

    private void initListView() {
        mAdapter = new ExcelTestAdapter(ReadExcelActivity.this, list);
        mListView.setLayoutManager(new LinearLayoutManager(ReadExcelActivity.this));
        mListView.addItemDecoration(new ListLineDecoration());
        mListView.setAdapter(mAdapter);
    }

    @OnClick({R.id.tv_read, R.id.tv_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_read:
                readExcel();
                break;
            case R.id.tv_out:
                outExcel();
                break;
        }
    }


    /**
     * 读取本地Excel文件
     */
    private void readExcel() {
        try {
            //创建输入流
            InputStream stream = new FileInputStream(filePath);

            //获取Excel文件对象
            Workbook rwb = Workbook.getWorkbook(stream);
            //获取文件的指定工作表 默认的第一个
            Sheet sheet = rwb.getSheet(0);
            list.clear();
            //行数(表头的目录不需要，从1开始)
            for (int i = 0; i < sheet.getRows(); i++) {
                ExcelResponse excelResponse = new ExcelResponse();
                Cell cell = null;
                //列数
                for (int j = 0; j < sheet.getColumns(); j++) {
                    //获取第i行，第j列的值
                    cell = sheet.getCell(j, i);
                    switch (j) {
                        case 0:
                            excelResponse.setId(cell.getContents());
                            break;
                        case 1:
                            excelResponse.setName(cell.getContents());
                            break;
                        case 2:
                            excelResponse.setSex(cell.getContents());
                            break;
                        case 3:
                            excelResponse.setAge(cell.getContents());
                            break;
                    }
                }
                //把刚获取的列存入list
                list.add(excelResponse);
                mAdapter.notifyDataSetChanged();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出Excel文件到本地
     */
    private void outExcel() {
        try {
            //创建输入流
            InputStream stream = new FileInputStream(filePath);
            //获取Excel文件对象
            Workbook rwb = Workbook.getWorkbook(stream);
            //创建可写的工作表
            WritableWorkbook wwb = Workbook.createWorkbook(new File(filePath), rwb);
            //获取文件的指定工作表 默认的第一个
            WritableSheet sheet = wwb.getSheet(0);
            //第三行第二列写入50
            Label label = new Label(2,3,"hello");
            sheet.addCell(label);
            wwb.write();
            wwb.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (BiffException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (RowsExceededException e) {
            e.printStackTrace();
        } catch (WriteException e) {
            e.printStackTrace();
        }
    }
}