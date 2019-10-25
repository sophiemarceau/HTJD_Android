package com.huatuo.custom_widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Context;

import com.huatuo.R;
import com.huatuo.util.Toast_Util;

public class XlistViewUtil {

	public static XlistViewUtil instance;

	public static XlistViewUtil getInstance() {
		if (instance == null) {
			synchronized (XlistViewUtil.class) {
				if (instance == null) {
					instance = new XlistViewUtil();
				}
			}
		}

		return instance;
	}

	public String getTime() {
		return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA)
				.format(new Date());
	}

	/**
	 * 计算listview的item数量设置能不能刷新和加载更多
	 * 
	 * @param listView
	 * @param list
	 */
	public void measureListSize(Context context ,XListView listView, List<?> list) {
		if (null != listView) {
			listView.stopRefresh();
			listView.stopLoadMore();
			if (null == list || list.size() < 50) {
				listView.setPullLoadEnable(false);
				
//				 Toast_Util.showToastAtBootom(context,
//						 context.getResources().getString(R.string.xlistview_footer_hint_no_more));
			} else {
				listView.setFooterViewVISIBLE();
				listView.setPullLoadEnable(true);
			}
		}
	}
}
