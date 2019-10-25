package com.huatuo.activity.personal;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.huatuo.R;
import com.huatuo.adapter.SearchArea_Adapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.LocationUtil_GD;
import com.huatuo.util.Toast_Util;

public class AddressSearchAreaActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	/**
	 * 定位刷新广播
	 */
	private IntentFilter dynamic_filter = null;
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver dynamicReceiver;

	private EditText edt_address_search_area;
	private TextView tv_search_area_localtion;
	private ImageView iv_search_area_localtion_refresh, iv_search_address_del;
	private TextView tv_search_empty;
	private ListView lv_search_area;
	private LinearLayout ll_back;
	private SearchArea_Adapter adapter;
	public static final int REQUEST_SELECT_ADDRESS_EDIT = 100;
	public static final int REQUEST_SELECT_ADDRESS_MANAGER = 100;
	private String now_city = ""; // 当前所选城市
	private String select_district = "";// 选择的区
	private String select_city = "";// 选择的城市
	private String select_address = "";// 选择的详细地址
	private String select_lat = "";// 选择的lat
	private String select_lng = "";// 选择的lng

	private String localtion_district = "";// 定位的区
	private String localtion_city = "";// 定位的城市
	private String localtion_address = "";// 定位的详细地址
	private String localtion_lat = "";// 定位的lat
	private String localtion_lng = "";// 定位的lng

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_address_search_area);
		mContext = this;
		now_city = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_CITY", "");
		findViewById();
		setOnClickListener();
		broadcastReceiver();
		// 注册广播
		registeBoardCast();
		getlocaltion();
	}

	private void findViewById() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		edt_address_search_area = (EditText) findViewById(R.id.edt_address_search_area);
		tv_search_area_localtion = (TextView) findViewById(R.id.tv_search_area_locationAddress);
		iv_search_area_localtion_refresh = (ImageView) findViewById(R.id.iv_search_area_location_refresh);
		lv_search_area = (ListView) findViewById(R.id.lv_search_area);
		iv_search_address_del = (ImageView) findViewById(R.id.iv_search_address_del);
		tv_search_empty = (TextView) findViewById(R.id.tv_search_empty);
		adapter = new SearchArea_Adapter(mContext);
		lv_search_area.setAdapter(adapter);
		lv_search_area.setEmptyView(tv_search_empty);
	}

	private void setOnClickListener() {
		ll_back.setOnClickListener(this);
		tv_search_area_localtion.setOnClickListener(this);
		iv_search_area_localtion_refresh.setOnClickListener(this);
		iv_search_address_del.setOnClickListener(this);
		handleOnItemClickListener();
		inputTipsList();
	}

	private void inputTipsList() {

		// 高德
		edt_address_search_area.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

				if (s.toString().trim().length() <= 0) {

					iv_search_address_del.setVisibility(View.INVISIBLE);

				} else {

					iv_search_address_del.setVisibility(View.VISIBLE);

				}
				String newText = s.toString().trim();

				Inputtips inputTips = new Inputtips(
						AddressSearchAreaActivity.this,
						new InputtipsListener() {

							@Override
							public void onGetInputtips(List<Tip> tipList,
									int rCode) {
								if (rCode == 0) {// 正确返回
									List<Tip> listString = new ArrayList<Tip>();
									for (Tip tip : tipList) {
										if (tip != null) {
											String tipName = tip.getName();
											String district = tip.getDistrict();

											CommonUtil.log("tipName:" + tipName
													+ "--district:" + district);
											// 过滤出与当前城市匹配的区域
											// if (!TextUtils.isEmpty(cityName)
											// &&
											// !TextUtils.isEmpty(searchCity)) {
											// if (district.contains(cityName))
											listString.add(tip);
											// }
										}
									}
									// 刷新搜索到的区域
									adapter.add(listString, now_city);
								}
							}
						});
				try {
					CommonUtil.log("onTextChanged:-----------------cityName:"
							+ now_city);
					inputTips.requestInputtips(newText, now_city);// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

				} catch (AMapException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * //清空查询列表数据
	 */
	private void clearSearchInput() {
		edt_address_search_area.getText().clear();
		adapter.clear();
		adapter.notifyDataSetChanged();
	}

	/**
	 * 用户信息修改广播
	 */
	private void registeBoardCast() {

		dynamic_filter = new IntentFilter();
		dynamic_filter.addAction(Constants.REFRESH_LOCATION_SEARCH_PERSON);
		registerReceiver(dynamicReceiver, dynamic_filter);
	}

	/**
	 * 广播接收
	 */
	private void broadcastReceiver() {
		dynamicReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {
				if (null != intent) {
					// 定位刷新
					if (intent.getAction().equals(
							Constants.REFRESH_LOCATION_SEARCH_PERSON)) {
						Bundle bundle = intent.getExtras();
						if (null != bundle) {
							localtion_city = bundle.getString("city");
							localtion_address = bundle.getString("address");
							localtion_lat = bundle.getString("lat");
							localtion_lng = bundle.getString("lng");
							tv_search_area_localtion.setText(localtion_address);

						} else {
							tv_search_area_localtion.setText("定位失败，请重新定位");
						}
					}
				}
			}
		};
	}

	private void handleOnItemClickListener() {
		lv_search_area.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommonUtil.hideKeyboard(mContext, edt_address_search_area);
				CommonUtil.hideKeyboard(mContext, edt_address_search_area);
				Tip tip = (Tip) parent.getAdapter()
						.getItem(position);

				if (null != tip && !("").equals(tip)) {
					select_city = now_city;
					select_district = tip.getDistrict();
					select_address = tip.getName();

						CommonUtil.log("新建地址前  ---点击的的tip--select_city:"
								+ select_city);
						CommonUtil.log("新建地址前  ---点击的的tip--select_address:"
								+ select_address);
						CommonUtil.log("新建地址前 ---点击到的tip--district:"
								+ select_district
								+ select_lng);
						Intent intent = new Intent();
						intent.putExtra("areaName", select_address);
						intent.putExtra("district", select_city
								+ select_district);
						setResult(Activity.RESULT_OK, intent);
						finish();
				} else {
					Toast_Util.showToast(mContext, mContext.getResources()
							.getString(R.string.search_address_error));
				}

			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_back1:
			CommonUtil.hideKeyboard(mContext, edt_address_search_area);
			finish();
			break;
		case R.id.iv_search_address_del:// 清空输入框
			clearSearchInput();
			break;
		case R.id.iv_search_area_location_refresh:// 刷新定位
			LocationUtil_GD.getInstance().stopLocation();
			getlocaltion();
			break;
		case R.id.tv_search_area_locationAddress:// 选择当前定位

			if (!TextUtils.isEmpty(localtion_address)
					&& !TextUtils.isEmpty(localtion_city)) {
				CommonUtil.log("搜索服务区域：点击定位城市：localtionCity:" + localtion_city);
				CommonUtil.log("搜索服务区域：点击定位城市：now_city:" + now_city);
				if (localtion_city.contains(now_city)) {
					Intent intent = new Intent();
					intent.putExtra("areaName", localtion_address);
					intent.putExtra("district", "");
					intent.putExtra("adcode", "");
					setResult(Activity.RESULT_OK, intent);
					finish();
				}

			}

			break;

		}

	}

	/**
	 * 获取定位
	 */
	private void getlocaltion() {
		LocationUtil_GD.getInstance().startLocation(getApplicationContext(),
				Constants.SENDBROACAST_LOCATION_PERSONAL);
		tv_search_area_localtion.setText(getResources().getString(
				R.string.loacation_proceed));
	}

}
