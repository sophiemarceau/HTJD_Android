package com.huatuo.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.appoint.GoToAddTime;
import com.huatuo.activity.order.DianPingActivity;
import com.huatuo.activity.pay.Pay_Activity;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.DeleteOrder_thread;
import com.huatuo.util.CallUtil;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.CustomDialogProgressHandler;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.NumFormatUtil;
import com.huatuo.util.Toast_Util;
import com.nostra13.universalimageloader.core.ImageLoader;

public class OrderList_Flash_Buy_Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;
	private HashMap<Integer, String> map;
	private int orderState = 0;
	private Intent intent;
	private Bundle bundle;
	private Handler del_handler;
	private DeleteOrder_thread deleteOrder_thread;

	public OrderList_Flash_Buy_Adapter(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<JSONObject>();
		del_handler = new Handler_DelOrder();
	}

	public void add(JSONObject item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<JSONObject> arrayList) {
		if (!CommonUtil.emptyListToString3(arrayList)) {
			for (int i = 0; i < arrayList.size(); i++) {
				JSONObject item = arrayList.get(i);
				this.arrayList.add(item);

			}

			map = new HashMap<Integer, String>();
			for (int j = 0; j < arrayList.size(); j++) {
				JSONObject item = arrayList.get(j);
				map.put(j, item.optString("status"));
			}
		}

		CommonUtil.log("add-----------OrderList---------map" + map);

		notifyDataSetChanged();
	}

	public void clear() {
		this.arrayList.clear();
	}

	@Override
	public int getCount() {
		return arrayList.size();
	}

	@Override
	public Object getItem(int position) {
		return arrayList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.fragment_miaosha_order_list_view_item, null);
			holder = new ViewHolder();
			holder.ll_btn = (LinearLayout) convertView.findViewById(R.id.ll_btn);
			holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.iv_call = (ImageView) convertView.findViewById(R.id.iv_call);
			holder.tv_status = (TextView) convertView.findViewById(R.id.tv_status);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_price = (TextView) convertView.findViewById(R.id.tv_orderList_price);
			holder.tv_serviceItem = (TextView) convertView.findViewById(R.id.tv_serviceItem);
			holder.tv_serviceAddress = (TextView) convertView.findViewById(R.id.tv_serviceAddress);
			holder.btn_state = (Button) convertView.findViewById(R.id.btn_state);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!CommonUtil.emptyListToString3(arrayList)) {
			JSONObject jsonObject = arrayList.get(position);
			String ID = jsonObject.optString("ID", "");// 订单ID
			String serviceName = jsonObject.optString("serviceName", "");// 服务名称
			String orderTime = jsonObject.optString("orderTime", "");// 下单时间
			String serviceStartTime = jsonObject.optString("serviceStartTime", "");// 服务时间
			String serviceEndTime = jsonObject.optString("serviceEndTime", "");// 服务结束时间
			String payment = jsonObject.optString("payment", "");// 实际支付金额
			String status = jsonObject.optString("status", "");// 订单状态(1待支付（确认订单）,2已支付,3进行中（技师出发）,4服务中（开始服务）,5完成（未评论）,6已评论,-1取消,-2关闭,-3已退款)
			String storeID = jsonObject.optString("storeID", "");// 店铺ID
			String storeName = jsonObject.optString("storeName", "");// 店铺名称
			String storeAddress = jsonObject.optString("storeAddress", "");// 店铺地址
			String storeTel = jsonObject.optString("storeTel", "");// 店铺tel
			String workerID = jsonObject.optString("workerID", "");// 技师 ID
			String workerName = jsonObject.optString("workerName", "");// 技师姓名
			String workerIcon = jsonObject.optString("workerIcon", "");// 技师头像
			String serviceIcon = jsonObject.optString("serviceIcon", "");// 服务头像
			String storeIcon = jsonObject.optString("storeIcon", "");// 服务头像
			String type = jsonObject.optString("type", "");// 订单类型:“1”或“”为一般订单,“2”是闪付订单,“3”是秒杀订单
			String clientArea = jsonObject.optString("clientArea", "");
			String clientAddress = jsonObject.optString("clientAddress", "");// 客户地址
			String serviceType = jsonObject.optString("serviceType", "");// 服务订单类型:1到店，2到店上门，3华佗上门
			String isExtended = jsonObject.optString("isExtended", "");// 是否加过钟:0没有加过钟,1加过钟
			String isExtensible = jsonObject.optString("isExtensible", "");// 不可加钟:0可以加钟,1不可加钟

			holder.tv_serviceItem.setText(serviceName);
			ImageLoader.getInstance().displayImage(serviceIcon, holder.iv_icon,
					ImageLoader_DisplayImageOptions.getInstance().setDefaultImageSmallImg());// 服务图片icon

			holder.tv_name.setText(storeName);
			holder.tv_price.setText(NumFormatUtil.centFormatYuanToString(payment));

			holder.tv_serviceItem.setCompoundDrawablesWithIntrinsicBounds(null, null,
					mContext.getResources().getDrawable(R.drawable.icon_order_home), null);
			holder.tv_serviceAddress.setText(storeAddress);

			holder.tv_name.setOnClickListener(new StoreNameListener(position));

			if ("1".equals(status)) {// 1 待支付
				orderState = 1;
				holder.tv_status.setText("待支付");
				holder.ll_btn.setVisibility(View.VISIBLE);
				holder.btn_state.setText("我要付款");
				holder.btn_state.setBackgroundResource(R.drawable.icon_order_n);
				holder.btn_state.setTextColor(mContext.getResources().getColor(R.color.c5));
				holder.btn_state.setOnClickListener(new BtStateListener(orderState, position));
			}

			else if ("2".equals(status)) {// 2待服务
				orderState = 2;
				holder.tv_status.setText("待服务");
				holder.ll_btn.setVisibility(View.GONE);
			}

			else if ("5".equals(status)) { // 5 待评论
				orderState = 5;
				holder.tv_status.setText("待评论");
				holder.ll_btn.setVisibility(View.VISIBLE);
				holder.btn_state.setText("我要评论");
				holder.btn_state.setClickable(true);
				holder.btn_state.setBackgroundResource(R.drawable.icon_order_n);
				holder.btn_state.setTextColor(mContext.getResources().getColor(R.color.c5));
				holder.btn_state.setOnClickListener(new BtStateListener(orderState, position));
			}

			else if ("6".equals(status)) {// 6 已评论
				orderState = 6;
				holder.tv_status.setText("已完成");
				holder.ll_btn.setVisibility(View.VISIBLE);
				holder.btn_state.setText("删除订单");
				holder.btn_state.setClickable(true);
				holder.btn_state.setBackgroundResource(R.drawable.icon_order_n);
				holder.btn_state.setTextColor(mContext.getResources().getColor(R.color.c5));
				holder.btn_state.setOnClickListener(new BtStateListener(orderState, position));
			}
		}

		return convertView;
	}

	class ViewHolder {
		ImageView iv_icon, iv_call;
		Button btn_state;
		TextView tv_serviceItem, tv_name, tv_status, tv_serviceAddress;
		TextView tv_price;
		LinearLayout ll_btn;
	}

	class IvCallListener implements OnClickListener {
		private String storeTel;

		public IvCallListener(String storeTel) {
			this.storeTel = storeTel;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CallUtil.showCallDialog(mContext, storeTel);
		}

	}

	class StoreNameListener implements OnClickListener {
		private int position;

		public StoreNameListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (intent == null) {
				intent = new Intent();
			}
			if (bundle == null) {
				bundle = new Bundle();
			}
			JSONObject item_jsonObject = arrayList.get(position);
			String storeID = item_jsonObject.optString("storeID", "");
			intent.setClass(mContext, StoreDetail_Aty.class);
			bundle.putString("ID", storeID);
			intent.putExtras(bundle);
			mContext.startActivity(intent);
		}

	}

	class BtStateListener implements OnClickListener {
		private int orderState;
		private int position;

		public BtStateListener(int status, int position) {
			orderState = status;
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			if (intent == null) {
				intent = new Intent();
			}
			if (bundle == null) {
				bundle = new Bundle();
			}

			JSONObject item_jsonObject = arrayList.get(position);
			// Toast.makeText(mContext, item_jsonObject+"", 1).show();
			// Log.e("OrderListAdapter", "item_jsonObject------------------->" +
			// item_jsonObject);
			String orderID = item_jsonObject.optString("ID", "");
			String workerID = item_jsonObject.optString("workerID", "");
			String payment = item_jsonObject.optString("payment", "");
			String serviceIcon = item_jsonObject.optString("serviceIcon", "");
			String serviceName = item_jsonObject.optString("serviceName", "");
			String serviceType = item_jsonObject.optString("serviceType", "");
			CommonUtil.saveBooleanOfSharedPreferences(mContext, "ISSTORE", false);// 存储订单是否是到店的
			switch (orderState) {
			case 1:// 1 待支付（确认订单）---去支付
				CommonUtil.log("------------------------订单列表：orderID：" + orderID);
				CommonUtil.log("------------------------订单列表：serviceName：" + serviceName);
				CommonUtil.log("------------------------订单列表：payment：" + payment);

				intent.setClass(mContext, Pay_Activity.class);
				bundle.putString("orderID", orderID);
				bundle.putString("serviceName", serviceName);
				bundle.putString("payment", payment);
				bundle.putString("serviceIcon", serviceIcon);
				intent.putExtras(bundle);
				mContext.startActivity(intent);

				break;
			case 2:// 2 已支付，待服务 ---去加钟
			case 3:// 3 进行中（技师出发）---去加钟
			case 4:// 4 服务中（开始服务）---去加钟
				if ("1".equals(item_jsonObject.optString("isExtensible", ""))) {
					// Log.e("OrderListAdapter", "ID------------------->" +
					// orderID);
					GoToAddTime.getInstance().goToAddTime((Activity) mContext, orderID);
				} else {
					Toast_Util.showToast(mContext, mContext.getResources().getString(R.string.cannot_addTime));
				}
				break;
			case 5:// 5 完成（未评论）---去评论
				intent.setClass(mContext, DianPingActivity.class);
				intent.putExtra("ID", orderID);
				intent.putExtra("workerID", workerID);
				intent.putExtra("orderClass", serviceType);
				mContext.startActivity(intent);
				break;
			case 6:
			case -1:
			case -2:
			case -3:
				showCustomDialog(orderID);
				break;
			}
		}
	}

	/**
	 * 删除订单：自定义Dialog
	 */
	private void showCustomDialog(final String orderID) {

		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage("您正在进行删除订单操作，删除后无法查询订单，确定删除吗？");
		builder.setPositiveButton(mContext.getResources().getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						// 联网 删除订单
						deleteOrder(orderID);
					}
				});

		builder.setNegativeButton(mContext.getResources().getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();

	}

	private void deleteOrder(String orderID) {
		CustomDialogProgressHandler.getInstance().showCustomCircleProgressDialog(mContext, null,
				mContext.getResources().getString(R.string.common_toast_net_prompt_submit));
		HashMap<String, String> injsonMap = new HashMap<String, String>();
		injsonMap.put("userID", MyApplication.getUserID());
		injsonMap.put("orderID", orderID);
		deleteOrder_thread = new DeleteOrder_thread(mContext, del_handler, injsonMap);
		Thread thread = new Thread(deleteOrder_thread);
		thread.start();
	}

	class Handler_DelOrder extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				// int item_position = (Integer) msg.obj;
				CustomDialogProgressHandler.getInstance().closeCustomCircleProgressDialog();
				// if (CommonUtil.emptyListToString2(arrayList) != null) {
				// CommonUtil.log("删除完订单前：订单arrayList:" + arrayList);

				// del(item_position);
				// CommonUtil.log("删除完订单后：订单arrayList:" + arrayList);
				// }

				Toast_Util.showToast(mContext, "删除订单成功");
				intent = new Intent();
				intent.setAction(Constants.REFRESH_ORDERLIST);
				intent.putExtra("tabIndex", 3);
				mContext.sendBroadcast(intent);
				break;
			case MsgId.DOWN_DATA_F:
				CustomDialogProgressHandler.getInstance().closeCustomCircleProgressDialog();
				String MSG = deleteOrder_thread.getOutMsg();
				int code = deleteOrder_thread.getOutCode();
				CommonUtil.log("删除订单返回的错误code：" + code);

				if (code < MsgId.HTTP_APPOINTMENT_ERRORCODE_MIN || code > MsgId.HTTP_APPOINTMENT_ERRORCODE_MAX) {
					// DialogUtils.showToastMsg(mContext, MSG,
					// Toast.LENGTH_SHORT);
					Toast_Util.showToast(mContext, MSG);
				} else {
					DialogUtils.showToastMsg(mContext,
							mContext.getResources().getString(R.string.common_toast_net_down_data_fail),
							Toast.LENGTH_SHORT);
				}
				break;
			case MsgId.NET_NOT_CONNECT:
				CustomDialogProgressHandler.getInstance().setCustomDialog(mContext,
						mContext.getResources().getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

}
