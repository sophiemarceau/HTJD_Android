package com.huatuo.activity.personal;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huatuo.R;
import com.huatuo.adapter.Find_ListViewAdapter;
import com.huatuo.adapter.First_Store_List_Adapter;
import com.huatuo.adapter.Service_girview_Adapter;
import com.huatuo.adapter.Technician_ListViewAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.bean.FindBean;
import com.huatuo.bean.FindItemBean;
import com.huatuo.custom_widget.ObservableScrollView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetCollectList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogSizeUtil;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Toast_Util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Collect_ListActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private RelativeLayout rl_tab_shop, rl_tab_service, rl_tab_tech,
			rl_tab_find;
	private TextView tv_tab_shop, tv_tab_service, tv_tab_tech, tv_tab_find;
	private View view_tab_shop, view_tab_service, view_tab_tech, view_tab_find;

	private RelativeLayout collect_result_noData;

	// 获取店铺列表
	private PullToRefreshListView mPullToRefreshGridView_store;
	// 获取项目列表
	private PullToRefreshGridView mPullToRefreshGridView_pro;
	// 获取技师列表
	private PullToRefreshListView mPullToRefreshGridView_tech;
	// 获取发现列表
	private PullToRefreshListView mPullToRefreshGridView_find;
	private ListView lv_list_store;
	private GridView gv_list_service;
	private ListView lv_list_tech;
	private ListView lv_find_list;

	private ObservableScrollView scrollView1;
	private RelativeLayout rl_find_icon;
	private ImageView iv_top;
	private TextView tv_name;
	private LinearLayout ll_back, ll_share, ll_collect;

	// 获取收藏列表
	private GetCollectList getCollectList;
	private Handler collect_Handler;
	private First_Store_List_Adapter store_List_Adapter;
	private ArrayList<JSONObject> storelist;

	// 获取项目列表
	private Service_girview_Adapter adapter_service;
	private ArrayList<JSONObject> servicelist;

	// 获取技师列表
	private Technician_ListViewAdapter technician_ListViewAdapter;
	private ArrayList<JSONObject> techList;

	private Find_ListViewAdapter find_ListViewAdapter;
	private ArrayList<FindItemBean> findList;

	private int type = 0;// 0 门店;1:项目；2：技师；3：发现
	private int pageNo = 1;// 第几页
	private String pageSize = Constants.PAGE_SIZE_LIST;
	private int pageCount;// 总共页数
	private int totalCount;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		setContentView(R.layout.activity_collect);
		initHandler();
		findViewById();
		setOnClickListener();
		changeTabAndLine(0);
		getCollectList();
	}

	private void initHandler() {
		collect_Handler = new Collect_Handler();
	}

	private void findViewById() {

		rl_tab_shop = (RelativeLayout) findViewById(R.id.rl_tab_shop);
		rl_tab_service = (RelativeLayout) findViewById(R.id.rl_tab_service);
		rl_tab_tech = (RelativeLayout) findViewById(R.id.rl_tab_tech);
		rl_tab_find = (RelativeLayout) findViewById(R.id.rl_tab_find);

		tv_tab_shop = (TextView) findViewById(R.id.tv_tab_shop);
		tv_tab_service = (TextView) findViewById(R.id.tv_tab_service);
		tv_tab_tech = (TextView) findViewById(R.id.tv_tab_tech);
		tv_tab_find = (TextView) findViewById(R.id.tv_tab_find);

		view_tab_shop = (View) findViewById(R.id.view_tab_shop);
		view_tab_service = (View) findViewById(R.id.view_tab_service);
		view_tab_tech = (View) findViewById(R.id.view_tab_tech);
		view_tab_find = (View) findViewById(R.id.view_tab_find);

		collect_result_noData = (RelativeLayout) findViewById(R.id.collect_result_noData);
		mPullToRefreshGridView_store = (PullToRefreshListView) findViewById(R.id.lv_list_store);
		lv_list_store = mPullToRefreshGridView_store.getRefreshableView();
		store_List_Adapter = new First_Store_List_Adapter(this);
		lv_list_store.setAdapter(store_List_Adapter);

		mPullToRefreshGridView_pro = (PullToRefreshGridView) findViewById(R.id.gv_list_service);
		gv_list_service = mPullToRefreshGridView_pro.getRefreshableView();
		adapter_service = new Service_girview_Adapter(this);
		gv_list_service.setAdapter(adapter_service);

		mPullToRefreshGridView_tech = (PullToRefreshListView) findViewById(R.id.lv_list_tech);
		lv_list_tech = mPullToRefreshGridView_tech.getRefreshableView();
		technician_ListViewAdapter = new Technician_ListViewAdapter(this);
		lv_list_tech.setAdapter(technician_ListViewAdapter);

		mPullToRefreshGridView_find = (PullToRefreshListView) findViewById(R.id.lv_find_list);
		lv_find_list = mPullToRefreshGridView_find.getRefreshableView();
		find_ListViewAdapter = new Find_ListViewAdapter(this);
		lv_find_list.setAdapter(find_ListViewAdapter);

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	private void setOnClickListener() {
		bindListener();
		rl_tab_shop.setOnClickListener(this);
		rl_tab_service.setOnClickListener(this);
		rl_tab_tech.setOnClickListener(this);
		rl_tab_find.setOnClickListener(this);
		mPullToRefreshGridView_store
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						refresh();
					}
				});

		mPullToRefreshGridView_store
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						loadMore();
					}
				});

		mPullToRefreshGridView_pro
				.setOnRefreshListener(new OnRefreshListener2<GridView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						// TODO Auto-generated method stub
						refresh();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<GridView> refreshView) {
						// TODO Auto-generated method stub
						loadMore();
					}
				});
		mPullToRefreshGridView_tech
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						refresh();
					}
				});

		mPullToRefreshGridView_tech
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						loadMore();
					}
				});
		mPullToRefreshGridView_find
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						refresh();
					}
				});

		mPullToRefreshGridView_find
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						loadMore();
					}
				});
	}

	private void getCollectList() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJsonObject = new HashMap<String, String>();
		// ID 用户ID ANS 64 M 用户ID
		// type 收藏内容类型 ANS 11 M 收藏内容类型，0 门店
		// latitude 纬度 ANS 64 M 用户纬度
		// longitude 经度 ANS 64 M 用户经度
		// pageStart 当前页数 N 8 O 默认值为：1
		// 当前第一页
		// pageOffset 获取数据条数 N 8 O 默认值为：10
		// 每页显示10条
		String lng = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LNG", "");
		String lat = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LAT", "");
		inJsonObject.put("longitude", lng);
		inJsonObject.put("latitude", lat);
		inJsonObject.put("userID", MyApplication.getUserID());
		inJsonObject.put("type", type + "");
		inJsonObject.put("pageStart", pageNo + "");
		inJsonObject.put("pageOffset", pageSize);
		getCollectList = new GetCollectList(mContext, collect_Handler,
				inJsonObject);
		Thread thread = new Thread(getCollectList);
		thread.start();
	}

	class Collect_Handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// 获取返回数据的页数
				JSONObject jsonObject = getCollectList.getOutJson();
				// 获取返回数据的页数
				if (null == jsonObject) {
					return;
				}

				String totalPages = jsonObject.optString("pageCount", "0");
				String totalCounts = jsonObject.optString("tupleCount", "0");

				CommonUtil.log("收藏总共页数：totalPages：" + totalPages);
				CommonUtil.log("收藏总条数：totalCounts：" + totalCounts);
				if (!TextUtils.isEmpty(totalPages)) {
					pageCount = Integer.parseInt(totalPages);
				}

				if (!TextUtils.isEmpty(totalCounts)) {
					totalCount = Integer.parseInt(totalCounts);

				}

				// invalidName 下架内容
				// invalidCount 下架个数
				String invalidName = jsonObject.optString("invalidName", "");
				String invalidCount = jsonObject.optString("invalidCount", "0");
				CommonUtil.log("收藏下架：invalidName：" + invalidName);
				CommonUtil.log("收藏下架：invalidCount：" + invalidCount);
				int removeCount = Integer.parseInt(invalidCount.trim());
				String tip = "";
				
				
				switch (type) {
				case Constants.TYPE_STORE:// 门店
					storelist = getCollectList.getStoreList();
					if (!CommonUtil.emptyListToString3(storelist)) {
						collect_result_noData.setVisibility(View.GONE);
						if (pageNo == 1) {

							store_List_Adapter.clear();
						}

						store_List_Adapter.add(storelist);

					} else {
						if (pageNo == 1) {
							collect_result_noData.setVisibility(View.VISIBLE);
						}
					}

					if (removeCount > 1) {
						tip = "您有多条收藏的门店已下线，相关收藏失效，敬请悉知";
					} else if (removeCount == 1){
						tip = "您收藏的" + invalidName + "已下线，该收藏失效，敬请悉知";
					}else {
						return;
					}

					showRemoveDialog(tip);
					break;
				case Constants.TYPE_PROJECT:// 项目

					servicelist = getCollectList.getServiceList();
					if (!CommonUtil.emptyListToString3(servicelist)) {
						collect_result_noData.setVisibility(View.GONE);
						if (pageNo == 1) {
							adapter_service.clear();

						}

						adapter_service.add(servicelist, "");

					} else {
						if (pageNo == 1) {
							collect_result_noData.setVisibility(View.VISIBLE);
						}
					}
					if (removeCount > 1) {
						tip = "您有多条收藏的项目已下线，相关收藏失效，敬请悉知";
					} else if (removeCount == 1){
						tip = "您收藏的" + invalidName + "已下线，该收藏失效，敬请悉知";
					}else {
						return;
					}

					showRemoveDialog(tip);
					break;
				case Constants.TYPE_TECH:// 技师

					// 技师列表
					techList = getCollectList.getWorkerList();
					if (!CommonUtil.emptyListToString3(techList)) {
						collect_result_noData.setVisibility(View.GONE);
						if (pageNo == 1) {

							technician_ListViewAdapter.clear();
						}

						technician_ListViewAdapter.add(techList);

					} else {
						if (pageNo == 1) {
							collect_result_noData.setVisibility(View.VISIBLE);
						}
					}
					if (removeCount > 1) {
						tip = "您有多条收藏的技师已下线，相关收藏失效，敬请悉知";
					} else if (removeCount == 1){
						tip = "您收藏的" + invalidName + "已下线，该收藏失效，敬请悉知";
					}else {
						return;
					}

					showRemoveDialog(tip);
					break;
				case Constants.TYPE_FINND:// 发现
					FindBean findBean = JSON.parseObject(jsonObject.toString(), FindBean.class);
					pageCount = findBean.pageCount;
					CommonUtil.log("发现列表总共页数：pageCount：" + String.valueOf(pageCount));
					findList = findBean.discoverList;
					if (!CommonUtil.emptyListToString3(findList)) {
						collect_result_noData.setVisibility(View.GONE);
						if (pageNo == 1) {
							find_ListViewAdapter.clear();
						}
						find_ListViewAdapter.add(findList);

					} else {
						if (pageNo == 1) {
							collect_result_noData.setVisibility(View.VISIBLE);
						}
					}
					if (removeCount > 1) {
						tip = "您有多条收藏的发现已下线，相关收藏失效，敬请悉知";
					} else if (removeCount == 1){
						tip = "您收藏的" + invalidName + "已下线，该收藏失效，敬请悉知";
					}else {
						return;
					}

					showRemoveDialog(tip);
					break;
				}

				break;
			case MsgId.DOWN_DATA_F:
				if (pageNo > 1) {
					pageNo--;
				}
				if (pageNo == 1) {
					rl_loadData_error.setVisibility(View.VISIBLE);
				} else {
					rl_loadData_error.setVisibility(View.GONE);
				}
				closeCustomCircleProgressDialog();

				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
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

			refreshComplete();
		}
	}
	/**
	 * 下架提示
	 */
	/**
	 * 快捷支付 提交支付：自定义Dialog
	 */
	private void showRemoveDialog(String msg) {
		final Dialog dialog = new Dialog(this, R.style.my_dialog);

		// 自定义AlertDialog
		View view = LayoutInflater.from(this).inflate(
				R.layout.alertdialog_one_btn, null);

		TextView pc_confirm = (TextView) view.findViewById(R.id.pc_confirm);
		TextView tv_message = (TextView) view.findViewById(R.id.tv_message);
		tv_message.setText(msg);
		dialog.setContentView(view);
		// 确认
		pc_confirm.setOnClickListener(new android.view.View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		DialogSizeUtil.setDialogSize3(mContext, dialog);
		dialog.setCanceledOnTouchOutside(false);// 设置点击屏幕Dialog不消失
		dialog.show();
	}
	private void changeTabAndLine(int flag) {
		tv_tab_shop.setTextColor(getResources().getColor(R.color.c5));
		tv_tab_service.setTextColor(getResources().getColor(R.color.c5));
		tv_tab_tech.setTextColor(getResources().getColor(R.color.c5));
		tv_tab_find.setTextColor(getResources().getColor(R.color.c5));

		view_tab_shop.setVisibility(View.INVISIBLE);
		view_tab_service.setVisibility(View.INVISIBLE);
		view_tab_tech.setVisibility(View.INVISIBLE);
		view_tab_find.setVisibility(View.INVISIBLE);

		mPullToRefreshGridView_store.setVisibility(View.INVISIBLE);
		mPullToRefreshGridView_pro.setVisibility(View.INVISIBLE);
		mPullToRefreshGridView_tech.setVisibility(View.INVISIBLE);
		mPullToRefreshGridView_find.setVisibility(View.INVISIBLE);

		// lv_brand_shop_zhixuan.setVisibility(View.GONE);
		// lv_brand_shop_near.setVisibility(View.GONE);
		// lv_brand_shop_goodAppraise.setVisibility(View.GONE);
		// lv_brand_shop_hotSell.setVisibility(View.GONE);
		type = flag;
		pageNo = 1;
		pageSize = Constants.PAGE_SIZE_LIST;
		switch (flag) {
		case Constants.TYPE_STORE:// 门店
			tv_tab_shop.setTextColor(getResources().getColor(R.color.c1));
			view_tab_shop.setVisibility(View.VISIBLE);
			mPullToRefreshGridView_store.setVisibility(View.VISIBLE);

			break;
		case Constants.TYPE_PROJECT:// 项目
			pageSize = Constants.PAGE_SIZE_GRID;
			tv_tab_service.setTextColor(getResources().getColor(R.color.c1));
			view_tab_service.setVisibility(View.VISIBLE);
			mPullToRefreshGridView_pro.setVisibility(View.VISIBLE);
			break;
		case Constants.TYPE_TECH:// 技师
			tv_tab_tech.setTextColor(getResources().getColor(R.color.c1));
			view_tab_tech.setVisibility(View.VISIBLE);
			mPullToRefreshGridView_tech.setVisibility(View.VISIBLE);
			break;
		case Constants.TYPE_FINND:// 发现
			tv_tab_find.setTextColor(getResources().getColor(R.color.c1));
			view_tab_find.setVisibility(View.VISIBLE);
			mPullToRefreshGridView_find.setVisibility(View.VISIBLE);
			break;

		case -1:// 没有选

			break;
		}

	}

	/**
	 * 下拉刷新数据
	 */
	private void refresh() {
		pageNo = 1;
		getCollectList();
	}

	/**
	 * 加载更多
	 * 
	 * @param type
	 */
	public void loadMore() {
		CommonUtil.log("pageNo:" + pageNo);
		CommonUtil.log("pageCount:" + pageCount);

		if (pageNo < pageCount) {
			++pageNo;
			getCollectList();
		} else {
			refreshComplete();
			if (pageNo > 1) {
				Toast_Util.showToastOnlyOne(mContext, mContext.getResources()
						.getString(R.string.load_no_more_data));
			}
		}
	}

	private void refreshComplete() {

		switch (type) {
		case Constants.TYPE_STORE:// 店铺
			CommonUtil.log(" 店铺");
			mPullToRefreshGridView_store.onRefreshComplete();
			break;
		case Constants.TYPE_PROJECT:// 项目
			CommonUtil.log(" 项目");
			mPullToRefreshGridView_pro.onRefreshComplete();
			break;
		case Constants.TYPE_TECH:// 技师
			CommonUtil.log(" 技师");
			mPullToRefreshGridView_tech.onRefreshComplete();
			break;
		case Constants.TYPE_FINND:// 发现
			CommonUtil.log(" 发现");
			mPullToRefreshGridView_find.onRefreshComplete();

			break;
		}

	}



	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_tab_shop:// 门店
			changeTabAndLine(Constants.TYPE_STORE);
			store_List_Adapter.clear();
			break;
		case R.id.rl_tab_service:// 项目
			changeTabAndLine(Constants.TYPE_PROJECT);
			adapter_service.clear();
			break;
		case R.id.rl_tab_tech:// 技师
			changeTabAndLine(Constants.TYPE_TECH);
			technician_ListViewAdapter.clear();
			break;
		case R.id.rl_tab_find:// 发现
			changeTabAndLine(Constants.TYPE_FINND);
			find_ListViewAdapter.clear();
			break;
		case R.id.rl_loadData_error:// 重新加载数据
			break;
		}
		pageNo = 1;
		getCollectList();

	}
}
