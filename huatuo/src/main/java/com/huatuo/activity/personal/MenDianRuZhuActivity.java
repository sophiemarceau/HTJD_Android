package com.huatuo.activity.personal;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.huatuo.R;
import com.huatuo.base.BaseActivity;
import com.huatuo.custom_widget.ProgressWebView;

public class MenDianRuZhuActivity extends BaseActivity {
	private ProgressWebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_qiye);
		bindListener();
		webView = (ProgressWebView) findViewById(R.id.webView);
		// 覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
		webView.setWebViewClient(new WebViewClient());  
		webView.loadUrl("http://wechat.huatuojiadao.com/weixin_user/html/wechat.html#user-apply");
		WebSettings setting = webView.getSettings();  
		setting.setJavaScriptEnabled(true);
	}

}
