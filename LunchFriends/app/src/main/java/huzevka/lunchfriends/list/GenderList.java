package huzevka.lunchfriends.list;

import java.util.TreeMap;

public class GenderList {

	public static final String[] GENDERS_EN = new String[] { "Any", "Male", "Female" };

	public static final String[] GENDERS_CS = new String[] { "Jakékoliv", "Muž", "Žena" };

	public static final TreeMap<String, String> GENDERS_CONVERT = new TreeMap<>();

	static {
		for ( int i = 0 ; i < GENDERS_EN.length ; i++ ) {
			GENDERS_CONVERT.put(GENDERS_EN[i], GENDERS_CS[i]);
		}
	}

}
