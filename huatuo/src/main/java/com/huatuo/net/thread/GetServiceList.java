package com.huatuo.net.thread;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.ActionResponse;
import com.huatuo.net.http.HttpAgent;
import com.huatuo.util.CommonUtil;

public class GetServiceList implements Runnable {
	private Handler mHandler;
	private Context mContext;
	private ArrayList<JSONObject> serviceList;
	private ArrayList<JSONObject> topImageList;
	private String servType;
	private String sitType;
	private String city;

	/**
	 * @param orderByType
	 *            排序方式，默认1;1 正序 ,-1 倒序
	 * @param orderByKey
	 *            1:按成单;2:按年龄;3:按距离 空表示按添加时间
	 * @param city
	 *            城市
	 * @param skillGrade
	 *            等级，默认空，空表示全部
	 * */
	public GetServiceList(Context mContext, Handler mHandler, String servType, String sitType, String city) {
		this.mContext = mContext;
		this.mHandler = mHandler;
		serviceList = new ArrayList<JSONObject>();
		topImageList = new ArrayList<JSONObject>();
		this.servType = servType;
		this.sitType = sitType;
		this.city = city;
	}

	@Override
	public void run() {
		try {
			// JSONObject inJsonObject = new JSONObject();
			HashMap<String, String> inJsonObject = new HashMap<String, String>();
			inJsonObject.put("servType", servType);
			inJsonObject.put("sitType", sitType);
			inJsonObject.put("city", city);

			CommonUtil.logE("上门技师：提交参数："+inJsonObject);
			HttpAgent httpAgent = new HttpAgent("serv/getServList", inJsonObject, mContext);
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

	/**
	 * 初始化返回结果数据
	 * */
	private void initRsultData(ActionResponse actionRespons) {
		JSONObject bodyJsonObject = actionRespons.getRsbody();
//		Log.e("GetServiceList", "bodyJsonObject----------->"+bodyJsonObject);
		// 服务列表
		JSONArray servListArray = bodyJsonObject.optJSONArray("servList");
		JSONObject json;
		if (servListArray == null || servListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < servListArray.length(); i++) {
				try {
					json = servListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				serviceList.add(json);
			}
		}

		// 顶部图片列表
		JSONArray topImageListArray = bodyJsonObject.optJSONArray("topImageList");
//		Log.e("GetServiceList", "topImageList----------->"+topImageListArray);
		JSONObject json1;
		if (topImageListArray == null || topImageListArray.length() == 0) {
			return;
		} else {
			for (int i = 0; i < topImageListArray.length(); i++) {
				try {
					json1 = topImageListArray.getJSONObject(i);
				} catch (JSONException e) {
					e.printStackTrace();
					continue;
				}
				topImageList.add(json1);
			}
		}
	}

	/**
	 * 返回技师列表
	 * */
	public ArrayList<JSONObject> getServiceList() {
		return serviceList;
	}

	/**
	 * 返回顶部图片列表
	 * */
	public ArrayList<JSONObject> getTopImageList() {
		return topImageList;
	}

}
