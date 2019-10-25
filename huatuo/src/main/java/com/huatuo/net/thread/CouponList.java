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

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/** 获取兑换券列表 */
public class CouponList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String status, serviceId, workerId;
	private JSONObject outJsonObject;
	private int outCode;
	private String outMsgJson = null;
	private ArrayList<JSONObject> CouponList;

	public CouponList(Context mContext, Handler mHandler, String status,
			String serviceId, String workerId) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.status = status;
		this.serviceId = serviceId;
		this.workerId = workerId;
		CouponList = new ArrayList<JSONObject>();

	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			// Log.e("CouponList",
			// "userID---------------->"+MyApplication.getUserID());
			inJsonObject.put("status", status);
			inJsonObject.put("serviceId", serviceId);
			inJsonObject.put("workerId", workerId);
			HttpAgent httpAgent = new HttpAgent("market/userCouponList",
					inJsonObject, mContext);
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
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();

		if (null != bodyJsonObject) {
			// 地址列表
			JSONArray addressListArray = bodyJsonObject
					.optJSONArray("couponList");
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
					CouponList.add(json);
				}
			}
		}
	}

	/** 返回订单列表 */
	public ArrayList<JSONObject> getCouponList() {
		return CouponList;
	}

	public String getOutMsg() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}
}