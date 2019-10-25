package com.huatuo.activity.guide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.activity.home.HomeActivity;
import com.huatuo.custom_widget.CustomDialog;
import com.huatuo.custom_widget.CustomDialogOfOneBtn;
import com.huatuo.util.CommonUtil;
import com.huatuo.util.LogUtil;


public class UpdateManager {
	private static final int DOWNLOAD = 1;
	private static final int DOWNLOAD_FINISH = 2;
	private String mSavePath;
	private int progress;
	private boolean cancelUpdate = false;

	private Context mContext;
	private ProgressBar mProgress;
	private Dialog mDownloadDialog;
	private String TAG = "UpdateManager";
	private Intent intent;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWNLOAD:
				mProgress.setProgress(progress);
				break;
			case DOWNLOAD_FINISH:
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	public void checkUpdate() {
		if (isUpdate()) {
			showNoticeDialog();
		} else {
			Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_LONG).show();
		}
	}

	public boolean isUpdate() {
		if ("0".equals(CommonUtil.STATUS)) {
			return false;
		} else if ("1".equals(CommonUtil.STATUS)) {
			return true;
		} else if ("2".equals(CommonUtil.STATUS)) {
			return true;
		}
		return false;
//		return true;
	}

	private void showNoticeDialog() {
		
		CustomDialog.Builder builder = new CustomDialog.Builder(mContext);
		builder.setTitle(mContext.getString(R.string.soft_update_title));
		builder.setMessage(mContext.getString(R.string.soft_update_info));
		builder.setPositiveButton(mContext.getString(R.string.soft_update_updatebtn),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						showDownloadDialog();
					}
				});

		builder.setNegativeButton(mContext.getString(R.string.soft_update_later),
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
//						new StartTask().execute();
					}
				});
		builder.create().show();
		
//		AlertDialog.Builder builder = new Builder(mContext);
//		builder.setTitle(R.string.soft_update_title);
//		builder.setMessage(R.string.soft_update_info);
//		builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				showDownloadDialog();
//			}
//		});
//		builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				new StartTask().execute();
//			}
//		});
//		Dialog noticeDialog = builder.create();
//		noticeDialog.show();
	}

	private void showDownloadDialog() {
		
		CustomDialogOfOneBtn.Builder builder = new CustomDialogOfOneBtn.Builder(mContext);
		builder.setTitle(mContext.getString(R.string.soft_updating));
		final LayoutInflater inflater = LayoutInflater.from(mContext);
		View v = inflater.inflate(R.layout.softupdate_progress, null);
		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
		builder.setContentView(v);
		builder.setPositiveButton(mContext.getString(R.string.soft_update_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						// 设置你的操作事项
						cancelUpdate = true;
					}
				});
		mDownloadDialog = builder.create();
		mDownloadDialog.show();
		downloadApk();
		
		
//		AlertDialog.Builder builder = new Builder(mContext);
//		builder.setTitle(R.string.soft_updating);
//		final LayoutInflater inflater = LayoutInflater.from(mContext);
//		View v = inflater.inflate(R.layout.softupdate_progress, null);
//		mProgress = (ProgressBar) v.findViewById(R.id.update_progress);
//		builder.setView(v);
//		builder.setNegativeButton(R.string.soft_update_cancel, new OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				dialog.dismiss();
//				cancelUpdate = true;
//			}
//		});
//		mDownloadDialog = builder.create();
//		mDownloadDialog.show();
//		downloadApk();
	}

	private void downloadApk() {
		new downloadApkThread().start();
	}
	
	private class downloadApkThread extends Thread {
		@Override
		public void run() {
//			LogUtil.e(TAG, "外部存储状态："+Environment.getExternalStorageState());
			try {
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					LogUtil.e(TAG, "外部存储状态："+Environment.getExternalStorageState());
					String sdpath = Environment.getExternalStorageDirectory() + "/";
					mSavePath = sdpath + "download";
					URL url = new URL(CommonUtil.NEW_DOWNLOADURL);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.connect();
					int length = conn.getContentLength();
					InputStream is = conn.getInputStream();

					File file = new File(mSavePath);
					if (!file.exists()) {
						file.mkdir();
					}
					File apkFile = new File(mSavePath, "huatuo.apk");
					LogUtil.e(TAG, "apkFile："+apkFile);
					FileOutputStream fos = new FileOutputStream(apkFile);
					int count = 0;
					byte buf[] = new byte[1024];
					do {
						int numread = is.read(buf);
						count += numread;
						progress = (int) (((float) count / length) * 100);
						mHandler.sendEmptyMessage(DOWNLOAD);
						if (numread <= 0) {
							mHandler.sendEmptyMessage(DOWNLOAD_FINISH);
							break;
						}
						fos.write(buf, 0, numread);
					} while (!cancelUpdate);
					fos.close();
					is.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mDownloadDialog.dismiss();
		}
	};

	private void installApk() {
		File apkfile = new File(mSavePath, "huatuo.apk");
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
		mContext.startActivity(i);
	}
	
	public class StartTask extends AsyncTask<String, Void, Integer> {
		@Override
		protected Integer doInBackground(String... params) {
			try {
				Thread.sleep(3 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			// 跳转页面
			if (StartActivity.isFirstIn) {
				intent = new Intent(mContext, GuideActivity.class);
			} else {
				intent = new Intent(mContext, HomeActivity.class);
			}
			mContext.startActivity(intent);
			((Activity) mContext).finish();
		}
	}
}
