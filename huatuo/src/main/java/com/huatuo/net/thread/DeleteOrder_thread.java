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

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/** 获取兑换券列表 */
public class DeleteOrder_thread implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private int outCode;
	private String outMsgJson = null;
	private JSONObject outJsonObject;
	private HashMap<String, String> injsonMap;
//	private int item_position;

	public DeleteOrder_thread(Context mContext, Handler mHandler,/*int item_position,*/HashMap<String, String> injosnMap) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.injsonMap = injosnMap;
//		this.item_position = item_position;

	}

	@Override
	public void run() {
		try {
			
//			Log.e("删除订单上传参数：injosnMap", "injosnMap---------------->"+injsonMap);

			HttpAgent httpAgent = new HttpAgent("order/deleteOrder", injsonMap, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			initRsultData(response);
			if (code == 0) {
//				initRsultData(response);
//			   item_position
//			CommonUtil.log("=====================================item_position:"+item_position);
			   Message message = mHandler.obtainMessage();
//			   message.obj = item_position;
			   message.what = MsgId.DOWN_DATA_S;
			   mHandler.sendMessage(message);
//				mHandler.sendEmptyMessage(MsgId.DOWN_DATA_S);
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

	
	/** 返回对象 */
	public JSONObject getCouponObjecct() {
		return outJsonObject;
	}
	
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}

}