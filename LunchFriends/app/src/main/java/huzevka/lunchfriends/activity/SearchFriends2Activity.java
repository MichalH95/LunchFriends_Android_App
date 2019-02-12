package huzevka.lunchfriends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.Calendar;
import java.util.TreeSet;

import huzevka.lunchfriends.R;

public class SearchFriends2Activity extends AppCompatActivity {

	private static final String TAG = "SearchFriends2Activity";

	private final int PLACE_PICKER_REQUEST = 223;

	private int chosenGender;
	private String chosenPlaceId;
	private int maxDistance;
	private int maxTimeDiff;
	private int mAgeFromInt;
	private int mAgeToInt;
	private TreeSet<String> selectedHobbies;

	private Place chosenPlace;
	private TextView placeName;

	private SeekBar distanceBar;
	private TextView distanceText;
	private boolean distanceDiffChanged = false;

	private SeekBar timeDiffBar;
	private TextView timeDiffText;
	private boolean timeDiffChanged = false;


	private NumberPicker hourPicker;
	private NumberPicker minutePicker;
	private int hour;
	private int minute;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friends_2);

		// set views

		findViewById(R.id.previousButton).setOnClickListener(previousButtonListener);

		findViewById(R.id.searchButton).setOnClickListener(searchButtonListener);

		findViewById(R.id.pickPlaceButton).setOnClickListener(pickPlaceButtonListener);

		placeName = findViewById(R.id.placeName);

		distanceText = findViewById(R.id.maxDistanceText);
		distanceBar = findViewById(R.id.distanceBar);
		distanceBar.setMax(50);
		distanceBar.setOnSeekBarChangeListener(distanceSeekBarChangeListener);

		timeDiffText = findViewById(R.id.maxTimeDiffText);
		timeDiffBar = findViewById(R.id.timeDiffBar);
		timeDiffBar.setMax(180);
		timeDiffBar.setOnSeekBarChangeListener(timeDiffSeekBarChangeListener);

		hourPicker = findViewById(R.id.hourPicker);
		hourPicker.setMinValue(0);
		hourPicker.setMaxValue(23);
		hourPicker.setValue(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

		minutePicker = findViewById(R.id.minutePicker);
		minutePicker.setMinValue(0);
		minutePicker.setMaxValue(59);
		minutePicker.setValue(Calendar.getInstance().get(Calendar.MINUTE));

		// get data from intent and set views

		Bundle intent = getIntent().getExtras();
		mAgeFromInt = intent.getInt("ageFrom");
		mAgeToInt = intent.getInt("ageTo");
		chosenGender = intent.getInt("gender", -1);
		selectedHobbies = (TreeSet<String>) intent.get("hobbies");
		if (selectedHobbies == null) {
			selectedHobbies = new TreeSet<>();
		}
		chosenPlaceId = intent.getString("place");
		if ( chosenPlaceId != null ) {
			setPlaceName(chosenPlaceId);
		}
		maxDistance = intent.getInt("maxDistance", -1);
		hour = intent.getInt("timeHour", -1);
		if ( hour != -1 ) {
			hourPicker.setValue(hour);
		}
		minute = intent.getInt("timeMinute", -1);
		if ( minute != -1 ) {
			minutePicker.setValue(minute);
		}
		maxDistance = intent.getInt("maxdistance", -1);
		if ( maxDistance != -1 ) {
			distanceBar.setProgress(maxDistance);
		}
		maxTimeDiff = intent.getInt("maxtimediff", -1);
		if ( maxTimeDiff != -1 ) {
			timeDiffBar.setProgress(maxTimeDiff);
		}
	}

	private SeekBar.OnSeekBarChangeListener distanceSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			distanceText.setText((progress/10) + "." + (progress%10) + " km");
			distanceDiffChanged = true;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};

	private SeekBar.OnSeekBarChangeListener timeDiffSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
			timeDiffText.setText(progress + " min");
			timeDiffChanged = true;
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {

		}
	};

	private final View.OnClickListener pickPlaceButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

			try {
				startActivityForResult(builder.build(SearchFriends2Activity.this), PLACE_PICKER_REQUEST);
			} catch (GooglePlayServicesRepairableException e) {
				e.printStackTrace();
			} catch (GooglePlayServicesNotAvailableException e) {
				e.printStackTrace();
			}
		}
	};

	private final View.OnClickListener previousButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startActivity(SearchFriends1Activity.class, null);
		}
	};

	private final View.OnClickListener searchButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startActivity(FriendResultsActivity.class, false);
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PLACE_PICKER_REQUEST) {
			if (resultCode == RESULT_OK) {
				chosenPlace = PlacePicker.getPlace(this, data);
				placeName.setText(chosenPlace.getName());
			}
		}
	}

	private void setPlaceName(String placeId) {
		GeoDataClient geoDataClient = Places.getGeoDataClient(SearchFriends2Activity.this, null);
		geoDataClient.getPlaceById(placeId).addOnCompleteListener(new OnCompleteListener<PlaceBufferResponse>() {
			@Override
			public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
				if (task.isSuccessful()) {
					PlaceBufferResponse places = task.getResult();
					Place myPlace = places.get(0);
					placeName.setText(myPlace.getName());
					places.release();
				}
			}
		});
	}

	private void startActivity(Class<? extends AppCompatActivity> cl, Boolean showAllFriendsFlag) {
		if (chosenPlace != null) {
			chosenPlaceId = chosenPlace.getId();
		}

		if (distanceDiffChanged) {
			maxDistance = distanceBar.getProgress();
		} else {
			maxDistance = -1;
		}

		hour = hourPicker.getValue();
		minute = minutePicker.getValue();

		if ( timeDiffChanged ) {
			maxTimeDiff = timeDiffBar.getProgress();
		} else {
			maxTimeDiff = -1;
		}

		Intent intent = new Intent(SearchFriends2Activity.this.getApplicationContext(), cl);
		intent.putExtra("ageFrom", mAgeFromInt);
		intent.putExtra("ageTo", mAgeToInt);
		intent.putExtra("gender", chosenGender);
		intent.putExtra("hobbies", selectedHobbies);
		intent.putExtra("place", chosenPlaceId);
		intent.putExtra("maxdistance", maxDistance);
		intent.putExtra("maxtimediff", maxTimeDiff);
		intent.putExtra("timeHour", hour);
		intent.putExtra("timeMinute", minute);
		intent.putExtra("showAllFriends", showAllFriendsFlag);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		startActivity(SearchFriends1Activity.class, null);
	}
}
