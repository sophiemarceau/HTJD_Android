package com.huatuo.net.thread;

import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.JsonUtil;

public class BaseNetPort implements Runnable {
	private Handler mHandler;
	private Context mContext;
//	private JSONObject inJsong;
	private HashMap<String, String> inJsong;
	private JSONObject outJson;
	private String ServerName;

	public BaseNetPort(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;
	}

	@Override
	public void run() {
		try {

			// JSONObject jsonObject= new JSONObject() ;
			/*if (inJsong == null)
				inJsong = new JSONObject();*/
			if (inJsong == null) {
				inJsong = new HashMap<String, String>();
			}

			HttpAgent httpAgent = new HttpAgent(ServerName, inJsong, mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();

			Message message = mHandler.obtainMessage();
			message.obj = response.getMsg();

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

	/**
	 * 初始化返回结果数据
	 * */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();

		outJson = bodyJsonObject;

	}

	/**
	 * inJson
	 * */
	public void setInJsong(JSONObject ij){
		// 设置Json 将Json转换为map
		this.inJsong = JsonUtil.Json2Map2(ij);
	}
	
	
	/**
	 * Out
	 */
	public JSONObject getOutJson() {
		return this.outJson;
	}

	public String getServerName() {
		return ServerName;
	}

	public void setServerName(String serverName) {
		ServerName = serverName;
	}

}