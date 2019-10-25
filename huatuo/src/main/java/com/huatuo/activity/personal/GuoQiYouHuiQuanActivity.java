package com.huatuo.activity.personal;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.adapter.ExchangeListViewAdapter;
import com.huatuo.base.BaseNetActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.AddCoupon;
import com.huatuo.net.thread.CouponList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;

public class GuoQiYouHuiQuanActivity extends BaseNetActivity implements
OnClickListener {
	private Context mContext;
	private Handler mHandler2;
	private ListView lv_duihuanquan;
	private CouponList couponList;
	private LinearLayout ll_back;
	private ExchangeListViewAdapter exchangeListViewAdapter;
	private String couponID;
	private AddCoupon addCoupon;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private RelativeLayout rl_noData;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_guoqiyouhuiquan);
		mContext = GuoQiYouHuiQuanActivity.this;
		mHandler2 = new MyHandler2();
		initWidget();
		bindListener();
		getCouponList();
	}

	private void initWidget() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		lv_duihuanquan = (ListView) findViewById(R.id.lv_duihuanquan);
		exchangeListViewAdapter = new ExchangeListViewAdapter(mContext);
		lv_duihuanquan.setAdapter(exchangeListViewAdapter);
		
		rl_noData = (RelativeLayout) findViewById(R.id.collect_result_noData);
		TextView tv_noData = (TextView) findViewById(R.id.tv_noData);
		tv_noData.setText("暂无过期优惠券");
		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	protected void bindListener() {
		// super.bindListener();
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		ll_back.setOnClickListener(myOnClickListener);

	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.ll_back1:
				finish();
				break;
			default:
				break;
			}
		}
	}

	/* 查询兑换券list */
	private void getCouponList() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		couponList = new CouponList(mContext, mHandler2, "2", "", "");
		Thread thread = new Thread(couponList);
		thread.start();
	}

	class MyHandler2 extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			lv_duihuanquan.setVisibility(View.GONE);
			rl_noData.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				ArrayList<JSONObject> list = couponList.getCouponList();
				if(!CommonUtil.emptyListToString3(list)){
					lv_duihuanquan.setVisibility(View.VISIBLE);
					exchangeListViewAdapter.clear();
					exchangeListViewAdapter.add(list);
				}else {
					rl_noData.setVisibility(View.VISIBLE);
				}
				
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
		case R.id.rl_loadData_error://重新加载数据
			getCouponList();
			break;
		}

	}
}
