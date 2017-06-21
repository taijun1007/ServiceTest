package com.cmlab.servicetest;

import android.content.Context;

import java.util.ArrayList;

public class MainGridItemLab {
	private ArrayList<GridItem> mGridItems;
	private Context mAppContext;
	private static MainGridItemLab sMainGridItemLab;
	
	private MainGridItemLab(Context appContext) {
		mAppContext = appContext;
		mGridItems = new ArrayList<GridItem>();
		GridItem gi = new GridItem(R.drawable.phone, R.string.telephone);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.sms, R.string.sendsms);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.ping, R.string.title_ping);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.apps, R.string.testapps);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.note, R.string.task_schedule);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.su, R.string.super_user);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.settings, R.string.title_setup);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.server, R.string.serverurlsetup_activity_name);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.register, R.string.register_activity_name);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.location, R.string.map);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.weixintext, R.string.weixintext);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.weixinimage, R.string.weixinimage);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.mocall, R.string.mocall);
		mGridItems.add(gi);
	}
	
	public static MainGridItemLab get(Context c) {
		if (sMainGridItemLab == null) {
			sMainGridItemLab = new MainGridItemLab(c.getApplicationContext());
		}
		return sMainGridItemLab;
	}

	public ArrayList<GridItem> getGridItems() {
		return mGridItems;
	}
	
	public GridItem getGridItem(int index) {
		if ((index <= (mGridItems.size() - 1)) && (index >= 0)) {
			return mGridItems.get(index);
		}
		return null;
	}

}
