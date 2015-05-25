package com.sogogi.webserveroid;

import com.sogogi.webserveroid.R;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				finish();
			}
		};
		
		handler.sendEmptyMessageDelayed(0, 2000);
		
	}

}
