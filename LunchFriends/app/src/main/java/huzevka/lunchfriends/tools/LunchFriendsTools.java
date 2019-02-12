package huzevka.lunchfriends.tools;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.activity.ChangeLanguageActivity;

import static com.facebook.FacebookSdk.getApplicationContext;

public class LunchFriendsTools {

	public static final String DBServerBaseURI = "http://10.0.0.34:3000/";

	public static float convertDpToPx(float dp, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float px = dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
		return px;
	}

	public static float convertPxToDp(float px, Context context){
		Resources resources = context.getResources();
		DisplayMetrics metrics = resources.getDisplayMetrics();
		float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
		return dp;
	}

	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static void setImageLoading(ImageView imageView, int dp) {
		Bitmap loadingImage = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.loading_image);
		int px = Math.round(LunchFriendsTools.convertDpToPx(dp, getApplicationContext()));
		imageView.setImageBitmap(Bitmap.createScaledBitmap(loadingImage, px, px, false));
	}

	public static Bitmap scaleBitmap(Bitmap bitmap, int maxDp) {
		int maxPx = Math.round(LunchFriendsTools.convertDpToPx(maxDp, getApplicationContext()));
		if ( bitmap.getWidth() > bitmap.getHeight() ) {
			bitmap = Bitmap.createScaledBitmap(bitmap, maxPx, (int)(bitmap.getHeight()*((double)maxPx/bitmap.getWidth())), false);
		} else {
			bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*((double)maxPx/bitmap.getHeight())), maxPx, false);
		}
		return bitmap;
	}

	public static void saveLocale(String lang, Activity activity) {
		String langPref = "Language";
		SharedPreferences prefs = activity.getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(langPref, lang);
		editor.commit();
	}

	public static boolean isLanguage(String lang, Activity activity) {
		return getLanguage(activity).equals(new Locale(lang).getLanguage());
	}

	private static String getLanguage(Activity activity) {
		Resources res = activity.getResources();
		Configuration conf = res.getConfiguration();
		return conf.locale.getLanguage();
	}

	public static String bytesToHex(byte[] bytes) {
		StringBuffer result = new StringBuffer();
		for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}


}
