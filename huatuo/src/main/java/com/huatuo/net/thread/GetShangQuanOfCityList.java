package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.JsonUtil;

import android.content.Context;
import android.os.Handler;

public class GetShangQuanOfCityList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outJsonObject;
	private HashMap<String, String> injsonMap;
	private String outMsgJson = null;
	private int outCode;

	public GetShangQuanOfCityList(Context mContext, Handler mHandler,HashMap<String, String> injosnMap) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.injsonMap = injosnMap;

	}

	@Override
	public void run() {
		try {
			
			CommonUtil.log("获取城市商圈提交 上传参数：injosnMap--------------->"+injsonMap);

			HttpAgent httpAgent = new HttpAgent("cityarea/business", injsonMap, mContext);
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
	
	public JSONObject getOuObjecct() {
		return outJsonObject;
	}
	
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}
}