package huzevka.lunchfriends.activity;

import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeSet;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.list.GenderList;
import huzevka.lunchfriends.list.HobbyList;
import huzevka.lunchfriends.model.Invitation;
import huzevka.lunchfriends.model.Login;
import huzevka.lunchfriends.model.Lunchperson;
import huzevka.lunchfriends.model.Lunchpersons;
import huzevka.lunchfriends.model.distance_matrix.DistanceMatrixResponse;
import huzevka.lunchfriends.taskfragment.GetFromRESTTaskFragment;
import huzevka.lunchfriends.taskfragment.LoadImageTaskFragment;
import huzevka.lunchfriends.taskfragment.PostToRESTTaskFragment;
import huzevka.lunchfriends.tools.LoginSingle;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class FriendResultsActivity extends AppCompatActivity implements
		GetFromRESTTaskFragment.TaskCallbacks, LoadImageTaskFragment.TaskCallbacks,
		PostToRESTTaskFragment.TaskCallbacks {

	private static final String TAG = "FriendResultsActivity";

	private int ageFrom;
	private int ageTo;
	private int gender;
	private String placeId;
	private int maxDistance;
	private int maxTimeDiff;
	private int timeHour;
	private int timeMinute;
	private TreeSet<String> selectedHobbies;

	private boolean showAllFriends;

	private TextView fullnameText;
	private TextView genderText;
	private TextView ageText;
	private TextView placeText;
	private TextView timeText;
	private Button backButton;
	private Button inviteButton;

	private ImageView userImage;

	private View leftArrow;
	private View rightArrow;

	private List<Lunchperson> lunchpersonList = null;
	private int index = 0;
	private ArrayList<Boolean> firstTimeLoading;
	private int peopleDistanceCounter = 0;
	private TreeSet<Integer> personsIdxToRemove = new TreeSet<>();
	private boolean showPersonInfo = false;
	private Lunchperson currentPerson;

	private LoadImageTaskFragment mLoadImageTaskFragment;
	private GetFromRESTTaskFragment mGetFromRESTTaskFragmentLunchperson;
	private GetFromRESTTaskFragment mGetFromRESTTaskFragmentDistance;
	private PostToRESTTaskFragment mPostToRESTTaskFragmentInvitation;

	private ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend_results);
		
		// get data from intent

		Bundle intent = getIntent().getExtras();
		ageFrom = intent.getInt("ageFrom", -1);
		ageTo = intent.getInt("ageTo", -1);
		gender = intent.getInt("gender", -1);
		placeId = intent.getString("place", null);
		maxDistance = intent.getInt("maxdistance", -1);
		maxTimeDiff = intent.getInt("maxtimediff", -1);
		timeHour = intent.getInt("timeHour", -1);
		timeMinute = intent.getInt("timeMinute", -1);
		selectedHobbies = (TreeSet<String>) intent.get("hobbies");
		if (selectedHobbies == null) {
			selectedHobbies = new TreeSet<>();
		}
		Boolean showAll = (Boolean) intent.get("showAllFriends");
		if ( showAll == null ) {
			this.showAllFriends = false;
		} else {
			showAllFriends = showAll.booleanValue();
		}

		// set views

		leftArrow = findViewById(R.id.arrow_left);
		leftArrow.setOnClickListener(leftArrowClickListener);

		rightArrow = findViewById(R.id.arrow_right);
		rightArrow.setOnClickListener(rightArrowClickListener);

		backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(backButtonClickListener);

		inviteButton = findViewById(R.id.inviteButton);
		inviteButton.setOnClickListener(inviteButtonClickListener);

		fullnameText = findViewById(R.id.fullnameText);
		genderText = findViewById(R.id.genderText);
		ageText = findViewById(R.id.ageText);
		placeText = findViewById(R.id.placeText);
		timeText = findViewById(R.id.timeText);
		userImage = findViewById(R.id.userImage);

		fullnameText.setText("");
		genderText.setText("");
		ageText.setText("");
		placeText.setText("");
		timeText.setText("");

		userImage.setVisibility(View.GONE);
		LunchFriendsTools.setImageLoading(userImage, 120);
		leftArrow.setVisibility(View.GONE);
		rightArrow.setVisibility(View.GONE);

		progDialog = ProgressDialog.show(FriendResultsActivity.this, "",
				getResources().getString(R.string.loading_people), true);

		// start people loading task

		startGetRESTTaskLunchperson(LunchFriendsTools.DBServerBaseURI + "lunchperson");
	}

	private void startGetRESTTaskLunchperson(String uri) {
		Log.i(TAG, "Started loading people task.");
		FragmentManager fm = getFragmentManager();
		mGetFromRESTTaskFragmentLunchperson = (GetFromRESTTaskFragment) fm.findFragmentByTag("PeopleLoadTask");

		if (mGetFromRESTTaskFragmentLunchperson == null) {
			mGetFromRESTTaskFragmentLunchperson = new GetFromRESTTaskFragment();
			fm.beginTransaction().add(mGetFromRESTTaskFragmentLunchperson, "PeopleLoadTask").commit();
		}

		mGetFromRESTTaskFragmentLunchperson.mTask.setResponseClass(Lunchpersons.class);
		mGetFromRESTTaskFragmentLunchperson.executeTask(uri);
	}

	private void startGetRESTTaskDistance(String uri, int idx) {
		Log.i(TAG, "Started loading distance task.");
		FragmentManager fm = getFragmentManager();
		mGetFromRESTTaskFragmentDistance = (GetFromRESTTaskFragment) fm.findFragmentByTag("DistanceLoadTask");

		if (mGetFromRESTTaskFragmentDistance == null) {
			mGetFromRESTTaskFragmentDistance = new GetFromRESTTaskFragment();
			fm.beginTransaction().add(mGetFromRESTTaskFragmentDistance, "DistanceLoadTask").commit();
		}

		mGetFromRESTTaskFragmentDistance.mTask.setResponseClass(DistanceMatrixResponse.class);
		mGetFromRESTTaskFragmentDistance.mTask.setObject(Integer.valueOf(idx));
		mGetFromRESTTaskFragmentDistance.executeTask(uri);
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

	private void startPostRESTTaskInvitation(String uri) {
		Log.i(TAG, "Started post invitation task.");
		FragmentManager fm = getFragmentManager();
		mPostToRESTTaskFragmentInvitation = (PostToRESTTaskFragment) fm.findFragmentByTag("PostInvitationTask");

		if (mPostToRESTTaskFragmentInvitation == null) {
			mPostToRESTTaskFragmentInvitation = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mPostToRESTTaskFragmentInvitation, "PostInvitationTask").commit();
		}

		Invitation invitation = new Invitation();
		invitation.setPID(LoginSingle.getPerson().getPID());
		invitation.setTargetPID(currentPerson.getPID());
		invitation.setLID(currentPerson.getLID());

		mPostToRESTTaskFragmentInvitation.mTask.setPostObject(invitation);
		mPostToRESTTaskFragmentInvitation.mTask.setResponseClass(null);
		mPostToRESTTaskFragmentInvitation.executeTask(uri);

		Toast.makeText(FriendResultsActivity.this, R.string.inv_sent, Toast.LENGTH_SHORT).show();
	}

	private final View.OnClickListener leftArrowClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			index--;
			if ( index < 0 ) {
				index = 0;
				return;
			}
			userImage.setVisibility(View.GONE);
			if (lunchpersonList != null) {
				currentPerson = lunchpersonList.get(index);
				setPersonData();
			}
		}
	};

	private final View.OnClickListener rightArrowClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			index++;
			if ( index > lunchpersonList.size()-1 ) {
				index = lunchpersonList.size() - 1;
				return;
			}
			userImage.setVisibility(View.GONE);
			if (lunchpersonList != null) {
				currentPerson = lunchpersonList.get(index);
				setPersonData();
			}
		}
	};

	private final View.OnClickListener backButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			onBackPressed();
		}
	};

	private final View.OnClickListener inviteButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startPostRESTTaskInvitation(LunchFriendsTools.DBServerBaseURI + "postinvitation");
		}
	};

	@Override
	public void postToRESTOnPreExecute() {

	}

	@Override
	public void postToRESTOnPostExecute(Object obj) {
	}

	@Override
	public void loadImageOnPreExecute() {
	}

	@Override
	public void loadImageOnPostExecute(Bitmap bitmap) {
		if ( bitmap != null ) {
			if ( showPersonInfo ) {
				setPersonInfo();
			}

			bitmap = LunchFriendsTools.scaleBitmap(bitmap, 150);
			userImage.setImageBitmap(bitmap);
			userImage.setVisibility(View.VISIBLE);
		} else {
			if ( showPersonInfo ) {
				setPersonInfo();
			}

			userImage.setVisibility(View.GONE);
		}
	}

	@Override
	public void getFromRESTOnPreExecute() {
	}

	@Override
	public void getFromRESTOnPostExecute(Object obj, GetFromRESTTaskFragment.GetRESTTask task) {
		if ( obj == null ) {
			progDialog.dismiss();
			setNoPeopleFound();
			return;
		}

		if ( task.getResponseClass() == DistanceMatrixResponse.class ) {
			peopleDistanceCounter++;

			int idx = (Integer)task.getObject();

			if ( obj instanceof DistanceMatrixResponse && obj != null ) {
				try {
					DistanceMatrixResponse response = (DistanceMatrixResponse) obj;
					int distance = response.getRow().getElement().getDistance().getValue();
					if ( distance > maxDistance * 100 ) {
						personsIdxToRemove.add(idx);
					}
				} catch ( NullPointerException e ) {
					// don't delete lunchperson
					Log.w(TAG, "Failed loading distance.");
				}
			}

		} else {
			if ( obj instanceof Lunchpersons && obj != null ) {
				lunchpersonList = ((Lunchpersons) obj).getLunchperson();
			}

			if ( lunchpersonList != null ) {
				if ( ! lunchpersonList.isEmpty() ) {

					if ( ! showAllFriends ) {
						filterPersons();
					} else {
						progDialog.dismiss();
						firstTimeLoading = new ArrayList<>(lunchpersonList.size());
						for (int i = 0; i < lunchpersonList.size(); i++) {
							firstTimeLoading.add(true);
						}

						// delete person that is logged in
						for ( int i = 0 ; i < lunchpersonList.size() ; i++ ) {
							Lunchperson p = lunchpersonList.get(i);
							if ( p.getPID() == LoginSingle.getPerson().getPID() ) {
								lunchpersonList.remove(i);
							}
						}

						if ( lunchpersonList.isEmpty() ) {
							setNoPeopleFound();
						} else {
							currentPerson = lunchpersonList.get(index);
							setPersonData();
						}
					}

				} else {
					setNoPeopleFound();
				}
			} else {
				setNoPeopleFound();
			}
		}

		if ( ! showAllFriends && ( ( lunchpersonList != null && peopleDistanceCounter == lunchpersonList.size() )
				|| placeId == null || maxDistance == -1 ) ) {
			// delete too far persons
			Iterator<Integer> iterator = personsIdxToRemove.iterator();
			int ctr = 0;
			while ( iterator.hasNext() ) {
				int i = iterator.next();
				lunchpersonList.remove(i - ctr);
				ctr++;
			}

			// everything set, show first person
			progDialog.dismiss();

			if ( lunchpersonList.isEmpty() ) {
				setNoPeopleFound();
				return;
			}

			firstTimeLoading = new ArrayList<>(lunchpersonList.size());
			for (int i = 0; i < lunchpersonList.size(); i++) {
				firstTimeLoading.add(true);
			}

			currentPerson = lunchpersonList.get(index);
			setPersonData();
		}
	}

	private void setPersonData() {
		if ( ! firstTimeLoading.isEmpty() && firstTimeLoading.get(index) ) {
			setPersonInfo();

			LunchFriendsTools.setImageLoading(userImage, 120);
			userImage.setVisibility(View.VISIBLE);
			firstTimeLoading.set(index, false);

			showPersonInfo = false;
		} else {
			hidePersonInfo();

			showPersonInfo = true;
		}

		startLoadImageTask(currentPerson.getImageuri());
	}

	private void setNoPeopleFound() {
		fullnameText.setText(getResources().getString(R.string.no_people));
		genderText.setText("");
		ageText.setText(getResources().getString(R.string.different_search));
		placeText.setText("");
		timeText.setText("");
		userImage.setVisibility(View.GONE);
		leftArrow.setVisibility(View.GONE);
		rightArrow.setVisibility(View.GONE);
		backButton.setVisibility(View.VISIBLE);
	}

	private void hidePersonInfo() {
		fullnameText.setText("");
		genderText.setText("");
		ageText.setText("");
		timeText.setText("");
		placeText.setText("");
	}

	private void setPersonInfo() {
		fullnameText.setText(getResources().getString(R.string.fullname_colon) + " " + currentPerson.getRealname());
		String genderString;
		if ( LunchFriendsTools.isLanguage("cs", FriendResultsActivity.this) ) {
			genderString = GenderList.GENDERS_CONVERT.get(currentPerson.getGender());
		} else {
			genderString = currentPerson.getGender();
		}
		genderText.setText(getResources().getString(R.string.gender_colon) + " " + genderString);
		if ( currentPerson.getAge() == -1 ) {
			ageText.setText(getString(R.string.age_colon) + " " + getString(R.string.not_available));
		} else {
			ageText.setText(getString(R.string.age_colon) + " " + currentPerson.getAge());
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Date date;
		try {
			date = format.parse(currentPerson.getLdate());
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int hours = calendar.get(Calendar.HOUR_OF_DAY);
			int minutes = calendar.get(Calendar.MINUTE);
			timeText.setText(getResources().getString(R.string.time_colon) + " " + hours + ":" +
					( minutes < 10 ? ("0" + minutes) : minutes));
		} catch (ParseException e) {
			try {
				format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				date = format.parse(currentPerson.getLdate());
				Calendar calendar = Calendar.getInstance();
				calendar.setTime(date);
				int hours = calendar.get(Calendar.HOUR_OF_DAY);
				int minutes = calendar.get(Calendar.MINUTE);
				timeText.setText(getResources().getString(R.string.time_colon) + " " + hours + ":" +
						( minutes < 10 ? ("0" + minutes) : minutes));
			}
			catch (ParseException ex ) {
				Log.w(TAG, "Failed parsing date.");
				timeText.setText(getResources().getString(R.string.time_colon) + " " + currentPerson.getLdate());
			}
		}

		leftArrow.setVisibility(View.VISIBLE);
		rightArrow.setVisibility(View.VISIBLE);
		inviteButton.setVisibility(View.VISIBLE);

		// load place name from place id
		GeoDataClient geoDataClient = Places.getGeoDataClient(FriendResultsActivity.this, null);
		geoDataClient.getPlaceById(currentPerson.getPlaceid()).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
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

	private void filterPersons() {
		for ( int i = 0 ; i < lunchpersonList.size() ; i++ ) {
			Lunchperson p = lunchpersonList.get(i);
			if ( placeId != null && maxDistance != -1 ) {
				matchPlace(p.getPlaceid(), i);
			}
			if ( ( p.getAge() < ageFrom && ageFrom != -1 ) ||
					( p.getAge() > ageTo && ageTo != -1 ) ||
					( ! p.getGender().equals(GenderList.GENDERS_EN[gender]) && gender != 0 ) ||
					( selectedHobbies.size() != 0 && ! matchHobbies(p.getHobbies()) ) ||
					! matchDate(p.getLdate()) ||
					p.getPID() == LoginSingle.getPerson().getPID() ) {
				lunchpersonList.remove(i);
				i--;
			}
		}
	}

	private boolean matchHobbies(String hobbies) {
		TreeSet<Integer> selectedHobbiesInts = new TreeSet<>();
		if ( LunchFriendsTools.isLanguage("cs", FriendResultsActivity.this) ) {
			for (String s : selectedHobbies) {
				selectedHobbiesInts.add(Arrays.binarySearch(HobbyList.HOBBIES_CS, s));
			}
		} else {
			for (String s : selectedHobbies) {
				selectedHobbiesInts.add(Arrays.binarySearch(HobbyList.HOBBIES_EN, s));
			}
		}

		Scanner s = new Scanner(hobbies);
		TreeSet<Integer> personHobbies = new TreeSet<>();
		while ( s.hasNextInt() ) {
			personHobbies.add(s.nextInt());
		}
		s.close();

		for ( int i : selectedHobbiesInts ) {
			if ( personHobbies.contains(i) ) {
				return true;
			}
		}

		return false;
	}

	private boolean matchDate(String targetPersonDate) {
		if ( timeHour == -1 || timeMinute == -1 || maxTimeDiff == -1 ) {
			return true;
		}

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date personDate;
		try {
			personDate = format.parse(targetPersonDate);
		} catch (ParseException e) {
			// don't delete person
			Log.w(TAG, "Failed parsing date.");
			return true;
		}

		Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, timeHour);
		calendar.set(Calendar.MINUTE, timeMinute);
		calendar.set(Calendar.SECOND, 0);
		Date chosenDate = calendar.getTime();

		long diff = personDate.getTime() - chosenDate.getTime();
		long seconds = diff / 1000;
		long minutes = seconds / 60;
		minutes = minutes < 0 ? -minutes : minutes;

		if ( minutes > maxTimeDiff ) {
			return false;
		}

		return true;
	}

	private void matchPlace(String targetPersonPlaceId, int idx) {
		startGetRESTTaskDistance("https://maps.googleapis.com/maps/api/distancematrix/xml?origins=place_id:"
				+ placeId + "&destinations=place_id:" + targetPersonPlaceId
				+ "&language=en&key=ENTER_API_KEY",
				idx
		);
	}
}
