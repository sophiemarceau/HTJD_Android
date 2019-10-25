package com.huatuo.custom_widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

/**圆角图片
 * @author Android开发工程师 wrz
 *
 */
public class RoundCornerImageView extends ImageView {
	public RoundCornerImageView(Context context) {
		super(context);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public RoundCornerImageView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Path clipPath = new Path();
		int w = this.getWidth();
		int h = this.getHeight();
		clipPath.addRoundRect(new RectF(0, 0, w, h), 5.0f, 5.0f,
				Path.Direction.CW);
		canvas.clipPath(clipPath);
		super.onDraw(canvas);
	}
}