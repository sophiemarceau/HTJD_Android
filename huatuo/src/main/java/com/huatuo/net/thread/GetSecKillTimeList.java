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
import com.huatuo.util.CommonUtil;
import com.huatuo.util.JsonUtil;

public class GetSecKillTimeList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String ID;
	private HashMap<String, String> inJson;
	private JSONObject outJson;
	private String outMsgJson = null;
	private int outCode;

	public GetSecKillTimeList(Context mContext, Handler mHandler,
			String ID) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.ID = ID;
	}

	@Override
	public void run() {
		try {
			if (inJson == null) {
				inJson = new HashMap<String, String>();
				inJson.put("ID", ID);
			}

			CommonUtil.logE("获取秒杀时间轴提交参数inJson：" + inJson);
			HttpAgent httpAgent = new HttpAgent("spike/queryTimeZoneList",
					inJson, mContext);
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
		JSONObject bodyJsonObject = actionRespons.getRsbody();

		outJson = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
	}
	public ArrayList<JSONObject> getList() {
		if (outJson != null) {
			JSONArray adListArray = outJson.optJSONArray("timeZoneList");
			JSONObject json_adObj;
			ArrayList<JSONObject> adList = new ArrayList<JSONObject>();
			if (CommonUtil.NoEmptyArray(adListArray)) {
				for (int i = 0; i < adListArray.length(); i++) {
					try {
						json_adObj = adListArray.getJSONObject(i);
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
					adList.add(json_adObj);
				}
			}
			return adList;
		}
		return null;
	}
	/**
	 * inJson
	 * */
	public void setinJson(JSONObject ij) {
		HashMap<String, String> map = JsonUtil.Json2Map(ij);
		this.inJson = map;
	}

	/**
	 * Out
	 */
	public JSONObject getOutJson() {
		return this.outJson;
	}

	public String getOutMsg() {
		return outMsgJson;
	}

	public int getOutCode() {
		return outCode;
	}
}
