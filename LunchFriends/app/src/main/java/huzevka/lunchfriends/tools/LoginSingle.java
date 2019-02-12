package huzevka.lunchfriends.tools;

import android.app.Application;

import huzevka.lunchfriends.model.Person;

public class LoginSingle extends Application {
	private static Person person;

	public void onCreate() {
		super.onCreate();
	}

	public static Person getPerson() {
		return person;
	}

	public static void setPerson(Person person) {
		LoginSingle.person = person;
	}
}