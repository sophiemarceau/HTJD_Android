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

public class GetSkillWorkerInfo implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String skillWorkerID;
	private int isGetProject, isEvaluate;
	private HashMap<String, String> inJson;
	private JSONObject outJson;
	private String outMsgJson = null;
	private int outCode;


	/**
	 * @param skillWorkerID
	 *            技师id
	 * @param isGetProject
	 *            是否需要同时返回服务列表,1 需要，0 不需要，默认0
	 * @param isEvaluate
	 *            是否需要同时返回评论信息,1 需要，0 不需要，默认0
	 * */
	public GetSkillWorkerInfo(Context mContext, Handler mHandler, String skillWorkerID) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.skillWorkerID = skillWorkerID;
	}

	@Override
	public void run() {
		try {
			if (inJson == null) {
				inJson = new HashMap<String, String>();
				inJson.put("ID", skillWorkerID);
				String lng = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LNG", "");
				String lat = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LAT", "");
				inJson.put("longitude", lng);
				inJson.put("latitude", lat);
				inJson.put("userID", MyApplication.getUserID());
			}

			CommonUtil.logE("获取技师详情提交参数inJson："+inJson);
			HttpAgent httpAgent = new HttpAgent("skill/getSysSkillDetailInfo", inJson, mContext);
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

