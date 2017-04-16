package com.cmlab.servicetest;

import java.util.ArrayList;

import android.content.Context;

public class AppsGridItemLab {
	private ArrayList<GridItem> mGridItems;
	private Context mAppContext;
	private static AppsGridItemLab sAppsGridItemLab;
	
	private AppsGridItemLab(Context appContext) {
		mAppContext = appContext;
		mGridItems = new ArrayList<GridItem>();
		GridItem gi = new GridItem(R.drawable.weixin, R.string.weixinapp);
		mGridItems.add(gi);
	}
	
	public static AppsGridItemLab get(Context c) {
		if (sAppsGridItemLab == null) {
			sAppsGridItemLab = new AppsGridItemLab(c.getApplicationContext());
		}
		return sAppsGridItemLab;
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
