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

public class GetSkillEvaluateList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> tagList;
	private ArrayList<JSONObject> skillEvaluateList;
	private String skillWorkerID;
	private String pageNum;
	private String pageSize;
	private JSONObject outBodyJsonObject = null;
	private HashMap<String, String> inJsonObject = new HashMap<String, String>();

	public GetSkillEvaluateList(Context mContext, Handler mHandler,
			HashMap<String, String> inJsonObject) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.inJsonObject = inJsonObject;
		this.skillEvaluateList = new ArrayList<JSONObject>();
		this.tagList = new ArrayList<JSONObject>();
	}

	@Override
	public void run() {
		try {

			CommonUtil.logE("获取评价列表：inJsonObject：" + inJsonObject);

			HttpAgent httpAgent = new HttpAgent("evaluate/evaluateList",
					inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			CommonUtil.log("获取评价列表response：" + response);
			int code = response.getCode();
			initRsultData(response);
			CommonUtil.log("获取评价列表code：" + code);
			if (code == 0) {
				mHandler.sendEmptyMessage(MsgId.DOWN_DATA_S);
				CommonUtil.log("获取评价列表if (code == 0) {");
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
		CommonUtil.log("获取评价列表actionRespons：" + actionRespons);
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outBodyJsonObject = bodyJsonObject;

	}

	public JSONObject getOutJsonObject() {
		return outBodyJsonObject;
	}

	/** 返回评价列表 */
	public ArrayList<JSONObject> getSkillEvaluateList() {
		// 评价列表
		if (null != outBodyJsonObject) {
			JSONArray skillEvaluateListArray = outBodyJsonObject
					.optJSONArray("evalList");
			CommonUtil.log("获取评价列表skillEvaluateListArray："
					+ skillEvaluateListArray);
			JSONObject json;
			if (CommonUtil.NoEmptyArray(skillEvaluateListArray)) {
				CommonUtil.log("获取评价列表skillEvaluateListArray.length()："
						+ skillEvaluateListArray.length());
				for (int i = 0; i < skillEvaluateListArray.length(); i++) {
					try {
						json = skillEvaluateListArray.getJSONObject(i);
						CommonUtil.log("获取评价列表json：" + json);
						skillEvaluateList.add(json);
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}
		return skillEvaluateList;
	}

	/** 返回tag列表 */
	public ArrayList<JSONObject> getTagList() {
		// 评价tag列表
		if (null != outBodyJsonObject) {
			JSONArray tagListArray = outBodyJsonObject.optJSONArray("tagList");
			JSONObject json;
			if (CommonUtil.NoEmptyArray(tagListArray)) {
				for (int i = 0; i < tagListArray.length(); i++) {
					try {
						json = tagListArray.getJSONObject(i);
					} catch (JSONException e) {
						e.printStackTrace();
						continue;
					}
					tagList.add(json);
				}
			}
		}
		return tagList;
	}

}
