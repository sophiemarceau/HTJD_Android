package com.huatuo.adapter;

import java.util.ArrayList;

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
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.login.LoginUtil;
import com.huatuo.activity.pay.Pay_SecKillActivity;
import com.huatuo.bean.SecKillActivityGradeBean;
import com.huatuo.bean.SecKillActivityListItemBean;
import com.huatuo.bean.SecKillActivitydescBean;
import com.huatuo.bean.SecKillPayBean;
import com.huatuo.interfaces.IHandler;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.NumFormatUtil;

public class SecKill_Session_detail_list_grade_list_Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<SecKillActivityGradeBean> arrayList;
	private Activity mContext;
	private SecKillActivityListItemBean secKillActivityListItemBean;
	private boolean isSecKill;// 是否可以秒杀
	private String serviceName = "";
	private String serviceIcon = "";
	private String ID = "";//等级ID
	private String minPrice = "";
	private String priceID = "";
	private ArrayList<SecKillActivitydescBean> activitydesc = new ArrayList<SecKillActivitydescBean>();
	private String activityID = "";

	public SecKill_Session_detail_list_grade_list_Adapter(Activity mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<SecKillActivityGradeBean>();
	}

//	public SecKill_Session_detail_list_grade_list_Adapter(Activity mContext,
//			ArrayList<JSONObject> arrayList,  boolean isFinishOrNoStart) {
//		this.mContext = mContext;
//		inflater = LayoutInflater.from(mContext);
//		this.isFinishOrNoStart = isFinishOrNoStart;
//		this.arrayList = new ArrayList<JSONObject>();
//		if (!CommonUtil.emptyListToString3(arrayList)) {
//			// for (int i = 0; i < arrayList.size(); i++) {
//			// JSONObject item = arrayList.get(i);
//			// this.arrayList.add(item);
//			// }
//			this.arrayList = arrayList;
//		}
//
//	}

	public void add(SecKillActivityGradeBean item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<SecKillActivityGradeBean> arrayList, boolean isSecKill,
			SecKillActivityListItemBean secKillActivityListItemBean) {
		this.isSecKill = isSecKill;
		this.secKillActivityListItemBean = secKillActivityListItemBean;
		if (!CommonUtil.emptyListToString3(arrayList)) {
				this.arrayList = arrayList;
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
					R.layout.fragment_first_seckill_item_detail_grade_item,
					null);
			holder = new ViewHolder();

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

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (!CommonUtil.emptyListToString3(arrayList)) {
			SecKillActivityGradeBean secKillActivityGradeBean = arrayList.get(position);
			// ID 等级ID ANS 64 M 等级ID
			// name 等级名称 ANS 64 M 等级名称
			// stock 库存 N 64 M 库存
			// minPrice 最低价格 ANS 64 M 最低价格
			// marketPrice 市场价格 ANS 64 M 最低价格的市场价格
			String ID = secKillActivityGradeBean.ID;
			String name = secKillActivityGradeBean.name;
			String minPrice = "￥"
					+ NumFormatUtil.centFormatYuanToString(secKillActivityGradeBean.minPrice);
			String marketPrice = "￥"
					+ NumFormatUtil.centFormatYuanToString(secKillActivityGradeBean.marketPrice);
			String stock = secKillActivityGradeBean.stock;
			// String stock = "3";

			holder.tv_project_grade.setText(name);
			holder.tv_project_stock.setText("剩余" + stock + "份");

			// 现价格
			SpannableStringBuilder spanBuilder = new SpannableStringBuilder(
					minPrice);
			spanBuilder.setSpan(new TextAppearanceSpan(mContext,
					R.style.secKill_price_style1), 0, 1, 0);
			spanBuilder.setSpan(new TextAppearanceSpan(mContext,
					R.style.secKill_price_style2), 2, minPrice.length(), 0);
			holder.tv_project_grade_price.setText(spanBuilder);
			// 市场价格
			holder.tv_project_grade_market_price.setText(marketPrice);
			holder.tv_project_grade_market_price.getPaint().setFlags(
					Paint.STRIKE_THRU_TEXT_FLAG);

			// CommonUtil.logE("服务等级列表：isFinishOrNoStart"+isFinishOrNoStart);
			holder.tv_project_secSkill
					.setOnClickListener(new MyOnClickListener(position));
			// CommonUtil.logE("服务等级列表：isFinishOrNoStart:"+isFinishOrNoStart);
			if (!isSecKill || ("0").equals(stock)) {

				holder.tv_project_secSkill.setClickable(false);
				holder.tv_project_secSkill.setTextColor(mContext.getResources()
						.getColor(R.color.tv_secSkillTime_finish_color));
				holder.tv_project_secSkill.setBackground(mContext
						.getResources().getDrawable(
								R.drawable.custom_seckill_finish_bg));
			} else {
				holder.tv_project_secSkill.setClickable(true);
				holder.tv_project_secSkill.setTextColor(mContext.getResources()
						.getColor(R.color.tv_secSkillTime_color));
				holder.tv_project_secSkill.setBackground(mContext
						.getResources().getDrawable(
								R.drawable.custom_seckill_bg));
			}
		}
		return convertView;
	}

	static class ViewHolder {
		TextView tv_project_grade_price, tv_project_grade_market_price,
				tv_project_grade, tv_project_stock, tv_project_secSkill;
	}

	class MyOnClickListener implements OnClickListener {
		private int position;

		public MyOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {

			SecKillActivityGradeBean secKillActivityGradeBean = arrayList.get(position);
			//等级信息
			if (null != secKillActivityGradeBean) {
//				ID	等级ID	ANS	64	M	等级ID
//				name	等级名称	ANS	64	M	等级名称
//				stock	库存	N	64	M	库存
//				minPrice	最低价格	ANS	64	M	最低价格
//				marketPrice	市场价格	ANS	64	M	最低价格的市场价格
				minPrice = secKillActivityGradeBean.minPrice;
				ID = secKillActivityGradeBean.ID;
			}
			//活动信息
			if (null != secKillActivityListItemBean) {
				serviceName = secKillActivityListItemBean.servName;
				serviceIcon = secKillActivityListItemBean.icon;
				activityID = secKillActivityListItemBean.ID;
				activitydesc = secKillActivityListItemBean.activitydesc;
			}
			
			final SecKillPayBean secKillPayBean = new SecKillPayBean();
			secKillPayBean.minPrice = minPrice;
			secKillPayBean.serviceName = serviceName;
			secKillPayBean.serviceIcon = serviceIcon;
			secKillPayBean.activityID = activityID;
			secKillPayBean.activitydesc = activitydesc;
			secKillPayBean.priceID = ID;
			
			LoginUtil.IsLogin(mContext, new IHandler() {

				@Override
				public void doHandler() {
					// 去往秒杀支付
					Intent intent = new Intent();
					intent.setClass(mContext, Pay_SecKillActivity.class);
					Bundle bundle = new Bundle();
				    bundle.putSerializable("SecKillPayBean", secKillPayBean);
					intent.putExtras(bundle);
					mContext.startActivity(intent);
				}
			});

		}

	}
}