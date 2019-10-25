package com.huatuo.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.order.OrderDetailActivity;
import com.huatuo.activity.order.OrderDetail_Flash_Buy_Activity;
import com.huatuo.activity.pay.PayUtil;
import com.huatuo.base.MyApplication;
import com.huatuo.util.CommonUtil;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private static final String TAG = "WXPayEntryActivity";

	private IWXAPI api;

	TextView tv_result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, PayUtil.WX_APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		CommonUtil.logE("onPayFinish, errCode = " + resp.errCode);
		
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			if (("0").equals(String.valueOf(resp.errCode))) {
				isJumpToOrderDetail(true);
			} else if (("-1").equals(String.valueOf(resp.errCode))) {
				Toast.makeText(WXPayEntryActivity.this, "发生错误",
						Toast.LENGTH_LONG).show();
			} else if (("-2").equals(String.valueOf(resp.errCode))) {
				Toast.makeText(WXPayEntryActivity.this, "用户取消",
						Toast.LENGTH_LONG).show();
				isJumpToOrderDetail(false);
			} else if ((BaseResp.ErrCode.ERR_AUTH_DENIED + "").equals(String
					.valueOf(resp.errCode))) {
				if (api.registerApp(PayUtil.WX_APP_ID)) {
					Toast.makeText(this, "拒绝授权", Toast.LENGTH_SHORT).show();
					api.unregisterApp();
					api.detach();
				}
			}
			
			
		}
	}

	/**
	 * 判断是否跳往订单详情
	 * @param flag 针对闪付 如果支付成功 去订单详情 ，失败：停留该页面
	 */
	private void isJumpToOrderDetail(boolean flag) {
		//闪付订单标识
		boolean ISQUICKPAY = CommonUtil.getBooleanOfSharedPreferences(getApplicationContext(), "ISQUICKPAY", false);
		//秒杀订单标识
		boolean ISSECKILLPAY = CommonUtil.getBooleanOfSharedPreferences(getApplicationContext(), "ISSECKILLPAY", false);
		CommonUtil.log("ISQUICKPAY:"+ ISQUICKPAY);
		CommonUtil.log("ISSECKILLPAY:"+ ISSECKILLPAY);
		//闪付订单
		if (ISQUICKPAY) {
			//闪付订单--成功后去订单详情 否则仍处于当前页面
			if(flag){
				goToOrderDetail();
			}else {
				finish();
			}
			
		}else {
			//非闪付订单 完成后都去订单详情
			
			if(ISSECKILLPAY){
				//秒杀订单详情
				goToSecKillOrderDetail();
			}else {
				//正常订单详情
				goToOrderDetail();
			}
			
		}
	}
	
	/**
	 * 去往订单详情
	 */
	private void goToOrderDetail(){
		String orderId = CommonUtil.getStringOfSharedPreferences(getApplicationContext(), "ORDERID", "");
		CommonUtil.log("orderid:"+ orderId);
		CommonUtil.log("微信支付回调---------------------------------去订单详情");
			Intent intent = new Intent(this, OrderDetailActivity.class);
			intent.putExtra("orderID", orderId);
			startActivity(intent);
			finish();
	}
	/**
	 * 去往秒杀订单详情
	 */
	private void goToSecKillOrderDetail(){
		String orderId = CommonUtil.getStringOfSharedPreferences(getApplicationContext(), "ORDERID", "");
		CommonUtil.log("orderid:"+ orderId);
		CommonUtil.log("微信支付回调---------------------------------去订单详情");
			Intent intent = new Intent(this, OrderDetail_Flash_Buy_Activity.class);
			intent.putExtra("orderID", orderId);
			startActivity(intent);
			finish();
	}
}