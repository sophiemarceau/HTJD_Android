package com.huatuo.adapter;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.activity.seckill.SecKill_SessionActivity;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.adapter.SecKill_Session_detail_list_Adapter.MyOnClickListener;
import com.huatuo.adapter.SecKill_Session_detail_list_Adapter.ViewHolder;
import com.huatuo.bean.SecKillSpecialListItemBean;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.FormatDistanceUtil;
import com.huatuo.util.ImageLoader_DisplayImageOptions;
import com.huatuo.util.NumFormatUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class SecKill_Session_List_Adapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<SecKillSpecialListItemBean> arrayList;
	private Activity mContext;
	private boolean isSecKill = false;//是否可以秒杀
	private boolean isSecKillSession = false;//是否是秒杀专场

	public SecKill_Session_List_Adapter(Activity mContext) {
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
		arrayList = new ArrayList<SecKillSpecialListItemBean>();
	}

	public void add(SecKillSpecialListItemBean item) {
		arrayList.add(item);
		notifyDataSetChanged();
	}

	public void add(ArrayList<SecKillSpecialListItemBean> arrayList,boolean isSecKill,boolean isSecKillSession) {
		this.isSecKill = isSecKill;
		this.isSecKillSession = isSecKillSession;
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
		CommonUtil.logE("getCount:"+arrayList.size());
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
			convertView = inflater.inflate(R.layout.fragment_first_seckill_session_item,
					null);
			holder = new ViewHolder();

//			
			holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
			holder.tv_storeName = (TextView) convertView
					.findViewById(R.id.tv_storeName);
			holder.tv_remain_copies = (TextView) convertView
					.findViewById(R.id.tv_remain_copies);
			holder.tv_price = (TextView) convertView
					.findViewById(R.id.tv_price);
			holder.tv_price1 = (TextView) convertView
					.findViewById(R.id.tv_price1);
			holder.tv_price2 = (TextView) convertView
					.findViewById(R.id.tv_price2);

			CommonUtil.initScreen(mContext);
			LayoutParams layoutParams = holder.iv_icon.getLayoutParams();
			layoutParams.width = (CommonUtil.WIDTH_SCREEN - CommonUtil.dip2px(
					mContext, 20));
			layoutParams.height = layoutParams.width / 4 * 3;
			holder.iv_icon.setLayoutParams(layoutParams);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		CommonUtil.logE("专场列表："+arrayList);
		if (!CommonUtil.emptyListToString3(arrayList)) {
			SecKillSpecialListItemBean secKillSpecialListItemBean = arrayList.get(position);

			/*
			 * ID	专题ID	ANS	64	M	专题ID
			name	专题名称	ANS	64	M	专题名称
			icon	专题Icon	ANS	256	M	专题图片
			minPrice	最低价格	ANS	64	M	最低价格
			stock	库存	N	64	M	库存
			*/
			String ID = secKillSpecialListItemBean.ID;
			String name = secKillSpecialListItemBean.name;
			String minPrice = NumFormatUtil.centFormatYuanToString(secKillSpecialListItemBean
					.minPrice) + "";
			String icon = secKillSpecialListItemBean.icon;
			String stock = secKillSpecialListItemBean.stock;
			ImageLoader.getInstance().displayImage(
					icon,
					holder.iv_icon,
					ImageLoader_DisplayImageOptions.getInstance()
							.setDefaultImageBigImg());
			holder.tv_storeName.setText(name);
			holder.tv_remain_copies.setText("剩余"+stock+"份");
			holder.tv_price.setText(minPrice);

			if ("0".equals(minPrice)) {
				holder.tv_price.setVisibility(View.GONE);
				holder.tv_price1.setVisibility(View.GONE);
				holder.tv_price2.setVisibility(View.GONE);
			} else {
				holder.tv_price.setVisibility(View.VISIBLE);
				holder.tv_price1.setVisibility(View.VISIBLE);
				holder.tv_price2.setVisibility(View.VISIBLE);
			}

			convertView.setOnClickListener(new MyOnClickListener(position));
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView iv_icon;
		TextView tv_storeName, tv_remain_copies, tv_price,
				tv_price1, tv_price2;
	}

	class MyOnClickListener implements OnClickListener {
		private int position;

		public MyOnClickListener(int position) {
			this.position = position;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			SecKillSpecialListItemBean secKillSpecialListItemBean = arrayList.get(position);
			String ID = "";
			if (null != secKillSpecialListItemBean) {
				ID = secKillSpecialListItemBean.ID;
			}
			Intent intent = new Intent();
			//秒杀开始并且未结束
			if(isSecKillSession && isSecKill){
				intent.putExtra("isSecKill", true);
				intent.putExtra("type", 0);
			}else if(!isSecKillSession ){
				intent.putExtra("isSecKill", false);//预告专场设置不能秒杀 （用秒杀结束替代这种情况）
				intent.putExtra("type", 1);
			}
			
			intent.setClass(mContext, SecKill_SessionActivity.class);
			intent.putExtra("ID", ID);
			
			mContext.startActivity(intent);
		}

	}
}