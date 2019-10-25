package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

public class Feedback implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outJson;
	private String context;
	private String outMsg = null;

	public Feedback(Context mContext, Handler mHandler, String context) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.context = context;
	}
	
	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("context", context);
			HttpAgent httpAgent = new HttpAgent("user/feedback/add", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			initRsultData(response);
			int code = response.getCode();
			if (code == 0) {
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

	/** 获取json对象 */
	private void initRsultData(ActionResponse actionRespons) {
		outJson = actionRespons.getRsbody();
		outMsg = actionRespons.getMsg();
	}

	/** 返回josn对象 */
	public JSONObject getOutJson() {
		return outJson;
	}

	public String getOutMsg() {
		return outMsg;
	}

}

