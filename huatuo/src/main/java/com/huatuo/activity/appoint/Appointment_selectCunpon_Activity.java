package com.huatuo.activity.appoint;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.adapter.ExchangeListViewAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.AddCoupon;
import com.huatuo.net.thread.CouponList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.JsonUtil;

public class Appointment_selectCunpon_Activity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private Handler mHandler, mHandler2;
	private ListView lv_duihuanquan;
	private CouponList couponList;
	private Button bt_cancel_cunpon;
	private LinearLayout ll_back;
	private ExchangeListViewAdapter exchangeListViewAdapter;
	private String couponID;
	private ArrayList<JSONObject> cunponList = null;
	public static final int RESPON_CODE_CUNPON = 3003;
	private JSONObject cunponJsonObject;
	private int appoint_view;// 那种预约界面

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_useable_exchange_lv);
		mContext = this;
		initWidget();
		setOnClickListener();
		getCunponList();

	}

	private void initWidget() {

		bt_cancel_cunpon = (Button) findViewById(R.id.bt_appoint_commit);
		bt_cancel_cunpon.setText("不使用优惠券");

		lv_duihuanquan = (ListView) findViewById(R.id.lv_duihuanquan);
		exchangeListViewAdapter = new ExchangeListViewAdapter(mContext);
		lv_duihuanquan.setAdapter(exchangeListViewAdapter);

	}

	protected void setOnClickListener() {
		bindListener();
		bt_cancel_cunpon.setOnClickListener(this);

		lv_duihuanquan.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				cunponJsonObject = (JSONObject) arg0.getAdapter().getItem(arg2);
				Intent intent = new Intent();
				intent.putExtra("COUPONS", cunponJsonObject.toString());
				intent.putExtra(Constants.ISUSE_CUNPON, Constants.USE_CUNPON);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}

		});
	}

	/**
	 * 接受代金券list
	 */
	@SuppressWarnings("unchecked")
	private void getCunponList() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			String cunponList_str = bundle.getString("cunponList");
			appoint_view = bundle.getInt(Constants.APPOINT_VIEW);
			CommonUtil.log("接受到的可用的代金券appoint_view:" + appoint_view);
			JSONObject cunponJsonObject;
			try {
				cunponJsonObject = new JSONObject(cunponList_str);
				cunponList = JsonUtil.getListFromJsonArray(cunponJsonObject,
						"couponList");
				CommonUtil.log("接受到的可用的代金券list:" + cunponList);
				if (!CommonUtil.emptyListToString3(cunponList)) {
					exchangeListViewAdapter.clear();
					exchangeListViewAdapter.add(cunponList);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

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
	private void addCoupon(String couponID) {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		AddCoupon addCoupon = new AddCoupon(mContext, mHandler, couponID);
		Thread thread = new Thread(addCoupon);
		thread.start();
	}

	class MyHandler1 extends Handler {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				getCouponList();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				// Log.e("ExchangeActivity",
				// "msg-------------------->"+msg.toString());
				String message = (String) msg.obj;
				DialogUtils.showToastMsg(mContext, message, Toast.LENGTH_SHORT);
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

			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				ArrayList<JSONObject> List = couponList.getCouponList();
				exchangeListViewAdapter.clear();
				exchangeListViewAdapter.add(List);
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				String message = (String) msg.obj;
				DialogUtils.showToastMsg(mContext, message, Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_appoint_commit:// 不使用优惠券
			Intent intent = new Intent();
			intent.putExtra(Constants.ISUSE_CUNPON, Constants.CANCEL_CUNPON);
			setResult(Activity.RESULT_OK, intent);
			finish();
			break;

		}

	}
}
