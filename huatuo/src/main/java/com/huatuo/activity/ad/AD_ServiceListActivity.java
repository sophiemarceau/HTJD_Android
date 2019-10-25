package com.huatuo.activity.ad;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.huatuo.R;
import com.huatuo.adapter.Service_girview_Adapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.ObservableScrollView;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetAD_Detail_List;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.JumpTargetActivityUtil;
import com.huatuo.util.Toast_Util;

public class AD_ServiceListActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;
	private TextView tv_headTiele;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	private RelativeLayout rl_tab_near, rl_tab_minPrice, rl_tab_highAppraise,
			rl_tab_orderMore;
	private TextView tv_tab_near, tv_tab_minPrice, tv_tab_highAppraise,
			tv_tab_orderMore;
	private View view_tab_near, view_tab_minPrice, view_tab_highAppraise,
			view_tab_orderMore;

	// 获取项目列表
	private PullToRefreshGridView mPullToRefreshGridView_pro;
	private GridView gv_list_service;

	private ObservableScrollView scrollView1;
	private RelativeLayout rl_find_icon;
	private ImageView iv_top;
	private TextView tv_name;
	private LinearLayout ll_back, ll_share, ll_collect;

	// 获取项目列表
	private GetAD_Detail_List getAD_Detail_List;
	private Handler ad_Handler;
	private Service_girview_Adapter adapter_service;
	private ArrayList<JSONObject> arraylist;

	private String ID = "";
	private String adName = "";
	private String orderBy = "1";
	private int pageNo = 1;// 第几页
	private String pageSize = Constants.PAGE_SIZE_GRID;
	private int pageCount;// 总共页数
	private int totalCount;
	private boolean push = false;// 是否是点击首图

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		setContentView(R.layout.activity_ad_servicelist);
		initHandler();
		findViewById();
		setOnClickListener();
		getID();
		changeTabAndLine(1);
	}

	/***
	 * 将activity 的创建模式设置为singletask,
	 * 使用这个方法可以再其他activity想这个activity发送Intent时，这个Intent能够及时更新
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		CommonUtil
				.logE("--------------------------------------------onNewIntent-----------------------------");
		super.onNewIntent(intent);
		setIntent(intent); // 这一句必须的，否则Intent无法获得最新的数据
		getID();
		changeTabAndLine(1);
	}

	private void getID() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			ID = bundle.getString("ID");
			adName = bundle.getString("adName");
			push = bundle.getBoolean("push");
			// 获取店铺id
			CommonUtil.log("ID:" + ID);
			CommonUtil.log("adName:" + adName);
		}

		tv_headTiele.setText(adName);
	}

	private void initHandler() {
		ad_Handler = new AD_Handler();
	}

	private void findViewById() {
		
		tv_headTiele = (TextView) findViewById(R.id.tv_headTiele);
		rl_tab_near = (RelativeLayout) findViewById(R.id.rl_tab_near);
		rl_tab_minPrice = (RelativeLayout) findViewById(R.id.rl_tab_minPrice);
		rl_tab_highAppraise = (RelativeLayout) findViewById(R.id.rl_tab_highAppraise);
		rl_tab_orderMore = (RelativeLayout) findViewById(R.id.rl_tab_orderMore);

		tv_tab_near = (TextView) findViewById(R.id.tv_tab_near);
		tv_tab_minPrice = (TextView) findViewById(R.id.tv_tab_minPrice);
		tv_tab_highAppraise = (TextView) findViewById(R.id.tv_tab_highAppraise);
		tv_tab_orderMore = (TextView) findViewById(R.id.tv_tab_orderMore);

		view_tab_near = (View) findViewById(R.id.view_tab_near);
		view_tab_minPrice = (View) findViewById(R.id.view_tab_minPrice);
		view_tab_highAppraise = (View) findViewById(R.id.view_tab_highAppraise);
		view_tab_orderMore = (View) findViewById(R.id.view_tab_orderMore);

		mPullToRefreshGridView_pro = (PullToRefreshGridView) findViewById(R.id.gv_ad_list_service);
		gv_list_service = mPullToRefreshGridView_pro.getRefreshableView();

		adapter_service = new Service_girview_Adapter(this);
		gv_list_service.setAdapter(adapter_service);

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	private void setOnClickListener() {
		LinearLayout ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		ll_back.setOnClickListener(this);
		rl_tab_near.setOnClickListener(this);
		rl_tab_minPrice.setOnClickListener(this);
		rl_tab_highAppraise.setOnClickListener(this);
		rl_tab_orderMore.setOnClickListener(this);

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

	}

	/**
	 * 下拉刷新数据
	 */
	private void refresh() {
		pageNo = 1;
		getList();
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
			getList();
		} else {
			mPullToRefreshGridView_pro.onRefreshComplete();
			if (pageNo > 1) {
				Toast_Util.showToastOnlyOne(mContext, mContext.getResources()
						.getString(R.string.load_no_more_data));
			}
		}
	}

	private void getList() {
		showCustomCircleProgressDialog(
				null,
				mContext.getResources().getString(
						R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJson = new HashMap<String, String>();
		// ID 广告ID ANS 64 M 广告ID
		// latitude 纬度 ANS 64 M 用户纬度
		// longitude 经度 ANS 64 M 用户经度
		// pageStart 当前页数 N 8 O 默认值为：1
		// 当前第一页
		// pageOffset 获取数据条数 N 8 O 默认值为：10
		// 每页显示10条
		// orderBy 排序字段 ANS 64 M 排序类型 1.距离最近
		// 2.价格最低
		// 3.评价最高
		// 4.订单最多

		String lng = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LNG", "");
		String lat = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LAT", "");
		inJson.put("longitude", lng);
		inJson.put("latitude", lat);

		inJson.put("ID", ID);
		inJson.put("orderBy", orderBy);
		inJson.put("pageStart", pageNo + "");
		inJson.put("pageOffset", pageSize + "");
		getAD_Detail_List = new GetAD_Detail_List(mContext, ad_Handler, inJson);
		Thread thread = new Thread(getAD_Detail_List);
		thread.start();
	}

	class AD_Handler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();

				// 获取返回数据的页数
				JSONObject jsonObject = getAD_Detail_List.getOutJson();
				// 获取返回数据的页数
				if (null != jsonObject) {

					String totalPages = jsonObject.optString("pageCount", "0");
					String totalCounts = jsonObject
							.optString("tupleCount", "0");
					CommonUtil.log("广告项目总共页数：totalPages：" + totalPages);
					CommonUtil.log("广告项目总条数：totalCounts：" + totalCounts);
					if (!TextUtils.isEmpty(totalPages)) {
						pageCount = Integer.parseInt(totalPages);
					}

					if (!TextUtils.isEmpty(totalCounts)) {
						totalCount = Integer.parseInt(totalCounts);

					}
				}
				arraylist = getAD_Detail_List.getServiceList();
				if (pageNo == 1) {
					adapter_service.clear();
				}
				adapter_service.add(arraylist, "");
				rl_loadData_error.setVisibility(View.GONE);
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
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
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

			mPullToRefreshGridView_pro.onRefreshComplete();
		}
	}

	private void changeTabAndLine(int flag) {
		tv_tab_near.setTextColor(getResources().getColor(R.color.c5));
		tv_tab_minPrice.setTextColor(getResources().getColor(R.color.c5));
		tv_tab_highAppraise.setTextColor(getResources().getColor(R.color.c5));
		tv_tab_orderMore.setTextColor(getResources().getColor(R.color.c5));

		view_tab_near.setVisibility(View.INVISIBLE);
		view_tab_minPrice.setVisibility(View.INVISIBLE);
		view_tab_highAppraise.setVisibility(View.INVISIBLE);
		view_tab_orderMore.setVisibility(View.INVISIBLE);

		switch (flag) {
		case 1:// 1.距离最近
			tv_tab_near.setTextColor(getResources().getColor(R.color.c1));
			view_tab_near.setVisibility(View.VISIBLE);
			orderBy = "1";
			break;
		case 2:// 2.价格最低
			tv_tab_minPrice.setTextColor(getResources().getColor(R.color.c1));
			view_tab_minPrice.setVisibility(View.VISIBLE);
			orderBy = "2";
			break;
		case 3:// 3.评价最高
			tv_tab_highAppraise.setTextColor(getResources()
					.getColor(R.color.c1));
			view_tab_highAppraise.setVisibility(View.VISIBLE);
			orderBy = "3";
			break;
		case 4:// 4.订单最多
			tv_tab_orderMore.setTextColor(getResources().getColor(R.color.c1));
			view_tab_orderMore.setVisibility(View.VISIBLE);
			orderBy = "4";
			break;

		case -1:// 没有选

			break;
		}
		firstLoadingData();
	}

	/**
	 * 加载第一页数据
	 */
	private void firstLoadingData() {
		pageNo = 1;
		adapter_service.clear();
		adapter_service.notifyDataSetChanged();
		getList();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_tab_near:// 离我最近
			changeTabAndLine(1);
			break;
		case R.id.rl_tab_minPrice:// 价格最低
			changeTabAndLine(2);

			break;
		case R.id.rl_tab_highAppraise:// 评价最高
			changeTabAndLine(3);
			break;
		case R.id.rl_tab_orderMore:// 订单最多
			changeTabAndLine(4);
			break;

		case R.id.rl_loadData_error:// 重新加载数据
			firstLoadingData();
			break;
		case R.id.ll_back1:// 处理返回键
			handleOnBackPress();
			break;
		}

	}
	/**
	 * 处理返回键
	 */
	private void handleOnBackPress(){
		boolean has_open_app = CommonUtil.getBooleanOfSharedPreferences(
				this, "HAS_OPEN_APP", false);
		CommonUtil.logE("是否已经打开app：has_open_app：" + has_open_app);
		// 点击通知后或者首图--app未打开状态 -返回时回到主页面home
		if (push && !has_open_app) {
			CommonUtil.saveBooleanOfSharedPreferences(this, "HAS_OPEN_APP",true);
			JumpTargetActivityUtil.getInstance().jumpToHomeActivity(this, 0);
		} else {
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		handleOnBackPress();
	}

}
