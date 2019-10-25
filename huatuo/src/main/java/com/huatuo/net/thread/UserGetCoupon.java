package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;

public class UserGetCoupon implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String activityID;
	private JSONObject outJson;
	private String outMsg = null;

	public UserGetCoupon(Context mContext, Handler mHandler, String activityID) {
		this.activityID = activityID;
		this.mContext = mContext;
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("activityID", activityID);
			CommonUtil.logE(inJsonObject + "");
			HttpAgent httpAgent = new HttpAgent("market/userGetCoupon", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			initRsultData(response);
			if (code == 0) {
				// initRsultData(response);
				mHandler.sendEmptyMessage(MsgId.DOWN_DATA_S);
			} else if (code == MsgId.NET_NOT_CONNECT) {
				mHandler.sendEmptyMessage(MsgId.NET_NOT_CONNECT);
			} else {
				mHandler.sendEmptyMessage(MsgId.DOWN_DATA_F);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 初始化返回结果数据 */
	private void initRsultData(ActionResponse actionRespons) {
		outJson = actionRespons.getRsbody();
		outMsg = actionRespons.getMsg();
	}

	public String getOutMsg() {
		return outMsg;
	}

	public JSONObject getOutJson() {
		return outJson;
	}

}
