package huzevka.lunchfriends;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import huzevka.lunchfriends.activity.ChangeLanguageActivity;
import huzevka.lunchfriends.activity.MainActivity;
import huzevka.lunchfriends.activity.RegisterActivity;
import huzevka.lunchfriends.activity.SearchFriends1Activity;
import huzevka.lunchfriends.tools.LunchFriendsTools;

import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

	@Rule
	public ActivityTestRule<MainActivity> activityActivityTestRule = new ActivityTestRule<>(MainActivity.class);

	private String lang;

	@Before
	public void setUp() {
		if ( LunchFriendsTools.isLanguage("cs", activityActivityTestRule.getActivity()) ) {
			lang = "cs";
		} else {
			lang = "en";
		}
	}

	@After
	public void tearDown() {
		if ( ! LunchFriendsTools.isLanguage(lang, activityActivityTestRule.getActivity()) ) {
			LunchFriendsTools.saveLocale(lang, activityActivityTestRule.getActivity());
		}
	}

	@Test
	public void testLogin() throws Exception {
		Espresso.onView(ViewMatchers.withId(R.id.userIdEdit)).perform(typeText("Adam5"));

		Espresso.onView(ViewMatchers.withId(R.id.passwordEdit)).perform(typeText("Adam"));

		Espresso.pressBack(); // close keyboard
		Thread.sleep(250);

		Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(click());

		assertTrue(activityActivityTestRule.getActivity().isFinishing());
	}

	@Test
	public void testChangeLanguage() {
		Espresso.onView(ViewMatchers.withId(R.id.changeLanguageButton)).perform(click());

		if ( LunchFriendsTools.isLanguage("cs", activityActivityTestRule.getActivity()) ) {
			Espresso.onView(ViewMatchers.withId(R.id.englishButton)).perform(click());
			assertTrue( LunchFriendsTools.isLanguage("en", activityActivityTestRule.getActivity()) );
		} else {
			Espresso.onView(ViewMatchers.withId(R.id.czechButton)).perform(click());
			assertTrue( LunchFriendsTools.isLanguage("cs", activityActivityTestRule.getActivity()) );
		}
	}

	@Test
	public void testRegisterButton() {
		Espresso.onView(ViewMatchers.withId(R.id.registerButton)).perform(click());

		Espresso.onView(ViewMatchers.withId(R.id.backButton)).perform(click());
	}

}
