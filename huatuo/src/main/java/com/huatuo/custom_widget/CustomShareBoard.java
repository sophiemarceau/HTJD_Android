/**
 * 
 */

package com.huatuo.custom_widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.bean.ShareObj;
import com.huatuo.custom_widget.StickyNavLayout.MyOnScrollListener;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Toast_Util;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 
 */
public class CustomShareBoard extends PopupWindow implements OnClickListener {

	private Activity mActivity;
	private ShareObj mShareObj;

	public CustomShareBoard(Activity activity, ShareObj shareObj) {
		super(activity);
		this.mActivity = activity;
		mShareObj = shareObj;
		initView(activity);
	}

	@SuppressWarnings("deprecation")
	private void initView(Context context) {
		View rootView = LayoutInflater.from(context).inflate(
				R.layout.custom_board, null);
		// 设置点击事件
		rootView.findViewById(R.id.rl_wechat).setOnClickListener(this);
		rootView.findViewById(R.id.rl_circle).setOnClickListener(this);
		rootView.findViewById(R.id.rl_sina).setOnClickListener(this);
		rootView.findViewById(R.id.rl_ctrl_c).setOnClickListener(this);
		rootView.findViewById(R.id.share_cancel).setOnClickListener(this);

		setContentView(rootView);
		setWidth(LayoutParams.MATCH_PARENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0x000000);
		setBackgroundDrawable(dw);
		// setBackgroundDrawable(new BitmapDrawable());
		setAnimationStyle(R.style.mypopwindow_anim_shareboard_style1);
		// 设置activity 背景颜色变灰
		backgroundAlpha(0.3f);
		setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stubs
				backgroundAlpha(1f);
			}
		});

		setTouchable(true);
	}

	/**
	 * 设置添加屏幕的背景透明度
	 * 
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha) {
		WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
		lp.alpha = bgAlpha; // 0.0-1.0
		mActivity.getWindow().setAttributes(lp);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.share_cancel:
			dismiss();
			break;
		case R.id.rl_wechat:
//			myOnShareListener.onWeiXin();
			new ShareAction(mActivity).setPlatform(SHARE_MEDIA.WEIXIN)
					.setCallback(umShareListener)
					.withMedia(mShareObj.getImage())
					.withTitle(mShareObj.getTitle())
					.withText(mShareObj.getContent())
					.withTargetUrl(mShareObj.getTargetUrl()).share();
			dismiss();
			break;
		case R.id.rl_circle:
//			myOnShareListener.onWeixin_circle();
			new ShareAction(mActivity).setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
					.setCallback(umShareListener)
					.withMedia(mShareObj.getImage())
					.withTitle(mShareObj.getTitle())
					.withText(mShareObj.getContent())
					.withTargetUrl(mShareObj.getTargetUrl()).share();
			dismiss();
			break;
		case R.id.rl_ctrl_c:
			CommonUtil.copy(mActivity, mShareObj.getTargetUrl());
			Toast_Util.showToast(mActivity, "链接已复制到粘贴板");
			dismiss();
			break;
		}
	}

	private UMShareListener umShareListener = new UMShareListener() {
		@Override
		public void onResult(SHARE_MEDIA platform) {
//			Toast.makeText(mActivity, platform + " 分享成功啦", Toast.LENGTH_SHORT)
//					.show();
		}

		@Override
		public void onError(SHARE_MEDIA platform, Throwable t) {
			Toast.makeText(mActivity, platform + " 分享失败啦", Toast.LENGTH_SHORT)
					.show();
		}

		@Override
		public void onCancel(SHARE_MEDIA platform) {
			Toast.makeText(mActivity, platform + " 分享取消了", Toast.LENGTH_SHORT)
					.show();
		}
	};

	// 设置自定义监听滑动回调
	public void setOnShareListener(OnShareListener onShareListener) {
		this.myOnShareListener = onShareListener;
	}

	private OnShareListener myOnShareListener;

	// 自定义滑动监听回调类
	public interface OnShareListener {
		public void onWeiXin();
		public void onWeixin_circle();
	}
}
