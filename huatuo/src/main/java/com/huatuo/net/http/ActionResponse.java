package com.huatuo.net.http;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author ftc
 * @version 创建时间：2014-8-4 上午11:21:24
 */
public class ActionResponse {
	private JSONObject resultHead;
	private JSONObject resultBody;
	private List<JSONObject> rslist = null;

	public ActionResponse() {
		resultHead = new JSONObject();
		resultBody = new JSONObject();
	}

	public int getCode() {
		return resultBody.optInt("code", 9999);
	}

	public void setCode(int code) {
		try {
			resultBody.put("code", code);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// LogUtil.e("deesha", "msg="+e.getMessage());
			e.printStackTrace();
		}
	}

	public void setMsg(String msg) {
		try {
			resultBody.put("message", msg);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	public String getMsg() {
		return resultBody.optString("message", "未知错误");
	}

	// 得到推送消息内容
	public String getPushMsg() {
		return resultBody.optString("msg", "未知错误");
	}

	public String getFFID() {
		return resultBody.optString("FFID", "--");
	}

	public void setRsbody(JSONObject resultBody) {
		this.resultBody = resultBody;
	}

	public JSONObject getRsbody() {
		if (resultBody == null) {
			resultBody = new JSONObject();
		}
		return resultBody;
	}

	public void setRshead(JSONObject resultHead) {
		this.resultHead = resultHead;
	}

	public JSONObject getRshead() {
		if (resultHead == null) {
			resultHead = new JSONObject();
		}
		return resultHead;
	}

	public int getTotalCount() {
		return resultBody.optInt("totalCount", 0);
	}

	public int getTotalPages() {
		return resultBody.optInt("totalPages", 0);
	}

	private void setRslist() {
		try {
			Iterator keys = resultBody.keys();
			int n = 0;
			while (keys.hasNext() && n == 0) {
				String key = (String) keys.next();
				String value = resultBody.get(key).toString();
				if (value.startsWith("[") && value.endsWith("]")) {
					addJsList(value);
					n++;
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
		}
	}

	private void addJsList(String jsarraysvalue) throws JSONException {
		rslist = new ArrayList<JSONObject>();
		JSONArray jsarrays = new JSONArray(jsarraysvalue);
		for (int i = 0; i < jsarrays.length(); i++) {
			rslist.add(jsarrays.optJSONObject(i));
		}
	}

	public List<JSONObject> getRslist() {
		if (rslist == null) {
			setRslist();
		}
		return rslist;
	}
}