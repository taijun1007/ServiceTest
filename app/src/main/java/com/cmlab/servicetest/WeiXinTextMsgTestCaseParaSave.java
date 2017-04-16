package com.cmlab.servicetest;

import java.util.HashMap;

import android.content.Context;

public class WeiXinTextMsgTestCaseParaSave {
	private static WeiXinTextMsgTestCaseParaSave sWeiXinTextMsgTestCaseParaSave;
	private Context mAppContext;
	private HashMap<String,String> mWeiXinTextMsgTestCaseParaMap;
	
	private WeiXinTextMsgTestCaseParaSave(Context appContext) {
		mAppContext = appContext;
		mWeiXinTextMsgTestCaseParaMap = new HashMap<String, String>();
	}
	
	public static WeiXinTextMsgTestCaseParaSave get(Context c) {
		if (sWeiXinTextMsgTestCaseParaSave == null) {
			sWeiXinTextMsgTestCaseParaSave = new WeiXinTextMsgTestCaseParaSave(c.getApplicationContext());
		}
		return sWeiXinTextMsgTestCaseParaSave;
	}

	public HashMap<String, String> getWeiXinTextMsgTestCaseParaMap() {
		return mWeiXinTextMsgTestCaseParaMap;
	}

	public void setWeiXinTextMsgTestCaseParaMap(
			HashMap<String, String> weiXinTextMsgTestCaseParaMap) {
		mWeiXinTextMsgTestCaseParaMap = weiXinTextMsgTestCaseParaMap;
	}

}
