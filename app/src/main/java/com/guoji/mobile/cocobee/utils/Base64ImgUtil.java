package com.guoji.mobile.cocobee.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

/**
 * Base64图片操作类
 * @author rebin
 *
 */
public class Base64ImgUtil {
	/**
	 * base64转成图片
	 * 
	 * @param str
	 * @return
	 */
	public static Bitmap Base64ToImg(String str) {
		try {
			byte[] byteIcon = Base64.decode(str, Base64.DEFAULT);
			return BitmapFactory.decodeByteArray(byteIcon, 0, byteIcon.length);
		} catch (Exception se) {

		}
		return null;
	}

	/**
	 * path转base64 str
	 * 
	 * @param path
	 * @return
	 */
	public static String pathToBase64(String path) {
		String str = null;
		try {
			Bitmap bmPhoto = BitmapFactory.decodeFile(path);
			str = bitmapToBase64(bmPhoto);
		} catch (Exception se) {
			se.printStackTrace();
		}
		return str;
	}

	/**
	 * imageView pic转成base64 string
	 * 
	 * @param pic
	 * @return
	 */
	public static String imgToBase64(ImageView pic) {
		String str = null;
		try {
			pic.setDrawingCacheEnabled(true);
			Bitmap obmp = Bitmap.createBitmap(pic.getDrawingCache());
			str = bitmapToBase64(obmp);
			pic.setDrawingCacheEnabled(false);
		} catch (Exception se) {
			se.printStackTrace();
		}
		return str;
	}

	/**
	 * 图片base64编码
	 *
	 * @return
	 */
	public static String bitmapToBase64(Bitmap bmPhoto) {
		try {

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bmPhoto.compress(Bitmap.CompressFormat.JPEG, 100, bos);
			String bstr = Base64.encodeToString(bos.toByteArray(),
					Base64.DEFAULT);
			return bstr;
		} catch (Exception se) {

		}
		return null;
	}
}
