package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

public class EvaluateOrder implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outJson;
	private String orderID, skillScore, projectScore, storeScore, remark, tags;
	private String outMsg = null;

	public EvaluateOrder(Context mContext, Handler mHandler, String orderID, String skillScore, String projectScore, String storeScore, String remark,
			String tags) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.orderID = orderID;
		this.skillScore = skillScore;
		this.projectScore = projectScore;
		this.storeScore = storeScore;
		this.remark = remark;
		this.tags = tags;
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("orderID", orderID);
			inJsonObject.put("skillScore", skillScore);
			inJsonObject.put("projectScore", projectScore);
			inJsonObject.put("storeScore", storeScore);
			inJsonObject.put("remark", remark);
			inJsonObject.put("tags", tags);
			Log.e("EvaluateOrder", "HashMap------------------->"+inJsonObject);
			HttpAgent httpAgent = new HttpAgent("evaluate/userEvaluate", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			initRsultData(response);
			int code = response.getCode();
			if (code == 0) {
				// initRsultData(response);
				mHandler.sendEmptyMessage(MsgId.DOWN_DATA_S);
			} else if (code == MsgId.NET_NOT_CONNECT) {
				mHandler.sendEmptyMessage(MsgId.NET_NOT_CONNECT);
			} else {
				mHandler.sendEmptyMessage(MsgId.DOWN_DATA_F);
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/** 获取json对象 */
	private void initRsultData(ActionResponse actionRespons) {
		outJson = actionRespons.getRsbody();
		// Log.e("EvaluateOrder", "outJson------------>" + outJson);
		outMsg = actionRespons.getMsg();
	}

	/** 返回josn对象 */
	public JSONObject getOutJson() {
		// Log.e("EvaluateOrder", "getOutJson().outJson------------>" +
		// outJson);
		return outJson;
	}

	public String getOutMsg() {
		return outMsg;
	}

}
