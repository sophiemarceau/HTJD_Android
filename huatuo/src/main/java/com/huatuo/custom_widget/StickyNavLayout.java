package com.huatuo.custom_widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.OverScroller;
import android.widget.ScrollView;

import com.huatuo.R;
import com.huatuo.util.CommonUtil;

public class StickyNavLayout extends LinearLayout {

//	private View mTop;
	private View mTop_icon;
	private CustomListView mTop_lv_info;
	private View mNav;
//	private ViewPager mViewPager;
	private ListView mListView;
	
	private int mTop_lv_infoHeight;
	private int mTopViewHeight;
	private ViewGroup mInnerScrollView;
	private boolean isTopHidden = false;

	private OverScroller mScroller;
	private VelocityTracker mVelocityTracker;
	private int mTouchSlop;
	private int mMaximumVelocity, mMinimumVelocity;

	private float mLastY;
	private boolean mDragging;

	private boolean isInControl = false;
	private float topShowScale;
	
	

	public StickyNavLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);

		mScroller = new OverScroller(context);
		
		mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		mMaximumVelocity = ViewConfiguration.get(context)
				.getScaledMaximumFlingVelocity();
		mMinimumVelocity = ViewConfiguration.get(context)
				.getScaledMinimumFlingVelocity();

	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

//		mTop = findViewById(R.id.id_stickynavlayout_topview);
		mTop_icon  = findViewById(R.id.iv_secskill_project_icon);
		View mTop_info  = findViewById(R.id.lv_secskill_project_top_info);
		if (!(mTop_info instanceof ListView)) {
			throw new RuntimeException(
					"id_stickynavlayout_listview show used by ListView !");
		}
		mTop_lv_info = (CustomListView) mTop_info;
		
		mNav = findViewById(R.id.id_stickynavlayout_tab);
		View view = findViewById(R.id.id_stickynavlayout_listview);
		if (!(view instanceof ListView)) {
			throw new RuntimeException(
					"id_stickynavlayout_listview show used by ListView !");
		}
		mListView = (ListView) view;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		ViewGroup.LayoutParams params = mListView.getLayoutParams();
		params.height = getMeasuredHeight() - mNav.getMeasuredHeight()-CommonUtil.dip2px(getContext(), 60);
		mTop_lv_infoHeight = mTop_lv_info.getMeasuredHeight();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
//		mTopViewHeight = mTop_icon.getMeasuredHeight() + mTop_lv_infoHeight- dip2px(getContext(), 50);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		int action = ev.getAction();
		float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;
			getCurrentScrollView();

			if (mInnerScrollView instanceof ScrollView) {
				if (mInnerScrollView.getScrollY() == 0 && isTopHidden && dy > 0
						&& !isInControl) {
					isInControl = true;
					ev.setAction(MotionEvent.ACTION_CANCEL);
					MotionEvent ev2 = MotionEvent.obtain(ev);
					dispatchTouchEvent(ev);
					ev2.setAction(MotionEvent.ACTION_DOWN);
					return dispatchTouchEvent(ev2);
				}
			} else if (mInnerScrollView instanceof ListView) {

				ListView lv = (ListView) mInnerScrollView;
				View c = lv.getChildAt(lv.getFirstVisiblePosition());

				if (!isInControl && c != null && c.getTop() == 0 && isTopHidden
						&& dy > 0) {
					isInControl = true;
					ev.setAction(MotionEvent.ACTION_CANCEL);
					MotionEvent ev2 = MotionEvent.obtain(ev);
					dispatchTouchEvent(ev);
					ev2.setAction(MotionEvent.ACTION_DOWN);
					return dispatchTouchEvent(ev2);
				}
			} else if (mInnerScrollView instanceof RecyclerView) {

				RecyclerView rv = (RecyclerView) mInnerScrollView;

				if (!isInControl
						&& android.support.v4.view.ViewCompat
								.canScrollVertically(rv, -1) && isTopHidden
						&& dy > 0) {
					isInControl = true;
					ev.setAction(MotionEvent.ACTION_CANCEL);
					MotionEvent ev2 = MotionEvent.obtain(ev);
					dispatchTouchEvent(ev);
					ev2.setAction(MotionEvent.ACTION_DOWN);
					return dispatchTouchEvent(ev2);
				}
			}
			break;
		}
		return super.dispatchTouchEvent(ev);
	}

	/**
	 * 
	 */
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastY = y;
			break;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;
			getCurrentScrollView();
			if (Math.abs(dy) > mTouchSlop) {
				mDragging = true;
				if (mInnerScrollView instanceof ScrollView) {
					// 如果topView没有隐藏
					// 或sc的scrollY = 0 && topView隐藏 && 下拉，则拦截
					if (!isTopHidden
							|| (mInnerScrollView.getScrollY() == 0
									&& isTopHidden && dy > 0)) {

						initVelocityTrackerIfNotExists();
						mVelocityTracker.addMovement(ev);
						mLastY = y;
						return true;
					}
				} else if (mInnerScrollView instanceof ListView) {

					ListView lv = (ListView) mInnerScrollView;
					View c = lv.getChildAt(lv.getFirstVisiblePosition());
					// 如果topView没有隐藏
					// 或sc的listView在顶部 && topView隐藏 && 下拉，则拦截

					if (!isTopHidden || //
							(c != null //
									&& c.getTop() == 0//
									&& isTopHidden && dy > 0)) {

						initVelocityTrackerIfNotExists();
						mVelocityTracker.addMovement(ev);
						mLastY = y;
						return true;
					}
				} else if (mInnerScrollView instanceof RecyclerView) {
					RecyclerView rv = (RecyclerView) mInnerScrollView;
					if (!isTopHidden
							|| (!android.support.v4.view.ViewCompat
									.canScrollVertically(rv, -1) && isTopHidden && dy > 0)) {
						initVelocityTrackerIfNotExists();
						mVelocityTracker.addMovement(ev);
						mLastY = y;
						return true;
					}
				}

			}
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			mDragging = false;
			recycleVelocityTracker();
			break;
		}
		return super.onInterceptTouchEvent(ev);
	}

	private void getCurrentScrollView() {
		
		mInnerScrollView = (ViewGroup) (findViewById(R.id.id_stickynavlayout_listview));

//		int currentItem = mViewPager.getCurrentItem();
//		PagerAdapter a = mViewPager.getAdapter();
//		if (a instanceof FragmentPagerAdapter) {
//			FragmentPagerAdapter fadapter = (FragmentPagerAdapter) a;
//			Fragment item = (Fragment) fadapter.instantiateItem(mViewPager,
//					currentItem);
//			mInnerScrollView = (ViewGroup) (item.getView()
//					.findViewById(R.id.id_stickynavlayout_innerscrollview));
//		} else if (a instanceof FragmentStatePagerAdapter) {
//			FragmentStatePagerAdapter fsAdapter = (FragmentStatePagerAdapter) a;
//			Fragment item = (Fragment) fsAdapter.instantiateItem(mViewPager,
//					currentItem);
//			mInnerScrollView = (ViewGroup) (item.getView()
//					.findViewById(R.id.id_stickynavlayout_innerscrollview));
//		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		initVelocityTrackerIfNotExists();
		mVelocityTracker.addMovement(event);
		int action = event.getAction();
		float y = event.getY();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (!mScroller.isFinished())
				mScroller.abortAnimation();
			mLastY = y;
			return true;
		case MotionEvent.ACTION_MOVE:
			float dy = y - mLastY;

			Log.e("TAG", "dy = " + dy + " , y = " + y + " , mLastY = " + mLastY);

			if (!mDragging && Math.abs(dy) > mTouchSlop) {
				mDragging = true;
			}
			if (mDragging) {
				scrollBy(0, (int) -dy);

				// 如果topView隐藏，且上滑动时，则改变当前事件为ACTION_DOWN
				//动态计算头部布局高度
				mTopViewHeight = mTop_icon.getMeasuredHeight() + mTop_lv_infoHeight- dip2px(getContext(), 40);
				if (getScrollY() == mTopViewHeight && dy < 0) {
					event.setAction(MotionEvent.ACTION_DOWN);
					dispatchTouchEvent(event);
					isInControl = false;
				}
			}

			mLastY = y;

			break;
		case MotionEvent.ACTION_CANCEL:
			mDragging = false;
			recycleVelocityTracker();
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_UP:
			mDragging = false;
			mVelocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
			int velocityY = (int) mVelocityTracker.getYVelocity();
			if (Math.abs(velocityY) > mMinimumVelocity) {
				fling(-velocityY);
			}
			recycleVelocityTracker();
			break;
		}

		return super.onTouchEvent(event);
	}

	public void fling(int velocityY) {
		mScroller.fling(0, getScrollY(), 0, velocityY, 0, 0, 0, mTopViewHeight);
		invalidate();
	}

	@Override
	public void scrollTo(int x, int y) {
		if (y < 0) {
			y = 0;
		}
		if (y > mTopViewHeight) {
			y = mTopViewHeight;
		}
		if (y != getScrollY()) {
			super.scrollTo(x, y);
		}
		isTopHidden = getScrollY() == mTopViewHeight;
		myOnScrollListener.onScroll();
	}

	/**
	 * 返回top部分view是否隐藏
	 * 
	 * @return
	 */
	public boolean getIsTopHidden() {

		return isTopHidden;
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(0, mScroller.getCurrY());
			invalidate();
		}
	}

	private void initVelocityTrackerIfNotExists() {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
	}

	private void recycleVelocityTracker() {
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	public float getTopShowScale() {
		topShowScale = (float) ((float) (mTopViewHeight - getScrollY()) / mTopViewHeight);
		return topShowScale;
	}

	
	 //设置自定义监听滑动回调
	 public void setMyOnScrollListener(MyOnScrollListener myOnScrollListener) {
		this.myOnScrollListener = myOnScrollListener;
	}

	 
	private MyOnScrollListener myOnScrollListener;
	//自定义滑动监听回调类
	 public interface MyOnScrollListener{
		 public void onScroll();
	 }
}
