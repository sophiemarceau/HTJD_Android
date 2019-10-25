package com.huatuo.activity.personal;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.adapter.ExchangeListViewAdapter;
import com.huatuo.base.BaseNetActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.AddCoupon;
import com.huatuo.net.thread.CouponList;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Toast_Util;

public class ExchangeActivity extends BaseNetActivity implements
		OnClickListener {
	private Context mContext;
	private Handler mHandler1, mHandler2;
	private EditText et_duihuanma;
	private Button bt_duihuan;
	private ListView lv_duihuanquan;
	private CouponList couponList;
	private LinearLayout ll_back;
	private ExchangeListViewAdapter exchangeListViewAdapter;
	private String activityID;
	private AddCoupon addCoupon;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_exchange);
		mContext = ExchangeActivity.this;
		mHandler1 = new MyHandler1();
		mHandler2 = new MyHandler2();
		initWidget();
		bindListener();
		getCouponList();
	}

	private void initWidget() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		et_duihuanma = (EditText) findViewById(R.id.et_duihuanma);
		bt_duihuan = (Button) findViewById(R.id.bt_duihuan);
		lv_duihuanquan = (ListView) findViewById(R.id.lv_duihuanquan);
		View footer_view = LayoutInflater.from(mContext).inflate(
				R.layout.youhuiduihuan_footer, null);
		lv_duihuanquan.addFooterView(footer_view);
		exchangeListViewAdapter = new ExchangeListViewAdapter(mContext);
		lv_duihuanquan.setAdapter(exchangeListViewAdapter);
		footer_view.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext,
						GuoQiYouHuiQuanActivity.class);
				startActivity(intent);
			}
		});

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	protected void bindListener() {
		// super.bindListener();
		ll_back.setOnClickListener(this);
		bt_duihuan.setOnClickListener(this);

	}

	/* 查询兑换券list */
	private void getCouponList() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		couponList = new CouponList(mContext, mHandler2, "0", "", "");
		Thread thread = new Thread(couponList);
		thread.start();
	}

	/* 添加兑换券 */
	private void addCoupon(String activityID) {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		addCoupon = new AddCoupon(mContext, mHandler1, activityID);
		Thread thread = new Thread(addCoupon);
		thread.start();
	}

	class MyHandler1 extends Handler {
		@Override
		public void handleMessage(Message msg) {
			String OutMsg;
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				OutMsg = addCoupon.getOutMsg();

				JSONObject obj = addCoupon.getOutJson();
				if (null != obj) {
					String Msg = obj.optString("couponType", "");
					String money = obj.optString("couponPrice", "");
					if ("1".endsWith(Msg)) {
						Toast_Util.showToastOfLONG(mContext, "您已成功兑换" + money
								+ "元优惠券，可在“我的华佗-优惠兑换”中查看并使用。");
					} else if ("0".endsWith(Msg)) {
						Toast_Util.showToastOfLONG(mContext, "您已成功充值" + money
								+ "元，可在“我的华佗-账户明细”中查看资金变动信息。");
					}
				}
				getCouponList();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				OutMsg = addCoupon.getOutMsg();
				Toast_Util.showToast(mContext, OutMsg);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
				break;
			}
		}
	}

	class MyHandler2 extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				ArrayList<JSONObject> List = couponList.getCouponList();
				exchangeListViewAdapter.clear();
				exchangeListViewAdapter.add(List);
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				String message = (String) msg.obj;
				DialogUtils.showToastMsg(mContext, message, Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back1:
			finish();
			break;
		case R.id.bt_duihuan://兑换优惠券
			activityID = et_duihuanma.getText().toString();
			// Log.e("activityID------------->", activityID);
			addCoupon(activityID);
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			getCouponList();
			break;
		}

	}
}
