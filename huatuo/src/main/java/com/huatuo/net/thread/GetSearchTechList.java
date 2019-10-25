package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.JsonUtil;

public class GetSearchTechList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outBodyJsonObject = null;
	private String outMsgJson = null;
	private int outCode;
	private HashMap<String, String> inJsonObject;
	private ArrayList<JSONObject> techList;

	public GetSearchTechList(Context mContext, Handler mHandler, HashMap<String, String> inJsonObject) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.inJsonObject = inJsonObject;
		this.techList = new ArrayList<JSONObject>();
	}

	@Override
	public void run() {
		try {

			CommonUtil.logE("获取搜索技師列表的提交参数：------------->" + inJsonObject);

			HttpAgent httpAgent = new HttpAgent("search/searchSkillerList", inJsonObject, mContext);
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
		techList = JsonUtil.jsonArray2List(bodyJsonObject.optJSONArray("skillList"));
	}

	public ArrayList<JSONObject> getTechList() {
		return techList;
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
