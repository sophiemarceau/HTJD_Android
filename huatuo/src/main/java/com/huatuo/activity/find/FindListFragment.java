package com.huatuo.activity.find;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.huatuo.R;
import com.huatuo.adapter.Find_ListViewAdapter;
import com.huatuo.base.BaseFragment;
import com.huatuo.bean.FindBean;
import com.huatuo.bean.FindItemBean;
import com.huatuo.dictionary.MsgId;
import com.huatuo.dictionary.RequestUrl;
import com.huatuo.net.http.RequestData;
import com.huatuo.net.http.RequestRunnable;
import com.huatuo.net.thread.GetFindList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.Toast_Util;

public class FindListFragment extends BaseFragment implements OnClickListener {
	private Context mContext;
	private View v;

	// 获取发现列表
	private PullToRefreshListView mRefreshListView;
	private ListView lv_find_list;
	// private GetFindList getFindList;
	private RequestRunnable getFindList;
	private Handler find_Handler;
	private Find_ListViewAdapter find_ListViewAdapter;
//	private ArrayList<JSONObject> findList;
	private ArrayList<FindItemBean> findList;

	private int pageNo = 1;// 第几页
	private int pageCount;// 总共页数
	private int totalCount;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.activity_find_list, container, false);
		mContext = getActivity();
		findViewById();
		initHandler();
		getFindList();
		return v;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if (!hidden) {
			getFindList();
		}
	}

	private void initHandler() {
		find_Handler = new Find_Handler();
	}

	private void findViewById() {

		mRefreshListView = (PullToRefreshListView) v
				.findViewById(R.id.lv_find_list);
		lv_find_list = mRefreshListView.getRefreshableView();
		find_ListViewAdapter = new Find_ListViewAdapter(getActivity());
		lv_find_list.setAdapter(find_ListViewAdapter);
		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) v
				.findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) v
				.findViewById(R.id.rl_loadData_empty);
		OnRefreshListener();
	}

	private void OnRefreshListener() {
		mRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						refresh();
					}
				});

		mRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
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
		getFindList();
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
			getFindList();
		} else {
			mRefreshListView.onRefreshComplete();
			if (pageNo > 1) {
				Toast_Util.showToastOnlyOne(mContext, mContext.getResources()
						.getString(R.string.load_no_more_data));
			}
		}

	}

	private void getFindList() {
		showCustomCircleProgressDialog(null,
				getString(R.string.common_toast_net_prompt_submit));
		HashMap<String, String> inJsonObject = new HashMap<String, String>();
		// cityCode 城市编码 ANS 64 O 城市编码
		// pageStart 当前页数 N 8 O 默认值为：1
		// 当前第一页
		// pageOffset 获取数据条数 N 8 O 默认值为：10
		// 每页显示10条
		String citycode = CommonUtil.getStringOfSharedPreferences(
				mContext.getApplicationContext(), "NOW_CITYCODE", "");
		inJsonObject.put("cityCode", citycode);
		inJsonObject.put("pageStart", pageNo + "");
		inJsonObject.put("pageOffset", Constants.PAGE_SIZE_LIST);
		
		getFindList = RequestData.getInstance().request(mContext, find_Handler,
				RequestUrl.GET_DISCOVERLIST, inJsonObject);
	}

	class Find_Handler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				// 获取返回数据的页数
				JSONObject jsonObject = getFindList.getOutJson();
				
				// 获取返回数据的页数
				if (null != jsonObject) {
					FindBean findBean = JSON.parseObject(jsonObject.toString(), FindBean.class);
					pageCount = findBean.pageCount;
					CommonUtil.log("发现列表总共页数：pageCount：" + String.valueOf(pageCount));
					findList = findBean.discoverList;
					if (pageNo == 1) {
						find_ListViewAdapter.clear();
					}

					find_ListViewAdapter.add(findList);

					break;

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

			mRefreshListView.onRefreshComplete();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_loadData_error:// 重新加载数据
			refresh();
			break;
		default:
			break;
		}

	}

}
