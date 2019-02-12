package huzevka.lunchfriends.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.TreeSet;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.list.GenderList;
import huzevka.lunchfriends.list.HobbyList;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class SearchFriends1Activity extends AppCompatActivity {

	private static final String TAG = "SearchFriends1Activity";

	private int chosenGender = -1;

	private EditText mAgeFrom;
	private EditText mAgeTo;
	private int mAgeFromInt;
	private int mAgeToInt;

	private int maxDistance;
	private int maxTimeDiff;

	private String chosenPlaceId;
	private int hour;
	private int minute;

	private ListView hobbyListView;
	private ArrayAdapter<String> hobbyAdapter;
	private EditText searchHobbyEditText;
	private TreeSet<String> selectedHobbies = new TreeSet<>();
	private boolean hobbies_cs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_friends_1);

		// set views
		Spinner genderSpinner = findViewById(R.id.spinnerGender);
		ArrayAdapter<String> genderAdapter;
		if ( LunchFriendsTools.isLanguage("cs", SearchFriends1Activity.this) ) {
			genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GenderList.GENDERS_CS);
		} else {
			genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GenderList.GENDERS_EN);
		}
		genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(genderAdapter);
		genderSpinner.setOnItemSelectedListener(genderOnSelectedListener);

		mAgeFrom = findViewById(R.id.editFrom);
		mAgeTo = findViewById(R.id.editTo);

		findViewById(R.id.nextButton).setOnClickListener(nextButtonListener);

		// begin hobbylistview ////////////////////////////
		hobbyListView = findViewById(R.id.hobbyList);
		searchHobbyEditText = findViewById(R.id.searchHobbyEditText);
		searchHobbyEditText.addTextChangedListener(filterTextWatcher);

		if ( LunchFriendsTools.isLanguage("cs", SearchFriends1Activity.this) ) {
			hobbyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, HobbyList.HOBBIES_CS);
			hobbies_cs = true;
		} else {
			hobbyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice, HobbyList.HOBBIES_EN);
			hobbies_cs = false;
		}
		hobbyListView.setAdapter(hobbyAdapter);

		hobbyListView.setItemsCanFocus(false);
		hobbyListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		hobbyListView.setTextFilterEnabled(true);
		hobbyListView.setFastScrollEnabled(false);
		hobbyListView.invalidate();
		// end hobbylistview //////////////////////////////

		// get data from intent and set views
		Bundle intent = getIntent().getExtras();
		mAgeFromInt = intent.getInt("ageFrom", -1);
		if ( mAgeFromInt != -1 ) {
			mAgeFrom.setText(String.valueOf(mAgeFromInt));
		}
		mAgeToInt = intent.getInt("ageTo", -1);
		if ( mAgeToInt != -1 ) {
			mAgeTo.setText(String.valueOf(mAgeToInt));
		}
		chosenGender = intent.getInt("gender", -1);
		if ( chosenGender != -1 ) {
			genderSpinner.setSelection(chosenGender);
		}
		selectedHobbies = (TreeSet<String>) intent.get("hobbies");
		if (selectedHobbies == null) {
			selectedHobbies = new TreeSet<>();
		}
		maxDistance = intent.getInt("maxdistance", -1);
		maxTimeDiff = intent.getInt("maxtimediff", -1);
		// select items in listview contained in set
		searchHobbyEditText.setText("");
		chosenPlaceId = intent.getString("place");
		hour = intent.getInt("timeHour", -1);
		minute = intent.getInt("timeMinute", -1);

	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			SparseBooleanArray checked = hobbyListView.getCheckedItemPositions();
			int len = (hobbies_cs ? HobbyList.HOBBIES_CS.length : HobbyList.HOBBIES_EN.length);
			for (int i = 0 ; i < len ; i++) {
				if ( checked.get(i) == true ) {
					Object o = hobbyAdapter.getItem(i);
					String name = o.toString();
					// if the arraylist does not contain the name, add it
					if ( ! selectedHobbies.contains(name) ) {
						selectedHobbies.add(name);
					}
				} else if ( checked.get(i, true) == false && i < hobbyAdapter.getCount() ) {
					Object o = hobbyAdapter.getItem(i);
					String name = o.toString();
					// if the arraylist contains the name, remove it
					if ( selectedHobbies.contains(name) ) {
						selectedHobbies.remove(name);
					}
				}
			}
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
			hobbyAdapter.getFilter().filter(s);
		}

		public void afterTextChanged(Editable s) {
			// Uncheck everything:
			for (int i = 0; i < hobbyListView.getCount(); i++){
				hobbyListView.setItemChecked(i, false);
			}

			hobbyAdapter.getFilter().filter(s, new Filter.FilterListener() {
				public void onFilterComplete(int count) {
					hobbyAdapter.notifyDataSetChanged();
					for (int i = 0 ; i < hobbyAdapter.getCount() ; i ++) {
						// if the current (filtered)
						// listview you are viewing has the name included in the list,
						// check the box
						Object o = hobbyAdapter.getItem(i);
						String name = o.toString();
						if (selectedHobbies.contains(name)) {
							hobbyListView.setItemChecked(i, true);
						}
					}

				}
			});
		}
	};

	AdapterView.OnItemSelectedListener genderOnSelectedListener = new AdapterView.OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
			chosenGender = pos;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	};

	private final View.OnClickListener nextButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			int ageFromInt = -1, ageToInt = -1;

			if ( chosenGender == -1 ) {
				chosenGender = 0;
			}
			try {
				ageFromInt = Integer.parseInt(mAgeFrom.getText().toString());
				ageToInt = Integer.parseInt(mAgeTo.getText().toString());
			} catch ( NumberFormatException e ) {
				Log.i(TAG, "mAgeFrom and/or mAgeTo is empty");
			}

			// put checked items in listview into the set and remove unchecked
			searchHobbyEditText.setText("");
			startSearchFriends2Activity(ageFromInt, ageToInt);
		}
	};

	private void startSearchFriends2Activity(int ageFromInt, int ageToInt) {
		Intent intent = new Intent(SearchFriends1Activity.this.getApplicationContext(), SearchFriends2Activity.class);
		intent.putExtra("ageFrom", ageFromInt);
		intent.putExtra("ageTo", ageToInt);
		intent.putExtra("gender", chosenGender);
		intent.putExtra("hobbies", selectedHobbies);
		intent.putExtra("place", chosenPlaceId);
		intent.putExtra("maxdistance", maxDistance);
		intent.putExtra("maxtimediff", maxTimeDiff);
		intent.putExtra("timeHour", hour);
		intent.putExtra("timeMinute", minute);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

}
