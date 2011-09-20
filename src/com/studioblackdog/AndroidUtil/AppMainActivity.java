package com.studioblackdog.AndroidUtil;

import android.app.Activity;

public class AppMainActivity extends Activity {
	private static AndroidUtil util;

	@Override
	public void onStop() {
		super.onStop();
		util.onStop(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		util = new AndroidUtil(this, 0);
		util.onStart(this);
	}
}
