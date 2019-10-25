package com.huatuo.net.http;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;

import com.huatuo.R;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.http.HttpConnectionUtil.HttpConnectionCallback;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.DateUtil;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.MD5;

/**
 * 网络接口代理程序
 * 
 * @author ftc
 * @version 创建时间：2014-8-3 下午06:37:10 使用方式 Context context = null;// 客户端Context实例
 *          实际使用时候不为空 HashMap<String,String> inmap = new
 *          HashMap<String,String>(); inmap.put("email","oc@163.com");//请求参数
 *          String action = "SystemUserRegister"; // 操作标示 HttpAgent httpagent =
 *          new HttpAgent(action,inmap,context);e
 * 
 *          同步方式 调用 HttpAgent.SendRequest(ActionCallbackInterFace callback)
 *          如果为空，回调 实例 查找 com.hisun.igain.action.lmp 下面 的 action 为名的类
 *          ActionCallbackInterFace 为 回调接口，实例他可以处理网络请求结果， 该接口 有一个 方法 public void
 *          executeresult(ActionRespons actionrespons); 回调处理类 实现该接口 即可
 *          ActionRespons 对象里面有 map 和 list ，分别放 返回 单数据 和结果集合，对因文档中的数组类型
 * 
 *          httpagent.SendRequest(null);
 * 
 *          // 异步方式 httpagent.aSendRequest(null); // 或 ActionCallbackInterFace
 *          actioncallback = new ActionCallbackTest();
 *          httpagent.SendRequest(actioncallback); // 异步方式
 *          httpagent.aSendRequest(actioncallback);
 */
public class HttpAgent {
	private static final String tag = "HTTP";
	private Context mContext;
	private String action;
	private HashMap<String, String> bodyJsonObject;
	private JSONObject bodyJsonObject1;
	private Resources resources;
	private ActionResponse response = new ActionResponse();

	public HttpAgent(final String action, final HashMap<String, String> bodyJsonObject, final Context mContext) {
		this.action = action;
		this.bodyJsonObject = bodyJsonObject;
		this.mContext = mContext;
		if (mContext != null) {
			this.resources = mContext.getResources();
		}
	}
	
	public HttpAgent(final String action, final JSONObject bodyJsonObject, final Context mContext) {
		this.action = action;
		this.bodyJsonObject1 = bodyJsonObject;
		this.mContext = mContext;
		if (mContext != null) {
			this.resources = mContext.getResources();
		}
	}

	/**
	 * 同步发送请求信息,如果callback 为空,支持 自动适配 已 action 为名的 ActionCallbackInterFace 实例
	 * 
	 * @param action
	 * @param inmap
	 * @param context
	 * @param callback
	 * @throws JSONException
	 */
	public ActionResponse sendRequest(final ActionCallbackInterFace callback) {
		if (mContext == null) {
			response.setCode(MsgId.CONTEXT_IS_NULL);
			response.setMsg("Context is null");
			return response;
		}
		if (action == null || action.length() == 0) {
			response.setCode(MsgId.ACTION_IS_NULL);
			response.setMsg("action is null");
			return response;
		}
		if (!CommonUtil.isNetworkConnected(mContext)) {
			response.setCode(MsgId.NET_NOT_CONNECT);
			response.setMsg(mContext.getString(R.string.common_toast_net_not_connect));
			return response;
		}
		send("");
		return response;
	}

	/**
	 * 上传文件
	 * 
	 * @param fileParas
	 */
	public ActionResponse sendUploadFileRequest(HashMap<Integer, Bitmap> fileParas, final ActionCallbackInterFace callback) {
		if (mContext == null) {
			response.setCode(MsgId.CONTEXT_IS_NULL);
			response.setMsg("Context is null");
			return response;
		}
		if (action == null || action.length() == 0) {
			response.setCode(MsgId.ACTION_IS_NULL);
			response.setMsg("action is null");
			return response;
		}
		if (!CommonUtil.isNetworkConnected(mContext)) {
			response.setCode(MsgId.NET_NOT_CONNECT);
			response.setMsg(mContext.getString(R.string.common_toast_net_not_connect));
			return response;
		}
		sendUploadFile(fileParas);
		return response;
	}

	/**
	 * 上传文件
	 * 
	 * @param fileParas
	 */
	public ActionResponse sendUploadFileRequest(ArrayList<String> filePathStringList, final ActionCallbackInterFace callback) {
		if (mContext == null) {
			response.setCode(MsgId.CONTEXT_IS_NULL);
			response.setMsg("Context is null");
			return response;
		}
		if (action == null || action.length() == 0) {
			response.setCode(MsgId.ACTION_IS_NULL);
			response.setMsg("action is null");
			return response;
		}
		if (!CommonUtil.isNetworkConnected(mContext)) {
			response.setCode(MsgId.NET_NOT_CONNECT);
			response.setMsg(mContext.getString(R.string.common_toast_net_not_connect));
			return response;
		}
		sendUploadFile(filePathStringList);
		return response;
	}

	/**
	 * 上传文件
	 * 
	 * @param bytes
	 */
	public ActionResponse sendUploadFileRequest(byte[] bytes, final ActionCallbackInterFace callback) {
		if (mContext == null) {
			response.setCode(MsgId.CONTEXT_IS_NULL);
			response.setMsg("Context is null");
			return response;
		}
		if (action == null || action.length() == 0) {
			response.setCode(MsgId.ACTION_IS_NULL);
			response.setMsg("action is null");
			return response;
		}
		if (!CommonUtil.isNetworkConnected(mContext)) {
			response.setCode(MsgId.NET_NOT_CONNECT);
			response.setMsg(mContext.getString(R.string.common_toast_net_not_connect));
		}
		sendUploadFile(bytes);
		return response;
	}

	/**
	 * 异步发送请求
	 * 
	 * @param action
	 * @param inmap
	 * @param context
	 * @param callback
	 * @throws JSONException
	 */
	public void aSendRequest(final ActionCallbackInterFace callback) {
		Handler handler = new Handler();
		Runnable runnable = new Runnable() {
			public void run() {
				sendRequest(callback);
			}
		};
		handler.post(runnable);
	}

	private void send(String digeststr) {
		
		// 基本参数和并传递的map参数
		HashMap<String, String> map1 = getGuDingCanShu();
		if (null != bodyJsonObject) {
			map1.putAll(bodyJsonObject);
		}
		Map<String, String> map3 = JsonUtil.DoingFilter(map1);// 过滤
		String str = JsonUtil.AppendString(map3);// 排序+拼接
		
		String key = ((String) resources.getString(R.string.key));;
		String newstr = str + key;
		String signvalue = JsonUtil.encryptMd5(newstr);
		map1.put("sign", signvalue);

		JSONObject obj1 = new JSONObject();
		try {
			obj1 = JsonUtil.setMapInJson(obj1, map1);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		byte[] encode = Base64.encode(obj1.toString().getBytes(), Base64.DEFAULT);
		String enc = new String(encode);
		String content = URLEncoder.encode(enc);
		String baseUrl = ((String) resources.getString(R.string.serviceBaseUrl));
		String sendstring = baseUrl + action + "?content=" + content;

//		Log.e("http", "sendstring----------------->"+sendstring);
		CommonUtil.logE("http----sendstring----------------->"+sendstring);
		HttpActionCallback httpCallback = new HttpActionCallback();
		HttpConnectionUtil httpUtil = new HttpConnectionUtil();
		httpUtil.syncHttpClientConnect(sendstring, null, HttpConnectionUtil.HttpMethod.POST, httpCallback);
	}

	private void sendUploadFile(HashMap<Integer, Bitmap> fileParas) {
		// 数据请求地址
		String baseUrl = ((String) resources.getString(R.string.uploadFileBaseUrl));

		HttpActionCallback httpCallback = new HttpActionCallback();
		HttpConnectionUtil httpUtil = new HttpConnectionUtil();
		httpUtil.syncConnectUploadFile(baseUrl, fileParas, HttpConnectionUtil.HttpMethod.POST, httpCallback);
	}

	private void sendUploadFile(ArrayList<String> filePathStringList) {
		// 数据请求地址
		String baseUrl = ((String) resources.getString(R.string.uploadFileBaseUrl));
		String url = baseUrl + action + ".action";

		HttpActionCallback httpCallback = new HttpActionCallback();
		HttpConnectionUtil httpUtil = new HttpConnectionUtil();
		httpUtil.syncConnectUploadFile(url, filePathStringList, HttpConnectionUtil.HttpMethod.POST, httpCallback);
	}

	private void sendUploadFile(byte[] bytes) {
		// 数据请求地址
		String baseUrl = ((String) resources.getString(R.string.uploadFileBaseUrl));
		String url = baseUrl + action + ".action";

		HttpActionCallback httpCallback = new HttpActionCallback();
		HttpConnectionUtil httpUtil = new HttpConnectionUtil();
		httpUtil.syncConnectUploadFile(url, bytes, HttpConnectionUtil.HttpMethod.POST, httpCallback);
	}

	/**
	 * 获取 发送的json 数据
	 * 
	 * @return
	 * @throws JSONException
	 */
	private HashMap<String, String> getGuDingCanShu(){
		HashMap<String, String> GuDingCanShu = new HashMap<String, String>();
		GuDingCanShu.put("client", "android");
		GuDingCanShu.put("deviceID", CommonUtil.DEVICEID);
		GuDingCanShu.put("screenPixel", CommonUtil.SCREENPIXEL);
		GuDingCanShu.put("channelCode", CommonUtil.CHANNELCODE);
		GuDingCanShu.put("messageID", "");
		GuDingCanShu.put("version", CommonUtil.APPVERSION);
		CommonUtil.logE("http----固定参数："+GuDingCanShu);
		return GuDingCanShu;
	}

	/**
	 * 获取 手机特征信息
	 * 
	 * @return
	 */
	private String getUa() {
		String uaString = "";
		uaString += "MODEL=" + Build.MODEL; // 手机型号
		uaString += "-SDK=" + Build.VERSION.SDK; // SDK版本号
		uaString += "-RELEASE=" + Build.VERSION.RELEASE; // Firmware/OS 版本号
		return uaString;
	}

	/**
	 * 获取会话id
	 * 
	 * @param hid
	 * @return
	 */
	private String getMessengerid(String hid) {
		String msgid = "";
		if ("".equals(hid)) {
			hid = "0";
		}
		String hidmd5 = MD5.md532(hid);
		String time = DateUtil.getStringNow();
		msgid = hidmd5 + time;
		return msgid;
	}

	/**
	 * 网络回调方法
	 * 
	 * @author admin
	 * 
	 */
	public class HttpActionCallback implements HttpConnectionCallback {
		@Override
		public void httpHandler(String responseStr) {
			// TODO Auto-generated method stub
			CommonUtil.logE("HttpResponseResult=" + responseStr);
			JSONObject responseJsonObject = JsonUtil.String2Json(responseStr);
			if (responseJsonObject == null) {
				response.setCode(MsgId.RESULT_IS_NULL);
				response.setMsg(mContext.getString(R.string.common_toast_net_down_data_is_null));
				return;
			}
			response.setRsbody(responseJsonObject);
			response.setMsg(responseJsonObject.optString("msg"));
//			response.setCode(responseJsonObject.optString("code"));
//			response.setRshead(responseJsonObject.optJSONObject("head"));
		}
	}

	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}
}