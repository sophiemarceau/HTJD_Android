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
import android.util.Log;

public class GetTechnicianListBySelect_Thread implements Runnable {
	private Handler mHandler;
	private Context mContext;
	// private ArrayList<JSONObject> technicianList;
	// private ArrayList<String> levelList;
	private JSONObject inJson;
	private HashMap<String, String> inJsonMap;
	private JSONObject outJson;
	private ArrayList<JSONObject> technicianList;
	private String outMsgJson = null;
	private int outCode;

	/**
	 * @param orderByType
	 *            排序方式，默认1;1 正序 ,-1 倒序
	 * @param orderByKey
	 *            1:按成单;2:按年龄;3:按距离 空表示按添加时间
	 * @param city
	 *            城市
	 * @param skillGrade
	 *            等级，默认空，空表示全部
	 * */
	public GetTechnicianListBySelect_Thread(Context mContext, Handler mHandler,
			HashMap<String, String> inJson) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.outJson = new JSONObject();
		this.inJsonMap = inJson;
	}

	@Override
	public void run() {
		try {
			// JSONObject inJsonObject = new
			// JSONObject();getStoreSkillListByServID

			HashMap<String, String> inJsonObject = inJsonMap;
			CommonUtil.logE("获取预约技师列表列表的提交参数：------------->" + inJsonObject);

			HttpAgent httpAgent = new HttpAgent(
					"publicorder/storeSkillListByServID", inJsonMap, mContext);
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

	/**
	 * 初始化返回结果数据
	 * */
	private void initRsultData(ActionResponse actionRespons) {
		if (technicianList == null) {
			technicianList = new ArrayList<JSONObject>();
		}

		if (technicianList.size() != 0) {
			technicianList.clear();
		}
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outJson = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
		if (null != bodyJsonObject) {
			// 技师列表
			JSONArray skillWorkerListArray = bodyJsonObject
					.optJSONArray("skillList");
			JSONObject json;
			if (skillWorkerListArray == null
					|| skillWorkerListArray.length() == 0) {
				return;
			} else {
				for (int i = 0; i < skillWorkerListArray.length(); i++) {
					try {
						json = skillWorkerListArray.getJSONObject(i);
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
					technicianList.add(json);
				}
			}
		}

	}

	/**
	 * 返回技师列表
	 * */
	public ArrayList<JSONObject> getTechnicianList() {
		return technicianList;
	}

	public String getOutMsg() {
		return outMsgJson;
	}
	
	public JSONObject getOutJsonObj() {
		return outJson;
	}


	public int getOutCode() {
		return outCode;
	}

}