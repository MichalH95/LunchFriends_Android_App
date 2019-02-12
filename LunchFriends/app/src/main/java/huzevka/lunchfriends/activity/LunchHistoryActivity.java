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
import java.util.TreeSet;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.list.GenderList;
import huzevka.lunchfriends.model.Lunchperson;
import huzevka.lunchfriends.model.Lunchpersons;
import huzevka.lunchfriends.model.Person;
import huzevka.lunchfriends.model.distance_matrix.DistanceMatrixResponse;
import huzevka.lunchfriends.taskfragment.LoadImageTaskFragment;
import huzevka.lunchfriends.taskfragment.PostToRESTTaskFragment;
import huzevka.lunchfriends.tools.LoginSingle;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class LunchHistoryActivity extends AppCompatActivity implements
		PostToRESTTaskFragment.TaskCallbacks, LoadImageTaskFragment.TaskCallbacks {

	private final String TAG = "LunchHistoryActivity";

	private TextView fullnameText;
	private TextView genderText;
	private TextView ageText;
	private TextView placeText;
	private TextView timeText;
	private Button backButton;

	private ImageView userImage;

	private View leftArrow;
	private View rightArrow;

	private List<Lunchperson> lunchpersonList = null;
	private int index = 0;
	private ArrayList<Boolean> firstTimeLoading;
	private boolean showPersonInfo = false;
	private Lunchperson currentPerson;

	private PostToRESTTaskFragment mPostToRESTTaskFragmentHistory;
	private LoadImageTaskFragment mLoadImageTaskFragment;

	private ProgressDialog progDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_history);

		// set views

		leftArrow = findViewById(R.id.arrow_left);
		leftArrow.setOnClickListener(leftArrowClickListener);

		rightArrow = findViewById(R.id.arrow_right);
		rightArrow.setOnClickListener(rightArrowClickListener);

		backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(backButtonClickListener);

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

		progDialog = ProgressDialog.show(LunchHistoryActivity.this, "",
				getString(R.string.loading_history), true);

		startPostRESTTaskHistory(LunchFriendsTools.DBServerBaseURI + "gethistory");
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

	private void startPostRESTTaskHistory(String uri) {
		Log.i(TAG, "Started post invitation task.");
		FragmentManager fm = getFragmentManager();
		mPostToRESTTaskFragmentHistory = (PostToRESTTaskFragment) fm.findFragmentByTag("GetHistoriesTask");

		if (mPostToRESTTaskFragmentHistory == null) {
			mPostToRESTTaskFragmentHistory = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mPostToRESTTaskFragmentHistory, "GetHistoriesTask").commit();
		}

		Person person = new Person();
		person.setPID(LoginSingle.getPerson().getPID());

		mPostToRESTTaskFragmentHistory.mTask.setPostObject(person);
		mPostToRESTTaskFragmentHistory.mTask.setResponseClass(Lunchpersons.class);
		mPostToRESTTaskFragmentHistory.executeTask(uri);
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
	public void postToRESTOnPreExecute() {

	}

	@Override
	public void postToRESTOnPostExecute(Object obj) {
		if ( obj == null ) {
			progDialog.dismiss();
			setNoPeopleFound();
			return;
		}

		if ( obj instanceof Lunchpersons && obj != null ) {
			lunchpersonList = ((Lunchpersons) obj).getLunchperson();
			// sort lunch history by date
			Object[] origArr = lunchpersonList.toArray();
			Lunchperson[] arr = Arrays.copyOf(origArr, origArr.length, Lunchperson[].class);
			Arrays.sort(arr);
			lunchpersonList = Arrays.asList(arr);

			if ( lunchpersonList != null && ! lunchpersonList.isEmpty() ) {
				progDialog.dismiss();
				firstTimeLoading = new ArrayList<>(lunchpersonList.size());
				for (int i = 0; i < lunchpersonList.size(); i++) {
					firstTimeLoading.add(true);
				}

				currentPerson = lunchpersonList.get(index);
				setPersonData();
			} else {
				progDialog.dismiss();
				setNoPeopleFound();
			}
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
		fullnameText.setText(getResources().getString(R.string.history_empty));
		genderText.setText("");
		ageText.setText("");
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
		if ( LunchFriendsTools.isLanguage("cs", LunchHistoryActivity.this) ) {
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

		timeText.setText(getResources().getString(R.string.time_colon) + " " + currentPerson.getLdate());

		leftArrow.setVisibility(View.VISIBLE);
		rightArrow.setVisibility(View.VISIBLE);

		// load place name from place id
		GeoDataClient geoDataClient = Places.getGeoDataClient(LunchHistoryActivity.this, null);
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


}
