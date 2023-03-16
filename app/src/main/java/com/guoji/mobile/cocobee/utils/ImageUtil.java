package com.guoji.mobile.cocobee.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.guoji.mobile.cocobee.R;

import java.io.ByteArrayOutputStream;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * ClassName: ImageUtils <br>
 * Description: 图片工具类<br>
 * Author: Cyarie <br>
 * Created: 2016/5/17 17:31 <br>
 * Update Time：<br>
 * Update Description：<br>
 */
public class ImageUtil {

    /**
     * 加载小头像
     *
     * @param url
     * @param imageView
     */
    public static void loadSmallAvatar(Context context, String url, ImageView imageView) {
        Glide.with(context).load(getUrl(url)).bitmapTransform(new CenterCrop(context), new CropCircleTransformation(context)).crossFade().placeholder(R.drawable.head).error(R.drawable.head).into(imageView);
    }

    /**
     * 加载车的小头像
     *
     * @param url
     * @param imageView
     */
    public static void loadCarSmallAvatar(Context context, String url, ImageView imageView) {
        Glide.with(context).load(getUrl(url)).bitmapTransform(new CenterCrop(context), new CropCircleTransformation(context)).crossFade().placeholder(R.drawable.role_bike_default).error(R.drawable.role_bike_default).into(imageView);
    }

    /**
     * 加载车的小图标
     *
     * @param url
     * @param imageView
     */
    public static void loadCarSmallIcon(Context context, String url, ImageView imageView) {
        Glide.with(context).load(getUrl(url)).bitmapTransform(new CenterCrop(context), new CropCircleTransformation(context)).crossFade().placeholder(R.drawable.location_bike_default).error(R.drawable.location_bike_default).into(imageView);
    }

    /**
     * 加载人的小头像
     *
     * @param url
     * @param imageView
     */

    public static void loadPersonSmallAvatar(Context context, String url, ImageView imageView) {
        Glide.with(context).load(getUrl(url)).bitmapTransform(new CenterCrop(context), new CropCircleTransformation(context)).crossFade().placeholder(R.drawable.role_person_default).error(R.drawable.role_person_default).into(imageView);
    }

    /**
     * 加载人的小图标
     *
     * @param url
     * @param imageView
     */
    public static void loadPersonSmallIcon(Context context, String url, ImageView imageView) {
        Glide.with(context).load(getUrl(url)).bitmapTransform(new CenterCrop(context), new CropCircleTransformation(context)).crossFade().placeholder(R.drawable.location_person_default).error(R.drawable.location_person_default).into(imageView);

    }


    //    /**
    //     * 加载本地资源图片
    //     *
    //     * @param resId
    //     * @param imageView
    //     */
    //    public static void loadLargePicByRes(int resId, ImageView imageView) {
    //        Glide.with(LeYaoGoApplication.getContext()).load(resId).bitmapTransform(new CenterCrop(LeYaoGoApplication.getContext())).crossFade().placeholder(R.drawable.product_img_big).error(R.drawable.product_img_big).into(imageView);
    //    }

    /**
     * 加载人的详情图片
     *
     * @param url
     * @param imageView
     */
    public static void loadPersonDetailPic(Context context, String url, ImageView imageView) {
        Glide.with(context).load(getUrl(url)).asBitmap().centerCrop().placeholder(R.drawable.person_empty).error(R.drawable.person_empty).into(imageView);
    }


    /**
     * 加载图片
     *
     * @param url
     * @param imageView
     */
    public static void loadPic(Context context, String url, ImageView imageView) {
        Glide.with(context).load(getUrl(url)).asBitmap().centerCrop().placeholder(R.drawable.bike_empty).error(R.drawable.bike_empty).into(imageView);
    }

    /**
     * 加载报警图片
     *
     * @param url
     * @param imageView
     */
    public static void loadAlermPic(Context context, String url, ImageView imageView) {
        Glide.with(context).load(getUrl(url)).asBitmap().centerCrop().placeholder(R.drawable.bike_pic_default).error(R.drawable.bike_pic_default).into(imageView);
    }


    /**
     * 加载圆形bitmap图片
     *
     * @param imageView
     */
    public static void loadRoundBitmap(Context context, Bitmap bitmap, ImageView imageView) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        Glide.with(context).load(bytes).bitmapTransform(new CenterCrop(context), new CropCircleTransformation(context)).crossFade().placeholder(R.drawable.head).error(R.drawable.head).into(imageView);
    }

    /**
     * 加载圆形bitmap图片
     *
     * @param imageView
     */
    public static void loadBitmap(Context context, Bitmap bitmap, ImageView imageView) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        Glide.with(context).load(bytes).asBitmap().centerCrop().placeholder(R.drawable.bike_pic_default).error(R.drawable.bike_pic_default).into(imageView);
    }

    /**
     * 加载圆形Uri图片
     *
     * @param imageView
     */
    public static void loadRoundUri(Context context, Uri uri, ImageView imageView) {
        Glide.with(context).load(uri).bitmapTransform(new CenterCrop(context), new CropCircleTransformation(context)).crossFade().placeholder(R.drawable.head).error(R.drawable.head).into(imageView);
    }
    /**
     * 加载选择后的图片
     *
     * @param imageView
     */
    public static void loadSelectPic(Context context, String path, ImageView imageView) {
        Glide.with(context).load(path).asBitmap().centerCrop().placeholder(R.drawable.add_pic).error(R.drawable.add_pic).into(imageView);
    }

    /**
     * 加载人的头像
     *
     * @param url
     * @param imageView
     */

    public static void loadPersonAvatar(Context context, String url, ImageView imageView) {
        Glide.with(context).load(url).bitmapTransform(new CenterCrop(context), new CropCircleTransformation(context)).crossFade().placeholder(R.drawable.role_person_default).error(R.drawable.role_person_default).into(imageView);
    }



//    /**
//     * 加载Splash Gif动画 指加载一次
//     *
//     * @param resId
//     * @param imageView
//     */
//    public static void loadSplashGif(Context context,int resId, ImageView imageView) {
//        Glide.with(context).load(resId).into(new GlideDrawableImageViewTarget(imageView, 1));
//    }
//
//    /**
//     * 加载圆角矩形
//     * @param url
//     * @param imageView
//     */
//    public static void loadRoundPic(Context context,String url, ImageView imageView) {
//        Glide.with(context).load(url).placeholder(R.drawable.img_default_avatar).error(R.drawable.img_default_avatar).transform(new GlideRoundTransform(context, 4)).into(imageView);
//    }

    public static GlideUrl getUrl(String url) {
        return new GlideUrl(url, new LazyHeaders.Builder().addHeader("referer", "http://www.intrace.cn/").build());
    }

}







