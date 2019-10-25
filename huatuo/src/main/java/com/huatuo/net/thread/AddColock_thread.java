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

/** 添加兑换券 */
public class AddColock_thread implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private HashMap<String, String> inJsonObject;
	private JSONObject outJsonObject;
	private String outMsgJson = null;
	private int outCode;

	public AddColock_thread(Context mContext, Handler mHandler, HashMap<String, String> inJsonObject) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		this.inJsonObject = inJsonObject;

	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonMap = inJsonObject;
//			Log.e("提交加减钟上传参数：injosnMap", "inJsonMap---------------->"+inJsonMap);
			HttpAgent httpAgent = new HttpAgent("order/addBeltOrder", inJsonMap, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			initRsultData(response);//不论是否有数据
			if (code == 0) {
				initRsultData(response);
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
	
	/** 返回加钟返回对象 */
	public JSONObject getOutAddClockObjecct() {
		return outJsonObject;
	}
	
	/** 返回加钟 错误数据数据 */
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}
}