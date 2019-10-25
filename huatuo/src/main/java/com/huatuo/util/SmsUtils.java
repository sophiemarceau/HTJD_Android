package com.huatuo.util;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.SmsManager;

/**
 * 发送短信的工具类
 * 
 * @author ftc
 * 
 */
public class SmsUtils {
	/**
	 * 调用系统发送短信页面
	 * 
	 * @param smsMobile
	 * @param smsBody
	 * @param context
	 */
	public static void showSendSmsView(String smsMobile, String smsBody, Context context) {
		Uri smsToUri = Uri.parse("smsto:" + smsMobile);
		Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", smsBody);
		context.startActivity(intent);
	}

	/**
	 * 调用系统发送短信页面
	 * 
	 * @param smsBody
	 * @param context
	 */
	public static void showSendSmsView(String smsBody, Context context) {
		Uri smsToUri = Uri.parse("smsto:");
		Intent intent = new Intent(android.content.Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", smsBody);
		context.startActivity(intent);
	}

	/**
	 * 后台发送短信
	 * 
	 * @param mobile
	 * @param msg
	 */
	public static void sendSmsMsg(String mobile, String msg) {
		try {
			SmsManager manager = SmsManager.getDefault();
			List<String> list = manager.divideMessage(msg);
			for (int i = 0; i < list.size(); i++) {
				manager.sendTextMessage(mobile, null, list.get(i), null, null);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}