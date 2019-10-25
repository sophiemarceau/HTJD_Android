package com.huatuo.activity.personal;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.adapter.XiaoFeiMaAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetStoreExchangeCodeList;
import com.huatuo.util.DialogUtils;

public class XiaoFeiMaActivity extends BaseActivity {
	private Context mContext;
	private ListView lv_xiaofeima;
	private ImageView iv_wuxiaofeima;
	private RelativeLayout rl_wuxiaofeima;
	private LinearLayout ll_back1;
	private TextView tv_unused, tv_used;
	private View v_unused, v_used;
	private XiaoFeiMaAdapter xiaoFeiMaAdapter;
	private GetStoreExchangeCodeList getStoreExchangeCodeList;
	private Handler mHandler;
	private String state = "2";
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		mContext = XiaoFeiMaActivity.this;
		setContentView(R.layout.activity_xiaofeima);
		mHandler = new MyHandler();
		initWidget();
		bindListener();
		changeSelectedTab(state);
	}

	private void initWidget() {
		tv_unused = (TextView) findViewById(R.id.tv_unused);
		tv_used = (TextView) findViewById(R.id.tv_used);
		v_unused = (View) findViewById(R.id.v_unused);
		v_used = (View) findViewById(R.id.v_used);
		lv_xiaofeima = (ListView) findViewById(R.id.lv_xiaofeima);
		iv_wuxiaofeima = (ImageView) findViewById(R.id.iv_wuxiaofeima);
		rl_wuxiaofeima = (RelativeLayout) findViewById(R.id.rl_wuxiaofeima);
		ll_back1 = (LinearLayout) findViewById(R.id.ll_back1);
		xiaoFeiMaAdapter = new XiaoFeiMaAdapter(mContext);
		lv_xiaofeima.setAdapter(xiaoFeiMaAdapter);
		rl_wuxiaofeima.setVisibility(View.GONE);

		tv_unused.setOnClickListener(new MyOnClickListener());
		tv_used.setOnClickListener(new MyOnClickListener());

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(new MyOnClickListener());
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
		bindListener();
	}

	private void changeSelectedTab(String state) {
		lv_xiaofeima.setVisibility(View.GONE);
		v_unused.setVisibility(View.INVISIBLE);
		v_used.setVisibility(View.INVISIBLE);
		tv_unused.setTextColor(XiaoFeiMaActivity.this.getResources().getColor(
				R.color.c5));
		tv_used.setTextColor(XiaoFeiMaActivity.this.getResources().getColor(
				R.color.c5));
		if ("2".equals(state)) {// 未使用
			v_unused.setVisibility(View.VISIBLE);
			tv_unused.setTextColor(XiaoFeiMaActivity.this.getResources()
					.getColor(R.color.c1));
		} else if ("3".equals(state)) {// 已使用
			v_used.setVisibility(View.VISIBLE);
			tv_used.setTextColor(XiaoFeiMaActivity.this.getResources()
					.getColor(R.color.c1));
		}
		getStoreExchangeCodeList(state);
	}

	private void getStoreExchangeCodeList(String state) {
		showCustomCircleProgressDialog(
				null,
				mContext.getResources().getString(
						R.string.common_toast_net_prompt_submit));
		getStoreExchangeCodeList = new GetStoreExchangeCodeList(mContext,
				mHandler, state);
		Thread thread = new Thread(getStoreExchangeCodeList);
		thread.start();
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				ArrayList<JSONObject> storeExchangeCodeList = getStoreExchangeCodeList
						.getExchangeCoderList();
				xiaoFeiMaAdapter.clear();
				xiaoFeiMaAdapter.add(storeExchangeCodeList);
				xiaoFeiMaAdapter.notifyDataSetChanged();
				if (storeExchangeCodeList.size() == 0) {
					lv_xiaofeima.setVisibility(View.INVISIBLE);
					rl_wuxiaofeima.setVisibility(View.VISIBLE);
				} else {
					rl_wuxiaofeima.setVisibility(View.INVISIBLE);
					lv_xiaofeima.setVisibility(View.VISIBLE);
				}
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}
		}
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.tv_unused:
				state = "2";
				changeSelectedTab(state);
				break;
			case R.id.tv_used:
				state = "3";
				changeSelectedTab(state);
				break;
			case R.id.rl_loadData_error:// 重新加载数据
				getStoreExchangeCodeList(state);
				break;
			default:
				break;
			}
		}
	}
}
