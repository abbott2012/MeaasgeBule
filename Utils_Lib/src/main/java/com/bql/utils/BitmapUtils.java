package com.bql.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Cyarie on 2016/4/1.
 */
public class BitmapUtils {

    /**
     * 根据Asset下的文件路径找到图片，返回bitmap
     */
    public static Bitmap getBitmapFromAsset(Context context, String imagePath) {
        Bitmap bitmap = null;
        if (CheckUtils.isEmpty(imagePath)) {
            return bitmap;
        }
        AssetManager assetManager = context.getAssets();
        InputStream is = null;
        try {
            is = assetManager.open(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (is != null) {
            bitmap = BitmapFactory.decodeStream(is);
        }
        return bitmap;
    }

    /*
     * 缩放图片
	 */
    public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
        if (bm == null) {
            return null;
        }
        // 获得图片的宽高
        int width = bm.getWidth();
        int height = bm.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
                true);
        return newbm;
    }

    /**
     * 解析bitmap
     *
     * @param filePath 文件路径
     * @return
     */
    public static byte[] decodeBitmap(String filePath) {
        // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        options.inSampleSize = (int) ((options.outHeight * options.outWidth * 4) / (Runtime
                .getRuntime().maxMemory() / 10));
        // 使用获取到的inSampleSize值再次解析图片
        options.inJustDecodeBounds = false;

        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        if (getBitmapsize(bitmap) > 1024 * 250) {
            return dealImg(bitmap);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        bitmap.recycle();
        bitmap = null;

        return baos.toByteArray();

    }

    /**
     * 获取bitmap大小
     *
     * @param bitmap
     * @return
     */
    public static long getBitmapsize(Bitmap bitmap) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return getHONEYCOMBBitmapsize(bitmap);
        }
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    //不同SDK版本获取图片大小不一样
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public static long getHONEYCOMBBitmapsize(Bitmap bitmap) {
        return bitmap.getByteCount();
    }

    /**
     * 处理bitmap
     *
     * @param image
     * @return
     */
    public static byte[] dealImg(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
        }

        image.recycle();
        image = null;

        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
        float hh = 800f;// 这里设置高度为800f
        float ww = 480f;// 这里设置宽度为480f
        // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;// be=1表示不缩放
        if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置缩放比例
        // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
    }

    /**
     * 压缩bitmap
     *
     * @param image
     * @return
     */
    public static byte[] compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        // ByteArrayInputStream isBm = new
        // ByteArrayInputStream(baos.toByteArray());//
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        // Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
        // 把ByteArrayInputStream数据生成图片
        // return bitmap;

        image.recycle();
        image = null;

        return baos.toByteArray();
    }

}
