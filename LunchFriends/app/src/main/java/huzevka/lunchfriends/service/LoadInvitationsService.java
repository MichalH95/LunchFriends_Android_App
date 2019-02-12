package huzevka.lunchfriends.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import huzevka.lunchfriends.R;
import huzevka.lunchfriends.activity.NotificationActivity;
import huzevka.lunchfriends.model.Invitation;
import huzevka.lunchfriends.model.Invitations;
import huzevka.lunchfriends.model.Lunch;
import huzevka.lunchfriends.model.Lunches;
import huzevka.lunchfriends.model.Person;
import huzevka.lunchfriends.model.Persons;
import huzevka.lunchfriends.taskfragment.GetFromRESTTaskFragment;
import huzevka.lunchfriends.taskfragment.PostToRESTTaskFragment;
import huzevka.lunchfriends.tools.LoginSingle;
import huzevka.lunchfriends.tools.LunchFriendsTools;

public class LoadInvitationsService extends IntentService
		implements PostToRESTTaskFragment.TaskCallbacks, GetFromRESTTaskFragment.TaskCallbacks {

	private static final String TAG = "LoadInvitationsService";
	private final int notificationID = 1;

	public static LoadInvitationsService instance;

	public Person forPerson;

	private PostToRESTTaskFragment postToRESTTaskFragment;
	private GetFromRESTTaskFragment getFromRESTTaskFragmentPerson;
	private GetFromRESTTaskFragment getFromRESTTaskFragmentLunch;

	private Handler mHandler = new Handler();

	private final int tenMinutes = 10 * 60 * 1000;
	private final int thirtySeconds = 30 * 1000;

	private List<Invitation> invitationList;

	private Person invitingPerson;
	private Lunch lunch;

	private int uniqueId = 0;

	public LoadInvitationsService() {
		super("LoadInvitationsService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		instance = LoadInvitationsService.this;
		forPerson = LoginSingle.getPerson();
		mHandler.postDelayed(taskRunnable, thirtySeconds);
	}


	private final Runnable taskRunnable = new Runnable() {
		@Override
		public void run() {
			Log.i(TAG, "Started loading invitations task.");
			if ( postToRESTTaskFragment == null ) {
				postToRESTTaskFragment = new PostToRESTTaskFragment();
			}

			postToRESTTaskFragment.attach(LoadInvitationsService.this);

			forPerson = LoginSingle.getPerson();
			postToRESTTaskFragment.mTask.setPostObject(forPerson);
			postToRESTTaskFragment.mTask.setResponseClass(Invitations.class);
			postToRESTTaskFragment.executeTask(LunchFriendsTools.DBServerBaseURI + "getinvitation");

			mHandler.postDelayed(taskRunnable, thirtySeconds);
		}
	};

	@Override
	public void getFromRESTOnPreExecute() {

	}

	@Override
	public void getFromRESTOnPostExecute(Object obj, GetFromRESTTaskFragment.GetRESTTask task) {
		if ( obj instanceof Persons && obj != null ) {
			List<Person> list = ((Persons) obj).getPerson();
			if ( list != null && ! list.isEmpty() && list.size() == 1 ) {
				invitingPerson = list.get(0);
			} else {
				Log.w(TAG, "Error when loading inviting person.");
			}
		} else if ( obj instanceof Lunches && obj != null ) {
			List<Lunch> list = ((Lunches) obj).getLunch();
			if ( list != null && ! list.isEmpty() && list.size() == 1 ) {
				lunch = list.get(0);
			} else {
				Log.w(TAG, "Error when loading inviting lunch.");
			}
		}

		if ( lunch != null && invitingPerson != null ) {
			notifyUser();
		}
	}

	@Override
	public void postToRESTOnPreExecute() {

	}

	@Override
	public void postToRESTOnPostExecute(Object obj) {
		if ( obj instanceof Invitations && obj != null ) {
			invitationList = ((Invitations) obj).getInvitation();
		}

		if ( invitationList != null && ! invitationList.isEmpty() ) {
			// someone invited us

			loadInvitationPersonLunch();

			mHandler.removeCallbacks(taskRunnable);

			// notify user again after 10 minutes
			mHandler.postDelayed(taskRunnable, tenMinutes);
		}
	}

	public void removeCallbacks() {
		mHandler.removeCallbacks(taskRunnable);
	}

	public void startHandle() {
		this.onHandleIntent(new Intent());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		instance = null;
		if ( postToRESTTaskFragment != null ) {
			postToRESTTaskFragment.detach();
		}
		if ( getFromRESTTaskFragmentPerson != null ) {
			getFromRESTTaskFragmentPerson.detach();
		}
		if ( getFromRESTTaskFragmentLunch != null ) {
			getFromRESTTaskFragmentLunch.detach();
		}
	}

	private void loadInvitationPersonLunch() {
		Invitation invitation = invitationList.get(0);

		startGetRESTTaskPerson(LunchFriendsTools.DBServerBaseURI + "person/" + invitation.getPID());
		startGetRESTTaskLunch(LunchFriendsTools.DBServerBaseURI + "lunch/" + invitation.getLID());
	}

	private void startGetRESTTaskPerson(String uri) {
		Log.i(TAG, "Started loading person task.");

		if (getFromRESTTaskFragmentPerson == null) {
			getFromRESTTaskFragmentPerson = new GetFromRESTTaskFragment();
		}

		getFromRESTTaskFragmentPerson.attach(LoadInvitationsService.this);

		getFromRESTTaskFragmentPerson.mTask.setResponseClass(Persons.class);
		getFromRESTTaskFragmentPerson.executeTask(uri);
	}

	private void startGetRESTTaskLunch(String uri) {
		Log.i(TAG, "Started loading lunch task.");
		if (getFromRESTTaskFragmentLunch == null) {
			getFromRESTTaskFragmentLunch = new GetFromRESTTaskFragment();
		}

		getFromRESTTaskFragmentLunch.attach(LoadInvitationsService.this);

		getFromRESTTaskFragmentLunch.mTask.setResponseClass(Lunches.class);
		getFromRESTTaskFragmentLunch.executeTask(uri);
	}

	private void notifyUser() {
		Context context = getApplicationContext();

		Intent intent = new Intent(context, NotificationActivity.class);

		Invitation inv = invitationList.get(0).clone();
		intent.putExtra("invitation", inv);
		intent.putExtra("invitingPerson", invitingPerson);
		intent.putExtra("lunch", lunch);
		intent.putExtra("notificationID", notificationID);

		String uId = getNextUniqueId();
		intent.putExtra("u_id", uId);
		intent.setAction(uId);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		PendingIntent pendingIntent = PendingIntent.getActivity(context,  0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

		Notification notif = new NotificationCompat.Builder(context, NotificationChannel.DEFAULT_CHANNEL_ID)
				.setSmallIcon(R.drawable.loading_image)
				.setWhen(System.currentTimeMillis())
				.setContentTitle(getString(R.string.notif_title))
				.setContentText(getString(R.string.notif_text))
				.setContentIntent(pendingIntent)
				.setVibrate(new long[] {100, 250, 100, 500})
				.build();

		nm.notify(notificationID, notif);
	}


	private String getNextUniqueId() {
		return String.valueOf(uniqueId++);
	}
}
