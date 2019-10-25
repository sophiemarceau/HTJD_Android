package com.huatuo.util;

import android.util.Log;

/**
 * 日志工具类
 * 
 * @author ftc
 */
public class LogUtil {
	public static void v(String tag, String msg) {
		// Log.v(tag, msg);
	}

	public static void d(String tag, String msg) {
		// Log.d(tag, msg);
	}

	public static void i(String tag, String msg) {
		Log.i(tag, msg);
	}

	public static void w(String tag, String msg) {
		// Log.w(tag, msg);
	}

	public static void e(String tag, String msg) {
		if (tag == null) {
//			Log.e("joymax2", msg);
		} else {
//			Log.e(tag, msg);
		}
	}
}