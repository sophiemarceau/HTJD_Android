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
import com.huatuo.util.UmengPushUtil;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/** 获取兑换券列表 */
public class CommitOrder_Thread_Store implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outBodyJsonObject = null;
	private String outMsgJson = null;
	private int outCode;
	private HashMap<String, String> injsonMap;

	public CommitOrder_Thread_Store(Context mContext, Handler mHandler,HashMap<String, String> injosnMap) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.injsonMap = injosnMap;

	}

	@Override
	public void run() {
		try {
			injsonMap.put("deviceToken", UmengPushUtil.deviceToken);
			CommonUtil.logE("提交订单上传参数：injosnMap---------------->"+injsonMap);
			HttpAgent httpAgent = new HttpAgent("publicorder/genServiceOrder", injsonMap, mContext);
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
			
			e.printStackTrace();
		}
	}

	/** 初始化返回结果数据 */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outBodyJsonObject = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
	}
	
	

	/** 返回提交订单后数据 */
	public JSONObject getOutJsonObject() {
		return outBodyJsonObject;
	}
	
//	/** 返回提交订单后错误数据数据 */
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}

}