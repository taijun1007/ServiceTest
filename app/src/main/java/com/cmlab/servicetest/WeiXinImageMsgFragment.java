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

public class WeiXinImageMsgFragment extends Fragment {

	public static final String TAG = "WeiXinImageMsgTest";
	private static final String JSON_WEIXINUSERNAMEIMAGE = "WeiXin_Image_DestID";
	private static final String JSON_WEIXINIMAGEINDEX = "WeiXin_Image_Num";
	private static final String JSON_WEIXINIMAGEMSGREPEATTIMES = "WeiXin_Image_RptTimes";
	private static final String JSON_WEIXINIMAGETASKCHECK = "WeiXinImageTaskCheck";
	private static final String WEIXINIMAGETESTCASENAME = "WeiXinImageCase";
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
	
	private EditText mWeiXinImageMsgSendTo;
	private EditText mWeiXinImageIndex;
	private EditText mWeiXinImageMsgRepeatTimes;
	private Button mWeiXinImageMsgButton;
	private CheckBox mTaskCheck;
	private String mJSON_FILENAME;
	private String mJSON_SETUP_FILENAME;
	
	private WeiXinHandler mHandler = new WeiXinHandler();
	private WeiXinRunnable mRunnable = new WeiXinRunnable();
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
		View v = inflater.inflate(R.layout.fragment_weixinimagemsg, container, false);
		mWeiXinImageMsgSendTo = (EditText)v.findViewById(R.id.weiXinImageSendToEdit);
		mWeiXinImageIndex = (EditText)v.findViewById(R.id.weiXinImageIndexEdit);
		mWeiXinImageMsgRepeatTimes = (EditText)v.findViewById(R.id.weiXinImageRepeatTimesEdit);
		mTaskCheck = (CheckBox)v.findViewById(R.id.addWeiXinImageTaskCheckBox);
		WeiXinImageMsgTestCaseParaSave wxImagePara = WeiXinImageMsgTestCaseParaSave.get(getActivity());
		HashMap<String, String> wxImageParaMap = wxImagePara.getWeiXinImageMsgTestCaseParaMap();
		mWeiXinImageMsgSendTo.setText(wxImageParaMap.get(JSON_WEIXINUSERNAMEIMAGE));
		mWeiXinImageIndex.setText(wxImageParaMap.get(JSON_WEIXINIMAGEINDEX));
		mWeiXinImageMsgRepeatTimes.setText(wxImageParaMap.get(JSON_WEIXINIMAGEMSGREPEATTIMES));
		mTaskCheck.setChecked(Boolean.parseBoolean(wxImageParaMap.get(JSON_WEIXINIMAGETASKCHECK)));
		mWeiXinImageMsgButton = (Button)v.findViewById(R.id.weiXinImageStartButton);
		mWeiXinImageMsgButton.setOnClickListener(new View.OnClickListener() {
			
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
			mWeiXinImageMsgButton.setText(R.string.caption_save);
		} else {
			mWeiXinImageMsgButton.setText(R.string.sms_button);
		}
		return v;
	}
	
	private void doStart(View view) {
		String sendToUserName;
		String weiXinImageIndex;
		String weiXinImageMsgRepeatTimes;
		//get user name to send wei xin text message
		if (mWeiXinImageMsgSendTo.getText().length() > 0 ) {
			sendToUserName = mWeiXinImageMsgSendTo.getText().toString();
		} else {
			Toast.makeText(getActivity(), R.string.hint_weixin_input_user_name, Toast.LENGTH_SHORT).show();
			return;
		}
		//get wei xin image index
		if (mWeiXinImageIndex.getText().length() > 0) {
			boolean isAllNum = true;
			for (int i = 1; i <= mWeiXinImageIndex.getText().length(); i++) {
				char ch = mWeiXinImageIndex.getText().charAt(i-1);
				if ((ch >= '0') && (ch <= '9')) {
					
				}else {
					isAllNum = false;					
					break;
				}
			}
			if (isAllNum) {
				weiXinImageIndex = mWeiXinImageIndex.getText().toString();
			} else {
				Toast.makeText(getActivity(), R.string.hint_image_index_illegal, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(getActivity(), R.string.hint_input_image_index, Toast.LENGTH_SHORT).show();
			return;
		}
		//get repeat times
		if (mWeiXinImageMsgRepeatTimes.getText().length() > 0) {
			boolean isAllNum = true;
			for (int i = 1; i <= mWeiXinImageMsgRepeatTimes.getText().length(); i++) {
				char ch = mWeiXinImageMsgRepeatTimes.getText().charAt(i-1);
				if ((ch >= '0') && (ch <= '9')) {
					
				}else {
					isAllNum = false;					
					break;
				}
			}
			if (isAllNum) {
				weiXinImageMsgRepeatTimes = mWeiXinImageMsgRepeatTimes.getText().toString();
			} else {
				Toast.makeText(getActivity(), R.string.hint_repeat_times_illegal, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			Toast.makeText(getActivity(), R.string.hint_input_repeat_times, Toast.LENGTH_SHORT).show();
			return;
		}
		//write the parameter into the json parameter file
		JSONObject json = new JSONObject();
		boolean isWriteOK;
		JSONArray weiXinImageMsgParaArray = Tools.readJSONFile(mJSON_FILENAME);
		if (weiXinImageMsgParaArray == null) {
			weiXinImageMsgParaArray = new JSONArray();
			try {
				json.put(JSON_WEIXINUSERNAMEIMAGE, sendToUserName);
				json.put(JSON_WEIXINIMAGEINDEX, weiXinImageIndex);
				json.put(JSON_WEIXINIMAGEMSGREPEATTIMES, weiXinImageMsgRepeatTimes);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return;
			}
			weiXinImageMsgParaArray.put(json);
			isWriteOK = Tools.writeJSONFile(weiXinImageMsgParaArray, mJSON_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			try {
				json = weiXinImageMsgParaArray.getJSONObject(0);
				json.put(JSON_WEIXINUSERNAMEIMAGE, sendToUserName);
				json.put(JSON_WEIXINIMAGEINDEX, weiXinImageIndex);
				json.put(JSON_WEIXINIMAGEMSGREPEATTIMES, weiXinImageMsgRepeatTimes);
				weiXinImageMsgParaArray.put(0, json);
				isWriteOK = Tools.writeJSONFile(weiXinImageMsgParaArray, mJSON_FILENAME);
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
				mThread = new Thread(mRunnable);
				mThread.start();
			} else {
				Toast.makeText(getActivity(), R.string.toast_thread_started, Toast.LENGTH_SHORT).show();
			}
			//set the activity variable mIsButtonClick
			WeiXinImageMsgActivity weiXinImageMsgActivity = (WeiXinImageMsgActivity)getActivity();
			weiXinImageMsgActivity.setIsButtonClick(true);
		}
		
	}
	
	private void doClickCheck() {
		SelectedTestCase selectedTestCase = SelectedTestCase.get(getActivity());
		ArrayList<String> testCaseList = selectedTestCase.getTestCaseList();
		if (mTaskCheck.isChecked()) {
			mWeiXinImageMsgButton.setText(R.string.caption_save);
			//add test case name to selected test case
			testCaseList.add(WEIXINIMAGETESTCASENAME);
		} else {
			mWeiXinImageMsgButton.setText(R.string.sms_button);
			//delete  test case name from selected test case
			testCaseList.remove(WEIXINIMAGETESTCASENAME);
		}
		selectedTestCase.setTestCaseList(testCaseList);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		WeiXinImageMsgTestCaseParaSave wxImagePara = WeiXinImageMsgTestCaseParaSave.get(getActivity());
		HashMap<String, String> wxImageParaMap = wxImagePara.getWeiXinImageMsgTestCaseParaMap();
		wxImageParaMap.put(JSON_WEIXINUSERNAMEIMAGE, mWeiXinImageMsgSendTo.getText().toString());
		wxImageParaMap.put(JSON_WEIXINIMAGEINDEX, mWeiXinImageIndex.getText().toString());
		wxImageParaMap.put(JSON_WEIXINIMAGEMSGREPEATTIMES, mWeiXinImageMsgRepeatTimes.getText().toString());
		String s;
		if (mTaskCheck.isChecked()) {
			s = "true";
		} else {
			s = "false";
		}
		wxImageParaMap.put(JSON_WEIXINIMAGETASKCHECK, s);
		wxImagePara.setWeiXinImageMsgTestCaseParaMap(wxImageParaMap);
	}
	
	private class WeiXinHandler extends Handler {

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
	
	private class WeiXinRunnable implements Runnable {
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
			cmd = new String[] {//"su",
					"uiautomator runtest " + sdCardPath  //HUAWEI MATE2 & SAMSUNG has the link dir "/sdcard"
					+ "/" + testCasePath
					+ "/" + testCaseFileName
					+ " -c com.testcase.WeiXinImageCase"
			};
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
