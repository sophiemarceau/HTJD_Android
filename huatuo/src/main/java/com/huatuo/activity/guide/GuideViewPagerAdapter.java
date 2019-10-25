package com.huatuo.activity.guide;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * 表情显示的viewpager
 */
public class GuideViewPagerAdapter extends PagerAdapter {

	private Context mContext;
	private List<View> data = new ArrayList<View>();

	public GuideViewPagerAdapter(Context context, List<View> data) {
		this.mContext = context;
		this.data = data;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// container.removeView((View) object);
		((ViewPager) container).removeView(data.get(position));
	}

	@Override
	public int getCount() {
		// return imagePathStrings.length;
		return data.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, final int position) {
		((ViewPager) view).addView(data.get(position), 0);
		return data.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public int getItemPosition(Object object) {
		return PagerAdapter.POSITION_NONE;
	}
}
