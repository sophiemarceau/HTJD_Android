package com.huatuo.activity.select_address;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.Tip;
import com.huatuo.R;
import com.huatuo.activity.db.SearchDBUtil;
import com.huatuo.adapter.SearchArea_Adapter;
import com.huatuo.adapter.SearchArea_HistoryAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.bean.SearchAddressObj;
import com.huatuo.citylist.CityListActivity;
import com.huatuo.citylist.Content;
import com.huatuo.citylist.GetCityListUtil;
import com.huatuo.custom_widget.FlowLayout;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetCityList;
import com.huatuo.net.thread.GetShangQuanOfCityList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.LocationUtil_GD;
import com.huatuo.util.Toast_Util;

public class SelectAddress_SearchAreaActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	/**
	 * 定位刷新广播
	 */
	private IntentFilter dynamic_filter = null;
	private IntentFilter dynamic_filter_search = null;
	/**
	 * 广播接收器
	 */
	private BroadcastReceiver dynamicReceiver;
	private EditText edt_address_search_area;
	private TextView tv_change_city;// 切换城市
	private ImageView iv_search_address_del;
	private ListView lv_search_area;
	private LinearLayout ll_back;
	private TextView tv_search_empty;
	private TextView tv_location;

	// 默认城市
	private String df_city = "";
	private String df_addr = "";
	private String df_cityCode = "";
	private String df_openStatus = "";
	private String df_lng = "";
	private String df_lat = "";

	private final int REQUEST_SELECT_CITY = 101;
	private String localtion_district = "";// 定位的区
	private String localtion_city = "";// 定位的城市
	private String localtion_lat = "";// 定位的经纬度
	private String localtion_lng = "";// 定位的经纬度
	private String localtion_address = "";// 定位的详细地址
	private String localtion_cityCode = "";// 定位的城市code--从城市列表中匹配出来
	private String localtion_openStatus = "";// 定位的openStatus--从城市列表中匹配出来
	private String cityName = "";
	private String cityCode = "";
	private String openStatus = "";
	private String cityListStr = null;
	private ArrayList<JSONObject> cityList;
	private SearchArea_Adapter adapter;
	private Intent intent = new Intent();
	private GetCityList getCityList;
	private Handler mHandler_getCityList;
	private boolean isOpen = true;

	private LinearLayout rl_address_default;
	private RelativeLayout rl_loadData_error;
	// 商圈
	private TextView tv_shangquan;
	private ArrayList<JSONObject> tagList;
	private RelativeLayout rl_fl_tab_list;
	private FlowLayout fl_tab_list;
	public Handler mHandler_tagList;
	private GetShangQuanOfCityList getShangQuanOfCityList;
	// 搜索历史记录
	private View footerView;
	private TextView tv_searchAdress_history;
	private ListView lv_search_history;
	private SearchArea_HistoryAdapter searchHistory_Adapter;
	private List<SearchAddressObj> search_history_list = new ArrayList<SearchAddressObj>();
	private Handler search_history_hanler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_select_address_search_area);
		mContext = this;
		findViewById();
		mHandler_getCityList = new MyHandler();
		mHandler_tagList = new TagListHandler();

		receiveExtras();
		setOnClickListener();
		judgeCityIsOpen(isOpen);
		// 获取当前选中的城市
		getSelectedCity();
		broadcastReceiver();
		// 注册广播
		registeBoardCast();

		// intTagList();
		// handleTagList();
		// 处理数据库操作
		receiveSearchHistoryByType();
		getTagList();
		


	}

	/**
	 * 判断城市是否开通
	 */
	private void judgeCityIsOpen(boolean isShow) {
		isOpen = CommonUtil.getBooleanOfSharedPreferences(mContext,
				"CITY_ISOPEN", true);
		CommonUtil.logE("-------isOpen:" + isOpen);
		if (isShow) {
			// tv_location.setVisibility(View.VISIBLE);
			tv_location.setBackground(getResources().getDrawable(
					R.drawable.btn_login_yellow));
			tv_location.setClickable(true);
		} else {
			// tv_location.setVisibility(View.GONE);
			tv_location.setBackground(getResources().getDrawable(
					R.drawable.btn_login_gary));
			tv_location.setClickable(false);
		}
	}

	private void receiveExtras() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			cityListStr = bundle.getString("cityList");
			CommonUtil.log("SelectAddaress_BD:cityListStr" + cityListStr);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == REQUEST_SELECT_CITY) {
				cityName = data.getStringExtra("cityName");
				cityCode = data.getStringExtra("cityCode");
				openStatus = data.getStringExtra("openStatus");
				cityListStr = data.getStringExtra("cityList");
				showCity();
				// 切换城市后获取tag list-->切换城市后重新查询当前城市对应搜索记录
				if (!CommonUtil.emptyListToString3(tagList)) {
					tagList.clear();
				}
				getTagList();

			}
		}
	}

	private void getSelectedCity() {

		cityName = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_CITY", "");
		cityCode = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_CITYCODE", "");
		openStatus = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_OPEN_STATUS", "");
		showCity();
	}

	private void showCity() {
		if (!TextUtils.isEmpty(cityName)) {
			String lastChar = String
					.valueOf(cityName.charAt(cityName.length() - 1));
			if (lastChar.equals("市")) {
				cityName = cityName.substring(0, cityName.length() - 1);
			}
		}

		tv_change_city.setText(cityName);
	}

	private void findViewById() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		tv_change_city = (TextView) findViewById(R.id.tv_change_city);
		edt_address_search_area = (EditText) findViewById(R.id.edt_address_search_area);
		lv_search_area = (ListView) findViewById(R.id.lv_search_area);
		iv_search_address_del = (ImageView) findViewById(R.id.iv_search_address_del);

		tv_location = (TextView) findViewById(R.id.tv_location);
		tv_search_empty = (TextView) findViewById(R.id.tv_search_empty);
		adapter = new SearchArea_Adapter(mContext);
		lv_search_area.setAdapter(adapter);

		rl_address_default = (LinearLayout) findViewById(R.id.rl_address_default);
		// 商圈

		tv_shangquan = (TextView) findViewById(R.id.tv_shangquan);
		rl_fl_tab_list = (RelativeLayout) findViewById(R.id.rl_fl_tab_list);
		fl_tab_list = (FlowLayout) findViewById(R.id.fl_tab_list);
		fl_tab_list.setFocusable(true);
		fl_tab_list.setFocusableInTouchMode(true);
		fl_tab_list.requestFocus();
		// 搜索历史记录
		tv_searchAdress_history = (TextView) findViewById(R.id.tv_searchAdress_history);
		lv_search_history = (ListView) findViewById(R.id.lv_searchAdress_history);

		footerView = LayoutInflater.from(this).inflate(
				R.layout.activity_select_address_search_area_history_clear,
				null);
		TextView tv_search_address_history_clear = (TextView) footerView
				.findViewById(R.id.tv_search_address_history_clear);
		lv_search_history.addFooterView(footerView);
		tv_search_address_history_clear.setOnClickListener(this);

		searchHistory_Adapter = new SearchArea_HistoryAdapter(mContext);
		lv_search_history.setAdapter(searchHistory_Adapter);

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);

	}

	private void setOnClickListener() {
		ll_back.setOnClickListener(this);
		tv_change_city.setOnClickListener(this);
		iv_search_address_del.setOnClickListener(this);
		tv_location.setOnClickListener(this);
		rl_loadData_error.setOnClickListener(this);
		handleOnItemClickListener();
		inputTipsList();
	}

	private void inputTipsList() {
		// 百度
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
				// 判断输入框是否为空 来控制布局显示
				if (TextUtils.isEmpty(newText)) {
					showViewByType(2);
				} else {
					showViewByType(1);
				}
				Inputtips inputTips = new Inputtips(
						SelectAddress_SearchAreaActivity.this,
						new InputtipsListener() {

							@Override
							public void onGetInputtips(List<Tip> tipList,
									int rCode) {
								CommonUtil.logE("输入提示：rCode："+rCode);
								if (rCode == 0) {// 正确返回
									List<Tip> listString = new ArrayList<Tip>();
									for (Tip tip : tipList) {
										if (tip != null) {
											String tipName = tip.getName();
											String district = tip.getDistrict();

											CommonUtil.log("tipName:" + tipName
													+ "--district:" + district);
											// 过滤出与当前城市匹配的区域
											if (!TextUtils.isEmpty(district)
													&& !TextUtils
															.isEmpty(district)) {
												if (district.contains(cityName))
													listString.add(tip);
											}
										}
									}
									// 刷新搜索到的区域
									adapter.add(listString, cityName);
								}
							}
						});
				try {
					CommonUtil.log("onTextChanged:-----------------cityName:"
							+ cityName);
					inputTips.requestInputtips(newText, cityName);// 第一个参数表示提示关键字，第二个参数默认代表全国，也可以为城市区号

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

	private void handleOnItemClickListener() {
		lv_search_area.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommonUtil.hideKeyboard(mContext, edt_address_search_area);
				Tip tip = (Tip) parent.getAdapter().getItem(position);

				if (null != tip && !("").equals(tip)) {
					String areaName = tip.getName();
					String district = tip.getDistrict();
					// String adcode = tip.getAdcode();
					LatLonPoint latLonPoint = tip.getPoint();
					if (null != latLonPoint) {
						String lat = String.valueOf(latLonPoint.getLatitude());
						String lng = String.valueOf(latLonPoint.getLongitude());
						CommonUtil.log("点击的的tip--areaName:" + areaName);
						CommonUtil.log("点击到的tip--district:" + district);
						CommonUtil.log("点击到的tip--lat:" + lat);
						CommonUtil.log("点击到的tip--lng:" + lng);

						saveSelectedCity(cityName, cityCode, openStatus, lat,
								lng, district, areaName, false);
						initMyApplicationCityInfo(cityName, cityCode,
								openStatus, lat, lng, district, areaName, false);
						// 数据库存储搜索的区域信息
						// 搜索成功
						SearchAddressObj searchAddressObj = new SearchAddressObj();
						searchAddressObj.setCityCode(cityCode);
						searchAddressObj.setDistrict(district);
						searchAddressObj.setAreaName(areaName);
						searchAddressObj.setLat(lat);
						searchAddressObj.setLng(lng);
						SearchDBUtil.getInstance().insertDB(mContext,
								Constants.SEARCH_ADDRESS_SELECTCITY,
								searchAddressObj);
						returnResult();
					} else {
						CommonUtil.logE("latLonPoint为空");
						Toast_Util.showToast(mContext, mContext.getResources()
								.getString(R.string.search_address_error));
					}
				} else {
					CommonUtil.logE("tip为空");
					Toast_Util.showToast(mContext, mContext.getResources()
							.getString(R.string.search_address_error));
				}

			}
		});

		// 搜索记录点击事件
		lv_search_history.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommonUtil.hideKeyboard(mContext, edt_address_search_area);
				SearchAddressObj searchAddressObj = (SearchAddressObj) parent
						.getAdapter().getItem(position);
				if (null != searchAddressObj && !("").equals(searchAddressObj)) {
					String areaName = searchAddressObj.getAreaName();
					String district = searchAddressObj.getDistrict();
					// String adcode = tip.getAdcode();
					// LatLng latLonPoint = tip.pt;
					String lat = searchAddressObj.getLat();
					String lng = searchAddressObj.getLng();
					CommonUtil.log("点击的的tip--areaName:" + areaName);
					CommonUtil.log("点击到的tip--district:" + district);
					CommonUtil.log("点击到的tip--lat:" + lat);
					CommonUtil.log("点击到的tip--lng:" + lng);

					saveSelectedCity(cityName, cityCode, openStatus, lat, lng,
							district, areaName, false);
					initMyApplicationCityInfo(cityName, cityCode, openStatus,
							lat, lng, district, areaName, false);
					returnResult();
				} else {
					Toast_Util.showToast(mContext, mContext.getResources()
							.getString(R.string.search_address_error));
				}

			}
		});
	}

	private void returnResult() {
		// intent.putExtra("areaName", areaName);
		// intent.putExtra("district", district);
		// intent.putExtra("adcode", adcode);
		// intent.putExtra("cityName", cityName);
		// intent.putExtra("cityCode", cityCode);
		// intent.putExtra("openStatus", openStatus);
		// intent.putExtra("lat", lat);
		// intent.putExtra("lng", lng);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	/**
	 * 给定默认城市
	 */
	private void getDefaultCityInfo() {

		df_city = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_CITY", mContext.getResources()
						.getString(R.string.df_city));
		df_addr = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_ADDRESS", mContext.getResources()
						.getString(R.string.df_address));
		df_cityCode = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_CITYCODE", mContext.getResources()
						.getString(R.string.df_citycode));
		df_openStatus = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_OPEN_STATUS", mContext
						.getResources().getString(R.string.df_open_status));
		df_lng = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_LNG", mContext.getResources()
						.getString(R.string.df_lng));
		df_lat = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "DF_LAT", mContext.getResources()
						.getString(R.string.df_lat));
	}

	/**
	 * 初始化cityinfo
	 * 
	 * @param lng
	 * @param lat
	 * @param openStatus2
	 * @param cityCode
	 * @param cityName
	 * @param isLo
	 * @param areaName
	 */
	private void initMyApplicationCityInfo(String cityName, String cityCode,
			String openStatus, String lat, String lng, String district,
			String areaName, boolean isLocation) {

		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_CITY", cityName);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_CITYCODE", cityCode);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_OPEN_STATUS", openStatus);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_LNG", lng);
		CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
				"NOW_LAT", lat);

		// 非定位
		if (!isLocation) {
			CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
					"NOW_ADDRESS", cityName + district + areaName);
		} else {
			CommonUtil.saveStringOfSharedPreferences(getApplicationContext(),
					"NOW_ADDRESS", areaName);
		}

	}

	/**
	 * 存储选中的城市的信息 ----暂时不用服务返回的城市的经纬度
	 * 
	 * @param changeCity
	 *            切换的城市
	 * @param changeCityCode
	 *            切换的城市code
	 * @param openStatus
	 *            切换的城市
	 * @param lng
	 * @param lat
	 * @param areaName
	 * @param district
	 * @param isLocation
	 */
	private void saveSelectedCity(String changeCity, String changeCityCode,
			String openStatus, String lat, String lng, String district,
			String areaName, boolean isLocation) {
		CommonUtil.log("saveStringOfSharedPreferences------changeCity:"
				+ changeCity);
		CommonUtil.log("saveStringOfSharedPreferences------changeCityCode:"
				+ changeCityCode);
		CommonUtil.log("saveStringOfSharedPreferences------openStatus:"
				+ openStatus);
		CommonUtil.log("saveStringOfSharedPreferences------lat:" + lat);
		CommonUtil.log("saveStringOfSharedPreferences------lng:" + lng);
		CommonUtil.log("saveStringOfSharedPreferences------district:"
				+ district);
		CommonUtil.log("saveStringOfSharedPreferences------areaName:"
				+ areaName);
		CommonUtil.log("saveStringOfSharedPreferences------isLocation:"
				+ isLocation);

		CommonUtil.saveStringOfSharedPreferences(mContext, "SELECTED_CITY",
				changeCity);
		CommonUtil.saveStringOfSharedPreferences(mContext, "SELECTED_CITYCODE",
				changeCityCode);
		CommonUtil.saveStringOfSharedPreferences(mContext, "OPENSTATUS",
				openStatus);
		CommonUtil.saveStringOfSharedPreferences(mContext, "SELECTED_CITYLAT",
				lat);
		CommonUtil.saveStringOfSharedPreferences(mContext, "SELECTED_CITYLNG",
				lng);
		// 非定位
		if (!isLocation) {
			CommonUtil.saveStringOfSharedPreferences(mContext, "ADDRESS",
					changeCity + "市" + district + areaName);
		} else {
			CommonUtil.saveStringOfSharedPreferences(mContext, "ADDRESS",
					areaName);
		}
	}

	/**
	 * 用户信息修改广播
	 */
	private void registeBoardCast() {

		dynamic_filter_search = new IntentFilter();
		dynamic_filter_search
				.addAction(Constants.REFRESH_LOCATION_SELECTADDRESS);
		registerReceiver(dynamicReceiver, dynamic_filter_search);
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
							Constants.REFRESH_LOCATION_SELECTADDRESS)) {
						Bundle bundle = intent.getExtras();
						if (null != bundle) {
							localtion_city = bundle.getString("city");
							localtion_lat = bundle.getString("lat");
							localtion_lng = bundle.getString("lng");
							localtion_address = bundle.getString("address");
							CommonUtil.log("REFRESH_LOCATION_HOME:city:"
									+ localtion_city);
							CommonUtil.log("REFRESH_LOCATION_HOME:lat:"
									+ localtion_lat);
							CommonUtil.log("REFRESH_LOCATION_HOME:lng:"
									+ localtion_lng);
							CommonUtil
									.logE("--------------------SelectAddress_BD---------------------------cityListStr:"
											+ cityListStr);
							if (TextUtils.isEmpty(cityListStr) && !TextUtils.isEmpty(localtion_city)) {
								getCityList();
							} else {
								closeCustomCircleProgressDialog();
								if (!TextUtils.isEmpty(localtion_city)
										&& !(TextUtils
												.isEmpty(localtion_address))) {
									unOpenThisCity(getCityCodeByLocationCity());
								} else {
									if (CommonUtil.isNetworkAvailable(mContext)) {
										// 定位失败
										Toast_Util.showToastOnlyOne(mContext,
												"定位失败，请重新定位", Gravity.BOTTOM,
												false);
									} else {
										Toast_Util
												.showToastOnlyOne(
														mContext,
														mContext.getResources()
																.getString(
																		R.string.common_toast_net_connect_error),
														Gravity.BOTTOM, false);
									}
								}

							}

						} else {
							closeCustomCircleProgressDialog();
							if (CommonUtil.isNetworkAvailable(mContext)) {
								// 定位失败
								Toast_Util.showToastOnlyOne(mContext,
										"定位失败，请重新定位", Gravity.BOTTOM, false);
							} else {
								Toast_Util
										.showToastOnlyOne(
												mContext,
												mContext.getResources()
														.getString(
																R.string.common_toast_net_connect_error),
												Gravity.BOTTOM, false);
							}

						}
					}
				}
			}
		};
	}

	/**
	 * 获取城市列表
	 */
	private void getCityList() {
		getCityList = new GetCityList(mContext, mHandler_getCityList);
		Thread thread = new Thread(getCityList);
		thread.start();
	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				JSONObject jsonObject = getCityList.getOutObj();
				if (null != jsonObject) {
					cityListStr = jsonObject.toString();
					CommonUtil.saveStringOfSharedPreferences(
							getApplicationContext(), "city_list", cityListStr);
				}
				cityList = getCityList.getCityList();
				unOpenThisCity(getCityCodeByLocationCity());
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(
						getString(R.string.common_toast_net_not_connect), true);
				break;
			}
		}
	}

	/**
	 * 获取城市cityCode
	 */
	private boolean getCityCodeByLocationCity() {
		List<Content> cityList = addCityToList();
		CommonUtil
				.log("=========================================== 获取城市cityCodecityList:"
						+ cityList);
		if (!CommonUtil.emptyListToString3(cityList)) {
			for (int i = 0; i < cityList.size(); i++) {
				String city = cityList.get(i).getName();

				CommonUtil.log("city:" + city);
				CommonUtil.log("localtion_city:" + localtion_city);

				// if ((localtion_city).equals(city)) {
				if (localtion_city.contains(city)) {

					String cityCode = cityList.get(i).getCode();
					String openStatus = cityList.get(i).getServingStatus();

					localtion_cityCode = cityCode;
					localtion_openStatus = openStatus;
					return true;
				}
			}
		}

		return false;

	}

	private List<Content> addCityToList() {
		// 初始化数据
		List<Content> list = GetCityListUtil.getInstance().addCityToList(
				cityListStr);

		return list;

	}

	private void unOpenThisCity(boolean flag) {

		if (!flag) {
			// 给定默认城市
			getDefaultCityInfo();
			initMyApplicationCityInfo(df_city, df_cityCode, df_openStatus,
					df_lat, df_lng, localtion_district, df_addr, true);
			returnResult();
			Toast_Util.showToastOfLONG(mContext, "您当前所在的城市：" + localtion_city
					+ ",还未开通，敬请期待...");
		} else {
			saveSelectedCity(localtion_city, localtion_cityCode,
					localtion_openStatus, localtion_lat, localtion_lng,
					localtion_district, localtion_address, true);
			initMyApplicationCityInfo(localtion_city, localtion_cityCode,
					localtion_openStatus, localtion_lat, localtion_lng,
					localtion_district, localtion_address, true);
			returnResult();
		}
	}

	/**
	 * 查询搜索历史记录
	 * 
	 * @param type
	 */
	private void findSearchHistoryByType(final int type, final String cityCode) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				search_history_hanler.sendEmptyMessage(0);

				List<SearchAddressObj> search_history_list = SearchDBUtil
						.getInstance().findDBOfSearchAddres(mContext, type,
								cityCode);
				msg.what = 1;
				msg.obj = search_history_list;
				search_history_hanler.sendMessage(msg);
			}
		}).start();

	}

	/**
	 * 清空历史记录
	 */
	private void clearSearchHistory(final int type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				search_history_hanler.sendEmptyMessage(0);
				boolean isClearSuccess = SearchDBUtil.getInstance()
						.clearSearchHistoryOfSearchAddress(mContext, type,
								cityCode);
				msg.what = 2;
				msg.obj = isClearSuccess;
				search_history_hanler.sendMessage(msg);
			}
		}).start();
	}

	private void receiveSearchHistoryByType() {
		search_history_hanler = new Handler() {
			@SuppressWarnings("unchecked")
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				CommonUtil.log("msg.what:" + msg.what);
				switch (msg.what) {
				case 0:
					// showCustomCircleProgressDialog(
					// null,
					// mContext.getResources().getString(
					// R.string.common_toast_net_prompt_submit));
					break;
				case 1:// 查询
					closeCustomCircleProgressDialog();
					search_history_list = (List<SearchAddressObj>) msg.obj;
					if (!CommonUtil.emptyListToString3(search_history_list)) {
						Collections.reverse(search_history_list);
					}
					showViewByType(2);
					searchHistory_Adapter.clear();
					searchHistory_Adapter.add(search_history_list);
					break;
				case 2:// 清空
					closeCustomCircleProgressDialog();
					boolean isClearSuccess = (Boolean) msg.obj;
					if (isClearSuccess) {
						search_history_list.clear();
						searchHistory_Adapter.clear();
						searchHistory_Adapter.notifyDataSetChanged();
						Toast_Util.showToast(getApplicationContext(),
								"成功清空搜索记录");
						showViewByType(2);
					} else {
						showViewByType(2);
						Toast_Util.showToast(getApplicationContext(),
								"清空搜索记录失败，请重试");
					}
					break;
				}
			}

		};
	}

	private void showViewByType(int type) {

		switch (type) {
		case 1:// 搜索地址列表
			rl_loadData_error.setVisibility(View.GONE);
			// 商圈和搜索历史记录布局
			rl_address_default.setVisibility(View.GONE);
			// 无数据
			tv_search_empty.setVisibility(View.GONE);
			// 搜索区域列表
			lv_search_area.setVisibility(View.VISIBLE);
			break;
		case 2:// 搜索历史记录
				// rl_loadData_error.setVisibility(View.GONE);
				// 商圈和搜索历史记录布局
			rl_address_default.setVisibility(View.VISIBLE);
			// 无数据
			tv_search_empty.setVisibility(View.GONE);
			// 搜索区域列表
			lv_search_area.setVisibility(View.GONE);

			isShowSearchHistory();

			break;

		case 3:// 无数据
			rl_loadData_error.setVisibility(View.GONE);
			lv_search_area.setVisibility(View.GONE);
			tv_search_empty.setVisibility(View.VISIBLE);
			rl_address_default.setVisibility(View.VISIBLE);
			break;
		case 4:// 加载数据失败
				// 商圈和搜索历史记录布局
			rl_address_default.setVisibility(View.GONE);
			// 无数据
			tv_search_empty.setVisibility(View.GONE);
			// 搜索区域列表
			lv_search_area.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.VISIBLE);
			break;
		case 5:// 商圈信息
			rl_loadData_error.setVisibility(View.GONE);
			rl_address_default.setVisibility(View.VISIBLE);
			isShowTagList();
			break;
		}
	}

	/**
	 * 是否显示商圈列表
	 */
	private void isShowTagList() {
		if (CommonUtil.emptyListToString3(tagList)) {
			rl_fl_tab_list.setVisibility(View.GONE);
			tv_shangquan.setVisibility(View.GONE);
		} else {
			tv_shangquan.setVisibility(View.VISIBLE);
			rl_fl_tab_list.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 是否显示搜索记录
	 */
	private void isShowSearchHistory() {
		lv_search_history.removeFooterView(footerView);
		if (CommonUtil.emptyListToString3(search_history_list)) {
			tv_searchAdress_history.setVisibility(View.GONE);
			lv_search_history.setVisibility(View.GONE);
		} else {
			tv_searchAdress_history.setVisibility(View.VISIBLE);
			lv_search_history.setVisibility(View.VISIBLE);
			lv_search_history.addFooterView(footerView);
		}
	}

	/**
	 * 获取热门商圈
	 */
	private void getTagList() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		// cityCode 城市编码 ANS 64 M 城市编码
		inJsonMap.put("cityCode", cityCode);
		getShangQuanOfCityList = new GetShangQuanOfCityList(mContext,
				mHandler_tagList, inJsonMap);
		Thread thread = new Thread(getShangQuanOfCityList);
		thread.start();
	}

	class TagListHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				JSONObject jsonObject = getShangQuanOfCityList.getOuObjecct();
				if (null != jsonObject) {
					JSONArray array = jsonObject.optJSONArray("areaBusiness");
					tagList = JsonUtil.jsonArray2List(array);
				}
				handleTagList();
				break;
			case MsgId.DOWN_DATA_F:
				showViewByType(4);
				closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				showViewByType(4);
				closeCustomCircleProgressDialog();
				break;
			}
			// 查询搜索记录
			findSearchHistoryByType(Constants.SEARCH_ADDRESS_SELECTCITY,
					cityCode);
		}
	}

	private void handleTagList() {
		showViewByType(5);
		fl_tab_list.removeAllViews();
		if (CommonUtil.emptyListToString3(tagList)) {
			return;
		}

		for (int i = 0; i < tagList.size(); i++) {

			// areaBusinessCode 商圈编码 ANS 64 M 商圈编码
			// areaBusinessName 商圈名称 String 64 M 商圈名称
			// type 商圈类型 ANS 4 M 1 核心商圈 2次级商圈 3一般商圈

			final TextView fuwuName = new TextView(mContext);
			fuwuName.setId(i);
			fuwuName.setPadding(CommonUtil.dip2px(mContext, 10),
					CommonUtil.dip2px(mContext, 0),
					CommonUtil.dip2px(mContext, 10),
					CommonUtil.dip2px(mContext, 0));

			LayoutParams layoutParams = new LayoutParams(
					LayoutParams.WRAP_CONTENT, CommonUtil.dip2px(mContext, 31));
			layoutParams.setMargins(CommonUtil.dip2px(mContext, 3),
					CommonUtil.dip2px(mContext, 4),
					CommonUtil.dip2px(mContext, 3),
					CommonUtil.dip2px(mContext, 4));

			fuwuName.setLayoutParams(layoutParams);
			fuwuName.setGravity(Gravity.CENTER);
			fuwuName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14); // 14sp

			fuwuName.setTextColor(getResources().getColor(R.color.c5));
			fuwuName.setBackground(mContext.getResources().getDrawable(
					R.drawable.custom_shangquan_bg_unselected));
			JSONObject jsonObject = tagList.get(i);
			if (null != jsonObject) {
				String tagName = jsonObject.optString("areaBusinessName", "");
				// String totalCount = jsonObject.optString("totalCount", "0");

				// tagType = jsonObject.optString("type","");
				// ID 标签ID ANS 64 M 标签ID
				// name 标签内容 ANS 64 M 标签内容
				// type 类型 ANS 64 M 类型 0：通用 ,1:到店，2：上门
				fuwuName.setText(tagName);

				fuwuName.setOnClickListener(new TabOnClickListener());
				// if (!("0").equals(totalCount.trim())) {
				fl_tab_list.addView(fuwuName);
				// }

			}
		}

		// 默认选择第一个
		// changeTab(0);

	}

	class TabOnClickListener implements OnClickListener {

		public TabOnClickListener() {
		}

		@Override
		public void onClick(View v) {
			int checkedId = v.getId();
			CommonUtil.log("checkedId:" + checkedId);
			changeTab(checkedId);
		}

	}

	/**
	 * 选中tab的操作
	 * 
	 * @param checkedId
	 */
	private void changeTab(int checkedId) {
		TextView tv;
		int tv_id;
		for (int i = 0; i < fl_tab_list.getChildCount(); i++) {
			tv_id = fl_tab_list.getChildAt(i).getId();
			tv = (TextView) fl_tab_list.getChildAt(i);
			if (checkedId == tv_id) {
				String checkedName = tv.getText().toString();
				changeTabStyle(tv, true);
				edt_address_search_area.setText(checkedName);
				edt_address_search_area.setSelection(edt_address_search_area
						.getText().toString().trim().length());

			} else {
				changeTabStyle(tv, false);
			}
		}
	}

	/**
	 * wrz 改变tab样式
	 * 
	 * @param textView
	 * @param flag
	 */
	private void changeTabStyle(TextView textView, boolean isChecked) {
		// shopdetail_tab_bg"
		// shopdetail_tab_bg_sel"
		if (isChecked) {
			textView.setBackground(mContext.getResources().getDrawable(
					R.drawable.custom_shangquan_bg_selected));
			// textView.setBackgroundResource(R.drawable.shopdetail_tab_bg_sel);
			textView.setTextColor(getResources().getColor(R.color.c5));
		} else {
			textView.setBackground(mContext.getResources().getDrawable(
					R.drawable.custom_shangquan_bg_unselected));
			// textView.setBackgroundResource(R.drawable.shopdetail_tab_bg);
			textView.setTextColor(getResources().getColor(R.color.c5));
		}
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
			showViewByType(2);
			break;
		case R.id.tv_change_city:// 切换城市
			intent.setClass(this, CityListActivity.class);
			intent.putExtra("cityList", cityListStr);
			startActivityForResult(intent, REQUEST_SELECT_CITY);

			clearSearchInput();
			showViewByType(2);
			break;
		case R.id.tv_location:// 定位当前位置

			showCustomCircleProgressDialog(null,
					getString(R.string.common_toast_net_prompt_submit));
			// 定位
			LocationUtil_GD.getInstance().startLocation(
					getApplicationContext(),
					Constants.SENDBROACAST_LOCATION_SELECTADDRESS);
			break;

		case R.id.tv_search_address_history_clear:// 清空搜索历史记录
			clearSearchHistory(Constants.SEARCH_ADDRESS_SELECTCITY);
			break;

		case R.id.rl_loadData_error:// 加载商圈信息失败
			getTagList();
			break;
		}

	}

	@Override
	protected void onDestroy() {
		if (null != dynamicReceiver) {
			unregisterReceiver(dynamicReceiver);
		}
		SearchDBUtil.getInstance().closeTable(this,
				Constants.SEARCH_ADDRESS_SELECTCITY);
		LocationUtil_GD.getInstance().stopLocation();
		super.onDestroy();
	}

}
