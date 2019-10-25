package com.huatuo.zxing;

import java.io.IOException;
import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.huatuo.R;
import com.huatuo.activity.ad.AD_ServiceListActivity;
import com.huatuo.activity.ad.AD_StoreListActivity;
import com.huatuo.activity.ad.AD_TechListActivity;
import com.huatuo.activity.project.ProjectDetailActivity;
import com.huatuo.activity.store.StoreDetail_Aty;
import com.huatuo.activity.technician.TechnicianDetail;
import com.huatuo.base.MyApplication;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.Toast_Util;

public class CaptureActivity extends Activity implements Callback, OnClickListener {

	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private ImageView iv_light;
	private boolean playBeep;
	private boolean isLight;
	private static final float BEEP_VOLUME = 0.10f;
	private boolean vibrate;
	private WindowManager windowManager;
	private int sw;
	private int sh;
	private LinearLayout ll_back;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.zxing_qr_code_scan);
		MyApplication.getInstance().addActivity(this);
		initCaptureSize();
		CameraManager.init(getApplication());
		findViewById();
		hasSurface = false;
		isLight = false;
		inactivityTimer = new InactivityTimer(this);
	}

	private void findViewById() {
		ll_back = (LinearLayout) findViewById(R.id.ll_back1);
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		iv_light = (ImageView) findViewById(R.id.iv_light);
		// create_host = (ImageView) findViewById(R.id.create_host);
		iv_light.setOnClickListener(this);
		// create_host.setOnClickListener(this);
		ll_back.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}

	@Override
	public void onDestroy() {
		inactivityTimer.shutdown();
		super.onDestroy();
	}

	/**
	 * 扫描结果
	 */
	public void handleDecode(Result result, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();

		String resultString = result.getText();
		CommonUtil.logE("resultString------------->" + resultString);
		if (resultString.indexOf("huatuojiadao") != -1) {
			Log.e("handleDecode", "是华佗驾到二维码");
			Log.d("URL:", resultString);
			if (!resultString.isEmpty()) {
				String R = "";
				int k = resultString.length();
				for (int i = 0; i < resultString.length(); i++) {
					if (resultString.substring(i, i + 1).equals("#")) {
						R = resultString.substring(i + 1, k).trim();
					}
				}
				String type = "";
				String L = "";
				int l = R.length();
				for (int i = 0; i < R.length(); i++) {
					if (R.substring(i, i + 1).equals("?")) {
						type = R.substring(0, i).trim();
						L = R.substring(i + 1, l).trim();
					}
				}
				String id = "";
				l = L.length();
				for (int i = 0; i < L.length(); i++) {
					if (L.substring(i, i + 1).equals("=")) {
						id = L.substring(i + 1, l).trim();
					}
				}

				if ("shop-detail".equals(type)) {
					Intent intent = new Intent(CaptureActivity.this, StoreDetail_Aty.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					intent.putExtras(bundle);
					startActivity(intent);
				} else if ("project-detail".equals(type)) {
					Intent intent = new Intent(CaptureActivity.this, ProjectDetailActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					intent.putExtras(bundle);
					startActivity(intent);
				} else if ("pt-detail".equals(type)) {
					Intent intent = new Intent(CaptureActivity.this, TechnicianDetail.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					intent.putExtras(bundle);
					startActivity(intent);
				} else if ("shop-topic".equals(type)) {
					Intent intent = new Intent(CaptureActivity.this, AD_StoreListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					intent.putExtras(bundle);
					startActivity(intent);
				} else if ("project-topic".equals(type)) {
					Intent intent = new Intent(CaptureActivity.this, AD_ServiceListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					intent.putExtras(bundle);
					startActivity(intent);
				} else if ("pt-topic".equals(type)) {
					Intent intent = new Intent(CaptureActivity.this, AD_TechListActivity.class);
					Bundle bundle = new Bundle();
					bundle.putString("ID", id);
					intent.putExtras(bundle);
					startActivity(intent);
				} else {
					Toast_Util.showToastOfLONG(CaptureActivity.this, "二维码有误，请您扫描华佗驾到门店二维码");
				}
			} else {
				Toast_Util.showToastOfLONG(CaptureActivity.this, getResources().getString(R.string.scan_error_text));
			}
		} else {
			Log.e("handleDecode", "不是华佗驾到二维码");
		}
		CaptureActivity.this.finish();
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	/**
	 * 扫描正确后的震动声音,如果感觉apk大了,可以删除
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	// private ImageView create_host;

	/**
	 * 初始化扫描二维码尺寸
	 */
	private void initCaptureSize() {
		windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE); // 屏幕宽度
		sw = windowManager.getDefaultDisplay().getWidth();
		// 屏幕高度
		sh = windowManager.getDefaultDisplay().getHeight();
		CameraManager.MIN_FRAME_HEIGHT = sw / 7 * 4;
		CameraManager.MIN_FRAME_WIDTH = sw / 7 * 5;
		CameraManager.MAX_FRAME_HEIGHT = sw / 7 * 5;
		CameraManager.MAX_FRAME_WIDTH = sw / 7 * 6;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_light:
			if (isLight) {
				CameraManager.get().offLight();
				iv_light.setBackgroundResource(R.drawable.but_light_off);
				isLight = false;
			} else {
				CameraManager.get().openLight();
				iv_light.setBackgroundResource(R.drawable.but_light_on);
				isLight = true;
			}

			break;
		case R.id.ll_back1:// 返回
			if (isLight) {
				CameraManager.get().offLight();
			}
			finish();
			break;
		}

	}

}
