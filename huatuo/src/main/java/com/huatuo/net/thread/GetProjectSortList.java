package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

public class GetProjectSortList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outJsonObject;
	private ArrayList<JSONObject> projectSortList;
	private HashMap<String, String> obj = null;

	public GetProjectSortList(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		projectSortList = new ArrayList<JSONObject>();
	}

	@Override
	public void run() {
		try {
			HttpAgent httpAgent = new HttpAgent("store/getProjectSortList", obj, mContext);
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
		outJsonObject = bodyJsonObject;
		// 城市列表
		JSONArray projectSortListArray = bodyJsonObject.optJSONArray("projectSortList");
		JSONObject json;
		if (projectSortListArray == null || projectSortListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < projectSortListArray.length(); i++) {
				try {
					json = projectSortListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				projectSortList.add(json);
			}
		}
	}
	
	/**
	 * 返回商品列表
	 * */
	public JSONObject getOutJsonObject() {
		return outJsonObject;
	}

	/**
	 * 返回商品列表
	 * */
	public ArrayList<JSONObject> getProjectSortList() {
		return projectSortList;
	}
}
