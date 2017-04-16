package com.cmlab.servicetest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

public class CallFragment extends Fragment {
	public static final String TAG = "CallTest";
	private static final String JSON_DIALNUMBER = "Call_Send_DestID";
	private static final String JSON_DIALDURATION = "Call_Send_HoldTime";
	private static final String JSON_DIALREPEATTIMES = "Call_Send_ShortRptTimes";
	private static final String JSON_DIALTASKCHECK = "DialTaskCheck";
	private static final String CALLTESTCASENAME = "TelCaseUI";
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
	private static final String JSON_PARAFILE = "testcaseparafile";
	
	//moved to R.java
	//private static final String JSON_FILENAME = "testcaseparameter.json";
	//private static final String JSON_FILEDIR = "/testcase/";
	//
	
	private EditText mEditText;
	private EditText mCallDurationEditText;
	private EditText mRepeatTimesEditText;
	private CheckBox mTaskCheck;
	private Button mCallButton;
	private String mJSON_FILENAME;
	private String mJSON_SETUP_FILENAME;
	
	private CallHandler mHandler = new CallHandler();
	private CallRunnable mRunnable = new CallRunnable();
	private Thread mThread;
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
		View v = inflater.inflate(R.layout.fragment_call, container, false);
		mEditText = (EditText)v.findViewById(R.id.editDialNum);
		mCallDurationEditText = (EditText)v.findViewById(R.id.callTimeEdit);
		mRepeatTimesEditText = (EditText)v.findViewById(R.id.callRepeatTimesEdit);
		mTaskCheck = (CheckBox)v.findViewById(R.id.addCallTaskCheckBox);
		//test1
//		mEditText.setText(mJSON_FILENAME);
		//test1 end
		//read and display the saved parameter
		CallTestCaseParaSave callPara = CallTestCaseParaSave.get(getActivity());
		HashMap<String, String> callParaMap = callPara.getCallTestCaseParaMap();
		mEditText.setText(callParaMap.get(JSON_DIALNUMBER));
		mCallDurationEditText.setText(callParaMap.get(JSON_DIALDURATION));
		mRepeatTimesEditText.setText(callParaMap.get(JSON_DIALREPEATTIMES));
		mTaskCheck.setChecked(Boolean.parseBoolean(callParaMap.get(JSON_DIALTASKCHECK)));
		mCallButton = (Button)v.findViewById(R.id.callButton);
		mCallButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					doCall(v);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		});
		mTaskCheck.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doClickCheck();
			}
		});
		if (mTaskCheck.isChecked()) {
			mCallButton.setText(R.string.caption_save);
		} else {
			mCallButton.setText(R.string.call_button);
		}
		return v;
	}
	
	private void doCall(View view) throws IOException, JSONException {
		String dialNumber;
		String dialDuration;
		String dialRepeatTimes;
		//add code to judge if the phone number is legal
		if (mEditText.getText().length() > 0) {
			boolean isAllNum = true;
			for (int i = 1; i <= mEditText.getText().length(); i++) {
				char ch = mEditText.getText().charAt(i-1);
				if ((ch >= '0') && (ch <= '9')) {
					
				}else if((i == 1)&&(ch == '+')) {
					
				}else {
					isAllNum = false;
					break;
				}
			}
			if (isAllNum) {
				dialNumber = mEditText.getText().toString();
			} else {
				Toast.makeText(getActivity(), R.string.hint_phone_number_illegal, Toast.LENGTH_SHORT).show();
				return;
			}
		}else {
			Toast.makeText(getActivity(), R.string.hint_input_phone_number, Toast.LENGTH_SHORT).show();
			return;
		}
		//get the call duration time
		if (mCallDurationEditText.getText().length() > 0) {
			boolean isAllNum = true;
			for (int i = 1; i <= mCallDurationEditText.getText().length(); i++) {
				char ch = mCallDurationEditText.getText().charAt(i-1);
				if ((ch >= '0') && (ch <= '9')) {
					
				}else {
					isAllNum = false;
					break;
				}
			}
			if (isAllNum) {
				dialDuration = mCallDurationEditText.getText().toString();
			} else {
				Toast.makeText(getActivity(), R.string.hint_call_duration_illegal, Toast.LENGTH_SHORT).show();
				return;
			}
			if (Integer.parseInt(dialDuration) <= 0) {
				Toast.makeText(getActivity(), R.string.hint_call_duration_illegal, Toast.LENGTH_SHORT).show();
				return;
			}
		}else {
			Toast.makeText(getActivity(), R.string.hint_input_call_duration, Toast.LENGTH_SHORT).show();
			return;
		}
		//get the repeat times
		if (mRepeatTimesEditText.getText().length() > 0) {
			boolean isAllNum = true;
			for (int i = 1; i <= mRepeatTimesEditText.getText().length(); i++) {
				char ch = mRepeatTimesEditText.getText().charAt(i-1);
				if ((ch >= '0') && (ch <= '9')) {
					
				}else {
					isAllNum = false;
					break;
				}
			}
			if (isAllNum) {
				dialRepeatTimes = mRepeatTimesEditText.getText().toString();
			} else {
				Toast.makeText(getActivity(), R.string.hint_repeat_times_illegal, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(getActivity(), R.string.hint_input_repeat_times, Toast.LENGTH_SHORT).show();
			return;
		}
		//Write the parameters into the testcaseparamenter.json file
		JSONObject json = new JSONObject();
		boolean isWriteOK;
		JSONArray dialNumberArray = Tools.readJSONFile(mJSON_FILENAME);
		//test4
//		mCallButton.setText(mJSON_FILENAME);
		//test4 end
		if (dialNumberArray == null) {
			//Toast.makeText(getActivity(), R.string.hint_read_jsonfile_error, Toast.LENGTH_SHORT).show();
			json.put(JSON_DIALNUMBER, dialNumber);
			json.put(JSON_DIALDURATION, dialDuration);
			json.put(JSON_DIALREPEATTIMES, dialRepeatTimes);
			dialNumberArray = new JSONArray();
			dialNumberArray.put(json);
			isWriteOK = Tools.writeJSONFile(dialNumberArray, mJSON_FILENAME);
			//test2
//			mEditText.setText("No such dir!");
			//test2 end
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			json = dialNumberArray.getJSONObject(0);
			json.put(JSON_DIALNUMBER, dialNumber);
			json.put(JSON_DIALDURATION, dialDuration);
			json.put(JSON_DIALREPEATTIMES, dialRepeatTimes);
			dialNumberArray.put(0, json);
			//test3
//			mEditText.setText("Find the file!");
			//test3 end
			isWriteOK = Tools.writeJSONFile(dialNumberArray, mJSON_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_update_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		if (mTaskCheck.isChecked()) {
			Toast.makeText(getActivity(), R.string.toast_para_saved, Toast.LENGTH_SHORT).show();
		} else {
			//start a new thread to run testcase
			if (mThread == null) {
				mThread = new Thread(mRunnable);
				mThread.start();
			} else {
				Toast.makeText(getActivity(), R.string.toast_thread_started, Toast.LENGTH_SHORT).show();
			}
			//set the activity variable mIsButtonClick
			CallActivity callActivity = (CallActivity)getActivity();
			callActivity.setIsButtonClick(true);
		}
	}
	
	private void doClickCheck() {
		SelectedTestCase selectedTestCase = SelectedTestCase.get(getActivity());
		ArrayList<String> testCaseList = selectedTestCase.getTestCaseList();
		if (mTaskCheck.isChecked()) {
			mCallButton.setText(R.string.caption_save);
			//add test case name to selected test case
			testCaseList.add(CALLTESTCASENAME);
		} else {
			mCallButton.setText(R.string.call_button);
			//delete  test case name from selected test case
			testCaseList.remove(CALLTESTCASENAME);
		}
		selectedTestCase.setTestCaseList(testCaseList);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//save the parameter on the screen into the variable before exit the window
		CallTestCaseParaSave callPara = CallTestCaseParaSave.get(getActivity());
		HashMap<String, String> callParaMap = callPara.getCallTestCaseParaMap();
		callParaMap.put(JSON_DIALNUMBER, mEditText.getText().toString());
		callParaMap.put(JSON_DIALDURATION, mCallDurationEditText.getText().toString());
		callParaMap.put(JSON_DIALREPEATTIMES, mRepeatTimesEditText.getText().toString());
		String s;
		if (mTaskCheck.isChecked()) {
			s = "true";
		} else {
			s = "false";
		}
		callParaMap.put(JSON_DIALTASKCHECK, s);
		callPara.setCallTestCaseParaMap(callParaMap);
	}
	
	private class CallHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			//the test case running thread return the result if the test case run successfully
			switch(msg.what) {
			case MSG_RUNSUCESS:
				Toast.makeText(getActivity(), R.string.toast_testcase_run_sucess, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			case MSG_RUNFAIL:
				Toast.makeText(getActivity(), R.string.toast_testcase_run_fail, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			case MSG_NOSETUPFILE:
				Toast.makeText(getActivity(), R.string.toast_no_setup_file, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			case MSG_SETUPFILEERROR:
				Toast.makeText(getActivity(), R.string.toast_setup_file_error, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			}
		}
		
	}
	
	private class CallRunnable implements Runnable {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			//test5
			//SAMSUNG: Environment.getExternalStorageDirectory().getPath()="/storage/emulated/0"
			//the directory "/0" does not exist.
			//the correct sdcard path is "/storage/emulated/legacy"
			//In APP, the sdcard path will be found correctly, but in shell not found!
			//HUAWEI MATE2: OK!
//			String sdPath = Environment.getExternalStorageDirectory().getPath();
//			File file = new File(sdPath);
//			if (!file.isDirectory()) {
//				//if sdPath gotton from the environment is wrong, then use "/sdcard"
//				sdPath = "/sdcard";
//			}
			//test5 end
			//get the testcase.jar file path
			JSONArray array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
			JSONObject json;
			if (array == null) {
				mHandler.obtainMessage(MSG_NOSETUPFILE).sendToTarget();  //return the result to the UI thread
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
				mHandler.obtainMessage(MSG_SETUPFILEERROR).sendToTarget();  //return the result to the UI thread
				return;
			}
			//exec cmd to implement the test case
			String[] cmd = new String[] {//"su",
					"uiautomator runtest " + sdCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
					+ "/" + testCasePath
					+ "/" + testCaseFileName
					+ " -c com.testcase.TelCaseUI"
			};
			//test7
//			String[] cmd = new String[] {"uiautomator runtest /sdcard/testcase/testcase.jar -c com.testcase.CatchLog"};
			//test7 end
			try {
				mTool = new Tools();
				mProcess = mTool.getProcess();
				mTool.execShellCMD(cmd, TAG);
			} catch (IOException |InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				mProcess.destroy();
				mTool.killProcess("uiautomator");
				Tools tool = new Tools();
				cmd = new String[] {
						"uiautomator runtest " + sdCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
								+ "/" + testCasePath
								+ "/" + testCaseFileName
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
				mHandler.obtainMessage(MSG_RUNFAIL).sendToTarget();  //return the result to the UI thread
				return;
			}
			//test6
			Tools tool = new Tools();
			cmd = new String[] {
					"uiautomator runtest " + sdCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
							+ "/" + testCasePath
							+ "/" + testCaseFileName
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
			mHandler.obtainMessage(MSG_RUNSUCESS).sendToTarget();  //return the result to the UI thread
		}
		
	}
	
}
