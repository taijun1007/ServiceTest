package com.cmlab.servicetest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class PingFragment extends Fragment {
	public static final String TAG = "Ping";
	private static final String JSON_PING_URL = "Ping_Send_DestIP";
	private static final String JSON_PING_COUNT = "Ping_Send_Packages";
	private static final String JSON_PINGTASKCHECK = "PingTaskCheck";
	private static final String PINGTESTCASENAME = "Ping";
	private static final int MSG_RUNSUCESS = 0;
	private static final int MSG_RUNFAIL = -1;
	private static final int MSG_NOSETUPFILE = -2;
	private static final int MSG_SETUPFILEERROR = -3;
	private static final String JSON_SDCARD_PATH = "SDCardPath";
	private static final String JSON_TESTCASE_PATH = "TestCasePath";
	private static final String JSON_TESTCASE_FILENAME = "TestCaseFileName";
	private static final String JSON_PARA_PATH = "ParaPath";
	private static final String JSON_PARA_FILENAME = "ParaFileName";
	private static final String JSON_TASK_PATH = "TaskPath";
	private static final String JSON_TASK_FILENAME = "TaskFileName";
	private static final String JSON_LOG_PATH = "logpath";
	private static final String JSON_PARAFILE = "testcaseparafile";
	

	private EditText mPingUrlEdit;
	private EditText mPingCountEdit;
	private CheckBox mTaskCheck;
	private Button mPingButton;
	private Button mStopButton;
	private Button mDeleteButton;
	private String mJSON_FILENAME;
	private String mJSON_SETUP_FILENAME;
	private String mPingLogPath = "/sdcard/testcase";
	private String mPingTxtFile = "ping.txt";
	
	PingHandler mPingHandler = new PingHandler();
	PingThread mPingThread;
	private Process mProcess;
	private Tools mTool;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mJSON_FILENAME = "/sdcard/testcase/testcaseparameter.json";  //default para file
		mJSON_SETUP_FILENAME = Environment.getExternalStorageDirectory().getPath()
				+ "/" + getActivity().getString(R.string.JSON_FILEDIR_SETUP)
				+ "/" + getActivity().getString(R.string.JSON_FILENAME_SETUP);
		File file = new File(mJSON_SETUP_FILENAME);
		if (file.exists()) {
			JSONArray array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
//			String sdCardPath = "/sdcard";
//			String paraPath = "testcase";
//			String paraFileName = "testcaseparameter.json";
			if (array != null) {
				try {
					JSONObject json = array.getJSONObject(0);
//					sdCardPath = json.getString(JSON_SDCARD_PATH);
//					paraPath = json.getString(JSON_PARA_PATH);
//					paraFileName = json.getString(JSON_PARA_FILENAME);
					mPingLogPath = json.getString(JSON_LOG_PATH);
					mJSON_FILENAME = json.getString(JSON_PARAFILE);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}			
//			mJSON_FILENAME = sdCardPath + "/" + paraPath + "/" + paraFileName;
//		} else {
//			//if setup.json file does not exist, then use the default file path
//			mJSON_FILENAME = Environment.getExternalStorageDirectory().getPath()
//					+ "/" + getActivity().getString(R.string.JSON_FILEDIR_PARAMETER)
//					+ "/" + getActivity().getString(R.string.JSON_FILENAME_PARAMETER);
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_ping, container, false);
		mPingUrlEdit = (EditText)v.findViewById(R.id.pingUrlEdit);
		mPingCountEdit = (EditText)v.findViewById(R.id.pingCountEdit);
		mTaskCheck = (CheckBox)v.findViewById(R.id.addPingTaskCheckBox);
		//read and display the saved parameter
		PingTestCaseParaSave pingPara = PingTestCaseParaSave.get(getActivity());
		HashMap<String, String> pingParaMap = pingPara.getPingTestCaseParaMap();
		mPingUrlEdit.setText(pingParaMap.get(JSON_PING_URL));
		mPingCountEdit.setText(pingParaMap.get(JSON_PING_COUNT));
		mTaskCheck.setChecked(Boolean.parseBoolean(pingParaMap.get(JSON_PINGTASKCHECK)));
		mPingButton = (Button)v.findViewById(R.id.pingButton);
		mPingButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					doPing();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		});
		mStopButton = (Button)v.findViewById(R.id.stopButton);
		mStopButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doStop();
			}
		});
		mStopButton.setEnabled(false);
		mDeleteButton = (Button)v.findViewById(R.id.deletePingTxtButton);
		mDeleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doDelete();
			}
		});
		//check if the ping.txt exist
		File file = new File(mPingLogPath + "/" + mPingTxtFile);
		if (file.exists()) {
			//if ping.txt exists, enable the delete button
			mDeleteButton.setEnabled(true);
		} else {
			//if ping.txt does not exist, disable the delete button
			mDeleteButton.setEnabled(false);
		}
		mTaskCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doClickCheck();
			}
		});
		if (mTaskCheck.isChecked()) {
			mPingButton.setText(R.string.caption_save);
		} else {
			mPingButton.setText(R.string.caption_ping);
		}
		return v;
	}
	
	private void doPing() throws JSONException {
		String url;
		int count;
		//check and get the ping url
		if (mPingUrlEdit.getText().length() > 0) {
			url = mPingUrlEdit.getText().toString();
		} else {
			Toast.makeText(getActivity(), R.string.toast_input_ping_url, Toast.LENGTH_SHORT).show();
			return;
		}
		//check and get the ping count
		if (mPingCountEdit.getText().length() > 0) {
			try {
				count = Integer.parseInt(mPingCountEdit.getText().toString());
			} catch(NumberFormatException e) {
				Toast.makeText(getActivity(), R.string.toast_input_count_error, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(getActivity(), R.string.toast_input_ping_count, Toast.LENGTH_SHORT).show();
			return;
		}
		if (count <= 0) {
			Toast.makeText(getActivity(), R.string.toast_input_count_error, Toast.LENGTH_SHORT).show();
			return;
		}
		//Write the parameters into the testcaseparamenter.json file
		JSONObject json = new JSONObject();
		boolean isWriteOK;
		JSONArray pingParaArray = Tools.readJSONFile(mJSON_FILENAME);
		if (pingParaArray == null) {
			json.put(JSON_PING_URL, url);
			json.put(JSON_PING_COUNT, Integer.toString(count));
			pingParaArray = new JSONArray();
			pingParaArray.put(json);
			isWriteOK = Tools.writeJSONFile(pingParaArray, mJSON_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			json = pingParaArray.getJSONObject(0);
			json.put(JSON_PING_URL, url);
			json.put(JSON_PING_COUNT, Integer.toString(count));
			pingParaArray.put(0, json);
			isWriteOK = Tools.writeJSONFile(pingParaArray, mJSON_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_update_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		if (mTaskCheck.isChecked()) {
			Toast.makeText(getActivity(), R.string.toast_para_saved, Toast.LENGTH_SHORT).show();
		} else {
			//start a new thread to run testcase
			if (mPingThread == null) {
				mPingThread = new PingThread();
				mPingThread.start();
				mStopButton.setEnabled(true);
				mDeleteButton.setEnabled(false);
				Toast.makeText(getActivity(), R.string.toast_testcase_run, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), R.string.toast_thread_started, Toast.LENGTH_SHORT).show();
			}
			//set the activity variable mIsButtonClick
			PingActivity pingActivity = (PingActivity)getActivity();
			pingActivity.setIsButtonClick(true);
		}
	}
	
	private void doStop() {
		mPingThread.interrupt();	//break the blocking thread, throw the exception and exit
	}
	
	private void doDelete() {
		File file = new File(mPingLogPath + "/" + mPingTxtFile);
		if(file.exists()) {
			boolean isOK = file.delete();
			if (isOK) {
				Toast.makeText(getActivity(), R.string.toast_deletefile_success, Toast.LENGTH_SHORT).show();
				mDeleteButton.setEnabled(false);
			} else {
				Toast.makeText(getActivity(), R.string.toast_deletefile_fail, Toast.LENGTH_SHORT).show();
			}
		} else {
			mDeleteButton.setEnabled(false);
		}
	}
	
	private void doClickCheck() {
		SelectedTestCase selectedTestCase = SelectedTestCase.get(getActivity());
		ArrayList<String> testCaseList = selectedTestCase.getTestCaseList();
		if (mTaskCheck.isChecked()) {
			mPingButton.setText(R.string.caption_save);
			//add test case name to selected test case
			testCaseList.add(PINGTESTCASENAME);
		} else {
			mPingButton.setText(R.string.caption_ping);
			//delete  test case name from selected test case
			testCaseList.remove(PINGTESTCASENAME);
		}
		selectedTestCase.setTestCaseList(testCaseList);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//save the parameter on the screen into the variable before exit the window
		PingTestCaseParaSave pingPara = PingTestCaseParaSave.get(getActivity());
		HashMap<String, String> pingParaMap = pingPara.getPingTestCaseParaMap();
		pingParaMap.put(JSON_PING_URL, mPingUrlEdit.getText().toString());
		pingParaMap.put(JSON_PING_COUNT, mPingCountEdit.getText().toString());
		String s;
		if (mTaskCheck.isChecked()) {
			s = "true";
		} else {
			s = "false";
		}
		pingParaMap.put(JSON_PINGTASKCHECK, s);
		pingPara.setPingTestCaseParaMap(pingParaMap);
	}
	
	private class PingThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//get the testcase.jar file path
			JSONArray array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
			JSONObject json;
			if (array == null) {
				mPingHandler.obtainMessage(MSG_NOSETUPFILE).sendToTarget();  //return the result to the UI thread
				return;
			}
			String sdCardPath;
			String testCasePath;
			String testCaseFileName;
			try {
				json = array.getJSONObject(0);
				sdCardPath = json.getString(JSON_SDCARD_PATH);
				testCasePath = json.getString(JSON_TESTCASE_PATH);
				testCaseFileName = json.getString(JSON_TESTCASE_FILENAME);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
				mPingHandler.obtainMessage(MSG_SETUPFILEERROR).sendToTarget();  //return the result to the UI thread
				return;
			}
			//exec cmd to implement the test case
			String[] cmd = new String[] {//"su",
					"uiautomator runtest " + sdCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
					+ "/" + testCasePath
					+ "/" + testCaseFileName
					+ " -c com.testcase.Ping"
			};
			try {
				mTool = new Tools();
				mProcess = mTool.getProcess();
				mTool.execShellCMD(cmd, TAG);
			} catch (IOException |InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				mProcess.destroy();
				mTool.killProcess("ping");
				mProcess = null;
				mTool = null;
				mPingHandler.obtainMessage(MSG_RUNFAIL).sendToTarget();  //return the result to the UI thread
				return;
			}
			mProcess = null;
			mTool = null;
			mPingHandler.obtainMessage(MSG_RUNSUCESS).sendToTarget();  //return the result to the UI thread
		}
		
	}
	
	private class PingHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//the test case running thread return the result if the test case run successfully
			switch(msg.what) {
			case MSG_RUNSUCESS:
				Toast.makeText(getActivity(), R.string.toast_testcase_run_sucess, Toast.LENGTH_SHORT).show();
				break;
			case MSG_RUNFAIL:
				Toast.makeText(getActivity(), R.string.toast_testcase_run_fail, Toast.LENGTH_SHORT).show();
				break;
			case MSG_NOSETUPFILE:
				Toast.makeText(getActivity(), R.string.toast_no_setup_file, Toast.LENGTH_SHORT).show();
				break;
			case MSG_SETUPFILEERROR:
				Toast.makeText(getActivity(), R.string.toast_setup_file_error, Toast.LENGTH_SHORT).show();
				break;
			}
			mPingThread = null;
			mStopButton.setEnabled(false);
			mDeleteButton.setEnabled(true);
		}
		
	}

}
