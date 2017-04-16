package com.cmlab.servicetest;

public class TaskItem {
	private String mTestCaseName;
	private String mDelayTime;
	
	public TaskItem(String testCaseName, String delayTime) {
		mTestCaseName = testCaseName;
		mDelayTime = delayTime;
	}

	public String getTestCaseName() {
		return mTestCaseName;
	}

	public void setTestCaseName(String testCaseName) {
		mTestCaseName = testCaseName;
	}

	public String getDelayTime() {
		return mDelayTime;
	}

	public void setDelayTime(String delayTime) {
		mDelayTime = delayTime;
	}

}
