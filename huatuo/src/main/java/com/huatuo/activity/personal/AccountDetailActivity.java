package com.huatuo.activity.personal;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huatuo.R;
import com.huatuo.adapter.MingXiListViewAdapter;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetMingXiList;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.Toast_Util;

public class AccountDetailActivity extends BaseActivity implements OnClickListener{
	private Context mContext;
	private TextView tv_yue;
	private MingXiListViewAdapter mingXiListViewAdapter;
	private GetMingXiList getMingXiList;
	private MyHandler mHandler;
	private LinearLayout ll_back;
	private PullToRefreshListView mPullToRefreshView;
	private ListView lv_mingxi;
	private int pageNo = 1;// 第几页
	private int pageOffset = 10;// 每页默认10条数据
	private int pageCount;// 总共页数
	private int tupleCount;// 总记录数
	private boolean isRefresh = false;
	private RelativeLayout rl_loadData_error,rl_loadData_empty;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_account_detail);
		mContext = AccountDetailActivity.this;
		mHandler = new MyHandler();
		initWidget();
		bindListener();
	}

	private void initWidget() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		tv_yue = (TextView) findViewById(R.id.tv_yue);

		mPullToRefreshView = (PullToRefreshListView) findViewById(R.id.lv_mingxi);
		lv_mingxi = mPullToRefreshView.getRefreshableView();

		JSONObject u = MyApplication.getUserJSON();
		tv_yue.setText(NumFormatUtil.centFormatYuanToString(u.optString(
				"deposit", " ")) + "");

		mingXiListViewAdapter = new MingXiListViewAdapter(mContext);
		lv_mingxi.setAdapter(mingXiListViewAdapter);
		mPullToRefreshView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						refresh();
					}
				});

		mPullToRefreshView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						loadMore();
					}
				});
		getMingXiList();
		
		//加载数据失败页面
				rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
				rl_loadData_error.setOnClickListener(this);
				//加载数据前的白界面
				rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	public void refresh() {
		pageNo = 1;
		isRefresh = true;
		getMingXiList();
	}

	public void loadMore() {
		if (pageNo < pageCount) {
			++pageNo;
			isRefresh = false;
			getMingXiList();
		} else {
			mPullToRefreshView.onRefreshComplete();
			if(pageNo > 1){
				 Toast_Util.showToastOnlyOne(mContext, mContext.getResources().getString(R.string.load_no_more_data));
				}
		}
	}

	protected void bindListener() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		ll_back.setOnClickListener(myOnClickListener);
	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.ll_back1:
				finish();
				break;

			default:
				break;
			}
		}
	}

	/* 获取明细列表 */
	private void getMingXiList() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		getMingXiList = new GetMingXiList(mContext, mHandler, pageNo + "",
				pageOffset + "");
		Thread thread = new Thread(getMingXiList);
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
				JSONObject outObj = getMingXiList.getOutObj();
				if (null!=outObj) {
					pageCount = Integer.parseInt(outObj.optString("pageCount", ""));
					tupleCount = Integer.parseInt(outObj
							.optString("tupleCount", ""));
					ArrayList<JSONObject> mingXiList = getMingXiList
							.getMingXiList();
					if (pageNo == 1) {
						mingXiListViewAdapter.clear();
						mingXiListViewAdapter.add(mingXiList);
					} else {
						mingXiListViewAdapter.add(mingXiList);
					}
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
			mPullToRefreshView.onRefreshComplete();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_loadData_error://重新加载数据
			refresh();
			break;
		}

	}
}
