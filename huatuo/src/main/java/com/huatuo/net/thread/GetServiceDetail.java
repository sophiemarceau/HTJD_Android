package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.JsonUtil;

public class GetServiceDetail implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String servID;
	private String skillWorkerID;
	private HashMap<String, String> inJson;
	private JSONObject outJson;

	/**
	 * @param servID
	 *            服务ID
	 * @param servBelong
	 *            服务归属 0:自营,1：门店
	 * */
	public GetServiceDetail(Context mContext, Handler mHandler, String servID,String skillWorkerID) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.servID = servID;
		this.skillWorkerID = skillWorkerID;
	}

	@Override
	public void run() {
		try {
			if (inJson == null) {
				inJson = new HashMap<String, String>();
				inJson.put("ID", servID);
				
				String lng = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LNG", "");
				String lat = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LAT", "");
				inJson.put("longitude", lng);
				inJson.put("latitude", lat);
				
				inJson.put("userID", MyApplication.getUserID() );
				inJson.put("skillWorkerID", skillWorkerID );
			}

			CommonUtil.logE("获取项目详情请求参数：inJson："+inJson);
			HttpAgent httpAgent = new HttpAgent("serv/getSysServiceDetail", inJson, mContext);
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

		outJson = bodyJsonObject;

	}

	/**
	 * inJson
	 * */
	public void setInJson(JSONObject ij) {
		HashMap<String, String> map = JsonUtil.Json2Map(ij);
		this.inJson = map;
	}

	/**
	 * Out
	 */
	public JSONObject getOutJson() {
		return this.outJson;
	}

}