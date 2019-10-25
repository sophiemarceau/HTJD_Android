package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.LogUtil;

/**
 * @author Android开发工程师王润泽
 * 
 */
public class GetServicesTimeListInfo implements Runnable {
	private Handler myHandler;
	private Context mContext;
	// private ArrayList<JSONObject> technicianList;
	// private ArrayList<String> levelList;
	private JSONObject inJson;
	private HashMap<String, String> inJsonMap;
	private JSONObject outJson;
	private ArrayList<JSONObject> serviceTimeList;
	private String outMsgJson = null;
	private int outCode;

	/**
	 * @param projectID
	 *            项目ID
	 * */
	public GetServicesTimeListInfo(Context mContext, Handler mHandler, HashMap<String, String> inJson) {
		this.mContext = mContext;
		this.myHandler = mHandler;
		serviceTimeList = new ArrayList<JSONObject>();
		this.inJsonMap = inJson;
		// levelList = new ArrayList<String>();
	}

	@Override
	public void run() {
		try {
			CommonUtil.log("请求服务时间列表参数------------inJsonMap：" + inJsonMap);
			
			HttpAgent httpAgent = new HttpAgent("order/getServiceTime", inJsonMap,
					mContext);
			ActionResponse response = httpAgent.sendRequest(null);
			int code = response.getCode();
			initRsultData(response);
			CommonUtil.log("请求服务时间列表参数------------code：" + code);
			if (code == 0) {
//				initRsultData(response);
				CommonUtil.log("请求服务时间列表参数------------myHandler：" + myHandler);
				myHandler.sendEmptyMessage(MsgId.DOWN_DATA_S);
			} else if (code == MsgId.NET_NOT_CONNECT) {
				myHandler.sendEmptyMessage(MsgId.NET_NOT_CONNECT);
			} else {
				myHandler.sendEmptyMessage(MsgId.DOWN_DATA_F);
			}
		} catch (Exception e) {
			// LogUtil.e(Tag, "LoginInvokeItem run" + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * inJson
	 * */
	public void setInJsong(JSONObject ij) {
		this.inJson = ij;
	}

	/**
	 * Out:outJson
	 */
	public JSONObject getOutJson() {
		return this.outJson;
	}
	



	/**
	 * 初始化返回结果数据
	 * */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();
		outJson = bodyJsonObject;
		outMsgJson = actionRespons.getMsg();
		outCode = actionRespons.getCode();
	}
	public String getOutMsg() {
		return outMsgJson;
	}
	
	public int getOutCode() {
		return outCode;
	}
	
}
