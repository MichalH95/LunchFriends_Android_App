package huzevka.lunchfriends;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import huzevka.lunchfriends.tools.LunchFriendsTools;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class LunchFriendsInstrumentedTest {
	@Test
	public void testConvertDpPx() {
		Context context = InstrumentationRegistry.getTargetContext();

		assertTrue(LunchFriendsTools.convertPxToDp(LunchFriendsTools.convertDpToPx(10, context), context) - 10.0 < 0.001);
	}

}
