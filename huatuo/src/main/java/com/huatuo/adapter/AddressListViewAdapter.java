package com.huatuo.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.personal.AddressManageActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.DeleteAddress_thread;
import com.huatuo.net.thread.SaveDefaultAddress_thread;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Constants;
import com.huatuo.util.CustomDialogProgressHandler;
import com.huatuo.util.DialogUtils;

public class AddressListViewAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<JSONObject> arrayList;
	private Context mContext;

	public AddressListViewAdapter(Context mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<JSONObject>();
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
		}
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
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.activity_personal_address_listview_item2, null);
			holder = new ViewHolder();
			holder.tv_default = (TextView) convertView
					.findViewById(R.id.tv_default);
			holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
			holder.tv_mobile = (TextView) convertView
					.findViewById(R.id.tv_mobile);
			holder.tv_address = (TextView) convertView
					.findViewById(R.id.tv_address);
			holder.tv_sex = (TextView) convertView.findViewById(R.id.tv_sex);
			holder.tv_edit = (TextView) convertView.findViewById(R.id.tv_edit);
			holder.tv_del = (TextView) convertView.findViewById(R.id.tv_del);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!CommonUtil.emptyListToString3(arrayList)) {
			final JSONObject jsonObject = arrayList.get(position);
			holder.tv_name.setText(jsonObject.optString("name", ""));
			holder.tv_mobile.setText(jsonObject.optString("mobile", ""));
			holder.tv_address.setText(jsonObject.optString("userArea", "")
					+ jsonObject.optString("address", ""));
			String sex = jsonObject.optString("gender", "");
			if (!"".equals(sex) && sex.equals("男")) {
				holder.tv_sex.setText("先生");
			} else if (!"".equals(sex) && sex.equals("女")) {
				holder.tv_sex.setText("女士");
			}
			final String isDefault = jsonObject.optString("isDefault", "");
			// isDefault 是否默认地址 N 1 M 非默认0 默认1
			if ("1".equals(isDefault)) {

				holder.tv_default.setCompoundDrawablesWithIntrinsicBounds(
						mContext.getResources().getDrawable(
								R.drawable.icon_address_def), null, null, null);
				holder.tv_default.setText("默认地址");
				holder.tv_default.setTextColor(mContext.getResources()
						.getColor(R.color.c4));

			} else {
				holder.tv_default.setCompoundDrawablesWithIntrinsicBounds(
						mContext.getResources().getDrawable(
								R.drawable.icon_address_nodef), null, null,
						null);
				holder.tv_default.setText("设为默认地址");
				holder.tv_default.setTextColor(mContext.getResources()
						.getColor(R.color.c6));
				holder.tv_default.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						CustomDialogProgressHandler
								.getInstance()
								.showCustomCircleProgressDialog(
										mContext,
										null,
										mContext.getResources()
												.getString(
														R.string.common_toast_net_prompt_submit));
						Handler saveDefaultAddressHandler = new SaveDefaultAddressHandler();
						HashMap<String, String> injosnMap = new HashMap<String, String>();
						injosnMap.put("userID", MyApplication.getUserID());
						injosnMap.put("addressID",
								jsonObject.optString("ID", ""));
						injosnMap.put("userName",
								jsonObject.optString("userName", ""));
						injosnMap.put("sex", jsonObject.optString("sex", ""));
						injosnMap.put("mobile",
								jsonObject.optString("mobile", ""));
						injosnMap.put("address",
								jsonObject.optString("address", ""));
						injosnMap.put("userArea",
								jsonObject.optString("userArea", ""));
						String city = CommonUtil.getStringOfSharedPreferences(mContext.getApplicationContext(), "NOW_CITY", "");
						injosnMap.put("cityName", city);
						// 设置为默认地址
						injosnMap.put("isDefault", "1");
						SaveDefaultAddress_thread saveDefaultAddress_thread = new SaveDefaultAddress_thread(
								mContext, saveDefaultAddressHandler, injosnMap);
						Thread thread = new Thread(saveDefaultAddress_thread);
						thread.start();

					}
				});
			}

			holder.tv_default.setCompoundDrawablePadding(CommonUtil.dip2px(
					mContext, 8));

			// 修改地址
			holder.tv_edit.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, AddressManageActivity.class);
					intent.putExtra("addressJsonObject", jsonObject.toString());
					mContext.startActivity(intent);

				}
			});

			// 删除地址
			holder.tv_del.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDelAddressDialog(jsonObject);
				}
			});
		}

		return convertView;
	}

	static class ViewHolder {
		ImageView iv_mark;
		TextView tv_default, tv_name, tv_mobile, tv_address, tv_sex, tv_edit,
				tv_del;
	}

	private void showDelAddressDialog(final JSONObject jsonObject) {

		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle("提示");
		builder.setMessage("您确定要删除该地址吗？");
		builder.setPositiveButton(
				mContext.getResources().getString(R.string.common_confirm),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						CustomDialogProgressHandler
								.getInstance()
								.showCustomCircleProgressDialog(
										mContext,
										null,
										mContext.getResources()
												.getString(
														R.string.common_toast_net_prompt_submit));
						Handler delAddressHandler = new DelAddressHandler();
						HashMap<String, String> injosnMap = new HashMap<String, String>();
						injosnMap.put("addressID",
								jsonObject.optString("ID", ""));
						DeleteAddress_thread deleteAddress_thread = new DeleteAddress_thread(
								mContext, delAddressHandler, injosnMap);
						Thread thread = new Thread(deleteAddress_thread);
						thread.start();
					}
				});

		builder.setNegativeButton(
				mContext.getResources().getString(R.string.common_cancel),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.create().show();
	}

	class DelAddressHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				CustomDialogProgressHandler.getInstance()
						.closeCustomCircleProgressDialog();
				sendBroadcast();
				break;
			case MsgId.DOWN_DATA_F:
				CustomDialogProgressHandler.getInstance()
						.closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				CustomDialogProgressHandler.getInstance().setCustomDialog(
						mContext,
						mContext.getResources().getString(
								R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	class SaveDefaultAddressHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				CustomDialogProgressHandler.getInstance()
						.closeCustomCircleProgressDialog();
				sendBroadcast();
				break;
			case MsgId.DOWN_DATA_F:
				CustomDialogProgressHandler.getInstance()
						.closeCustomCircleProgressDialog();
				DialogUtils.showToastMsg(mContext, mContext.getResources()
						.getString(R.string.common_toast_net_down_data_fail),
						Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				CustomDialogProgressHandler.getInstance().setCustomDialog(
						mContext,
						mContext.getResources().getString(
								R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

	private void sendBroadcast() {
		Intent intent = new Intent();
		intent.setAction(Constants.REFRESH_DELADDRESS);
		mContext.sendBroadcast(intent);
	}
}
