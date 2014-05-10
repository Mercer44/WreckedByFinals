package com.latenight.testapp;

import java.util.List;
import android.app.Application;
import com.facebook.model.GraphUser;

// We use a custom Application class to store our minimal state data (which users have been selected).
// A real-world application will likely require a more robust data model.
public class FingerBangerApplication extends Application {
	private List<GraphUser> selectedUsers;
	private List<War> wars;

	public List<GraphUser> getSelectedUsers() {
		return selectedUsers;
	}

	public void setSelectedUsers(List<GraphUser> selectedUsers) {
		this.selectedUsers = selectedUsers;
	}
	
	public List<War> getWars(){
		return wars;
	}
	
	public void startWarWith(GraphUser opponent){
		//TODO check war with opponent is not currently happening
		wars.add(War.createWarWith(opponent));
	}
}
