package huzevka.lunchfriends.taskfragment;

import android.accounts.Account;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.Scopes;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.People;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Name;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import huzevka.lunchfriends.R;

public class LoadUserInfoTaskFragment extends Fragment {

	private static final String TAG = "LoadUserInfoTaskFrag";

	private static HttpTransport HTTP_TRANSPORT = AndroidHttp.newCompatibleTransport();
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

	private TaskCallbacks mCallbacks;
	public LoadUserInfoTask mTask;

	public interface TaskCallbacks {
		void loadUserInfoOnPreExecute();
		void loadUserInfoOnPostExecute(Person person);
	}

	public LoadUserInfoTaskFragment() {
		mTask = new LoadUserInfoTask();
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


	public void executeTask(String email) {
		if ( mTask == null || mTask.getStatus() != AsyncTask.Status.PENDING ) {
			LoadUserInfoTask task = new LoadUserInfoTask();
			task.setContext(mTask.mContext);
			mTask = task;
		}
		mTask.execute(email);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}


	public class LoadUserInfoTask extends AsyncTask<String, Void, Person> {

		public Context mContext;

		public void setContext(Context mContext) {
			this.mContext = mContext;
		}

		@Override
		protected void onPreExecute() {
			if (mCallbacks != null) {
				mCallbacks.loadUserInfoOnPreExecute();
			}
		}

		@Override
		protected Person doInBackground(String... email)
		{
			GoogleAccountCredential credential = GoogleAccountCredential.usingOAuth2(mContext, Arrays.asList(Scopes.PROFILE));
			credential.setSelectedAccount(
					new Account(email[0], "com.google"));
			People service = new People.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
					.setApplicationName("LunchFriends")
					.build();
			Person meProfile = null;
			try {
				meProfile = service.people().get("people/me").set("personFields", "birthdays,genders,phoneNumbers,names").execute();
			} catch (IOException e) {
				Log.w(TAG, "Error getting profile info.", e);
			}

			return meProfile;
		}

		@Override
		protected void onPostExecute(Person person)
		{
			super.onPostExecute(person);
			if (mCallbacks != null) {
				mCallbacks.loadUserInfoOnPostExecute(person);
			}
		}
	}

}
