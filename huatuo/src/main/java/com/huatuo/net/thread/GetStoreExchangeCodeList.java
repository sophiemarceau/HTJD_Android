package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

public class GetStoreExchangeCodeList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> exchangeCoderList;
//	private ArrayList<JSONObject> unusedCoderList;
//	private ArrayList<JSONObject> usedCoderList;
	private String state;
	private String outMsgJson = null;
	private int outCode;

	public GetStoreExchangeCodeList(Context mContext, Handler mHandler, String state) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		exchangeCoderList = new ArrayList<JSONObject>();
		this.state = state;
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("state", state);
			HttpAgent httpAgent = new HttpAgent("storeorder/ecode/get", inJsonObject, mContext);
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
			// LogUtil.e(Tag, "LoginInvokeItem run" + e.getMessage());
			e.printStackTrace();
		}
	}

	/** 初始化返回结果数据 */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();

		// 消费码列表
		JSONArray Array = bodyJsonObject.optJSONArray("exchangeCoderList");
		JSONObject json;
		if (Array == null || Array.length() == 0) {
			return;
		} else {
			for (int i = 0; i < Array.length(); i++) {
				try {
					json = Array.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				exchangeCoderList.add(json);
//				String state = json.optString("state", "");
//				if ("2".equals(state)) {
//					unusedCoderList.add(json);
//				}else if ("3".equals(state)) {
//					usedCoderList.add(json);
//				}
			}
		}

	}

	/** 返回消费码列表 */
	public ArrayList<JSONObject> getExchangeCoderList() {
		return exchangeCoderList;
	}
	
//	/** 返回已使用消费码列表 */
//	public ArrayList<JSONObject> getUsedCoderList() {
//		return usedCoderList;
//	}
//	
//	/** 返回未使用消费码列表 */
//	public ArrayList<JSONObject> getUnusedCoderList() {
//		return unusedCoderList;
//	}

	public String getOutJson() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}
}
