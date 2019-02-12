package huzevka.lunchfriends.tools;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;

public class GoogleApiClientSingle extends Application {
	private static GoogleApiClient client;

	public void onCreate() {
		super.onCreate();
		client = null;
	}

	public static void setClient(GoogleApiClient client) {
		GoogleApiClientSingle.client = client;
	}

	public static GoogleApiClient getClient() {
		return client;
	}
}