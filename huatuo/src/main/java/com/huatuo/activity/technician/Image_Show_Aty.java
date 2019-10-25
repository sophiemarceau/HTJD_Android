package com.huatuo.activity.technician;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.huatuo.R;
import com.huatuo.base.BaseActivity;
import com.huatuo.base.MyApplication;
import com.huatuo.util.CommonUtil;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class Image_Show_Aty extends BaseActivity{
	private Context mContext;
	private int type;
	private TextView tv_title;
	private ImageView image_icon;
	private ProgressBar loading;
	private FrameLayout fl_icon;
	private String url,name;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MyApplication.getInstance().addActivity(this);
		setContentView(R.layout.image_viewpager_activity);
		initView();
		receiveData();
		setDataToView();
	}
	private void initView(){
		tv_title = (TextView) findViewById(R.id.tv_title);
		fl_icon = (FrameLayout) findViewById(R.id.fl_icon);
		image_icon = (ImageView) findViewById(R.id.image_icon);
		loading = (ProgressBar) findViewById(R.id.loading);
		findViewById(R.id.ll_back1).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		setImgSize();
	}
	private void setImgSize(){
		//设置图片的为正方形
		CommonUtil.initScreen(this);
		LayoutParams layoutParams = image_icon.getLayoutParams();
		layoutParams.width = CommonUtil.WIDTH_SCREEN;
		layoutParams.height = CommonUtil.WIDTH_SCREEN;
		image_icon.setLayoutParams(layoutParams);
	}
	private void receiveData() {
		Bundle bundle = getIntent().getExtras();
		if (null != bundle) {
			url = bundle.getString("url", "");
			name = bundle.getString("name", "");
		}
	}
	
	private void setDataToView(){
		tv_title.setText(name);
		ImageLoader.getInstance().displayImage(url, image_icon,
				new SimpleImageLoadingListener() {
					@Override
					public void onLoadingStarted(String imageUri, View view) {
						loading.setVisibility(View.VISIBLE);
					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						loading.setVisibility(View.GONE);
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						loading.setVisibility(View.GONE);
					}
				});
		fl_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(0,0);  
			}
		});

	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		overridePendingTransition(0,0);  
		finish();
	}

}
