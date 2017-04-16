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

public class SMSFragment extends Fragment {
	public static final String TAG = "SMSTest";
	private static final String JSON_SMSNUMBER = "SMSNumber";
	private static final String JSON_SMSCONTENT = "SMSContent";
	private static final String JSON_SMSCONTENTLONG = "SMSContentLong";
	private static final String JSON_SMSREPEATTIMES = "SMSRepeatTimes";
	private static final String JSON_SMSLONGCHECK = "SMSLongCheck";
	private static final String JSON_SMSTASKCHECK = "SMSTaskCheck";
	private static final String SMSTESTCASENAME = "SMSCaseUI";
	private static final String SMSLONGTESTCASENAME = "SMSLCaseUI";
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
	
	private EditText mSMSSendTo;
	private EditText mSMSContent;
	private EditText mSMSRepeatTimes;
	private CheckBox mSMSIsLong;
	private CheckBox mTaskCheck;
	private Button mSMSButton;
	private String mJSON_FILENAME;
	private String mJSON_SETUP_FILENAME;
	
	private SMSHandler mHandler = new SMSHandler();
	private SMSRunnable mRunnable;
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
		View v = inflater.inflate(R.layout.fragment_sms, container, false);
		mSMSSendTo = (EditText)v.findViewById(R.id.smsSendToEdit);
		mSMSContent = (EditText)v.findViewById(R.id.smsContentEdit);
		mSMSRepeatTimes = (EditText)v.findViewById(R.id.smsRepeatTimesEdit);
		mSMSIsLong = (CheckBox)v.findViewById(R.id.checkLongSMS);
		mTaskCheck = (CheckBox)v.findViewById(R.id.addSMSTaskCheckBox);
		//read and display the saved parameter
		SMSTestCaseParaSave smsPara = SMSTestCaseParaSave.get(getActivity());
		HashMap<String, String> smsParaMap = smsPara.getSMSTestCaseParaMap();
		mSMSSendTo.setText(smsParaMap.get(JSON_SMSNUMBER));
		mSMSContent.setText(smsParaMap.get(JSON_SMSCONTENT));
		mSMSIsLong.setChecked(Boolean.parseBoolean(smsParaMap.get(JSON_SMSLONGCHECK)));
		mSMSRepeatTimes.setText(smsParaMap.get(JSON_SMSREPEATTIMES));
		mTaskCheck.setChecked(Boolean.parseBoolean(smsParaMap.get(JSON_SMSTASKCHECK)));
		mSMSButton = (Button)v.findViewById(R.id.smsStartButton);
		mSMSButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doStart(v);
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
			mSMSButton.setText(R.string.caption_save);
		} else {
			mSMSButton.setText(R.string.sms_button);
		}
		return v;
	}
	
	private void doStart(View view) {
		String sendToNumber;
		String smsContent;
		String smsRepeatTimes;
		//get phone number to send sms
		if (mSMSSendTo.getText().length() > 0 ) {
			boolean isAllNum = true;
			for (int i = 1; i <= mSMSSendTo.getText().length(); i++) {
				char ch = mSMSSendTo.getText().charAt(i-1);
				if ((ch >= '0') && (ch <= '9')) {
					
				}else {
					isAllNum = false;					
					break;
				}
			}
			if (isAllNum) {
				sendToNumber = mSMSSendTo.getText().toString();
			} else {
				Toast.makeText(getActivity(), R.string.hint_phone_number_illegal, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(getActivity(), R.string.hint_input_phone_number, Toast.LENGTH_SHORT).show();
			return;
		}
		//get sms content
		if (mSMSContent.getText().length() > 0) {
			smsContent = mSMSContent.getText().toString();
		} else {
			Toast.makeText(getActivity(), R.string.hint_input_sms_content, Toast.LENGTH_SHORT).show();
			return;
		}
		//get repeat times
		if (mSMSRepeatTimes.getText().length() > 0) {
			boolean isAllNum = true;
			for (int i = 1; i <= mSMSRepeatTimes.getText().length(); i++) {
				char ch = mSMSRepeatTimes.getText().charAt(i-1);
				if ((ch >= '0') && (ch <= '9')) {
					
				}else {
					isAllNum = false;					
					break;
				}
			}
			if (isAllNum) {
				smsRepeatTimes = mSMSRepeatTimes.getText().toString();
			} else {
				Toast.makeText(getActivity(), R.string.hint_repeat_times_illegal, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(getActivity(), R.string.hint_input_repeat_times, Toast.LENGTH_SHORT).show();
			return;
		}
		//get whether Long SMS is selected
		boolean isLongSMS = mSMSIsLong.isChecked();
		//if long sms test is selected, the sms content will be more than 200 chars, less than 400 chars
		if (isLongSMS) {
			while (smsContent.length() < 200) {
				smsContent = smsContent + smsContent;
			}
		}
		//write the parameter into the json parameter file
		JSONObject json = new JSONObject();
		boolean isWriteOK;
		JSONArray smsParaArray = Tools.readJSONFile(mJSON_FILENAME);
		if (smsParaArray == null) {
			smsParaArray = new JSONArray();
			try {
				json.put(JSON_SMSNUMBER, sendToNumber);
				if (isLongSMS) {
					json.put(JSON_SMSCONTENTLONG, smsContent);
				} else {
					json.put(JSON_SMSCONTENT, smsContent);
				}
				json.put(JSON_SMSREPEATTIMES, smsRepeatTimes);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return;
			}
			smsParaArray.put(json);
			isWriteOK = Tools.writeJSONFile(smsParaArray, mJSON_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			try {
				json = smsParaArray.getJSONObject(0);
				json.put(JSON_SMSNUMBER, sendToNumber);
				if (isLongSMS) {
					json.put(JSON_SMSCONTENTLONG, smsContent);
				} else {
					json.put(JSON_SMSCONTENT, smsContent);
				}
				json.put(JSON_SMSREPEATTIMES, smsRepeatTimes);
				smsParaArray.put(0, json);
				isWriteOK = Tools.writeJSONFile(smsParaArray, mJSON_FILENAME);
				if (!isWriteOK) {
					Toast.makeText(getActivity(), R.string.hint_update_jsonfile_error, Toast.LENGTH_SHORT).show();
					return;
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return;
			}			
		}
		if (mTaskCheck.isChecked()) {
			Toast.makeText(getActivity(), R.string.toast_para_saved, Toast.LENGTH_SHORT).show();
		} else {
			//start a new thread to run testcase
			if (mThread == null) {
				mRunnable = new SMSRunnable(isLongSMS);
				mThread = new Thread(mRunnable);
				mThread.start();
			} else {
				Toast.makeText(getActivity(), R.string.toast_thread_started, Toast.LENGTH_SHORT).show();
			}
			//set the activity variable mIsButtonClick
			SMSActivity smsActivity = (SMSActivity)getActivity();
			smsActivity.setIsButtonClick(true);
		}		
	}
	
	private void doClickCheck() {
		SelectedTestCase selectedTestCase = SelectedTestCase.get(getActivity());
		ArrayList<String> testCaseList = selectedTestCase.getTestCaseList();
		if (mTaskCheck.isChecked()) {
			mSMSButton.setText(R.string.caption_save);
			//add test case name to selected test case
			if (mSMSIsLong.isChecked()) {
				testCaseList.add(SMSLONGTESTCASENAME);
			} else {
				testCaseList.add(SMSTESTCASENAME);
			}
		} else {
			mSMSButton.setText(R.string.sms_button);
			//delete  test case name from selected test case
			if (mSMSIsLong.isChecked()) {
				testCaseList.remove(SMSLONGTESTCASENAME);
			} else {
				testCaseList.remove(SMSTESTCASENAME);
			}
		}
		selectedTestCase.setTestCaseList(testCaseList);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		SMSTestCaseParaSave smsPara = SMSTestCaseParaSave.get(getActivity());
		HashMap<String, String> smsParaMap = smsPara.getSMSTestCaseParaMap();
		smsParaMap.put(JSON_SMSNUMBER, mSMSSendTo.getText().toString());
		smsParaMap.put(JSON_SMSCONTENT, mSMSContent.getText().toString());
		smsParaMap.put(JSON_SMSREPEATTIMES, mSMSRepeatTimes.getText().toString());
		String s;
		if (mSMSIsLong.isChecked()) {
			s = "true";
		} else {
			s = "fasle";
		}
		smsParaMap.put(JSON_SMSLONGCHECK, s);
		if (mTaskCheck.isChecked()) {
			s = "true";
		} else {
			s = "false";
		}
		smsParaMap.put(JSON_SMSTASKCHECK, s);
		smsPara.setSMSTestCaseParaMap(smsParaMap);
	}
	
	private class SMSHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
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
	
	private class SMSRunnable implements Runnable {
		
		private boolean mIsLongSMS;
		
		public SMSRunnable(boolean isLongSMS) {
			mIsLongSMS = isLongSMS;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
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
			String[] cmd;
			if (mIsLongSMS) {
				cmd = new String[] {//"su",
						"uiautomator runtest " + sdCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
						+ "/" + testCasePath
						+ "/" + testCaseFileName
						+ " -c com.testcase.SMSLCaseUI"
				};
			} else {
				cmd = new String[] {//"su",
						"uiautomator runtest " + sdCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
						+ "/" + testCasePath
						+ "/" + testCaseFileName
						+ " -c com.testcase.SMSCaseUI"
				};
			}
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
				mHandler.obtainMessage(MSG_RUNFAIL).sendToTarget();
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
			mHandler.obtainMessage(MSG_RUNSUCESS).sendToTarget();
		}
		
	}

}
