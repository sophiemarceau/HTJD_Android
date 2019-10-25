package com.huatuo.custom_widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.huatuo.R;

/**
 * 
 * @author Android开发工程师
 * 
 */
public class CustomEdittext extends EditText implements
		View.OnFocusChangeListener, View.OnKeyListener {
	private static final String TAG = CustomEdittext.class.getSimpleName();
	/**
	 * 是否是默认图标再左边的样式
	 */
	private boolean isLeft = false;
	/**
	 * 是否点击软键盘搜索
	 */
	private boolean pressSearch = false;
	/**
	 * 软键盘搜索键监听
	 */
	private OnSearchClickListener listener;

	/**
	 * 
	 * 删除按钮的引用
	 */

	private Drawable mClearDrawable;
	/**
	 * 
	 * 控件是否有焦点
	 */

	private boolean hasFoucs;

	public void setOnSearchClickListener(OnSearchClickListener listener) {
		this.listener = listener;
	}

	public CustomEdittext(Context context) {
		this(context, null);
		init();
		initClear();
	}

	public CustomEdittext(Context context, AttributeSet attrs) {
		this(context, attrs, android.R.attr.editTextStyle);
		init();
		initClear();
	}

	public CustomEdittext(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
		initClear();
	}

	private void init() {
		setOnFocusChangeListener(this);
		// setOnKeyListener(this);
	}

	private void initClear() {

		// 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片

		mClearDrawable = getCompoundDrawables()[2];

		if (mClearDrawable == null) {

			mClearDrawable = getResources().getDrawable(
					R.drawable.icon_quxiao_3x);

		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(),
				mClearDrawable.getIntrinsicHeight());

		// 默认设置隐藏图标

		setClearIconVisible(false);

		// 设置焦点改变的监听
		setOnFocusChangeListener(this);
		// 设置输入框里面内容发生改变的监听

		addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (hasFoucs) {

					setClearIconVisible(s.length() > 0);

				} else {
					if (s.length() == 0) {
						isLeft = false;
					} else {
						isLeft = true;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});

	}

	/**
	 * 
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * 
	 * @param visible
	 */

	protected void setClearIconVisible(boolean visible) {

		Drawable right = visible ? mClearDrawable : null;

		setCompoundDrawables(getCompoundDrawables()[0],

		getCompoundDrawables()[1], right, getCompoundDrawables()[3]);

	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isLeft) { // 如果是默认样式，则直接绘制
			super.onDraw(canvas);
		} else { // 如果不是默认样式，需要将图标绘制在中间
			Drawable[] drawables = getCompoundDrawables();
			if (drawables != null) {
				Drawable drawableLeft = drawables[0];
				if (drawableLeft != null) {
					float textWidth = getPaint().measureText(
							getHint().toString());
					int drawablePadding = getCompoundDrawablePadding();
					int drawableWidth = drawableLeft.getIntrinsicWidth();
					float bodyWidth = textWidth + drawableWidth
							+ drawablePadding;
					canvas.translate(
							(getWidth() - bodyWidth - getPaddingLeft() - getPaddingRight()) / 2,
							0);
				}
			}
			super.onDraw(canvas);
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		// Log.d(TAG, "onFocusChange execute");
		// 恢复EditText默认的样式
		if (!pressSearch && TextUtils.isEmpty(getText().toString())) {
			isLeft = hasFocus;
		}

		// if(!TextUtils.isEmpty(getText().toString())){
		// if(getText().toString().length() > 0){
		// isLeft = true;
		// }
		// }else {
		// isLeft = false;
		// }

		// 删除按钮
		this.hasFoucs = hasFocus;

		if (hasFocus) {

			setClearIconVisible(getText().length() > 0);

		} else {

			setClearIconVisible(false);

		}

	}

	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		pressSearch = (keyCode == KeyEvent.KEYCODE_ENTER);
		if (pressSearch && listener != null) {
			// 隐藏软键盘
			InputMethodManager imm = (InputMethodManager) v.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
			}
			listener.onSearchClick(v);
		}
		return false;
	}

	public interface OnSearchClickListener {
		void onSearchClick(View view);
	}

	/**
	 * 
	 * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件
	 * 
	 * 当我们按下的位置 在 EditText的宽度 - 图标到控件右边的间距 - 图标的宽度 和
	 * 
	 * EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
	 */
	/**
	 * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
	 * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));

				if (touchable) {
					this.setText("");
				}
			}
		}

		return super.onTouchEvent(event);
	}
}
