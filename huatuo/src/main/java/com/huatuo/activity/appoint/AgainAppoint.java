package com.huatuo.activity.appoint;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;

import com.huatuo.R;
import com.huatuo.activity.home.HomeActivity;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetServiceDetail;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.CustomDialogProgressHandler;
import com.huatuo.util.FormatDistanceUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.Toast_Util;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

public class AgainAppoint {
	private Activity mActivity;
	private String orderClass = "";
	private GetServiceDetail getSysServiceDetail;
	private Handler sysServiceDetail_handler;

	private JSONObject serviceDetailObj = null;
	private String techID, storeID;
	private String isSelfOwned = "";// 0 门店 1 华佗自营
	private String isGoHome = "";// 0 到店 1 上门
	private int appointServType = -100;
	// state storeState workerState 3:生效，9：已下架
	private String storeState = "";
	private String state = "";
	private String workerState = "";

	private static AgainAppoint instance;
	private String msg = "";

	public static AgainAppoint getInstance() {
		if (instance == null) {
			synchronized (AgainAppoint.class) {
				if (instance == null) {
					instance = new AgainAppoint();
				}
			}
		}

		return instance;
	}

	public void againAppoint(Activity activity, String orderClass,
			String serviceID, String techID) {
		this.mActivity = activity;
		this.orderClass = orderClass;
		getSysServiceDetail(serviceID, techID);
	}

	/**
	 * 获取项目详情
	 * 
	 * @param workerID
	 * @param servID
	 */
	private void getSysServiceDetail(String servID, String workerID) {
		CustomDialogProgressHandler.getInstance()
				.showCustomCircleProgressDialog(
						mActivity,
						null,
						mActivity.getResources().getString(
								R.string.common_toast_net_prompt_submit));
		sysServiceDetail_handler = new SysServiceDetail_handler();
		getSysServiceDetail = new GetServiceDetail(mActivity,
				sysServiceDetail_handler, servID, workerID);
		Thread thread = new Thread(getSysServiceDetail);
		thread.start();
	}

	/**
	 * 处理项目详情
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class SysServiceDetail_handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				CustomDialogProgressHandler.getInstance()
						.closeCustomCircleProgressDialog();
				serviceDetailObj = getSysServiceDetail.getOutJson();
				if (null != serviceDetailObj) {
					handleData(serviceDetailObj);
					appoint();
				}
				break;
			case MsgId.DOWN_DATA_F:
				CustomDialogProgressHandler.getInstance()
						.closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				CustomDialogProgressHandler.getInstance().setCustomDialog(
						mActivity,
						mActivity.getResources().getString(
								R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	private void handleData(JSONObject jsonObject) {
		techID = serviceDetailObj.optString("workerID", "");
		storeID = serviceDetailObj.optString("storeID", "");// 店铺ID
		isSelfOwned = serviceDetailObj.optString("isSelfOwned", "");// 0 门店 1
		isGoHome = serviceDetailObj.optString("isGoHome", "");// 0 到店 1 上门

		CommonUtil.log("订单详情：techID：" + techID);
		CommonUtil.log("订单详情：storeID：" + storeID);
		CommonUtil.log("订单详情： 0 门店 1 华佗自营 isSelfOwned：" + isSelfOwned);
		CommonUtil.log("订单详情： 0 到店 1 上门 isGoHome：" + isGoHome);

		// state storeState workerState 3:生效，9：已下架
		storeState = jsonObject.optString("storeState", "-1");
		state = jsonObject.optString("state", "-1");
		workerState = jsonObject.optString("workerState", "-1");
		CommonUtil.log("订单详情：storeState：" + storeState);
		CommonUtil.log("订单详情：state：" + state);
		CommonUtil.log("订单详情：workerState：" + workerState);
	}

	/**
	 * 判断门店 技师 项目状态获取对应提示语
	 */
	private void appoint() {

		// 店铺的项目
		if (("0").equals(isSelfOwned)) {
			if (storeState.equals("9")) {
				// 门店下线 --去往到店列表
				msg = mActivity.getResources()
						.getString(R.string.store_offLine);
				showDialog();
			} else if (state.equals("9")) {
				// 门店在线 --项目下线 去往到店店铺详情
				msg = mActivity.getResources().getString(
						R.string.service_offLine);
				showDialog();

			} else if (workerState.equals("9")) {// 技师下线
				msg = mActivity.getResources().getString(
						R.string.tech_store_offLine);
				showDialog();
			} else {// 未选择选技师或技师
				judgeState();
			}

			// 自营的项目
		} else if (("1").equals(isSelfOwned)) {// 项目下线
			if (state.equals("9")) {
				msg = mActivity.getResources().getString(
						R.string.service_offLine);
				showDialog();
			} else if (workerState.equals("9")) {// 技师下线
				msg = mActivity.getResources().getString(R.string.tech_offLine);
				showDialog();
			} else if (!workerState.equals("9")) {// 未选择选技师或技师（该处正常有技师）
				judgeState();
			}
		}

	}

	private void showDialog() {
		CustomDialog.Builder builder = new CustomDialog.Builder(mActivity);
		builder.setTitle("提示");
		builder.setMessage(msg);
		builder.setPositiveButton(
				mActivity.getResources().getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						judgeState();
					}
				});

		builder.setNegativeButton(
				mActivity.getResources().getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}

	/**
	 * 判断门店 技师 项目状态
	 */
	private void judgeState() {
		/**
		 * ==============================店铺的项目=======================
		 */
		if (("0").equals(isSelfOwned)) {
			if (storeState.equals("9")) {
				// 门店下线 --去往到店列表
				CommonUtil
						.log("-----订单-------------门店下线 --去往到店列表-----------------");
				appointJumpToTargetView(Constants.APPOINT_VIEW_FIST_FRAGMENT);
			} else if (state.equals("9")) {
				CommonUtil
						.log("-----订单-------------门店在线-项目下线- 不管技师状态 ---去往门店详情页面-----------------");
				// 门店在线-项目下线-技师在线 ---去往门店详情页面
				appointJumpToTargetView(Constants.APPOINT_VIEW_STORE_DEATAIL);

			} else if (!workerState.equals("3")) {
				CommonUtil
						.log("-----订单-------------门店在线-项目在线-技师下线 （或无技师）---去往不带技师的项目预约界面-----------------");
				// 门店在线-项目在线-技师下线 ---去往不带技师的项目预约界面
				// 到店---处理服务类型
				if (("0").equals(isGoHome)) {
					appointJumpToTargetView(Constants.APPOINT_VIEW_STORE_NO_TECH);
				} else {

					appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_NO_TECH);
				}

			} else {
				CommonUtil
						.log("-----订单-------------门店在线-项目在线-技师在线 ---去往带技师的项目预约界面-----------------");

				// 到店---处理服务类型
				if (("0").equals(isGoHome)) {
					appointJumpToTargetView(Constants.APPOINT_VIEW_STORE_WITH_TECH);
				} else {

					appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_WITH_TECH);
				}
			}

			/**
			 * ==============================自营的项目=======================
			 */
		} else if (("1").equals(isSelfOwned)) {
			if (state.equals("9")) {
				CommonUtil
						.log("-----订单-------------项目下线-技师在线 ---去往上门页面----------------");
				// 项目下线-技师在线 ---去往上门页面
				appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_FRAGMENT);

			} else if (!workerState.equals("3")) {
				CommonUtil
						.log("-----订单-------------项目在线-技师下线 (或无技师)---去往不带技师的项目预约界面----------------");
				// 项目在线-技师下线 ---去往不带技师的项目预约界面
				// 自营的
				appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_NO_TECH);
			} else {
				CommonUtil
						.log("-----订单-------------项目在线-技师在线 ---去往带技师的项目预约界面----------------");
				// 项目在线-技师下线 ---去往带技师的项目预约界面
				// 自营的
				appointJumpToTargetView(Constants.APPOINT_VIEW_VISIT_WITH_TECH);

			}
		}

	}

	/**
	 * 预约上门的服务类型2上门，3 自营的上门
	 */
	private void getAppointVisitServType() {
		// 预约的服务类型：1 到店(不用判断)， 2上门，3 自营的上门
		if (("0").equals(isSelfOwned)) {
			// 到店---处理服务类型
			appointServType = Constants.SERVTYPE_STORE_VISIT;
			// 自营的
		} else if (("1").equals(isSelfOwned)) {
			appointServType = Constants.SERVTYPE_VISIT;
		}
	}

	private void appointJumpToTargetView(int targetView) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		switch (targetView) {
		case Constants.APPOINT_VIEW_VISIT_WITH_TECH:// 预约上门带技师
			if (null == serviceDetailObj) {
				break;
			}
			getAppointVisitServType();// 预约上门的服务类型2上门，3 自营的上门
			intent.setClass(mActivity,
					Apponitment_OnVisit_FromTechActivity.class);

			bundle.putString(Constants.APPOINT_SERVICE,
					serviceDetailObj.toString());
			bundle.putInt("servType", appointServType);
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
			mActivity.finish();
			break;
		case Constants.APPOINT_VIEW_VISIT_NO_TECH:// 预约上门不带技师
			if (null == serviceDetailObj) {
				break;
			}
			getAppointVisitServType();// 预约上门的服务类型2上门，3 自营的上门
			intent.setClass(mActivity,
					Apponitment_OnVisit_FromProjectActivity.class);
			bundle.putString(Constants.APPOINT_SERVICE,
					serviceDetailObj.toString());
			bundle.putInt("servType", appointServType);
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
			mActivity.finish();
			break;
		case Constants.APPOINT_VIEW_STORE_WITH_TECH:// 预约到店 带技师
			if (null == serviceDetailObj) {
				break;
			}
			intent.setClass(mActivity,
					Apponitment_OnStore_FromTechActivity.class);
			bundle.putString(Constants.APPOINT_SERVICE,
					serviceDetailObj.toString());
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
			mActivity.finish();
			break;
		case Constants.APPOINT_VIEW_STORE_NO_TECH:// 预约到店 不带技师
			if (null == serviceDetailObj) {
				break;
			}
			intent.setClass(mActivity,
					Apponitment_OnStore_FromProjectActivity.class);
			bundle.putString(Constants.APPOINT_SERVICE,
					serviceDetailObj.toString());
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
			mActivity.finish();
			break;
		case Constants.APPOINT_VIEW_STORE_DEATAIL:// 去往店铺详情
			intent.setClass(mActivity, StoreDetail_Aty.class);
			bundle = new Bundle();
			bundle.putString("ID", storeID);
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
			mActivity.finish();
			break;
		case Constants.APPOINT_VIEW_FIST_FRAGMENT:// 去往到店列表
			intent.setClass(mActivity, HomeActivity.class);
			bundle.putInt("tabIndex", 0);
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
			mActivity.finish();
			break;
		case Constants.APPOINT_VIEW_VISIT_FRAGMENT:// 去往上门列表
			intent.setClass(mActivity, HomeActivity.class);
			bundle.putInt("tabIndex", 1);
			intent.putExtras(bundle);
			mActivity.startActivity(intent);
			mActivity.finish();
			break;

		default:
			break;
		}
	}

}
