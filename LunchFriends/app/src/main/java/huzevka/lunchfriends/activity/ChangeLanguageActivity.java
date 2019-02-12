package huzevka.lunchfriends.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import java.util.Locale;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class ChangeLanguageActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_language);

		findViewById(R.id.englishButton).setOnClickListener(englishButtonListener);
		findViewById(R.id.czechButton).setOnClickListener(czechButtonListener);
		findViewById(R.id.languageBackButton).setOnClickListener(languageBackButtonListener);
	}

	private final View.OnClickListener englishButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if ( ! LunchFriendsTools.isLanguage("en", ChangeLanguageActivity.this) ) {
				setLanguage("en");
			}
		}
	};

	private final View.OnClickListener czechButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			if ( ! LunchFriendsTools.isLanguage("cs", ChangeLanguageActivity.this) ) {
				setLanguage("cs");
			}
		}
	};

	private final View.OnClickListener languageBackButtonListener = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			startMainActivity();
		}
	};

	public void setLanguage(String lang) {
		Locale myLocale = new Locale(lang);
		Resources res = getResources();
		DisplayMetrics dm = res.getDisplayMetrics();
		Configuration conf = res.getConfiguration();
		conf.locale = myLocale;
		res.updateConfiguration(conf, dm);
		LunchFriendsTools.saveLocale(lang, ChangeLanguageActivity.this);
		startMainActivity();
	}

	private void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		finish();
	}
}
