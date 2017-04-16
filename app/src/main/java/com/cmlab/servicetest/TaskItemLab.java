package com.cmlab.servicetest;

import java.util.ArrayList;

import android.content.Context;

public class TaskItemLab {
	private static TaskItemLab sTaskItemLab;
	private Context mAppContext;
	private ArrayList<TaskItem> mTaskItems;
	
	private TaskItemLab(Context appContext) {
		mAppContext = appContext;
		mTaskItems = new ArrayList<TaskItem>();
	}
	
	public static TaskItemLab get(Context c) {
		if (sTaskItemLab == null) {
			sTaskItemLab = new TaskItemLab(c.getApplicationContext());
		}
		return sTaskItemLab;
	}

	public ArrayList<TaskItem> getTaskItems() {
		return mTaskItems;
	}

	public void setTaskItems(ArrayList<TaskItem> taskItems) {
		mTaskItems = taskItems;
	}
	

}
