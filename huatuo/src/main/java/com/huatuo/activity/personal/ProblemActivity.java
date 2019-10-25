package com.huatuo.activity.personal;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.huatuo.R;
import com.huatuo.base.BaseNetActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.custom_widget.ProgressWebView;

/* 我的华佗 -> 关于华佗 */
public class ProblemActivity extends BaseNetActivity {
	private LinearLayout ll_back;
	private ProgressWebView webView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_problem);
		MyApplication.getInstance().addActivity(this);
		mContext = this;
		// mHandler = new MyHandler1();
		initWidget();
		bindListener();
		init();
	}

	private void initWidget() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		webView = (ProgressWebView) findViewById(R.id.webView);
	}

	protected void bindListener() {
		MyOnClickListener myOnClickListener = new MyOnClickListener();
		ll_back.setOnClickListener(myOnClickListener);

	}

	class MyOnClickListener implements OnClickListener {
		@Override
		public void onClick(View view) {

			switch (view.getId()) {
			case R.id.ll_back1:
				finish();
				break;
			}
		}
	}

	private void init() {
		
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());  
		// WebView加载web资源
		webView.loadUrl("http://wechat.huatuojiadao.com/weixin_user/html/wechat.html#user-help");
		// webView.loadUrl("http://3g.sina.com");
	}
}
