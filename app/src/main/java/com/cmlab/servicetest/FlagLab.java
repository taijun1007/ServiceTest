package com.cmlab.servicetest;

import android.content.Context;

public class FlagLab {
	private boolean mClockServiceRun;
	private Context mAppContext;
	private static FlagLab sFlagLab;
	
	private FlagLab(Context appContext) {
		mAppContext = appContext;
		mClockServiceRun = false;
	}
	
	public static FlagLab get(Context c) {
		if (sFlagLab == null) {
			sFlagLab = new FlagLab(c.getApplicationContext());
		}
		return sFlagLab;
	}

	public boolean isClockServiceRun() {
		return mClockServiceRun;
	}

	public void setClockServiceRun(boolean clockServiceRun) {
		mClockServiceRun = clockServiceRun;
	}

}
