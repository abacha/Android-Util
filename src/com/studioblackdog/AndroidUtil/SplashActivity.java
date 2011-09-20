package com.studioblackdog.AndroidUtil;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.studioblackdog.com.AndroidUtil.R;

public class SplashActivity extends Activity implements Runnable {
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		Preferences.initialize(this);
		Preferences.onCreate(this);
	}

	@Override
	public void run() {
		finish();
		startActivity(new Intent(this, Preferences.getStartClass()));
	}
}