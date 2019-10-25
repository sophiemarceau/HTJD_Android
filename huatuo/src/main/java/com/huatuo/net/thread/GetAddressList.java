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
import com.huatuo.util.LogUtil;

import android.content.Context;
import android.os.Handler;

/** 获取地址列表 */
public class GetAddressList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> addressList;

	// private int isDefault;

	public GetAddressList(Context mContext, Handler mHandler/* , int isDefault */) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		addressList = new ArrayList<JSONObject>();
		// this.isDefault = isDefault;

	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("isDefault", "1");

			HttpAgent httpAgent = new HttpAgent("user/userAddress",
					inJsonObject, mContext);
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

	/** 初始化返回结果数据 */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();

		if (null != bodyJsonObject) {
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
	}

	/** 返回订单列表 */
	public ArrayList<JSONObject> getAddressList() {
		return addressList;
	}

}