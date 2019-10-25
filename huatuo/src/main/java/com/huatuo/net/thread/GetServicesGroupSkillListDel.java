package com.huatuo.net.thread;

import java.util.ArrayList;

import org.json.JSONObject;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class GetServicesGroupSkillListDel implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private JSONObject inJsong;
	private JSONObject outJson;

	private ArrayList<JSONObject> skillWorkerGroupList;

	public GetServicesGroupSkillListDel(Context mContext, Handler mHandler) {
		this.mContext = mContext;
		this.mHandler = mHandler;

	}

	@Override
	public void run() {
		try {

			// JSONObject jsonObject= new JSONObject() ;
			if (inJsong == null)
				inJsong = new JSONObject();

			HttpAgent httpAgent = new HttpAgent("project_GetServicesGroupSkillList", inJsong, mContext);
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
	public void setInJsong(JSONObject ij) {
		this.inJsong = ij;
	}

	/**
	 * Out
	 */
	public JSONObject getOutJson() {
		return this.outJson;
	}

}