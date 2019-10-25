package com.huatuo.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.view.Gravity;

import com.huatuo.interfaces.CallBackHandler_DeviceToken;
import com.umeng.message.PushAgent;
import com.umeng.message.UmengRegistrar;
import com.umeng.message.tag.TagManager;

public class UmengPushUtil {
	private static UmengPushUtil instance;
	private Context mContext;
	public static String deviceToken = "";
	private PushAgent mPushAgent;

	public static UmengPushUtil getInstance() {
		if (instance == null) {
			synchronized (UmengPushUtil.class) {
				if (instance == null) {
					instance = new UmengPushUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 获取友盟token
	 * 
	 * @param context
	 * @return
	 */
	public String getDeviceToken(Context context) {
		this.mContext = context;
		CommonUtil.logE("getDeviceToken：mContext:" + mContext + "---------------deviceToken:"+ deviceToken);
		if (TextUtils.isEmpty(deviceToken)) {
			CommonUtil.logE("getDeviceToken：为空:" );
		}
		
		CallBackHandler_DeviceToken mHandler = new CallBackHandler_DeviceToken() {
			
			@Override
			public void doHandler(String mdeviceToken) {
				deviceToken = mdeviceToken;
			}
		};
		new MyTask(mHandler).execute();
		
		
		CommonUtil.logE("getDeviceToken：不为空:" );
		CommonUtil.logE("mContext:" + mContext + "---------------deviceToken:"
				+ deviceToken);
		return deviceToken;
	}

	
	public class MyTask extends AsyncTask<String, Void, String> {
		private CallBackHandler_DeviceToken mCallBackHandler_DeviceToken;
		public MyTask(CallBackHandler_DeviceToken mHandler) {
			// TODO Auto-generated constructor stub
			mCallBackHandler_DeviceToken = mHandler;
		}

		@Override
		protected String doInBackground(String... params) {
			String deviceToken = "";
			do {
				 deviceToken = UmengRegistrar
						.getRegistrationId(mContext);
				CommonUtil.logE("mContext:" + mContext
						+ "---------------deviceToken:" + deviceToken);
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} while (TextUtils.isEmpty(deviceToken));
			
			return deviceToken;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			mCallBackHandler_DeviceToken.doHandler(result);
		}
	}
	/**
	 * 添加标签
	 * 
	 * @param pushAgent
	 * @param tag
	 */
	public void addTag(PushAgent pushAgent, String tag) {
		mPushAgent = pushAgent;

		if (!mPushAgent.isRegistered()) {
			Toast_Util.showToastOnlyOne(mContext, "抱歉，还未注册", Gravity.CENTER,
					false);
			return;
		}

		new AddTagTask(tag).execute();
	}

	class AddTagTask extends AsyncTask<Void, Void, String> {

		String tagString;
		String[] tags;

		public AddTagTask(String tag) {
			// TODO Auto-generated constructor stub
			tagString = tag;
			tags = tagString.split(",");
		}

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			try {
				TagManager.Result result = mPushAgent.getTagManager().add(tags);
				return result.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return "Fail";
		}

		@Override
		protected void onPostExecute(String result) {
			CommonUtil.logE("addTag:" + result.toString());
			// edTag.setText("");
			// listTags();
		}
	}

	/**
	 * 获取标签列表
	 */
	public void listTags(PushAgent pushAgent) {
		mPushAgent = pushAgent;
		if (!mPushAgent.isRegistered()) {
			Toast_Util.showToastOnlyOne(mContext, "抱歉，还未注册", Gravity.CENTER,
					false);
			return;
		}
		new ListTagTask().execute();
	}

	class ListTagTask extends AsyncTask<Void, Void, List<String>> {
		@Override
		protected List<String> doInBackground(Void... params) {
			List<String> tags = new ArrayList<String>();
			try {
				tags = mPushAgent.getTagManager().list();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return tags;
		}

		@Override
		protected void onPostExecute(List<String> result) {
			if (result != null) {
				StringBuilder info = new StringBuilder();
				info.append("Tags:\n");
				for (int i = 0; i < result.size(); i++) {
					String tag = result.get(i);
					info.append(tag + "\n");
				}
				info.append("\n");
				CommonUtil.logE("list:" + info);
			} else {
				CommonUtil.logE("list:" + "无");
			}
		}
	}

}
