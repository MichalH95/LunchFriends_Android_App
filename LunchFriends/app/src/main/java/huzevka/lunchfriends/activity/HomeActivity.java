package huzevka.lunchfriends.activity;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.service.LoadInvitationsService;
import huzevka.lunchfriends.taskfragment.LoadImageTaskFragment;
import huzevka.lunchfriends.tools.GoogleApiClientSingle;
import huzevka.lunchfriends.tools.LoginSingle;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class HomeActivity extends AppCompatActivity implements LoadImageTaskFragment.TaskCallbacks {

	private String username;
	private Uri photoUri;
	private MainActivity.LunchLoginService loginService;

	private ImageView userImage;

	private LoadImageTaskFragment mTaskFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_home);

		// get data from intent
		Bundle extras = getIntent().getExtras();
		username = extras.getString("username");
		photoUri = (Uri) extras.get("photoUri");
		loginService = (MainActivity.LunchLoginService) extras.get("loginService");

		// set views
		((TextView)findViewById(R.id.fullnameText)).setText(username);
		findViewById(R.id.goOnLunchButton).setOnClickListener(goOnLunchListener);
		findViewById(R.id.searchPeopleButton).setOnClickListener(searchPeopleListener);
		findViewById(R.id.browseButton).setOnClickListener(browsePeopleListener);
		findViewById(R.id.historyButton).setOnClickListener(historyListener);
		findViewById(R.id.exitButton).setOnClickListener(exitListener);

		userImage = findViewById(R.id.userImage);
		LunchFriendsTools.setImageLoading(userImage, 80);

		findViewById(R.id.logoutButton).setOnClickListener(logoutButtonListener);

		// start loading image using async task in task fragment, or if there is no user image, hide the ImageView
		if (photoUri != null ) {
			FragmentManager fm = getFragmentManager();
			mTaskFragment = (LoadImageTaskFragment) fm.findFragmentByTag("ImageLoadTask");

			if (mTaskFragment == null) {
				mTaskFragment = new LoadImageTaskFragment();
				fm.beginTransaction().add(mTaskFragment, "ImageLoadTask").commit();
			}

			mTaskFragment.executeTask(photoUri);
		} else {
			userImage.setVisibility(View.GONE);
		}

		// start service for loading lunch invitations
		if ( LoadInvitationsService.instance == null ) {
			startService(new Intent(getApplicationContext(), LoadInvitationsService.class));
		} else {
			if ( LoadInvitationsService.instance.forPerson.getPID() != LoginSingle.getPerson().getPID() ) {
				LoadInvitationsService.instance.removeCallbacks();
				LoadInvitationsService.instance.startHandle();
			}
		}
	}

	private final View.OnClickListener logoutButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch ( loginService ) {
				case GOOGLE:
					Auth.GoogleSignInApi.signOut(GoogleApiClientSingle.getClient()).setResultCallback(
							new ResultCallback<Status>() {
								@Override
								public void onResult(@NonNull Status status) {
									GoogleApiClientSingle.getClient().disconnect();
									GoogleApiClientSingle.setClient(null);
									startMainActivity();
								}
							});
					break;
				case FACEBOOK:
					LoginManager.getInstance().logOut();
					startMainActivity();
					break;
				case SERVER:
					startMainActivity();
					break;
			}
		}
	};

	private final View.OnClickListener goOnLunchListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startGoOnLunchActivity();
		}
	};

	private final View.OnClickListener searchPeopleListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startSearchFriendsActivity();
		}
	};

	private final View.OnClickListener browsePeopleListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startFriendResultsActivity();
		}
	};

	private final View.OnClickListener historyListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startLunchHistoryActivity();
		}
	};

	private final View.OnClickListener exitListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			switch ( loginService ) {
				case GOOGLE:
					Auth.GoogleSignInApi.signOut(GoogleApiClientSingle.getClient()).setResultCallback(
							new ResultCallback<Status>() {
								@Override
								public void onResult(@NonNull Status status) {
									GoogleApiClientSingle.getClient().disconnect();
									GoogleApiClientSingle.setClient(null);
									finish();
								}
							});
					break;
				case FACEBOOK:
					LoginManager.getInstance().logOut();
					finish();
					break;
				case SERVER:
					finish();
					break;
			}
		}
	};

	private void startMainActivity() {
		Intent intent = new Intent(getApplicationContext(), MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}

	private void startGoOnLunchActivity() {
		Intent intent = new Intent(getApplicationContext(), GoOnLunchActivity.class);
		startActivity(intent);
	}

	private void startSearchFriendsActivity() {
		Intent intent = new Intent(getApplicationContext(), SearchFriends1Activity.class);
		startActivity(intent);
	}

	private void startLunchHistoryActivity() {
		Intent intent = new Intent(getApplicationContext(), LunchHistoryActivity.class);
		startActivity(intent);
	}

	private void startFriendResultsActivity() {
		Intent intent = new Intent(getApplicationContext(), FriendResultsActivity.class);
		intent.putExtra("showAllFriends", Boolean.TRUE);
		startActivity(intent);
	}

	@Override
	public void onBackPressed() {
	}

	@Override
	public void loadImageOnPreExecute() {
	}

	@Override
	public void loadImageOnPostExecute(Bitmap bitmap) {
		if ( bitmap != null ) {
			bitmap = LunchFriendsTools.scaleBitmap(bitmap, 80);
			userImage.setImageBitmap(bitmap);
		} else {
			userImage.setVisibility(View.GONE);
		}
	}


}
