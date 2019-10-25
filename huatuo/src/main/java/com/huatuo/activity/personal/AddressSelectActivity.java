package com.huatuo.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.adapter.AddressSelectListViewAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetUserAddressList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;

/** 我的地址 */
public class AddressSelectActivity extends BaseActivity {
	private Context mContext;
	private RelativeLayout rl_noAddress;
	private TextView tv_add;
	private ListView listView;
	private AddressSelectListViewAdapter addressListViewAdapter;

	// 获取地址
	private GetUserAddressList getUserAddressList = null;
	private Handler handlerAddress;
	private ArrayList<JSONObject> arrayList;
	public int APPOINTMENT_VIEW = -1;// 预约界面

	/**
	 * 定位刷新广播
	 */
	private IntentFilter dynamic_filter_refreshDelAddress = null;
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver dynamicReceiver;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private RelativeLayout rl_add;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_personal_address_list);
		mContext = AddressSelectActivity.this;
		handlerAddress = new HandlerAddress();
		// 广播接收（先）
		broadcastReceiver();
		// 注册广播（后）
		registeBoardCast();
		initWidget();
		setListener();
	}

	@Override
	public void onResume() {
		super.onResume();
		getAddressList();
	}

	/**
	 * 注册广播
	 */
	private void registeBoardCast() {
		// 删除地址刷新
		dynamic_filter_refreshDelAddress = new IntentFilter();
		dynamic_filter_refreshDelAddress
				.addAction(Constants.REFRESH_DELADDRESS);
		registerReceiver(dynamicReceiver, dynamic_filter_refreshDelAddress);

	}

	/**
	 * 广播接收
	 */
	private void broadcastReceiver() {
		dynamicReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {
				if (null != intent) {
					// 刷新选中tab
					if (intent.getAction().equals(Constants.REFRESH_DELADDRESS)) {
						getAddressList();
					}
				}
			}
		};
	}

	/**
	 * 获取是预约界面的标志
	 */
	private void getFlag() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			APPOINTMENT_VIEW = bundle.getInt(Constants.APPOINT_VIEW);
		}
	}

	/** 在这里获取到每个需要用到的控件的实例，并给它们设置好必要的点击事件 */
	private void initWidget() {
		rl_noAddress = (RelativeLayout) findViewById(R.id.rl_noAddress);
		tv_add = (TextView) findViewById(R.id.tv_add);
		listView = (ListView) findViewById(R.id.listView);
		rl_noAddress.setVisibility(View.GONE);
		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(new MyOnClickListener());
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
		rl_add = (RelativeLayout) findViewById(R.id.rl_add);

		addressListViewAdapter = new AddressSelectListViewAdapter(mContext);
		listView.setAdapter(addressListViewAdapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				JSONObject jsonObject = (JSONObject) parent.getAdapter()
						.getItem(position);
				Intent intent = new Intent();

				intent.putExtra("ADDRESS", jsonObject.toString());
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	protected void setListener() {
		bindListener();
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		tv_add.setOnClickListener(myOnClickListener);
	}

	/**
	 * 获取地址地址列表
	 */
	private void getAddressList() {
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("userID", MyApplication.getUserID());
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		getUserAddressList = new GetUserAddressList(mContext, handlerAddress,
				inJsonMap);
		Thread thread = new Thread(getUserAddressList);
		thread.start();
	}

	/**
	 * 处理 默认地址
	 * 
	 * @author Android开发工程师
	 * 
	 */
	class HandlerAddress extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			rl_add.setVisibility(View.VISIBLE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				addressListViewAdapter.clear();
				arrayList = getUserAddressList.getAddressList();
				if (CommonUtil.emptyListToString3(arrayList)) {
					listView.setVisibility(View.GONE);
					rl_noAddress.setVisibility(View.VISIBLE);
					return;
				}

				if (arrayList.size() == 0) {
					listView.setVisibility(View.GONE);
					rl_noAddress.setVisibility(View.VISIBLE);
				} else {
					rl_noAddress.setVisibility(View.GONE);
					listView.setVisibility(View.VISIBLE);
				}
				for (int i = 0; i < arrayList.size(); i++) {
				JSONObject item = arrayList.get(i);
				if (null != item) {
					String isDefault = item.optString("isDefault", "");
					if ("1".equals(isDefault)) {
						addressListViewAdapter.add(item);
						break;
					}
				}
			}
			
			for (int i = 0; i < arrayList.size(); i++) {
				JSONObject item = arrayList.get(i);
				if (null != item) {
					String isDefault = item.optString("isDefault", "");
					if ("0".equals(isDefault)) {
						addressListViewAdapter.add(item);
					}
				}
			}
			addressListViewAdapter.notifyDataSetChanged();
				addressListViewAdapter.notifyDataSetChanged();
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_add.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				rl_add.setVisibility(View.GONE);
				closeCustomCircleProgressDialog();
				break;
			}
		}

	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.tv_add:// 新建地址
				Intent intent = new Intent();
				intent.setClass(mContext, AddressEditActivity.class);
				intent.putExtra("type", 0);
				startActivity(intent);
				break;
			case R.id.rl_loadData_error:// 重新加载数据
				getAddressList();
				break;
			}
		}
	}

}
