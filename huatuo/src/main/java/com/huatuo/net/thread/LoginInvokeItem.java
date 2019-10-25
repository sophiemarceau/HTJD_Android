package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.UmengPushUtil;

import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class LoginInvokeItem implements Runnable {
	private String Tag = "LoginInvokeItem";
	private Handler mHandler;
	private Context mContext;
	private int type;
	private String userMobile;
	private String userPassword;
	private String deviceToken;
	private String message;

	public String getMessage() {
		return message;
	}

	/**
	 * @param type
	 * */
	public LoginInvokeItem(Handler mHandler, Context mContext, String userMobile, String userPassword) {
		this.mHandler = mHandler;
		this.mContext = mContext;
		this.userMobile = userMobile;
		this.userPassword = userPassword;
	}

	@Override()
	public void run() {
		try {
//			JSONObject loginJsonObject = new JSONObject();
//			loginJsonObject.put("mobile", userMobile);
//			loginJsonObject.put("authCode", userPassword);
			
			HashMap<String, String> loginJsonObject = new HashMap<String, String>();
			loginJsonObject.put("client", "android");
			loginJsonObject.put("mobile", userMobile);
			loginJsonObject.put("authCode", userPassword);
			loginJsonObject.put("deviceID",CommonUtil.DEVICEID);
			loginJsonObject.put("deviceToken", UmengPushUtil.deviceToken);
			HttpAgent httpAgent = new HttpAgent("user/login/new", loginJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			Message message = mHandler.obtainMessage();
			message.obj = response.getMsg();
			if (code == 0) {
				JSONObject userJsonObject = response.getRsbody();
				String codestr =userJsonObject.getString("code");
				if(codestr.equals("0000")){
					String userID = userJsonObject.optString("userID", "");
					String mobile = userJsonObject.optString("mobile", "");
					MyApplication.setLoginFlag(true);
					MyApplication.setUserID(userID);
					MyApplication.setUserMobile(mobile);
					MyApplication.setUserJSON(userJsonObject);
				}
				message.what = MsgId.LOGIN_S;
				mHandler.sendMessage(message);
			} else if (code == MsgId.NET_NOT_CONNECT) {
				mHandler.sendEmptyMessage(MsgId.NET_NOT_CONNECT);
			} else {
				message.what = MsgId.LOGIN_F;
				mHandler.sendMessage(message);
			}
		} catch (Exception e) {
			// LogUtil.e(Tag, "LoginInvokeItem run" + e.getMessage());
			e.printStackTrace();
		}
	}

}