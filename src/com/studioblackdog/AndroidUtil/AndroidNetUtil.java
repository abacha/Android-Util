package com.studioblackdog.AndroidUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class AndroidNetUtil {

	public static boolean isOnline(Activity activity) {
		ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static void downloadFromUrl(String inputUrl, OutputStream os) throws IOException {
		URL url = new URL(inputUrl);

		long startTime = System.currentTimeMillis();
		Log.d("DOWNLOAD", "download begining");
		Log.d("DOWNLOAD", "download url:" + url);
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(5000);
		long downloaded = 0;
		byte buffer[];
		InputStream stream = connection.getInputStream();
		long size = (long) connection.getContentLength();
		while (true) {
			buffer = (size - downloaded) > 1024 ? new byte[1024] : new byte[(int) (size - downloaded)];
			int read = stream.read(buffer);
			if (read == -1) {
				break;
			}
			os.write(buffer, 0, read);
			downloaded += read;
		}
		stream.close();
		os.close();
		Log.d("DOWNLOAD", "download ready in " + ((System.currentTimeMillis() - startTime) / 1000) + " sec");

	}

}
