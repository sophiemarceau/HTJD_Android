package com.huatuo.custom_widget;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.huatuo.R;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarDialog extends AlertDialog {
	private ProgressBar mProgress;
	private TextView mProgressNumber;
	private TextView mProgressPercent;

	public static final int M = 1024 * 1024;
	public static final int K = 1024;

	private double dMax;
	private double dProgress;

	private int middle = K;

	private int prev = 0;

	private Handler mViewUpdateHandler;
	private static final NumberFormat nf = NumberFormat.getPercentInstance();
	private static final DecimalFormat df = new DecimalFormat("###.##");

	private boolean isDisplayProgressNumber;

	public ProgressBarDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		mViewUpdateHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				double precent = dProgress / dMax;
				if (prev != (int) (precent * 100)) {
					mProgress.setProgress((int) (precent * 100));
					if (isDisplayProgressNumber) {
						mProgressNumber.setText(df.format(dProgress) + "/" + df.format(dMax) + (middle == K ? "K" : "M"));
					}
					mProgressPercent.setText(nf.format(precent));
					prev = (int) (precent * 100);
				}
			}
		};
		View view = inflater.inflate(R.layout.custom_alert_dialog_progress, null);
		mProgress = (ProgressBar) view.findViewById(R.id.progress);
		mProgress.setMax(100);

		mProgressPercent = (TextView) view.findViewById(R.id.progress_percent);
		if (isDisplayProgressNumber) {
			mProgressNumber = (TextView) view.findViewById(R.id.progress_number);
			mProgressNumber.setVisibility(View.VISIBLE);
		}
		setView(view);

		// onProgressChanged();
		super.onCreate(savedInstanceState);
	}

	private void onProgressChanged() {
		mViewUpdateHandler.sendEmptyMessage(0);
	}

	public double getDMax() {
		return dMax;
	}

	public void setDMax(double max) {
		if (max > M) {
			middle = M;
		} else {
			middle = K;
		}

		dMax = max / middle;
	}

	public double getDProgress() {
		return dProgress;
	}

	public void setDProgress(double progress) {
		dProgress = progress / middle;
		onProgressChanged();
	}

	public void setDisplayProgressNumber(boolean isDisplayProgressNumber) {
		this.isDisplayProgressNumber = isDisplayProgressNumber;
	}
}