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

import android.content.Context;
import android.os.Handler;
import android.util.Log;

/**
 * 充值卡
 * @author Android开发工程师
 *
 */
public class GenRechargeCardOrder_thread implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outJsonObject;
	private HashMap<String, String> injsonMap;
	private String outMsgJson = null;
	private int outCode;
	public GenRechargeCardOrder_thread(Context mContext, Handler mHandler,HashMap<String, String> injosnMap) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.injsonMap = injosnMap;

	}

	@Override
	public void run() {
		try {
			
//			Log.e("获取充值卡提交 上传参数：injosnMap", "injosnMap---------------->"+injsonMap);

			HttpAgent httpAgent = new HttpAgent("order/genRechargeCardOrder", injsonMap, mContext);
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
		outJsonObject = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
//		// 地址列表
//		JSONArray addressListArray = bodyJsonObject.optJSONArray("data");
//		JSONObject json;
//		if (addressListArray == null || addressListArray.length() == 0) {
//			return;
//		} else {
//			for (int i = 0; i < addressListArray.length(); i++) {
//				try {
//					json = addressListArray.getJSONObject(i);
//				} catch (JSONException e) {
//					e.printStackTrace();
//					continue;
//				}
//				CouponList.add(json);
//			}
//		}
	}
	
	/** 返回支付返回对象 */
	public JSONObject getOutPayObjecct() {
		return outJsonObject;
	}
	
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}

}