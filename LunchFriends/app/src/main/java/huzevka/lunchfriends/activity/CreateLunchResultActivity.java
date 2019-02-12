package huzevka.lunchfriends.activity;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.model.CreateLunchResult;
import huzevka.lunchfriends.model.Lunch;
import huzevka.lunchfriends.taskfragment.PostToRESTTaskFragment;
import huzevka.lunchfriends.tools.LoginSingle;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class CreateLunchResultActivity extends AppCompatActivity
		implements PostToRESTTaskFragment.TaskCallbacks {

	private static final String TAG = "CreateLunchResultAct.";

	private String chosenPlaceId;
	private int hour;
	private int minute;

	private TextView lunchResultText;

	CreateLunchResult createLunchResult;

	private PostToRESTTaskFragment mPostToRESTTaskFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_lunch_result);

		// get data from intent and set views
		Bundle intent = getIntent().getExtras();
		chosenPlaceId = intent.getString("place");
		hour = intent.getInt("timeHour");
		minute = intent.getInt("timeMinute");

		lunchResultText = findViewById(R.id.lunchResultText);

		findViewById(R.id.backButton).setOnClickListener(backButtonClickListener);

		// save lunch to database
		startCreateLunchRESTTask(LunchFriendsTools.DBServerBaseURI + "postlunch");
	}

	private void startCreateLunchRESTTask(String uri) {
		Log.i(TAG, "Started create lunch task.");
		FragmentManager fm = getFragmentManager();
		mPostToRESTTaskFragment = (PostToRESTTaskFragment) fm.findFragmentByTag("CreateLunchTask");

		if (mPostToRESTTaskFragment == null) {
			mPostToRESTTaskFragment = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mPostToRESTTaskFragment, "CreateLunchTask").commit();
		}

		Lunch postLunch = new Lunch();
		postLunch.setPlaceid(chosenPlaceId);

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		date = calendar.getTime();

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateString = dateFormat.format(date);

		postLunch.setLdate(dateString);

		try {
			postLunch.setPID(LoginSingle.getPerson().getPID());
		} catch ( NullPointerException e ) {
			postToRESTOnPostExecute(null);
			return;
		}

		mPostToRESTTaskFragment.mTask.setPostObject(postLunch);
		mPostToRESTTaskFragment.mTask.setResponseClass(CreateLunchResult.class);
		mPostToRESTTaskFragment.executeTask(uri);
	}

	@Override
	public void postToRESTOnPreExecute() {
	}

	@Override
	public void postToRESTOnPostExecute(Object obj) {
		if ( obj instanceof CreateLunchResult && obj != null ) {
			createLunchResult = ((CreateLunchResult)obj);
		}

		if ( createLunchResult != null ) {
			if (createLunchResult.isSuccess()) {
				lunchResultText.setText(R.string.created_lunch);
			} else {
				lunchResultText.setText(R.string.create_lunch_fail);
			}
		} else {
			lunchResultText.setText(R.string.create_lunch_fail);
		}
	}

	private final View.OnClickListener backButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			onBackPressed();
		}
	};


}
