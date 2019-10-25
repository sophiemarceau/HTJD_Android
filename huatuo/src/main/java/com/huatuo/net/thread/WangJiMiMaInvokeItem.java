package com.huatuo.net.thread;

import org.json.JSONObject;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.DateUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class WangJiMiMaInvokeItem implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String mobileNo, identifyingCode, password;

	public WangJiMiMaInvokeItem(Handler mHandler, Context mContext, String mobileNo, String identifyingCode, String password) {
		this.mHandler = mHandler;
		this.mContext = mContext;
		this.mobileNo = mobileNo;
		this.identifyingCode = identifyingCode;
		this.password = password;
	}

	@Override
	public void run() {
		try {
			JSONObject registerJsonObject = new JSONObject();
			registerJsonObject.put("mobileNo", mobileNo);
			registerJsonObject.put("captcha", identifyingCode);
			registerJsonObject.put("password", password);

			HttpAgent httpAgent = new HttpAgent("user_SetPassword", registerJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);

			int code = response.getCode();
			Message message = mHandler.obtainMessage();
			if (code == 0) {
				JSONObject userJsonObject = response.getRsbody();
				userJsonObject.put("mobileNo", mobileNo);
				userJsonObject.put("useServiceNumber", "0");
				userJsonObject.put("addTime", DateUtil.getDateTimeNow());
				MyApplication.setLoginFlag(true);
				MyApplication.setUserJSON(userJsonObject);

				message.what = MsgId.REGISTER_S;
				message.obj = response.getMsg();
				mHandler.sendMessage(message);
			} else if (code == MsgId.NET_NOT_CONNECT) {
				mHandler.sendEmptyMessage(MsgId.NET_NOT_CONNECT);
			} else {
				message.what = MsgId.REGISTER_F;
				message.obj = response.getMsg();
				mHandler.sendMessage(message);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}