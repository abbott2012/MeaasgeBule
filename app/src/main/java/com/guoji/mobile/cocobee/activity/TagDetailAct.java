package com.guoji.mobile.cocobee.activity;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.guoji.mobile.cocobee.R;
import com.guoji.mobile.cocobee.common.Path;
import com.guoji.mobile.cocobee.model.TagRecord;
import com.guoji.mobile.cocobee.utils.ImageUtil;
import com.guoji.mobile.cocobee.view.NoScrollGridView;

import java.util.ArrayList;
import java.util.List;

/**
 * 标签详情页面
 * Created by _H_JY on 2017/1/9.
 */
public class TagDetailAct extends BaseAct implements View.OnClickListener {

    private ImageButton back_ib;
    private TextView username_tv;
    private TextView gender_tv;
    private TextView phone_tv;
    private TextView cno_tv;
    private TagRecord tagRecord;
    private TextView title_tv;
    private RelativeLayout cno_rl;
    private NoScrollGridView image_gv;
    private ImageLvAdapter adapter;
    private List<String> imageUrls = new ArrayList<>(); //存放网络图片地址的集合

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tag_detail);


        tagRecord = (TagRecord) getIntent().getSerializableExtra("tagRecord");

        initView();
    }


    private void initView() {
        back_ib = (ImageButton) findViewById(R.id.back_ib);
        username_tv = (TextView) findViewById(R.id.user_name_tv);
        gender_tv = (TextView) findViewById(R.id.user_sex_tv);
        phone_tv = (TextView) findViewById(R.id.user_phone_tv);
        cno_tv = (TextView) findViewById(R.id.cno_tv);
        title_tv = (TextView) findViewById(R.id.title_tv);
        cno_rl = (RelativeLayout) findViewById(R.id.cno_rl);
        image_gv = (NoScrollGridView) findViewById(R.id.image_gv);
        image_gv.setFocusable(false); //禁止图片抢占焦点


        if (tagRecord != null) {

            username_tv.setText(tagRecord.getPname());
            if ("0".equals(tagRecord.getGender())) { //男
                gender_tv.setText(getString(R.string.man));
            } else {
                gender_tv.setText(getString(R.string.famale));
            }

            phone_tv.setText(tagRecord.getMobile());

            String allImageUrl = "";

            if ("1".equals(tagRecord.getFlag())) {
                title_tv.setText(getString(R.string.car_tag_info));
                cno_rl.setVisibility(View.VISIBLE);
                cno_tv.setText(tagRecord.getCno());
                allImageUrl = tagRecord.getCcarpicurl();
            } else {
                title_tv.setText(getString(R.string.person_tag_info));
                cno_rl.setVisibility(View.GONE);
                allImageUrl = tagRecord.getPhotourl();
            }


            if (!TextUtils.isEmpty(allImageUrl)) {
                String[] images = allImageUrl.split(",");
                if (images != null && images.length > 0) {
                    for (int i = 0; i < images.length; i++) {
                        imageUrls.add(Path.IMG_BASIC_PATH + images[i]);
                    }
                }
            }
        }


        adapter = new ImageLvAdapter(this, imageUrls);
        image_gv.setAdapter(adapter);

        back_ib.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_ib:
                finish();
                break;
        }
    }


    private class ImageLvAdapter extends BaseAdapter {

        private LayoutInflater inflater;
        private List<String> urls = new ArrayList<>();
        private Context context;

        public ImageLvAdapter(Context context, List<String> urls) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.urls = urls;
        }

        @Override
        public int getCount() {
            return urls.size();
        }

        @Override
        public Object getItem(int position) {
            return urls.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;

            if (view == null) {
                viewHolder = new ViewHolder();
                view = inflater.inflate(R.layout.gv_item, null);
                viewHolder.pic_iv = (ImageView) view.findViewById(R.id.imageView);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            ImageUtil.loadPic(context,urls.get(position), viewHolder.pic_iv);

            return view;
        }
    }


    private class ViewHolder {
        private ImageView pic_iv;
    }


}
