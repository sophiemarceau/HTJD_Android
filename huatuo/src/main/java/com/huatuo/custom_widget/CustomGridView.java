package com.huatuo.custom_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;
import android.widget.ListView;

public class CustomGridView extends GridView {// com.huatuo.custom_listview.CustomGridView
	private boolean haveScrollbar = true;

	public CustomGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 设置是否有ScrollBar，当要在ScollView中显示时，应当设置为false。 默认为 true
	 * 
	 * @param haveScrollbars
	 */
	public void setHaveScrollbar(boolean haveScrollbar) {
		this.haveScrollbar = haveScrollbar;
	}
	
	@Override
		public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction() == MotionEvent.ACTION_MOVE){
	           return true;//禁止Gridview进行滑动
	       }
			return super.onTouchEvent(ev);
		}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if (this.haveScrollbar == true) {
			int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
			super.onMeasure(widthMeasureSpec, expandSpec);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

}
