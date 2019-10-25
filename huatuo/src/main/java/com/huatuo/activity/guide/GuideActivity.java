package com.huatuo.activity.guide;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;

import com.huatuo.R;
import com.huatuo.activity.home.HomeActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.CirclePageIndicator;
import com.huatuo.custom_widget.PageIndicator;
import com.huatuo.util.Constants;
import com.umeng.analytics.MobclickAgent;

public class GuideActivity extends Activity {
	private ViewPager pager;
	private PageIndicator mIndicator;
	private Context mContext;
	private GuideViewPagerAdapter adapter;
	private int[] pagerView = { R.layout.guide_pager_01,
			R.layout.guide_pager_02, R.layout.guide_pager_03,
			R.layout.guide_pager_04 };
	private List<View> data = new ArrayList<View>();
	private View view;
	private int currentItem = 0; // 当前图片的索引号
	private GestureDetector gestureDetector; // 用户滑动
	/** 记录当前分页ID */
	private int flaggingWidth;// 互动翻页所需滚动的长度是当前屏幕宽度的1/3

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.activity_guide);
		gestureDetector = new GestureDetector(new GuideViewTouch());
		mContext = this;
		initWidget();
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	/**
	 * 在这里获取到每个需要用到的控件的实例
	 * 
	 */
	private void initWidget() {
		for (int i = 0; i < pagerView.length; i++) {
			view = this.getLayoutInflater().inflate(pagerView[i], null);
			data.add(view);
		}
		pager = (ViewPager) findViewById(R.id.pager);
		adapter = new GuideViewPagerAdapter(mContext, data);
		pager.setAdapter(adapter);
		pager.setOffscreenPageLimit(3);
		mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
		mIndicator.setViewPager(pager);

		// 设置一个监听器，当ViewPager中的页面改变时调用
		pager.setOnPageChangeListener(new MyPageChangeListener());
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if (gestureDetector.onTouchEvent(event)) {
			event.setAction(MotionEvent.ACTION_CANCEL);
		}
		return super.dispatchTouchEvent(event);
	}

	private class GuideViewTouch extends SimpleOnGestureListener {
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			if (currentItem == 3) {
				if (Math.abs(e1.getX() - e2.getX()) > Math.abs(e1.getY()
						- e2.getY())
						&& (e1.getX() - e2.getX() <= (-flaggingWidth) || e1
								.getX() - e2.getX() >= flaggingWidth)) {
					if (e1.getX() - e2.getX() >= flaggingWidth) {
						jumpToHomeActivity();
						return true;
					}
				}
			}
			return false;
		}
	}

	/**
	 * 当ViewPager中页面的状态发生改变时调用
	 * 
	 * @author Administrator
	 * 
	 */
	private class MyPageChangeListener implements OnPageChangeListener {
		public void onPageSelected(int position) {
			currentItem = position;
		}

		public void onPageScrollStateChanged(int arg0) {
		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}

	/**
	 * 
	 * @param 立即体验按钮的监听事件响应
	 */
	public void experienceBtnClickListener(View v) {
		jumpToHomeActivity();
	}

	private void jumpToHomeActivity() {
		Intent intent = new Intent(mContext, HomeActivity.class);
		intent.putExtra("tabIndex", 0);
		startActivity(intent);
		finish();
		MyApplication.setIsFirstIn();
	}
	
	
	@Override
	public void onBackPressed() {
		MyApplication.getInstance().exit();
	}
}