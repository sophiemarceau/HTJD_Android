package com.huatuo.net.http;

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
public class RequestRunnable implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String requestUrl;
	private ArrayList<JSONObject> arrayList;
	private String outMsgJson = null;
	private JSONObject outJsonObject;
	private HashMap<String, String> map;
	private int outCode;

	public RequestRunnable(Context mContext, Handler mHandler, String requestUrl,HashMap<String, String> map) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.requestUrl = requestUrl;
		arrayList = new ArrayList<JSONObject>();
		this.map = map;
	}

	@Override
	public void run() {
		try {
			CommonUtil.logE("接口:"+requestUrl+"--上传参数map："+map);
			HttpAgent httpAgent = new HttpAgent(requestUrl,
					map, mContext);
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
			 CommonUtil.logE("ActionResponse:Exception:"+e.getMessage());
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
	public JSONObject getOutJson(){
		return outJsonObject;
	}
	
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}
}