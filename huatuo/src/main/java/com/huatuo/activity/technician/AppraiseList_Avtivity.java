package com.huatuo.activity.technician;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huatuo.R;
import com.huatuo.adapter.SkillEvaluateListViewAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.FlowLayout;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetSkillEvaluateList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Menu_Search_pop;
import com.huatuo.util.Toast_Util;

public class AppraiseList_Avtivity extends BaseActivity implements OnClickListener {
	public Context mContext;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView lv_pinglun;
	private ArrayList<JSONObject> tagList;
	public Handler mHandler_apprList;
	private GetSkillEvaluateList getSkillEvaluateList;
	private SkillEvaluateListViewAdapter skillEvaluateListViewAdapter;

	private String ID = "";
	private String type = "";
	private String tagID = "";

	private int pageNo = 1;// 第几页
	private String pageSize = Constants.PAGE_SIZE_LIST;
	private int pageCount;// 总共页数
	private RelativeLayout rl_fl_tab_list;
	private FlowLayout fl_tab_list;
	private boolean isFirst = true;// 只有第一次或下拉刷新加载taglist
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_technician_pinglun_visit);
		mContext = AppraiseList_Avtivity.this;
		receiveData();
		initHandler();
		initWidget();
		setOnClickListener();
		// 获取选中tag的内容
		isFirst = true;
		getSkillEvaluateList();
	}

	private void receiveData() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			ID = bundle.getString("ID");
			type = bundle.getString("type");
		}
		CommonUtil.log("查询评价：ID:" + ID);
		CommonUtil.log("查询评价：type:" + type);
	}

	private void initHandler() {
		mHandler_apprList = new Handler_AppraiseList();
	}

	private void initWidget() {

		mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_pinglun);
		lv_pinglun = mPullToRefreshListView.getRefreshableView();
		skillEvaluateListViewAdapter = new SkillEvaluateListViewAdapter(
				mContext);
		lv_pinglun.setAdapter(skillEvaluateListViewAdapter);
		rl_fl_tab_list = (RelativeLayout) findViewById(R.id.rl_fl_tab_list);
		fl_tab_list = (FlowLayout) findViewById(R.id.fl_tab_list);
		fl_tab_list.setFocusable(true);
		fl_tab_list.setFocusableInTouchMode(true);
		fl_tab_list.requestFocus();
		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	private void setOnClickListener() {
		bindListener();
		setOnRefreshListener();

	}

	private void setOnRefreshListener() {
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

	public void refresh() {
		pageNo = 1;
		if (!CommonUtil.emptyListToString3(tagList) && isFirst == false) {
			getSkillEvaluateList();
		} else if (CommonUtil.emptyListToString3(tagList)) {
			tagID = "";
			getSkillEvaluateList();
		}

	}

	public void loadMore() {
		CommonUtil.log("pageNo:" + pageNo);
		CommonUtil.log("pageCount:" + pageCount);
		if (pageNo < pageCount) {
			++pageNo;
			getSkillEvaluateList();
		} else {
			mPullToRefreshListView.onRefreshComplete();
			if(pageNo > 1){
				 Toast_Util.showToastOnlyOne(mContext, mContext.getResources().getString(R.string.load_no_more_data));
				}
		}
	}

	/**
	 * 获取选中tab信息
	 * 
	 * @param index
	 */
	private void getSelectedTabInfo(int index) {
		for (int i = 0; i < tagList.size(); i++) {
			if (index == i) {
				JSONObject jsonObject = tagList.get(index);
				tagID = jsonObject.optString("ID", "");
				// tabType = jsonObject.optString("type", "");
			}
		}

		pageNo = 1;
		skillEvaluateListViewAdapter.clear();
		skillEvaluateListViewAdapter.notifyDataSetChanged();
		// 获取选中tag的内容
		getSkillEvaluateList();
	}

	private void getSkillEvaluateList() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		// ID ID ANS 64 O ID(门店，项目，技师 id 用type区分)
		// type 类型 ANS 64 O 类型 0 是门店，1是 项目，是技师
		// kind 类别 ANS 64 O 0：全部，1：好评，2差评
		// tagID 标签ID ANS 64 O 标签ID
		// pageStart 当前页数 N 8 O 默认值为：1
		// 当前第一页
		// pageOffset 获取数据条数 N 8 O 默认值为：10
		// 每页显示10条
		HashMap<String, String> inJsonMap = new HashMap<String, String>();
		inJsonMap.put("ID", ID);
		inJsonMap.put("type", type);
		inJsonMap.put("tagID", tagID);
		inJsonMap.put("pageStart", pageNo + "");
		inJsonMap.put("pageOffset", pageSize + "");

		getSkillEvaluateList = new GetSkillEvaluateList(mContext,
				mHandler_apprList, inJsonMap);
		Thread thread = new Thread(getSkillEvaluateList);
		thread.start();
	}

	class Handler_AppraiseList extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// 若标签不存在，就要处理标签---此处判断用于第一次加载
				if (isFirst) {
					tagList = getSkillEvaluateList.getTagList();
					CommonUtil.log("tagList:" + tagList);
					if (!CommonUtil.emptyListToString3(tagList)) {
						isFirst = false;// 当标签不为空时，才设置为false
						rl_fl_tab_list.setVisibility(View.VISIBLE);
						handleTagList();
					} else {
						rl_fl_tab_list.setVisibility(View.GONE);
					}
				}
				// 获取标签内容
				JSONObject appraiseObj = getSkillEvaluateList
						.getOutJsonObject();
				CommonUtil.log("appraiseObj:" + appraiseObj);

				if (null != appraiseObj) {
					String totalPages = appraiseObj.optString("pageCount", "0");
					CommonUtil.log("评价列表总共页数：totalPages：" + totalPages);
					if (!TextUtils.isEmpty(totalPages)) {
						pageCount = Integer.parseInt(totalPages);

					}
				}

				ArrayList<JSONObject> skillEvaluateList = getSkillEvaluateList
						.getSkillEvaluateList();

					if (pageNo == 1) {

						skillEvaluateListViewAdapter.clear();
					}

					skillEvaluateListViewAdapter.add(skillEvaluateList);

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
				DialogUtils.showToastMsg(mContext,
						getString(R.string.common_toast_net_down_data_fail),
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
			default:
				break;
			}
			mPullToRefreshListView.onRefreshComplete();
		}
	}

	private void handleTagList() {

		fl_tab_list.removeAllViews();
		if (!CommonUtil.emptyListToString3(tagList)) {
			for (int i = 0; i < tagList.size(); i++) {

				// navigationList 分类导航列表 JsonArray M 分类导航列表
				// ID 分类导航ID ANS 64 M 分类导航ID(默认展示标签)
				// name 分类导航名称 ANS 128 M 分类导航名称(默认展示标签)
				// type 标签类型 ANS 4 M 0:普通导航,1:附近,2: 收藏(默认展示标签)

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

				fuwuName.setTextColor(getResources().getColor(
						R.color.appraise_tag_unSelected_color));
				fuwuName.setBackground(mContext.getResources().getDrawable(
						R.drawable.custom_appraise_bg_unselected));
				JSONObject jsonObject = tagList.get(i);
				if (null != jsonObject) {
					String tagName = jsonObject.optString("tagName", "");
					String totalCount = jsonObject.optString("totalCount", "0");

					// tagType = jsonObject.optString("type","");
					// ID 标签ID ANS 64 M 标签ID
					// name 标签内容 ANS 64 M 标签内容
					// type 类型 ANS 64 M 类型 0：通用 ,1:到店，2：上门
					fuwuName.setText(tagName + " " + totalCount);

					fuwuName.setOnClickListener(new TabOnClickListener());
					if (!("0").equals(totalCount.trim())) {
						fl_tab_list.addView(fuwuName);
					}

				}
			}

			// 默认选择第一个
			// changeTab(0);

		}

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
				getSelectedTabInfo(checkedId);
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
					R.drawable.custom_appraise_bg_selected));
			// textView.setBackgroundResource(R.drawable.shopdetail_tab_bg_sel);
			textView.setTextColor(getResources().getColor(R.color.white));
		} else {
			textView.setBackground(mContext.getResources().getDrawable(
					R.drawable.custom_appraise_bg_unselected));
			// textView.setBackgroundResource(R.drawable.shopdetail_tab_bg);
			textView.setTextColor(getResources().getColor(
					R.color.appraise_tag_unSelected_color));
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
