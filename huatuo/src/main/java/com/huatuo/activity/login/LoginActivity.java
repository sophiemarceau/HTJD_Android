package com.huatuo.activity.login;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.personal.MianZeShengMingActivity;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialogOfOneBtn;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetIdentifyingCodeInvokeItem;
import com.huatuo.net.thread.LoginInvokeItem;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;

public class LoginActivity extends BaseActivity {// 登录
	private Context mContext;
	private Handler mHandler, mHandler2;
	private LinearLayout ll_back;
	private TextView tv_getIdentifyingCode, tv_xieyi/* ,tv_forgetPassword */;
	private ImageView mobile_qingkong, verification_clear;
	private EditText et_mobile, et_password;
	private Button btn_login;
	private GetIdentifyingCodeInvokeItem getIdentifyingCodeInvokeItem;
	private Timer _timer = null;
	private final int BASEMSGID = 99999;
	private CharSequence mobile, password;
	private boolean ISCALLBACK = false;// 登陆后是否需要回调

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_login);
		mContext = this;
		mHandler = new MyHandler();
		mHandler2 = new MyHandler2();
		mobile = "";
		password = "";
		getExtras();
		initWidget();
		bindListener();
	}

	private void getExtras() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			ISCALLBACK = bundle.getBoolean("ISCALLBACK");

		}
		CommonUtil.logE("Login------ISCALLBACK----------------->" + ISCALLBACK);

	}

	/** 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件 */
	private void initWidget() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		tv_xieyi = (TextView) findViewById(R.id.tv_xieyi);
		et_mobile = (EditText) findViewById(R.id.et_mobile);
		et_password = (EditText) findViewById(R.id.et_login_password);
		btn_login = (Button) findViewById(R.id.btn_login);
		tv_getIdentifyingCode = (TextView) findViewById(R.id.tv_getIdentifyingCode_2);

		mobile_qingkong = (ImageView) findViewById(R.id.qingkong);
		verification_clear = (ImageView) findViewById(R.id.verification_clear);
		// tv_xieyi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
	}

	protected void bindListener() {
		// super.bindListener();
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		ll_back.setOnClickListener(myOnClickListener);
		btn_login.setOnClickListener(myOnClickListener);
		tv_getIdentifyingCode.setOnClickListener(myOnClickListener);
		mobile_qingkong.setOnClickListener(myOnClickListener);
		verification_clear.setOnClickListener(myOnClickListener);
		tv_xieyi.setOnClickListener(myOnClickListener);
		addTextChangedListener(1, et_mobile, mobile_qingkong);// 监听手机号输入框的内容变化
		addTextChangedListener(2, et_password, verification_clear);// 监听手机号输入框的内容变化
	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.ll_back1:
				finish();
				break;
			case R.id.btn_login:// 登陆
				UmengEventUtil.visitor_login(mContext);
				String msg = checkUserInputValues();
				if (!TextUtils.isEmpty(msg)) {
					Toast_Util.showToast(mContext, msg);
				}
				break;
			case R.id.tv_getIdentifyingCode_2:// 获取验证码
				UmengEventUtil.visitor_getcode(mContext);
				if (!TextUtils.isEmpty(et_mobile.getText()) && et_mobile.getText().toString().length() == 11
						&& et_mobile.getText().toString().startsWith("1")) {
					getIdentifyingCode(et_mobile.getText().toString());
					tv_getIdentifyingCode.setEnabled(false);
					startTimer();
				} else {

					Toast_Util.showToast(mContext, getString(R.string.tel_mistake));
				}
				break;
			case R.id.qingkong:// 手机号清空
				et_mobile.getText().clear();
				break;
			case R.id.verification_clear:// 验证码清空
				et_password.getText().clear();
				break;
			case R.id.tv_xieyi:
				UmengEventUtil.visitor_protocol(mContext);
				Intent intent = new Intent(mContext, MianZeShengMingActivity.class);
				startActivity(intent);
				break;
			}
		}
	}

	/**
	 * 监听Edittext内容变化
	 * 
	 * @param i
	 * 
	 * @param editText
	 * @param delImg
	 */
	private void addTextChangedListener(final int i, final EditText editText, final ImageView delImg) {
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				switch (i) {
				case 1:// 手机号监听
					mobile = s;
					break;
				case 2:// 验证码监听
					password = s;
					break;
				}
				// TODO Auto-generated method stub
				if (editText.getText().toString().trim().length() <= 0) {

					delImg.setVisibility(View.INVISIBLE);

				} else {

					delImg.setVisibility(View.VISIBLE);

				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (mobile.length() > 0 && password.length() > 0) {
					// btn_login.setTextColor(Color.WHITE);
					btn_login.setBackgroundResource(R.drawable.btn_login_yellow);
				} else {
					// btn_login.setTextColor(Color.GRAY);
					btn_login.setBackgroundResource(R.drawable.btn_login_gary);
				}

			}
		});

	}

	/**
	 * 检测手机号和验证码是否正确
	 * 
	 * @return
	 */
	private String checkUserInputValues() {
		// 判断手机号是否为空
		if (TextUtils.isEmpty(et_mobile.getText())) {
			return getString(R.string.register_mobile_is_null);
		} else if (!TextUtils.isEmpty(et_mobile.getText())) {// 判断手机号不为空
			// 判断手机号是否正确
			if (et_mobile.getText().toString().length() == 11 || et_mobile.getText().toString().startsWith("1")) {

				// 判断验证码是否为空
				if (TextUtils.isEmpty(et_password.getText())) {
					return getString(R.string.register_password_is_null);
				} else {
					login(et_mobile.getText().toString(), et_password.getText().toString());
				}

			} else {

				return getString(R.string.register_mobile_is_error);
			}
		}
		return null;
	}

	/**
	 * 登录
	 */
	private void login(final String userMobile, final String userPassword) {

		// 云测用
		// JSONObject userJsonObject = new JSONObject();
		// try {
		// userJsonObject.put("userID", "14448068363101315");
		// userJsonObject.put("accountBalance", "0.00");
		// userJsonObject.put("serviceCount", "3");
		// userJsonObject.put("code", "0000");
		// userJsonObject.put("msg", "ok");
		// userJsonObject.put("addTime", "2015-10-14 15:13:56");
		// userJsonObject.put("integral", "0");
		// userJsonObject.put("mobile", "15701362810");
		// } catch (JSONException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		// {"userID":"14448068363101315","accountBalance":"0.00","serviceCount":3,"code":"0000","msg":"ok",
		// "addTime":"2015-10-14 15:13:56","integral":0,"mobile":"15701362810"}

		// MyApplication.userID = "14448068363101315";
		// MyApplication.mobile = "15701362810";
		// MyApplication.setLoginFlag(true);
		// MyApplication.setUserID(MyApplication.userID);
		// MyApplication.setUserMobile(MyApplication.mobile);
		// MyApplication.setUserJSON(userJsonObject);
		// finish();

		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		LoginInvokeItem loginInvokeItem = new LoginInvokeItem(mHandler, mContext, userMobile, userPassword);
		Thread thread = new Thread(loginInvokeItem);
		thread.start();

	}

	/**
	 * 获取验证码
	 */
	private void getIdentifyingCode(String userMobile) {
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		getIdentifyingCodeInvokeItem = new GetIdentifyingCodeInvokeItem(mHandler2, mContext, userMobile, 0);
		Thread thread = new Thread(getIdentifyingCodeInvokeItem);
		thread.start();
	}

	private void startTimer() {
		_timer = new Timer();
		_timer.schedule(new NotificationTimeTask(), 0, 1000);
	}

	private class NotificationTimeTask extends TimerTask {
		int time = BASEMSGID + 60;

		@Override
		public void run() {
			Message message = new Message();
			message.what = time--;
			mHandler2.sendMessage(message);
		}
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// if (msg.what > BASEMSGID) {
			// tv_getIdentifyingCode.setText(msg.what - BASEMSGID +
			// getString(R.string.register_get_identifying_code_again));
			// return;
			// }
			// LogUtil.e("deesha", "msg.what=" + msg.what);
			switch (msg.what) {
			case MsgId.LOGIN_S:
				closeCustomCircleProgressDialog();

				if (ISCALLBACK) {
					/**
					 * 登陆成功回调
					 */
					CommonUtil.log("-------------------登录界面：IsLoginUtil.iHandlerLogin：" + LoginUtil.iHandlerLogin);
					if (LoginUtil.iHandlerLogin != null) {
						LoginUtil.iHandlerLogin.doHandler();
					}
				}
				finish();
				break;
			case MsgId.LOGIN_F:
				closeCustomCircleProgressDialog();
				String message = (String) msg.obj;
				DialogUtils.showToastMsg(mContext, message, Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;
			}
		}
	}

	class MyHandler2 extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what > BASEMSGID) {
				tv_getIdentifyingCode.setTextColor(mContext.getResources().getColor(R.color.c7));
				tv_getIdentifyingCode
						.setText(msg.what - BASEMSGID + getString(R.string.register_get_identifying_code_again));
				return;
			}
			switch (msg.what) {
			case MsgId.GET_IDENTIFYING_CODE_S:
				closeCustomCircleProgressDialog();
				// DialogUtils.showToastMsg(mContext,
				// getIdentifyingCodeInvokeItem.getMessage(),
				// Toast.LENGTH_SHORT);
				break;
			case MsgId.GET_IDENTIFYING_CODE_F:
				closeCustomCircleProgressDialog();
				String MSG = getIdentifyingCodeInvokeItem.getOutMsg();
				int code = getIdentifyingCodeInvokeItem.getOutCode();
				CommonUtil.log("获取验证码后返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN || code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					if (code == MsgId.GET_IDENTIFYING_CODE_MORE) {
						CustomDialogOfOneBtn.Builder builder = new CustomDialogOfOneBtn.Builder(LoginActivity.this);
						builder.setTitle("提示");
						builder.setMessage(MSG);
						builder.setPositiveButton(getString(R.string.common_confirm),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int which) {
										dialog.dismiss();
									}
								});

						builder.create().show();
					}
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail),
							Toast.LENGTH_SHORT);
				}
				break;
			case MsgId.REGISTER_S:
				closeCustomCircleProgressDialog();
				String message = (String) msg.obj;
				DialogUtils.showToastMsg(mContext, message, Toast.LENGTH_SHORT);
				finish();
				break;
			case MsgId.REGISTER_F:
				closeCustomCircleProgressDialog();
				message = (String) msg.obj;
				DialogUtils.showToastMsg(mContext, message, Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;
			case BASEMSGID:
				tv_getIdentifyingCode.setText(getString(R.string.register_get_identifyingCode));
				_timer.cancel();
				tv_getIdentifyingCode.setEnabled(true);
				tv_getIdentifyingCode.setTextColor(mContext.getResources().getColor(R.color.c6));
				break;
			}
		}
	}
}