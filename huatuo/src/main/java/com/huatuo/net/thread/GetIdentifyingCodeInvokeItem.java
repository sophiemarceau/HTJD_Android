package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

import android.content.Context;
import android.os.Handler;

public class GetIdentifyingCodeInvokeItem implements Runnable {
	private Handler handler;
	private Context mContext;
	private String userMobile;
	private int actiontype;
	
	private JSONObject outBodyJsonObject = null;
	private String outMsgJson = null;
	private int outCode;

	

	public GetIdentifyingCodeInvokeItem(Handler handler, Context mContext, String userMobile, int actiontype) {
		this.handler = handler;
		this.mContext = mContext;
		this.userMobile = userMobile;
		this.actiontype = actiontype;
	}

	@Override
	public void run() {
		try {
//			JSONObject bodyJsonObject = new JSONObject();
//			bodyJsonObject.put("mobileNo", userMobile);
//			bodyJsonObject.put("actiontype", actiontype);
			
			HashMap<String, String> bodyJsonObject = new HashMap<String, String>();
			bodyJsonObject.put("mobile", userMobile);
			bodyJsonObject.put("smsType", "2");

			HttpAgent httpAgent = new HttpAgent("cms/sendSms", bodyJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);

			int code = response.getCode();
			initRsultData(response);
			if (code == 0) {
				handler.sendEmptyMessage(MsgId.GET_IDENTIFYING_CODE_S);
			} else if (code == MsgId.NET_NOT_CONNECT) {
				handler.sendEmptyMessage(MsgId.NET_NOT_CONNECT);
			} else {
				handler.sendEmptyMessage(MsgId.GET_IDENTIFYING_CODE_F);
			}
		} catch (Exception e) {
		}
	}
	
	
	/** 初始化返回结果数据 */
	private void initRsultData(ActionResponse actionRespons) {
			JSONObject bodyJsonObject = actionRespons.getRsbody();
			outBodyJsonObject = bodyJsonObject;
			outMsgJson = actionRespons.getMsg();
			outCode = actionRespons.getCode();
	}
	
	

	public JSONObject getOutJsonObject() {
		return outBodyJsonObject;
	}
	
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}

}