package huzevka.lunchfriends.taskfragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentCallbacks2;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import huzevka.lunchfriends.model.Lunches;

import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class GetFromRESTTaskFragment extends Fragment {

	private static final String TAG = "GetFromRESTTaskFragment";

	private TaskCallbacks mCallbacks;
	public GetRESTTask mTask;

	public interface TaskCallbacks {
		void getFromRESTOnPreExecute();
		void getFromRESTOnPostExecute(Object obj, GetRESTTask task);
	}

	public GetFromRESTTaskFragment() {
		mTask = new GetRESTTask();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}

	public void attach(ComponentCallbacks2 callbacks) {
		mCallbacks = (GetFromRESTTaskFragment.TaskCallbacks) callbacks;
	}

	public void detach() {
		mCallbacks = null;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setRetainInstance(true);
	}


	public void executeTask(String uri) {
		if ( mTask == null || mTask.getStatus() != AsyncTask.Status.PENDING ) {
			GetRESTTask task = new GetRESTTask();
			task.setResponseClass(mTask.responseClass);
			mTask = task;
		}
		mTask.execute(uri);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}


	public class GetRESTTask extends AsyncTask<String, Void, Object> {

		Class<? extends Object> responseClass;

		Object object;

		public void setResponseClass(Class<? extends Object> responseClass) {
			this.responseClass = responseClass;
		}

		public Class<? extends Object> getResponseClass() {
			return responseClass;
		}

		public Object getObject() {
			return object;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		@Override
		protected void onPreExecute() {
			if (mCallbacks != null) {
				mCallbacks.getFromRESTOnPreExecute();
			}
		}

		@Override
		protected Object doInBackground(String... uri) {
			Object obj = null;
			try {
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

				obj = restTemplate.getForObject(uri[0], responseClass);

			} catch (Exception e) {
				Log.e(TAG, "Error loading", e);
			}
			return obj;
		}

		@Override
		protected void onPostExecute(Object obj)
		{
			super.onPostExecute(obj);
			if (mCallbacks != null) {
				mCallbacks.getFromRESTOnPostExecute(obj, this);
			}
		}
	}

}
