package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

public class GetMingXiList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> mingXiList;
	private String pageStart;
	private String pageOffset;
	private JSONObject outObj;

	public GetMingXiList(Context mContext, Handler mHandler,String pageStart,String pageOffset) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		mingXiList = new ArrayList<JSONObject>();
		this.pageStart = pageStart;
		this.pageOffset = pageOffset;
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("pageStart", pageStart);
			inJsonObject.put("pageOffset", pageOffset);
			
			HttpAgent httpAgent = new HttpAgent("user/bill/get", inJsonObject, mContext);
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

	/**
	 * 初始化返回结果数据
	 * */
	private void initRsultData(ActionResponse actionRespons) {
		outObj = actionRespons.getRsbody();

		// 技师列表
		JSONArray mingXiListArray = outObj.optJSONArray("data");
		JSONObject json;
		if (mingXiListArray == null || mingXiListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < mingXiListArray.length(); i++) {
				try {
					json = mingXiListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				mingXiList.add(json);
			}
		}
	}

	/**
	 * 返回明细列表
	 * */
	public ArrayList<JSONObject> getMingXiList() {
		return mingXiList;
	}
	
	public JSONObject getOutObj(){
		return outObj;
		
	}

}
