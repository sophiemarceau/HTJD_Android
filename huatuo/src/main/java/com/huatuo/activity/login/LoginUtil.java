package com.huatuo.activity.login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.huatuo.base.MyApplication;
import com.huatuo.interfaces.IHandler;

public class LoginUtil {// 登录
	public static Context mContext;
    public static Intent intent ;
    public static Dialog dialog;
	//登录成功后的回调
	public static IHandler iHandlerLogin;
	// 是否已登录
	public static final void IsLogin(Context context  ,IHandler iHandler ){
		mContext = context;
		iHandlerLogin = iHandler;
		//已登录
		if(MyApplication.getLoginFlag()){
			iHandlerLogin.doHandler();
		//未登录，去登录
		}else {
			intent = new Intent();
			intent.setClass( mContext, LoginActivity.class );
			Bundle bundle = new Bundle();
			bundle.putBoolean("ISCALLBACK", true);
			intent.putExtras(bundle);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			mContext.startActivity(intent);
		}
	}
	
}