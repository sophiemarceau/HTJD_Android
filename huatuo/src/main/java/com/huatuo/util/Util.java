package com.huatuo.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.huatuo.R;

/**
 * 通用的变量和方法类
 */
public class Util {
	private static long lastClickTime;

	public static boolean isFastDoubleClick() {
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 500) {
			return true;
		}
		lastClickTime = time;
		return false;
	}

	public static Drawable creatGrayBitmap(Bitmap paramBitmap) {
		Drawable localDrawable = null;
		if (paramBitmap != null) {
			Bitmap.Config localConfig = paramBitmap.getConfig();
			Paint localPaint = new Paint();
			ColorMatrix localColorMatrix = new ColorMatrix();
			localColorMatrix.setScale(0.8F, 0.8F, 0.8F, 1.0F);
			localPaint.setColorFilter(new ColorMatrixColorFilter(localColorMatrix));
			if (localConfig == null)
				localConfig = Bitmap.Config.ARGB_8888;
			Bitmap localBitmap = Bitmap.createBitmap(paramBitmap.getWidth(), paramBitmap.getHeight(), localConfig);
			new Canvas(localBitmap).drawBitmap(paramBitmap, 0.0F, 0.0F, localPaint);
			ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
			localBitmap.compress(Bitmap.CompressFormat.PNG, 100, localByteArrayOutputStream);
			localDrawable = BitmapDrawable.createFromStream(new ByteArrayInputStream(localByteArrayOutputStream.toByteArray()), "ColorFilterRelativeLayout");
			paramBitmap.recycle();
			localBitmap.recycle();
		}
		return localDrawable;
	}

	/**
	 * 根据变量名称获得变量值
	 * 
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	public static int getRValue(String name) {
		try {
			return R.drawable.class.getField(name).getInt(name);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * 显示软键盘
	 * */
	public static void showSoftInputFromWindow(View v) {
		InputMethodManager inputManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		inputManager.showSoftInput(v, 0);
	}

	/**
	 * 隐藏软键盘
	 * */
	public static void hideSoftInputFromWindow(View v) {
		InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
		}
	}

	public static String getVersionName(Context context) {
		// String packageName = "";
		String versionName = "";
		int versionCode = 0;
		PackageInfo packageInfo = null;
		try { // 取得包名
			String packageName = context.getPackageName();
			// 取得包信息
			packageInfo = context.getPackageManager().getPackageInfo(packageName, 0);
			versionCode = packageInfo.versionCode;
			versionName = packageInfo.versionName;
		} catch (Exception e) {
		}
		return versionName;
	}

	/**
	 * 设置Button是否可以点击
	 * */
	public static void setButtonClickable(View view, boolean isChecked) {
		if (isChecked) {
			view.getBackground().setAlpha(255);// 0~255透明度值
			view.setClickable(true);
		} else {
			view.getBackground().setAlpha(100);// 0~255透明度值
			view.setClickable(false);
		}
	}

	public static String getSystemLanguage(Context mContext) {
		Locale locale = mContext.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		// LogUtil.e("test", "language="+language);
		return language;
	}

	/**
	 * 获取手机mac地址
	 * */
	public static String getLocalMacAddress(Context mContext) {
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	/**
	 * 判断有无sim卡
	 * 
	 * @param
	 * @return true : sim存在<br/>
	 *         false: sim不存在
	 * */
	public static boolean isSimExist(Context mContext) {
		TelephonyManager manager = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		int absent = manager.getSimState();
		if (1 == absent) {
			// displayToast("请确认sim卡是否插入或者sim卡暂时不可用！");
			return false;
		} else {
			return true;
		}
	}

	private boolean isZh(Context mContext) {
		Locale locale = mContext.getResources().getConfiguration().locale;
		String language = locale.getLanguage();
		// LogUtil.e("test", "language="+language);
		if (language.endsWith("zh")) {
			return true;
		} else {
			return false;
		}
	}
}