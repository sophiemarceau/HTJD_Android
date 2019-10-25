package com.huatuo.activity.order;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.FlowLayout;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.EvaluateOrder;
import com.huatuo.net.thread.GetBedCommentRemarkList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Toast_Util;

public class DianPingActivity extends BaseActivity implements OnClickListener{
	public Context mContext;
	public Handler mHandler1, mHandler2;
	private LinearLayout ll_back;
	private TextView tv_zishu;
	private EditText et_content;
	private Button bt_pinglun;
	private RatingBar ratingBar1, ratingBar2, ratingBar3;
	private RelativeLayout rl_store, rl_project, rl_tech;
	private EvaluateOrder evaluateOrder;
	private GetBedCommentRemarkList getBedCommentRemarkList;
	private Handler mHandler_tag;
	private ArrayList<JSONObject> tagList;
	private String skillWorkerID, orderID, skillScore = "5", projectScore = "5", storeScore = "5", remark, tags = "";
	private String xingming, xingbie, juli, danshu, jianjie, dengji, pingfen, pinglunshu, headIconUrl;
	private int size;
	private ArrayList<HashMap<String, String>> remarks;
	private ArrayList<String> IDs;
	private HashMap<Integer, Boolean> map;
	private FlowLayout fl_tab_list;
	private String ID = "";
	private String type = "0";
	private String kind = "0";
	private String tagID = "0";
	private String orderClass = "";
	private RelativeLayout rl_loadData_error, rl_loadData_empty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_dianping2);
		mContext = DianPingActivity.this;
		mHandler1 = new evaluateOrder_Handler();
		mHandler2 = new getBedCommentRemarkList_Handler();
		orderID = getIntent().getStringExtra("ID");
		orderClass = getIntent().getStringExtra("orderClass");
		if (!orderClass.isEmpty()) {
			if ("1".equals(orderClass)) {
				type = "1";// 到店
			} else {
				type = "2";// 上门
			}
		}
		map = new HashMap<Integer, Boolean>();
		IDs = new ArrayList<String>();
		remarks = new ArrayList<HashMap<String, String>>();
		initWidget();
		getBedCommentRemarkList();
	}

	private void initWidget() {
		rl_store = (RelativeLayout) findViewById(R.id.rl_store);
		rl_project = (RelativeLayout) findViewById(R.id.rl_project);
		rl_tech = (RelativeLayout) findViewById(R.id.rl_tech);
		fl_tab_list = (FlowLayout) findViewById(R.id.fl_tab_list);
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		tv_zishu = (TextView) findViewById(R.id.tv_zishu);
		et_content = (EditText) findViewById(R.id.et_content);
		bt_pinglun = (Button) findViewById(R.id.bt_pinglun);
		ratingBar1 = (RatingBar) findViewById(R.id.ratingBar1);
		ratingBar2 = (RatingBar) findViewById(R.id.ratingBar2);
		ratingBar3 = (RatingBar) findViewById(R.id.ratingBar3);

		if ("1".equals(type)) {// 到店
			rl_store.setVisibility(View.VISIBLE);
			rl_project.setVisibility(View.VISIBLE);
			rl_tech.setVisibility(View.VISIBLE);
		} else if ("2".equals(type)) {// 上门
			rl_store.setVisibility(View.GONE);
			rl_project.setVisibility(View.VISIBLE);
			rl_tech.setVisibility(View.VISIBLE);
		}

		ratingBar1.setRating(Float.parseFloat(storeScore));
		ratingBar1.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				// doing actions
				storeScore = (int) rating + "";
			}
		});
		ratingBar2.setRating(Float.parseFloat(projectScore));
		ratingBar2.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				// doing actions
				projectScore = (int) rating + "";
			}
		});
		ratingBar3.setRating(Float.parseFloat(skillScore));
		ratingBar3.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
				// doing actions
				skillScore = (int) rating + "";
			}
		});

		et_content.addTextChangedListener(new TextWatcher() {
			private CharSequence temp = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				size = 500 - temp.length();
				if (size >= 0) {
					tv_zishu.setText("还可以输入" + size + "个汉字");
				}
			}
		});

		bindListener1();
		// 加载数据失败页面
				rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
				rl_loadData_error.setOnClickListener(this);
				// 加载数据前的白界面
				rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);
	}

	private void bindListener1() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		ll_back.setOnClickListener(myOnClickListener);
		bt_pinglun.setOnClickListener(myOnClickListener);
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
				fuwuName.setPadding(CommonUtil.dip2px(mContext, 8), CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 8),
						CommonUtil.dip2px(mContext, 5));
				LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				layoutParams.setMargins(CommonUtil.dip2px(mContext, 6), CommonUtil.dip2px(mContext, 6), CommonUtil.dip2px(mContext, 6),
						CommonUtil.dip2px(mContext, 6));

				fuwuName.setLayoutParams(layoutParams);
				fuwuName.setGravity(Gravity.CENTER);
				fuwuName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15); // 15sp
				fuwuName.setTextColor(getResources().getColor(R.color.c6));
				fuwuName.setBackground(mContext.getResources().getDrawable(R.drawable.custom_appraise_write_bg_unselected));
				JSONObject jsonObject = tagList.get(i);
				if (null != jsonObject) {
					// tabId = jsonObject.optString("ID","");
					String tagName = jsonObject.optString("tagName", "");
					// tagType = jsonObject.optString("type","");
					// ID 标签ID ANS 64 M 标签ID
					// name 标签内容 ANS 64 M 标签内容
					// type 类型 ANS 64 M 类型 0：通用 ,1:到店，2：上门
					fuwuName.setText(tagName);
					map.put(i, false);
					// fuwuName.setTag(map);
					fuwuName.setOnClickListener(new TabOnClickListener());
					fl_tab_list.addView(fuwuName);
				}
				// Log.e("", "map----------------->" + map);
			}
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
			String ID = tagList.get(i).optString("ID", "");
			if (checkedId == tv_id) {
				if (map.get(i)) {
					map.put(i, false);
					tv.setBackground(mContext.getResources().getDrawable(R.drawable.custom_appraise_write_bg_unselected));
					tv.setTextColor(getResources().getColor(R.color.c6));
					tv.setTextSize(15);
				} else {
					map.put(i, true);
					tv.setBackground(mContext.getResources().getDrawable(R.drawable.custom_appraise_write_bg_selected));
					tv.setTextColor(getResources().getColor(R.color.white));
					tv.setTextSize(15);
				}
			}
		}
	}

	private class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.ll_back1:
				finish();
				break;
			case R.id.bt_pinglun:
				StringBuffer sb = new StringBuffer("");
				remark = et_content.getText().toString();
				for (int i = 0; i < map.size(); i++) {
					Boolean b = map.get(i);
					if (b) {
						String tag = tagList.get(i).optString("ID", "");
						sb.append(tag + ",");
					}
				}
				tags = sb.toString();
				if (!TextUtils.isEmpty(tags)) {
					tags = tags.substring(0, tags.length() - 1);
				}
				// Log.e("", "orderID------------->" + orderID);
				// Log.e("", "skillScore------------->" + skillScore);
				// Log.e("", "projectScore------------->" + projectScore);
				// Log.e("", "storeScore------------->" + storeScore);
				// Log.e("", "remark------------->" + remark);
				// Log.e("", "tags------------->" + tags);
				evaluateOrder(orderID, skillScore, projectScore, storeScore, remark, tags);
				break;
			default:
				break;
			}
		}
	}

	private void evaluateOrder(String orderID, String skillScore, String projectScore, String storeScore, String remark, String tags) {
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		evaluateOrder = new EvaluateOrder(mContext, mHandler1, orderID, skillScore, projectScore, storeScore, remark, tags);
		Thread thread = new Thread(evaluateOrder);
		thread.start();
	}

	private void getBedCommentRemarkList() {
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		getBedCommentRemarkList = new GetBedCommentRemarkList(mContext, mHandler2, type);
		Thread thread = new Thread(getBedCommentRemarkList);
		thread.start();
	}

	class evaluateOrder_Handler extends Handler {
		String OutMsg;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				Toast_Util.showToast(mContext, "评论成功");
				finish();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				OutMsg = evaluateOrder.getOutMsg();
				DialogUtils.showToastMsg(mContext, OutMsg, Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	class getBedCommentRemarkList_Handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				tagList = getBedCommentRemarkList.getBedCommentRemarkList();
				handleTagList();
				break;
			case MsgId.DOWN_DATA_F:
				rl_loadData_error.setVisibility(View.VISIBLE);
				closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, getString(R.string.common_toast_net_down_data_fail), Toast.LENGTH_SHORT);
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
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_loadData_error:// 重新加载数据
			getBedCommentRemarkList();
			break;
		}

	}
}
