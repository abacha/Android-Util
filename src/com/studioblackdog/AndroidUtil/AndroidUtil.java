package com.studioblackdog.AndroidUtil;

import android.app.Activity;
import br.com.jeramobstats.JeraAgent;

import com.flurry.android.FlurryAgent;

public class AndroidUtil {

	JeraAdmob admob;

	public AndroidUtil(Activity activity, int admobPosition) {

		if (!Preferences.isInitialized()) {
			Preferences.initialize(activity);
		}

		if (Preferences.isDynamicSplash()) {
			new PopupDialog(activity);
		}

		if (Preferences.isFlurry()) {
			JeraAgent.onStartSession(activity, Preferences.getFlurryKey());
		}

		if (Preferences.isAppRate()) {
			AppRater.appLaunched(activity);
		}

		if (Preferences.isAdmob() && !Preferences.isPaid()) {
			admob = new JeraAdmob(activity, admobPosition);
		}

	}

	public void onStart(Activity activity) {
		if (Preferences.isFlurry()) {
			FlurryAgent.onStartSession(activity, Preferences.getFlurryKey());
		}
	}

	public void onStop(Activity activity) {
		if (Preferences.isFlurry()) {
			FlurryAgent.onEndSession(activity);
		}
	}
}