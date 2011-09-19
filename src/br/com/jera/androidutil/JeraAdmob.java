package br.com.jera.androidutil;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import br.com.jeramobstats.JeraAgent;

import com.google.ads.Ad;
import com.google.ads.AdListener;
import com.google.ads.AdRequest;
import com.google.ads.AdRequest.ErrorCode;
import com.google.ads.AdSize;
import com.google.ads.AdView;

public class JeraAdmob {

	private Activity activity;

	private static long AD_SHOW_CLOSE = 5000;
	private static long AD_RELOAD = 15000;
	private static long AD_RETRY = 15000;

	private int position;
	private Handler handler = new Handler();
	private static RelativeLayout rl;

	public static void setAdVisibility(boolean visible) {
		rl.setVisibility((visible) ? View.VISIBLE : View.INVISIBLE);
	}

	public JeraAdmob(Activity activity, int position) {
		this.activity = activity;
		this.position = position;
		addAd();
	}

	private void addAd() {
		rl = (RelativeLayout) activity.getLayoutInflater().inflate(R.layout.admob, null);
		rl.setGravity(position);
		activity.addContentView(rl, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		AdView admobView = new AdView(activity, AdSize.BANNER, Preferences.getAdmobKey());
		admobView.loadAd(new AdRequest());
		admobView.setAdListener(new AdListener() {
			@Override
			public void onDismissScreen(Ad ad) {
				Log.v("JeraAdmob", "onDismissScreen");
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						addAd();
					}
				}, AD_RETRY);
			}

			@Override
			public void onFailedToReceiveAd(Ad ad, ErrorCode ec) {
				Log.v("JeraAdmob", "onFailedToReceiveAd: " + ec.name() + " - " + ec);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						addAd();
					}
				}, AD_RETRY);
			}

			@Override
			public void onLeaveApplication(Ad ad) {
				Log.v("JeraAdmob", "onLeaveApplication");
			}

			@Override
			public void onPresentScreen(Ad ad) {
				Log.v("JeraAdmob", "onPresentScreen");
			}

			@Override
			public void onReceiveAd(Ad ad) {
				Log.v("JeraAdmob", "onReceiveAd");
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						createCloseButton();
					}
				}, AD_SHOW_CLOSE);
			}
		});

		rl.addView(admobView);

	};

	private void createCloseButton() {
		ImageButton button = (ImageButton) rl.findViewById(R.id.close_ad);
		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rl.removeAllViews();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						JeraAgent.logEvent("AD_CLOSE");
						addAd();
						rl.bringToFront();
					}
				}, AD_RELOAD);
			}
		});
		button.setVisibility(View.VISIBLE);
		button.bringToFront();
	}
}
