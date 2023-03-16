package com.guoji.mobile.cocobee.activity;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.adapter.ExcelAdapter;
import com.guoji.mobile.cocobee.btreader.BlueToothHelper;
import com.guoji.mobile.cocobee.common.Constant;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.Tag;
import com.guoji.mobile.cocobee.model.TagSearch;
import com.guoji.mobile.cocobee.utils.XToastUtils;
import com.guoji.mobile.cocobee.view.ListLineDecoration;
import com.lzy.okgo.OkGo;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author _H_JY
 * @date 16/11/1
 * 资产盘点页面，配合2.0/4.0蓝牙设备搜索标签
 */
public class AssetsSearchTagActivity extends BaseAct implements View.OnClickListener {

    private Context context;
    private ImageView back_ib;
    private TextView status_tv;
    private RecyclerView mListView;
    private BlueToothHelper reader;
    private List<TagSearch> dataList = new ArrayList<>();
    private List<String> collectList = new ArrayList<>();
    private TextView beep_btn, control_btn, tvTitle;
    private boolean isSpeak = true;
    private MediaPlayer mp;
    private boolean isOpenTagDataOutput = false;
    private String newFilePath = Path.PROJECT_FILE_PATH + "copyhwzc.xlsx";

    private int controlFlag = 0;//0正在扫描,1未扫描


    private ExcelAdapter mAdapter;
    private String filePath = Path.PROJECT_FILE_PATH + "hwzc.xlsx";
    private TextView tvOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_read_tag);
        context = this;
        mp = MediaPlayer.create(AssetsSearchTagActivity.this, R.raw.beep);
        reader = BlueToothHelper.getInstance(context, handler);

        if (isConnected()) { //如果已经连接，开启标签输出
            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) { //2.0
//                reader.openTagDataOutput();
                reader.openTagDataOutputOfSixByte();
            } else { //BLE
                reader.bleSendCmd(Constant.OPEN_TAG_DATA_OUTPUT, BlueToothHelper.START_SET_SIX_BYTE_OF_SEARCH, false);
            }
            isOpenTagDataOutput = true;
        }

        initView();
        readExcel();
    }


    private boolean isConnected() {

        SharedPreferences sp = getSharedPreferences(Constant.SP_CONNECT_STATUS, Context.MODE_PRIVATE);
        boolean status = sp.getBoolean(Constant.CONNECT_STATUS, false);

        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC && status && (reader.getSocket() == null || !reader.getSocket().isConnected())) { //如果当前标记已连接，但是socket连接实际已经断了
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean(Constant.CONNECT_STATUS, false);
            editor.commit();
            status = false;
        }
        return status;
    }


    private void initView() {
        back_ib = (ImageView) findViewById(R.id.back_ib);
        mListView = (RecyclerView) findViewById(R.id.listView);
        status_tv = (TextView) findViewById(R.id.status_tv);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        beep_btn = (TextView) findViewById(R.id.beep_btn);
        control_btn = (TextView) findViewById(R.id.control_btn);
        tvOut = (TextView) findViewById(R.id.tv_out);
        tvTitle.setText(getString(R.string.tag_discovery));
        if (isSpeak) {
            beep_btn.setText(getString(R.string.turn_sound_off));
        } else {
            beep_btn.setText(getString(R.string.open_the_sound));
        }

        back_ib.setOnClickListener(this);
        beep_btn.setOnClickListener(this);
        control_btn.setOnClickListener(this);
        tvOut.setOnClickListener(this);

        mAdapter = new ExcelAdapter(AssetsSearchTagActivity.this, dataList);
        mListView.setLayoutManager(new LinearLayoutManager(AssetsSearchTagActivity.this));
        mListView.addItemDecoration(new ListLineDecoration());
        mListView.setAdapter(mAdapter);

    }

    /**
     * 读取本地Excel文件
     */
    private void readExcel() {
        try {


            FileInputStream myInput = new FileInputStream(filePath);
            XSSFWorkbook workbook = new XSSFWorkbook(myInput);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowsCount = sheet.getPhysicalNumberOfRows();
            FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
            if(workbook != null){
                for (int r = 1; r<rowsCount; r++) {
                    TagSearch tagSearch=new TagSearch();
                    Row row = sheet.getRow(r);
                    int cellsCount = row.getPhysicalNumberOfCells();
                    for (int c = 0; c<cellsCount; c++) {
                        String value = getCellAsString(row, c, formulaEvaluator);
                        switch (c){
                            case 0:
                                tagSearch.setTagId(value);
                                break;
                            case 1:
                                tagSearch.setDesc(value);
                                break;
                            case 2:
                                tagSearch.setIsRead(value);
                                break;
                            case 3:
                                tagSearch.setReadTime(value);
                                break;
                        }
                    }
                    dataList.add(tagSearch);

                }
                mAdapter.notifyDataSetChanged();
            }
            mAdapter.notifyDataSetChanged();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            XToastUtils.showShortToast(getString(R.string.no_found_excel));
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导出Excel文件到本地
     */
    private void outExcel(String filename ) {
        try {
            /*
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
            */
            XSSFWorkbook workBook = new XSSFWorkbook();
            XSSFSheet sheet = workBook.createSheet("Sheet 0");
            //创建工作表
            //创建行
            XSSFRow row = sheet.createRow(0);
            //创建单元格，操作第三行第三列
            XSSFCell cell = row.createCell(0);
            cell.setCellValue("标签ID");
            cell = row.createCell(1);
            cell.setCellValue("资产描述");
            cell = row.createCell(2);
            cell.setCellValue("盘点情况");
            cell = row.createCell(3);
            cell.setCellValue("盘点时间");

            for (int i=0;i<dataList.size();i++){
                XSSFRow   row_ = sheet.createRow(i+1);
                XSSFCell cell_ = row_.createCell(0);
                cell_.setCellValue(dataList.get(i).getTagId());
                cell_ = row_.createCell(1);
                cell_.setCellValue(dataList.get(i).getDesc());
                cell_ = row_.createCell(2);
                cell_.setCellValue(dataList.get(i).getIsRead());
                cell_ = row_.createCell(3);
                cell_.setCellValue(dataList.get(i).getReadTime());

            }

            FileOutputStream outputStream = new FileOutputStream(new File(Path.PROJECT_FILE_PATH+filename+".xlsx"));
            workBook.write(outputStream);
            workBook.close();//记得关闭工作簿

            /*

            //创建可写的工作表
            WritableWorkbook wwb = Workbook.createWorkbook(new File(filePath));
            //获取文件的指定工作表 默认的第一个
            WritableSheet sheet = wwb.createSheet("Sheet 0", 0);
            sheet.addCell(new Label(0, 0, "标签ID"));
            sheet.addCell(new Label(1, 0, "资产描述"));
            sheet.addCell(new Label(2, 0, "Y/N"));
            sheet.addCell(new Label(3, 0, "盘点时间"));
            for (int i = 0; i < dataList.size(); i++) {
                //从第二行开始写入
                Label labelId = new Label(0, i + 1, dataList.get(i).getTagId());
                Label labelDesc = new Label(1, i + 1, dataList.get(i).getDesc());
                Label label = new Label(2, i + 1, dataList.get(i).getIsRead());
                Label labelTime = new Label(3, i + 1, dataList.get(i).getReadTime());
                sheet.addCell(labelId);
                sheet.addCell(labelDesc);
                sheet.addCell(label);
                sheet.addCell(labelTime);

            }
            wwb.write();
            wwb.close();
            */
            XToastUtils.showShortToast(getString(R.string.export_success)+"\n"+getString(R.string.file_name_path)+":"+Path.PROJECT_FILE_PATH+filename+".xlsx");

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            XToastUtils.showShortToast(getString(R.string.no_found_excel));
        } catch (IOException e) {
            e.printStackTrace();
            XToastUtils.showShortToast(getString(R.string.write_excel_error));
        }
        /*
        catch (RowsExceededException e) {
            e.printStackTrace();
            XToastUtils.showShortToast("超过允许在工作表的最大行数");
        } catch (WriteException e) {
            e.printStackTrace();
            XToastUtils.showShortToast("写入Excel文件错误");

        }
        */
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_ib:
                finish();
                break;
            case R.id.tv_out:
                //outExcel();
                onInputFileNameDailog();
                break;
            case R.id.beep_btn:
                if (isSpeak) {
                    isSpeak = false;
                    beep_btn.setText(getString(R.string.open_the_sound));
                } else {
                    isSpeak = true;
                    beep_btn.setText(getString(R.string.turn_sound_off));

                }
                break;

            case R.id.control_btn:
                if (!isConnected()) {
                    Toast.makeText(context, getString(R.string.unconnected_device), Toast.LENGTH_SHORT).show();
                    status_tv.setText(R.string.state_unconnected);
                    return;
                }

                btnSearch();
                break;
            default:
                break;
        }
    }
    //输入文件名对话框
    private void onInputFileNameDailog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(AssetsSearchTagActivity.this);
        builder.setTitle(getString(R.string.please_input_file_name));
        View view = View  .inflate(this, R.layout.dailog_filename_layout, null);
        builder.setView(view);
        builder.setCancelable(true);
        final EditText fileNameEdt= (EditText) view
                .findViewById(R.id.fileNameEdt);
        fileNameEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void afterTextChanged(Editable editable) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String editable = fileNameEdt.getText().toString();
                String speChat="[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
                Pattern p = Pattern.compile(speChat);
                Matcher m = p.matcher(editable);
                String str = m.replaceAll("").trim();    //删掉不是字母或数字的字符
                if(!editable.equals(str)){
                    fileNameEdt.setText(str);  //设置EditText的字符
                    fileNameEdt.setSelection(str.length()); //因为删除了字符，要重写设置新的光标所在位置
                }
            }
        });

        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String fileNameString = fileNameEdt.getText().toString();
                        if (fileNameString!=null&&!fileNameString.equals("")){
                            dialog.cancel();
                            outExcel(fileNameString);
                        }else {
                            XToastUtils.showShortToast(getString(R.string.please_input_file_name));
                               }

                    }
                }
        );
        builder.setNegativeButton(android.R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //点击取消按钮处理
                        dialog.cancel();
                    }
                });
        final AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void btnSearch() {
        if (controlFlag == 1) { //未扫描,开始扫描
            startScan();
        } else if (controlFlag == 0) { //正在扫描,停止扫描
            stopScan();
            Toast.makeText(context, getString(R.string.stop), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 开始扫描
     */
    private void startScan() {
        control_btn.setText(getString(R.string.stop_reading));
        controlFlag = 0;
        reader.setScanFlag(false); //先停止已有线程
        reader.setScanFlag(true);

        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
            reader.scanTagHuaWang();
        } else {
            reader.setBleCmdType(Constant.BLE_SCAN_TAG_HUA_WEI);
        }
    }

    /**
     * 停止扫描
     */
    private void stopScan() {
        control_btn.setText(getString(R.string.start_reading));
        controlFlag = 1;
        reader.setScanFlag(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (!isConnected()) {
            Toast.makeText(context, getString(R.string.unconnected_device_not_search), Toast.LENGTH_SHORT).show();
            status_tv.setText(getString(R.string.state_unconnected));
            stopScan();
        } else {
            status_tv.setText(getString(R.string.state_connected));
            startScan();
        }
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case BlueToothHelper.MSG_READ_TAG_OK:
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        reader.setIsNextTagDataCloudEnter(true);
                        reader.setIsFirstPackage(true);
                    }
                    Tag tag = (Tag) msg.obj;
                    String tagId = tag.getTagId();
                    if (!collectList.contains(tagId)) {
                        collectList.add(tagId);
                        TagSearch tagSearchNew = new TagSearch();
                        tagSearchNew.setTagId(tagId);
                        dataList.add(tagSearchNew);
                        for (int i = 0; i < dataList.size(); i++) {
                            TagSearch tagSearch = dataList.get(i);
                            if (tagId.equals(tagSearch.getTagId())) {
                                tagSearch.setIsRead("Y");
                                tagSearch.setReadTime(getDateToString(System.currentTimeMillis()));
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                    if (isSpeak) {
                        int absRssi = Math.abs(tag.getRssi());
                        play(absRssi); //播放声音
                    }
                    break;

                case BlueToothHelper.MSG_READ_TAG_FAIL:
                    if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_LE) {
                        reader.setIsNextTagDataCloudEnter(true);
                        reader.setIsFirstPackage(true);
                    }
                    break;
                case BlueToothHelper.MSG_NO_BLUETOOTH_SOCKET:
                    Toast.makeText(context, getString(R.string.unconnected_device_not_search), Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };


    public void play(int absRssi) {

        if (mp != null) {
            if (absRssi <= 25) {
                mp.setVolume(1.0f, 1.0f);

            } else if (absRssi > 25 && absRssi <= 35) {
                mp.setVolume(0.7f, 0.7f);
            } else if (absRssi > 35 && absRssi <= 40) {
                mp.setVolume(0.5f, 0.5f);
            } else if (absRssi > 40 && absRssi <= 50) {
                mp.setVolume(0.25f, 0.25f);
            } else if (absRssi > 50 && absRssi <= 70) {
                mp.setVolume(0.15f, 0.15f);
            } else if (absRssi > 70 && absRssi <= 80) {
                mp.setVolume(0.1f, 0.1f);
            } else {
                mp.setVolume(0.02f, 0.02f);
            }
            mp.start();
        }


    }


    @Override
    protected void onDestroy() {
        if (reader != null) {
            stopScan();
            /*如果已与设备建立连接，进入标签搜索页面后开启标签数据输出*/
            deviceStop();
        }
        if (mp != null) {
            mp.release();
            mp = null;
        }
        OkGo.getInstance().cancelTag(this);
        super.onDestroy();
    }

    /**
     * 停止设备读卡
     */
    private void deviceStop() {
        if (isConnected() && isOpenTagDataOutput) {

            if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                reader.closeTagDataOutput();
            } else {
                reader.bleSendCmd(Constant.BLE_CLOSE_TAG_DATA_OUTPUT, BlueToothHelper.CLOSE_TAG_OUTPUT_COMMAND, false);
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(50);
                    if (isConnected()) {
                        if (reader.getBluetoothType() == BluetoothDevice.DEVICE_TYPE_CLASSIC) {
                            reader.closeTagDataOutput();
                        } else {
                            reader.bleSendCmd(Constant.BLE_CLOSE_TAG_DATA_OUTPUT, BlueToothHelper.CLOSE_TAG_OUTPUT_COMMAND, false);
                        }
                    }
                }
            }).start();

            isOpenTagDataOutput = false;
        }
    }

    public String getDateToString(long time) {

        Date d = new Date(time);
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sf.format(d);
    }
    protected String getCellAsString(Row row, int c, FormulaEvaluator formulaEvaluator) {
        String value = "";
        try {
            org.apache.poi.ss.usermodel.Cell cell = row.getCell(c);
            CellValue cellValue = formulaEvaluator.evaluate(cell);
            switch (cellValue.getCellType()) {
                case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BOOLEAN:
                    value = ""+cellValue.getBooleanValue();
                    break;
                case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_NUMERIC:
                    double numericValue = cellValue.getNumberValue();
                    if(HSSFDateUtil.isCellDateFormatted(cell)) {
                        double date = cellValue.getNumberValue();
                        SimpleDateFormat formatter =
                                new SimpleDateFormat("dd/MM/yy");
                        value = formatter.format(HSSFDateUtil.getJavaDate(date));
                    } else {
                        value = ""+numericValue;
                    }
                    break;
                case org.apache.poi.ss.usermodel.Cell.CELL_TYPE_STRING:
                    value = ""+cellValue.getStringValue();
                    break;
                default:
            }
        } catch (NullPointerException e) {
            /* proper error handling should be here */
            //printlnToUser(e.toString());
        }
        return value;
    }
}
