package com.huatuo.activity.appoint;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huatuo.R;
import com.huatuo.adapter.Select_Technician_ListViewAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetTechnicianListBySelect_Thread;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Toast_Util;

/**
 * @author Android开发工程师
 * 
 */
public class Select_ServiceTechnicianList_Activity extends BaseActivity
		implements OnClickListener {
	private Context mContext;
	public static Activity mActivity;
	private Button bt_appoint_select;
	// 获取技师列表
	private PullToRefreshListView mPullToRefreshListView;
	private ListView lv_list_tech;
	private RelativeLayout lv_noData;
	private TextView tv_noData;
	private Select_Technician_ListViewAdapter technician_ListViewAdapter;
	public Handler mHandler;
	private GetTechnicianListBySelect_Thread getTechnicianListBySelect_Thread1;
	private HashMap<String, String> postMap = null;
	private JSONObject selectedTechnicalObject = null;
	private ArrayList<JSONObject> systemMatch_technicianList = null;
	private int IsSelected = 1;// -1:系统匹配可以点击 ；1：服务过的可以点击
	public static final int RESULT_CODE_Select_ServiceTechnician = 2002;
	private Intent intent = new Intent();
	/*
	 * servID 服务项目ID city 城市; isService 1表示推荐 2 表示服务过的。空表示全部 address 用户填的地址 sex
	 * 用户的性别 1表示男，2表示女 dateTime 预约时间，格式为yyyy-MM-dd HH:mm beltCount 钟数 longitude
	 * 经度 latitude 纬度 userID 用户ID addressID 用户的地址ID
	 */
	private String servID, dateTime, beltCount, servType, servGradeID,
			addressID;
	private int appoint_view;

	private int pageNo = 1;// 第几页
	private String pageSize = Constants.PAGE_SIZE_LIST;
	private int pageCount;// 总共页数
	private int totalCount;
	private boolean isBottom = true;

	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_select_service_technicianlist_store);
		mContext = this;
		mActivity = this;
		mHandler = new MyHandler();
		initWidget();
		getDataFromAppointmentActivity();
		pageNo = 1;
		getTechList();

	}

	private void getDataFromAppointmentActivity() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			String jsonsString = bundle.getString("jsonObject");
			appoint_view = bundle.getInt(Constants.APPOINT_VIEW);
			if (!TextUtils.isEmpty(jsonsString)) {
				JSONObject receiveJsonObj;
				try {
					receiveJsonObj = new JSONObject(jsonsString);
					if (null != receiveJsonObj) {
						servID = receiveJsonObj.optString("servID", "");
						beltCount = receiveJsonObj.optString("beltCount", "");
						dateTime = receiveJsonObj.optString("dateTime", "");
						servGradeID = receiveJsonObj.optString("servGradeID",
								"");
						servType = receiveJsonObj.optString("serviceType", "");
						addressID = receiveJsonObj.optString("addressID", "");
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

	/** 在这里获取到每个需要用到的控件的实例 */
	private void initWidget() {
		lv_noData = (RelativeLayout) findViewById(R.id.collect_result_noData);
		tv_noData = (TextView) findViewById(R.id.tv_noData);
		tv_noData.setText("没有符合条件的技师");
		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_list_tech);
		lv_list_tech = mPullToRefreshListView.getRefreshableView();

		technician_ListViewAdapter = new Select_Technician_ListViewAdapter(this);
		lv_list_tech.setAdapter(technician_ListViewAdapter);

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

		bindListener();
		pullToRefresh();

	}

	private void pullToRefresh() {

		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						refresh();
					}
				});

		mPullToRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						loadMore();
					}
				});

	}

	private void refresh() {
		pageNo = 1;
		getTechList();
	}

	public void loadMore() {
		CommonUtil.log("pageNo:" + pageNo);
		CommonUtil.log("pageCount:" + pageCount);

		if (pageNo < pageCount) {
			++pageNo;
			getTechList();
		} else {
			mPullToRefreshListView.onRefreshComplete();
			if(pageNo > 1 && pageNo == pageCount){
				Toast_Util.showToastOnlyOne(mContext, mContext.getResources()
						.getString(R.string.load_no_more_data));
			}
		}
	}

	private void postParams() {
		postMap = new HashMap<String, String>();
		// // longitude 用户经度 ANS 64 O 用户经度
		// latitude 用户纬度 ANS 64 O 用户纬度
		// amount 钟数 ANS 64 M 订单的钟数
		// serviceTime 服务时间 ANS 64 M 服务时间 格式为yyyy-MM-dd HH:mm
		// serviceType 服务类型 N 2 M 1 到店， 2上门
		// serviceID 服务ID ANS 64 M 服务ID
		// gradeID 服务等级ID ANS 64 O 服务等级ID
		// addressID 地址ID ANS 64 C 上门时必填
		// pageStart 当前页数 N 8 O 默认值为：1
		// 当前第一页
		// pageOffset 获取数据条数 N 8 O 默认值为：10
		// 每页显示10条
		String citycode = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_CITYCODE", "");
		String lng = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LNG", "");
		String lat = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_LAT", "");
		postMap.put("longitude", lng);
		postMap.put("latitude", lat);
		postMap.put("serviceID", servID);
		postMap.put("amount", beltCount);
		postMap.put("serviceTime", dateTime);
		postMap.put("serviceType", servType);
		postMap.put("gradeID", servGradeID);
		postMap.put("addressID", addressID);
		postMap.put("pageStart", pageNo + "");
		postMap.put("pageOffset", pageSize);
		postMap.put("userID", MyApplication.getUserID());
	}

	/**
	 * 获取到店技师列表
	 */
	private void getTechList() {
		postParams();
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		getTechnicianListBySelect_Thread1 = new GetTechnicianListBySelect_Thread(
				mContext, mHandler, postMap);
		Thread thread = new Thread(getTechnicianListBySelect_Thread1);
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
				// 获取返回数据的页数
				JSONObject jsonObject = getTechnicianListBySelect_Thread1
						.getOutJsonObj();
				if (null != jsonObject) {
					// pageCount 总共页数 N 10 O
					// tupleCount 总记录数 N 10 O
					String totalPages = jsonObject.optString("pageCount", "0");
					String totalCounts = jsonObject
							.optString("tupleCount", "0");

					CommonUtil.log("search----visit------总共页数：totalPages："
							+ totalPages);
					CommonUtil.log("search----visit------总共条数：totalCounts："
							+ totalCounts);
					if (!TextUtils.isEmpty(totalPages)) {
						pageCount = Integer.parseInt(totalPages);
					}
					if (!TextUtils.isEmpty(totalCounts)) {
						totalCount = Integer.parseInt(totalCounts);

					}
				}

				// 技师列表
				systemMatch_technicianList = getTechnicianListBySelect_Thread1
						.getTechnicianList();

				if (!CommonUtil.emptyListToString3(systemMatch_technicianList)) {

					CommonUtil.log("//获取搜索技师pageNo:" + pageNo);
					if (pageNo == 1) {
						technician_ListViewAdapter.clear();
					}
					technician_ListViewAdapter.add(systemMatch_technicianList);
				} else {

					if (pageNo == 1) {
						systemMatch_technicianList.clear();
						technician_ListViewAdapter.clear();
						lv_noData.setVisibility(View.VISIBLE);
					}

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
				String MSG = getTechnicianListBySelect_Thread1.getOutMsg();
				int code = getTechnicianListBySelect_Thread1.getOutCode();
				CommonUtil.log("获取到店技师列表返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN
						|| code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils
							.showToastMsg(
									mContext,
									getString(R.string.common_toast_net_down_data_fail),
									Toast.LENGTH_SHORT);
				}
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_loadData_error:// 重新加载数据
			refresh();
			break;
		}

	}
}