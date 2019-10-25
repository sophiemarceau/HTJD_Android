package com.huatuo.citylist.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.citylist.MyAdapter;

public class SideBar extends View {
	private char[] l;
	private SectionIndexer sectionIndexter = null;
	private ListView list;
	private TextView mDialogText;
	Bitmap mbitmap;
	private int type = 1;
	private int color = 0xffffffff;

	public SideBar(Context context) {
		super(context);
		init();
	}

	public SideBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {

		l = new char[] { '#','A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
				'W', 'X', 'Y', 'Z' };
		mbitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.scroll_bar_search_icon);
//		setBackgroundDrawable(new ColorDrawable(0xff444444));
		setBackgroundResource(R.drawable.city_list_sidear_bg);
	}

	public SideBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void setListView(ListView _list) {
		list = _list;
		 HeaderViewListAdapter ha = (HeaderViewListAdapter) _list
		 .getAdapter();
		 MyAdapter ad = (MyAdapter)ha.getWrappedAdapter();
//		MyAdapter ad = (MyAdapter) _list.getAdapter();
		sectionIndexter = (SectionIndexer) ad;

	}

	public void setTextView(TextView mDialogText) {
		this.mDialogText = mDialogText;
	}

	public boolean onTouchEvent(MotionEvent event) {

		super.onTouchEvent(event);
		int i = (int) event.getY();

		int idx = i / (getMeasuredHeight() / (l.length+1));
		if (idx >= l.length) {
			idx = l.length - 1;
		} else if (idx < 0) {
			idx = 0;
		}
		if (event.getAction() == MotionEvent.ACTION_DOWN
				|| event.getAction() == MotionEvent.ACTION_MOVE) {
//			setBackgroundResource(R.drawable.city_list_sidear_bg);
			mDialogText.setVisibility(View.VISIBLE);
			if (idx == 0) {
				mDialogText.setText("#");
				mDialogText.setTextSize(34);
			} else {
				mDialogText.setText(String.valueOf(l[idx]));
				mDialogText.setTextSize(34);
			}
			if (sectionIndexter == null) {

				sectionIndexter = (SectionIndexer) list.getAdapter();

			}
			
			int position = sectionIndexter.getPositionForSection(l[idx]);

			if (position == -1) {
				return true;
			}
			list.setSelection(position);
		} else {
			mDialogText.setVisibility(View.INVISIBLE);

		}
		if (event.getAction() == MotionEvent.ACTION_UP) {
//			setBackgroundDrawable(new ColorDrawable(0xff444444));
		}
		return true;
	}

	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setTextSize(40);
		paint.setStyle(Style.FILL);
		paint.setTextAlign(Paint.Align.CENTER);
		float widthCenter = getMeasuredWidth() / 2;
		if (l.length > 0) {
			float height = getMeasuredHeight() / (l.length+1);
			for (int i = 0; i < l.length; i++) {
//				if (i == 0 && type != 2) {
//					canvas.drawBitmap(mbitmap, widthCenter - 7, (i + 1)
//							* height - height / 2, paint);
//				} else
					canvas.drawText(String.valueOf(l[i]), widthCenter, (i + 1)
							* height, paint);
			}
		}
		this.invalidate();
		super.onDraw(canvas);
	}
}
