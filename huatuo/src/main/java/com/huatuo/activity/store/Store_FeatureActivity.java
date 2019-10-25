package com.huatuo.activity.store;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.ObservableScrollView;
import com.huatuo.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

public class Store_FeatureActivity extends BaseActivity implements
		OnClickListener {
	private Context mContext;

	private ObservableScrollView scrollView1;
	private ImageView iv_top;
	private TextView tv_name;
	private LinearLayout ll_back;
	private WebView wv_find_list_textImg;

	private String image = "";

	private String store_feature = "";

	private RelativeLayout rl_loadData_error, rl_loadData_empty;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		setContentView(R.layout.activity_store_feature_text_image_list);
		findViewById();
		receiveExtras();
		setOnClickListener();
	}

	/***
	 * 将activity 的创建模式设置为singletask,
	 * 使用这个方法可以再其他activity想这个activity发送Intent时，这个Intent能够及时更新
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		CommonUtil
				.logE("--------------------------------------------onNewIntent-----------------------------");
		super.onNewIntent(intent);
		setIntent(intent); // 这一句必须的，否则Intent无法获得最新的数据
		receiveExtras();
	}

	private void receiveExtras() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			store_feature = bundle.getString("store_feature");
			image = bundle.getString("image");
			CommonUtil.log("store_feature" + store_feature);
			setData();
		}
	}

	private void setData() {
		tv_name.setText("门店特色");
		wv_find_list_textImg.loadDataWithBaseURL(null, store_feature,
				"text/html", "utf-8", null);
	}

	private void findViewById() {
		scrollView1 = (ObservableScrollView) findViewById(R.id.scrollView1);
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);


		iv_top = (ImageView) findViewById(R.id.iv_top);
		tv_name = (TextView) findViewById(R.id.tv_name);

		iv_top.setFocusable(true);
		iv_top.setFocusableInTouchMode(true);
		iv_top.requestFocus();
		wv_find_list_textImg = (WebView) findViewById(R.id.wv_find_list_textImg);
		WebSettings webSettings = wv_find_list_textImg.getSettings();
		webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		// webSettings.setJavaScriptEnabled(true);
		// wv_find_list_textImg.setWebViewClient(new WebViewClient());
		// wv_find_list_textImg.setWebChromeClient(new WebChromeClient());

		// 加载数据失败页面
		rl_loadData_error = (RelativeLayout) findViewById(R.id.rl_loadData_error);
		rl_loadData_error.setOnClickListener(this);
		// 加载数据前的白界面
		rl_loadData_empty = (RelativeLayout) findViewById(R.id.rl_loadData_empty);

	}

	private void setOnClickListener() {
		bindListener();
		scrollListener();
	}

	private void scrollListener() {
		scrollView1
				.setScrollViewListener(new com.huatuo.custom_widget.ScrollViewListener() {

					@SuppressLint("NewApi")
					@Override
					public void onScrollChanged(
							ObservableScrollView scrollView, int x, int y,
							int oldx, int oldy) {
						if (0 != y && 0 != oldy) {
							float alpha = (float) 2 * y/ 150f;
							iv_top.setAlpha(alpha);
							tv_name.setAlpha(alpha);
						}
						ll_back.setBackgroundColor(Color.argb(0, 0, 0, 0));

					}
				});

		scrollView1
				.setOnBorderListener(new com.huatuo.custom_widget.OnBorderListener() {

					@SuppressLint("NewApi")
					@Override
					public void onTop() {
						// TODO Auto-generated method stub
						ll_back.setBackgroundResource(R.drawable.icon_bg);
					}

					@SuppressLint("NewApi")
					@Override
					public void onBottom() {
					}
				});

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_loadData_error:// 重新加载数据
			break;
		}
	}

}
