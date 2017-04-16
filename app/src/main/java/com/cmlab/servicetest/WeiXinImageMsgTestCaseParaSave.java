package com.cmlab.servicetest;

import java.util.HashMap;

import android.content.Context;

public class WeiXinImageMsgTestCaseParaSave {
	private static WeiXinImageMsgTestCaseParaSave sWeiXinImageMsgTestCaseParaSave;
	private Context mAppContext;
	private HashMap<String,String> mWeiXinImageMsgTestCaseParaMap;
	
	private WeiXinImageMsgTestCaseParaSave(Context appContext) {
		mAppContext = appContext;
		mWeiXinImageMsgTestCaseParaMap = new HashMap<String, String>();
	}
	
	public static WeiXinImageMsgTestCaseParaSave get(Context c) {
		if (sWeiXinImageMsgTestCaseParaSave == null) {
			sWeiXinImageMsgTestCaseParaSave = new WeiXinImageMsgTestCaseParaSave(c.getApplicationContext());
		}
		return sWeiXinImageMsgTestCaseParaSave;
	}

	public HashMap<String, String> getWeiXinImageMsgTestCaseParaMap() {
		return mWeiXinImageMsgTestCaseParaMap;
	}

	public void setWeiXinImageMsgTestCaseParaMap(
			HashMap<String, String> weiXinImageMsgTestCaseParaMap) {
		mWeiXinImageMsgTestCaseParaMap = weiXinImageMsgTestCaseParaMap;
	}

}
