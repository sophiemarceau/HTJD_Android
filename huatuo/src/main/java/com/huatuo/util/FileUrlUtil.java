package com.huatuo.util;

/*
 * 文 件 名:  DataCleanManager.java
 * 描    述:  主要功能有清除内/外缓存，清除数据库，清除sharedPreference，清除files和清除自定义目录
 */

import java.io.File;

import android.content.Context;
import android.os.Environment;

/**
 * 本应用文件路径管理器
 */
public class FileUrlUtil {
	/**
	 * 返回本应用内部缓存(/data/data/com.xxx.xxx/cache)路径
	 * 
	 * @param context
	 */
	public static File internalCache(Context context) {
		return context.getCacheDir();
	}

	/**
	 * 返回本应用所有数据库(/data/data/com.xxx.xxx/databases)路径
	 * 
	 * @param context
	 */
	public static File databases(Context context) {
		return new File("/data/data/" + context.getPackageName() + "/databases");
	}

	/**
	 * 返回本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs)路径
	 * 
	 * @param context
	 */
	public static File sharedPreference(Context context) {
		return new File("/data/data/" + context.getPackageName() + "/shared_prefs");
	}

	/**
	 * 返回/data/data/com.xxx.xxx/files下的内容路径
	 * 
	 * @param context
	 */
	public static File files(Context context) {
		return context.getFilesDir();
	}

	/**
	 * 返回外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache)路径
	 * 
	 * @param context
	 */
	public static File externalCache(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return context.getExternalCacheDir();
		}
		return null;
	}

	/**
	 * 返回外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache/uil-images)
	 * 图片缓存路径
	 * 
	 * @param context
	 */
	public static File imageExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			return new File(context.getExternalCacheDir() + "/uil-images");
		}
		return null;
	}

}