package com.cmlab.servicetest;

import java.util.ArrayList;

import android.content.Context;

public class WeiXinGridItemLab {
	private ArrayList<GridItem> mGridItems;
	private Context mAppContext;
	private static WeiXinGridItemLab sWeiXinGridItemLab;
	
	private WeiXinGridItemLab(Context appContext) {
		mAppContext = appContext;
		mGridItems = new ArrayList<GridItem>();
		GridItem gi = new GridItem(R.drawable.sendtext, R.string.send_text);
		mGridItems.add(gi);
		gi = new GridItem(R.drawable.sendpic, R.string.send_pic);
		mGridItems.add(gi);
	}
	
	public static WeiXinGridItemLab get(Context c) {
		if (sWeiXinGridItemLab == null) {
			sWeiXinGridItemLab = new WeiXinGridItemLab(c.getApplicationContext());
		}
		return sWeiXinGridItemLab;
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
