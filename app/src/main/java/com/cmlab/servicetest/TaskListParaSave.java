package com.cmlab.servicetest;

import java.util.HashMap;

import android.content.Context;

public class TaskListParaSave {
	private static TaskListParaSave sTaskListParaSave;
	private Context mAppContext;
	private HashMap<String,String> mTaskListParaMap;
	
	private TaskListParaSave(Context appContext) {
		mAppContext = appContext;
		mTaskListParaMap = new HashMap<String, String>();
	}
	
	public static TaskListParaSave get(Context c) {
		if (sTaskListParaSave == null) {
			sTaskListParaSave = new TaskListParaSave(c.getApplicationContext());
		}
		return sTaskListParaSave;
	}

	public HashMap<String, String> getTaskListParaMap() {
		return mTaskListParaMap;
	}

	public void setTaskListParaMap(HashMap<String, String> taskListParaMap) {
		mTaskListParaMap = taskListParaMap;
	}

}
