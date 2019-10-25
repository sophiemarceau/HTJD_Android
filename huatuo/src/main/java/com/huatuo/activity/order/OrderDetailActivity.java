package com.huatuo.activity.order;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.appoint.AgainAppoint;
import com.huatuo.activity.appoint.GoToAddTime;
import com.huatuo.activity.map.GuideToStoreUtil;
import com.huatuo.activity.pay.PayUtil;
import com.huatuo.activity.technician.TechnicianDetail;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.CancelOrder_thread;
import com.huatuo.net.thread.GetOrderDetail;
import com.huatuo.net.thread.GetUserInfo;
import com.huatuo.net.thread.Pay_thread;
import com.huatuo.util.CallUtil;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.JumpTargetActivityUtil;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OrderDetailActivity extends BaseActivity implements OnClickListener {
	private Context mContext;
	private Handler mHandler, handler_cancelOrder;
	private ImageView iv_icon, iv_userAddress, iv_tech, iv_call;
	private RelativeLayout rl_top, rl_lianxikefu, rl_chongxinyuyue, rl_querendingdan, rl_jiazhong, rl_pinglun,
			rl_zaiciyuyue;
	private LinearLayout ll_yituikuan_door, ll_yituikuan_store, ll_normalOrder, ll_flashOrder, ll_xiaofeima,
			ll_xiaofeima_used, ll_kaishijieshu, ll_back, ll_storeName, ll_userMobile, ll_userName, ll_techName,
			ll_servAddress, ll_servAddressFlash, ll_call;
	private TextView tv_head, tv_top, tv_userAddressFlash, tv_userMobileFlash, tv_orderStatus, tv_storeName,
			tv_orderNum, tv_orderTime, tv_price, tv_appointTime, tv_xiaofeima, tv_xiaofeima_used, tv_xiaofeima_status,
			tv_xiaofeishijian, tv_fuwukaishi, tv_fuwujieshu, tv_userAddress, tv_userName, tv_userMobile, tv_userBeizhu,
			tv_servName, tv_techName, tv_techLevel, tv_techSex, tv_timeLong, tv_beltNum, tv_flashPrice_1,
			tv_flashPrice_2, tv_flashPrice_3, tv_flashPrice_4, tv_flashPrice_5, tv_normalPrice_1, tv_normalPrice_2,
			tv_normalPrice_3, tv_normalPrice_4, tv_normalPrice_5, tv_normalPrice_6, tv_xiaofeima_yituikuan,
			tv_tuikuan_store, tv_tuikuanshijian_store, tv_tuikuan_door, tv_tuikuanshijian_door, tv_storetel,
			tv_flash_remark;
	private Button bt_cancel, bt_zhifu, bt_lianxikefu, bt_woyaojiazhong, bt_woyaopinglun,
			/*
			 * bt_fenxiang ,
			 */bt_zaiciyuyue, bt_chongxinyuyue;
	private GetOrderDetail getOrderDetail;
	private CancelOrder_thread cancelOrder_thread;
	private String orderID, amount, serviceID, serviceName, serviceIcon, totalPrice, price, discountAmount,
			discountName, serviceStartTime, serviceEndTime, transportationFee, bedFee, memo, couponpay, payment, status,
			storeID, storeName, storeAddress, storeIcon, workerID, workerName, workerIcon, workerGrade, workerGender,
			clientName, clientGender, clientArea, clientAddress, clientMobile, duration, type, orderClass, orderTime,
			actualBeginTime, actualEndTime, mobile, verifyCode, verifyTime, isExtended, isExtensible,
			workerDepartureTime, commentCount, skillScore, orderCount, longitude, latitude, storeContactTel, dobackTime,
			orderFrom;
	private JSONObject ExtentionInfo;
	public int VIEW_TYPE = -1;// 标记是否是预约界面过来的的
	public OrderDetailActivity orderDetailActivity;
	private HashMap<String, HashMap<String, String>> JSon2Map_TwoLayerMap;
	private JSONObject orderDetail_jsonObject;

	// 账户余额
	private GetUserInfo getUserInfo;
	private JSONObject userObj;
	private Handler handler_getUserInfo;
	private TextView tx_accountPay;// 账户余额

	// 支付选择
	private LinearLayout ll_pay;
	private RelativeLayout layout_wxPay, layout_aliPay, layout_accountPay, rl_accountPay_line;
	private ImageView iv_alipay_select, iv_wxPay_select, iv_accountPay_select;

	// 支付
	private String accountBalance = "0";
	private double accountBalance_double;// 账户余额
	private double stillPay;// 最终需要支付金额
	private boolean ISEnoughOfAccountPay = true;// 账户余额是否充足

	private String isAccount;// 账户余额支付：1
	private String payChannelCode;// 支付渠道编号

	private boolean account_pay_isSelected = true;
	private boolean ali_pay_isSelected = false;
	private boolean wx_pay_isSelected = false;
	private int FLAG_PAYTYPE = -1;// -1:代表账户余额：0：表示不用支付宝（账户）或者线下；1：表示支付宝--调用支付宝；2：表示微信支付--调用微信APP
	private double totalFee;
	private Handler handler_Pay;
	private Pay_thread pay_thread;
	// 提交订单成后返回支付
	private JSONObject orderObj = null;
	private String alipayData;
	private JSONObject tenpayData;

	private boolean push = false;// 是否是推送
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_order_detail2);
		mContext = OrderDetailActivity.this;
		orderDetailActivity = OrderDetailActivity.this;
		initHandler();
		findViewById();
		getExtras();
		getUserInfo();

	}

	/***
	 * 将activity 的创建模式设置为singletask,
	 * 使用这个方法可以再其他activity想这个activity发送Intent时，这个Intent能够及时更新
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		CommonUtil.logE("--------------------------------------------onNewIntent-----------------------------");
		super.onNewIntent(intent);
		setIntent(intent); // 这一句必须的，否则Intent无法获得最新的数据
		getExtras();
		getUserInfo();
	}

	private void getExtras() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			orderID = bundle.getString("orderID");
			orderFrom = bundle.getString("orderFrom");
			push = bundle.getBoolean("push");

		}
		CommonUtil.logE("OrderDetailActivity------orderID----------------->" + orderID);

	}

	private void initHandler() {
		mHandler = new MyHandler();
		handler_cancelOrder = new Handler_CancelOrder();
		handler_getUserInfo = new Handler_getUserInfo();
		handler_Pay = new Handler_Pay();
	}

	private void findViewById() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_yituikuan_door = (LinearLayout) findViewById(R.id.ll_yituikuan_door);
		ll_yituikuan_store = (LinearLayout) findViewById(R.id.ll_yituikuan_store);
		ll_normalOrder = (LinearLayout) findViewById(R.id.ll_normalOrder);
		ll_flashOrder = (LinearLayout) findViewById(R.id.ll_flashOrder);
		ll_xiaofeima = (LinearLayout) findViewById(R.id.ll_xiaofeima);
		ll_xiaofeima_used = (LinearLayout) findViewById(R.id.ll_xiaofeima_used);
		ll_kaishijieshu = (LinearLayout) findViewById(R.id.ll_kaishijieshu);
		ll_pay = (LinearLayout) findViewById(R.id.ll_pay);
		ll_storeName = (LinearLayout) findViewById(R.id.ll_storeName);
		ll_userName = (LinearLayout) findViewById(R.id.ll_userName);
		ll_userMobile = (LinearLayout) findViewById(R.id.ll_userMobile);
		ll_techName = (LinearLayout) findViewById(R.id.ll_techName);
		ll_servAddress = (LinearLayout) findViewById(R.id.ll_servAddress);
		ll_servAddressFlash = (LinearLayout) findViewById(R.id.ll_servAddressFlash);
		ll_call = (LinearLayout) findViewById(R.id.ll_call);
		rl_top = (RelativeLayout) findViewById(R.id.rl_top);

		rl_chongxinyuyue = (RelativeLayout) findViewById(R.id.rl_chongxinyuyue);
		rl_lianxikefu = (RelativeLayout) findViewById(R.id.rl_lianxikefu);
		rl_querendingdan = (RelativeLayout) findViewById(R.id.rl_querendingdan);
		rl_jiazhong = (RelativeLayout) findViewById(R.id.rl_jiazhong);
		rl_pinglun = (RelativeLayout) findViewById(R.id.rl_pinglun);
		rl_zaiciyuyue = (RelativeLayout) findViewById(R.id.rl_zaiciyuyue);

		iv_icon = (ImageView) findViewById(R.id.iv_icon);
		iv_userAddress = (ImageView) findViewById(R.id.iv_userAddress);
		iv_tech = (ImageView) findViewById(R.id.iv_tech);
		iv_call = (ImageView) findViewById(R.id.iv_call);
		tv_head = (TextView) findViewById(R.id.tv_head);
		tv_top = (TextView) findViewById(R.id.tv_top);
		tv_xiaofeima_yituikuan = (TextView) findViewById(R.id.tv_xiaofeima_yituikuan);
		tv_tuikuan_store = (TextView) findViewById(R.id.tv_tuikuan_store);
		tv_tuikuanshijian_store = (TextView) findViewById(R.id.tv_tuikuanshijian_store);
		tv_tuikuan_door = (TextView) findViewById(R.id.tv_tuikuan_door);
		tv_tuikuanshijian_door = (TextView) findViewById(R.id.tv_tuikuanshijian_door);
		tv_userAddressFlash = (TextView) findViewById(R.id.tv_userAddressFlash);
		tv_userMobileFlash = (TextView) findViewById(R.id.tv_userMobileFlash);
		tv_orderStatus = (TextView) findViewById(R.id.tv_orderStatus);
		tv_techName = (TextView) findViewById(R.id.tv_techName);
		tv_techLevel = (TextView) findViewById(R.id.tv_techLevel);
		tv_techSex = (TextView) findViewById(R.id.tv_techSex);
		tv_servName = (TextView) findViewById(R.id.tv_servName);
		tv_storeName = (TextView) findViewById(R.id.tv_storeName);
		tv_timeLong = (TextView) findViewById(R.id.tv_timeLong);
		tv_beltNum = (TextView) findViewById(R.id.tv_beltNum);
		tv_orderNum = (TextView) findViewById(R.id.tv_orderNum);
		tv_orderTime = (TextView) findViewById(R.id.tv_orderTime);
		tv_price = (TextView) findViewById(R.id.tv_price);
		tv_appointTime = (TextView) findViewById(R.id.tv_appointTime);
		tv_xiaofeima = (TextView) findViewById(R.id.tv_xiaofeima);
		tv_xiaofeima_used = (TextView) findViewById(R.id.tv_xiaofeima_used);
		tv_xiaofeima_status = (TextView) findViewById(R.id.tv_xiaofeima_status);
		tv_xiaofeishijian = (TextView) findViewById(R.id.tv_xiaofeishijian);
		tv_fuwukaishi = (TextView) findViewById(R.id.tv_fuwukaishi);
		tv_fuwujieshu = (TextView) findViewById(R.id.tv_fuwujieshu);
		tv_userAddress = (TextView) findViewById(R.id.tv_userAddress);
		tv_userName = (TextView) findViewById(R.id.tv_userName);
		tv_userMobile = (TextView) findViewById(R.id.tv_userMobile);
		tv_userBeizhu = (TextView) findViewById(R.id.tv_userBeizhu);
		tv_flashPrice_1 = (TextView) findViewById(R.id.tv_flashPrice_1);
		tv_flashPrice_2 = (TextView) findViewById(R.id.tv_flashPrice_2);
		tv_flashPrice_3 = (TextView) findViewById(R.id.tv_flashPrice_3);
		tv_flashPrice_4 = (TextView) findViewById(R.id.tv_flashPrice_4);
		tv_flashPrice_5 = (TextView) findViewById(R.id.tv_flashPrice_5);
		tv_normalPrice_1 = (TextView) findViewById(R.id.tv_normalPrice_1);
		tv_normalPrice_2 = (TextView) findViewById(R.id.tv_normalPrice_2);
		tv_normalPrice_3 = (TextView) findViewById(R.id.tv_normalPrice_3);
		tv_normalPrice_4 = (TextView) findViewById(R.id.tv_normalPrice_4);
		tv_normalPrice_5 = (TextView) findViewById(R.id.tv_normalPrice_5);
		tv_normalPrice_6 = (TextView) findViewById(R.id.tv_normalPrice_6);
		tv_storetel = (TextView) findViewById(R.id.tv_storetel);
		tv_flash_remark = (TextView) findViewById(R.id.tv_flash_remark);
		bt_cancel = (Button) findViewById(R.id.bt_orderDetail_cancel);
		bt_zhifu = (Button) findViewById(R.id.bt_zhifu);
		bt_lianxikefu = (Button) findViewById(R.id.bt_lianxikefu);
		bt_woyaojiazhong = (Button) findViewById(R.id.bt_woyaojiazhong);
		bt_woyaopinglun = (Button) findViewById(R.id.bt_woyaopinglun);
		// bt_fenxiang = (Button) findViewById(R.id.bt_fenxiang);
		bt_zaiciyuyue = (Button) findViewById(R.id.bt_zaiciyuyue);
		bt_chongxinyuyue = (Button) findViewById(R.id.bt_chongxinyuyue);
		// 支付
		ll_pay = (LinearLayout) findViewById(R.id.ll_pay);

		layout_accountPay = (RelativeLayout) findViewById(R.id.layout_accountPay);
		layout_aliPay = (RelativeLayout) findViewById(R.id.layout_alipay);
		layout_wxPay = (RelativeLayout) findViewById(R.id.layout_wxPay);

		rl_accountPay_line = (RelativeLayout) findViewById(R.id.rl_accountPay_line);

		iv_accountPay_select = (ImageView) findViewById(R.id.iv_accountPay_select);
		iv_alipay_select = (ImageView) findViewById(R.id.iv_alipay_select);
		iv_wxPay_select = (ImageView) findViewById(R.id.iv_wxPay_select);
		// 支付
		tx_accountPay = (TextView) findViewById(R.id.tx_accountPay);

		initListener();

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	public void initWidget() {

		if ("2".equals(type)) {// 闪付订单
			tv_flash_remark.setText(
					"1.为什么没有消费码？\n闪付不同于团购，是不会产生消费码的。您可以将你支付的订单号展示给门店。\n2.我买错了想退款怎么处理？\n闪付是到店消费，与门店确认买单金额后再进行支付，如果您买错了可以直接联系门店办理退款。");
			ll_normalOrder.setVisibility(View.GONE);
			ll_flashOrder.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(storeIcon, iv_icon,
					ImageLoader_DisplayImageOptions.getInstance().setDefaultImageSmallImg());// 图片icon
			tv_price.setVisibility(View.GONE);
			tv_servName.setText("店铺名称：" + storeName);
			tv_flashPrice_1.setText("闪付-￥" + NumFormatUtil.centFormatYuanToString(discountAmount));
			tv_flashPrice_2.setText("优惠券-￥" + NumFormatUtil.centFormatYuanToString(couponpay));
			if ("0".equals(discountAmount)) {
				tv_flashPrice_1.setVisibility(View.GONE);
			}
			if ("0".equals(couponpay)) {
				tv_flashPrice_2.setVisibility(View.GONE);
			}
			if ("0".equals(discountAmount) && "0".equals(couponpay)) {
				tv_flashPrice_1.setVisibility(View.GONE);
				tv_flashPrice_2.setVisibility(View.GONE);
				tv_flashPrice_3.setVisibility(View.GONE);
			}
			Double a = Double.parseDouble(discountAmount) + Double.parseDouble(couponpay);
			tv_flashPrice_3.setText("总共为您节省：￥" + NumFormatUtil.centFormatYuanToString(a + ""));
			tv_flashPrice_4.setText("消费金额：￥" + NumFormatUtil.centFormatYuanToString(totalPrice));
			tv_flashPrice_5.setText("实际支付：￥" + NumFormatUtil.centFormatYuanToString(payment));
		} else {// 一般订单
			ll_normalOrder.setVisibility(View.VISIBLE);
			ll_flashOrder.setVisibility(View.GONE);
			ImageLoader.getInstance().displayImage(serviceIcon, iv_icon,
					ImageLoader_DisplayImageOptions.getInstance().setDefaultImageSmallImg());// 图片icon
			tv_servName.setText("项目名称：" + serviceName);
		}

		tv_orderNum.setText(orderID);
		tv_orderTime.setText(orderTime);
		if (workerName.isEmpty()) {
			iv_tech.setVisibility(View.GONE);
		}

		if (workerName.isEmpty()) {
			tv_techName.setText("推荐技师");
		} else {
			tv_techName.setText(workerName);
		}

		tv_techLevel.setText(workerGrade);
		tv_techSex.setText(workerGender);

		tv_price.setText("价格：￥" + NumFormatUtil.centFormatYuanToString(price));

		tv_timeLong.setText(duration + "分钟");
		tv_beltNum.setText(amount + "钟");
		tv_appointTime.setText(serviceStartTime);
		tv_storeName.setText(storeName);
		tv_fuwukaishi.setText(actualBeginTime);
		tv_fuwujieshu.setText(actualEndTime);
		tv_userAddressFlash.setText(storeAddress);
		tv_userMobileFlash.setText(clientMobile);
		tv_storetel.setText("联系门店：" + storeContactTel);

		boolean isGoToStore;
		if ("1".equals(orderClass)) {// 到店
			isGoToStore = true;
		} else {// 上门
			isGoToStore = false;
		}

		boolean isStore;
		if ("1".equals(orderClass) || "2".equals(orderClass)) {// 门店
			isStore = true;
		} else {// 自营
			isStore = false;
		}

		if ("1".equals(orderClass)) {// 到店
			ll_storeName.setVisibility(View.VISIBLE);
			ll_userName.setVisibility(View.GONE);
			ll_userMobile.setVisibility(View.GONE);
			ll_xiaofeima.setVisibility(View.VISIBLE);
			ll_xiaofeima_used.setVisibility(View.VISIBLE);
		} else if ("2".equals(orderClass)) {// 店铺上门
			ll_storeName.setVisibility(View.VISIBLE);
			ll_userName.setVisibility(View.VISIBLE);
			ll_userMobile.setVisibility(View.VISIBLE);
			ll_xiaofeima.setVisibility(View.GONE);
			ll_xiaofeima_used.setVisibility(View.GONE);
			iv_userAddress.setVisibility(View.GONE);
		} else if ("3".equals(orderClass)) {// 自营上门
			ll_storeName.setVisibility(View.GONE);
			ll_userName.setVisibility(View.VISIBLE);
			ll_userMobile.setVisibility(View.VISIBLE);
			ll_xiaofeima.setVisibility(View.GONE);
			ll_xiaofeima_used.setVisibility(View.GONE);
			iv_userAddress.setVisibility(View.GONE);
		}

		if ("1".equals(orderClass)) {// 到店
			tv_userAddress.setText(storeAddress);
		} else {// 上门
			tv_userAddress.setText(clientArea + clientAddress);
		}

		if (!TextUtils.isEmpty(clientGender)) {
			if (clientGender.trim().equals("男")) {
				tv_userName.setText(clientName + " " + "先生");
			} else {
				if (clientGender.trim().equals("女")) {
					tv_userName.setText(clientName + " " + "女士");
				}
			}
		}

		tv_userMobile.setText(clientMobile);

		if (TextUtils.isEmpty(memo)) {
			memo = "无";
		}
		// 判断bedFee是否为0
		if (!TextUtils.isEmpty(bedFee)) {
			if (("0").equals(bedFee)) {
				tv_normalPrice_3.setVisibility(View.GONE);
			} else {
				tv_normalPrice_3.setVisibility(View.VISIBLE);
			}
		} else {
			tv_normalPrice_3.setVisibility(View.GONE);
		}

		// 判断交通费是否为0
		if (!TextUtils.isEmpty(transportationFee)) {
			if (("0").equals(transportationFee)) {
				tv_normalPrice_4.setVisibility(View.GONE);
			} else {
				tv_normalPrice_4.setVisibility(View.VISIBLE);
			}
		} else {
			tv_normalPrice_4.setVisibility(View.GONE);
		}
		// 判断卡券支付金额是否为0
		if (!TextUtils.isEmpty(couponpay)) {
			if (("0").equals(couponpay)) {
				tv_normalPrice_2.setVisibility(View.GONE);
			} else {
				tv_normalPrice_2.setVisibility(View.VISIBLE);
			}
		} else {
			tv_normalPrice_2.setVisibility(View.GONE);
		}

		tv_userBeizhu.setText(memo);

		// String countFee = NumFormatUtil.saveTwoPointOfDouble(Double
		// .parseDouble(price) * Double.parseDouble(amount));
		double countFee = Double.parseDouble(price) * Double.parseDouble(amount);
		// 修改此处小bug===NumFormatUtil.centFormatYuanToString该方法不能用于展示计算后的价格
		// 因为计算后价格可能存在小数
		tv_normalPrice_1.setText("￥" + NumFormatUtil.centFormatYuanToString(price) + "×" + amount + "= ￥"
				+ NumFormatUtil.saveTwoPointOfDouble(NumFormatUtil.centFormatYuanTodouble(Double.toString(countFee))));

		tv_normalPrice_2.setText("优惠券-￥" + NumFormatUtil.centFormatYuanToString(couponpay));
		tv_normalPrice_3.setText("上门带床费￥" + NumFormatUtil.centFormatYuanToString(bedFee));
		tv_normalPrice_4.setText("交通费￥" + NumFormatUtil.centFormatYuanToString(transportationFee));
		String timeLong = "";
		if (!TextUtils.isEmpty(duration) && !TextUtils.isEmpty(amount)) {
			timeLong = NumFormatUtil.saveOnePointOfFloat((Float.parseFloat(duration) * Float.parseFloat(amount)));
		}
		tv_normalPrice_5.setText("总时长：" + timeLong + "分钟");
		tv_normalPrice_6.setText("总计￥" + NumFormatUtil.centFormatYuanToString(payment));
		tv_fuwukaishi.setText(actualBeginTime);
		tv_fuwujieshu.setText(actualEndTime);

		CommonUtil.log("==========================上门订单详情status:" + status);

		// 初始化都不显示
		ll_pay.setVisibility(View.GONE);
		rl_querendingdan.setVisibility(View.GONE);
		rl_jiazhong.setVisibility(View.GONE);
		rl_pinglun.setVisibility(View.GONE);

		if ("1".equals(status)) {// 1待支付（确认订单）
			tv_head.setText("待支付");
			tv_orderStatus.setText("待支付");
			rl_querendingdan.setVisibility(View.VISIBLE);
			rl_top.setVisibility(View.VISIBLE);
			tv_top.setText("10分钟内未完成支付，将取消订单");
			ll_pay.setVisibility(View.VISIBLE);
			ll_xiaofeima.setVisibility(View.GONE);
			ll_xiaofeima_used.setVisibility(View.GONE);

			// 账户余额是否充足
			if (!TextUtils.isEmpty(totalPrice)) {
				totalFee = NumFormatUtil.centFormatYuanTodouble(totalPrice);
				judgeAccountIsEnough();
				accountStillPay(payment);
			}
		} else if ("2".equals(status)) {// 2 已支付
			tv_head.setText("待服务");
			if ("1".equals(orderClass)) {// 到店
				tv_top.setText("温馨提示：未按预约时间到店，请与门店另行协调服务时间和技师。");
				tv_orderStatus.setText("待服务");
				ll_xiaofeima.setVisibility(View.VISIBLE);
				tv_xiaofeima.setText(verifyCode);
				rl_lianxikefu.setVisibility(View.VISIBLE);
				ll_xiaofeima_used.setVisibility(View.GONE);
			} else {// 上门
				rl_top.setVisibility(View.GONE);
				tv_orderStatus.setText("待出发");
				if ("1".equals(isExtensible)) {// 可以加钟
					rl_jiazhong.setVisibility(View.VISIBLE);
				} else if ("0".equals(isExtensible)) {// 可以加钟
					rl_jiazhong.setVisibility(View.GONE);
				}
				if ("2".equals(orderClass)) {// 门店上门
					ll_call.setVisibility(View.VISIBLE);
				} else {
					ll_call.setVisibility(View.GONE);
				}
			}
		} else if ("3".equals(status)) {// 3进行中（技师出发）
			tv_head.setText("待服务");
			tv_orderStatus.setText("已出发");
			if ("1".equals(isExtensible)) {// 可以加钟
				rl_jiazhong.setVisibility(View.VISIBLE);
			} else if ("0".equals(isExtensible)) {// 可以加钟
				rl_jiazhong.setVisibility(View.GONE);
			}
			rl_top.setVisibility(View.VISIBLE);
			tv_top.setText("技师已在" + workerDepartureTime + "出发");
			if ("2".equals(orderClass)) {// 门店上门
				ll_call.setVisibility(View.VISIBLE);
			} else {
				ll_call.setVisibility(View.GONE);
			}
		} else if ("4".equals(status)) {// 4 服务中（开始服务）
			tv_head.setText("待服务");
			tv_orderStatus.setText("服务中");
			if ("1".equals(isExtensible)) {// 可以加钟
				rl_jiazhong.setVisibility(View.VISIBLE);
			} else if ("0".equals(isExtensible)) {// 可以加钟
				rl_jiazhong.setVisibility(View.GONE);
			}
			rl_top.setVisibility(View.VISIBLE);
			tv_top.setText("开始服务时间" + actualBeginTime);
			if ("2".equals(orderClass)) {// 门店上门
				ll_call.setVisibility(View.VISIBLE);
			} else {
				ll_call.setVisibility(View.GONE);
			}
		} else if ("5".equals(status)) {// 5 待评论
			tv_head.setText("待评论");
			rl_top.setVisibility(View.GONE);
			tv_orderStatus.setText("待评论");
			rl_pinglun.setVisibility(View.VISIBLE);
			if ("1".equals(orderClass)) {// 到店
				ll_xiaofeima.setVisibility(View.GONE);
				ll_xiaofeima_used.setVisibility(View.VISIBLE);
				tv_xiaofeima_used.setText(verifyCode);
				tv_xiaofeishijian.setText(verifyTime);
				if ("2".equals(type)) {// 闪付订单
					ll_xiaofeima.setVisibility(View.GONE);
					ll_xiaofeima_used.setVisibility(View.GONE);
				}
			} else {// 上门
				ll_kaishijieshu.setVisibility(View.VISIBLE);
			}
		} else if ("6".equals(status)) {// 6 已评论
			tv_head.setText("已完成");
			rl_top.setVisibility(View.GONE);
			tv_orderStatus.setText("已完成");
			if ("1".equals(orderClass)) {// 到店
				ll_xiaofeima.setVisibility(View.GONE);
				ll_xiaofeima_used.setVisibility(View.VISIBLE);
				tv_xiaofeima_used.setText(verifyCode);
				tv_xiaofeishijian.setText(verifyTime);
				if ("2".equals(type)) {// 闪付订单
					ll_xiaofeima.setVisibility(View.GONE);
					ll_xiaofeima_used.setVisibility(View.GONE);
				}
			} else {// 上门
				ll_kaishijieshu.setVisibility(View.VISIBLE);
			}
			rl_zaiciyuyue.setVisibility(View.VISIBLE);
			if ("2".equals(type)) {// 闪付订单
				rl_zaiciyuyue.setVisibility(View.GONE);
			}
		} else if ("-1".equals(status)) {// -1 取消
			tv_head.setText("已完成");
			rl_top.setVisibility(View.GONE);
			tv_orderStatus.setText("已取消");
			if ("1".equals(orderClass)) {// 到店
				ll_xiaofeima.setVisibility(View.GONE);
				ll_xiaofeima_used.setVisibility(View.VISIBLE);
				tv_xiaofeima_used.setText(verifyCode);
				tv_xiaofeishijian.setText(verifyTime);
				if ("2".equals(type)) {// 闪付订单
					ll_xiaofeima.setVisibility(View.GONE);
					ll_xiaofeima_used.setVisibility(View.GONE);
				}
			} else {// 上门
				ll_kaishijieshu.setVisibility(View.VISIBLE);
			}
		} else if ("-2".equals(status)) {// -1 关闭
			tv_head.setText("已完成");
			rl_top.setVisibility(View.GONE);
			tv_orderStatus.setText("已关闭");
			ll_xiaofeima.setVisibility(View.GONE);
			ll_xiaofeima_used.setVisibility(View.GONE);
			tv_xiaofeima_used.setText(verifyCode);
			tv_xiaofeishijian.setText(verifyTime);
			ll_kaishijieshu.setVisibility(View.GONE);
		} else if ("-3".equals(status)) {// -1 已退款
			tv_head.setText("已完成");
			rl_top.setVisibility(View.GONE);
			tv_orderStatus.setText("已退款");
			if ("1".equals(orderClass)) {// 到店
				ll_yituikuan_store.setVisibility(View.VISIBLE);
				tv_xiaofeima_yituikuan.setText(verifyCode);
				tv_tuikuan_store.setText(NumFormatUtil.centFormatYuanToString(payment) + "");
				tv_tuikuanshijian_store.setText(dobackTime);
				if ("2".equals(type)) {// 闪付订单
					ll_xiaofeima.setVisibility(View.GONE);
					ll_xiaofeima_used.setVisibility(View.GONE);
				}
			} else {// 上门
				ll_yituikuan_door.setVisibility(View.VISIBLE);
				tv_tuikuan_door.setText(NumFormatUtil.centFormatYuanTodouble(payment) + "");
				tv_tuikuanshijian_door.setText(dobackTime);
			}
			ll_xiaofeima.setVisibility(View.GONE);
			ll_xiaofeima_used.setVisibility(View.GONE);
			ll_kaishijieshu.setVisibility(View.GONE);
			rl_chongxinyuyue.setVisibility(View.VISIBLE);
		}

		tv_techLevel.setText(workerGrade);

	}

	private void initListener() {
		ll_back.setOnClickListener(this);
		bt_zhifu.setOnClickListener(this);
		bt_cancel.setOnClickListener(this);
		bt_lianxikefu.setOnClickListener(this);
		bt_woyaojiazhong.setOnClickListener(this);
		bt_woyaopinglun.setOnClickListener(this);
		// bt_fenxiang.setOnClickListener(this);
		bt_zaiciyuyue.setOnClickListener(this);
		bt_chongxinyuyue.setOnClickListener(this);
		ll_techName.setOnClickListener(this);
		ll_servAddress.setOnClickListener(this);
		ll_servAddressFlash.setOnClickListener(this);
		ll_call.setOnClickListener(this);

		// 支付方式
		layout_accountPay.setOnClickListener(this);
		layout_aliPay.setOnClickListener(this);
		layout_wxPay.setOnClickListener(this);
	}

	private void getOrderDetail(String orderID) {
		getOrderDetail = new GetOrderDetail(mContext, mHandler, orderID);
		Thread thread = new Thread(getOrderDetail);
		thread.start();
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				orderDetail_jsonObject = getOrderDetail.getOutJson();
				// Log.e("OrderDetailActivity2",
				// "jsonObject------------------->" + orderDetail_jsonObject);
				orderID = orderDetail_jsonObject.optString("orderID", "");// 订单ID
				amount = orderDetail_jsonObject.optString("amount", "");// 钟数
				serviceID = orderDetail_jsonObject.optString("serviceID", "");// 服务ID
				serviceName = orderDetail_jsonObject.optString("serviceName", "");// 服务名称
				serviceIcon = orderDetail_jsonObject.optString("serviceIcon", "");// 服务图标
				totalPrice = orderDetail_jsonObject.optString("totalPrice", "0");// 订单总价格
				price = orderDetail_jsonObject.optString("price", "0");// 服务单价
				discountAmount = orderDetail_jsonObject.optString("discountAmount", "0");// 优惠金额
				discountName = orderDetail_jsonObject.optString("discountName", "");// 优惠名称
				serviceStartTime = orderDetail_jsonObject.optString("serviceStartTime", "");// 服务时间
				serviceEndTime = orderDetail_jsonObject.optString("serviceEndTime", "");// 服务结束时间
				workerDepartureTime = orderDetail_jsonObject.optString("workerDepartureTime", "");// 技师出发时间
				transportationFee = orderDetail_jsonObject.optString("transportationFee", "0");// 交通费
				bedFee = orderDetail_jsonObject.optString("bedFee", "0");// 床费
				memo = orderDetail_jsonObject.optString("memo", "无");// 备注
				couponpay = orderDetail_jsonObject.optString("couponpay", "");// 卡券支付金额
				payment = orderDetail_jsonObject.optString("payment", "");// 实际支付金额
				status = orderDetail_jsonObject.optString("status", "");// 订单状态
				storeID = orderDetail_jsonObject.optString("storeID", "");// 店铺ID
				storeName = orderDetail_jsonObject.optString("storeName", "");// 店铺名称
				storeAddress = orderDetail_jsonObject.optString("storeAddress", "");// 店铺地址
				storeIcon = orderDetail_jsonObject.optString("storeIcon", "");// 店铺图标
				workerID = orderDetail_jsonObject.optString("workerID", "");// 技师ID
				workerName = orderDetail_jsonObject.optString("workerName", "");// 技师姓名
				workerIcon = orderDetail_jsonObject.optString("workerIcon", "");// 技师头像
				workerGrade = orderDetail_jsonObject.optString("workerGrade", "");// 技师级别
				workerGender = orderDetail_jsonObject.optString("workerGender", "");// 技师性别
				clientName = orderDetail_jsonObject.optString("clientName", "");// 客户姓名
				clientGender = orderDetail_jsonObject.optString("clientGender", "");// 客户性别
				clientArea = orderDetail_jsonObject.optString("clientArea", "");// 客户区域
				clientAddress = orderDetail_jsonObject.optString("clientAddress", "");// 客户地址
				clientMobile = orderDetail_jsonObject.optString("clientMobile", "");// 客户电话
				duration = orderDetail_jsonObject.optString("duration", "");// 单个钟时长
				type = orderDetail_jsonObject.optString("type", "");// 订单类型
				orderClass = orderDetail_jsonObject.optString("orderClass", "");// 服务订单类型1到店,2到店上门,3华佗上门
				orderTime = orderDetail_jsonObject.optString("orderTime", "");// 下单时间
				actualBeginTime = orderDetail_jsonObject.optString("actualBeginTime", "");// 实际开始时间
				actualEndTime = orderDetail_jsonObject.optString("actualEndTime", "");// 实际结束时间
				mobile = orderDetail_jsonObject.optString("mobile", "");// 下单电话
				verifyCode = orderDetail_jsonObject.optString("verifyCode", "");// 验证码
				verifyTime = orderDetail_jsonObject.optString("verifyTime", "");// 验证时间
				isExtended = orderDetail_jsonObject.optString("isExtended", "");// 是否加过钟
				isExtensible = orderDetail_jsonObject.optString("isExtensible", "");// 不可加钟
				ExtentionInfo = orderDetail_jsonObject.optJSONObject("ExtentionInfo");// 加钟信息
				commentCount = orderDetail_jsonObject.optString("commentCount", "0");// 评论数
				skillScore = orderDetail_jsonObject.optString("skillScore", "");// 技师分数，满分5分
				orderCount = orderDetail_jsonObject.optString("orderCount", "0");// 技师服务单数
				longitude = orderDetail_jsonObject.optString("longitude", "");// 门店经度
				latitude = orderDetail_jsonObject.optString("latitude", "");// 门店纬度
				storeContactTel = orderDetail_jsonObject.optString("storeContactTel", "4008551512");// 门店电话
				dobackTime = orderDetail_jsonObject.optString("dobackTime", "");
				initWidget();
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 取消订单
	 */
	private void cancelOrder() {

		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJsonHashMap = new HashMap<String, String>();
		inJsonHashMap.put("orderID", orderID);
		inJsonHashMap.put("userID", MyApplication.getUserID());
		cancelOrder_thread = new CancelOrder_thread(mContext, handler_cancelOrder, inJsonHashMap);
		Thread thread = new Thread(cancelOrder_thread);
		thread.start();
	}

	class Handler_CancelOrder extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// JSONObject jsonObject = getOrderDetail.getOutJson();
				Toast_Util.showToast(mContext, "取消订单成功");
				finish();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				String MSG = cancelOrder_thread.getOutMsg();
				int code = cancelOrder_thread.getOutCode();
				CommonUtil.log("取消订单返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN || code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail),
							Toast.LENGTH_SHORT);
				}
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 取消订单：自定义Dialog
	 */
	private void showCustomDialog() {

		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage("您确定取消订单吗");
		builder.setPositiveButton(mContext.getResources().getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						// 联网 确认收货
						cancelOrder();
					}
				});

		builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}

	private void call() {
		CallUtil.showCallDialog(mContext, storeContactTel);
	}

	/**
	 * 获取账户信息
	 */
	private void getUserInfo() {
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		getUserInfo = new GetUserInfo(mContext, handler_getUserInfo);
		Thread thread = new Thread(getUserInfo);
		thread.start();
	}

	/**
	 * 获取用户信息：---账户余额
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_getUserInfo extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				// closeCustomCircleProgressDialog();
				userObj = getUserInfo.getUserJson();
				if (userObj == null || ("").equals(userObj)) {
					break;
				}
				accountBalance = userObj.optString("deposit", "0");// 账户余额
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}

			getOrderDetail(orderID);

		}
	}

	/**
	 * 计算还需支付金额
	 */
	private void accountStillPay(String totalPrice) {

		// 还需支付
		stillPay = totalFee - accountBalance_double;

		CommonUtil.log("订单金额：totalFee：" + totalFee);
		CommonUtil.log("账户金额：accountBalance_double：" + accountBalance_double);
		if (ISEnoughOfAccountPay) {
			layout_accountPay.setClickable(true);
			// 账户余额足
			intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);
		} else {
			layout_accountPay.setClickable(false);
			// 账户余额不足--默认选中账户
			initDefaultSelectedAccountOfNotEnough();

		}

	}

	/**
	 * 判断月是否充足
	 */
	private void judgeAccountIsEnough() {
		CommonUtil.log("账户金额：accountBalance" + accountBalance);
		if (!TextUtils.isEmpty(accountBalance)) {
			accountBalance_double = NumFormatUtil.centFormatYuanTodouble(accountBalance);

			if (accountBalance_double == 0d) {
				rl_accountPay_line.setVisibility(View.GONE);
				layout_accountPay.setVisibility(View.GONE);
			} else {
				rl_accountPay_line.setVisibility(View.VISIBLE);
				layout_accountPay.setVisibility(View.VISIBLE);
			}
			if (totalFee <= accountBalance_double) {
				// 账户余额足
				ISEnoughOfAccountPay = true;
			} else {
				// 账户余额不足
				ISEnoughOfAccountPay = false;

			}

			tx_accountPay.setText(NumFormatUtil.saveTwoPointOfDouble(accountBalance_double));
		}
	}

	/**
	 * 初始化账户不充足情况下 默认选中账户
	 */
	private void initDefaultSelectedAccountOfNotEnough() {
		isAccount = Constants.ISACCOUNT;
		payChannelCode = Constants.PAYCHANNELCODE_NO;
		FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ACCOUNT;
		// 选中了
		iv_accountPay_select.setVisibility(View.VISIBLE);
		iv_alipay_select.setVisibility(View.GONE);
		iv_wxPay_select.setVisibility(View.GONE);

		account_pay_isSelected = true;
		ali_pay_isSelected = false;
		wx_pay_isSelected = false;
	}

	/**
	 * 判断账户余额不充足,并设置选中的支付方式的变量和参数
	 */
	private void intPayParamsByNotEnoughOfAcccount(int flag) {
		switch (flag) {
		case Constants.PAY_SELECTED_ACCOUNT:// 账户支付
			if (account_pay_isSelected) {

				isAccount = Constants.ISACCOUNT;
				payChannelCode = Constants.PAYCHANNELCODE_NO;
			} else {
				isAccount = Constants.NOT_ACCOUNT;
				payChannelCode = Constants.PAYCHANNELCODE_NO;
				// 记录支付方式都未选中
			}
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ACCOUNT);
			break;
		case Constants.PAY_SELECTED_ALI:// 支付宝支付
			isAccount = Constants.NOT_ACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_ALIPAY;// 支付宝支付渠道
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ALIPAY;
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ALIPAY);
			break;
		case Constants.PAY_SELECTED_WX:// 微信支付

			isAccount = Constants.NOT_ACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_WXPAY;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_WXPAY;
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_WXPAY);

			break;
		case Constants.PAY_SELECTED_ACCOUNT_ALI:// 支付宝+账户（选中）
			isAccount = Constants.ISACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_ALIPAY;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ALIPAY;
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ACCOUNT_ALIPAY);

			break;
		case Constants.PAY_SELECTED_ACCOUNT_WX:// 微信+账户（选中）
			isAccount = Constants.ISACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_WXPAY;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_WXPAY;
			changePayTypeByNotEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ACCOUNT_WXPAY);
			break;
		}
	}

	/**
	 * 判断账户余额充足,并设置选中的支付方式的变量和参数
	 */
	private void intPayParamsByEnoughOfAcccount(int flag) {
		switch (flag) {
		case Constants.PAY_SELECTED_ACCOUNT:// 账户支付
			// 账户余额足
			isAccount = Constants.ISACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_NO;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ACCOUNT;
			changePayTypeByEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ACCOUNT);
			break;
		case Constants.PAY_SELECTED_ALI:// 支付宝支付
			isAccount = Constants.NOT_ACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_ALIPAY;// 支付宝支付渠道
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ALIPAY;
			changePayTypeByEnoughOfAccountPay(Constants.FLAG_PAYTYPE_ALIPAY);
			break;
		case Constants.PAY_SELECTED_WX:// 微信支付

			isAccount = Constants.NOT_ACCOUNT;
			payChannelCode = Constants.PAYCHANNELCODE_WXPAY;
			FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_WXPAY;
			changePayTypeByEnoughOfAccountPay(Constants.FLAG_PAYTYPE_WXPAY);
			break;
		}
	}

	/**
	 * 改变支付宝、微信
	 * 
	 * @param flag
	 */
	private void changePayTypeByNotEnoughOfAccountPay(int flag) {
		if (account_pay_isSelected) {
			// 选中了
			iv_accountPay_select.setVisibility(View.VISIBLE);
		} else {
			// 取消选中了
			iv_accountPay_select.setVisibility(View.GONE);
		}
		switch (flag) {
		case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户支付

			break;
		case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝
			iv_wxPay_select.setVisibility(View.GONE);
			iv_alipay_select.setVisibility(View.VISIBLE);
			break;
		case Constants.FLAG_PAYTYPE_WXPAY:// 微信
			iv_alipay_select.setVisibility(View.GONE);
			iv_wxPay_select.setVisibility(View.VISIBLE);
			break;
		case Constants.FLAG_PAYTYPE_ACCOUNT_ALIPAY:// 账户(是否选中)+支付宝）
			iv_alipay_select.setVisibility(View.VISIBLE);
			iv_wxPay_select.setVisibility(View.GONE);
			break;
		case Constants.FLAG_PAYTYPE_ACCOUNT_WXPAY:// // 账户(是否选中)+微信
			iv_alipay_select.setVisibility(View.GONE);
			iv_wxPay_select.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * 改变支付宝、微信
	 * 
	 * @param flag
	 */
	private void changePayTypeByEnoughOfAccountPay(int flag) {
		iv_accountPay_select.setVisibility(View.GONE);
		iv_alipay_select.setVisibility(View.GONE);
		iv_wxPay_select.setVisibility(View.GONE);
		switch (flag) {
		case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户支付
			iv_accountPay_select.setVisibility(View.VISIBLE);
			break;
		case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝
			iv_alipay_select.setVisibility(View.VISIBLE);
			break;
		case Constants.FLAG_PAYTYPE_WXPAY:// 微信
			iv_wxPay_select.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * 初始化提交参数
	 */
	private HashMap<String, String> initParams() {
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("orderID", orderID);
		inJsonMap.put("payType", "1");
		inJsonMap.put("orderType", "");
		inJsonMap.put("isAccount", isAccount);
		inJsonMap.put("payChannelCode", payChannelCode);
		// Log.e("FLAG_PAYTYPE", "FLAG_PAYTYPE------------------->" +
		// FLAG_PAYTYPE);
		return inJsonMap;
	}

	/**
	 * 账户支付弹框提示
	 */
	private void commitOrderIsShowTip() {
		switch (FLAG_PAYTYPE) {
		case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户余额支付成功
			showDialog();
			break;
		case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝支付成功
		case Constants.FLAG_PAYTYPE_WXPAY:// 微信支付成功

			pay();

			break;
		}
	}

	private void showDialog() {

		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage(
				getString(R.string.appoint_dialog_tips) + "￥" + NumFormatUtil.saveTwoPointOfDouble(totalFee));
		builder.setPositiveButton(mContext.getResources().getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						pay();
					}
				});

		builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private void pay() {
		initParams();
		HashMap<String, String> inJsonMap = initParams();
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		pay_thread = new Pay_thread(mContext, handler_Pay, inJsonMap);
		Thread thread = new Thread(pay_thread);
		thread.start();
	}

	/**
	 * 处理支付结果
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_Pay extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// Log.e("FLAG_PAYTYPE",
				// "FLAG_PAYTYPE10101010---------------------->" +
				// FLAG_PAYTYPE);
				CommonUtil.log("=========================支付界面：FLAG_PAYTYPE:" + FLAG_PAYTYPE);
				orderObj = pay_thread.getOutPayObjecct();
				// 支付成功后 跳转的页面
				jumpAfterCommitOrderSuccess();
				break;

			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();

				String MSG = pay_thread.getOutMsg();
				int code = pay_thread.getOutCode();
				CommonUtil.log("支付后返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN || code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail),
							Toast.LENGTH_SHORT);

				}

				break;

			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;

			}
		}

	}

	/**
	 * 支付接口成功后的跳转
	 */
	private void jumpAfterCommitOrderSuccess() {
		// 支付实际金额大于0
		if (totalFee > 0d) {
			switch (FLAG_PAYTYPE) {
			case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户余额支付成功
				Toast_Util.showToast(this, "支付成功");// 刷新数据
				getUserInfo();//
				break;
			case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝支付成功
				// Toast_Util.showToast(this, "支付宝支付");
				// 初始化支付宝参数
				initAlipayData();
				if (null != alipayData) {
					PayUtil.getInstance().aliPay(this, alipayData, orderID);
				} else {
					CommonUtil.logE("支付宝alipayData为空");
				}
				break;
			case Constants.FLAG_PAYTYPE_WXPAY:// 微信支付成功
				// Toast_Util.showToast(this, "微信支付");
				// 初始化微信支付参数
				initTenpayData();
				if (null != tenpayData) {
					PayUtil.getInstance().wxPay(this, tenpayData, orderID);
				} else {
					CommonUtil.logE("微信tenpayData为空");
				}

				break;
			}
		} else {
			Toast_Util.showToast(this, "支付成功");// 刷新数据
			getUserInfo();//
		}
	}

	/**
	 * 处理支付宝返回数据
	 */
	private void initAlipayData() {
		// CommonUtil.log("alipay_jsonObject-------------->" + pay_jsonObject);
		if (null != orderObj) {
			alipayData = orderObj.optString("alipayData", "");
		}
		CommonUtil.log("alipayData-------------->" + alipayData);
	}

	/**
	 * 处理微信支付返回数据
	 */
	private void initTenpayData() {
		CommonUtil.log("pay_jsonObject-------------->" + "开始初始化微信支付");
		if (null != orderObj) {
			String tenpayData_str = orderObj.optString("tenpayData", "");
			if (!TextUtils.isEmpty(tenpayData_str)) {
				try {
					tenpayData = new JSONObject(tenpayData_str);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		CommonUtil.log("tenpayData-------------->" + tenpayData);
		CommonUtil.log("new_orderID-------------->" + orderID);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		switch (v.getId()) {
		case R.id.layout_accountPay:// 账户支付
			if (account_pay_isSelected) {
				account_pay_isSelected = false;
			} else {
				account_pay_isSelected = true;
			}
			CommonUtil.log("account_pay_isSelected:" + account_pay_isSelected);
			CommonUtil.log("ali_pay_isSelected:" + ali_pay_isSelected);
			CommonUtil.log("wx_pay_isSelected:" + wx_pay_isSelected);
			CommonUtil.log("ISEnoughOfAccountPay:" + ISEnoughOfAccountPay);
			if (ISEnoughOfAccountPay) {
				intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);
			} else {
				if (account_pay_isSelected && !ali_pay_isSelected && !wx_pay_isSelected) {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);

				} else if (account_pay_isSelected && ali_pay_isSelected && !wx_pay_isSelected) {// 账户+支付宝
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_ALI);
				} else if (account_pay_isSelected && !ali_pay_isSelected && wx_pay_isSelected) {// 账户+微信
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_WX);
				} else if (!account_pay_isSelected && ali_pay_isSelected && !wx_pay_isSelected) {// 支付宝
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ALI);
				} else if (!account_pay_isSelected && !ali_pay_isSelected && wx_pay_isSelected) {// 微信
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_WX);
				} else if (!account_pay_isSelected && !ali_pay_isSelected && !wx_pay_isSelected) {// 无支付方式
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);
				}
			}
			break;
		case R.id.layout_alipay:// 支付宝支付

			if (ISEnoughOfAccountPay) {
				intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_ALI);
			} else {

				// 账户不充足--第三方支付 二选一--该点击：选中 支付宝
				ali_pay_isSelected = true;
				wx_pay_isSelected = false;

				CommonUtil.log("account_pay_isSelected:" + account_pay_isSelected);
				if (account_pay_isSelected) {

					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_ALI);
				} else {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ALI);
				}
			}
			break;
		case R.id.layout_wxPay:// 微信支付
			if (ISEnoughOfAccountPay) {
				intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_WX);
			} else {
				// 账户不充足--第三方支付 二选一--该点击：选中 微信
				ali_pay_isSelected = false;
				wx_pay_isSelected = true;
				CommonUtil.log("account_pay_isSelected:" + account_pay_isSelected);
				if (account_pay_isSelected) {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_WX);
				} else {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_WX);
				}

			}
			break;
		case R.id.bt_zhifu:// 支付

			CommonUtil.log("ISEnoughOfAccountPay:" + ISEnoughOfAccountPay);
			CommonUtil.log("FLAG_PAYTYPE:" + FLAG_PAYTYPE);

			if (!ISEnoughOfAccountPay) {
				// 账户余额不足
				if (!ali_pay_isSelected && !wx_pay_isSelected) {
					if (accountBalance_double > 0d) {
						Toast_Util.showToast(mContext, mContext.getResources().getString(R.string.pay_no_enough_tips));
					} else {
						Toast_Util.showToast(mContext,
								mContext.getResources().getString(R.string.pay_no_choice_payType));
					}
				} else {
					commitOrderIsShowTip();
				}
			} else {
				commitOrderIsShowTip();
			}
			// finish();
			break;
		case R.id.bt_orderDetail_cancel:// 取消订单
			showCustomDialog();
			break;
		case R.id.bt_lianxikefu:
			call();
			break;
		case R.id.ll_techName:
			if (!workerID.isEmpty()) {
				intent.setClass(mContext, TechnicianDetail.class);
				intent.putExtra("ID", workerID);
				startActivity(intent);
			}
			break;
		case R.id.ll_servAddress:
			if (!storeAddress.isEmpty() && "1".equals(orderClass)) {
				GuideToStoreUtil.getInstance().jumpToGuideActivity(this, latitude, longitude, storeAddress, storeName);
			}
			break;
		case R.id.ll_servAddressFlash:
			if (!storeAddress.isEmpty() && "1".equals(orderClass)) {
				GuideToStoreUtil.getInstance().jumpToGuideActivity(this, latitude, longitude, storeAddress, storeName);
			}
			break;
		case R.id.bt_woyaojiazhong:// 加钟
			GoToAddTime.getInstance().goToAddTime(this, orderID);
			break;
		case R.id.bt_woyaopinglun:// 评论
			intent.setClass(mContext, DianPingActivity.class);
			intent.putExtra("ID", orderID);
			intent.putExtra("workerID", workerID);
			intent.putExtra("orderClass", orderClass);
			mContext.startActivity(intent);
			finish();
			break;
		case R.id.bt_zaiciyuyue:// 再次预约
			UmengEventUtil.order_orderdetail_again(mContext);
			AgainAppoint.getInstance().againAppoint(OrderDetailActivity.this, orderClass, serviceID, workerID);
			break;
		case R.id.bt_chongxinyuyue:// 重新预约
			AgainAppoint.getInstance().againAppoint(OrderDetailActivity.this, orderClass, serviceID, workerID);
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			getUserInfo();
			break;
		case R.id.ll_call:
			CallUtil.showCallDialog(mContext, storeContactTel);
			break;
		case R.id.ll_back1:// 处理返回键
			handleOnBackPress();
			break;
		}

	}

	/**
	 * 处理返回键
	 */
	private void handleOnBackPress() {
		// 此处用来区别 order是从哪里来的：ORDER_LIST:订单列表---另者支付页面
		boolean has_open_app = CommonUtil.getBooleanOfSharedPreferences(this, "HAS_OPEN_APP", false);
		CommonUtil.logE("是否已经打开app：has_open_app：" + has_open_app);
		// 点击通知后或者首图--app未打开状态 -返回时回到主页面home
		if (push && !has_open_app) {
			CommonUtil.saveBooleanOfSharedPreferences(this, "HAS_OPEN_APP", true);
			JumpTargetActivityUtil.getInstance().jumpToHomeActivity(this, 0);

		} else {
			CommonUtil.log("orderFrom:" + orderFrom);
			if (TextUtils.isEmpty(orderFrom)) {
				JumpTargetActivityUtil.getInstance().jumpToMyOrderList(this, status);
				finish();
			} else if ((Constants.NOPAY).equals(orderFrom)) {
				finish();
			}
		}
	}

	@Override
	public void onBackPressed() {
		handleOnBackPress();
	}

}
