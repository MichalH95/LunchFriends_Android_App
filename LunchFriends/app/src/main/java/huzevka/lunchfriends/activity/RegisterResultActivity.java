package huzevka.lunchfriends.activity;

import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.TreeSet;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.list.GenderList;
import huzevka.lunchfriends.list.HobbyList;
import huzevka.lunchfriends.model.Lunchpersons;
import huzevka.lunchfriends.model.Person;
import huzevka.lunchfriends.model.RegisterResult;
import huzevka.lunchfriends.taskfragment.PostToRESTTaskFragment;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class RegisterResultActivity extends AppCompatActivity
		implements PostToRESTTaskFragment.TaskCallbacks {

	private static final String TAG = "RegisterResultActivity";

	private String username;
	private String passwordHash;
	private String fullname;
	private int age;
	private String email;
	private String phone;
	private int chosenGender = -1;
	private String profilePictureUri;
	private TreeSet<String> hobbiesStrings;
	private TreeSet<Integer> hobbiesInts = new TreeSet<>();
	private String hobbies = "";

	private TextView registeredText;

	private RegisterResult registerResult;

	private PostToRESTTaskFragment mPostToRESTTaskFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_result);

		// get data from intent

		Bundle intent = getIntent().getExtras();
		username = intent.getString("username");
		passwordHash = intent.getString("passwordHash");
		fullname = intent.getString("fullname");
		age = intent.getInt("age", -1);
		email = intent.getString("email");
		phone = intent.getString("phone");
		chosenGender = intent.getInt("gender", -1);
		profilePictureUri = intent.getString("profilePictureUri");
		hobbiesStrings = (TreeSet<String>) intent.get("hobbies");
		if (hobbiesStrings == null) {
			hobbiesStrings = new TreeSet<>();
		}

		if ( LunchFriendsTools.isLanguage("cs", RegisterResultActivity.this) ) {
			for (String s : hobbiesStrings) {
				hobbiesInts.add(Arrays.binarySearch(HobbyList.HOBBIES_CS, s));
			}
		} else {
			for (String s : hobbiesStrings) {
				hobbiesInts.add(Arrays.binarySearch(HobbyList.HOBBIES_EN, s));
			}
		}
		for ( Integer i : hobbiesInts ) {
			hobbies += " " + i;
		}
		hobbies += " ";

		// set views

		registeredText = findViewById(R.id.registeredText);
		findViewById(R.id.backButton).setOnClickListener(backButtonClickListener);

		startPostRESTTask(LunchFriendsTools.DBServerBaseURI + "register");
	}

	private void startPostRESTTask(String uri) {
		Log.i(TAG, "Started register task.");
		FragmentManager fm = getFragmentManager();
		mPostToRESTTaskFragment = (PostToRESTTaskFragment) fm.findFragmentByTag("RegisterTask");

		if (mPostToRESTTaskFragment == null) {
			mPostToRESTTaskFragment = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mPostToRESTTaskFragment, "RegisterTask").commit();
		}

		Person postPerson = new Person();
		postPerson.setUserid(username);
		postPerson.setPasswd(passwordHash);
		postPerson.setRealname(fullname);
		postPerson.setAge(age);
		postPerson.setGender(GenderList.GENDERS_EN[chosenGender]);
		postPerson.setImageuri(profilePictureUri);
		postPerson.setEmail(email);
		postPerson.setPhone(phone);
		postPerson.setHobbies(hobbies);

		mPostToRESTTaskFragment.mTask.setPostObject(postPerson);
		mPostToRESTTaskFragment.mTask.setResponseClass(RegisterResult.class);
		mPostToRESTTaskFragment.executeTask(uri);
	}

	@Override
	public void postToRESTOnPreExecute() {
	}

	@Override
	public void postToRESTOnPostExecute(Object obj) {
		if ( obj instanceof RegisterResult && obj != null ) {
			registerResult = ((RegisterResult)obj);
		}

		if ( registerResult != null ) {
			if (registerResult.isSuccess()) {
				registeredText.setText(R.string.registered);
			} else if (registerResult.isUseridUniqueError()) {
				registeredText.setText(R.string.registered_userid_fail);
			} else {
				registeredText.setText(R.string.registered_fail);
			}
		} else {
			registeredText.setText(R.string.registered_fail);
		}
	}

	private final View.OnClickListener backButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			onBackPressed();
		}
	};

}
