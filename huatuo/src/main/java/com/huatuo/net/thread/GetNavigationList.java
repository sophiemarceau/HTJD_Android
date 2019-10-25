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
public class GetNavigationList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> navigationList;
	private String pageNum;
	private String pageSize;
	private String outMsgJson = null;
	private JSONObject outJsonObject;
	private HashMap<String, String> map;
	private int outCode;

	public GetNavigationList(Context mContext, Handler mHandler, HashMap<String, String> map) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		navigationList = new ArrayList<JSONObject>();
		this.map = map;
	}

	@Override
	public void run() {
		try {
			CommonUtil.log("获取导航列表上传参数：map："+map);
			HttpAgent httpAgent = new HttpAgent("navigation/navigationList",
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

		JSONArray orderListArray = bodyJsonObject.optJSONArray("navigationList");
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
				navigationList.add(json);
			}
		}
	}
	public JSONObject getOutJson(){
		return outJsonObject;
	}


	/** 返回导航列表 */
	public ArrayList<JSONObject> getNavigationList() {
		return navigationList;
	}
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}
}