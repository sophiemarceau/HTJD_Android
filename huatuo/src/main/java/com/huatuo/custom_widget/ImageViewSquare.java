package com.huatuo.custom_widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class ImageViewSquare extends ImageView {
	public ImageViewSquare(Context paramContext) {
		super(paramContext);
	}

	public ImageViewSquare(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public ImageViewSquare(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	protected void onMeasure(int paramInt1, int paramInt2) {
		int i = getDefaultSize(getSuggestedMinimumWidth(), paramInt1);
		setMeasuredDimension(i, i);
	}

	protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		super.onSizeChanged(paramInt1, paramInt1, paramInt3, paramInt4);
	}
}