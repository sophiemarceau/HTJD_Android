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

import android.content.Context;
import android.os.Handler;

/** 获取订单列表 */
public class GetOrderList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> orderList;
	private String orderStatus;
	private String type;
	private String pageStart;
	private String pageOffset;
	private String outMsgJson = null;
	private JSONObject outJsonObject;
	private int outCode;

	public GetOrderList(Context mContext, Handler mHandler, String orderStatus, String type, String pageStart,
			String pageOffset) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		orderList = new ArrayList<JSONObject>();
		this.orderStatus = orderStatus;
		this.type = type;
		this.pageStart = pageStart;
		this.pageOffset = pageOffset;
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("orderStatus", orderStatus);
			inJsonObject.put("type", type);
			inJsonObject.put("pageStart", pageStart);
			inJsonObject.put("pageOffset", pageOffset);
			CommonUtil.logE("查询订单列表：请求参数：" + inJsonObject);
			HttpAgent httpAgent = new HttpAgent("publicorder/userOrderList", inJsonObject, mContext);
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
		outJsonObject = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
		if (null != bodyJsonObject) {
			// 订单列表
			JSONArray orderListArray = bodyJsonObject.optJSONArray("orderList");
			JSONObject json;
			if (orderListArray == null || orderListArray.length() == 0) {
				return;
			} else {
				for (int i = 0; i < orderListArray.length(); i++) {
					try {
						json = orderListArray.getJSONObject(i);
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
					orderList.add(json);
				}
			}
		}
	}

	public JSONObject getOutJson() {
		return outJsonObject;
	}

	/** 返回订单列表 */
	public ArrayList<JSONObject> getOrderList() {
		return orderList;
	}

	public String getOutMsg() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}
}