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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
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
import com.huatuo.activity.personal.AddressSelectActivity;
import com.huatuo.adapter.SkillGradeList_Gridview_Adapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.custom_widget.CustomGridView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.CommitOrder_Thread_Store;
import com.huatuo.net.thread.GetUserAddressList;
import com.huatuo.net.thread.GetUserInfo;
import com.huatuo.net.thread.UseableCouponList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 上门预约 根据项目
 * 
 * @author Android开发工程师 王润泽
 * 
 */
public class Apponitment_OnVisit_FromProjectActivity extends BaseActivity
		implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_appointment_visit_pro);
		mContext = this;
		initWidget();
		initOnClickListener();
		initHandler();
		getAddressList();// 获取默认联系人收获地址---->获取账户余额----接受传递数据/判断支付方式

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Bundle bundle;
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_SELECT_CLOCK:// 选择服务时间
				bundle = data.getExtras();
				if (null != bundle) {
					memoryDay = bundle.getString("memoryDay");
					memoryClock = bundle.getString("memoryClock");
					serviceTime_old = bundle.getString("old_clock");
					serviceTime_new = bundle.getString("new_clock");
					transportationFee = bundle.getString("transportationFee");
					CommonUtil.log("serviceTime_new:" + serviceTime_new);
					CommonUtil.log("transportationFee:" + transportationFee);
					tv_serviceTime.setText(serviceTime_new);
					tv_bootom_trafficFee.setText("交通费："
							+ "￥"
							+ NumFormatUtil
									.centFormatYuanToString(transportationFee));
					cancelCunpon();
					countTotalFee();// 计算总费用
				}
				break;
			case REQUEST_SELECT_ADDRESS:// 选择服务地址
				bundle = data.getExtras();
				if (null != bundle) {
					String addressInfo = bundle.getString("ADDRESS");
					if (!TextUtils.isEmpty(addressInfo)) {
						try {
							initAddress(new JSONObject(addressInfo));
							// 根据服务项目预约 --清空技师
							skillWorkerID = "";
							tv_xuanzejishi.setText(mContext.getResources()
									.getString(R.string.select_tech));

						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				break;
			case REQUEST_SELECT_TECH:// 选择技师
				bundle = data.getExtras();
				if (null != bundle) {
					String techInfo = bundle.getString("TECH");
					if (!TextUtils.isEmpty(techInfo)) {
						try {
							initTechnicalBySelected(new JSONObject(techInfo));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					}
				}
				break;
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
	 * 获取服务项目信息
	 */
	private void getJsonObject_serDetail() {

		Bundle bundle = getIntent().getExtras();
		CommonUtil.log("到店预约界面：bundle:" + bundle);
		if (null != bundle) {
			receive_jsonObject = bundle.getString(Constants.APPOINT_SERVICE);
			appointServType = bundle.getInt("servType");
			CommonUtil
					.log("---------上门预约界面：appointServType:" + appointServType);
			if (!TextUtils.isEmpty(receive_jsonObject)) {
				try {
					service_jsonObj = new JSONObject(receive_jsonObject);

					if (service_jsonObj != null) {
						GetDataFromServiceObj();// 获取服务详情页面传过来的对象的值
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 获取默认地址地址列表
	 */
	private void getAddressList() {
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("isDefault", "1");
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		getUserAddressList = new GetUserAddressList(mContext,
				handler_morenAddress, inJsonMap);
		Thread thread = new Thread(getUserAddressList);
		thread.start();
	}

	/**
	 * 处理 默认地址
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class Handler_morenAddress extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				// CustomDialogProgressHandler.getInstance()
				// .closeCustomCircleProgressDialog();
				address_list = getUserAddressList.getAddressList();
				getMorenAddress();// 从地址列表中获取默认地址
				break;
			case MsgId.DOWN_DATA_F:
				// CustomDialogProgressHandler.getInstance()
				// .closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
				break;
			}
			getUserInfo();
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

			getJsonObject_serDetail();

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
			// 账户余额足
			layout_accountPay.setClickable(true);
			intPayParamsByEnoughOfAcccount(Constants.PAY_SELECTED_ACCOUNT);
		} else {
			// 账户余额不足--默认选中账户
			initDefaultSelectedAccountOfNotEnough();
			layout_accountPay.setClickable(false);
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
	 * 从获取地址列表中获取默认地址
	 */
	private void getMorenAddress() {
		CommonUtil
				.log("S_Apponitment_OnlyVisitActivity-----从获取地址列表中获取默认地址address_list:"
						+ address_list);
		if (!CommonUtil.emptyListToString3(address_list)) {
			if (address_list.size() > 0) {
				moren_JsonObject = address_list.get(0);
				// IsEmptyMorenAddress = false;
				initAddress(moren_JsonObject);
			}
		}
	}

	/**
	 * 展示服务地址信息
	 * 
	 * @param jsonObject
	 */
	private void initAddress(JSONObject jsonObject) {
		userArea = jsonObject.optString("userArea", "");
		address = jsonObject.optString("address", "");
		addressID = jsonObject.optString("ID", "");
		lng = jsonObject.optString("lng", "");
		lat = jsonObject.optString("lat", "");
		userName = jsonObject.optString("name", "");
		mobile = jsonObject.optString("mobile", "");
		String user_sex = jsonObject.optString("gender", "");
		if (!TextUtils.isEmpty(user_sex)) {
			if (user_sex.trim().equals("男")) {
				// userSex = "1";
				tv_serviceAddress_contactName.setText(userName + " 先生");

			} else {
				if (user_sex.trim().equals("女")) {
					// userSex = "2";
					tv_serviceAddress_contactName.setText(userName + " 女士");
				}
			}
		}

		tv_serviceAddress_contactPhone.setText(mobile);
		tv_serviceAddress.setText(userArea + address);
		CommonUtil.log("userName：" + userName);
		CommonUtil.log("mobile：" + mobile);
		CommonUtil.log("userArea + address：" + userArea + address);
		// 是否展示用户信息
		isShowUserInfo();

	}

	/**
	 * 是否展示用户信息---根据是否有地址信息
	 * 
	 * @param isbed
	 */
	private void isShowUserInfo() {
		if (!TextUtils.isEmpty(address)) {
		} else {
		}
	}

	/**
	 * 展示服务等级列表
	 */
	private void initSkillGradeList_Gridview() {

		// 是否显示级别列表
		if (!CommonUtil.emptyListToString3(servGradeList)) {

			isShowServiceLevel(true);// 展示服务级别
			getDefaultFromSkillGradeList();// 从服务级别中获取默认级别
			CommonUtil.log("从服务级别中获取默认的的等级ID:" + servGradeID);

			// 初始化等级列表
			final SkillGradeList_Gridview_Adapter skillGradeList_Gridview_Adapter = new SkillGradeList_Gridview_Adapter(
					mContext);
			gridView_level.setAdapter(skillGradeList_Gridview_Adapter);
			skillGradeList_Gridview_Adapter.add(servGradeList);
			// 级别点击监听
			gridView_level.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					skillGradeList_Gridview_Adapter.setSeclection(position);// 设置选中的等级

					// skillGradeID
					// skillGradeName
					// gradePrice
					// isDefault
					// 获取点击的等级
					JSONObject jsonObj = (JSONObject) parent.getAdapter()
							.getItem(position);
					// workerGradeID
					// workerGradeName
					// gradePrice
					// marketPrice
					gradePrice = jsonObj.optString("gradePrice", "");// 获取的等级价格
					marketPrice = jsonObj.optString("marketPrice", "");
					servGradeID = jsonObj.optString("workerGradeID", "");// 获取的等级的id
					CommonUtil.log("选中的等级gradePrice:" + gradePrice);
					CommonUtil.log("选中的等级marketPrice:" + marketPrice);
					CommonUtil.log("选中的等级servGradeID:" + servGradeID);
					skillWorkerID = "";// 重新选择等级后清空技师：
					tv_xuanzejishi.setText(mContext.getResources().getString(
							R.string.select_tech));
					cancelCunpon();
					countFee();
				}
			});
		} else {
			isShowServiceLevel(false);// 不展示服务级别
		}

	}

	/**
	 * 获取默认等级和价格
	 */
	private void getDefaultFromSkillGradeList() {
		if (!CommonUtil.emptyListToString3(servGradeList)) {
			for (int i = 0; i < servGradeList.size(); i++) {
				JSONObject jsonObj = (JSONObject) servGradeList.get(i);
				// "marketPrice": 21600,
				// "gradeName": "高级",
				// "gradeID": "040e1e6d-ba8c-488c-a8c1-5ef99adaed53",
				// "isDefault": "1",
				// "gradePrice": 10800
				String isDefault = jsonObj.optString("isDefault", "");
				CommonUtil.log("isDefault:" + isDefault);
				if (("1").equals(isDefault)) {
					gradePrice = jsonObj.optString("gradePrice", "");// 获取的等级价格
					marketPrice = jsonObj.optString("marketPrice", "");
					servGradeID = jsonObj.optString("workerGradeID", "");// 获取的等级的id
					break;
				}
			}
		}
	}

	private void canSelectTechcial(boolean flag) {
		if (flag) {
			// layout_selectJishi.setClickable(true);
			// im_xuanzejishi_next.setVisibility(View.VISIBLE);
		} else {
			// layout_selectJishi.setClickable(false);
			// im_xuanzejishi_next.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 根据服务详情页面的数据 ：获取对象属性值
	 */
	private void GetDataFromServiceObj() {
		// ID 服务ID ANS 64 M 服务ID
		// name 服务名称 ANS 64 M 服务名称
		// duration 服务时长 N 11 M 服务时长，单位分钟
		// introduction 服务介绍 ANS 1024 M 服务介绍
		// commentCount 评价数量 N 64 M 评价数量
		// storeID 店铺ID ANS 64 C 店铺ID
		// storeName 属店铺名称 ANS 64 C 技师所属店铺名称
		// storeAddress 店铺地址 ANS 256 C 店铺地址
		// distance 距离 ANS 64 C 距离，单位公里
		// storeTel 店铺电话 ANS 64 C 店铺电话
		// minPrice 服务最低价格 ANS 64 M 服务最低价格
		// forBodyPart 针对部位 ANS 512 M 针对部位
		// forSymptom 适用症状 ANS 512 M 适用症状
		// isFavorite 是否收藏 ANS 64 O 类型 0：否，1：是，
		// reminder 购买须知 JsonArray O 温馨提示
		// imageList 图片 JsonArray M

		imgUrl = service_jsonObj.optString("icon", "");//

		servID = service_jsonObj.optString("ID", "");// servID 服务ID
		servName = service_jsonObj.optString("name", "");// 服务名称
		servTime = service_jsonObj.optString("duration", "");// 服务时长
		sitType = service_jsonObj.optString("sitType", "");// 服务坐姿

		String maxNumberClock_str = service_jsonObj.optString("maxNumberClock",
				"0");// 最大加钟数
		maxNumberClock = (int) Float.parseFloat(maxNumberClock_str);

		// workNum 项目技师数 N 11 M 项目可选技师数,不考虑时间和服务类型等因素
		String workNum_str = service_jsonObj.optString("workerNum", "0");// 最大加钟数
		int workNum = Integer.parseInt(workNum_str);
		CommonUtil.log("==========================workNum:" + workNum + "");
		// 是否可以选择技师
		// if(workNum == 0){
		// canSelectTechcial(false);
		// }else {
		// canSelectTechcial(true);
		// }

		// 服务级别 集合
		try {
			servGradeList = JsonUtil.jsonArray2List(service_jsonObj
					.getJSONArray("serviceGradeList"));
			CommonUtil.log("上门预约界面：技师等级servGradeList" + servGradeList);
			initSkillGradeList_Gridview();// 显示等级列表

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String minPrice = service_jsonObj.optString("minPrice", "");// 服务最低价格
		String maxPrice = service_jsonObj.optString("maxPrice", "");// 服务最高价格
		// 当服务等级列表为空时，取最小值和最大值
		if (CommonUtil.emptyListToString3(servGradeList)) {
			// minPrice 服务最低价格
			// maxPrice 服务最高价格
			gradePrice = minPrice;// 最新价格
			marketPrice = maxPrice;// 市场价
		}

		CommonUtil.log("上门根据项目--gradePrice:" + gradePrice);
		CommonUtil.log("上门根据项目--marketPrice:" + marketPrice);
		// 选择技师类型
		switch (appointServType) {
		case Constants.SERVTYPE_STORE_VISIT:
			serviceType = Constants.APPOINT_FROM_STORE + "";
			break;
		case Constants.SERVTYPE_VISIT:
			serviceType = Constants.APPOINT_FROM_VISIT + "";
			break;

		}

		// break;
		// case Constants.SERVTYPE_VISIT:
		// String minPrice = service_jsonObj.optString("minPrice", "");// 自营
		// 默认价格
		// gradePrice = minPrice;
		// break;
		//
		// }

		ImageLoader.getInstance().displayImage(
				imgUrl,
				iv_icon,
				ImageLoader_DisplayImageOptions.getInstance()
						.setDefaultImageSmallImg());// 服务项目图片icon
		tv_serverName.setText(servName);// 服务项目名字
		tv_shijian.setText(servTime + "分钟");// 服务项目服务时间

		double bedFee = NumFormatUtil.centFormatYuanTodouble(service_jsonObj
				.optString("bedFee", "0"));// bedFee带床费当自营服务坐姿卧时返回，单位分

		tv_bedFee.setText("￥" + NumFormatUtil.saveTwoPointOfDouble(bedFee));
		countTotalTime();// 计算服务时长
		countFee();// 计算服务时长对应的服务费用

	}

	/**
	 * 等级列表是否为空
	 * 
	 * @param isShow
	 */
	private void isShowServiceLevel(boolean isShow) {
		if (isShow) {
			serviceLevel_bottomLine.setVisibility(View.VISIBLE);
			layout_store_serviceLevel.setVisibility(View.VISIBLE);
		} else {
			serviceLevel_bottomLine.setVisibility(View.GONE);
			layout_store_serviceLevel.setVisibility(View.GONE);
		}
	}

	/**
	 * 账户支付弹框提示
	 */
	private void commitOrderIsShowTip() {
		switch (FLAG_PAYTYPE) {
		case Constants.FLAG_PAYTYPE_ACCOUNT:// 账户余额支付
			showDialog();
			break;
		case Constants.FLAG_PAYTYPE_ALIPAY:// 支付宝支付
			commitOrder();
			break;
		case Constants.FLAG_PAYTYPE_WXPAY:// 微信支付
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
		final HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("workerID", skillWorkerID);// 技师ID
		inJsonMap.put("gradeID", servGradeID);// 技师ID
		inJsonMap.put("serviceID", servID);// 服务项目ID
		inJsonMap.put("servType", appointServType + "");// 1 到店， 2上门，3 自营的上门
		inJsonMap.put("amount", beltCount + "");// 选择的钟数
		inJsonMap.put("serviceTime", serviceTime_old);// 服务时间
		inJsonMap.put("userID", MyApplication.getUserID());
		inJsonMap.put("couponID", couponID);
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
			// alipayData
			// ="sign=ItfesMMfCfGT%2F4ZM8tj7QW332ly3gEY5Qpj2IjQLr7tlT6z5Adn0LGB0NKGuCssC23SNoSFwRlZqCzuCoV5g0KI%2Fqvvii4WFu44R6od3GP5HIScyLdrPgUfD2cIruDIrr7v5YeTyZhvSnCqVEA%2BR3TsNSKxeMHEhjLX9S5FbZTo%3D&body=huatuojiadao&_input_charset=utf-8&it_b_pay=10m&subject=%E5%85%85%E5%80%BC%E5%8D%A1&total_fee=135.0&sign_type=RSA&service=mobile.securitypay.pay&notify_url=http%3A%2F%2Fpayment.huatuojiadao.cn%2Fhuatuo_pay%2Fpayser%2FalipaySDKNotifyUrl&partner=2088711331431963&seller_id=2088711331431963&out_trade_no=99910169836&payment_type=1";

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
		if (colckCount > 1) {// 最小一个钟

			colckCount--;
			tv_zhongshu.setText(colckCount + "");
			beltCount = colckCount;

			// ----------------------------改变减号样式：1：不能减，能加；1< clock <
			// 4:能加能减--------------------------
			// 改变减钟按钮属性
			if (colckCount == 1) {
				changeCutButtonStyle(false);
			} else {
				changeCutButtonStyle(true);
			}

			// 改变加钟按钮属性
			if (colckCount < 4 && colckCount >= 1) {
				changeAddButtonStyle(true);
			}

			cancelCunpon();
			clearTechAndServiceTime();// 清空技师和服务时间段

		}
	}

	/**
	 * 加钟
	 */
	private void addClock() {
		if (colckCount < maxNumberClock) {// 最大四个钟
			colckCount++;
			tv_zhongshu.setText(colckCount + "");
			beltCount = colckCount;

			if (colckCount == maxNumberClock) {
				changeAddButtonStyle(false);
			} else {
				changeAddButtonStyle(true);
			}

			if (colckCount > 1) {
				changeCutButtonStyle(true);
			}
			cancelCunpon();
			clearTechAndServiceTime();// 清空技师和服务时间段

		}
	}

	/**
	 * 清楚服务时间和所选技师
	 */
	private void clearTechAndServiceTime() {

		countTotalTime();// 计算服务时长
		countFee();// 计算服务时长对应的服务费用
		// 清空服务时间
		serviceTime_old = null;
		serviceTime_new = null;
		tv_serviceTime.setText("");
		memoryDay = null;
		memoryClock = null;
		// 是否清空技师
		// 根据服务项目预约 --清空技师
		skillWorkerID = "";
		tv_xuanzejishi.setText("");
		tv_clockNum.setText("x" + beltCount);
	}

	/**
	 * 选择服务时间
	 */
	private void choooseServiceClock() {
		if (intent == null) {
			intent = new Intent();
		}
		intent.setClass(mContext, Appointment_SelectTimeActivity.class);
		intent.putExtra("workerID", skillWorkerID);
		intent.putExtra("serviceID", servID);
		intent.putExtra("amount", beltCount + "");
		intent.putExtra("dayCount", dayCount);
		intent.putExtra("memoryDay", memoryDay);
		intent.putExtra("memoryClock", memoryClock);
		startActivityForResult(intent, REQUEST_SELECT_CLOCK);
	}

	/**
	 * 选择服务地址
	 */
	private void choooseAddress() {
		if (intent == null) {
			intent = new Intent();
		}

		// 有默认地址
		intent.setClass(mContext, AddressSelectActivity.class);
		startActivityForResult(intent, REQUEST_SELECT_ADDRESS);
	}

	/**
	 * 选择技师
	 */
	private void chooseTechnican() {

		if (intent == null) {
			intent = new Intent();
		}

		// ------------------- 预约选择技师列表上传参数----------------------
		// beltCount 钟数
		// serviceTime 服务时间
		// servType 服务类型
		// servID 服务ID
		// servGradeID 服务等级ID

		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("servID", servID);
			jsonObject.put("beltCount", beltCount + "");
			jsonObject.put("dateTime", serviceTime_old + "");
			jsonObject.put("servGradeID", servGradeID);
			jsonObject.put("serviceType", serviceType);
			jsonObject.put("addressID", addressID);
			// 去往选择技师页面
			intent.setClass(mContext,
					Select_ServiceTechnicianList_Activity.class);
			Bundle bundle = new Bundle();
			bundle.putString("jsonObject", jsonObject.toString());
			intent.putExtras(bundle);
			startActivityForResult(intent, REQUEST_SELECT_TECH);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 根据上个界面服务项目信息： 展示服务技师信息:1：是否带床；2：显示什么级别
	 * 
	 * @param jsonObject
	 */
	private void initTechnicalBySelected(JSONObject selectedTechnicalObject) {
		cancelCunpon();
		sex = selectedTechnicalObject.optString("gender", "");

		skillWorkerID = selectedTechnicalObject.optString("ID", "");

		// 技师级别
		String skillGrade = selectedTechnicalObject.optString("gradeName", "");
		String skillWorkerName = selectedTechnicalObject.optString("name", "");
		// 展示选的技师姓名+级别
		tv_xuanzejishi.setText(skillWorkerName + " " + skillGrade);

		CommonUtil.log("上门预约界面：选择的技师：skillWorkerID：" + skillWorkerID);
		CommonUtil.log("上门预约界面：选择的技师：skillWorkerName：" + skillWorkerName);
		CommonUtil.log("上门预约界面：选择的技师：skillGrade：" + skillGrade);

		// 是否显示带床
		showOrHideBedFee();
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
		inJsonMap.put("payment", countTotalPrice_NoCunpon() * 100 + "");

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
	 * 取消优惠券
	 */
	private void cancelCunpon() {
		couponID = "";
		couponPrice = "0";
		CommonUtil.log("处理服务券couponID:" + couponID);
		CommonUtil.log("处理服务券couponPrice:" + couponPrice);
		// 取消优惠券后重新处理优惠券价格并计算总价
		handleCouponPrice();
		countTotalFee();
	}

	/**
	 * 计算服务总时间
	 */
	private void countTotalTime() {
		CommonUtil.log("countTotalTime-----------------servTime:" + servTime);
		if (!TextUtils.isEmpty(servTime)) {
			totalTime = beltCount * Integer.parseInt(servTime);
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

		countTotalFee();
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

	/**
	 * 设置男技师 是否带床监听事件
	 */
	private void initCheckBoxListener() {
		iv_selectBed.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				cancelCunpon();
				if (!isChecked) {
					iv_selectBed
							.setBackgroundResource(R.drawable.icon_choice_n);
					countBedFee_Service(false);

				} else {
					iv_selectBed
							.setBackgroundResource(R.drawable.icon_choice_p);
					countBedFee_Service(true);
				}
				countTotalFee();
			}
		});

	}

	/**
	 * 处理带床费
	 */
	private void showOrHideBedFee() {
		CommonUtil.log("===================================sitType:" + sitType);
		// 0：坐，1：卧
		if (("1").equals(sitType)) {
			if (("男").equals(sex)) {
				// int servType = -1;// 1 到店， 2上门，3 自营的上门
				switch (appointServType) {
				case Constants.SERVTYPE_STORE_VISIT:
					layout_isbed.setVisibility(View.GONE);
					layout_bed_tips.setVisibility(View.GONE);
					view_isbed.setVisibility(View.GONE);
					break;
				case Constants.SERVTYPE_VISIT:
					layout_isbed.setVisibility(View.VISIBLE);
					layout_bed_tips.setVisibility(View.VISIBLE);
					view_isbed.setVisibility(View.VISIBLE);
					break;
				}

			} else {
				layout_isbed.setVisibility(View.GONE);
				layout_bed_tips.setVisibility(View.GONE);
				view_isbed.setVisibility(View.GONE);
			}
		} else {
			layout_isbed.setVisibility(View.GONE);
			layout_bed_tips.setVisibility(View.GONE);
			view_isbed.setVisibility(View.GONE);
		}

	}

	/**
	 * 根据 上个界面： 服务--- 是否计算床费
	 * 
	 * @param flag
	 */
	private void countBedFee_Service(boolean flag) {
		if (flag) {
			if (null != service_jsonObj) {
				bedFee = NumFormatUtil.centFormatYuanTodouble(service_jsonObj
						.optString("bedFee", "0"));// bedFee带床费当自营服务坐姿卧时返回，单位分
				iv_selectBed.setBackgroundResource(R.drawable.icon_choice_p);
				tv_bootom_bedFee.setText("带床上门￥"
						+ NumFormatUtil.saveTwoPointOfDouble(bedFee));
				tv_bootom_bedFee.setVisibility(View.VISIBLE);
				isBed = "1";
			}

		} else {
			iv_selectBed.setBackgroundResource(R.drawable.icon_choice_n);
			tv_bootom_bedFee.setText("");
			tv_bootom_bedFee.setVisibility(View.GONE);
			bedFee = 0d;
			isBed = "0";
		}
	}

	/**
	 * 处理交通费
	 */
	private void handleTrafficFee() {
		if (!TextUtils.isEmpty(transportationFee)) {
			transportationFee_db = NumFormatUtil
					.centFormatYuanTodouble(transportationFee.trim());
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
	 * 计算总费用
	 */
	private void countTotalFee() {

		CommonUtil.log("transportationFee:" + transportationFee);
		CommonUtil.log("couponPrice:" + couponPrice);
		handleTrafficFee();
		if (countFee > 0) {
			totalFee = countFee + bedFee - coupon_db + transportationFee_db;
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
	 * 计算无优惠券的实际价格
	 * 
	 * @param couponPrice
	 */
	private double countTotalPrice_NoCunpon() {
		CommonUtil.log("transportationFee:" + transportationFee);
		handleTrafficFee();
		if (countFee > 0) {
			totalFee = countFee + bedFee + transportationFee_db;
			if (totalFee < 0) {// 当订单金额小于0
				totalFee = 0;
			}

		} else {
			totalFee = 0;
		}

		return totalFee;

	}

	/**
	 * 判断服务时间是否为空---店铺技师上门
	 */
	private boolean judgeIsEmptyOfServiceTime() {
		CommonUtil.log("判断服务时间是否为空 ActionSheetDialog.serviceTime_old:"
				+ serviceTime_old);
		if (TextUtils.isEmpty(serviceTime_old)) {
			// Toast.makeText(mContext, "请选择服务时间", Toast.LENGTH_SHORT).show();
			Toast_Util.showToast(mContext, "请选择服务时间");
			return true;
		}

		return false;

	}

	/**
	 * 判断服务地址是否为空---店铺技师上门
	 */
	private boolean judgeIsEmptyOfServiceAddress() {
		if (TextUtils.isEmpty(addressID)) {// 根据服务地址id是否为空来判断
			// Toast.makeText(mContext, "请选择服务地址", Toast.LENGTH_SHORT).show();
			Toast_Util.showToast(mContext, "请选择服务地址");
			return true;
		}
		return false;
	}

	/**
	 * 判断服务技师是否为空
	 */
	private boolean judgeIsEmptyOfServiceTechnical() {
		if (TextUtils.isEmpty(skillWorkerID)) {// 根据技师id判断
			// Toast.makeText(mContext, "请选择服务技师", Toast.LENGTH_SHORT).show();
			Toast_Util.showToast(mContext, "请选择服务技师");
			return true;
		}
		return false;
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
			UmengEventUtil.door_tichniciandetail_projectdetail_placeorder_reducetime(mContext);
			cutClock();// 减钟数操作
			break;
		case R.id.btn_jiahao:// 加钟数
			UmengEventUtil.door_tichniciandetail_projectdetail_placeorder_addtime(mContext);
			addClock();// 加钟数操作
			break;
		case R.id.layout_serviceTime:// 选择服务时间
			UmengEventUtil.door_tichniciandetail_projectdetail_placeorder_time(mContext);
			choooseServiceClock();
			break;
		case R.id.layout_serviceAddress:// 选择服务地址
			UmengEventUtil.door_tichniciandetail_projectdetail_placeorder_adress(mContext);
			choooseAddress();
			break;
		case R.id.layout_selectJishi:// 选择技师
			if (judgeIsEmptyOfServiceTime()) {// 判断服务时间是否为空
				break;
			}

			if (judgeIsEmptyOfServiceAddress()) {// 判断服务地址是否为空
				break;
			}

			chooseTechnican();// 选择服务技师
			break;

		case R.id.layout_selectDaijinquan:// 选择代金券
			// 获取优惠券
			getUseableCunponList();
			break;
		case R.id.layout_accountPay:// 账户支付
			UmengEventUtil.
			door_tichniciandetail_projectdetail_placeorder_account(mContext);
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
			UmengEventUtil.door_tichniciandetail_projectdetail_placeorder_zhifubao(mContext);
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
			UmengEventUtil.door_tichniciandetail_projectdetail_placeorder_weixin(mContext);
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

			// 判断 服务时间 /服务地址/服务技师是否为空
			if (judgeIsEmptyOfServiceTime()) {// 判断服务时间是否为空
				break;
			}

			if (judgeIsEmptyOfServiceAddress()) {// 判断服务地址是否为空
				break;
			}

			// 店铺的上门 --可以不选上门---1 到店， 2上门，3 自营的上门
			if (Constants.SERVTYPE_VISIT == appointServType) {
				if (judgeIsEmptyOfServiceTechnical()) {// 判断服务技师是否为空
					break;
				}
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
			UmengEventUtil.
			door_tichniciandetail_projectdetail_placeorder_pay(mContext);
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

		// 服务等级
		gridView_level = (CustomGridView) findViewById(R.id.gridView_level);
		serviceLevel_bottomLine = findViewById(R.id.layout_store_serviceLevel_bottomLine);
		layout_store_serviceLevel = (LinearLayout) findViewById(R.id.layout_store_serviceLevel);
		// 加减钟数
		btn_jiahao = (Button) findViewById(R.id.btn_jiahao);
		btn_jianhao = (Button) findViewById(R.id.btn_jianhao);
		tv_zhongshu = (TextView) findViewById(R.id.tv_zhongshu);

		// 选择服务地址
		layout_serviceAddress = (RelativeLayout) findViewById(R.id.layout_serviceAddress);

		tv_serviceAddress = (TextView) findViewById(R.id.tv_serviceAddress);
		tv_serviceAddress_contactName = (TextView) findViewById(R.id.tv_serviceAddress_contactName);
		tv_serviceAddress_contactPhone = (TextView) findViewById(R.id.tv_serviceAddress_contactPhone);

		// 选择服务时间
		tv_serviceTime = (TextView) findViewById(R.id.tv_serviceTime);
		layout_serviceTime = (RelativeLayout) findViewById(R.id.layout_serviceTime);

		// 选择技师
		layout_selectJishi = (RelativeLayout) findViewById(R.id.layout_selectJishi);
		tv_xuanzejishi = (TextView) findViewById(R.id.tv_xuanzejishi);
		im_xuanzejishi_next = (ImageView) findViewById(R.id.im_xuanzejishi_next);

		view_isbed = (View) findViewById(R.id.view_isbed);
		layout_isbed = (RelativeLayout) findViewById(R.id.layout_isbed);
		layout_bed_tips = (RelativeLayout) findViewById(R.id.layout_bed_tips);
		iv_selectBed = (CheckBox) findViewById(R.id.iv_selectBed);
		tv_bedFee = (TextView) findViewById(R.id.tv_bedFee);
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

		layout_serviceTime.setOnClickListener(this);
		layout_selectJishi.setOnClickListener(this);
		layout_selectDaijinquan.setOnClickListener(this);
		layout_serviceAddress.setOnClickListener(this);

		// 是否带床点击事件
		initCheckBoxListener();
	}

	private void initHandler() {
		handler_morenAddress = new Handler_morenAddress();
		handler_cunPonList = new Handler_CunPonList();
		handler_commitOrder = new Handler_CommitOrder();
		handler_getUserInfo = new Handler_getUserInfo();
	}

	public Activity mContext;
	private LinearLayout ll_back1;
	private Intent intent = new Intent();
	private String receive_jsonObject;
	private JSONObject service_jsonObj;
	private JSONObject serviceTime_jsonObj = null;
	private ImageView iv_icon;
	private ImageView im_xuanzejishi_next;
	private TextView tv_bedFee;
	private CheckBox iv_selectBed;
	private TextView tv_serverName, tv_shijian, tv_jiage, tv_oldJiage;
	private TextView tv_serverFee, tv_clockNum, tv_serviceCountFee,
			tv_totalTime;
	private TextView tv_totalFee;
	private LinearLayout layout_level_container;
	private CustomGridView gridView_level;

	// 数据回调
	private final int REQUEST_SELECT_CLOCK = 100;
	private final int REQUEST_SELECT_ADDRESS = 300;
	private final int REQUEST_SELECT_TECH = 200;
	private final int REQUEST_SELECT_COUPONS = 400;

	// 服务项目信息
	private String servID = "";
	private int beltCount = 1;
	private String dayCount = "4";
	private String imgUrl = "";
	private String servName = "";
	private int maxNumberClock = 0;
	private int workNum = 0;// workNum 项目技师数 N 11 M 项目可选技师数,不考虑时间和服务类型等因素

	// 服务时间
	private String sitType = "";// 0：坐，1：卧
	private String servTime = "";
	private TextView tv_serviceTime;
	private RelativeLayout layout_serviceTime;// 选择服务时间

	private String memoryDay = "";// 记录选中的时间
	private String memoryClock = "";// 记录选中的时间
	private String serviceTime_old = "";// 原始的时间--上传参数用
	private String serviceTime_new = "";// 处理后的时间格式---仅用于展示

	// 加减钟数
	private Button btn_jiahao;
	private Button btn_jianhao;
	private TextView tv_zhongshu;
	private int colckCount = 1;// 钟数
	// 代金券
	private TextView tv_quanPrice;// 代金券金额
	private TextView tv_bootom_quanPrice;
	private RelativeLayout layout_selectDaijinquan;// 选择代金券
	private ImageView iv_quanPrice_next;
	private String couponID = "";// 代金券
	private String couponPrice = "0";

	// 优惠券 减免
	double coupon_db = 0d;

	// 技师
	private TextView tv_xuanzejishi;
	private RelativeLayout layout_selectJishi;// 选择技师
	// 带床
	private View view_isbed;
	private RelativeLayout layout_isbed, layout_bed_tips;
	private TextView tv_bootom_bedFee;

	// 交通费
	private TextView tv_bootom_trafficFee;

	// 技师
	private String sex;
	private String skillWorkerID = "";// 是否是根据技师 来预约的
	private String skillGrade = "";// 技师等级
	private String skillWorkerName = "";// 技师名字
	private String skillGradeName;// 技师等级
	private String serviceType = "";// 1 到店， 2上门

	// 交通费 增加
	double transportationFee_db = 0d;
	// 备注
	private EditText et_appoint_remark;
	private String remark = "";// 备注

	// 等级
	private ArrayList<JSONObject> servGradeList;// 等级列表
	private String gradePrice = "";// 等级对应的价格
	private String marketPrice = "";// 原价的价格
	private String servGradeID;// 等级Id
	private LinearLayout layout_store_serviceLevel;
	private View serviceLevel_bottomLine;
	// 地址联系人
	private RelativeLayout layout_serviceAddress;// 联系人服务地址
	private TextView tv_serviceAddress;
	private TextView tv_serviceAddress_contactName;
	private TextView tv_serviceAddress_contactPhone;

	// 获取默认地址
	private GetUserAddressList getUserAddressList = null;
	private ArrayList<JSONObject> address_list;
	private JSONObject moren_JsonObject = null;
	private Handler handler_morenAddress;

	private String address = "";
	private String userArea = "";
	private String userID = "";
	private String addressID = "";
	private String userName = "";
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

	// 计算费用
	private TextView tv_countFee;
	private double db_serviceFee = 0d;// 记录服务单价
	private double db_oldServiceFee = 0d;// 记录服务旧单价
	private String transportationFee = "";// 交通费
	private double countFee;
	private double totalFee;
	private int totalTime;
	private String isBed = "";// 是否带床
	private double bedFee = 0d;// 床费
	// 提交订单
	private CommitOrder_Thread_Store commitOrder_Thread = null;
	private Handler handler_commitOrder;
	private Button bt_appoint_commit;
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
	private int FLAG_PAYTYPE = -1;// -1:代表账户余额：0：表示不用支付宝（账户）或者线下；1：表示支付宝--调用支付宝；2：表示微信支付--调用微信APP

	// 提交订单成后返回支付
	private JSONObject orderObj = null;
	private String alipayData;
	private JSONObject tenpayData;
	private String new_orderID = "";

	private int appointServType = -1;// 1 到店， 2上门，3 自营的上门

	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private RelativeLayout rl_commit;
}
