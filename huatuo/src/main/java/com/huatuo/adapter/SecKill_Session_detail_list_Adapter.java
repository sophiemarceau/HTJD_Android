package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.login.LoginUtil;
import com.huatuo.activity.pay.Pay_SecKillActivity;
import com.huatuo.activity.project.ProjectDetailActivity;
import com.huatuo.bean.SecKillActivityGradeBean;
import com.huatuo.bean.SecKillActivityListItemBean;
import com.huatuo.bean.SecKillActivitydescBean;
import com.huatuo.bean.SecKillPayBean;
import com.huatuo.custom_widget.CustomListView;
import com.huatuo.interfaces.IHandler;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.JsonUtil;
import com.huatuo.util.NumFormatUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SecKill_Session_detail_list_Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<SecKillActivityListItemBean> arrayList;
	private Activity mContext;
	private boolean isSecKill = false;// 是否可以秒杀
	private String serviceName = "";
	private String serviceIcon = "";
	private String minPrice = "";
	private String priceID = "";
	private ArrayList<SecKillActivitydescBean> activitydesc = new ArrayList<SecKillActivitydescBean>();
	private String servID = "";
	private String activityID = "";
	

	public SecKill_Session_detail_list_Adapter(Activity mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<SecKillActivityListItemBean>();
	}

	public void add(SecKillActivityListItemBean item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<SecKillActivityListItemBean> arrayList, boolean isSecKill) {
		this.isSecKill = isSecKill;
		if (!CommonUtil.emptyListToString3(arrayList)) {
			this.arrayList.addAll(arrayList);
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
					R.layout.fragment_first_seckill_item_detail, null);
			holder = new ViewHolder();

			holder.iv_secskill_project_icon = (ImageView) convertView
					.findViewById(R.id.iv_secskill_project_icon);
			holder.tv_secskill_project_name = (TextView) convertView
					.findViewById(R.id.tv_secskill_project_name);
			holder.tv_secskill_store = (TextView) convertView
					.findViewById(R.id.tv_secskill_store);
			holder.tv_secskill_addr = (TextView) convertView
					.findViewById(R.id.tv_secskill_addr);
			holder.lv_secskill_project_info = (CustomListView) convertView
					.findViewById(R.id.lv_secskill_project_info);
			holder.lv_secskill_project_grade = (CustomListView) convertView
					.findViewById(R.id.lv_secskill_project_grade);

			holder.iv_line2 = (ImageView) convertView
					.findViewById(R.id.iv_line2);

			// 未开启等级
			holder.grade_item = (RelativeLayout) convertView
					.findViewById(R.id.grade_item);
			holder.tv_project_grade_price = (TextView) convertView
					.findViewById(R.id.tv_project_grade_price);
			holder.tv_project_grade_market_price = (TextView) convertView
					.findViewById(R.id.tv_project_grade_market_price);
			holder.tv_project_grade = (TextView) convertView
					.findViewById(R.id.tv_project_grade);
			holder.tv_project_stock = (TextView) convertView
					.findViewById(R.id.tv_project_stock);
			holder.tv_project_secSkill = (TextView) convertView
					.findViewById(R.id.tv_project_secSkill);

			CommonUtil.initScreen(mContext);
			LayoutParams layoutParams = holder.iv_secskill_project_icon
					.getLayoutParams();
			layoutParams.width = (CommonUtil.WIDTH_SCREEN - CommonUtil.dip2px(
					mContext, 20));
			layoutParams.height = layoutParams.width / 4 * 3;
			holder.iv_secskill_project_icon.setLayoutParams(layoutParams);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!CommonUtil.emptyListToString3(arrayList)) {
			SecKillActivityListItemBean secKillActivityListItemBean = arrayList.get(position);
			// ID 秒杀活动ID ANS 64 M 秒杀活动ID
			// name 秒杀活动名称 ANS 64 M 秒杀活动名称
			// servID 服务ID ANS 64 M 服务ID
			// servName 服务名称 ANS 64 M 服务名称
			// storeID 店铺ID ANS 64 M 店铺ID
			// storeName 属店铺名称 ANS 64 M 店铺名称
			// address 店铺地址 ANS 256 M 店铺地址
			// activitydesc 活动说明 JSONArray 64 C 活动说明
			// isLevel 是否开启等级 N 4 M 是否开启等级(0 未启用， 1启用)
			// stock 库存 N 64 M 库存
			// minPrice 最低价格 ANS 64 C 最低价格
			// marketPrice 市场价格 ANS 64 C 最低价格的市场价格
			// servLevelList 服务价格等级列表 JSONArray C 服务价格等级列表
			String ID = secKillActivityListItemBean.ID;
			String name = secKillActivityListItemBean.name;
			String servName = secKillActivityListItemBean.servName;
			String storeName = secKillActivityListItemBean.storeName;
			String address = secKillActivityListItemBean.address;

			String isLevel = secKillActivityListItemBean.isLevel;

			String minPrice = NumFormatUtil.centFormatYuanToString(secKillActivityListItemBean
					.minPrice) + "";
			String marketPrice = NumFormatUtil
					.centFormatYuanToString(secKillActivityListItemBean.marketPrice);
			String icon = secKillActivityListItemBean.icon;
			int stock = secKillActivityListItemBean.stock;
			ImageLoader.getInstance().displayImage(
					icon,
					holder.iv_secskill_project_icon,
					ImageLoader_DisplayImageOptions.getInstance()
							.setDefaultImageBigImg());
			holder.tv_secskill_project_name.setText(servName);
			holder.tv_secskill_store.setText(storeName);
			holder.tv_secskill_addr.setText(address);
			holder.tv_project_grade.setText("");

			// activitydesc 活动说明
			ArrayList<SecKillActivitydescBean> servInfoList = new ArrayList<SecKillActivitydescBean>();
			servInfoList = secKillActivityListItemBean.activitydesc;
				if (!CommonUtil.emptyListToString3(servInfoList)) {
				holder.iv_line2.setVisibility(View.VISIBLE);
				holder.lv_secskill_project_info.setVisibility(View.VISIBLE);
				// 服务介绍列表
				SecKill_Session_detail_list_servInfo_list_Adapter servInfo_list_Adapter = new SecKill_Session_detail_list_servInfo_list_Adapter(
						mContext);
				holder.lv_secskill_project_info
						.setAdapter(servInfo_list_Adapter);
				servInfo_list_Adapter.clear();
				servInfo_list_Adapter.add(servInfoList);
			} else {
				holder.iv_line2.setVisibility(View.GONE);
				holder.lv_secskill_project_info.setVisibility(View.GONE);
			}
			// 设置图片点击事件
			holder.iv_secskill_project_icon
					.setOnClickListener(new MyOnClickListener(position, 1));
			// 是否开启等级(0 未启用， 1启用)
			if (isLevel.equals("0")) {
				holder.grade_item.setVisibility(View.VISIBLE);
				holder.lv_secskill_project_grade.setVisibility(View.GONE);
				holder.tv_project_grade.setText("");
				holder.tv_project_stock.setText("剩余" + stock + "份");
				// 现价格
				String newPrice = "￥" + minPrice;
				SpannableStringBuilder spanBuilder = new SpannableStringBuilder(
						newPrice);
				spanBuilder.setSpan(new TextAppearanceSpan(mContext,
						R.style.secKill_price_style1), 0, 1, 0);
				spanBuilder.setSpan(new TextAppearanceSpan(mContext,
						R.style.secKill_price_style2), 2, newPrice.length(), 0);
				holder.tv_project_grade_price.setText(spanBuilder);
				// 市场价格
				holder.tv_project_grade_market_price.setText("￥" + marketPrice);
				holder.tv_project_grade_market_price.getPaint().setFlags(
						Paint.STRIKE_THRU_TEXT_FLAG);
				// 设置秒杀点击事件
				holder.tv_project_secSkill
						.setOnClickListener(new MyOnClickListener(position, 0));

				// 不能秒杀/库存为0 不能点击秒杀
				if (!isSecKill || 0 == stock) {
					holder.tv_project_secSkill.setClickable(false);
					holder.iv_secskill_project_icon.setClickable(true);
					holder.tv_project_secSkill.setTextColor(mContext
							.getResources().getColor(
									R.color.tv_secSkillTime_finish_color));
					holder.tv_project_secSkill.setBackground(mContext
							.getResources().getDrawable(
									R.drawable.custom_seckill_finish_bg));
				} else {
					holder.tv_project_secSkill.setClickable(true);
					holder.iv_secskill_project_icon.setClickable(false);

					holder.tv_project_secSkill.setTextColor(mContext
							.getResources().getColor(
									R.color.tv_secSkillTime_color));
					holder.tv_project_secSkill.setBackground(mContext
							.getResources().getDrawable(
									R.drawable.custom_seckill_bg));
				}

			} else {
				holder.grade_item.setVisibility(View.GONE);
				holder.lv_secskill_project_grade.setVisibility(View.VISIBLE);
				// 服务等级
				ArrayList<SecKillActivityGradeBean> servLevelList = new ArrayList<SecKillActivityGradeBean>();
				servLevelList = secKillActivityListItemBean.servLevelList;
				
				if (!CommonUtil.emptyListToString3(servLevelList)) {
					CommonUtil.logE("服务等级:" + servLevelList);
					// 判断等级库存都为0或者不能秒杀 能点击图片进入项目详情
					if (judgeServGradeStock(servLevelList) || !isSecKill) {
						holder.iv_secskill_project_icon.setClickable(true);
					} else {
						holder.iv_secskill_project_icon.setClickable(false);
					}
					// 等级列表
					// SecKill_Session_detail_list_grade_list_Adapter
					// grade_list_Adapter = new
					// SecKill_Session_detail_list_grade_list_Adapter(
					// mContext,servLevelList, isFinishOrNoStart);
					SecKill_Session_detail_list_grade_list_Adapter grade_list_Adapter = new SecKill_Session_detail_list_grade_list_Adapter(
							mContext);
					holder.lv_secskill_project_grade
							.setAdapter(grade_list_Adapter);
					grade_list_Adapter.clear();
					grade_list_Adapter.add(servLevelList, isSecKill,secKillActivityListItemBean);

				}

			}

		}
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_secskill_project_icon;
		TextView tv_secskill_project_name, tv_secskill_store, tv_secskill_addr;
		CustomListView lv_secskill_project_info, lv_secskill_project_grade;
		ImageView iv_line2;
		// 未开启等级
		TextView tv_project_grade_price, tv_project_grade_market_price,
				tv_project_grade, tv_project_stock, tv_project_secSkill;
		RelativeLayout grade_item;
	}

	class MyOnClickListener implements OnClickListener {
		private int position;
		private int type;

		/**
		 * @param position
		 * @param type
		 *            0：秒杀点击 1：整体项目点击
		 */
		public MyOnClickListener(int position, int type) {
			this.position = position;
			this.type = type;
		}

		@Override
		public void onClick(View v) {
			SecKillActivityListItemBean secKillActivityListItemBean = arrayList.get(position);
		    final SecKillPayBean secKillPayBean = new SecKillPayBean();
			if (null != secKillActivityListItemBean) {
				// ID 秒杀活动ID ANS 64 M 秒杀活动ID
				// name 秒杀活动名称 ANS 64 M 秒杀活动名称
				// servID 服务ID ANS 64 M 服务ID
				// servName 服务名称 ANS 64 M 服务名称
				// icon 服务Icon ANS 256 M 服务图片
				// storeID 店铺ID ANS 64 M 店铺ID
				// storeName 属店铺名称 ANS 64 M 店铺名称
				// address 店铺地址 ANS 256 M 店铺地址
				// activitydesc 活动说明 JSONArray 64 C 活动说明
				// isLevel 是否开启等级 N 4 M 是否开启等级(0 未启用， 1启用)
				// stock 库存 N 64 C 库存
				// minPrice 最低价格
				// marketPrice 市场价格
				// servLevelList 服务价格等级列表
				serviceName = secKillActivityListItemBean.servName;
				serviceIcon = secKillActivityListItemBean.icon;
				minPrice = secKillActivityListItemBean.minPrice;
				servID = secKillActivityListItemBean.servID;
				activityID = secKillActivityListItemBean.ID;
				activitydesc = secKillActivityListItemBean.activitydesc;
				
				
				
				secKillPayBean.minPrice = minPrice;
				secKillPayBean.serviceName = serviceName;
				secKillPayBean.serviceIcon = serviceIcon;
				secKillPayBean.activityID = activityID;
				secKillPayBean.activitydesc = activitydesc;

			}
			switch (type) {
			case 0:// 去往秒杀支付
				LoginUtil.IsLogin(mContext, new IHandler() {

					@Override
					public void doHandler() {
						Intent intent = new Intent();
						intent.setClass(mContext, Pay_SecKillActivity.class);
						Bundle bundle = new Bundle();
					    bundle.putSerializable("SecKillPayBean", secKillPayBean);
						intent.putExtras(bundle);
						mContext.startActivity(intent);
					}
				});

				break;
			case 1:// 去项目详情
				CommonUtil.log("ID:" + servID);
				Intent intent = new Intent();
				intent.setClass(mContext, ProjectDetailActivity.class);
				intent.putExtra("ID", servID);
				mContext.startActivity(intent);

				break;
			default:
				break;
			}

		}

	}

	/**
	 * 判断等级对应库存数量--都为0则返回true
	 * 
	 * @return
	 */
	private boolean judgeServGradeStock(ArrayList<SecKillActivityGradeBean> servLevelList) {
		boolean isEmptyOfAllStock = false;
		for (int i = 0; i < servLevelList.size(); i++) {
			String stock = servLevelList.get(i).stock;
			if (!stock.equals("0")) {
				isEmptyOfAllStock = false;
				break;
			} else {
				isEmptyOfAllStock = true;
			}
		}

		return isEmptyOfAllStock;
	}

}