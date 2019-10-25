package com.huatuo.activity.pay;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import com.huatuo.activity.home.HomeActivity;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.activity.order.OrderDetail_Flash_Buy_Activity;
import com.huatuo.adapter.SecKill_Session_detail_list_servInfo_list_Adapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.bean.SecKillActivitydescBean;
import com.huatuo.bean.SecKillPayBean;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.CommitSecKillOrder_Thread;
import com.huatuo.net.thread.GetUserInfo;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengPushUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 
 * @author Android开发工程师 王润泽
 * 
 */
public class Pay_SecKillActivity extends BaseActivity implements
		OnClickListener {
	private Map<Integer, Boolean> map_isClickable = new HashMap<Integer, Boolean>();
	private ImageView iv_pro_icon;
	private TextView tv_pay_serviceName, tv_pay_orderMoney, tv_accountBalance,
			tv_stillPay;
	private CustomListView lv_secskill_project_info;
	private RelativeLayout rl_secskill_project_info;
	private RelativeLayout layout_stillPay;
	private LinearLayout layout_zhifu_back;
	private Button bt_pay_commit;
	private RelativeLayout rl_commit;

	private Context mContext;
	private String servName = "";// 服务项目名称
	private String service_price = "";
	private String serviceIcon = "";
	private double totalFee;// 消费金额
	private String priceID = "";// 价格ID
	private ArrayList<SecKillActivitydescBean> activitydesc;// 服务说明
	private String activityID = "";// 活动ID
	private String orderType = "0";// 订单类型

	private Handler handler_Pay;
	private CommitSecKillOrder_Thread commitSecKillOrder_Thread;
	// 账户余额
	private GetUserInfo getUserInfo;
	private JSONObject userObj;
	private Handler handler_getUserInfo;

	// 支付选择
	private RelativeLayout layout_wxPay, layout_aliPay, layout_accountPay,
			rl_accountPay_line;
	private ImageView iv_alipay_select, iv_wxPay_select, iv_accountPay_select;

	// 支付
	private TextView tx_accountPay;// 账户余额
	private String accountBalance = "0";
	private double accountBalance_double;// 账户余额
	private double stillPay;// 最终需要支付金额
	private boolean ISEnoughOfAccountPay = true;// 账户余额是否充足

	private String isAccount = "";// 账户余额支付：1
	private String payChannelCode;// 支付渠道编号

	private boolean account_pay_isSelected = true;
	private boolean ali_pay_isSelected = false;
	private boolean wx_pay_isSelected = false;

	private int FLAG_PAYTYPE = -1;// -1:代表账户余额：0：表示不用支付宝（账户）或者线下；1：表示支付宝--调用支付宝；2：表示微信支付--调用微信APP

	// 提交订单成后返回支付
	private JSONObject orderObj = null;
	private String orderID = "";
	private String alipayData;
	private JSONObject tenpayData;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_seckill_pay);
		mContext = this;
		initHandler();
		initWidget();
		getUserInfo();
	}

	private void initHandler() {
		handler_Pay = new Handler_Pay();
		handler_getUserInfo = new Handler_getUserInfo();
	}

	private void initWidget() {
		layout_zhifu_back = (LinearLayout) findViewById(R.id.layout_zhifu_back);
		iv_pro_icon = (ImageView) findViewById(R.id.iv_pro_icon);
		tv_pay_serviceName = (TextView) findViewById(R.id.tv_pay_serviceName);
		tv_pay_orderMoney = (TextView) findViewById(R.id.tv_pay_orderMoney);
		lv_secskill_project_info = (CustomListView) findViewById(R.id.lv_secskill_project_info);
		rl_secskill_project_info = (RelativeLayout) findViewById(R.id.rl_secskill_project_info);
		// 支付
		layout_accountPay = (RelativeLayout) findViewById(R.id.layout_accountPay);
		layout_aliPay = (RelativeLayout) findViewById(R.id.layout_alipay);
		layout_wxPay = (RelativeLayout) findViewById(R.id.layout_wxPay);

		rl_accountPay_line = (RelativeLayout) findViewById(R.id.rl_accountPay_line);

		iv_accountPay_select = (ImageView) findViewById(R.id.iv_accountPay_select);
		iv_alipay_select = (ImageView) findViewById(R.id.iv_alipay_select);
		iv_wxPay_select = (ImageView) findViewById(R.id.iv_wxPay_select);

		// 支付
		tx_accountPay = (TextView) findViewById(R.id.tx_accountPay);
		rl_commit = (RelativeLayout) findViewById(R.id.rl_commit);
		bt_pay_commit = (Button) findViewById(R.id.bt_appoint_commit);
		initListener();

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	private void initListener() {
		layout_zhifu_back.setOnClickListener(this);
		bt_pay_commit.setOnClickListener(this);

		// 支付方式
		layout_accountPay.setOnClickListener(this);
		layout_aliPay.setOnClickListener(this);
		layout_wxPay.setOnClickListener(this);
	}

	/**
	 * 接受订单页面数据
	 */
	private void getSecKillServInfo() {
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {

			SecKillPayBean secKillPayBean = (SecKillPayBean) bundle
					.getSerializable("SecKillPayBean");
			if (secKillPayBean != null) {
				servName = secKillPayBean.serviceName;
				serviceIcon = secKillPayBean.serviceIcon;
				activityID = secKillPayBean.activityID;
				service_price = secKillPayBean.minPrice;
				activitydesc = secKillPayBean.activitydesc;
				priceID = secKillPayBean.priceID;
			}

		}
		setDataToView();

		// 账户余额是否充足
		judgeAccountIsEnough();
		accountStillPay();
	}

	private void setDataToView() {
		ImageLoader.getInstance().displayImage(
				serviceIcon,
				iv_pro_icon,
				ImageLoader_DisplayImageOptions.getInstance()
						.setDefaultImageSmallImg());// 服务项目图片icon
		tv_pay_serviceName.setText(servName);
		if (!TextUtils.isEmpty(service_price)) {
			totalFee = NumFormatUtil.centFormatYuanTodouble(service_price);
			tv_pay_orderMoney.setText("￥"
					+ NumFormatUtil.centFormatYuanToString(service_price));
		}

		// activitydesc 活动说明
		if (CommonUtil.emptyListToString3(activitydesc)) {
			rl_secskill_project_info.setVisibility(View.VISIBLE);
			// 服务介绍列表
			SecKill_Session_detail_list_servInfo_list_Adapter servInfo_list_Adapter = new SecKill_Session_detail_list_servInfo_list_Adapter(
					this);
			lv_secskill_project_info.setAdapter(servInfo_list_Adapter);
			servInfo_list_Adapter.add(activitydesc);
			servInfo_list_Adapter.notifyDataSetChanged();
		} else {
			rl_secskill_project_info.setVisibility(View.GONE);
		}

	}

	/**
	 * 获取账户信息
	 */
	private void getUserInfo() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
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
				rl_commit.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				userObj = getUserInfo.getUserJson();
				if (userObj == null || ("").equals(userObj)) {
					break;
				}
				accountBalance = userObj.optString("deposit", "0");// 账户余额
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_commit.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_commit.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}
			getSecKillServInfo();// 获取秒杀项目信息

		}
	}

	/**
	 * 计算还需支付金额
	 */
	private void accountStillPay() {

		CommonUtil.log("订单金额：totalFee：" + stillPay);
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
			accountBalance_double = NumFormatUtil
					.centFormatYuanTodouble(accountBalance);

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

			tx_accountPay.setText(NumFormatUtil
					.saveTwoPointOfDouble(accountBalance_double));
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
		// userID 用户id ANS 64 M 用户id
		// activityID 活动id ANS 64 M 秒杀活动id
		// priceID 秒杀价格id ANS 64 C 启用价格等级时必录
		// isPaidByDeposit 余额支付 ANS 16 C 1：使用余额（不使用余额不传）
		// payChannelCode 支付渠道编号 ANS 64 C 微信：tenpay_js
		// 支付宝支付：alipay_app
		// 客户端微信支付：tenpay_app
		// 客户端支付宝支付：alipay_sdk
		// client 客户端类型 ANS 64 O 客户端类型， wechat, ios, android
		// redirectUrl 支付宝窗口同步返回地址
		// ANS
		// 64 O 支付宝窗口同步返回地址
		// 支付宝wap支付alipay_app
		// deviceToken 友盟device_token ANS 64 C 友盟的device_token

		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("client", "android");
		inJsonMap.put("activityID", activityID);
		inJsonMap.put("priceID", priceID);
		inJsonMap.put("isPaidByDeposit", isAccount);
		inJsonMap.put("payChannelCode", payChannelCode);
		inJsonMap.put("deviceToken", UmengPushUtil.deviceToken);
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
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage(getString(R.string.appoint_dialog_tips) + "￥"
				+ NumFormatUtil.centFormatYuanToString(service_price));
		builder.setPositiveButton(getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						pay();
					}
				});

		builder.setNegativeButton(getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	private void pay() {
		HashMap<String, String> inJsonMap = initParams();
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		commitSecKillOrder_Thread = new CommitSecKillOrder_Thread(mContext,
				handler_Pay, inJsonMap);
		Thread thread = new Thread(commitSecKillOrder_Thread);
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
				// "FLAG_PAYTYPE10101010---------------------->"
				// + FLAG_PAYTYPE);
				CommonUtil.log("=========================支付界面：FLAG_PAYTYPE:"
						+ FLAG_PAYTYPE);
				orderObj = commitSecKillOrder_Thread.getOutJsonObject();
				if (orderObj != null) {
					// orderID 订单ID ANS 32 M 订单号
					// payType 支付方式 ANS 32 M 0：线下支付
					// 1：在线支付
					// orderType 订单类型 ANS 1 C 0 : 默认服务订单
					// 1：充值
					// isAccount 余额支付 ANS 16 C 1：使用余额（不使用余额不传）
					// payChannelCode 支付渠道编号 ANS 64 C 支付方式payType=1选填与余额支付至少填一个
					// 微信等第三方：微信支付tenpay_js,支付宝支付alipay_js
					// 客户端app：支付宝支付alipay_sdk
					// 微信支付tenpay_app
					//
					// isPaidByDeposit 余额支付 ANS 16 C 1：使用余额（不使用余额不传）
					orderID = orderObj.optString("orderID", "");
					// 支付成功后 跳转的页面
					jumpAfterCommitOrderSuccess();
				} else {
					CommonUtil.logE("支付返回信息唯空");
				}
				break;

			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();

				String MSG = commitSecKillOrder_Thread.getOutMsg();
				int code = commitSecKillOrder_Thread.getOutCode();
				CommonUtil.log("支付后返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN
						|| code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils
							.showToastMsg(
									mContext,
									getString(R.string.common_toast_net_down_data_fail),
									Toast.LENGTH_SHORT);

				}

				break;

			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
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
				Toast_Util.showToast(this, "支付成功");
				/**
				 * 调往订单详情
				 */
				Intent intent = new Intent(mContext,
						OrderDetail_Flash_Buy_Activity.class);
				intent.putExtra("orderID", orderID);
				startActivity(intent);
				finish();
				break;
			case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝支付成功
				// Toast_Util.showToast(this, "支付宝支付");
				// 初始化支付宝参数
				initAlipayData();
				if (null != alipayData) {
					PaySecKillUtil.getInstance().aliPay(this, alipayData,
							orderID);
				} else {
					CommonUtil.logE("支付宝alipayData为空");
				}
				break;
			case Constants.FLAG_PAYTYPE_WXPAY:// 微信支付成功
				// Toast_Util.showToast(this, "微信支付");
				// 初始化微信支付参数
				initTenpayData();
				if (null != tenpayData) {
					PaySecKillUtil.getInstance().wxPay(this, tenpayData,
							orderID);
				} else {
					CommonUtil.logE("微信tenpayData为空");
				}
			}
		} else {
			/**
			 * 调往订单详情
			 */
			Intent intent = new Intent(mContext,
					OrderDetail_Flash_Buy_Activity.class);
			intent.putExtra("orderID", orderID);
			startActivity(intent);
			finish();
			// Toast_Util.showToastOnlyOne(mContext, mContext.getResources()
			// .getString(R.string.cannot_wx_ali_pay), Gravity.CENTER,
			// true);
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
	}

	/**
	 * ：自定义Dialog
	 */
	private void showCustomDialog() {

		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("您确定取消支付操作吗？");
		builder.setPositiveButton(getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						Intent intent = new Intent(Pay_SecKillActivity.this,
								HomeActivity.class);
						intent.putExtra("tabIndex", 3);
						startActivity(intent);
						finish();
					}
				});

		builder.setNegativeButton(getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// showCustomDialog();
		super.onBackPressed();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_zhifu_back:
			onBackPressed();
			break;
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
				if (account_pay_isSelected && !ali_pay_isSelected
						&& !wx_pay_isSelected) {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);

				} else if (account_pay_isSelected && ali_pay_isSelected
						&& !wx_pay_isSelected) {// 账户+支付宝
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_ALI);
				} else if (account_pay_isSelected && !ali_pay_isSelected
						&& wx_pay_isSelected) {// 账户+微信
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_WX);
				} else if (!account_pay_isSelected && ali_pay_isSelected
						&& !wx_pay_isSelected) {// 支付宝
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ALI);
				} else if (!account_pay_isSelected && !ali_pay_isSelected
						&& wx_pay_isSelected) {// 微信
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_WX);
				} else if (!account_pay_isSelected && !ali_pay_isSelected
						&& !wx_pay_isSelected) {// 无支付方式
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);
				}
			}
			break;
		case R.id.layout_alipay:// 支付宝支付

			if (ISEnoughOfAccountPay) {
				intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_ALI);
			} else {

				// 账户不充足--第三方支付 二选一--该点击选中 支付宝
				ali_pay_isSelected = true;
				wx_pay_isSelected = false;

				CommonUtil.log("account_pay_isSelected:"
						+ account_pay_isSelected);
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
				CommonUtil.log("account_pay_isSelected:"
						+ account_pay_isSelected);
				if (account_pay_isSelected) {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT_WX);
				} else {
					intPayParamsByNotEnoughOfAcccount(Constants.PAY_SELECTED_WX);
				}

			}
			break;
		case R.id.bt_appoint_commit:// 提交订单
			if (!MyApplication.getLoginFlag()) {
				// showCustomCircleProgressDialog("请登录", "你尚未登录,请登录!");
				DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
						Toast.LENGTH_SHORT);
				Intent intent = new Intent();
				intent.setClass(mContext, LoginActivity.class);
				startActivity(intent);

				break;
			}

			if (!ISEnoughOfAccountPay) {
				// 账户余额不足
				if (!ali_pay_isSelected && !wx_pay_isSelected) {
					if (accountBalance_double > 0d) {
						Toast_Util.showToast(mContext, mContext.getResources()
								.getString(R.string.pay_no_enough_tips));
					} else {
						Toast_Util.showToast(mContext, mContext.getResources()
								.getString(R.string.pay_no_choice_payType));
					}
				} else {
					commitOrderIsShowTip();
				}
			} else {
				commitOrderIsShowTip();
			}

			break;

		case R.id.rl_loadData_error:// 重新加载数据
			getUserInfo();
			break;
		}

	}

}
