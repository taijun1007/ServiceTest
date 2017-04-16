package com.cmlab.servicetest;

import java.util.HashMap;

import android.content.Context;

public class CallTestCaseParaSave {
	private static CallTestCaseParaSave sCallTestCaseParaSave;
	private Context mAppContext;
	private HashMap<String,String> mCallTestCaseParaMap;
	
	private CallTestCaseParaSave(Context appContext) {
		mAppContext = appContext;
		mCallTestCaseParaMap = new HashMap<String, String>();
	}
	
	public static CallTestCaseParaSave get(Context c) {
		if (sCallTestCaseParaSave == null) {
			sCallTestCaseParaSave = new CallTestCaseParaSave(c.getApplicationContext());
		}
		return sCallTestCaseParaSave;
	}

	public HashMap<String, String> getCallTestCaseParaMap() {
		return mCallTestCaseParaMap;
	}

	public void setCallTestCaseParaMap(HashMap<String, String> callTestCaseParaMap) {
		mCallTestCaseParaMap = callTestCaseParaMap;
	}

}
