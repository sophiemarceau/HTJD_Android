package com.huatuo.net.thread;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class GetHomePageData implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> advertisementList;
	private ArrayList<JSONObject> recommendlist;

	public GetHomePageData(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		advertisementList = new ArrayList<JSONObject>();
		recommendlist = new ArrayList<JSONObject>();
	}

	@Override
	public void run() {
		try {
			JSONObject jsonObject = new JSONObject();

			HttpAgent httpAgent = new HttpAgent("homePage_GetData", jsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();

			Message message = mHandler.obtainMessage();
			message.obj = response.getMsg();
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

		// 广告列表
		// jsonArray2List
		JsonUtil.jsonArray2List(bodyJsonObject.optJSONArray("advertisementList"), advertisementList);
		// 推荐
		JsonUtil.jsonArray2List(bodyJsonObject.optJSONArray("recommendlist"), recommendlist);

	}

	/**
	 * 返回广告列表
	 * */
	public ArrayList<JSONObject> getAdvertisementList() {
		return advertisementList;
	}

	/**
	 * 推荐
	 */
	public ArrayList<JSONObject> getRecommendlist() {
		return recommendlist;
	}

}