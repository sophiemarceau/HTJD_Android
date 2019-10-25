package com.huatuo.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Service;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.huatuo.BuildConfig;
import com.huatuo.R;

/**
 * @author Android开发工程师王润泽
 * 
 */
public class CommonUtil {
	public static String DEVICEID = "";// 设备号 ANS 64 C
										// 客户端唯一设备号，用imei(长度小于13位放弃)或MAC，如果俩个都得不到，初次安装的时候生成UUID（系统提供的一个方法生成的唯一号）将这个uuid记录下来，每次会话都用这个，微信用户这里用openid
	public static String SCREENPIXEL = "";// 屏幕像素 ANS 16 M
	public static String LONGITUDE = "";// 经度 ANS 32 C （gps 位置坐标）
	public static String LATITUDE = "";// 纬度 ANS 32 C （gps 位置坐标）
	public static String APPVERSION = "";// 客户端版本 ANS 16 M 手机客户端版本
	public static String MESSAGEID = "";// 消息id ANS 32 C
										// [唯一消息id串]//同步处理不需要，服务端会原样返回
	public static String CHANNELCODE = "";// 推广渠道编号 ANS 128 O 推广渠道编号，可空
	// 屏幕宽
	public static int WIDTH_SCREEN;
	// 屏幕高
	public static int HEIGHT_SCREEN;
	// 状态栏高
	public static int HEIGHT_STATUS_BAR;
	//版本更新
	public static String STATUS = "";
	public static String NEW_VERSION = "";
	public static String NEW_DOWNLOADURL = "";
	public static final void log(String info) {
		if (BuildConfig.DEBUG) {
			Log.d("Test", info);
		}
	}

	public static final void logE(String info) {
		if (BuildConfig.DEBUG) {
			Log.e("Test", info);
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		CommonUtil.log("CommonUtil---- dp 的单位 转成为 px-------------scale:"
				+ scale);
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		CommonUtil.log("CommonUtil---- px(像素) 的单位 转成为 dp-------------scale:"
				+ scale);
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 将px值转换为sp值，保证文字大小不变
	 * 
	 * @param pxValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		CommonUtil.log("CommonUtil----将px值转换为sp值-------------fontScale:"
				+ fontScale);
		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * 将sp值转换为px值，保证文字大小不变
	 * 
	 * @param spValue
	 * @param fontScale
	 *            （DisplayMetrics类中属性scaledDensity）
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		CommonUtil.log("CommonUtil----sp值转换为px值-------------fontScale:"
				+ fontScale);
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 
	 * 判断String是不是为空
	 * 
	 * @param list
	 * @return
	 */
	public static String isEmptyString(String str) {
		if (null == str) {
			return null;
		} else if (("").equals(str)) {
			return null;
		} else if (("null").equals(str + ("").trim())) {
			return null;
		} else {
			return str;
		}

	}

	public static <T> boolean emptyListToString3(List<T> list) {
		CommonUtil.log("要转型list:" + list);
		if ((list + "").trim().equals("[]")) {
			return true;
		} else if (("").equals(list)) {
			return true;
		} else if (("null").equals((list + ("")).trim())) {
			return true;
		} else if (null == list) {
			return true;

		} else {
			return false;
		}

	}

	public static boolean emptyArrayToString(String[] arrayList) {
//		CommonUtil.log("要转型数组:" + arrayList);
		if ((arrayList + "").trim().equals("[]")) {
			return true;
		} else if (("").equals(arrayList)) {
			return true;
		} else if (null == arrayList) {
			return true;
		} else {
			return false;
		}

	}

	public static boolean NoEmptyArray(JSONArray array) {
		CommonUtil.log("要转型数组:" + array);
		if ((array + "").trim().equals("[]")) {
			return false;
		} else if (("").equals(array)) {
			return false;
		} else if (null == array) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 *  
	 * @param jsonObject 不为空返回true
	 * @return
	 */
	public static boolean NotEmptyJSONObject(JSONObject jsonObject) {
		if (("null").equals((jsonObject + ("")).trim())) {
			return false;
		} else if (null == jsonObject) {
			return false;
		} else if (("").equals((jsonObject + ("")).trim())) {
			return false;
		} else {
			return true;
		}

	}

	/**
	 * 保留两位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String formatNumOfString(String price) {
		double num = Double.parseDouble(price);
		// 保留两位小数
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(num);
	}

	/**
	 * 保留两位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveTwoPointOfDouble(Double price) {
		// 保留两位小数
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(price);
	}

	/**
	 * 保留1位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveOnePoint(String price) {
		double num = Double.parseDouble(price);
		// 保留两位小数
		DecimalFormat df = new DecimalFormat("0.0");
		return df.format(num);
	}

	/**
	 * 保留2位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveTwoPoint(String price) {
		double num = Double.parseDouble(price);
		// 保留两位小数
		DecimalFormat df = new DecimalFormat("0.0");
		return df.format(num);
	}

	/**
	 * 保留3位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveThreePoint(String price) {
		double num = Double.parseDouble(price);
		// 保留两位小数
		DecimalFormat df = new DecimalFormat("0.000");
		return df.format(num);
	}

	/**
	 * 保留4位小数
	 * 
	 * @param price
	 * @return
	 */
	public static String saveFourPoint(String price) {
		double num = Double.parseDouble(price);
		// 保留两位小数
		DecimalFormat df = new DecimalFormat("0.0000");
		return df.format(num);
	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param context
	 * @param view
	 */
	public static void hideKeyboard(Context context, EditText view) {
		CommonUtil.log("隐藏软键盘:view:" + view);
		if (null != view && null != context) {
			InputMethodManager inputmanger = (InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
		} else {
			CommonUtil.log("隐藏软键盘:view为空");
		}

	}

	/**
	 * 隐藏软键盘
	 * 
	 * @param context
	 * @param view
	 * 
	 *            ((InputMethodManager)
	 *            edt_first_serach.getContext().getSystemService
	 *            (Context.INPUT_METHOD_SERVICE)) .hideSoftInputFromWindow(
	 *            getActivity() .getCurrentFocus() .getWindowToken(),
	 *            InputMethodManager.HIDE_NOT_ALWAYS);
	 */
	public static void hideKeyboard(Activity context) {
		if (null != context) {
			((InputMethodManager) context
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow((context).getCurrentFocus()
							.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
		}

	}

	public static final String SP_NAME = "config";

	private static SharedPreferences sp;

	public static void saveStringOfSharedPreferences(Context context,
			String key, String value) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putString(key, value).commit();
	}

	public static String getStringOfSharedPreferences(Context context,
			String key, String defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getString(key, defValue);
	}

	public static void saveIntOfSharedPreferences(Context context, String key,
			int value) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putInt(key, value).commit();
	}

	public static int getIntOfSharedPreferences(Context context, String key,
			int defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getInt(key, defValue);
	}

	public static void saveBooleanOfSharedPreferences(Context context,
			String key, Boolean value) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		sp.edit().putBoolean(key, value).commit();
	}

	public static boolean getBooleanOfSharedPreferences(Context context,
			String key, Boolean defValue) {
		if (sp == null)
			sp = context.getSharedPreferences(SP_NAME, 0);
		return sp.getBoolean(key, defValue);
	}

	/**
	 * ' 获取星期几
	 * 
	 * @return
	 */
	public static int getWay() {
		final Calendar c = Calendar.getInstance();
		c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
		String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
		String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
		String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
		// String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
		int mWay = c.get(Calendar.DAY_OF_WEEK);
		// if("1".equals(mWay)){
		// mWay ="天";
		// }else if("2".equals(mWay)){
		// mWay ="一";
		// }else if("3".equals(mWay)){
		// mWay ="二";
		// }else if("4".equals(mWay)){
		// mWay ="三";
		// }else if("5".equals(mWay)){
		// mWay ="四";
		// }else if("6".equals(mWay)){
		// mWay ="五";
		// }else if("7".equals(mWay)){
		// mWay ="六";
		// }
		return mWay;
	}

	/**
	 * 根据一个日期，返回是星期几的字符串
	 * 
	 * @param sdate
	 * @return
	 */
	public static String getWeek(String sdate) {
		// 再转换为时间
		Date date = strToDate(sdate);
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		// int hour=c.get(Calendar.DAY_OF_WEEK);
		// hour中存的就是星期几了，其范围 1~7
		// 1=星期日 7=星期六，其他类推
		return new SimpleDateFormat("EEEE").format(c.getTime());
	}
	 public static String getWeek2(String sdate) {
         int mydate = 0;
         String week = null;
         Date date = strToDate(sdate);
 		 Calendar cd = Calendar.getInstance();
 		 cd.setTime(date);
         mydate = cd.get(Calendar.DAY_OF_WEEK);
         // 获取指定日期转换成星期几
         if (mydate == 1) {
                 week = "周日";
         } else if (mydate == 2) {
                 week = "周一";
         } else if (mydate == 3) {
                 week = "周二";
         } else if (mydate == 4) {
                 week = "周三";
         } else if (mydate == 5) {
                 week = "周四";
         } else if (mydate == 6) {
                 week = "周五";
         } else if (mydate == 7) {
                 week = "周六";
         }
         return week;
 }
	
	/**
	 * 将短时间格式字符串转换为时间 yyyy-MM-dd
	 * 
	 * @param strDate
	 * @return
	 */
	public static Date strToDate(String strDate) {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		ParsePosition pos = new ParsePosition(0);
		Date strtodate = formatter.parse(strDate, pos);
		return strtodate;
	}

	public static String getVersionName(Context context) {
		// String packageName = "";
		String versionName = "";
		int versionCode = 0;
		PackageInfo packageInfo = null;
		try { // 取得包名
			String packageName = context.getPackageName();
			// 取得包信息
			packageInfo = context.getPackageManager().getPackageInfo(
					packageName, 0);
			versionCode = packageInfo.versionCode;
			versionName = packageInfo.versionName;
		} catch (Exception e) {
		}

		CommonUtil.log("版本号：" + versionName);
		return versionName;
	}

	// 注意：导包的时候
	//
	// API 11之前： android.text.ClipboardManager
	// API 11之后： android.content.ClipboardManager

	/**
	 * 实现文本复制功能
	 * 
	 * @param content
	 */
	public static void copy(Context context, String content) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	/**
	 * 实现粘贴功能
	 * 
	 * @param context
	 * @return
	 */
	public static String paste(Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

	// SimpleCallback是一个简单的回调接口：
	public interface SimpleCallback {
		void onComplete(String result);
	}

	public static final void initScreen(Activity activity) {
		// 获取屏幕大小
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		CommonUtil.WIDTH_SCREEN = dm.widthPixels;
		CommonUtil.HEIGHT_SCREEN = dm.heightPixels;

		Rect frame = new Rect();
		activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		HEIGHT_STATUS_BAR = frame.top;

		CommonUtil.log("WIDTH_SCREEN = " + CommonUtil.WIDTH_SCREEN);
		CommonUtil.log("HEIGHT_SCREEN = " + CommonUtil.HEIGHT_SCREEN);
		CommonUtil.log("HEIGHT_STATUS_BAR = " + CommonUtil.HEIGHT_STATUS_BAR);
	}

	/**
	 * 取终端 IMEI , IMSI ICCID MSISDN 参数
	 * 
	 * @param context
	 * @return
	 */
	public static String getIMEI(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		String imei = tm.getDeviceId();
		String mac = "";
		String uuid = "";
		// 如果取不到Imei,则取本机MAC地址
		if (imei == null || imei.length() < 13) {
			mac = getMacAddress(context);
			imei = mac;
			if (TextUtils.isEmpty(mac) || mac.trim().length() <= 0) {
				// 如果取不到本机MAC地址,则取UUID
				uuid = getUUID(context);
				imei = uuid;
			}
		}

		CommonUtil.logE("========================imei:" + imei);
		CommonUtil.logE("========================mac:" + mac);
		CommonUtil.logE("========================uuid:" + uuid);
		return imei;

	}

	/**
	 * 获取手机mac地址<br/>
	 */
	public static String getMacAddress(Context context) {
		// 获取mac地址：
		String macAddress = "";
		try {

			WifiManager wifiMgr = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = (null == wifiMgr ? null : wifiMgr
					.getConnectionInfo());
			if (null != info) {
				if (!TextUtils.isEmpty(info.getMacAddress()))
					macAddress = info.getMacAddress().replace(":", "");
				else
					return macAddress;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return macAddress;
		}

		CommonUtil.logE("获取手机mac地址:" + macAddress);
		return macAddress;
	}

	/**
	 * 获取设备的mac地址
	 * 
	 * @param ac
	 * @param callback
	 *            成功获取到mac地址之后会回调此方法
	 */
	public static void getMacAddress(final Activity ac,
			final SimpleCallback callback) {
		final WifiManager wm = (WifiManager) ac
				.getSystemService(Service.WIFI_SERVICE);

		// 如果本次开机后打开过WIFI，则能够直接获取到mac信息。立刻返回数据。
		WifiInfo info = wm.getConnectionInfo();
		if (info != null && info.getMacAddress() != null) {
			if (callback != null) {
				callback.onComplete(info.getMacAddress());
			}
			return;
		}

		// 尝试打开WIFI，并获取mac地址
		if (!wm.isWifiEnabled()) {
			wm.setWifiEnabled(true);
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				int tryCount = 0;
				final int MAX_COUNT = 10;

				while (tryCount < MAX_COUNT) {
					final WifiInfo info = wm.getConnectionInfo();
					if (info != null && info.getMacAddress() != null) {
						if (callback != null) {
							ac.runOnUiThread(new Runnable() {
								@Override
								public void run() {
									callback.onComplete(info.getMacAddress());
								}
							});
						}
						return;
					}

					SystemClock.sleep(300);
					tryCount++;
				}

				// 未获取到mac地址
				if (callback != null) {
					callback.onComplete(null);
				}
			}
		}).start();
	}

	/**
	 * 获取设备UUID
	 */
	private static String sID = null;

	private static final String INSTALLATION = "INSTALLATION-"
			+ UUID.nameUUIDFromBytes("androidkit".getBytes());

	/**
	 * 
	 * 返回该设备在此程序上的唯一标识符。
	 * 
	 * @param context
	 * 
	 *            Context对象。
	 * 
	 * @return 表示该设备在此程序上的唯一标识符。
	 */

	public synchronized static String getUUID(Context context) {

		if (sID == null) {

			File installation = new File(context.getFilesDir(), INSTALLATION);

			try {

				if (!installation.exists()) {

					writeInstallationFile(context, installation);

				}

				sID = readInstallationFile(installation);

			} catch (IOException e) {

				e.printStackTrace();

			}

		}

		return sID;

	}

	/**
	 * 
	 * 将表示此设备在该程序上的唯一标识符写入程序文件系统中。
	 * 
	 * @param installation
	 * 
	 *            保存唯一标识符的File对象。
	 * 
	 * @return 唯一标识符。
	 * 
	 * @throws IOException
	 * 
	 *             IO异常。
	 */

	private static String readInstallationFile(File installation)

	throws IOException {

		RandomAccessFile accessFile = new RandomAccessFile(installation, "r");

		byte[] bs = new byte[(int) accessFile.length()];

		accessFile.readFully(bs);

		accessFile.close();

		return new String(bs);

	}

	/**
	 * 
	 * 读出保存在程序文件系统中的表示该设备在此程序上的唯一标识符。
	 * 
	 * @param context
	 * 
	 *            Context对象。
	 * 
	 * @param installation
	 * 
	 *            保存唯一标识符的File对象。
	 * 
	 * @throws IOException
	 * 
	 *             IO异常。
	 */

	private static void writeInstallationFile(Context context, File installation)

	throws IOException {

		FileOutputStream out = new FileOutputStream(installation);

		String uuid = UUID.nameUUIDFromBytes(

		Secure.getString(context.getContentResolver(),

		Secure.ANDROID_ID).getBytes()).toString();

		Log.i("cfuture09-androidkit", uuid);

		out.write(uuid.getBytes());

		out.close();

	}

	/**
	 * 检测是否存在活动的网络连接
	 * 
	 * @param context
	 * @return
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null)
			return false;
		NetworkInfo[] info = connectivity.getAllNetworkInfo();
		if (info == null)
			return false;
		if (info != null) {
			for (int i = 0; i < info.length; i++) {
				if (info[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 判断是否有网络连接
	 * */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 获取渠道号
	 */
	public static String getChannelCode(Context context) {
		// 推广渠道号
		return context.getResources().getString(R.string.channel);
	}
	
}