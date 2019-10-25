package com.huatuo.citylist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.citylist.view.PinyinComparator;
import com.huatuo.citylist.view.SideBar;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetCityList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Toast_Util;

public class CityListActivity extends BaseActivity implements OnClickListener {
	private Context context_ = CityListActivity.this;

	// private ContactListViewImpl listview;

	private EditText searchBox;
	private String searchString;

	private Object searchLock = new Object();
	boolean inSearchMode = false;

	private final static String TAG = "MainActivity2";


	private Handler mHandler;
	private GetCityList getCityList;
	private ArrayList<JSONObject> cityList;

	private String selected_city, loaction_city;
	private String selected_cityCode;
	private String selected_city_openStatus;
	private String cityListStr = null;
	private Intent intent;
	private MyAdapter adapter;
	private ListView mListView;
	private SideBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	private View head;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.city_list);
		// findViewById();
		initListview();
		setOnListener();
		initHandler();
		getCityList();
	}

	private void initListview() {

		mListView = (ListView) findViewById(R.id.listview);
		indexBar = (SideBar) findViewById(R.id.sideBar);

		mDialogText = (TextView) LayoutInflater.from(this).inflate(
				R.layout.cityl_list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);

		View head = LayoutInflater.from(this).inflate(R.layout.city_list_head,
				null);
		mListView.addHeaderView(head);
		// 实例化自定义内容适配类
		adapter = new MyAdapter(this);
		// 为listView设置适配
		mListView.setAdapter(adapter);
		mListView.setOnItemClickListener(new MyOnItemClickListener());

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		indexBar.setTextView(mDialogText);
		
		// 加载数据失败页面
				rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
				rl_loadData_error.setOnClickListener(this);
				// 加载数据前的白界面
				rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	private void findViewById() {
		// searchBox = (EditText) findViewById(R.id.input_search_query);
		// searchBox.addTextChangedListener(this);

		// listview.setFastScrollEnabled(true);
	}

	private void setOnListener() {
		 bindListener();
		// listview.setOnItemClickListener(new MyOnItemClickListener());
	}

	private void initHandler() {
		// 获取城市列表
		mHandler = new MyHandler();

	}

	class MyOnItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
//			Content content = (Content) parent.getAdapter().getItem(position);
			Content content = (Content) parent.getAdapter().getItem(position);
			if (null != content && !("").equals(content)) {

				String city = content.getName();
				String cityCode = content.getCode();
				String openStatus = content.getServingStatus();
				handleSelectedCity(city, cityCode, openStatus);
//				Toast_Util.showToast(CityListActivity.this, city);
			}

		}

	}

	private void handleSelectedCity(String city, String cityCode,
			String openStatus) {
		// 获取定位到的城市
//		getLoacationCity();

//		CommonUtil.log("切换城市前 ---定位到的loaction_city:" + loaction_city);
		CommonUtil.log("切换城市前 ---点击的的city:" + city);
		CommonUtil.log("切换城市前 ---点击到的cityCode:" + cityCode);
		CommonUtil.log("切换城市前 ---点击到的openStatus:" + openStatus);
		// 定位城市是否为空，为空代表未定位或失败
//		if (!TextUtils.isEmpty(loaction_city)) {
//			// 比较当前定位城市 与要切换的城市是否一样
//			if (!city.equals(loaction_city)) {
//				// 定位城市 与 要切换的城市不一样；提示是否切换
//				CommonUtil.log("切换城市前 --定位城市 与 要切换的城市不一样；提示是否切换-");
//				isChangeCity(loaction_city, city, cityCode, openStatus, "", "");
//			} else {
//				// 定位的城市与切换的城市一样，不用提示
//				setResultOfSelectCity(city, cityCode, openStatus, cityListStr);//
//			}
//		} else {
			setResultOfSelectCity(city, cityCode, openStatus, cityListStr);// 将选中的城市信息带回到对应的界面
//		}
	}

	private void getCityList() {

		// 从上个页面传过来的城市列表是否为空
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			cityListStr = bundle.getString("cityList");
		}

		CommonUtil.logE("--------------------------------城市列表：cityListStr："
				+ cityListStr);

		if (TextUtils.isEmpty(cityListStr)) {
			rl_loadData_empty.setVisibility(View.VISIBLE);
			showCustomCircleProgressDialog(null,
					getString(R.string.common_toast_net_prompt_submit));
			getCityList = new GetCityList(this, mHandler);
			Thread thread = new Thread(getCityList);
			thread.start();
		} else {
			CommonUtil.log("cityListStr:" + cityListStr);
			if (!TextUtils.isEmpty(cityListStr)) {
			   handleCityList();
			}

		}

	}

	class MyHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();

				JSONObject jsonObject = getCityList.getOutObj();
				if (null != jsonObject) {
					cityListStr = jsonObject.toString();
				}
				cityList = getCityList.getCityList();
				handleCityList();
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			case MsgId.NET_NOT_CONNECT:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				break;
			}
		}
	}

	private void handleCityList() {
			List<Content> list = addCityToList();
			CommonUtil.log("===========================list:" + list);
			// 根据a-z进行排序
			Collections.sort(list, new PinyinComparator());

			adapter.addList(list);
			// 设置SideBar的ListView内容实现点击a-z中任意一个进行定位
			indexBar.setListView(mListView);
	}

	private List<Content> addCityToList() {
		// 初始化数据
		List<Content> list = GetCityListUtil.getInstance().addCityToList(cityListStr);
		return list;

	}

	/**
	 * 获取定位到的城市
	 */
	private void getLoacationCity() {
		loaction_city = CommonUtil.getStringOfSharedPreferences(
				getApplicationContext(), "LOCATION_CITY", "");
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
	 */
	private void saveSelectedCity(String changeCity, String changeCityCode,
			String openStatus) {
		CommonUtil.saveStringOfSharedPreferences(this, "SELECTED_CITY",
				changeCity);
		CommonUtil.saveStringOfSharedPreferences(this, "SELECTED_CITYCODE",
				changeCityCode);
		CommonUtil
				.saveStringOfSharedPreferences(this, "OPENSTATUS", openStatus);

	}

	/**
	 * 是否切换城市：自定义Dialog
	 */
	private void isChangeCity(final String city, final String changeCity,
			final String changeCityCode, final String openStatus,
			final String lng, final String lat) {
		
		CustomDialog.Builder builder = new CustomDialog.Builder(this);  
		builder.setTitle("提示");
        builder.setMessage("当前定位城市为：" + city + "市\n您是否需要切换到：" + changeCity
				+ "市 ？");  
        builder.setPositiveButton(getResources().getString(R.string.common_confirm), new DialogInterface.OnClickListener() {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                //设置你的操作事项  
             // 存储要切换的城市的信息
				saveSelectedCity(changeCity, changeCityCode, openStatus);

				// 存储要切换的城市的信息
				setResultOfSelectCity(changeCity, changeCityCode, openStatus,
						cityListStr);// 回调选中的数据：城市和code
            }  
        });  
  
        builder.setNegativeButton(getResources().getString(R.string.common_cancel),  
                new android.content.DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                    }  
                });  
  
        builder.create().show();  

	}

	/**
	 * 
	 * 返回选中的城市信息或 当前
	 * 
	 * @param changeCity
	 * @param changeCityCode
	 * @param openStatus
	 * @param lng
	 * @param lat
	 */
	private void setResultOfSelectCity(String changeCity,
			String changeCityCode, String openStatus, String cityListStr) {
		intent = new Intent();

		CommonUtil.log("setResultOfSelectCity------changeCity:" + changeCity);
		CommonUtil.log("setResultOfSelectCity------changeCityCode:"
				+ changeCityCode);
		CommonUtil.log("setResultOfSelectCity------openStatus:" + openStatus);
		CommonUtil.log("setResultOfSelectCity------cityListStr:" + cityListStr);
		intent.putExtra("cityName", changeCity);
		intent.putExtra("cityCode", changeCityCode);
		intent.putExtra("openStatus", openStatus);
		intent.putExtra("cityList", cityListStr);
		setResult(Activity.RESULT_OK, intent);
		finish();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_loadData_error:// 重新加载数据
			getCityList();
			break;
		}

	}

}
