package huzevka.lunchfriends.activity;

import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.taskfragment.LoadImageTaskFragment;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class UserInfoActivity extends AppCompatActivity
		implements LoadImageTaskFragment.TaskCallbacks {

	private final String TAG = "UserInfoActivity";

	private String fullname;
	private String email;
	private String phone;
	private String imageUri;

	private Button backButton;

	private TextView fullnameText;
	private TextView emailText;
	private TextView phoneText;

	private ImageView userImage;

	private LoadImageTaskFragment mLoadImageTaskFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);

		// set views
		fullnameText = findViewById(R.id.fullnameText);
		emailText = findViewById(R.id.emailText);
		phoneText = findViewById(R.id.phoneText);
		userImage = findViewById(R.id.userImage);
		backButton = findViewById(R.id.backButton);
		backButton.setOnClickListener(backButtonClickListener);

		LunchFriendsTools.setImageLoading(userImage, 120);

		// get data from intent
		Bundle intent = getIntent().getExtras();
		fullname = intent.getString("fullname");
		email = intent.getString("email");
		phone = intent.getString("phone");
		imageUri = intent.getString("imageUri");

		// load image for person
		startLoadImageTask(imageUri);

		// set person info
		fullnameText.setText(getResources().getString(R.string.fullname_colon) + " " + fullname);
		emailText.setText(getResources().getString(R.string.email_colon) + " " + email);
		phoneText.setText(getResources().getString(R.string.phone_colon) + " " + phone);
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
			bitmap = LunchFriendsTools.scaleBitmap(bitmap, 150);
			userImage.setImageBitmap(bitmap);
			userImage.setVisibility(View.VISIBLE);
		} else {
			userImage.setVisibility(View.GONE);
		}
	}

	private final View.OnClickListener backButtonClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			finish();
		}
	};

}
