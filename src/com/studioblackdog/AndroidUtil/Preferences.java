package com.studioblackdog.AndroidUtil;

import java.io.IOException;
import java.util.Properties;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Handler;

import com.studioblackdog.com.AndroidUtil.R;

public class Preferences {

	public static boolean PAID;
	private static String APP_TITLE;
	private static String PACKAGE_PATH;
	private static String PATH;
	private static String SPLASH_IMG_URL;
	private static String SPLASH_LINK_URL;
	private static String SPLASH_FILENAME;
	private static String PACKAGE_NAME;
	private static String START_CLASS;
	private static String PAID_URL;

	private static String FLURRY_KEY;
	private static String ADMOB_KEY;
	
	private static Boolean ADMOB;
	private static Boolean APP_RATE;
	private static Boolean SPLASH;
	private static Boolean FLURRY;
	private static Boolean DYNAMIC_SPLASH;

	private Preferences(Application ap) {
	}

	public static boolean isInitialized() {
		return PACKAGE_PATH != null;
	}
	
	public static void initialize(Activity activity) {
		Properties properties = new Properties();

		try {
			properties.load(activity.getAssets().open("util.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		PACKAGE_PATH = activity.getPackageName();

		String paid = properties.getProperty("paid");
		PAID = (paid != null) && !(properties.getProperty("paid").equals("0") || properties.getProperty("paid").equals("false"));

		String[] name = PACKAGE_PATH.split("\\.");
		PATH = "/data/data/" + PACKAGE_PATH + "/";
		PACKAGE_NAME = name[name.length - 1];

		Resources appR = activity.getResources();
		CharSequence txt = appR.getText(appR.getIdentifier("app_name", "string", activity.getPackageName()));

		APP_TITLE = txt.toString();

		ADMOB_KEY = properties.getProperty("admobKey");
		FLURRY_KEY = properties.getProperty((PAID) ? "flurryKeyPaid" : "flurryKey");
		if (AndroidConfigUtil.isDebugMode(activity)) {
			FLURRY_KEY = properties.getProperty("flurryKeyDebug");
			ADMOB_KEY = properties.getProperty("admobKeyDebug");
		}

		START_CLASS = PACKAGE_PATH + "." + properties.getProperty("startClass");

		PAID_URL = properties.getProperty("paidUrl");

		APP_RATE = properties.getProperty("appRate").equals("1");
		FLURRY = properties.getProperty("flurry").equals("1");
		ADMOB = properties.getProperty("admob").equals("1");
		DYNAMIC_SPLASH = properties.getProperty("dynamicSplash").equals("1");
		SPLASH = properties.getProperty("splash").equals("1");

		String SPLASH_URL = "http://assets.jera.com.br/splash/";
		SPLASH_IMG_URL = SPLASH_URL + PACKAGE_NAME + "/splash.jpg";
		SPLASH_LINK_URL = SPLASH_URL + PACKAGE_NAME + "/splash.dat";
		SPLASH_FILENAME = PATH + "splash.jpg";		
	}

	public static void onCreate(Activity activity) {
		int delay = 0;
		if (SPLASH) {
			activity.setContentView(R.layout.splash);
			activity.findViewById(R.id.splash_bg).setBackgroundColor(Color.WHITE);
			delay = 2000;
			new Handler().postDelayed((Runnable) activity, delay);
		}
	}

	public static String readString(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(PACKAGE_NAME, 0);
		return settings.getString(key, null);
	}

	public static boolean readBoolean(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(PACKAGE_NAME, 0);
		return settings.getBoolean(key, false);
	}

	public static long readLong(Activity activity, String key) {
		SharedPreferences settings = activity.getSharedPreferences(PACKAGE_NAME, 0);
		return settings.getLong(key, 0);
	}

	public static void write(Activity activity, String key, Object value) {
		SharedPreferences settings = activity.getSharedPreferences(PACKAGE_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		} else if (value instanceof String) {
			editor.putString(key, (String) value);
		} else if (value instanceof Long) {
			editor.putLong(key, (Long) value);
		}
		editor.commit();
	}

	public static boolean isPaid() {
		return PAID;
	}

	public static String getAppTitle() {
		return APP_TITLE;
	}

	public static String getPackagePath() {
		return PACKAGE_PATH;
	}

	public static String getPath() {
		return PATH;
	}

	public static String getSplashImgUrl() {
		return SPLASH_IMG_URL;
	}

	public static String getSplashLinkUrl() {
		return SPLASH_LINK_URL;
	}

	public static String getSplashFilename() {
		return SPLASH_FILENAME;
	}

	public static String getPackageName() {
		return PACKAGE_NAME;
	}

	public static String getFlurryKey() {
		return FLURRY_KEY;
	}

	public static String getAdmobKey() {
		return ADMOB_KEY;
	}

	
	public static Boolean isAppRate() {
		return APP_RATE;
	}

	public static Boolean isFlurry() {
		return FLURRY;
	}

	public static Boolean isDynamicSplash() {
		return DYNAMIC_SPLASH;
	}

	public static String getPaidUrl() {
		return PAID_URL;
	}

	public static Class<?> getStartClass() {
		try {
			return Class.forName(START_CLASS);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean isAdmob() {
		return ADMOB;
	}
}