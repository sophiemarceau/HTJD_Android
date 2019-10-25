package com.huatuo.activity.personal;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.huatuo.R;
import com.huatuo.base.BaseActivity;
import com.huatuo.dictionary.MsgId;
import com.huatuo.net.thread.Feedback;
import com.huatuo.util.DialogUtils;
import com.huatuo.util.Toast_Util;

public class ShiYongFanKuiActivity extends BaseActivity {
	private Context mContext;
	private Feedback feedback;
	private Handler feedback_handler;
	private EditText et_content;
	private TextView tv_zishu, tv_commit;
	private int size;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = ShiYongFanKuiActivity.this;
		setContentView(R.layout.activity_shiyongfankui);
		bindListener();
		et_content = (EditText) findViewById(R.id.et_content);
		tv_zishu = (TextView) findViewById(R.id.tv_zishu);
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		feedback_handler = new feedback_Handler();
		et_content.addTextChangedListener(new TextWatcher() {
			private CharSequence temp = "";

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				temp = s;
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				size = temp.length();
				if (size >= 0) {
					tv_zishu.setText(size + "/500");
				}
			}
		});
		
		tv_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				feedback(et_content.getText().toString());
			}
		});
	}
	
	private void feedback(String context) {
		showCustomCircleProgressDialog(null, getString(R.string.common_toast_net_prompt_submit));
		feedback = new Feedback(mContext, feedback_handler, context);
		Thread thread = new Thread(feedback);
		thread.start();
	}
	
	class feedback_Handler extends Handler {
		String OutMsg;

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MsgId.DOWN_DATA_S:
				closeCustomCircleProgressDialog();
				Toast_Util.showToast(mContext, "提交成功");
				finish();
				break;
			case MsgId.DOWN_DATA_F:
				closeCustomCircleProgressDialog();
				OutMsg = feedback.getOutMsg();
				DialogUtils.showToastMsg(mContext, OutMsg, Toast.LENGTH_SHORT);
				break;
			case MsgId.NET_NOT_CONNECT:
				setCustomDialog(getString(R.string.common_toast_net_not_connect), true);
				break;
			default:
				break;
			}
		}
	}

}
