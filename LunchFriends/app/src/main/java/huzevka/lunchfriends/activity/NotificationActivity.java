package huzevka.lunchfriends.activity;

import android.app.FragmentManager;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.list.GenderList;
import huzevka.lunchfriends.model.History;
import huzevka.lunchfriends.model.Invitation;
import huzevka.lunchfriends.model.Login;
import huzevka.lunchfriends.model.Lunch;
import huzevka.lunchfriends.model.Person;
import huzevka.lunchfriends.taskfragment.LoadImageTaskFragment;
import huzevka.lunchfriends.taskfragment.PostToRESTTaskFragment;
import huzevka.lunchfriends.tools.LoginSingle;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class NotificationActivity extends AppCompatActivity
		implements LoadImageTaskFragment.TaskCallbacks, PostToRESTTaskFragment.TaskCallbacks {

	private final String TAG = "NotificationActivity";

	private Invitation invitation;

	private Person invitingPerson;
	private Lunch lunch;

	private TextView fullnameText;
	private TextView genderText;
	private TextView ageText;
	private TextView placeText;
	private TextView timeText;
	private Button acceptButton;
	private Button rejectButton;

	private ImageView userImage;

	private LoadImageTaskFragment mLoadImageTaskFragment;
	private PostToRESTTaskFragment mDeleteInvRESTTaskFragment;
	private PostToRESTTaskFragment mPostHistoryRESTTaskFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_notification);

		// set views
		fullnameText = findViewById(R.id.fullnameText);
		genderText = findViewById(R.id.genderText);
		ageText = findViewById(R.id.ageText);
		placeText = findViewById(R.id.placeText);
		timeText = findViewById(R.id.timeText);
		userImage = findViewById(R.id.userImage);
		acceptButton = findViewById(R.id.acceptButton);
		rejectButton = findViewById(R.id.rejectButton);

		userImage.setVisibility(View.GONE);
		LunchFriendsTools.setImageLoading(userImage, 120);

		fullnameText.setText("");
		genderText.setText("");
		ageText.setText("");
		placeText.setText("");
		timeText.setText("");

		acceptButton.setOnClickListener(acceptButtonOnClickListener);
		rejectButton.setOnClickListener(rejectButtonOnClickListener);

		// hide notification and show info about inviting person
		Bundle intent = getIntent().getExtras();

		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(intent.getInt("notificationID"));

		invitation = (Invitation) intent.getSerializable("invitation");
		invitingPerson = (Person) intent.getSerializable("invitingPerson");
		lunch = (Lunch) intent.getSerializable("lunch");

		if ( invitingPerson != null && lunch != null ) {
			startLoadImageTask(invitingPerson.getImageuri());
			userImage.setVisibility(View.VISIBLE);

			setPersonLunchData();
		}
	}

	private void setPersonLunchData() {
		fullnameText.setText(getResources().getString(R.string.fullname_colon) + " " + invitingPerson.getRealname());
		String genderString;
		if ( LunchFriendsTools.isLanguage("cs", NotificationActivity.this) ) {
			genderString = GenderList.GENDERS_CONVERT.get(invitingPerson.getGender());
		} else {
			genderString = invitingPerson.getGender();
		}
		genderText.setText(getResources().getString(R.string.gender_colon) + " " + genderString);
		if ( invitingPerson.getAge() == -1 ) {
			ageText.setText(getString(R.string.age_colon) + " " + getString(R.string.not_available));
		} else {
			ageText.setText(getString(R.string.age_colon) + " " + invitingPerson.getAge());
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date;
		try {
			date = format.parse(lunch.getLdate());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int hours = calendar.get(Calendar.HOUR_OF_DAY);
			int minutes = calendar.get(Calendar.MINUTE);
			timeText.setText(getResources().getString(R.string.time_colon) + " " + hours + ":" +
					( minutes < 10 ? ("0" + minutes) : minutes));
		} catch (ParseException e) {
			try {
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = format.parse(lunch.getLdate());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int hours = calendar.get(Calendar.HOUR_OF_DAY);
				int minutes = calendar.get(Calendar.MINUTE);
				timeText.setText(getResources().getString(R.string.time_colon) + " " + hours + ":" +
						( minutes < 10 ? ("0" + minutes) : minutes));
			}
			catch (ParseException ex ) {
				Log.w(TAG, "Failed parsing date.");
				timeText.setText(getResources().getString(R.string.time_colon) + " " + lunch.getLdate());
			}
		}

		// load place name from place id
		GeoDataClient geoDataClient = Places.getGeoDataClient(NotificationActivity.this, null);
		geoDataClient.getPlaceById(lunch.getPlaceid()).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
			@Override
			public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
				if (task.isSuccessful()) {
					PlaceBufferResponse places = task.getResult();
					Place myPlace = places.get(0);
					placeText.setText(getResources().getString(R.string.place_colon) + " " + myPlace.getName());
					places.release();
				} else {
					placeText.setText(getResources().getString(R.string.no_place));
				}
			}
		});
	}

	@Override
	public void loadImageOnPreExecute() {
	}

	@Override
	public void loadImageOnPostExecute(Bitmap bitmap) {
		if ( bitmap != null ) {
			bitmap = LunchFriendsTools.scaleBitmap(bitmap, 150);
			userImage.setImageBitmap(bitmap);
			userImage.setVisibility(View.VISIBLE);
		} else {
			userImage.setVisibility(View.GONE);
		}
	}

	private void startLoadImageTask(String photoUri) {
		Log.i(TAG, "Started loading image task.");
		FragmentManager fm = getFragmentManager();
		if (photoUri != null ) {
			mLoadImageTaskFragment = (LoadImageTaskFragment) fm.findFragmentByTag("ImageLoadTask");

			if (mLoadImageTaskFragment == null) {
				mLoadImageTaskFragment = new LoadImageTaskFragment();
				fm.beginTransaction().add(mLoadImageTaskFragment, "ImageLoadTask").commit();
			}

			mLoadImageTaskFragment.executeTask(Uri.parse(photoUri));
		} else {
			userImage.setVisibility(View.GONE);
		}
	}

	private void startDeleteInvPostRESTTask(String uri) {
		Log.i(TAG, "Started delete invitation task.");
		FragmentManager fm = getFragmentManager();
		mDeleteInvRESTTaskFragment = (PostToRESTTaskFragment) fm.findFragmentByTag("DeleteInvTask");

		if (mDeleteInvRESTTaskFragment == null) {
			mDeleteInvRESTTaskFragment = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mDeleteInvRESTTaskFragment, "DeleteInvTask").commit();
		}

		mDeleteInvRESTTaskFragment.mTask.setPostObject(invitation);
		mDeleteInvRESTTaskFragment.mTask.setResponseClass(null);
		mDeleteInvRESTTaskFragment.executeTask(uri);
	}

	private void startHistoryPostRESTTask(String uri) {
		Log.i(TAG, "Started history post task.");
		FragmentManager fm = getFragmentManager();
		mPostHistoryRESTTaskFragment = (PostToRESTTaskFragment) fm.findFragmentByTag("HistoryPostTask");

		if (mPostHistoryRESTTaskFragment == null) {
			mPostHistoryRESTTaskFragment = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mPostHistoryRESTTaskFragment, "HistoryPostTask").commit();
		}

		History history = new History();
		history.setTargetPID(LoginSingle.getPerson().getPID());
		history.setPID(invitation.getPID());
		history.setLdate(lunch.getLdate().replace('T', ' '));
		history.setPlaceid(lunch.getPlaceid());

		mPostHistoryRESTTaskFragment.mTask.setPostObject(history);
		mPostHistoryRESTTaskFragment.mTask.setResponseClass(null);
		mPostHistoryRESTTaskFragment.executeTask(uri);
	}

	private View.OnClickListener acceptButtonOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startUserInfoActivity();

			startDeleteInvPostRESTTask(LunchFriendsTools.DBServerBaseURI + "invitationdel");
			startHistoryPostRESTTask(LunchFriendsTools.DBServerBaseURI + "posthistory");
		}
	};

	private void startUserInfoActivity() {
		Intent intent = new Intent(NotificationActivity.this, UserInfoActivity.class);
		intent.putExtra("fullname", invitingPerson.getRealname());
		intent.putExtra("email", invitingPerson.getEmail());
		intent.putExtra("phone", invitingPerson.getPhone());
		intent.putExtra("imageUri", invitingPerson.getImageuri());
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	private View.OnClickListener rejectButtonOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			finish();
			startDeleteInvPostRESTTask(LunchFriendsTools.DBServerBaseURI + "invitationdel");
		}
	};

	@Override
	public void postToRESTOnPreExecute() {

	}

	@Override
	public void postToRESTOnPostExecute(Object obj) {

	}
}
