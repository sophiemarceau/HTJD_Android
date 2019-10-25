package com.huatuo.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.huatuo.R;
import com.huatuo.base.BaseNetFragment;

public class FragmentQiYe extends BaseNetFragment implements OnPageChangeListener{
	private WebView webView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_qiye, null);
		webView = (WebView) view.findViewById(R.id.webView);
		webView.loadUrl("http://www.mikecrm.com/f.php?t=HmqkNZ");
		WebSettings setting = webView.getSettings();  
		setting.setJavaScriptEnabled(true);
 		return view;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}

}
