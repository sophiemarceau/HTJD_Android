package com.huatuo.activity.appoint;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.activity.order.OrderDetailActivity;
import com.huatuo.activity.pay.PayUtil;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.CommitOrder_Thread_Store;
import com.huatuo.net.thread.GetUserInfo;
import com.huatuo.net.thread.UseableCouponList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.StringUtil;
import com.huatuo.util.Toast_Util;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 上门预约 根据项目
 * 
 * @author Android开发工程师 王润泽
 * 
 */
public class AddTime_Activity extends BaseActivity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_appointment_add_clock);
		mContext = this;
		initWidget();
		receiveExtras();
		initOnClickListener();
		initHandler();
		getUserInfo();// 获取账户余额----获取订单详情

	}

	private void receiveExtras() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			String addTime_info = bundle.getString("ADDTIME_INFO");
			if (!TextUtils.isEmpty(addTime_info)) {
				try {
					addTime_info_Obj = new JSONObject(addTime_info);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Bundle bundle;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_SELECT_COUPONS:// 选择优惠券
				bundle = data.getExtras();
				if (null != bundle) {
					String couponsInfo = bundle.getString("COUPONS");
					int isUse_Cunpon = bundle.getInt(Constants.ISUSE_CUNPON);
					switch (isUse_Cunpon) {
					case Constants.CANCEL_CUNPON:// 不使用优惠券
						cancelCunpon();
						break;
					case Constants.USE_CUNPON:// 使用优惠券'
						if (!TextUtils.isEmpty(couponsInfo)) {
							try {
								initCunpon(new JSONObject(couponsInfo));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						}
						break;
					}

				}
				break;
			}
		}

	}

	/**
	 * 获取账户信息
	 */
	private void getUserInfo() {
		// showCustomCircleProgressDialog(null,
		// getString(R.string.common_toast_net_prompt_submit));
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
			rl_commit.setVisibility(View.VISIBLE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
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
			handleData();
		}
	}

	private void handleData() {
		// orderID 订单ID ANS 64 M 订单ID
		// serviceID 服务ID ANS 64 M 服务ID
		// serviceName 服务名称 ANS 64 M 服务名称
		// serviceIcon 服务图标 ANS 256 M 服务图标
		// serviceTime 服务时间 ANS 64 M 原订单的服务结束时间
		// gradePrice 服务等级价格 ANS 64 M 服务等级价格，单位分
		// marketPrice 市场价格 ANS 64 M 市场价格，单位分
		// memo 备注 ANS 64 M 备注
		// workerID 技师 ID ANS 64 O 技师ID
		// workerName 技师姓名 ANS 64 O 技师姓名
		// workerGrade 技师级别 ANS 64 O 技师级别
		// clientName 客户姓名 ANS 64 O 客户姓名
		// clientGender 客户性别 ANS 64 O 客户性别
		// clientAddress 客户地址 ANS 64 O 客户地址
		// clientMobile 客户电话 ANS 64 O 客户电话
		// duration 单个钟时长 ANS 64 O 单个钟时长
		// ExtentionInfo
		if (null != addTime_info_Obj) {
			orderID = addTime_info_Obj.optString("orderID", "");// 订单ID
			servID = addTime_info_Obj.optString("serviceID", "");// 服务ID
			servName = addTime_info_Obj.optString("serviceName", "");// 服务名称
			imgUrl = addTime_info_Obj.optString("serviceIcon", "");// 服务图标
			String servType_str = addTime_info_Obj.optString("serviceType", "");// 服务类型
			if (!TextUtils.isEmpty(servType_str)) {
				servType = Integer.parseInt(servType_str.trim());
			}
			// countFee = addTime_info_Obj.optString("totalPrice", "");//
			// 订单总价格
			gradePrice = addTime_info_Obj.optString("gradePrice", "");// 服务单价
			marketPrice = addTime_info_Obj.optString("marketPrice", "");// 服务单价
			serviceTime_old = addTime_info_Obj.optString("serviceTime", "");

			// 技师信息
			skillWorkerID = addTime_info_Obj.optString("workerID", "");// 技师ID
			skillWorkerName = addTime_info_Obj.optString("workerName", "");// 技师姓名
			skillGrade = addTime_info_Obj.optString("workerGrade", "");// 技师级别
			// 用户信息
			userName = addTime_info_Obj.optString("clientName", "");// 客户姓名
			userSex = addTime_info_Obj.optString("clientGender", "");// 客户性别
			userArea = addTime_info_Obj.optString("clientArea", "");// 客户地址
			address = addTime_info_Obj.optString("clientAddress", "");// 客户地址
			addressID = addTime_info_Obj.optString("addressID", "");// 客户地址
			mobile = addTime_info_Obj.optString("clientMobile", "");// 客户电话

			servTime = addTime_info_Obj.optString("duration", "");// 单个钟时长

			JSONObject ExtentionInfo = addTime_info_Obj
					.optJSONObject("ExtentionInfo");// 加钟信息

			setDataToView();
			// 处理加钟信息
			handleAddBeltInfo(ExtentionInfo);

			if (maxAddClockNum == 0.5) {//
				changeAddButtonStyle(false);
			}

			formatPrice();// 处理单价价格
			getFeeFromMap(oneBelt);// 初始化加钟数1
			countTotalTime();// 初始化加一钟时间
			countFee();// 计算钟数对应的价格
			countTotalFee();// 计算总价格
			tv_zhongshu.setText(String.valueOf(oneBelt));// 初始化 加钟变化的数目为
			tv_clockNum.setText("X" + String.valueOf(oneBelt));// 初始化 加钟变化的数目为

		}
	}

	/**
	 * 展示订单信息
	 */
	private void setDataToView() {

		CommonUtil.log("加钟--gradePrice:" + gradePrice);
		ImageLoader.getInstance().displayImage(
				imgUrl,
				iv_icon,
				ImageLoader_DisplayImageOptions.getInstance()
						.setDefaultImageSmallImg());// 服务项目图片icon
		ImageLoader.getInstance().displayImage(imgUrl, iv_icon);// 服务项目图片icon
		tv_serverName.setText(servName);// 服务项目名字
		tv_shijian.setText(servTime + "分钟");// 服务项目服务时间
		tv_serviceTime.setText(serviceTime_old);
		if (!TextUtils.isEmpty(skillWorkerName)
				&& !TextUtils.isEmpty(StringUtil.replaceBlank(skillWorkerName))) {
			// 展示选的技师姓名+级别
			tv_xuanzejishi.setText(skillWorkerName + " " + skillGrade);
		} else {
			tv_xuanzejishi.setText("推荐技师");
		}

		// 用户信息
		if (!TextUtils.isEmpty(userSex)) {
			if (userSex.trim().equals("男")) {
				// userSex = "1";
				tv_serviceAddress_contactName.setText(userName + " 先生");

			} else {
				if (userSex.trim().equals("女")) {
					// userSex = "2";
					tv_serviceAddress_contactName.setText(userName + " 女士");
				}
			}
		}

		tv_serviceAddress_contactPhone.setText(mobile);
		tv_serviceAddress.setText(userArea + address);

	}

	/**
	 * 处理加钟最大数
	 */
	private void handleAddBeltInfo(JSONObject addBeltInfo_str) {
		if (null != addBeltInfo_str) {
			clock_trafficFee_map = JsonUtil
					.JSon2Map_TwoLayerMap(addBeltInfo_str);

			HashMap<String, String> map = null;
			if (!("{}").equals(clock_trafficFee_map)
					&& null != clock_trafficFee_map) {

				maxAddClockNum = clock_trafficFee_map.size() / 2.0f;
				CommonUtil.log("clock_trafficFee_map======"
						+ clock_trafficFee_map);
				CommonUtil.log("maxAddClockNum======" + maxAddClockNum);
			} else {
				changeAddButtonStyle(false);
				changeCutButtonStyle(false);
			}
		} else {
			changeAddButtonStyle(false);
			changeCutButtonStyle(false);
		}

	}

	/**
	 * 计算还需支付金额
	 */
	private void accountStillPay() {

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
	 * 改变加钟按钮样式
	 * 
	 * @param flag
	 */
	private void changeAddButtonStyle(boolean flag) {
		if (flag) {
			btn_jiahao.setClickable(true);
			btn_jiahao.setBackgroundResource(R.drawable.icon_clock_add);
		} else {
			btn_jiahao.setClickable(false);
			btn_jiahao.setBackgroundResource(R.drawable.icon_clock_add);
		}
	}

	/**
	 * 改变加钟按钮样式
	 * 
	 * @param flag
	 */
	private void changeCutButtonStyle(boolean flag) {
		if (flag) {
			btn_jianhao.setClickable(true);
			btn_jianhao.setBackgroundResource(R.drawable.icon_clock_j);
		} else {
			btn_jianhao.setClickable(false);
			btn_jianhao.setBackgroundResource(R.drawable.icon_clock_j);
		}
	}

	/**
	 * 减钟数操作
	 */

	private void cutClock() {
		if (colckCount > oneBelt) {
			changeCutButtonStyle(true);
			colckCount -= 0.5f;
			beltCount = colckCount;
			tv_zhongshu.setText(colckCount + "");
			tv_clockNum.setText("x" + colckCount + "");
			countTotalTime();
			cancelCunpon();// 取消优惠券
			getFeeFromMap(beltCount);
			countFee();
			countTotalFee();

			// 加钟可以点击
			if (colckCount < maxAddClockNum) {
				changeAddButtonStyle(true);
			}

			if (colckCount == oneBelt) {
				changeCutButtonStyle(false);
			}

		} else {
			changeCutButtonStyle(false);
		}
	}

	/**
	 * 加钟
	 */
	private void addClock() {
		if (colckCount < maxAddClockNum) {
			changeAddButtonStyle(true);
			colckCount += 0.5f;
			beltCount = colckCount;
			tv_zhongshu.setText(colckCount + "");
			tv_clockNum.setText("x" + colckCount + "");
			countTotalTime();
			cancelCunpon();// 取消优惠券
			getFeeFromMap(beltCount);
			countFee();
			countTotalFee();
			// 减钟可以点击
			if (colckCount > oneBelt) {
				changeCutButtonStyle(true);
			}

			if (colckCount == maxAddClockNum) {
				changeAddButtonStyle(false);
			}

		} else {
			changeAddButtonStyle(false);
		}
	}

	/**
	 * 获取可用优惠券
	 */
	private void getUseableCunponList() {
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		// userID 用户ID ANS 32 M 用户ID
		// status 状态 N 4 O 0 未使用1 已使用
		// 2 已过期
		// serviceId 项目ID ANS 64 O 项目ID
		// workerId 技师 ID ANS 64 O 技师ID
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("status", "0");
		inJsonMap.put("serviceId", servID);
		inJsonMap.put("workerId", skillWorkerID);
		inJsonMap.put("payment", totalFee_noCunpon * 100 + "");
		showCustomCircleProgressDialog(
				null,
				mContext.getResources().getString(
						R.string.common_toast_net_prompt_submit));
		useableCouponList = new UseableCouponList(mContext, handler_cunPonList,
				inJsonMap);
		Thread thread = new Thread(useableCouponList);
		thread.start();
	}

	/**
	 * 处理 可用优惠券
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_CunPonList extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				cunponList = useableCouponList.getCouponList();
				cunponJsonObject = useableCouponList.getCouponObjecct();
				CommonUtil.log("可用优惠券：CouponList" + cunponList);
				judgeIsHasCunpon();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				String MSG = useableCouponList.getOutMsg();
				int code = useableCouponList.getOutCode();
				CommonUtil.log("获取可用优惠券返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN
						|| code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils.showToastMsg(
							mContext,
							mContext.getResources().getString(
									R.string.common_toast_net_down_data_fail),
							Toast.LENGTH_SHORT);
				}
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						mContext.getResources().getString(
								R.string.common_toast_net_not_connect), true);
				break;

			}

		}

	}

	/**
	 * 获取最大优惠券
	 */
	private void judgeIsHasCunpon() {

		// 判断有无代金券
		if (CommonUtil.emptyListToString3(cunponList)) {
			couponPrice = "0";
			couponID = "";// 清空优惠券
			tv_quanPrice.setText(getResources().getString(R.string.no_cunpon));
			Toast_Util.showToast(mContext,
					getResources().getString(R.string.no_cunpon));
		} else {
			chooseUsableCunpon();// 去优惠券列表选择可用优惠券
		}

	}

	/**
	 * 选择可用的代金券
	 */
	private void chooseUsableCunpon() {
		if (intent == null) {
			intent = new Intent();
		}

		intent.setClass(mContext, Appointment_selectCunpon_Activity.class);
		intent.putExtra("cunponList", cunponJsonObject.toString());
		// intent.putExtra(Constants.APPOINT_VIEW,
		// Constants.APPOINT_VIEW_STORE_ONLYVISIT);
		startActivityForResult(intent, REQUEST_SELECT_COUPONS);
	}

	/**
	 * 处理服务券
	 * 
	 * @param jsonObject
	 */
	private void initCunpon(JSONObject jsonObject) {
		// ID 劵ID ANS 32 M 劵ID
		// name 优惠券名称 ANS 64 M
		// type 优惠券类型 N 4 O
		// price 面额 N 12 M 面额
		// expiryDate 失效日期 ANS 20 M 失效日期
		// startTime 有效开始时间 AN 20 M 有效开始时间
		// endTime 有效结束时间 AN 20 M 有效结束时间
		// icon 图片地址 ANS 256 M 图片地址

		if (null != jsonObject) {
			couponID = jsonObject.optString("ID", "");
			couponPrice = jsonObject.optString("price", "");
			CommonUtil.log("处理服务券couponID:" + couponID);
			CommonUtil.log("处理服务券couponPrice:" + couponPrice);
			Toast_Util.showToast(mContext,
					"总价减免" + NumFormatUtil.centFormatYuanToString(couponPrice)
							+ "元");
			handleCouponPrice();
			countTotalFee();

		}

	}

	/**
	 * 计算服务总时间
	 */
	private void countTotalTime() {
		CommonUtil.log("countTotalTime-----------------servTime:" + servTime);
		CommonUtil.log("countTotalTime-----------------beltCount:" + beltCount);
		if (!TextUtils.isEmpty(servTime)) {
			totalTime = NumFormatUtil.saveOnePointOfFloat(beltCount
					* Float.parseFloat(servTime.trim()));
			tv_totalTime.setText("总时长：" + totalTime + "分钟");
		}

	}

	/**
	 * 计算服务时长对应的服务费用 计算服务费用*钟数
	 */
	private void countFee() {
		formatPrice();// 处理单价价格
		countFee = db_serviceFee * beltCount;

		tv_countFee.setText("=" + "￥"
				+ NumFormatUtil.saveTwoPointOfDouble(countFee));
	}

	/**
	 * 格式化单价
	 */
	private void formatPrice() {
		CommonUtil.log("gradePrice:" + gradePrice);
		CommonUtil.log("marketPrice:" + marketPrice);
		if (!TextUtils.isEmpty(gradePrice)) {
			db_serviceFee = NumFormatUtil.centFormatYuanTodouble(gradePrice);

		}

		if (!TextUtils.isEmpty(marketPrice)) {
			db_oldServiceFee = NumFormatUtil
					.centFormatYuanTodouble(marketPrice);
		}

		String newPrice = NumFormatUtil.saveTwoPointOfDouble(db_serviceFee);
		tv_jiage.setText(newPrice);// 选中的级别价格
		tv_serverFee.setText("￥" + newPrice);// 选中的级别价格

		tv_oldJiage.setText("￥"
				+ NumFormatUtil.saveTwoPointOfDouble(db_oldServiceFee));// 服务项目原价价格
		tv_oldJiage.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

	}

	// /**
	// * 计算总费用
	// */
	// private void countTotalFee() {
	// CommonUtil.log("couponPrice:" + couponPrice);
	// if (totalFee_noCunpon > 0) {
	// totalFee = totalFee_noCunpon - coupon_db;
	// if (totalFee < 0) {// 当订单金额小于0
	// totalFee = 0;
	// }
	//
	// } else {
	// totalFee = 0;
	// }
	//
	//
	// tv_totalFee.setText(NumFormatUtil.saveTwoPointOfDouble(totalFee));
	// // 账户余额是否充足
	// judgeAccountIsEnough();
	// accountStillPay();
	// }

	/**
	 * 计算总费用
	 */
	private void countTotalFee() {
		CommonUtil.log("transportationFee:" + transportationFee);
		CommonUtil.log("couponPrice:" + couponPrice);
		handleTrafficFee();
		if (countFee > 0) {
			totalFee_noCunpon = countFee + bedFee + transportationFee_db;// 不包含优惠券的总价格
			totalFee = countFee + bedFee - coupon_db + transportationFee_db;// 包含处理优惠券的总价格
			if (totalFee < 0) {// 当订单金额小于0
				totalFee = 0;
			}

		} else {
			totalFee = 0;
		}

		tv_totalFee.setText(NumFormatUtil.saveTwoPointOfDouble(totalFee));

		// 账户余额是否充足
		judgeAccountIsEnough();
		accountStillPay();

	}

	/**
	 * 处理交通费
	 */
	private void handleTrafficFee() {
		if (!TextUtils.isEmpty(transportationFee)) {
			tv_bootom_trafficFee.setText("交通费：" + "￥" + transportationFee);
			transportationFee_db = NumFormatUtil
					.stringToDouble(transportationFee.trim());
			if (transportationFee_db <= 0d) {
				tv_bootom_trafficFee.setVisibility(View.GONE);
			} else {
				tv_bootom_trafficFee.setVisibility(View.VISIBLE);
			}
		} else {
			tv_bootom_trafficFee.setVisibility(View.GONE);
		}
	}

	/**
	 * 处理优惠券
	 */
	private void handleCouponPrice() {

		if (!TextUtils.isEmpty(couponPrice)) {
			coupon_db = NumFormatUtil.centFormatYuanTodouble(couponPrice);
			if (coupon_db <= 0d) {
				tv_bootom_quanPrice.setVisibility(View.GONE);
			} else {
				tv_bootom_quanPrice.setVisibility(View.VISIBLE);

			}
		} else {
			tv_bootom_quanPrice.setVisibility(View.GONE);
		}

		if (coupon_db > 0d) {
			tv_quanPrice.setText("-" + "￥"
					+ NumFormatUtil.saveTwoPointOfDouble(coupon_db));
		} else {
			tv_quanPrice.setText("");
		}

		tv_bootom_quanPrice.setText("优惠券 -" + "￥"
				+ NumFormatUtil.saveTwoPointOfDouble(coupon_db));

	}

	/**
	 * 取消优惠券
	 */
	private void cancelCunpon() {
		couponID = "";
		couponPrice = "0";
		CommonUtil.log("处理服务券couponID:" + couponID);
		CommonUtil.log("处理服务券couponPrice:" + couponPrice);
		handleCouponPrice();
	}

	/**
	 * 根据加钟改变服务费用
	 * 
	 * @param serviceFee
	 * @param bedFee
	 * @param trafficFee
	 */
	private void getFeeFromMap(float belCount) {

		HashMap<String, String> map = clock_trafficFee_map.get(NumFormatUtil
				.saveOnePointOfFloat(belCount));
		// "serviceFee": 420,
		// "allprice": 420,
		// "transportationFee": 0
		CommonUtil.log("belCount:" + belCount + "--------------------map:"
				+ map);
		if (null != map) {
			String trafficFee = map.get("transportationFee");

			this.transportationFee = trafficFee;
		}

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
			commitOrder();
			break;
		}
	}

	private void showDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage(getString(R.string.appoint_dialog_tips) + "￥"
				+ NumFormatUtil.saveTwoPointOfDouble(totalFee));
		builder.setPositiveButton(getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						commitOrder();
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

	/**
	 * 提交订单 ----店铺的上门 预约 skillWorkerID 技师ID servID 服务ID serviceTime 服务时间
	 * beltCount 钟数 addressID 用户的地址ID userID 用户ID isBed 是否带床 couponID 用户要使用的卡券信息
	 * longitude 经度 latitude 纬度 name 预约人姓名 remark 备注
	 */
	private void commitOrder() {

		// amount 钟数
		// serviceTime 服务时间
		// orderID 加钟时原订单ID
		// serviceID 服务ID
		// servType 服务类型
		// workerID 技师ID
		// remark 备注
		// couponID 卡券ID
		// userID 用户ID
		// gradeID 服务等级ID
		// client 客户端类型
		// addressID 上门地址ID
		// longitude 上门地址经度
		// latitude 上门地址纬度
		// isWithBed 是否带床
		// platformDiscountID 平台优惠 ID
		// storeDiscountID 门店优惠ID
		// isPaidByDeposit 余额支付
		// payChannelCode 支付渠道编号
		HashMap<String, String> inJsonMap = new HashMap<String, String>();

		inJsonMap.put("amount", beltCount + "");// 选择的钟数
		inJsonMap.put("orderID", orderID);
		inJsonMap.put("serviceID", servID);// 服务项目ID
		inJsonMap.put("servType", servType + "");// 1 到店， 2上门，3 自营的上门
		inJsonMap.put("serviceTime", serviceTime_old);// 服务时间
		inJsonMap.put("couponID", couponID);
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("gradeID", servGradeID);// 服务等级ID

		inJsonMap.put("workerID", skillWorkerID);// 技师ID
		inJsonMap.put("remark", et_appoint_remark.getText().toString());
		inJsonMap.put("addressID", addressID);
		inJsonMap.put("longitude", lng);
		inJsonMap.put("latitude", lat);
		inJsonMap.put("client", "android");
		inJsonMap.put("isWithBed", isBed);
		inJsonMap.put("platformDiscountID", "");
		inJsonMap.put("storeDiscountID", "");
		inJsonMap.put("isPaidByDeposit", isAccount);
		inJsonMap.put("payChannelCode", payChannelCode);
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		commitOrder_Thread = new CommitOrder_Thread_Store(mContext,
				handler_commitOrder, inJsonMap);
		Thread thread = new Thread(commitOrder_Thread);
		thread.start();
	}

	/**
	 * 处理提交订单后----店铺的上门 预约
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_CommitOrder extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				CommonUtil.saveBooleanOfSharedPreferences(mContext, "ISSTORE",
						true);
				closeCustomCircleProgressDialog();
				orderObj = commitOrder_Thread.getOutJsonObject();
				// Toast_Util.showToast(mContext, "提交订单成功");
				if (null != orderObj) {
					new_orderID = orderObj.optString("orderID", "");
				}
				jumpAfterCommitOrderSuccess();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				String MSG = commitOrder_Thread.getOutMsg();
				int code = commitOrder_Thread.getOutCode();
				CommonUtil.log("提交订单后返回的错误code：" + code);

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
				Intent intent = new Intent(mContext, OrderDetailActivity.class);
				intent.putExtra("orderID", new_orderID);
				startActivity(intent);
				finish();
				break;
			case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝支付成功
				// Toast_Util.showToast(this, "支付宝支付");
				// 初始化支付宝参数
				initAlipayData();
				if (null != alipayData) {
					PayUtil.getInstance().aliPay(this, alipayData, new_orderID);
				} else {
					CommonUtil.logE("支付宝alipayData为空");
				}
				break;
			case Constants.FLAG_PAYTYPE_WXPAY:// 微信支付成功
				// Toast_Util.showToast(this, "微信支付");
				// 初始化微信支付参数
				initTenpayData();
				if (null != tenpayData) {
					PayUtil.getInstance().wxPay(this, tenpayData, new_orderID);
				} else {
					CommonUtil.logE("微信tenpayData为空");
				}
			}
		} else {
			/**
			 * 调往订单详情
			 */
			Intent intent = new Intent(mContext, OrderDetailActivity.class);
			intent.putExtra("orderID", new_orderID);
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
			new_orderID = orderObj.optString("orderID", "");
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
			new_orderID = orderObj.optString("orderID", "");
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

	/*
	 * onclick点击事件
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back1:// 返回
			CommonUtil.hideKeyboard(mContext, et_appoint_remark);
			finish();
			break;
		case R.id.btn_jianhao:// 减钟数
			cutClock();// 减钟数操作
			break;
		case R.id.btn_jiahao:// 加钟数
			addClock();// 加钟数操作
			break;
		case R.id.layout_selectDaijinquan:// 选择代金券
			// 获取优惠券
			getUseableCunponList();
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

				// 账户不充足--第三方支付 二选一--该点击：选中 支付宝
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
			if (intent == null) {
				intent = new Intent();
			}
			if (!MyApplication.getLoginFlag()) {
				// showCustomCircleProgressDialog("请登录", "你尚未登录,请登录!");
				DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
						Toast.LENGTH_SHORT);

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

	private void initWidget() {
		ll_back1 = (LinearLayout) findViewById(R.id.ll_back1);
		// 服务项目
		iv_icon = (ImageView) findViewById(R.id.iv_yuyue_icon);
		tv_serverName = (TextView) findViewById(R.id.tv_serverName);
		tv_shijian = (TextView) findViewById(R.id.tv_shijian);
		tv_jiage = (TextView) findViewById(R.id.tv_jiage);
		tv_oldJiage = (TextView) findViewById(R.id.tv_Oldjiage);

		// 加减钟数
		btn_jiahao = (Button) findViewById(R.id.btn_jiahao);
		btn_jianhao = (Button) findViewById(R.id.btn_jianhao);
		tv_zhongshu = (TextView) findViewById(R.id.tv_zhongshu);

		// 服务地址
		tv_serviceAddress = (TextView) findViewById(R.id.tv_serviceAddress);
		tv_serviceAddress_contactName = (TextView) findViewById(R.id.tv_serviceAddress_contactName);
		tv_serviceAddress_contactPhone = (TextView) findViewById(R.id.tv_serviceAddress_contactPhone);

		// 服务时间
		tv_serviceTime = (TextView) findViewById(R.id.tv_serviceTime);
		// 技师
		tv_xuanzejishi = (TextView) findViewById(R.id.tv_xuanzejishi);

		// 计算费用
		tv_serverFee = (TextView) findViewById(R.id.tv_serviceFee);
		tv_clockNum = (TextView) findViewById(R.id.tv_clock);
		tv_serviceCountFee = (TextView) findViewById(R.id.tv_countFee);
		tv_bootom_bedFee = (TextView) findViewById(R.id.tv_bottom_bedFee);
		tv_bootom_trafficFee = (TextView) findViewById(R.id.tv_bootom_trafficFee);

		tv_totalTime = (TextView) findViewById(R.id.tv_clockLength);
		tv_totalFee = (TextView) findViewById(R.id.tv_total);
		tv_countFee = (TextView) findViewById(R.id.tv_countFee);

		// 选择代金券
		layout_selectDaijinquan = (RelativeLayout) findViewById(R.id.layout_selectDaijinquan);
		tv_quanPrice = (TextView) findViewById(R.id.tv_quanPrice);
		tv_bootom_quanPrice = (TextView) findViewById(R.id.tv_cunponFee);
		iv_quanPrice_next = (ImageView) findViewById(R.id.iv_quanPrice_next);
		// 备注
		et_appoint_remark = (EditText) findViewById(R.id.et_appoint_remark);

		layout_accountPay = (RelativeLayout) findViewById(R.id.layout_accountPay);
		layout_aliPay = (RelativeLayout) findViewById(R.id.layout_alipay);
		layout_wxPay = (RelativeLayout) findViewById(R.id.layout_wxPay);

		rl_accountPay_line = (RelativeLayout) findViewById(R.id.rl_accountPay_line);

		iv_accountPay_select = (ImageView) findViewById(R.id.iv_accountPay_select);
		iv_alipay_select = (ImageView) findViewById(R.id.iv_alipay_select);
		iv_wxPay_select = (ImageView) findViewById(R.id.iv_wxPay_select);

		// 支付
		tx_accountPay = (TextView) findViewById(R.id.tx_accountPay);

		// 提交订单
		bt_appoint_commit = (Button) findViewById(R.id.bt_appoint_commit);
		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
		rl_commit = (RelativeLayout) findViewById(R.id.rl_commit);
	}

	private void initOnClickListener() {
		ll_back1.setOnClickListener(this);
		btn_jiahao.setOnClickListener(this);
		btn_jianhao.setOnClickListener(this);

		// 支付方式
		layout_accountPay.setOnClickListener(this);
		layout_aliPay.setOnClickListener(this);
		layout_wxPay.setOnClickListener(this);
		bt_appoint_commit.setOnClickListener(this);

		layout_selectDaijinquan.setOnClickListener(this);

	}

	private void initHandler() {
		handler_cunPonList = new Handler_CunPonList();
		handler_commitOrder = new Handler_CommitOrder();
		handler_getUserInfo = new Handler_getUserInfo();
	}

	public Activity mContext;
	private LinearLayout ll_back1;
	private Intent intent = new Intent();
	private JSONObject addTime_info_Obj;
	private ImageView iv_icon;
	private TextView tv_serverName, tv_shijian, tv_jiage, tv_oldJiage;
	private TextView tv_serverFee, tv_clockNum, tv_serviceCountFee,
			tv_bootom_bedFee, tv_totalTime;
	private TextView tv_totalFee;
	private LinearLayout layout_level_container;
	// 交通费
	private TextView tv_bootom_trafficFee;
	// 数据回调
	private final int REQUEST_SELECT_CLOCK = 100;
	private final int REQUEST_SELECT_COUPONS = 400;

	private HashMap<String, HashMap<String, String>> clock_trafficFee_map;

	private String orderID;
	// 服务项目信息
	private String servID = "";
	private String dayCount = "4";
	private String imgUrl = "";
	private String servName = "";

	// 服务时间
	private String servTime = "";
	private TextView tv_serviceTime;
	private String serviceTime_old = "";// 原始的时间--上传参数用
	private String serviceTime_new = "";// 处理后的时间格式---仅用于展示

	// 加减钟数
	private Button btn_jiahao;
	private Button btn_jianhao;
	private TextView tv_zhongshu;
	private float oneBelt = 0.5f;// 加一个钟数
	private float colckCount = 0.5f;// 钟数
	private float beltCount = 0.5f;// 记录加钟后的数据
	private float maxAddClockNum = 0f;

	// 代金券
	private TextView tv_quanPrice;// 代金券金额
	private TextView tv_bootom_quanPrice;
	private RelativeLayout layout_selectDaijinquan;// 选择代金券
	private String couponID = "";// 代金券
	private String couponPrice = "0";
	// 优惠券 减免
	double coupon_db = 0d;
	// 技师
	private TextView tv_xuanzejishi;
	private String skillWorkerID = "";// 是否是根据技师 来预约的
	private String skillGrade = "";// 技师等级
	private String skillWorkerName = "";// 技师名字
	private String skillGradeName;// 技师等级
	private String serviceType = "2";// 1 到店， 2上门

	// 备注
	private EditText et_appoint_remark;
	private String remark = "";// 备注

	// 等级
	private String gradePrice = "";// 等级对应的价格
	private String marketPrice = "";// 原价的价格
	private String servGradeID;// 等级Id
	// 地址联系人
	private TextView tv_serviceAddress;
	private TextView tv_serviceAddress_contactName;
	private TextView tv_serviceAddress_contactPhone;
	private String sex;

	private String address = "";
	private String userArea = "";
	private String userID = "";
	private String addressID = "";
	private String userName = "";
	private String userSex = "";
	private String mobile = "";
	private String lng = "";
	private String lat = "";

	// 账户余额
	private GetUserInfo getUserInfo;
	private JSONObject userObj;
	private Handler handler_getUserInfo;
	private TextView tx_accountPay;// 账户余额

	// 可用优惠券
	private Handler handler_cunPonList;
	private UseableCouponList useableCouponList = null;
	public ArrayList<JSONObject> cunponList = new ArrayList<JSONObject>();
	private JSONObject cunponJsonObject = null;
	private ImageView iv_quanPrice_next;

	// 计算费用
	private TextView tv_countFee;
	private double db_serviceFee = 0d;// 记录服务单价
	private double db_oldServiceFee = 0d;// 记录服务旧单价
	private String transportationFee = "";// 交通费
	private double countFee;
	private double totalFee;
	private double totalFee_noCunpon;// 跟选择的优惠券无关的总金额
	// 交通费 增加
	private double transportationFee_db = 0d;
	private String totalTime;
	private String isBed = "";// 是否带床
	private static int bedFee = 0;// 床费
	// 提交订单
	private CommitOrder_Thread_Store commitOrder_Thread = null;
	private Handler handler_commitOrder;
	private Button bt_appoint_commit;
	private String new_orderID = "";
	// 支付选择
	private RelativeLayout layout_wxPay, layout_aliPay, layout_accountPay,
			rl_accountPay_line;
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
	private int FLAG_PAYTYPE = Constants.FLAG_PAYTYPE_ACCOUNT;// -1:代表账户余额：0：表示不用支付宝（账户）或者线下；1：表示支付宝--调用支付宝；2：表示微信支付--调用微信APP

	// 提交订单成后返回支付
	private JSONObject orderObj = null;
	private String alipayData;
	private JSONObject tenpayData;

	private int servType = -1;// 1 到店， 2上门，3 自营的上门

	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private RelativeLayout rl_commit;

}
