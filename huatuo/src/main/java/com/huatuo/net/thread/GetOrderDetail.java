package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

import android.content.Context;
import android.os.Handler;

public class GetOrderDetail implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private String orderID;
	private JSONObject outJson;

	public GetOrderDetail(Context mContext, Handler mHandler, String orderID) {

		this.mContext = mContext;
		this.mHandler = mHandler;
		this.orderID = orderID;
	}

	
	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("userID", MyApplication.getUserID());
			inJsonObject.put("orderID", orderID);

			HttpAgent httpAgent = new HttpAgent("publicorder/userOrderDetail", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();

			if (code == 0) {
				initRsultData(response);
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
		outJson = actionRespons.getRsbody();
	}

	/** 返回订单列表 */
	public JSONObject getOutJson() {
		return outJson;
	}

}
