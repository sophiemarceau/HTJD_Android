package com.huatuo.activity.personal;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;

import com.huatuo.R;
import com.huatuo.base.BaseActivity;
import com.huatuo.custom_widget.ProgressWebView;

public class YongHuXieYiActivity extends BaseActivity {
	private ProgressWebView webView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yonghuxieyi);
		bindListener();
		webView = (ProgressWebView) findViewById(R.id.webView);
		webView.loadUrl("http://wechat.huatuojiadao.com/weixin_user/html/wechat.html#user-protocol");
		WebSettings setting = webView.getSettings(); 

		setting.setJavaScriptEnabled(true);
		webView.setWebViewClient(new WebViewClient());  
		
		
	}
}
