package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.JsonUtil;

public class GetStoreDetail implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String storeId;
	private JSONObject outBodyJsonObject = null;
	private String outMsgJson = null;
	private int outCode;
	private ArrayList<JSONObject> serviceList, activityList, imgList;

	public GetStoreDetail(Context mContext, Handler mHandler, String storeId) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.storeId = storeId;
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("ID", storeId);
			
			String lng = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LNG", "");
			String lat = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LAT", "");
			inJsonObject.put("longitude", lng);
			inJsonObject.put("latitude", lat);
			CommonUtil.logE("获取店铺详情的提交参数：------------->" + inJsonObject);
			HttpAgent httpAgent = new HttpAgent("store/getSysStoreDetail", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			initRsultData(response);
			if (code == 0) {
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
		outBodyJsonObject = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
	}

	public JSONObject getOutJsonObject() {
		return outBodyJsonObject;
	}

	public String getOutMsg() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}

	public ArrayList<JSONObject> getServiceList() {
		serviceList = JsonUtil.jsonArray2List(outBodyJsonObject.optJSONArray("serviceList"));
		return serviceList;
	}

	public ArrayList<JSONObject> getActivityList() {
		activityList = JsonUtil.jsonArray2List(outBodyJsonObject.optJSONArray("activityList"));
		return activityList;
	}

	public ArrayList<JSONObject> getImgList() {
		imgList = JsonUtil.jsonArray2List(outBodyJsonObject.optJSONArray("imageList"));
		return imgList;
	}

}
