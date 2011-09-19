package br.com.jera.androidutil;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import br.com.jeramobstats.JeraAgent;

import com.androidquery.AQuery;

public class AppRater {

	private final static int DAYS_UNTIL_PROMPT = 1;
	private final static int LAUNCHES_UNTIL_PROMPT = 10;

	private static final String SUFIX_PREFS_NAME = "_APP_RATE_DIALOG";
	private static final String DONT_SHOW_AGAIN_PREFS_NAME = "dontshowagain";
	private static final String LAUNCH_COUNT_PREFS_NAME = "launch_count";
	private static final String FIRST_LAUNCH_DATE_PREFS_NAME = "date_firstlaunch";

	public static void show(Activity context, boolean force) {
		SharedPreferences prefs = context.getSharedPreferences(context.getApplicationInfo().packageName + SUFIX_PREFS_NAME, 0);
		if (prefs.getBoolean(DONT_SHOW_AGAIN_PREFS_NAME, false) && !force) {
			return;
		}
		showRateDialog(context, prefs);
	}

	public static void appLaunched(Activity context) {
		SharedPreferences prefs = context.getSharedPreferences(context.getApplicationInfo().packageName + SUFIX_PREFS_NAME, 0);
		Editor editor = prefs.edit();

		if (prefs.getBoolean(DONT_SHOW_AGAIN_PREFS_NAME, false)) {
			return;
		}

		long launchCount = prefs.getLong(LAUNCH_COUNT_PREFS_NAME, 0L) + 1;
		editor.putLong(LAUNCH_COUNT_PREFS_NAME, launchCount);

		Long firstLaunch = prefs.getLong(FIRST_LAUNCH_DATE_PREFS_NAME, 0);
		if (firstLaunch == 0) {
			firstLaunch = System.currentTimeMillis();
			editor.putLong(FIRST_LAUNCH_DATE_PREFS_NAME, firstLaunch);
		}
		editor.commit();

		if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
			if (System.currentTimeMillis() >= firstLaunch + (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)) {
				showRateDialog(context, prefs);
			}
		}
	}

	private static void showRateDialog(final Activity context, SharedPreferences prefs) {
		final Editor editor = prefs.edit();
		final Dialog dialog = new Dialog(context);
		CharSequence appLabel = context.getApplicationInfo().loadLabel((context.getApplication().getPackageManager()));
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		LinearLayout layout = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.rate_dialog, null);
		AQuery aq = new AQuery(layout);
		aq.id(R.id.rate_text).text(context.getString(R.string.rate_it_msg, appLabel));
		aq.id(R.id.rate_button).text(context.getString(R.string.rate, appLabel));

		// listeners
		/*
		 * aq.id(R.id.no_thanks_button).clicked(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) {
		 * JeraAgent.logEvent("RATE_NO");
		 * editor.putBoolean(DONT_SHOW_AGAIN_PREFS_NAME, true); editor.commit();
		 * dialog.dismiss(); } });
		 */

		aq.id(R.id.remind_later_button).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JeraAgent.logEvent("RATE_REMIND_LATER");
				editor.commit();
				dialog.dismiss();
			}
		});

		aq.id(R.id.rate_button).clicked(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				editor.putBoolean(DONT_SHOW_AGAIN_PREFS_NAME, true);
				JeraAgent.logEvent("RATE_RATED");
				editor.commit();
				Log.i("APP RATE DIALOG", "opening market: " + "market://details?id=" + context.getApplicationInfo().packageName);
				context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getApplicationInfo().packageName)));
				dialog.dismiss();
			}
		});
		dialog.setContentView(layout);
		dialog.show();

	}
}