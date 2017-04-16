package com.cmlab.servicetest;

import java.io.File;
import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;

public class TaskRunner {
	private static final String TAG = "TaskRunner";
	private static final String JSON_TASKREPEATTIMES = "TaskRepeatTimes";
	private static final String JSON_TASKCASENAME = "TaskCaseName";
	private static final String JSON_TASKCASEDELAYTIME = "TaskCaseDelayTime";
	private static final String JSON_SDCARD_PATH = "SDCardPath";
	private static final String JSON_TESTCASE_PATH = "TestCasePath";
	private static final String JSON_TESTCASE_FILENAME = "TestCaseFileName";
	private static final String JSON_PARA_PATH = "ParaPath";
	private static final String JSON_PARA_FILENAME = "ParaFileName";
	private static final String JSON_TASK_PATH = "TaskPath";
	private static final String JSON_TASK_FILENAME = "TaskFileName";
	
	private String mTaskFileName;
	private String mJSON_SETUP_FILENAME;
	private Context mContext;
	private String mSDCardPath;
	private String mTestCasePath;
	private String mTestCaseFileName;
	private Process mProcess;
	private Tools mTool;
	
	public TaskRunner(Context context, String taskFileName) {
		mContext = context;
		mTaskFileName = taskFileName;
	}
	
	public int run() {
		/*
		 * return the value of isOk to indicate the run test case result
		 * return 0: finish task normally
		 * return -1: task file is empty
		 * return -2: JSON format error or no such value
		 * return -3: exec cmd, IOException occur!
		 * return -4: exec cmd, InterruptedException occur!
		 * return -5: setup.json file does not exist
		 * return -6: setup.json file error
		 */
		mJSON_SETUP_FILENAME = Environment.getExternalStorageDirectory().getPath()
				+ "/" + mContext.getString(R.string.JSON_FILEDIR_SETUP)
				+ "/" + mContext.getString(R.string.JSON_FILENAME_SETUP);
		File file = new File(mJSON_SETUP_FILENAME);
		if (file.exists()) {
			JSONArray array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
			mSDCardPath = "/sdcard";
			mTestCasePath = "testcase";
			mTestCaseFileName = "testcase.jar";
			if (array != null) {
				try {
					JSONObject json = array.getJSONObject(0);
					mSDCardPath = json.getString(JSON_SDCARD_PATH);
					mTestCasePath = json.getString(JSON_TESTCASE_PATH);
					mTestCaseFileName = json.getString(JSON_TESTCASE_FILENAME);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
					return -6;
				}
			} else {
				return -7;
			}
		} else {
			//if setup.json file does not exist, then use the default file path
//			mSDCardPath = "/sdcard";
//			mTestCasePath = "testcase";
//			mTestCaseFileName = "testcase.jar";
			return -5;
		}
		JSONArray taskListArray = Tools.readJSONFile(mTaskFileName);
		if (taskListArray == null) {
			return -1;
		}
		String[] cmd;
		try {
			String testCaseName;
			int delayTime;
			JSONObject json = taskListArray.getJSONObject(0);
			int taskRepeatTimes = Integer.parseInt(json.getString(JSON_TASKREPEATTIMES));
			for (int i = 1; i <= taskRepeatTimes; i++) {
				for (int j = 1; j < taskListArray.length(); j++) {
					json = taskListArray.getJSONObject(j);
					testCaseName = json.getString(JSON_TASKCASENAME);
					delayTime = Integer.parseInt(json.getString(JSON_TASKCASEDELAYTIME));
					cmd = new String[] {//"su",
						"uiautomator runtest " + mSDCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
						+ "/" + mTestCasePath
						+ "/" + mTestCaseFileName
						+ " -c com.testcase." + testCaseName
					};
					mTool = new Tools();
					mProcess = mTool.getProcess();
					mTool.execShellCMD(cmd, TAG);
					Tools.sleep(delayTime*1000);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return -2;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return -3;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			mProcess.destroy();
			mTool.killProcess("uiautomator");
			Tools tool = new Tools();
			cmd = new String[] {
					"uiautomator runtest " + mSDCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
							+ "/" + mTestCasePath
							+ "/" + mTestCaseFileName
							+ " -c com.testcase.StopCase"
			};
			try {
				tool.execShellCMD(cmd, TAG);
			} catch (IOException | InterruptedException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			mProcess = null;
			mTool = null;
			return -4;
		}
		//test6
		Tools tool = new Tools();
		cmd = new String[] {
				"uiautomator runtest " + mSDCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
						+ "/" + mTestCasePath
						+ "/" + mTestCaseFileName
						+ " -c com.testcase.StopCase"
		};
		try {
			tool.execShellCMD(cmd, TAG);
		} catch (IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			//e1.printStackTrace();
		}
		//test6 end
		mProcess = null;
		mTool = null;	
		return 0;
	}

}
