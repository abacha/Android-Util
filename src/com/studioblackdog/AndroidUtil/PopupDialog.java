package com.studioblackdog.AndroidUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import br.com.jeramobstats.JeraAgent;

import com.studioblackdog.com.AndroidUtil.R;

public class PopupDialog extends Dialog {

	private Activity context;
	private SharedPreferences prefs;
	private static String SUFIX_PREFS_NAME = "_SPLASH_DIALOG";
	private static String VERSION_PREFS_NAME = "_SPLASH_VERSION";

	public PopupDialog(final Activity activity) {
		super(activity);
		this.context = activity;
		prefs = activity.getSharedPreferences(activity.getApplicationInfo().packageName + SUFIX_PREFS_NAME, 0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * Mostra um Dialog baseado nas informações do aquivo .dat<br />
	 * formato do arquivo:</br> 1</br>
	 * market://details?id=br.com.jera.vikingspaid
	 *
	 * @param datUrl
	 * @param imageUrl
	 */
	public void showIfNeeded(final String datUrl, final String imageUrl) {
		if (AndroidNetUtil.isOnline(this.context)) {
			final ByteArrayOutputStream datFile = new ByteArrayOutputStream();
			AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>() {
				@Override
				protected Void doInBackground(Void... params) {
					try {
						AndroidNetUtil.downloadFromUrl(datUrl, datFile);
					} catch (IOException e) {
						Log.e("SPLASH", "error loading dat file");
					}
					return null;
				}

				@Override
				protected void onPostExecute(Void result) {
					PopupDialog.this.checkUpdates(datFile.toByteArray(), imageUrl);
				}

			};
			task.execute();
		}
	}

	private void showSplash(final String link, final String imageUrl) {
		setContentView(R.layout.dynamic_splash);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			AndroidNetUtil.downloadFromUrl(imageUrl, buffer);
		} catch (IOException e) {
			Log.e("SPLASH", "error loading dat image");
		}
		Drawable drawable = Drawable.createFromStream(new ByteArrayInputStream(buffer.toByteArray()), "splash");
		ImageButton button = (ImageButton) findViewById(R.id.close_splash);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
		ImageView image = (ImageView) findViewById(R.id.splash);
		image.setBackgroundDrawable(drawable);
		if (!TextUtils.isEmpty(link)) {
			image.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					context.runOnUiThread(new Runnable() {
						public void run() {
							JeraAgent.logEvent("SPLASH_CLICK");
							Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(link));
							context.startActivity(intent);
						}
					});
				}
			});
		}
		context.runOnUiThread(new Runnable() {
			public void run() {
				show();
			}
		});
	}

	private void checkUpdates(byte[] bytes, final String imageUrl) {
		InputStream stream = new ByteArrayInputStream(bytes);
		Scanner sc = new Scanner(stream);
		if (sc.hasNext()) {
			long localVersion = prefs.getLong(VERSION_PREFS_NAME, 0);
			long remoteVersion = sc.nextLong();
			if (remoteVersion > localVersion) {
				String link = sc.hasNext() ? sc.next() : null;
				showSplash(link, imageUrl);
				prefs.edit().putLong(VERSION_PREFS_NAME, remoteVersion).commit();
			}
		}
	}
}