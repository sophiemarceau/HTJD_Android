package com.huatuo.activity.seach;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huatuo.R;
import com.huatuo.activity.db.SearchDBUtil;
import com.huatuo.adapter.SearchHistory_Adapter;
import com.huatuo.adapter.Service_girview_Adapter;
import com.huatuo.adapter.Technician_ListViewAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.interfaces.Select_SearchType_OnStore_Interface;
import com.huatuo.net.thread.GetServiceListByPosition;
import com.huatuo.net.thread.GetTechnicianList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Menu_Search_pop;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;

public class Search_OnVisitAty extends BaseActivity implements OnClickListener {
	private Context mContext;
	// private ListView shopExList;
	// private ListView techExList;

	private TextView tv_change_searchType;
	private TextView tv_search;
	private String keywords = "";
	private EditText edt_search;
	private TextView tv_searchResult;
	private RelativeLayout loadMore_result_tips;
	private FrameLayout search_lv;
	private RelativeLayout layout_null_store_bg, layout_null_tech_bg;
	private String storeName;// 店铺名称
	private String lng;// 经度
	private String lat;// 纬度
	private String userID;// 用户ID
	private String sortType;// 排序类型
	private String proCode;// 省代码
	private String cityCode;// 市代码
	private String areaCode;// 区代码
	private String districtCode;// 商圈代码
	private int totalCount;
	private int pageNo = 1;// 页号
	private int pageCount;// 总共页数
	private boolean isBottom = true;

	private String sex;
	private boolean isShowLoading = true;

	private Technician_ListViewAdapter technician_ListViewAdapter;
	private GetTechnicianList getTechnicianList;
	public Handler mHandler_tech;
	private ArrayList<JSONObject> techList;

	// 搜索项目无结果
	private RelativeLayout search_result_noData;
	private ImageView iv_noData;
	private TextView tv_noData;
	// 获取技师列表
	private PullToRefreshListView mPullToRefreshListView;
	private ListView lv_list_tech;
	// 获取项目列表
	private PullToRefreshGridView mPullToRefreshGridView;
	private GridView gv_list_service;

	private GetServiceListByPosition getServiceListByPosition;
	private Service_girview_Adapter adapter_service;
	private ArrayList<JSONObject> servicelist;
	private Handler serviceList_Handler;
	private int searchType;// 0:门店 1：项目
	private int[] searchType_arr_ID = new int[] { R.string.search_menu_service, R.string.search_menu_tech, };
	private Select_SearchType_OnStore_Interface searchType_OnStore_Interface;

	// 搜索历史记录
	private RelativeLayout rl_search_history;
	private TextView tv_search_clear;
	private ListView lv_search_history;
	private SearchHistory_Adapter searchHistory_Adapter;
	private List<String> search_history_list = new ArrayList<String>();
	private Handler search_history_hanler;
	private String keyWords = "";
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = this;
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_search_in_visit);
		initView();
		receiveSearchHistoryByType();
		findSearchHistoryByType(Constants.SEARCH_VISIT_PRO);
		initHandler();//
		changeMenu();

	}

	private void initHandler() {
		mHandler_tech = new MyHandler_Tech();
		serviceList_Handler = new ServiceList_Handler();

	}

	private void initView() {
		loadMore_result_tips = (RelativeLayout) findViewById(R.id.search_result_tips);
		edt_search = (EditText) findViewById(R.id.edt_search);
		tv_change_searchType = (TextView) findViewById(R.id.tv_change_searchType);
		tv_search = (TextView) findViewById(R.id.tv_search);

		// 搜索历史记录
		rl_search_history = (RelativeLayout) findViewById(R.id.rl_search_history);
		tv_search_clear = (TextView) findViewById(R.id.tv_search_clear);
		lv_search_history = (ListView) findViewById(R.id.lv_search_history);
		searchHistory_Adapter = new SearchHistory_Adapter(mContext);
		lv_search_history.setAdapter(searchHistory_Adapter);

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_list_tech);
		lv_list_tech = mPullToRefreshListView.getRefreshableView();

		technician_ListViewAdapter = new Technician_ListViewAdapter(this);
		lv_list_tech.setAdapter(technician_ListViewAdapter);

		mPullToRefreshGridView = (PullToRefreshGridView) findViewById(R.id.gv_list_service);
		gv_list_service = mPullToRefreshGridView.getRefreshableView();

		adapter_service = new Service_girview_Adapter(this);
		gv_list_service.setAdapter(adapter_service);

		// 搜索无结果
		search_result_noData = (RelativeLayout) findViewById(R.id.search_result_noData);
		iv_noData = (ImageView) findViewById(R.id.iv_noData);
		tv_noData = (TextView) findViewById(R.id.tv_noData);

		setOnClickListener();

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	private void isShowListEmpty(boolean isShow) {
		// 是否显示搜索无结果布局
		if (isShow) {
			search_result_noData.setVisibility(View.VISIBLE);
			changeSearchNoDataStyle(searchType);
			showViewByType(3);
		} else {
			showViewByType(searchType);
			search_result_noData.setVisibility(View.GONE);
		}
	}

	/**
	 * 改变搜索无结果状态
	 * 
	 * @param flag
	 */
	private void changeSearchNoDataStyle(int flag) {
		switch (flag) {
		case 0:// 项目
			iv_noData.setImageResource(R.drawable.icon_no_project);
			tv_noData.setText(getResources().getString(R.string.search_no_service));
			break;
		case 1:// 技师
			iv_noData.setImageResource(R.drawable.icon_no_people);
			tv_noData.setText(getResources().getString(R.string.search_no_tech));
			break;
		}

	}

	private void setOnClickListener() {
		bindListener();
		tv_search_clear.setOnClickListener(this);
		tv_change_searchType.setOnClickListener(this);
		tv_search.setOnClickListener(this);
		lv_search_history.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				keyWords = (String) parent.getAdapter().getItem(position);
				edt_search.setText(keyWords);
				edt_search.setSelection(edt_search.getText().toString().trim().length());
				search();
			}
		});
		edt_search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				showViewByType(2);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

		pullToRefresh();

	}

	private void pullToRefresh() {
		mPullToRefreshGridView.setOnRefreshListener(new OnRefreshListener2<GridView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<GridView> refreshView) {
				// TODO Auto-generated method stub
				refresh();
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<GridView> refreshView) {
				// TODO Auto-generated method stub
				loadMore(2);
			}
		});

		mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				refresh();
			}
		});

		mPullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				loadMore(1);
			}
		});

	}

	private void refresh() {
		pageNo = 1;
		search();
	}

	public void loadMore(int type) {
		CommonUtil.log("pageNo:" + pageNo);
		CommonUtil.log("pageCount:" + pageCount);

		if (pageNo < pageCount) {
			++pageNo;
			search();
		} else {
			switch (type) {
			case 1:// 技师
				mPullToRefreshListView.onRefreshComplete();
				break;
			case 2:// 项目
				mPullToRefreshGridView.onRefreshComplete();
				break;
			default:
				break;
			}

			if (pageNo > 1) {
				Toast_Util.showToastOnlyOne(mContext, mContext.getResources().getString(R.string.load_no_more_data));
			}
		}
	}

	private void changeMenu() {
		searchType_OnStore_Interface = new Select_SearchType_OnStore_Interface() {

			@Override
			public void recallSearchType(int type) {
				searchType = type;
				edt_search.getText().clear();
				switch (type) {
				case 0:// 项目
					UmengEventUtil.door_search_choose_project(mContext);
					tv_change_searchType.setText(mContext.getResources().getString(R.string.search_menu_service));
					edt_search.setHint(mContext.getResources().getString(R.string.search_service));
					findSearchHistoryByType(Constants.SEARCH_VISIT_PRO);
					break;

				case 1:// 技师
					UmengEventUtil.door_search_choose_tichnician(mContext);
					tv_change_searchType.setText(mContext.getResources().getString(R.string.search_menu_tech));
					edt_search.setHint(mContext.getResources().getString(R.string.search_tech));
					findSearchHistoryByType(Constants.SEARCH_TECH);
					break;

				}
			}
		};
	}

	/**
	 * 获取技列表
	 * 
	 * 
	 */
	public void getTechnicianList() {

		showCustomCircleProgressDialog(null, mContext.getResources().getString(R.string.common_toast_net_prompt_submit));

		HashMap<String, String> inJsonObject = new HashMap<String, String>();
		// name 技师名称 ANS 64 O 技师名称,可模糊搜素
		// 搜索范围：技师名称，
		// 技师介绍
		// cityCode 城市代码 ANS 64 O 城市代码
		// longitude 经度 ANS 64 O 经度
		// latitude 纬度 ANS 64 O 纬度
		// userID 用户ID ANS 64 O 用户ID
		// orderBy 排序字段 ANS 64 M
		// pageStart 当前页数 N 8 O 默认值为：1
		// 当前第一页
		// pageOffset 获取数据条数 N 8 O 默认值为：10
		// 每页显示10条
		String citycode = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_CITYCODE", "");
		String lng = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LNG", "");
		String lat = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LAT", "");
		inJsonObject.put("longitude", lng);
		inJsonObject.put("latitude", lat);
		inJsonObject.put("cityCode", citycode);
		inJsonObject.put("name", keyWords);
		inJsonObject.put("userID", MyApplication.getUserID());
		inJsonObject.put("orderBy", "0");
		inJsonObject.put("pageStart", pageNo + "");
		inJsonObject.put("pageOffset", Constants.PAGE_SIZE_LIST);
		getTechnicianList = new GetTechnicianList(mContext, mHandler_tech, inJsonObject);
		Thread thread = new Thread(getTechnicianList);
		thread.start();
	}

	class MyHandler_Tech extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:

				// 获取返回数据的页数
				JSONObject jsonObject = getTechnicianList.getOutJsonObj();
				if (null != jsonObject) {
					// pageCount 总共页数 N 10 O
					// tupleCount 总记录数 N 10 O
					String totalPages = jsonObject.optString("pageCount", "0");
					String totalCounts = jsonObject.optString("tupleCount", "0");

					CommonUtil.log("search----visit------总共页数：totalPages：" + totalPages);
					CommonUtil.log("search----visit------总共条数：totalCounts：" + totalCounts);
					if (!TextUtils.isEmpty(totalPages)) {
						pageCount = Integer.parseInt(totalPages);
					}

					if (!TextUtils.isEmpty(totalCounts)) {
						totalCount = Integer.parseInt(totalCounts);

					}
				}

				// 技师列表
				techList = getTechnicianList.getTechnicianList();

				if (!CommonUtil.emptyListToString3(techList)) {
					SearchDBUtil.getInstance().insertDB(mContext, Constants.SEARCH_TECH, keyWords);
					isShowListEmpty(false);
					CommonUtil.log("//获取搜索技师pageNo:" + pageNo);
					if (pageNo == 1) {

						technician_ListViewAdapter.clear();
					}

					technician_ListViewAdapter.add(techList);

				} else {
					isShowListEmpty(true);
				}
				closeCustomCircleProgressDialog();
				break;
			case MsgId.DOWN_DATA_F:
				// 加载数据失败
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources().getString(R.string.common_toast_net_down_data_fail), Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				// 加载数据失败
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				break;
			default:
				break;
			}
			mPullToRefreshListView.onRefreshComplete();
		}

	}

	/**
	 * 获取服务列表
	 */
	private void getServiceList() {
		showCustomCircleProgressDialog(null, mContext.getResources().getString(R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJsonObject = new HashMap<String, String>();

		// type 类型 ANS 64 O 类型 0：到店，1：上门，2：通用
		// name 服务名称 ANS 64 O 关键字 模糊查询
		// 搜索范围:服务名称，服务介绍
		// longitude 经度 ANS 64 O 经度
		// latitude 纬度 ANS 64 O 纬度
		// userID 用户ID ANS 64 O 用户ID
		// orderBy 排序字段 ANS 64 M
		// cityCode 城市代码 ANS 64 O 城市代码，默认北京
		// pageStart 当前页数 N 8 O 默认值为：1
		// 当前第一页
		// pageOffset 获取数据条数 N 8 O 默认值为：10
		// 每页显示10条
		String citycode = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_CITYCODE", "");
		String lng = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LNG", "");
		String lat = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_LAT", "");
		inJsonObject.put("longitude", lng);
		inJsonObject.put("latitude", lat);
		inJsonObject.put("cityCode", citycode);
		inJsonObject.put("type", "1");
		inJsonObject.put("name", keyWords);
		inJsonObject.put("userID", MyApplication.getUserID() + " ");
		inJsonObject.put("orderBy", "0");
		inJsonObject.put("pageStart", pageNo + "");
		inJsonObject.put("pageOffset", Constants.PAGE_SIZE_GRID);
		getServiceListByPosition = new GetServiceListByPosition(mContext, serviceList_Handler, inJsonObject);
		Thread thread = new Thread(getServiceListByPosition);
		thread.start();
	}

	class ServiceList_Handler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// 获取返回数据的页数
				JSONObject jsonObject = getServiceListByPosition.getOutJsonObject();

				servicelist = getServiceListByPosition.getServiceList();

				if (null != jsonObject) {
					// pageCount 总共页数 N 10 O
					// tupleCount 总记录数 N 10 O
					String totalPages = jsonObject.optString("pageCount", "0");
					String totalCounts = jsonObject.optString("tupleCount", "0");

					CommonUtil.log("search----visit------总共页数：totalPages：" + totalPages);
					CommonUtil.log("search----visit------总共条数：totalCounts：" + totalCounts);
					if (!TextUtils.isEmpty(totalPages)) {
						pageCount = Integer.parseInt(totalPages);
					}

					if (!TextUtils.isEmpty(totalCounts)) {
						totalCount = Integer.parseInt(totalCounts);

					}
				}

				if (!CommonUtil.emptyListToString3(servicelist)) {
					SearchDBUtil.getInstance().insertDB(mContext, Constants.SEARCH_VISIT_PRO, keyWords);
					isShowListEmpty(false);
					CommonUtil.log("//获取搜索项目pageNo:" + pageNo);
					if (pageNo == 1) {
						adapter_service.clear();
					}

					adapter_service.add(servicelist, "");

				} else {
					isShowListEmpty(true);
				}

				break;
			case MsgId.DOWN_DATA_F:
				// 加载数据失败
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources().getString(R.string.common_toast_net_down_data_fail), Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				// 加载数据失败
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();
				break;
			}

			mPullToRefreshGridView.onRefreshComplete();
		}
	}

	private void search() {
		switch (searchType) {
		case 0:// 项目
			showViewByType(0);
			if (TextUtils.isEmpty(keyWords) && ("").equals(keyWords.trim())) {
				Toast_Util.showToast(mContext, "请输入相关项目名称");
				break;
			}
			getServiceList();
			break;
		case 1:// 技师
			showViewByType(1);
			if (TextUtils.isEmpty(keyWords) && ("").equals(keyWords.trim())) {
				Toast_Util.showToast(mContext, "请输入相关技师名称");
				break;
			}
			getTechnicianList();
			break;
		}
	}

	private void showViewByType(int type) {
		rl_search_history.setVisibility(View.GONE);
		mPullToRefreshGridView.setVisibility(View.GONE);
		mPullToRefreshListView.setVisibility(View.GONE);
		rl_loadData_error.setVisibility(View.GONE);
		search_result_noData.setVisibility(View.GONE);
		switch (type) {

		case 0:// 项目
			mPullToRefreshGridView.setVisibility(View.VISIBLE);
			break;
		case 1:// 技师
			mPullToRefreshListView.setVisibility(View.VISIBLE);
			break;
		case 2:// 搜索记录

			if (CommonUtil.emptyListToString3(search_history_list)) {
				rl_search_history.setVisibility(View.GONE);
			} else {
				rl_search_history.setVisibility(View.VISIBLE);
			}

			break;
		case 3:// 无数据
			search_result_noData.setVisibility(View.VISIBLE);
			break;
		case 4://加载数据失败
			rl_loadData_error.setVisibility(View.VISIBLE);
			break;
		}
	}

	/**
	 * 查询搜索历史记录
	 * 
	 * @param type
	 */
	private void findSearchHistoryByType(final int type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg = Message.obtain();
				search_history_hanler.sendEmptyMessage(0);
				List<String> search_history_list = SearchDBUtil.getInstance().findDBOfKeyWords(mContext, type);
				msg.what = 1;
				msg.obj = search_history_list;
				search_history_hanler.sendMessage(msg);
			}
		}).start();

	}

	/**
	 * 清空历史记录
	 */
	private void clearHistory() {
		switch (searchType) {
		case 0:// 项目
			clearSearchHistory(Constants.SEARCH_VISIT_PRO);
			break;
		case 1:// 技师
			clearSearchHistory(Constants.SEARCH_TECH);
			break;
		}
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
				boolean isClearSuccess = SearchDBUtil.getInstance().clearSearchHistory(mContext, type);
				msg.what = 2;
				msg.obj = isClearSuccess;
				search_history_hanler.sendMessage(msg);

			}
		}).start();
	}

	private void receiveSearchHistoryByType() {
		search_history_hanler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				CommonUtil.log("msg.what:" + msg.what);
				switch (msg.what) {
				case 0:
					showCustomCircleProgressDialog(null, mContext.getResources().getString(R.string.common_toast_net_prompt_submit));
					break;
				case 1:// 查询
					closeCustomCircleProgressDialog();
					search_history_list = (List<String>) msg.obj;
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
						Toast_Util.showToast(getApplicationContext(), "成功清空搜索记录");
						rl_search_history.setVisibility(View.GONE);
					} else {
						Toast_Util.showToast(getApplicationContext(), "清空搜索记录失败，请重试");
					}
					break;
				}
			}

		};
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.tv_change_searchType:// 改变搜索类型
			UmengEventUtil.door_search_choose(mContext);
			Menu_Search_pop.getInstance().showPupupWindow(this, tv_change_searchType, searchType_arr_ID, searchType_OnStore_Interface);
			break;
		case R.id.tv_search:// 搜索
			pageNo = 1;
			keyWords = edt_search.getText().toString().trim();
			technician_ListViewAdapter.clear();
			adapter_service.clear();
			search();
			UmengEventUtil.door_search(mContext);
			break;
		case R.id.tv_search_clear:// 清空搜索记录
			UmengEventUtil.door_search_delete(mContext);
			clearHistory();
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			pageNo = 1;
			keyWords = edt_search.getText().toString().trim();
			technician_ListViewAdapter.clear();
			adapter_service.clear();
			search();
			break;
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		SearchDBUtil.getInstance().closeTable(this, Constants.SEARCH_TECH);
		SearchDBUtil.getInstance().closeTable(this, Constants.SEARCH_VISIT_PRO);
		super.onDestroy();

	}

}
