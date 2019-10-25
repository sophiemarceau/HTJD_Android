package com.huatuo.custom_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ScrollView;

public class ObservableScrollView extends ScrollView {

	private ScrollViewListener scrollViewListener = null;
	private OnBorderListener onBorderListener = null;

	public ObservableScrollView(Context context) {
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}
	
	public void setOnBorderListener(OnBorderListener onBorderListener) {
		this.onBorderListener = onBorderListener;
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldx, int oldy) {
		super.onScrollChanged(x, y, oldx, oldy);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
		}
		doOnBorderListener();
	}

	private void doOnBorderListener() {
		if (getChildAt(0) != null && getChildAt(0).getMeasuredHeight() <= getScrollY() + getHeight()) {
			Log.e("ObservableScrollView", getChildAt(0).getMeasuredHeight()+"---"+getScrollY()+"---"+getHeight());
			if (onBorderListener != null) {
				onBorderListener.onBottom();
			}
		} else if (getChildAt(0) != null && getScrollY() == 0) {
			
			if (onBorderListener != null) {
				Log.e("ObservableScrollView_top", getChildAt(0).getMeasuredHeight()+"---"+getScrollY()+"---"+getHeight());
				onBorderListener.onTop();
			}
		}
	}

}
