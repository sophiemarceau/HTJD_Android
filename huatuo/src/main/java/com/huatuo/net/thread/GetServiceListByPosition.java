package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.JsonUtil;

public class GetServiceListByPosition implements Runnable {

	private Handler mHandler;
	private Context mContext;
	private JSONObject outBodyJsonObject = null;
	private String outMsgJson = null;
	private int outCode;
	private ArrayList<JSONObject> servList;
	private HashMap<String, String> map;

	public GetServiceListByPosition(Context mContext, Handler mHandler, HashMap<String, String> map) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		servList = new ArrayList<JSONObject>();
		this.map = map;
	}

	@Override
	public void run() {
		CommonUtil.logE("获取搜索服务列表的提交参数：------------->" + map);
			HttpAgent httpAgent = new HttpAgent("search/searchServiceList", map, mContext);
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
	}

	/**
	 * 初始化返回结果数据
	 * */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outBodyJsonObject = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
		// 技师列表
		JSONArray servListArray = bodyJsonObject.optJSONArray("serviceList");
		servList = JsonUtil.jsonArray2List(servListArray);
	}

	/**
	 * 返回技师列表
	 * */
	public ArrayList<JSONObject> getServiceList() {
		return servList;
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

}
