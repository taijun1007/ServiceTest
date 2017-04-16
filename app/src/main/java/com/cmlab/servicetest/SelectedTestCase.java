package com.cmlab.servicetest;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;

public class SelectedTestCase {
	private static SelectedTestCase sSelectedTestCase;
	private Context mAppContext;
	private ArrayList<String> mTestCaseList;
	
	private SelectedTestCase(Context appContext) {
		mAppContext = appContext;
		mTestCaseList = new ArrayList<String>();
	}
	
	public static SelectedTestCase get(Context c) {
		if (sSelectedTestCase == null) {
			sSelectedTestCase = new SelectedTestCase(c.getApplicationContext());
		}
		return sSelectedTestCase;
	}
	
	public ArrayList<String> getTestCaseList() {
		return mTestCaseList;
	}

	public void setTestCaseList(ArrayList<String> testCaseList) {
		mTestCaseList = testCaseList;
	}

}
