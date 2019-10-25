package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;

public class GetAreaJson implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject outJson;

	public GetAreaJson(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		try {
			HashMap<String, String> inJsonObject = new HashMap<String, String>();

			HttpAgent httpAgent = new HttpAgent("cms/getAreaList", inJsonObject, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			CommonUtil.log("GetAreaJson---response------------" + response);
			int code = response.getCode();
//			Log.e("GetAreaJson", "code------------" + code);
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

	/** 获取json对象 */
	private void initRsultData(ActionResponse actionRespons) {
		outJson = actionRespons.getRsbody();
//		Log.e("GetAreaJson", "outJson------------" + outJson);
	}

	/** 返回josn对象 */
	public JSONObject getOutJson() {
//		Log.e("GetAreaJson", "getOutJson().outJson------------" + outJson);
		return outJson;
	}

}
