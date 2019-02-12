package huzevka.lunchfriends.taskfragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentCallbacks2;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class PostToRESTTaskFragment extends Fragment {

	private static final String TAG = "PostToRESTTaskFragment";

	private TaskCallbacks mCallbacks;
	public PostRESTTask mTask;

	public interface TaskCallbacks {
		void postToRESTOnPreExecute();
		void postToRESTOnPostExecute(Object obj);
	}

	public PostToRESTTaskFragment() {
		mTask = new PostRESTTask();
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mCallbacks = (TaskCallbacks) activity;
	}

	public void attach(ComponentCallbacks2 callbacks) {
		mCallbacks = (TaskCallbacks) callbacks;
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
			PostRESTTask task = new PostRESTTask();
			task.setPostObject(mTask.postObject);
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


	public class PostRESTTask extends AsyncTask<String, Void, Object> {

		Object postObject;

		Class<? extends Object> responseClass;

		Object object;

		public void setPostObject(Object postObject) {
			this.postObject = postObject;
		}

		public void setResponseClass(Class<? extends Object> responseClass) {
			this.responseClass = responseClass;
		}

		public Object getPostObject() {
			return postObject;
		}

		public Class<? extends Object> getResponseClass() {
			return responseClass;
		}

		public void setObject(Object object) {
			this.object = object;
		}

		public Object getObject() {
			return object;
		}

		@Override
		protected void onPreExecute() {
			if (mCallbacks != null) {
				mCallbacks.postToRESTOnPreExecute();
			}
		}

		@Override
		protected Object doInBackground(String... uri) {
			Object obj = null;
			try {
				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_XML);

				HttpEntity<? extends Object> request = new HttpEntity<>(postObject, headers);

				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(new SimpleXmlHttpMessageConverter());

				ResponseEntity<? extends Object> response = restTemplate.postForEntity(uri[0], request, responseClass);

				obj = response.getBody();

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
				mCallbacks.postToRESTOnPostExecute(obj);
			}
		}
	}

}




