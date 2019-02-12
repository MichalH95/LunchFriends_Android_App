package huzevka.lunchfriends.taskfragment;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class LoadImageTaskFragment extends Fragment {

	private static final String TAG = "LoadImageTaskFragment";

	private TaskCallbacks mCallbacks;
	private LoadImageTask mTask;

	public interface TaskCallbacks {
		void loadImageOnPreExecute();
		void loadImageOnPostExecute(Bitmap bitmap);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}


	public void executeTask(Uri uri) {
		mTask = new LoadImageTask();
		mTask.execute(uri);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}


	private class LoadImageTask extends AsyncTask<Uri, Void, Bitmap> {

		@Override
		protected void onPreExecute() {
			if (mCallbacks != null) {
				mCallbacks.loadImageOnPreExecute();
			}
		}

		@Override
		protected Bitmap doInBackground(Uri... uri)
		{
			Bitmap bm = null;
			try {
				long startTime = System.currentTimeMillis();
				URL url = new URL(uri[0].toString());
				URLConnection conn = url.openConnection();
				conn.connect();
				InputStream is = conn.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				bm = BitmapFactory.decodeStream(bis);
				bis.close();
				is.close();
				double difference = System.currentTimeMillis() - startTime;
				Log.i(TAG, "Took " + (difference/1000) + " seconds to load image.");
			} catch ( Exception e ) {
				Log.e(TAG, "Error getting bitmap", e);
			}
			return bm;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			super.onPostExecute(bitmap);
			if (mCallbacks != null) {
				mCallbacks.loadImageOnPostExecute(bitmap);
			}
		}
	}

}
