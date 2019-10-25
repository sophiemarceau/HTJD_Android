package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.JsonUtil;

import android.content.Context;
import android.os.Handler;

public class GetCityList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> cityList;
	private HashMap<String, String> obj = null;
	private JSONObject outObj = null;

	public GetCityList(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		cityList = new ArrayList<JSONObject>();
	}

	@Override
	public void run() {
		try {
			obj = new HashMap<String, String>();
			obj.put("orderBy", "1");
			HttpAgent httpAgent = new HttpAgent("cms/getCityList", obj, mContext);
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
		outObj = bodyJsonObject;

		// 城市列表
		JSONArray cityListArray = bodyJsonObject.optJSONArray("cityList");
		JSONObject json;
		if (cityListArray == null || cityListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < cityListArray.length(); i++) {
				try {
					json = cityListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				cityList.add(json);
			}
		}
	}

	/**
	 * 返回商品列表list
	 * */
	public ArrayList<JSONObject> getCityList() {
		return cityList;
	}

	/**
	 * 返回商品列表obj
	 * */
	public JSONObject getOutObj() {
		return outObj;
	}
}