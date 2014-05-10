package com.latenight.testapp;

import com.facebook.AppEventsLogger;
import com.facebook.model.*;
import com.facebook.widget.ProfilePictureView;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class CurrentWarActivity extends ActionBarActivity {

	private ProfilePictureView profilePictureView;
	private TextView userName;
	private GraphUser user;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_current_war);

		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
		userName = (TextView) findViewById(R.id.name);

		user = ((FingerBangerApplication) this.getApplication()).getUser();
		Log.d("User", "Got User: " + user.getName());

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.current_war, menu);
		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();

		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);

		updateUI();
	}

	private void updateUI() {
		if (user != null) {

			String id = user.getId();

			if (id != null) {
				Log.d("User", "Setting ProfileView:" + id);

				// profilePictureView.setProfileId(id);
			}
			String fName = user.getFirstName();
			if (fName != null) {
				Log.d("User", "Setting Username:" + fName);
				// userName.setText(fName);
			}
			Log.d("User", "Successsssssss!!!!!");
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_current_war,
					container, false);
			return rootView;
		}
	}

}
