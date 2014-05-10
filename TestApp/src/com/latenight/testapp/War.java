package com.latenight.testapp;

import android.text.format.Time;

import com.facebook.model.GraphUser;

public class War {
	
	public static War createWarWith(GraphUser opponent){
		return new War(opponent);
	}

	private Time timeStarted;
	private int durationInHours;
	private Time timeEnd;
	
	private War(GraphUser opponent){
		timeStarted = new Time();
		timeStarted.setToNow();
		timeEnd = new Time();
		durationInHours = 3;
		//TODO Error prone. This sucks.
		timeEnd.set(timeStarted.second,//
				timeStarted.minute,//
				timeStarted.hour + durationInHours,//
				timeStarted.monthDay,//
				timeStarted.month,//
				timeStarted.year);
	}
	
	public boolean isWarOver(){
		Time now = new Time();
		now.setToNow();
		return now.after(timeEnd);
	}

}
