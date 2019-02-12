package huzevka.lunchfriends.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.TreeSet;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.list.GenderList;
import huzevka.lunchfriends.list.HobbyList;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class RegisterActivity extends AppCompatActivity {

	private static final String TAG = "RegisterActivity";
	private String username;
	private String password;
	private String fullname;
	private int age;
	private String email;
	private String phone;
	private int chosenGender = -1;
	private String profilePictureUri;

	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText fullnameEditText;
	private EditText ageEditText;
	private EditText emailEditText;
	private EditText phoneEditText;
	private EditText profilePicEditText;

	private TextView textRequired;

	private ListView hobbyListView;
	private ArrayAdapter<String> hobbyAdapter;
	private EditText searchHobbyEditText;
	private TreeSet<String> selectedHobbies = new TreeSet<>();
	private boolean hobbies_cs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		// set views
		Spinner genderSpinner = findViewById(R.id.spinnerGenderRegister);
		ArrayAdapter<String> genderAdapter;
		if ( LunchFriendsTools.isLanguage("cs", RegisterActivity.this) ) {
			genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GenderList.GENDERS_CS);
		} else {
			genderAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, GenderList.GENDERS_EN);
		}
		genderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		genderSpinner.setAdapter(genderAdapter);
		genderSpinner.setOnItemSelectedListener(genderOnSelectedListener);

		findViewById(R.id.backButton).setOnClickListener(backButtonClickListener);
		findViewById(R.id.registerButton).setOnClickListener(registerButtonListener);

		textRequired = findViewById(R.id.textRequired);

		usernameEditText = findViewById(R.id.usernameEditText);
		passwordEditText = findViewById(R.id.passwordEditText);
		fullnameEditText = findViewById(R.id.fullnameEditText);
		ageEditText = findViewById(R.id.ageEditText);
		emailEditText = findViewById(R.id.emailEditText);
		phoneEditText = findViewById(R.id.phoneEditText);
		profilePicEditText = findViewById(R.id.profilePicEditText);

		// begin hobbylistview ///////////////////
		hobbyListView = findViewById(R.id.hobbyList);
		searchHobbyEditText = findViewById(R.id.searchHobbyEditText);
		searchHobbyEditText.addTextChangedListener(filterTextWatcher);

		if ( LunchFriendsTools.isLanguage("cs", RegisterActivity.this) ) {
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
		// select items in listview contained in set
		searchHobbyEditText.setText("");
		// end hobbylistview /////////////////////
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
						// if the current (filtered) listview you are viewing has
						// the name included in the list, check the box
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

	private final View.OnClickListener backButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			onBackPressed();
		}
	};

	private final View.OnClickListener registerButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			// check if all fields are non-empty
			if ( usernameEditText.getText().toString().equals("") ||
					passwordEditText.getText().toString().equals("") ||
					fullnameEditText.getText().toString().equals("") ||
					profilePicEditText.getText().toString().equals("") ||
					emailEditText.getText().toString().equals("") ||
					phoneEditText.getText().toString().equals("") ||
					ageEditText.getText().toString().equals("") ) {
				textRequired.setVisibility(View.VISIBLE);
			} else {
				// get values and start register result activity
				username = usernameEditText.getText().toString();
				password = passwordEditText.getText().toString();
				password += "\n";
				try {
					MessageDigest md = MessageDigest.getInstance("SHA-256");

					md.update(password.getBytes());

					password = LunchFriendsTools.bytesToHex(md.digest());
				} catch (NoSuchAlgorithmException e) {
					Log.e(TAG, "SHA-256 not available.");
				}
				fullname = fullnameEditText.getText().toString();
				try {
					age = Integer.parseInt(ageEditText.getText().toString());
				} catch (Exception e) {
					age = -1;
				}
				email = emailEditText.getText().toString();
				phone = phoneEditText.getText().toString();
				profilePictureUri = profilePicEditText.getText().toString();
				searchHobbyEditText.setText("");
				startRegisterResultActivity();
			}
		}
	};

	private void startRegisterResultActivity() {
		Intent intent = new Intent(RegisterActivity.this.getApplicationContext(), RegisterResultActivity.class);
		intent.putExtra("username", username);
		intent.putExtra("passwordHash", password);
		intent.putExtra("fullname", fullname);
		intent.putExtra("age", age);
		intent.putExtra("email", email);
		intent.putExtra("gender", chosenGender);
		intent.putExtra("phone", phone);
		intent.putExtra("profilePictureUri", profilePictureUri);
		intent.putExtra("hobbies", selectedHobbies);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

}
