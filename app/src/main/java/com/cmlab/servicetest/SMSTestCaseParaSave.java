package com.cmlab.servicetest;

import java.util.HashMap;

import android.content.Context;

public class SMSTestCaseParaSave {
	private static SMSTestCaseParaSave sSMSTestCaseParaSave;
	private Context mAppContext;
	private HashMap<String,String> mSMSTestCaseParaMap;
	
	private SMSTestCaseParaSave(Context appContext) {
		mAppContext = appContext;
		mSMSTestCaseParaMap = new HashMap<String, String>();
	}
	
	public static SMSTestCaseParaSave get(Context c) {
		if (sSMSTestCaseParaSave == null) {
			sSMSTestCaseParaSave = new SMSTestCaseParaSave(c.getApplicationContext());
		}
		return sSMSTestCaseParaSave;
	}

	public HashMap<String, String> getSMSTestCaseParaMap() {
		return mSMSTestCaseParaMap;
	}

	public void setSMSTestCaseParaMap(HashMap<String, String> sMSTestCaseParaMap) {
		mSMSTestCaseParaMap = sMSTestCaseParaMap;
	}

}
