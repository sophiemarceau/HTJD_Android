package com.huatuo.net.thread;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.LogUtil;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class GetServicesListByCategoryID implements Runnable {

	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> technicianList;
	private ArrayList<String> levelList;
	private int orderByType;
	private int orderByKey;
	private String city;
	private String skillGrade;
	private String projectCategoryID;

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
	public GetServicesListByCategoryID(Context mContext, Handler mHandler, String projectCategoryID, int orderByType, int orderByKey, String city, String skillGrade) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		technicianList = new ArrayList<JSONObject>();
		levelList = new ArrayList<String>();
		this.projectCategoryID = projectCategoryID;
		this.orderByType = orderByType;
		this.orderByKey = orderByKey;
		this.city = city;
		this.skillGrade = skillGrade;
	}

	@Override
	public void run() {
		try {
			JSONObject inJsonObject = new JSONObject();
			inJsonObject.put("projectCategoryID", projectCategoryID);
			inJsonObject.put("orderByType", orderByType);
			inJsonObject.put("orderByKey", orderByKey);
			inJsonObject.put("city", city);
			// inJsonObject.put("city", "北京");
			inJsonObject.put("skillGrade", skillGrade);

			HttpAgent httpAgent = new HttpAgent("skill_getServicesListByCategoryID", inJsonObject, mContext);
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

		// 技师列表
		JSONArray skillWorkerListArray = bodyJsonObject.optJSONArray("skillWorkerList");
		JSONObject json;
		if (skillWorkerListArray == null || skillWorkerListArray.length() == 0) {
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
		// 等级列表
		JSONArray groupSkillGradeArray = bodyJsonObject.optJSONArray("groupSkillGrade");
		String levelString;
		if (groupSkillGradeArray == null || groupSkillGradeArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < groupSkillGradeArray.length(); i++) {
				try {
					levelString = groupSkillGradeArray.getString(i);
//					LogUtil.e("deesha", "levelString=" + levelString);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				levelList.add(levelString);
			}
		}
	}

	/**
	 * 返回技师列表
	 * */
	public ArrayList<JSONObject> getServicesListByCategoryID() {
		return technicianList;
	}

	/**
	 * 返回等级列表
	 * */
	public String[] getLevelArray() {
		String[] levelItems = new String[levelList.size()];
		for (int i = 0; i < levelList.size(); i++) {
			levelItems[i] = levelList.get(i);
		}
		return levelItems;
	}

	// private Handler mHandler;
	// private Context mContext;
	// private JSONObject inJsong;
	// private JSONObject outJson;
	//
	// private ArrayList<JSONObject> ServicesList;
	//
	// public GetServicesListByCategoryID(Context mContext, Handler mHandler) {
	// this.mContext = mContext;
	// this.mHandler = mHandler;
	//
	// }
	// @Override
	// public void run() {
	// // TODO Auto-generated method stub
	//
	// try {
	//
	// // JSONObject jsonObject= new JSONObject() ;
	// if (inJsong == null)
	// inJsong = new JSONObject();
	//
	// HttpAgent httpAgent = new HttpAgent("skill_getServicesListByCategoryID",
	// inJsong, mContext);
	// ActionResponse response = httpAgent.sendRequest(null);
	// int code = response.getCode();
	//
	// Message message = mHandler.obtainMessage();
	// message.obj = response.getMsg();
	//
	// if (code == 0) {
	// initRsultData(response);
	// mHandler.sendEmptyMessage(MsgId.DOWN_DATA_S);
	// } else if (code == MsgId.NET_NOT_CONNECT) {
	// mHandler.sendEmptyMessage(MsgId.NET_NOT_CONNECT);
	// } else {
	// mHandler.sendEmptyMessage(MsgId.DOWN_DATA_F);
	// }
	// } catch (Exception e) {
	// // LogUtil.e(Tag, "LoginInvokeItem run" + e.getMessage());
	// e.printStackTrace();
	// }
	//
	// }
	//
	// /**
	// * 初始化返回结果数据
	// * */
	// private void initRsultData(ActionResponse actionRespons) {
	// JSONObject bodyJsonObject = actionRespons.getRsbody();
	//
	// outJson = bodyJsonObject;
	//
	// }
	//
	// /**
	// * inJson
	// * */
	// public void setInJsong(JSONObject ij) {
	// this.inJsong = ij;
	// }
	//
	// /**
	// * Out
	// */
	// public JSONObject getOutJson() {
	// return this.outJson;
	// }

}
