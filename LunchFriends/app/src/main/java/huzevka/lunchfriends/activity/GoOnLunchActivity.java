package huzevka.lunchfriends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.NumberPicker;
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

import huzevka.lunchfriends.R;

public class GoOnLunchActivity extends AppCompatActivity {

	private static final String TAG = "SearchFriends2Activity";

	private final int PLACE_PICKER_REQUEST = 223;

	private String chosenPlaceId;

	private Place chosenPlace;
	private TextView placeName;
	private TextView textRequired;

	private NumberPicker hourPicker;
	private NumberPicker minutePicker;
	private int hour;
	private int minute;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_go_on_lunch);

		// set views

		findViewById(R.id.createLunchButton).setOnClickListener(createLunchButtonListener);

		findViewById(R.id.pickPlaceButton).setOnClickListener(pickPlaceButtonListener);

		textRequired = findViewById(R.id.textRequired);

		placeName = findViewById(R.id.placeName);

		hourPicker = findViewById(R.id.hourPicker);
		hourPicker.setMinValue(0);
		hourPicker.setMaxValue(23);
		hourPicker.setValue(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));

		minutePicker = findViewById(R.id.minutePicker);
		minutePicker.setMinValue(0);
		minutePicker.setMaxValue(59);
		minutePicker.setValue(Calendar.getInstance().get(Calendar.MINUTE));
	}

	private final View.OnClickListener pickPlaceButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

			try {
				startActivityForResult(builder.build(GoOnLunchActivity.this), PLACE_PICKER_REQUEST);
			} catch (GooglePlayServicesRepairableException e) {
				e.printStackTrace();
			} catch (GooglePlayServicesNotAvailableException e) {
				e.printStackTrace();
			}
		}
	};

	private final View.OnClickListener createLunchButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if ( chosenPlace == null ) {
				textRequired.setVisibility(View.VISIBLE);
			} else {
				startCreateLunchResultActivity();
			}
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

	private void startCreateLunchResultActivity() {
		if (chosenPlace != null) {
			chosenPlaceId = chosenPlace.getId();
		}

		hour = hourPicker.getValue();
		minute = minutePicker.getValue();

		Intent intent = new Intent(GoOnLunchActivity.this.getApplicationContext(), CreateLunchResultActivity.class);
		intent.putExtra("place", chosenPlaceId);
		intent.putExtra("timeHour", hour);
		intent.putExtra("timeMinute", minute);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}
