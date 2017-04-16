package com.cmlab.servicetest;

import java.util.HashMap;

import android.content.Context;

public class PingTestCaseParaSave {
	private static PingTestCaseParaSave sPingTestCaseParaSave;
	private Context mAppContext;
	private HashMap<String,String> mPingTestCaseParaMap;
	
	private PingTestCaseParaSave(Context appContext) {
		mAppContext = appContext;
		mPingTestCaseParaMap = new HashMap<String, String>();
	}
	
	public static PingTestCaseParaSave get(Context c) {
		if (sPingTestCaseParaSave == null) {
			sPingTestCaseParaSave = new PingTestCaseParaSave(c.getApplicationContext());
		}
		return sPingTestCaseParaSave;
	}

	public HashMap<String, String> getPingTestCaseParaMap() {
		return mPingTestCaseParaMap;
	}

	public void setPingTestCaseParaMap(HashMap<String, String> pingTestCaseParaMap) {
		mPingTestCaseParaMap = pingTestCaseParaMap;
	}


}
