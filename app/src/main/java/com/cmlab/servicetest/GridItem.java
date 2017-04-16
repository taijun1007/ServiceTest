package com.cmlab.servicetest;

/*
 * GridItem include a icon id and a text,
 * Used to display a test item, such as voice, sms, weixin.  
 */

public class GridItem {
	private int mIconId;
	private int mTitleId;
	
	public GridItem(int itemId, int itemTitle) {
		mIconId = itemId;
		mTitleId = itemTitle;
	}

	public int getIconId() {
		return mIconId;
	}

	public void setIconId(int iconId) {
		mIconId = iconId;
	}

	public int getTitleId() {
		return mTitleId;
	}

	public void setTitleId(int title) {
		mTitleId = title;
	}
	
}
