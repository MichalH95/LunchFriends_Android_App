package huzevka.lunchfriends.activity;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.services.people.v1.model.Birthday;
import com.google.api.services.people.v1.model.Date;
import com.google.api.services.people.v1.model.Gender;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.PhoneNumber;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.list.GenderList;
import huzevka.lunchfriends.model.Login;
import huzevka.lunchfriends.model.RegisterResult;
import huzevka.lunchfriends.taskfragment.LoadUserInfoTaskFragment;
import huzevka.lunchfriends.taskfragment.PostToRESTTaskFragment;
import huzevka.lunchfriends.tools.GoogleApiClientSingle;
import huzevka.lunchfriends.tools.LoginSingle;
import huzevka.lunchfriends.tools.LunchFriendsTools;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

public class MainActivity extends AppCompatActivity implements
		GoogleApiClient.OnConnectionFailedListener, PostToRESTTaskFragment.TaskCallbacks,
		LoadUserInfoTaskFragment.TaskCallbacks {

	private static final String TAG = "MainActivity";
	private static final int RC_SIGN_IN = 9001;

	enum LunchLoginService { GOOGLE, FACEBOOK, SERVER }

	private EditText mUsernameEditText;
	private EditText mPasswordEditText;

	private String username;
	private String passwordHash;

	// for facebook login
	private CallbackManager callbackManager;
	private Profile profile;
	huzevka.lunchfriends.model.Person postPersonFB = new huzevka.lunchfriends.model.Person();
	// end facebook login

	// for google login
	private Person googleLoginPerson;
	private GoogleSignInAccount account;
	// end google login

	private ProgressDialog progDialog;
	private LunchLoginService loginService;

	private PostToRESTTaskFragment mLoginPostRESTTaskFragment;
	private huzevka.lunchfriends.model.LoginResult loginResult;
	private PostToRESTTaskFragment mDeleteOldLunchRESTTaskFragment;
	private LoadUserInfoTaskFragment mLoadUserInfoTaskFragment;
	private PostToRESTTaskFragment mRegisterRESTTaskFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		loadLocale();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// set views
		findViewById(R.id.loginButton).setOnClickListener(loginButtonListener);
		findViewById(R.id.google_sign_in_button).setOnClickListener(signInButtonListener);
		mUsernameEditText = findViewById(R.id.userIdEdit);
		mPasswordEditText = findViewById(R.id.passwordEdit);
		findViewById(R.id.changeLanguageButton).setOnClickListener(changeLanguageButtonListener);
		findViewById(R.id.registerButton).setOnClickListener(registerButtonListener);

		// setup Google login
		if ( GoogleApiClientSingle.getClient() == null ) {
			GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
					.requestEmail()
					.requestProfile()
					.requestScopes(new Scope(Scopes.PROFILE))
					.build();
			GoogleApiClientSingle.setClient(new GoogleApiClient.Builder(this)
					.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
					.build());
		}

		// setup google login button
		SignInButton signInButton = findViewById(R.id.google_sign_in_button);
		signInButton.setSize(SignInButton.SIZE_STANDARD);
		signInButton.setColorScheme(SignInButton.COLOR_LIGHT);

		// setup Facebook login
		callbackManager = CallbackManager.Factory.create();
		LoginButton fbLoginButton = findViewById(R.id.fb_login_button);
		fbLoginButton.setReadPermissions(Arrays.asList(
				"public_profile", "email", "user_birthday"));
		fbLoginButton.registerCallback(callbackManager, fbLoginButtonCallback);

		// delete old lunches and invitations
		startDeleteOldLunchRESTTask(LunchFriendsTools.DBServerBaseURI + "lunchdel");
	}

	@Override
	protected void onStart() {
		super.onStart();
		if ( GoogleApiClientSingle.getClient() != null ) {
			GoogleApiClientSingle.getClient().connect();
		}
		LoginManager.getInstance().logOut();
	}

	private final FacebookCallback<LoginResult> fbLoginButtonCallback = new FacebookCallback<LoginResult>() {

		private ProfileTracker mProfileTracker;

		@Override
		public void onSuccess(LoginResult loginResult) {
			GraphRequest request = GraphRequest.newMeRequest(
					loginResult.getAccessToken(),
					new GraphRequest.GraphJSONObjectCallback() {
						@Override
						public void onCompleted(JSONObject object, GraphResponse response) {
							try {
								String email = object.getString("email");
								if ( email == null ) {
									email = "None";
								}
								postPersonFB.setEmail(email);
								String birthday = object.getString("birthday"); // 01/31/1980 format
								int age = -1;
								if ( birthday != null ) {
									Calendar birthdate = Calendar.getInstance();
									birthdate.set(MONTH, Integer.parseInt(birthday.substring(0,2))-1);
									birthdate.set(Calendar.DAY_OF_MONTH, Integer.parseInt(birthday.substring(3,5)));
									birthdate.set(YEAR, Integer.parseInt(birthday.substring(6,10)));
									Calendar today = Calendar.getInstance();
									age = today.get(YEAR) - birthdate.get(YEAR);
									if (birthdate.get(MONTH) > today.get(MONTH) ||
											(birthdate.get(MONTH) == today.get(MONTH) && birthdate.get(DATE) > today.get(DATE))) {
										age--;
									}
								}
								postPersonFB.setAge(age);
								String gender = object.getString("gender");
								if ( gender.equals("male") || gender.equals("Male") ) {
									gender = "Male";
								} else if ( gender.equals("female") || gender.equals("Female") ) {
									gender = "Female";
								} else {
									gender = GenderList.GENDERS_EN[0];
								}
								postPersonFB.setGender(gender);

								progDialog = ProgressDialog.show(MainActivity.this, "",
										getResources().getString(R.string.logging_in), true);

								startFacebookRegisterRESTTask(LunchFriendsTools.DBServerBaseURI + "register");
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
			Bundle parameters = new Bundle();
			parameters.putString("fields", "email,gender,birthday");
			request.setParameters(parameters);
			request.executeAsync();

			loginService = LunchLoginService.FACEBOOK;

			if ( Profile.getCurrentProfile() == null ) {
				mProfileTracker = new ProfileTracker() {
					@Override
					protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
						profile = currentProfile;
						mProfileTracker.stopTracking();
					}
				};
			} else {
				profile = Profile.getCurrentProfile();
			}
		}

		@Override
		public void onCancel() {
		}

		@Override
		public void onError(FacebookException exception) {
			if ( ! LunchFriendsTools.isNetworkAvailable(MainActivity.this) ) {
				Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private final View.OnClickListener changeLanguageButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startChangeLanguageActivity();
		}
	};

	private final View.OnClickListener registerButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startRegisterActivity();
		}
	};

	private final View.OnClickListener loginButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if ( LunchFriendsTools.isNetworkAvailable(MainActivity.this) ) {
				loginService = LunchLoginService.SERVER;

				progDialog = ProgressDialog.show(MainActivity.this, "",
						getResources().getString(R.string.logging_in), true);

				username = mUsernameEditText.getText().toString();
				String password = mPasswordEditText.getText().toString();
				password += "\n";

				try {
					MessageDigest md = MessageDigest.getInstance("SHA-256");

					md.update(password.getBytes());

					passwordHash = LunchFriendsTools.bytesToHex(md.digest());
				} catch ( NoSuchAlgorithmException e ) {
					Log.e(TAG, "SHA-256 not available.");
					passwordHash = password;
				}

				startLoginPostRESTTask(LunchFriendsTools.DBServerBaseURI + "login");
			} else {
				Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private final View.OnClickListener signInButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if ( LunchFriendsTools.isNetworkAvailable(MainActivity.this) ) {
				Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(GoogleApiClientSingle.getClient());
				startActivityForResult(signInIntent, RC_SIGN_IN);
			} else {
				Toast.makeText(MainActivity.this, R.string.no_connection, Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		callbackManager.onActivityResult(requestCode, resultCode, data);
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == RC_SIGN_IN) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
		try {
			account = completedTask.getResult(ApiException.class);
			// signed in successfully
			progDialog = ProgressDialog.show(MainActivity.this, "",
					getResources().getString(R.string.logging_in), true);
			loginService = LunchLoginService.GOOGLE;
			startLoadUserInfoTask(account.getEmail());
		} catch (ApiException e) {
			Log.e(TAG, "signInResult:failed code=" + e.getStatusCode());
		}
	}

	@Override
	public void loadUserInfoOnPreExecute() {

	}

	@Override
	public void loadUserInfoOnPostExecute(Person taskPerson) {
		if ( taskPerson != null ) {
			googleLoginPerson = taskPerson;
		}
		startGoogleRegisterRESTTask(LunchFriendsTools.DBServerBaseURI + "register");
	}

	private void startHomeActivity(String username, Uri photoUri, LunchLoginService service) {
		Intent intent = new Intent(MainActivity.this.getApplicationContext(), HomeActivity.class);
		intent.putExtra("username", username);
		intent.putExtra("photoUri", photoUri);
		intent.putExtra("loginService", service);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	private void startChangeLanguageActivity() {
		Intent intent = new Intent(MainActivity.this.getApplicationContext(), ChangeLanguageActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	private void startRegisterActivity() {
		Intent intent = new Intent(MainActivity.this.getApplicationContext(), RegisterActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	private void startLoginPostRESTTask(String uri) {
		Log.i(TAG, "Started login task.");
		FragmentManager fm = getFragmentManager();
		mLoginPostRESTTaskFragment = (PostToRESTTaskFragment) fm.findFragmentByTag("LoginTask");

		if (mLoginPostRESTTaskFragment == null) {
			mLoginPostRESTTaskFragment = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mLoginPostRESTTaskFragment, "LoginTask").commit();
		}

		Login login = new Login();
		login.setUserid(username);

		mLoginPostRESTTaskFragment.mTask.setPostObject(login);
		mLoginPostRESTTaskFragment.mTask.setResponseClass(huzevka.lunchfriends.model.LoginResult.class);
		mLoginPostRESTTaskFragment.executeTask(uri);
	}

	private void startDeleteOldLunchRESTTask(String uri) {
		Log.i(TAG, "Started delete old lunch task.");
		FragmentManager fm = getFragmentManager();
		mDeleteOldLunchRESTTaskFragment = (PostToRESTTaskFragment) fm.findFragmentByTag("DeleteOldLunchTask");

		if (mDeleteOldLunchRESTTaskFragment == null) {
			mDeleteOldLunchRESTTaskFragment = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mDeleteOldLunchRESTTaskFragment, "DeleteOldLunchTask").commit();
		}

		mDeleteOldLunchRESTTaskFragment.mTask.setPostObject(null);
		mDeleteOldLunchRESTTaskFragment.mTask.setResponseClass(null);
		mDeleteOldLunchRESTTaskFragment.executeTask(uri);
	}

	private void startLoadUserInfoTask(String email) {
		Log.i(TAG, "Started load user info task.");
		FragmentManager fm = getFragmentManager();
		mLoadUserInfoTaskFragment = (LoadUserInfoTaskFragment) fm.findFragmentByTag("LoadUserInfoTask");

		if (mLoadUserInfoTaskFragment == null) {
			mLoadUserInfoTaskFragment = new LoadUserInfoTaskFragment();
			fm.beginTransaction().add(mLoadUserInfoTaskFragment, "LoadUserInfoTask").commit();
		}

		mLoadUserInfoTaskFragment.mTask.setContext(MainActivity.this);
		mLoadUserInfoTaskFragment.executeTask(email);
	}

	private void startGoogleRegisterRESTTask(String uri) {
		Log.i(TAG, "Started register task Google.");
		FragmentManager fm = getFragmentManager();
		mRegisterRESTTaskFragment = (PostToRESTTaskFragment) fm.findFragmentByTag("RegisterTask");

		if (mRegisterRESTTaskFragment == null) {
			mRegisterRESTTaskFragment = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mRegisterRESTTaskFragment, "RegisterTask").commit();
		}

		List<Gender> genders = googleLoginPerson.getGenders();
		String gender = null;
		if (genders != null && genders.size() > 0) {
			gender = genders.get(0).getFormattedValue();
			if ( gender == null || ( ! gender.equals("Male") && ! gender.equals("Female") ) ) {
				gender = GenderList.GENDERS_EN[0];
			}
		}
		List<PhoneNumber> phoneNumbers = googleLoginPerson.getPhoneNumbers();
		String phoneNumber = null;
		if (phoneNumbers != null && phoneNumbers.size() > 0) {
			phoneNumber = phoneNumbers.get(0).getValue();
		}
		if ( phoneNumber == null ) {
			phoneNumber = "None";
		}
		List<Birthday> birthdays = googleLoginPerson.getBirthdays();
		Date birthday = null;
		int age = -1;
		if (birthdays != null && birthdays.size() > 0) {
			birthday = birthdays.get(0).getDate();
			if ( birthday != null ) {
				Calendar cal = Calendar.getInstance();
				if ( cal.get(MONTH) > birthday.getMonth() ||
						cal.get(MONTH) == birthday.getMonth() && cal.get(Calendar.DAY_OF_MONTH) >= birthday.getDay() ) {
					age = Calendar.getInstance().get(YEAR) - birthday.getYear();
				} else {
					age = Calendar.getInstance().get(YEAR) - birthday.getYear() - 1;
				}
			}
		}

		huzevka.lunchfriends.model.Person postPerson = new huzevka.lunchfriends.model.Person();
		postPerson.setUserid(account.getDisplayName());
		postPerson.setPasswd(" ");
		postPerson.setRealname(account.getDisplayName());
		postPerson.setAge(age);
		postPerson.setGender(gender);
		postPerson.setImageuri(account.getPhotoUrl().toString());
		postPerson.setEmail(account.getEmail());
		postPerson.setPhone(phoneNumber);
		postPerson.setHobbies("None");

		mRegisterRESTTaskFragment.mTask.setPostObject(postPerson);
		mRegisterRESTTaskFragment.mTask.setResponseClass(RegisterResult.class);
		mRegisterRESTTaskFragment.executeTask(uri);
	}

	private void startFacebookRegisterRESTTask(String uri) {
		Log.i(TAG, "Started register task Facebook.");
		FragmentManager fm = getFragmentManager();
		mRegisterRESTTaskFragment = (PostToRESTTaskFragment) fm.findFragmentByTag("RegisterTask");

		if (mRegisterRESTTaskFragment == null) {
			mRegisterRESTTaskFragment = new PostToRESTTaskFragment();
			fm.beginTransaction().add(mRegisterRESTTaskFragment, "RegisterTask").commit();
		}
		
		postPersonFB.setUserid(profile.getName());
		postPersonFB.setPasswd(" ");
		postPersonFB.setRealname(profile.getName());
		final int px = Math.round(LunchFriendsTools.convertDpToPx(150, getApplicationContext()));
		postPersonFB.setImageuri(profile.getProfilePictureUri(px, px).toString());
		postPersonFB.setPhone("None");
		postPersonFB.setHobbies("None");

		mRegisterRESTTaskFragment.mTask.setPostObject(postPersonFB);
		mRegisterRESTTaskFragment.mTask.setResponseClass(RegisterResult.class);
		mRegisterRESTTaskFragment.executeTask(uri);
	}

	@Override
	public void postToRESTOnPreExecute() {

	}

	@Override
	public void postToRESTOnPostExecute(Object obj) {
		if ( obj instanceof huzevka.lunchfriends.model.LoginResult && obj != null ) {
			loginResult = (huzevka.lunchfriends.model.LoginResult) obj;
			progDialog.dismiss();

			try {
				if (loginResult != null) {
					if (loginResult.isNoUsernameError() || loginResult.getPerson() == null) {
						Toast.makeText(MainActivity.this, R.string.user_not_exist, Toast.LENGTH_SHORT).show();
						return;
					} else if (!loginResult.isSuccess()) {
						Toast.makeText(MainActivity.this, R.string.error_try_later, Toast.LENGTH_SHORT).show();
						return;
					} else if (loginService.equals(LunchLoginService.SERVER) &&
							!loginResult.getPerson().getPasswd().equals(passwordHash)) {
						Toast.makeText(MainActivity.this, R.string.wrong_passwd, Toast.LENGTH_SHORT).show();
						return;
					}
					LoginSingle.setPerson(loginResult.getPerson());
					startHomeActivity(username, Uri.parse(loginResult.getPerson().getImageuri()), loginService);
				} else {
					Toast.makeText(MainActivity.this, R.string.error_try_later, Toast.LENGTH_SHORT).show();
				}
			} catch (NullPointerException e) {
				Toast.makeText(MainActivity.this, R.string.error_try_later, Toast.LENGTH_SHORT).show();
			}
		} else if ( obj instanceof RegisterResult ) {
			if ( loginService.equals(LunchLoginService.GOOGLE) ) {
				username = account.getDisplayName();
				startLoginPostRESTTask(LunchFriendsTools.DBServerBaseURI + "login");
			} else if ( loginService.equals(LunchLoginService.FACEBOOK) ) {
				username = profile.getName();
				startLoginPostRESTTask(LunchFriendsTools.DBServerBaseURI + "login");
			}
		}
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		Log.e(TAG, "onConnectionFailed: " + connectionResult.toString());
	}

	public void loadLocale() {
		String langPref = "Language";
		SharedPreferences prefs = getSharedPreferences("CommonPrefs",
				Activity.MODE_PRIVATE);
		String language = prefs.getString(langPref, "");
		changeLang(language);
	}

	public void changeLang(String lang) {
		if (lang.equalsIgnoreCase(""))
			return;
		Locale myLocale = new Locale(lang);
		LunchFriendsTools.saveLocale(lang, MainActivity.this);
		Locale.setDefault(myLocale);
		Configuration config = new Configuration();
		config.locale = myLocale;
		getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

	}

}
