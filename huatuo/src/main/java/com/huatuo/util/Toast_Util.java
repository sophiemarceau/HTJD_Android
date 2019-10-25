package com.huatuo.util;

import com.huatuo.util.*;

import android.R.integer;
import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Toast
 * 
 * @author Administrator
 * 
 */
public class Toast_Util {

	private static final int TOAST_Y = 50;
	private static String oldMsg;
	protected static Toast toast = null;
	private static long oneTime = 0;
	private static long twoTime = 0;

	/**
	 * 设置Toast的属性
	 * 
	 * @param context
	 * @param text
	 */
	public static void showToast(Context context, String text) {
		
		
		
		if (context != null) {
			
			if (toast == null) {
				toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, TOAST_Y);
				toast.show();
				oneTime = System.currentTimeMillis();
			} else {
				twoTime = System.currentTimeMillis();
				if (text.equals(oldMsg)) {
					if (twoTime - oneTime > Toast.LENGTH_SHORT) {
						toast.setGravity(Gravity.CENTER, 0, TOAST_Y);
						toast.show();
					}
				} else {
					oldMsg = text;
					toast.setText(text);
					toast.setGravity(Gravity.CENTER, 0, TOAST_Y);
					toast.show();
				}
			}
			oneTime = twoTime;
		}
	}

	public static void showToastAtBootom(Context context, String text) {
		if (context != null) {
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, TOAST_Y);
			toast.show();
		}
	}

	public static void showToast(Context context, String text, int duration) {
		Toast toast = new Toast(context);
		toast.setText(text);
		toast.setDuration(duration);
		toast.setGravity(Gravity.CENTER, 0, TOAST_Y);
		toast.show();
	}

	/**
	 * 设置Toast的属性
	 * 
	 * @param context
	 * @param text
	 */
	public static void showToastOfLONG(Context context, String text) {
		if (context != null) {
			Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
			toast.setGravity(Gravity.CENTER, 0, TOAST_Y);
			toast.show();
		}
	}

	/**
	 * 设置Toast的属性
	 * 
	 * @param context
	 * @param text
	 */
	public static void showToastAtBottom(Context context, String text) {
		int time = 100;
		Toast toast = Toast.makeText(context, text, time);
		toast.setGravity(Gravity.BOTTOM, 0, TOAST_Y);
		toast.show();
	}

	public static void showToastOfDataEmpty(Context context) {
		Toast toast = Toast.makeText(context, "未能获取到数据", Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, TOAST_Y);
		toast.show();
	}

	/**
	 * 只显示一次
	 * @param context
	 * @param s
	 */
	public static void showToastOnlyOne(Context context, String s) {
		if (toast == null) {
			toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.BOTTOM, 0, TOAST_Y);
			toast.show();
			oneTime = System.currentTimeMillis();
		} else {
			twoTime = System.currentTimeMillis();
			if (s.equals(oldMsg)) {
				if (twoTime - oneTime > Toast.LENGTH_SHORT) {
					toast.setGravity(Gravity.BOTTOM, 0, TOAST_Y);
					toast.show();
				}
			} else {
				oldMsg = s;
				toast.setText(s);
				toast.setGravity(Gravity.BOTTOM, 0, TOAST_Y);
				toast.show();
			}
		}
		oneTime = twoTime;
	}
	
	
	/** 
	 * 只显示一次
	 * @param context
	 * @param s 内容
	 * @param gravity 位置
	 * @param isLong 是否长显示
	 */
	public static void showToastOnlyOne(Context context, String s,int gravity,Boolean isLong) {
		int time = 0;
		if(isLong){
			time = Toast.LENGTH_LONG;
		}else {
			time = Toast.LENGTH_SHORT;
		}
		
		if (toast == null) {
			toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
			toast.setGravity(gravity, 0, TOAST_Y);
			toast.show();
			oneTime = System.currentTimeMillis();
		} else {
			twoTime = System.currentTimeMillis();
			if (s.equals(oldMsg)) {
				if (twoTime - oneTime > time) {
					toast.setGravity(gravity, 0, TOAST_Y);
					toast.show();
				}
			} else {
				oldMsg = s;
				toast.setText(s);
				toast.setGravity(gravity, 0, TOAST_Y);
				toast.show();
			}
		}
		oneTime = twoTime;
	}

	public static void cancelToast() {
		if (toast != null) {
			toast.cancel();
		}
	}

}
