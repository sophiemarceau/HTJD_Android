package com.huatuo.custom_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

public class CustomHorizontalScrollView extends HorizontalScrollView {

	private View mView;

	public CustomHorizontalScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		if (mView != null) {
			mView.scrollTo(l, t);
		}
	}

	public void setScrollView(View view) {
		mView = view;
	}
}