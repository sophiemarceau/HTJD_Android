package com.huatuo.activity.pay;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.huatuo.activity.order.OrderDetailActivity;
import com.huatuo.activity.order.OrderDetail_Flash_Buy_Activity;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Toast_Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 
 * @author Android开发工程师 王润泽
 * 
 */
public class PaySecKillUtil {
	public final static String WX_APP_ID = "wx9812707fd136e8bb";// 微信支付appID --//
	public final static String WX_APP_SECRET = "9e755f01725602288079b6d3d18f4bd0";// 微信支付APP_SECRET
	private Activity mContext;
	private static PaySecKillUtil instance;
	private String alipayData;
	private JSONObject tenpayData;
	private String orderID = "";

	private PayReq req;
	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_CHECK_FLAG = 2;
	private String appId, timeStamp, signType, nonceStr, paySign, partnerId,
			prepayId, package1;

	private boolean sIsWXAppInstalledAndSupported;

	public static PaySecKillUtil getInstance() {
		if (instance == null) {
			synchronized (PaySecKillUtil.class) {
				if (instance == null) {
					instance = new PaySecKillUtil();
				}
			}
		}

		return instance;
	}

	/**
	 * 微信支付
	 * 
	 * @param activity
	 * @param tenpayData
	 */
	public void wxPay(Activity activity, JSONObject tenpayData,String orderID) {
		mContext = activity;
		this.tenpayData = tenpayData;
		this.orderID = orderID;
		CommonUtil.saveStringOfSharedPreferences(mContext.getApplicationContext(), "ORDERID", orderID);
		CommonUtil.saveBooleanOfSharedPreferences(mContext.getApplicationContext(), "ISQUICKPAY", false);
		CommonUtil.saveBooleanOfSharedPreferences(mContext.getApplicationContext(), "ISSECKILLPAY", true);
		CommonUtil.log("orderID:"+orderID);
		final IWXAPI msgApi = WXAPIFactory.createWXAPI(mContext, null);
		msgApi.registerApp(WX_APP_ID);
		req = new PayReq();

		if (isWXAppInstalledAndSupported(msgApi)) {
			initTenpayData();
			sendPayReq(msgApi);
//			if(null != mContext){
//				mContext.finish();
//			}
		} else {
			Toast_Util.showToast(mContext, "请您安装微信客户端");
//			jumpToOrderDetail();
		}
	}

	/**
	 * 支付宝支付
	 * 
	 * @param activity
	 * @param alipayData
	 */
	public void aliPay(Activity activity, final String alipayData,String orderID) {
		mContext = activity;
		this.alipayData = alipayData;
		this.orderID = orderID;
		CommonUtil.saveStringOfSharedPreferences(mContext.getApplicationContext(), "ORDERID", orderID);
		// 初始化支付宝参数
		Runnable payRunnable = new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(mContext);
				// 调用支付接口，获取支付结果
				String result = alipay.pay(alipayData, true);// isShowPayLoading 
				Message msg = new Message();
				msg.what = SDK_PAY_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread payThread = new Thread(payRunnable);
		payThread.start();
	}

	/**
	 * 处理微信支付返回数据
	 */
	private void initTenpayData() {
		if(null != tenpayData){

			appId = tenpayData.optString("appId", "");
			CommonUtil.log("--------------------------------------appId:" + appId);
			CommonUtil
					.log("-------------------------------wx9812707fd136e8bb-------appId:");
	
			partnerId = tenpayData.optString("partnerid", "");
			prepayId = tenpayData.optString("prepayid", "");
			timeStamp = tenpayData.optString("timestamp", "");
			signType = tenpayData.optString("signType", "");
			nonceStr = tenpayData.optString("noncestr", "");
			paySign = tenpayData.optString("sign", "");
			package1 = tenpayData.optString("package", "");
		}else {
			Toast_Util.showToast(mContext, "支付参数有误");
		}

		CommonUtil.logE("PayUtil--------------tenpayData-------------->" + tenpayData);
	}

	/**
	 * 发起微信支付请求
	 */
	private void sendPayReq(IWXAPI msgApi) {

		req.appId = PaySecKillUtil.WX_APP_ID;
		req.partnerId = partnerId;
		req.prepayId = prepayId;
		req.packageValue = package1;
		req.nonceStr = nonceStr;
		req.timeStamp = timeStamp;
		req.sign = paySign;

		msgApi.registerApp(PaySecKillUtil.WX_APP_ID);
		msgApi.sendReq(req);
	}

	/**
	 * 支付宝支付结果处理
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SDK_PAY_FLAG: {
				PayResult payResult = new PayResult((String) msg.obj);

				// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
				String resultInfo = payResult.getResult();

				String resultStatus = payResult.getResultStatus();

				// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
				if (TextUtils.equals(resultStatus, "9000")) {
//					Intent intent = new Intent(mContext, OrderDetailActivity2.class);
//					intent.putExtra("orderID", orderID);
//					intent.putExtra("MYHUTUO", Constants.MY_HUTUO_ORDER);
//					mContext.startActivity(intent);
//					mContext.finish();
				} else {
					// 判断resultStatus 为非“9000”则代表可能支付失败
					// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
					if (TextUtils.equals(resultStatus, "8000")) {
						Toast.makeText(mContext, "支付结果确认中", Toast.LENGTH_SHORT)
								.show();
					} else if (TextUtils.equals(resultStatus, "4000")) {
						Toast.makeText(mContext, "订单支付失败", Toast.LENGTH_SHORT)
								.show();
					} else if (TextUtils.equals(resultStatus, "6001")) {
						Toast.makeText(mContext, "支付取消", Toast.LENGTH_SHORT)
								.show();
					} else if (TextUtils.equals(resultStatus, "6002")) {
						Toast.makeText(mContext, "网络连接出错", Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(mContext, "未知错误", Toast.LENGTH_SHORT)
								.show();
					}
				}
				
				jumpToOrderDetail();
				break;
			}
			case SDK_CHECK_FLAG: {
				Toast.makeText(mContext, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT)
						.show();
				break;
			}
			default:
				break;
			}
		};
	};
	
	/**
	 * 调往订单详情
	 */
	private void jumpToOrderDetail(){
		Intent intent = new Intent(mContext, OrderDetail_Flash_Buy_Activity.class);
		intent.putExtra("orderID", orderID);
		mContext.startActivity(intent);
		mContext.finish();
	}

	private boolean isWXAppInstalledAndSupported(IWXAPI api) {
		sIsWXAppInstalledAndSupported = api.isWXAppInstalled()
				&& api.isWXAppSupportAPI();
		if (!sIsWXAppInstalledAndSupported) {
			CommonUtil.log("未安装微信客户端");
		}
		return sIsWXAppInstalledAndSupported;
	}
}
