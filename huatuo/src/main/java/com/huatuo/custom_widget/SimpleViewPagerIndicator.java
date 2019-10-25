package com.huatuo.custom_widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SimpleViewPagerIndicator extends LinearLayout {

	private static final int COLOR_TEXT_NORMAL = 0xFF282828;
	private static final int COLOR_INDICATOR_COLOR = 0xFFD83219;

	private String[] mTitles;
	private int mTabCount;
//	/**
//	 * 导航条的最大宽度
//	 */
//	private final int DIMENSION_TRIANGEL_WIDTH = ;

	private int mIndicatorColor = COLOR_INDICATOR_COLOR;
	private float mTranslationX;
	private Paint mPaint = new Paint();
	private int mTabWidth;
	private int mTabHeight = 2;
	/**
	 * 初始时，指示器的偏移量
	 */
//	private int mInitTranslationX;
	public SimpleViewPagerIndicator(Context context) {
		this(context, null);
	}

	public SimpleViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint.setColor(mIndicatorColor);
		mPaint.setStrokeWidth(dip2px(getContext(),mTabHeight));
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		mTabWidth = w / mTabCount;
//		mTabWidth = Math.min(dip2px(getContext(),30), mTabWidth);
//		
//		// 初始时的偏移量
//				mInitTranslationX = getWidth() / mTabCount / 2 - mTabWidth
//						/ 2;
	}
	public void setTitles(String[] titles)
	{
		mTitles = titles;
		mTabCount = titles.length;
		generateTitleView();
		setItemClickEvent();

	}

	public void setIndicatorColor(int indicatorColor) {
		this.mIndicatorColor = indicatorColor;
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		canvas.save();
		canvas.translate(mTranslationX, getHeight() - 2);
		canvas.drawLine(0, 0, mTabWidth, 0, mPaint);
		canvas.restore();
	}

	public void scroll(int position, float offset) {
		/**
		 * <pre>
		 *  0-1:position=0 ;1-0:postion=0;
		 * </pre>
		 */
		mTranslationX = getWidth() / mTabCount * (position + offset);
		invalidate();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return super.dispatchTouchEvent(ev);
	}

	private void generateTitleView() {
		if (getChildCount() > 0)
			this.removeAllViews();
		int count = mTitles.length;

		setWeightSum(count);
		for (int i = 0; i < count; i++) {
			TextView tv = new TextView(getContext());
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0,
					LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			tv.setGravity(Gravity.CENTER);
			tv.setTextColor(COLOR_TEXT_NORMAL);
			tv.setText(mTitles[i]);
			tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
			tv.setLayoutParams(lp);
			addView(tv);
		}
	}

	/**
	 * 对外的ViewPager的回调接口
	 * 
	 * 
	 */
	public interface PageChangeListener {
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		public void onPageSelected(int position);

		public void onPageScrollStateChanged(int state);
	}

	// 对外的ViewPager的回调接口
	private PageChangeListener onPageChangeListener;
	/**
	 * 与之绑定的ViewPager
	 */
	public ViewPager mViewPager;

	// 对外的ViewPager的回调接口的设置
	public void setOnPageChangeListener(PageChangeListener pageChangeListener) {
		this.onPageChangeListener = pageChangeListener;
	}

	// 设置关联的ViewPager
	public void setViewPager(ViewPager mViewPager, int pos) {
		this.mViewPager = mViewPager;

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				// 设置字体颜色高亮
				resetTextViewColor();
				setCurrentTextViewColor(position);

				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageSelected(position);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
				// 滚动
				scroll(position, positionOffset);

				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrolled(position,
							positionOffset, positionOffsetPixels);
				}

			}

			@Override
			public void onPageScrollStateChanged(int state) {
				// 回调
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrollStateChanged(state);
				}

			}
		});
		// 设置当前页
		mViewPager.setCurrentItem(pos);
		// 高亮
		setCurrentTextViewColor(pos);
	}

	/**
	 * 重置文本颜色
	 */
	private void resetTextViewColor() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof TextView) {
				((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
			}
		}
	}
	/**
	 * 文本
	 * 
	 * @param position
	 */
	protected void setCurrentTextViewColor(int position)
	{
		View view = getChildAt(position);
		if (view instanceof TextView)
		{
			((TextView) view).setTextColor(COLOR_INDICATOR_COLOR);
		}

	}
	/**
	 * 设置点击事件
	 */
	public void setItemClickEvent() {
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			final int j = i;
			View view = getChildAt(i);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(j);
				}
			});
		}
	}
	
	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

}
