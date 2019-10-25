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

/** 获取订单列表 */
public class GetAD_Detail_List implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> arrayList;
	private String pageNum; 
	private String pageSize;
	private String outMsgJson = null;
	private JSONObject outJsonObject;
	private HashMap<String, String> map;
	private int outCode;

	public GetAD_Detail_List(Context mContext, Handler mHandler, HashMap<String, String> map) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		arrayList = new ArrayList<JSONObject>();
		this.map = map;
	}

	@Override
	public void run() {
		try {
			CommonUtil.log("获取广告详情列表上传参数：map："+map);
			HttpAgent httpAgent = new HttpAgent("commercial/adList",
					map, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			initRsultData(response);
			if (code == 0) {
//				initRsultData(response);
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
		outJsonObject = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();

		JSONArray orderListArray = bodyJsonObject.optJSONArray("adList");
		JSONObject json;
		if (orderListArray == null || orderListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < orderListArray.length(); i++) {
				try {
					json = orderListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				arrayList.add(json);
			}
		}
	}
	public JSONObject getOutJson(){
		return outJsonObject;
	}


	public ArrayList<JSONObject> getStoreList() {
		JSONArray storeListArray = outJsonObject.optJSONArray("storeList");
		JSONObject json_storeObj;
		ArrayList<JSONObject> storeList = new ArrayList<JSONObject>();
		if (CommonUtil.NoEmptyArray(storeListArray)) {
			for (int i = 0; i < storeListArray.length(); i++) {
				try {
					json_storeObj = storeListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				storeList.add(json_storeObj);
			}
		}
		return storeList;
	}

	public ArrayList<JSONObject> getServiceList() {
		JSONArray serviceListArray = outJsonObject.optJSONArray("serviceList");
		JSONObject json_serviceObj;
		ArrayList<JSONObject> serviceList = new ArrayList<JSONObject>();
		if (CommonUtil.NoEmptyArray(serviceListArray)) {
			for (int i = 0; i < serviceListArray.length(); i++) {
				try {
					json_serviceObj = serviceListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				serviceList.add(json_serviceObj);
			}
		}
		return serviceList;
	}

	public ArrayList<JSONObject> getWorkerList() {
		JSONArray workerListArray = outJsonObject.optJSONArray("skillList");
		JSONObject json_workerObj;
		ArrayList<JSONObject> workerList = new ArrayList<JSONObject>();
		if (CommonUtil.NoEmptyArray(workerListArray)) {
			for (int i = 0; i < workerListArray.length(); i++) {
				try {
					json_workerObj = workerListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				workerList.add(json_workerObj);
			}
		}
		return workerList;
	}
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}
}