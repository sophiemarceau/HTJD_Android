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

/** 获取订单列表 */
public class GetQuickPayList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String pageNum;
	private String pageSize;
	private String outMsgJson = null;
	private JSONObject outJsonObject;
	private HashMap<String, String> map;
	private int outCode;

	public GetQuickPayList(Context mContext, Handler mHandler, HashMap<String, String> map) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.map = map;
	}

	@Override
	public void run() {
		try {
			CommonUtil.log("查询闪付营销活动：map：" + map);
			HttpAgent httpAgent = new HttpAgent("activity/getFastPayActivity", map, mContext);
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
	}

	public JSONObject getOutJson() {
		return outJsonObject;
	}
	/**
	 * 门店活动列表
	 * @return
	 */
	public ArrayList<JSONObject> getStoreActList() {
		JSONArray storeActListArray = outJsonObject.optJSONArray("storeActList");
		JSONObject json_storeObj;
		ArrayList<JSONObject> storeActList = new ArrayList<JSONObject>();
		if (CommonUtil.NoEmptyArray(storeActListArray)) {
			for (int i = 0; i < storeActListArray.length(); i++) {
				try {
					json_storeObj = storeActListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				storeActList.add(json_storeObj);
			}
		}
		return storeActList;
	}
	
	/**
	 * 平台活动列表
	 * @return
	 */
	public ArrayList<JSONObject> getPlatActList() {
		JSONArray platActListArray = outJsonObject.optJSONArray("platActList");
		JSONObject json_serviceObj;
		ArrayList<JSONObject>  platActList = new ArrayList<JSONObject>();
		if (CommonUtil.NoEmptyArray(platActListArray)) {
			for (int i = 0; i < platActListArray.length(); i++) {
				try {
					json_serviceObj = platActListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				 platActList.add(json_serviceObj);
			}
		}
		return  platActList;
	}

	public String getOutMsg() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}
}