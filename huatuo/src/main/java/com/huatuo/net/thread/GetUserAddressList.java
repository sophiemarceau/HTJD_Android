package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class GetUserAddressList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	// private ArrayList<JSONObject> technicianList;
	// private ArrayList<String> levelList;
	private JSONObject inJson;
	private HashMap<String, String> inJsonMap;
	private ArrayList<JSONObject> addressList;

	
	public GetUserAddressList(Context mContext, Handler mHandler, HashMap<String, String> inJson) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		addressList = new ArrayList<JSONObject>();
		this.inJsonMap = inJson;
	}

	@Override
	public void run() {
		try {
//			JSONObject inJsonObject = new JSONObject();
			HashMap<String, String> inJsonObject = inJsonMap;
			CommonUtil.log("GetUserAddressList----inJsonMap--------------->"+inJsonMap);

			HttpAgent httpAgent = new HttpAgent("user/address/get", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();

			if (code == 0) {
				initRsultData(response);
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

	/**
	 * 初始化返回结果数据
	 * */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();

		// 地址列表
		JSONArray addressListArray = bodyJsonObject.optJSONArray("data");
		JSONObject json;
		if (addressListArray == null || addressListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < addressListArray.length(); i++) {
				try {
					json = addressListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				addressList.add(json);
			}
		}
	}

	/**
	 * 返回地址列表
	 * */
	public ArrayList<JSONObject> getAddressList() {
		return addressList;
	}

}