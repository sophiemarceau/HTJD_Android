package com.huatuo.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.base.BaseNetActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.util.Util;

/* 关于华佗 */
public class AboutHuatuoActivity extends BaseNetActivity {
	// private Context mContext;
	// private Handler mHandler;
	// private EditText et_duihuanma;
	// private Button bt_duihuan;
	private TextView tv_about;
	private TextView tv_version, tv_xieyi, tv_ruzhu, tv_fankui;
	private LinearLayout ll_back;

	// private ExchangeListViewAdapter exchangeListViewAdapter;
	// private String couponID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_about_huatuo);
		mContext = this;
		// mHandler = new MyHandler1();
		initWidget();
		bindListener();
	}

	private void initWidget() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		tv_about = (TextView) findViewById(R.id.tv_about);
		tv_version = (TextView) findViewById(R.id.tv_version);
		tv_xieyi = (TextView) findViewById(R.id.tv_xieyi);
		tv_ruzhu = (TextView) findViewById(R.id.tv_ruzhu);
		tv_fankui = (TextView) findViewById(R.id.tv_fankui);
		tv_version.setText(" V" + Util.getVersionName(mContext));
		tv_about.setText("推拿按摩，找我华佗！\n全面：无论到店、到家、都为您提供优质服务；\n权威：全国推拿协会副会长刘长信为首席技术顾问；\n精湛：所有技师至少五年以上经验，更有十年、十五年顶尖高手；\n便捷：全面支持APP/微信下单。");
		// et_duihuanma = (EditText) findViewById(R.id.et_duihuanma);
		// bt_duihuan = (Button) findViewById(R.id.bt_duihuan);
		// lv_duihuanquan = (ListView) findViewById(R.id.lv_duihuanquan);
		//
		// exchangeListViewAdapter = new ExchangeListViewAdapter(mContext);
		// lv_duihuanquan.setAdapter(exchangeListViewAdapter);

	}

	protected void bindListener() {
		// super.bindListener();
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		ll_back.setOnClickListener(myOnClickListener);
		tv_xieyi.setOnClickListener(myOnClickListener);
		tv_ruzhu.setOnClickListener(myOnClickListener);
		tv_fankui.setOnClickListener(myOnClickListener);
		// bt_duihuan.setOnClickListener(myOnClickListener);
	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			Intent intent = null;
			if (intent == null) {
				intent = new Intent();
			}
			switch (view.getId()) {
			case R.id.ll_back1:
				finish();
				break;
			case R.id.tv_xieyi:
				intent.setClass(mContext, YongHuXieYiActivity.class);
				startActivity(intent);
				break;
			case R.id.tv_ruzhu:
				intent.setClass(mContext, MenDianRuZhuActivity.class);
				startActivity(intent);
				break;
			case R.id.tv_fankui:
				intent.setClass(mContext, ShiYongFanKuiActivity.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}
	}

	// class MyHandler1 extends Handler {
	// @Override
	// public void handleMessage(Message msg) {
	//
	// switch (msg.what) {
	// case MsgId.LOGIN_S:
	// closeCustomCircleProgressDialog();
	// finish();
	// break;
	// case MsgId.LOGIN_F:
	// closeCustomCircleProgressDialog();
	// String message = (String) msg.obj;
	// DialogUtils.showToastMsg(mContext, message, Toast.LENGTH_SHORT);
	// break;
	// case MsgId.NET_NOT_CONNECT:
	// setCustomDialog(
	// getString(R.string.common_toast_net_not_connect), true);
	// break;
	// }
	// }
	// }
}
