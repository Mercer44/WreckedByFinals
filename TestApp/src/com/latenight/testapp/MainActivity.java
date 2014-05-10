package com.latenight.testapp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.model.GraphUser;

public class MainActivity extends Activity {

	private static final int PICK_FRIENDS_ACTIVITY = 1;
	private static final int WAR_STORIES_ACTIVITY = 2;
	private static final int CURRENT_STORIES_ACTIVITY = 3;
	private Button pickFriendsButton, warStoriesButton, currentWarsButton;
	private TextView resultsTextView;
	private UiLifecycleHelper lifecycleHelper;
	boolean pickFriendsWhenSessionOpened, warStoriesWhenSessionOpened, currWarsWhenSessionOpened;

	private boolean ensureOpenSession() {
		if (Session.getActiveSession() == null
				|| !Session.getActiveSession().isOpened()) {
			Session.openActiveSession(this, true, PERMISSIONS,
					new Session.StatusCallback() {
						@Override
						public void call(Session session, SessionState state,
								Exception exception) {
							Log.d("Session","Setting SessionStateChanged");
							onSessionStateChanged(session, state, exception);
						}
					});
			return false;
		}
		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		resultsTextView = (TextView) findViewById(R.id.resultsTextView);
		pickFriendsButton = (Button) findViewById(R.id.pickFriendsButton);
		pickFriendsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				onClickPickFriends();
			}
		});
		warStoriesButton = (Button) findViewById(R.id.warStoriesButton);
		warStoriesButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				onClickWarStories();
			}
		});
		currentWarsButton = (Button) findViewById(R.id.currentWarsButton);
		currentWarsButton.setOnClickListener( new View.OnClickListener() {
			public void onClick(View v) {
				onClickCurrentWars();
			}
		});
		lifecycleHelper = new UiLifecycleHelper(this,
				new Session.StatusCallback() {
					@Override
					public void call(Session session, SessionState state,
							Exception exception) {
						if (session.isOpened()) {

							// make request to the /me API
							Request.newMeRequest(session,
									new Request.GraphUserCallback() {

										// callback after Graph API response
										// with user object
										@Override
										public void onCompleted(GraphUser user,
												Response response) {
											if (user != null) {
												TextView welcome = (TextView) findViewById(R.id.welcome);
												welcome.setText("Hello "
														+ user.getName() + "!");
											Log.d("User","Set welcome text");
											}
										}
									}).executeAsync();
						}
						onSessionStateChanged(session, state, exception);
					}
				});
		lifecycleHelper.onCreate(savedInstanceState);
		
		Log.d("Session","Ensuring open Session");
		
		ensureOpenSession();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.d("Request","request code received:" + requestCode);
		switch (requestCode) {
		case PICK_FRIENDS_ACTIVITY:
			Log.d("Request","Displaying selected friends");
			displaySelectedFriends(resultCode);
			break;
		case CURRENT_STORIES_ACTIVITY:
			Log.d("Request","Displaying Current War Stories");
			break;
		case WAR_STORIES_ACTIVITY:
			Log.d("Request","Displaying War Stories");
			break;
		default:
			Session.getActiveSession().onActivityResult(this, requestCode,
					resultCode, data);
			break;
		}
		Session.getActiveSession().onActivityResult(this, requestCode,
				resultCode, data);
	}

	private static final List<String> PERMISSIONS = new ArrayList<String>() {
		private static final long serialVersionUID = -4389583738442035807L;

		{
			add("user_friends");
			add("public_profile");
		}
	};

	@Override
	protected void onStart() {
		super.onStart();

		// Update the display every time we are started.
		displaySelectedFriends(RESULT_OK);
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);
	}

	private boolean sessionHasNecessaryPerms(Session session) {
		if (session != null && session.getPermissions() != null) {
			for (String requestedPerm : PERMISSIONS) {
				if (!session.getPermissions().contains(requestedPerm)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	private List<String> getMissingPermissions(Session session) {
		List<String> missingPerms = new ArrayList<String>(PERMISSIONS);
		if (session != null && session.getPermissions() != null) {
			for (String requestedPerm : PERMISSIONS) {
				if (session.getPermissions().contains(requestedPerm)) {
					missingPerms.remove(requestedPerm);
				}
			}
		}
		return missingPerms;
	}

	private void onSessionStateChanged(final Session session,
			SessionState state, Exception exception) {
		if (state.isOpened() && !sessionHasNecessaryPerms(session)) {
			Log.d("Session","Entering Session State Changed");
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			Log.d("Session","Created Alert Dialog Builder");
			builder.setMessage(R.string.need_perms_alert_text);
			builder.setPositiveButton(R.string.need_perms_alert_button_ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Log.d("Session","Clicking for Permissions");
							session.requestNewReadPermissions(new NewPermissionsRequest(
									MainActivity.this,
									getMissingPermissions(session)));
						}
					});
			builder.setNegativeButton(R.string.need_perms_alert_button_quit,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			builder.show();
		} else if (pickFriendsWhenSessionOpened && state.isOpened()) {
			pickFriendsWhenSessionOpened = false;
			Log.d("Session","Starting PickFriendsActivity");
			startPickFriendsActivity();
		}
		else if (warStoriesWhenSessionOpened && state.isOpened()) {
			warStoriesWhenSessionOpened = false;
			Log.d("Session","Starting WarStoriesActivity");
			startWarStories();
		}
		else if (currWarsWhenSessionOpened && state.isOpened()) {
			currWarsWhenSessionOpened = false;
			Log.d("Session","Starting CurrentWarsActivity");
			startCurrentWars();
		}
	}

	private void displaySelectedFriends(int resultCode) {
		String results = "";
		FingerBangerApplication application = (FingerBangerApplication) getApplication();
		Log.d("DisplayFriends","Displaying Selected Friends");
		Collection<GraphUser> selection = application.getSelectedUsers();
		if (selection != null && selection.size() > 0) {
			ArrayList<String> names = new ArrayList<String>();
			for (GraphUser user : selection) {
				Log.d("DisplayFriends","Adding user:" + user.getName());
				names.add(user.getName());
			}
			results = TextUtils.join(", ", names);
		} else {
			Log.d("DisplayFriends","No Friends Selected");
			results = "<No friends selected>";
		}

		resultsTextView.setText(results);
	}
	
	private void onClickCurrentWars()
	{
		startCurrentWars();
	}
	
	private void startCurrentWars()
	{
		if (ensureOpenSession()) {
			Log.d("Intent", "Creating CurrentWarsActivity");
			Intent intent = new Intent(this, CurrentWarActivity.class);
			// Note: The following line is optional, as multi-select behavior is
			// the default for
			// FriendPickerFragment. It is here to demonstrate how parameters
			// could be passed to the
			// friend picker if single-select functionality was desired, or if a
			// different user ID was
			// desired (for instance, to see friends of a friend).
			PickFriendsActivity.populateParameters(intent, null, true, true);
			Log.d("Intent", "Starting CurrentWarsActivity");
			startActivityForResult(intent, CURRENT_STORIES_ACTIVITY);
		} else {
			Log.d("Session", "Session was not open");
			currWarsWhenSessionOpened = true;
		}
	}
	
	private void onClickWarStories()
	{
		startWarStories();
	}
	
	private void startWarStories()
	{
		if (ensureOpenSession()) {
			Log.d("Intent", "Creating WarStoriesActivity");
			Intent intent = new Intent(this, StartWarActivity.class);
			// Note: The following line is optional, as multi-select behavior is
			// the default for
			// FriendPickerFragment. It is here to demonstrate how parameters
			// could be passed to the
			// friend picker if single-select functionality was desired, or if a
			// different user ID was
			// desired (for instance, to see friends of a friend).
			PickFriendsActivity.populateParameters(intent, null, true, true);
			Log.d("Intent", "Starting WarStoriesActivity");
			startActivityForResult(intent, WAR_STORIES_ACTIVITY);
		} else {
			Log.d("Session", "Session was not open");
			warStoriesWhenSessionOpened = true;
		}
	}
	
	private void onClickPickFriends() {
		startPickFriendsActivity();
	}
	
	private void startPickFriendsActivity()
	{
		if (ensureOpenSession()) {
			Log.d("Intent", "Creating PickFriendsActivity");
			Intent intent = new Intent(this, PickFriendsActivity.class);
			// Note: The following line is optional, as multi-select behavior is
			// the default for
			// FriendPickerFragment. It is here to demonstrate how parameters
			// could be passed to the
			// friend picker if single-select functionality was desired, or if a
			// different user ID was
			// desired (for instance, to see friends of a friend).
			PickFriendsActivity.populateParameters(intent, null, true, true);
			Log.d("Intent", "Starting PickFriendsActivity");
			startActivityForResult(intent, PICK_FRIENDS_ACTIVITY);
		} else {
			Log.d("Session", "Session was not open");
			pickFriendsWhenSessionOpened = true;
		}	
	}
	

}
