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
public class GetNavigationContent implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String pageNum;
	private String pageSize;
	private String outMsgJson = null;
	private JSONObject outJsonObject;
	private HashMap<String, String> map;
	private int outCode;

	public GetNavigationContent(Context mContext, Handler mHandler,
			HashMap<String, String> map) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.map = map;
	}

	@Override
	public void run() {
		try {
			CommonUtil.log("获取导航内容上传参数：map：" + map);
			HttpAgent httpAgent = new HttpAgent("publicorder/getNavContent",
					map, mContext);
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

	/** 初始化返回结果数据 */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outJsonObject = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
	}

	public JSONObject getOutJson() {
		return outJsonObject;
	}

	public ArrayList<JSONObject> getAdList() {
		JSONArray adListArray = outJsonObject.optJSONArray("adList");
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

	public ArrayList<JSONObject> getStoreList() {
		ArrayList<JSONObject> storeList = new ArrayList<JSONObject>();
		if (null != outJsonObject) {
			JSONArray storeListArray = outJsonObject.optJSONArray("storeList");
			JSONObject json_storeObj;

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
		}
		return storeList;
	}

	public ArrayList<JSONObject> getServiceList() {
		ArrayList<JSONObject> serviceList = new ArrayList<JSONObject>();
		if (null != outJsonObject) {
			JSONArray serviceListArray = outJsonObject
					.optJSONArray("serviceList");
			JSONObject json_serviceObj;

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
		}
		return serviceList;
	}

	public ArrayList<JSONObject> getWorkerList() {
		ArrayList<JSONObject> workerList = new ArrayList<JSONObject>();
		if (null != outJsonObject) {
			JSONArray workerListArray = outJsonObject
					.optJSONArray("skillList");
			JSONObject json_workerObj;

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