package com.huatuo.activity.order;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
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
import com.huatuo.adapter.OrderListAdapter;
import com.huatuo.base.BaseFragment;
import com.huatuo.base.MyApplication;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.GetOrderList;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Toast_Util;
import com.huatuo.util.UmengEventUtil;

public class FragmentMyOrder extends BaseFragment implements OnClickListener {
	private Context mContext;
	private Handler mGetOrderListHandler;
	private View rootLayout;

	private LinearLayout ll_back;
	private TextView tv_daifukuan, tv_daifuwu, tv_daipinglun, tv_quanbu, tv_wudingdan, tv_miaosha;
	private View v_daifukuan, v_daifuwu, v_daipinglun, v_quanbu;
	private ImageView iv_wudingdan;
	private PullToRefreshListView lv_order;// 1待支付,2待服务,3待评论,4已完成
	private OrderListAdapter orderListAdapter;
	private GetOrderList getOrderList;
	private String orderID;
	private String orderStatus = Constants.TAB_ORDER_STATUS_WAITING_PAY;//
	private ArrayList<JSONObject> orderList;
	private int index = -100;
	public int orderStatusTag = 0;// 记录上方导航条状态
	private int pageNo = 1;// 第几页
	private int pageOffset = 15;// 每页默认15条数据
	private int pageCount;// 总共页数
	private int totalCount;
	private boolean isBottom = true;
	private boolean isShowLoading = true;
	private IntentFilter dynamic_filter_refreshList = null;

	private BroadcastReceiver dynamicReceiver;
	private boolean isOnCreate = true;
	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootLayout = inflater.inflate(R.layout.fragment_my_order, container, false);
		mContext = getActivity();
		initWidget();
		mGetOrderListHandler = new GetOrderListHandler();
		bindListener();
		broadcastReceiver();
		registeBoardCast();
		// 获取传过来的参数
		getOrderStatusFromBundle();
		if (MyApplication.getLoginFlag()) {
			changeSelectedTab(orderStatus);
		}

		isOnCreate = false;
		return rootLayout;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		CommonUtil.logE("--------------hidden-----------------" + hidden);
		CommonUtil.logE("------hidden--------isOnCreate---------------" + isOnCreate);
		if (!hidden) {
			if (!isOnCreate) {
				// 处理订单详情返回到此处的操作 --根据订单状态来选择tab和查询对应的订单列表
				getOrderStatusFromBundle();
			} else {
				isOnCreate = false;// 是否走onCreate，走过后
									// 下次切换该tab，直接走onHiddenChanged方法
			}

			CommonUtil.logE("--------fragmentOrder------orderStatus---------------" + orderStatus);
			CommonUtil.logE("--------fragmentOrder------isOnCreate---------------" + isOnCreate);
			if (MyApplication.getLoginFlag()) {
				changeSelectedTab(orderStatus);
			}
		}
	}

	/**
	 * 从传过来的参数 中获取对应标签的状态
	 */
	private void getOrderStatusFromBundle() {
		Bundle bundle = getActivity().getIntent().getExtras();
		CommonUtil.logE("--------fragmentOrder------bundle---------------" + bundle);
		if (null != bundle) {
			String status = bundle.getString(Constants.TAB_ORDER_STATUS, "");

			CommonUtil.logE("--------fragmentOrder---null != bundle---status---------------" + status);
			if (!TextUtils.isEmpty(status)) {
				int status_int = (int) Float.parseFloat(status);
				getOrderStatus(status_int);
			}

		}
	}

	/**
	 * 根据订单状态 设置 tab选中状态
	 * 
	 * @param status_int
	 */
	private void getOrderStatus(int status_int) {
		switch (status_int) {
		case 1:
			orderStatus = Constants.TAB_ORDER_STATUS_WAITING_PAY;
			break;
		case 2:
		case 3:
		case 4:
			orderStatus = Constants.TAB_ORDER_STATUS_WAITING_SERVICE;
			break;
		case 5:// 待评价
			orderStatus = Constants.TAB_ORDER_STATUS_WAITING_APPRAISE;
			break;
		case 6:
		case -1:
		case -2:
		case -3:
			orderStatus = Constants.TAB_ORDER_STATUS_ALL;
		}
	}

	private void broadcastReceiver() {
		dynamicReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context arg0, Intent intent) {
				if (null != intent) {
					// 刷新订单列表
					if (intent.getAction().equals(Constants.REFRESH_ORDERLIST)) {
						getOrderList(orderStatus);
					}
				}
			}
		};
	}

	private void registeBoardCast() {
		// 刷新列表
		dynamic_filter_refreshList = new IntentFilter();
		dynamic_filter_refreshList.addAction(Constants.REFRESH_ORDERLIST);
		mContext.registerReceiver(dynamicReceiver, dynamic_filter_refreshList);
	}

	private void initWidget() {
		ll_back = (LinearLayout) rootLayout.findViewById(R.id.ll_back1);

		tv_daifukuan = (TextView) rootLayout.findViewById(R.id.tv_daifukuan);
		tv_daifuwu = (TextView) rootLayout.findViewById(R.id.tv_daifuwu);
		tv_daipinglun = (TextView) rootLayout.findViewById(R.id.tv_daipinglun);
		tv_quanbu = (TextView) rootLayout.findViewById(R.id.tv_quanbu);
		tv_wudingdan = (TextView) rootLayout.findViewById(R.id.tv_wudingdan);
		tv_miaosha = (TextView) rootLayout.findViewById(R.id.tv_miaosha);
		iv_wudingdan = (ImageView) rootLayout.findViewById(R.id.iv_wudingdan);

		v_daifukuan = (View) rootLayout.findViewById(R.id.v_daifukuan);
		v_daifuwu = (View) rootLayout.findViewById(R.id.v_daifuwu);
		v_daipinglun = (View) rootLayout.findViewById(R.id.v_daipinglun);
		v_quanbu = (View) rootLayout.findViewById(R.id.v_quanbu);

		lv_order = (PullToRefreshListView) rootLayout.findViewById(R.id.lv_order);
		ListView actualListView = lv_order.getRefreshableView();
		orderListAdapter = new OrderListAdapter(mContext);

		actualListView.setAdapter(orderListAdapter);
		v_daifukuan.setVisibility(View.VISIBLE);
		v_daifuwu.setVisibility(View.INVISIBLE);
		v_daipinglun.setVisibility(View.INVISIBLE);
		v_quanbu.setVisibility(View.INVISIBLE);
		lv_order.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				refresh();
			}
		});

		lv_order.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

			@Override
			public void onLastItemVisible() {
				// TODO Auto-generated method stub
				loadMore();
			}
		});
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				JSONObject jsonObject = (JSONObject) parent.getAdapter().getItem(position);
				String ID = jsonObject.optString("ID", "");
				
				UmengEventUtil.order_orderdetail(mContext);	
				
				Intent intent = new Intent(mContext, OrderDetailActivity.class);
				intent.putExtra("orderID", ID);
				intent.putExtra("orderFrom", Constants.NOPAY);
				startActivity(intent);
			}
		});

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) rootLayout.findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) rootLayout.findViewById(R.id.rl_loadData_empty);

	}

	private void changeSelectedTab(String orderStatus) {
		v_daifukuan.setVisibility(View.INVISIBLE);
		v_daifuwu.setVisibility(View.INVISIBLE);
		v_daipinglun.setVisibility(View.INVISIBLE);
		v_quanbu.setVisibility(View.INVISIBLE);
		lv_order.setVisibility(View.GONE);
		tv_daifukuan.setTextColor(getActivity().getResources().getColor(R.color.c5));
		tv_daifuwu.setTextColor(getActivity().getResources().getColor(R.color.c5));
		tv_daipinglun.setTextColor(getActivity().getResources().getColor(R.color.c5));
		tv_quanbu.setTextColor(getActivity().getResources().getColor(R.color.c5));
		if (Constants.TAB_ORDER_STATUS_WAITING_PAY.equals(orderStatus)) {
			v_daifukuan.setVisibility(View.VISIBLE);
			tv_daifukuan.setTextColor(getActivity().getResources().getColor(R.color.c1));
		} else if (Constants.TAB_ORDER_STATUS_WAITING_SERVICE.equals(orderStatus)) {
			v_daifuwu.setVisibility(View.VISIBLE);
			tv_daifuwu.setTextColor(getActivity().getResources().getColor(R.color.c1));
		} else if (Constants.TAB_ORDER_STATUS_WAITING_APPRAISE.equals(orderStatus)) {
			v_daipinglun.setVisibility(View.VISIBLE);
			tv_daipinglun.setTextColor(getActivity().getResources().getColor(R.color.c1));
		} else if (Constants.TAB_ORDER_STATUS_ALL.equals(orderStatus)) {
			v_quanbu.setVisibility(View.VISIBLE);
			tv_quanbu.setTextColor(getActivity().getResources().getColor(R.color.c1));
		}
		pageNo = 1;
		orderListAdapter.clear();
		getOrderList(orderStatus);
	}

	private void bindListener() {
		tv_daifukuan.setOnClickListener(new MyOnClickListener());
		tv_daifuwu.setOnClickListener(new MyOnClickListener());
		tv_daipinglun.setOnClickListener(new MyOnClickListener());
		tv_quanbu.setOnClickListener(new MyOnClickListener());
		tv_miaosha.setOnClickListener(new MyOnClickListener());
	}

	public void refresh() {
		pageNo = 1;
		getOrderList(orderStatus);
	}

	public void loadMore() {
		if (pageNo < pageCount) {
			pageNo++;
			getOrderList(orderStatus);
		} else {
			lv_order.onRefreshComplete();
			if (pageNo > 1) {
				Toast_Util.showToastOnlyOne(mContext, mContext.getResources().getString(R.string.load_no_more_data));
			}
		}
	}

	class MyOnClickListener implements OnClickListener {

		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			switch (view.getId()) {
			case R.id.tv_daifukuan:
				UmengEventUtil.order_nopay(mContext);
				// Toast_Util.showToast(mContext, "tv_daifukuan");
				orderStatus = "1";
				changeSelectedTab(orderStatus);
				break;
			case R.id.tv_daifuwu:
				UmengEventUtil.order_waitserve(mContext);
				// Toast_Util.showToast(mContext, "tv_daifuwu");
				orderStatus = "2";
				changeSelectedTab(orderStatus);
				break;
			case R.id.tv_daipinglun:
				UmengEventUtil.order_waitcommet(mContext);
				// Toast_Util.showToast(mContext, "tv_daipinglun");
				orderStatus = "3";
				changeSelectedTab(orderStatus);
				break;
			case R.id.tv_quanbu:
				UmengEventUtil.order_all(mContext);
				// Toast_Util.showToast(mContext, "tv_quanbu");
				orderStatus = "4";
				changeSelectedTab(orderStatus);
				break;
			case R.id.tv_miaosha:
				Intent i = new Intent(getActivity(), OrderList_Flash_Buy_Activity.class);
				startActivity(i);
			default:
				break;
			}
		}
	}

	private void getOrderList(String orderStatus) {// 1待付款,2待服务,3待评论,4已完成
		showCustomCircleProgressDialog(null,
				mContext.getResources().getString(R.string.common_toast_net_prompt_submit));
		isShowLoading = false;
		getOrderList = new GetOrderList(mContext, mGetOrderListHandler, orderStatus, "1,2", pageNo + "",
				pageOffset + "");
		Thread thread = new Thread(getOrderList);
		thread.start();
	}

	class GetOrderListHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			rl_loadData_empty.setVisibility(View.GONE);
			rl_loadData_error.setVisibility(View.GONE);
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();

				orderList = getOrderList.getOrderList();
				// orderListAdapter.clear();
				// orderListAdapter.add(orderList);
				// 获取返回数据的页数
				JSONObject jsonObject = getOrderList.getOutJson();
				if (null != jsonObject) {

					String totalPages = jsonObject.optString("pageCount", "0");
					String totalCounts = jsonObject.optString("tupleCount", "0");
					CommonUtil.log("我的订单列表总共页数：totalPages：" + totalPages);
					CommonUtil.log("我的订单列表总共页数：totalCounts：" + totalCounts);
					if (!TextUtils.isEmpty(totalPages)) {
						pageCount = Integer.parseInt(totalPages);
					}
					if (!TextUtils.isEmpty(totalCounts)) {
						totalCount = Integer.parseInt(totalCounts);
					}
				}

				if (!CommonUtil.emptyListToString3(orderList)) {
					lv_order.setVisibility(View.VISIBLE);
					tv_wudingdan.setVisibility(View.GONE);
					iv_wudingdan.setVisibility(View.GONE);
					if (pageNo == 1) {
						orderListAdapter.clear();
					}
					orderListAdapter.add(orderList);
				} else {
					lv_order.setVisibility(View.GONE);
					tv_wudingdan.setVisibility(View.VISIBLE);
					iv_wudingdan.setVisibility(View.VISIBLE);
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
						mContext.getResources().getString(R.string.common_toast_net_down_data_fail),
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

			lv_order.onRefreshComplete();

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_loadData_error:// 重新加载数据
			pageNo = 1;
			orderListAdapter.clear();
			getOrderList(orderStatus);
			break;

		default:
			break;
		}

	}

}
