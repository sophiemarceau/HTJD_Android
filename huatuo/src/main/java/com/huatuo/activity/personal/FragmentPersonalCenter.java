package com.huatuo.activity.personal;

import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.login.LoginActivity;
import com.huatuo.base.BaseFragment;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetUserInfo;
import com.huatuo.util.CallUtil;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.UmengEventUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class FragmentPersonalCenter extends BaseFragment {
	private Context mContext;
	private Handler mHandler;
	private View rootLayout;
	private RelativeLayout rl_userInfo, rl_login;
	private LinearLayout ll_address, ll_WoDeDingDan, ll_JinE, ll_YouHuiDuiHuan, ll_ChangJianWenTi, ll_GuanYuHuaTuo, ll_XiaoFeiMa, ll_WoDeShouCang;
	private Button btn_login;
	private ImageView /* iv_kefu, */iv_icon;
	private TextView tv_userMobile, tv_userName, tvYuE, tv_zhuxiao, tv_youhuiduihuan_1, tv_xiaofeima_1;
	private GetUserInfo getUserInfo;
	private JSONObject userObj;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootLayout = inflater.inflate(R.layout.fragment_personal_center, container, false);
		mContext = getActivity();
		mHandler = new MyHandler();
		initWidget();
		bindListener();
		CommonUtil.log("-----------------onCreateView--------------------------");
		// initData();
		return rootLayout;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (MyApplication.getLoginFlag()) {
			getUserInfo();
			rl_userInfo.setVisibility(View.VISIBLE);
			tv_zhuxiao.setVisibility(View.VISIBLE);
			tv_youhuiduihuan_1.setVisibility(View.VISIBLE);
			tv_xiaofeima_1.setVisibility(View.VISIBLE);
			rl_login.setVisibility(View.GONE);
			JSONObject u = MyApplication.getUserJSON();
			if (!("").equals(u) && null != u) {
				String a = u.optString("mobile", "XXXXXXXXXXX");
				CommonUtil.logE("usejson---------->" + u);
				if (a.length() >= 11) {
					tv_userMobile.setText(a.replaceFirst(a.substring(3, 7), "****"));
				}
				ll_JinE.setVisibility(View.VISIBLE);
			}
		} else {
			rl_userInfo.setVisibility(View.GONE);
			tv_zhuxiao.setVisibility(View.GONE);
			tv_youhuiduihuan_1.setVisibility(View.GONE);
			tv_xiaofeima_1.setVisibility(View.GONE);
			rl_login.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		// LogUtil.e("deesha", "HelpFragment onDetach");
	}

	/**
	 * 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件。
	 * 
	 */
	private void initWidget() {
		rl_userInfo = (RelativeLayout) rootLayout.findViewById(R.id.rl_userInfo);
		rl_login = (RelativeLayout) rootLayout.findViewById(R.id.rl_login);
		ll_address = (LinearLayout) rootLayout.findViewById(R.id.ll_dizhi);
		ll_WoDeDingDan = (LinearLayout) rootLayout.findViewById(R.id.ll_WoDeDingDan);
		ll_YouHuiDuiHuan = (LinearLayout) rootLayout.findViewById(R.id.ll_YouHuiDuiHuan);
		ll_ChangJianWenTi = (LinearLayout) rootLayout.findViewById(R.id.ll_ChangJianWenTi);
		ll_GuanYuHuaTuo = (LinearLayout) rootLayout.findViewById(R.id.ll_GuanYuHuaTuo);
		ll_XiaoFeiMa = (LinearLayout) rootLayout.findViewById(R.id.ll_XiaoFeiMa);
		ll_WoDeShouCang = (LinearLayout) rootLayout.findViewById(R.id.ll_WoDeShouCang);
		ll_JinE = (LinearLayout) rootLayout.findViewById(R.id.ll_JinE);
		tv_zhuxiao = (TextView) rootLayout.findViewById(R.id.tv_zhuxiao);
		// tv_zhuxiao.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);//设置下划线
		tv_userName = (TextView) rl_userInfo.findViewById(R.id.tv_userName);
		tv_userMobile = (TextView) rl_userInfo.findViewById(R.id.tv_userMobile);
		tvYuE = (TextView) rootLayout.findViewById(R.id.tvYuE);
		tv_youhuiduihuan_1 = (TextView) rootLayout.findViewById(R.id.tv_youhuiduihuan_1);
		tv_xiaofeima_1 = (TextView) rootLayout.findViewById(R.id.tv_xiaofeima_1);
		// tvMingXi = (TextView) rootLayout.findViewById(R.id.tvMingXi);
		// tvMingXi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		// iv_kefu = (ImageView) rootLayout.findViewById(R.id.iv_kefu);
		iv_icon = (ImageView) rootLayout.findViewById(R.id.iv_icon);
		btn_login = (Button) rootLayout.findViewById(R.id.btn_login);
	}

	private void bindListener() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		btn_login.setOnClickListener(myOnClickListener);
		ll_address.setOnClickListener(myOnClickListener);
		ll_WoDeDingDan.setOnClickListener(myOnClickListener);
		tv_zhuxiao.setOnClickListener(myOnClickListener);
		// ll_ZhangHuChongZhi.setOnClickListener(myOnClickListener);
		ll_ChangJianWenTi.setOnClickListener(myOnClickListener);
		ll_YouHuiDuiHuan.setOnClickListener(myOnClickListener);
		ll_GuanYuHuaTuo.setOnClickListener(myOnClickListener);
		ll_XiaoFeiMa.setOnClickListener(myOnClickListener);
		ll_WoDeShouCang.setOnClickListener(myOnClickListener);
		// iv_kefu.setOnClickListener(myOnClickListener);
		// tvMingXi.setOnClickListener(myOnClickListener);
		ll_JinE.setOnClickListener(myOnClickListener);
	}

	/**
	 * 是否注销：自定义Dialog
	 */
	private void showCustomDialog() {

		CustomDialog.Builder builder = new CustomDialog.Builder(getActivity());
		builder.setTitle("提示");
		builder.setMessage("您确定要注销该账号吗？");
		builder.setPositiveButton(getActivity().getResources().getString(R.string.common_confirm), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				// 设置你的操作事项
				MyApplication.setLoginFlag(false);
				rl_userInfo.setVisibility(View.GONE);
				tv_zhuxiao.setVisibility(View.GONE);
				rl_login.setVisibility(View.VISIBLE);
				tvYuE.setText("￥0");
			}
		});

		builder.setNegativeButton(getActivity().getResources().getString(R.string.common_cancel), new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();

	}

	/**
	 * 打电话
	 */
	private void call() {
		CallUtil.showCallDialog(mContext, Constants.phoneNumber);
	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			Intent intent = null;
			switch (view.getId()) {

			case R.id.ll_YouHuiDuiHuan://优惠券
				if (!MyApplication.getLoginFlag()) {
					DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!", Toast.LENGTH_SHORT);
					return;
				}
				
				UmengEventUtil.my_coupon(mContext);
				intent = new Intent();
				intent.setClass(mContext, ExchangeActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_GuanYuHuaTuo://关于华佗
				UmengEventUtil.my_aboutus(mContext);
				intent = new Intent();
				intent.setClass(mContext, AboutHuatuoActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_XiaoFeiMa://消费吗
				if (!MyApplication.getLoginFlag()) {
					DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!", Toast.LENGTH_SHORT);
					return;
				}
				
				UmengEventUtil.my_code(mContext);
				intent = new Intent();
				intent.setClass(mContext, XiaoFeiMaActivity.class);
				startActivity(intent);
				break;
			case R.id.btn_login:
				intent = new Intent();
				intent.setClass(mContext, LoginActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_dizhi://我的地址
				if (!MyApplication.getLoginFlag()) {
					DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!", Toast.LENGTH_SHORT);
					return;
				}
				
				UmengEventUtil.my_adress(mContext);
				intent = new Intent();
				intent.setClass(mContext, AddressListActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_WoDeDingDan://我的订单
				// CommonUtil.saveBooleanOfSharedPreferences(mContext,
				// "ISSTORE", false);
				if (!MyApplication.getLoginFlag()) {
					DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!", Toast.LENGTH_SHORT);
					return;
				}
				intent = new Intent();
				intent.setAction(Constants.REFRESH_CHANGETAB);
				intent.putExtra("tabIndex", 3);
				mContext.sendBroadcast(intent);
				// intent = new Intent();
				// intent.setClass(mContext, MyOrderListActivity.class);
				// startActivity(intent);
				break;
			case R.id.tv_zhuxiao:// 注销账号
				UmengEventUtil.my_logout(mContext);
				showCustomDialog();
				break;
			case R.id.ll_JinE:
				if (!MyApplication.getLoginFlag()) {
					DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!", Toast.LENGTH_SHORT);
					return;
				}
				intent = new Intent();
				intent.setClass(mContext, AccountDetailActivity.class);
				startActivity(intent);
				break;
			case R.id.ll_WoDeShouCang:
				if (!MyApplication.getLoginFlag()) {
					DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!", Toast.LENGTH_SHORT);
					return;
				}
				UmengEventUtil.my_collect(mContext);
				intent = new Intent();
				intent.setClass(mContext, Collect_ListActivity.class);
				startActivity(intent);
				break;
			// case R.id.tvMingXi:
			// if (!MyApplication.getLoginFlag()) {
			// DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
			// Toast.LENGTH_SHORT);
			// return;
			// }
			// intent = new Intent();
			// intent.setClass(mContext, AccountDetailActivity.class);
			// startActivity(intent);
			// break;
			// case R.id.ll_ZhangHuChongZhi:
			// if (!MyApplication.getLoginFlag()) {
			// DialogUtils.showToastMsg(mContext, "你尚未登录,请登录!",
			// Toast.LENGTH_SHORT);
			// return;
			// }
			// intent = new Intent();
			// intent.setClass(mContext, ChongZhiActivity.class);
			// startActivity(intent);
			// break;
			case R.id.ll_ChangJianWenTi://常见问题
				UmengEventUtil.my_qa(mContext);
				intent = new Intent();
				intent.setClass(mContext, ProblemActivity.class);
				startActivity(intent);
				break;
			// case R.id.iv_kefu:
			// call();
			// break;
			default:
				break;
			}
		}
	}

	private void getUserInfo() {
		showCustomCircleProgressDialog(null, mContext.getResources().getString(R.string.common_toast_net_prompt_submit));
		getUserInfo = new GetUserInfo(mContext, mHandler);
		Thread thread = new Thread(getUserInfo);
		thread.start();
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				userObj = getUserInfo.getUserJson();
				if (!("").equals(userObj) && null != userObj) {
					tvYuE.setText("￥" + NumFormatUtil.centFormatYuanTodouble(userObj.optString("deposit", " ")));
					String couponCount = userObj.optString("couponCount", "");
					String exchangeCodeCount = userObj.optString("exchangeCodeCount", "");
					ImageLoader.getInstance().displayImage(userObj.optString("userIcons", ""), iv_icon);
					MyApplication.setUserJSON(userObj);
					String userName = userObj.optString("name", "");
					if ("未知昵称".equals(userName) || ("").equals(userName)) {
						tv_userName.setVisibility(View.GONE);
					} else {
						tv_userName.setText(userName);
					}
					// 是否显示优惠券
					if (!TextUtils.isEmpty(couponCount) && !couponCount.equals("0")) {
						tv_youhuiduihuan_1.setText("您有" + couponCount + "张优惠券");

					} else {
						tv_youhuiduihuan_1.setText("");
					}
					// 是否显示消费码个数
					if (!TextUtils.isEmpty(exchangeCodeCount) && !exchangeCodeCount.equals("0")) {
						tv_xiaofeima_1.setText("待消费" + exchangeCodeCount + "个");
					} else {
						tv_xiaofeima_1.setText("");
					}
				}
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources().getString(R.string.common_toast_net_down_data_fail), Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(mContext.getResources().getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}
}
