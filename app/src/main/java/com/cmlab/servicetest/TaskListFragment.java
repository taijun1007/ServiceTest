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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class TaskListFragment extends Fragment {
	
	private static final String TAG = "TaskListFragment";
	private static final String JSON_TASKREPEATTIMES = "TaskRepeatTimes";
	private static final String JSON_TASKCASENAME = "TaskCaseName";
	private static final String JSON_TASKCASEDELAYTIME = "TaskCaseDelayTime";
	private static final int MSG_RUNSUCESS = 0;
	private static final int MSG_RUNFAIL_1 = -1;
	private static final int MSG_RUNFAIL_2 = -2;
	private static final int MSG_RUNFAIL_3 = -3;
	private static final int MSG_RUNFAIL_4 = -4;
	private static final int MSG_NOSETUPFILE = -5;
	private static final int MSG_SETUPFILEERROR = -6;
	private static final int MSG_SETUPFILEEMPTY = -7;
	private static final String JSON_SDCARD_PATH = "SDCardPath";
	private static final String JSON_TESTCASE_PATH = "TestCasePath";
	private static final String JSON_TESTCASE_FILENAME = "TestCaseFileName";
	private static final String JSON_PARA_PATH = "ParaPath";
	private static final String JSON_PARA_FILENAME = "ParaFileName";
	private static final String JSON_TASK_PATH = "TaskPath";
	private static final String JSON_TASK_FILENAME = "TaskFileName";
	
	private Button mStartTaskButton;
	private Button mClearTaskButton;
	private ListView mSTCListView;
	private ListView mTaskListView;
	private EditText mTaskRepeatTimesEdit;
	private String mJSON_TaskFileName;
	private String mJSON_SETUP_FILENAME;
	
	private TaskHandler mHandler = new TaskHandler();
	private TaskRunnable mRunnable = new TaskRunnable();
	private Thread mThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mJSON_SETUP_FILENAME = Environment.getExternalStorageDirectory().getPath()
				+ "/" + getActivity().getString(R.string.JSON_FILEDIR_SETUP)
				+ "/" + getActivity().getString(R.string.JSON_FILENAME_SETUP);
		File file = new File(mJSON_SETUP_FILENAME);
		if (file.exists()) {
			JSONArray array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
			String sdCardPath = "/sdcard";
			String taskPath = "testcase";
			String taskFileName = "tasklist.json";
			if (array != null) {
				try {
					JSONObject json = array.getJSONObject(0);
					sdCardPath = json.getString(JSON_SDCARD_PATH);
					taskPath = json.getString(JSON_TASK_PATH);
					taskFileName = json.getString(JSON_TASK_FILENAME);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}			
			mJSON_TaskFileName = sdCardPath + "/" + taskPath + "/" + taskFileName;
		} else {
			//if setup.json file does not exist, then use the default file path
			mJSON_TaskFileName = Environment.getExternalStorageDirectory().getPath()
					+ "/" + getActivity().getString(R.string.JSON_FILEDIR_TASKLIST)
					+ "/" + getActivity().getString(R.string.JSON_FILENAME_TASKLIST);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_tasklist, container, false);
		//read and display the saved parameter
		mTaskRepeatTimesEdit = (EditText)v.findViewById(R.id.taskRepeatTimesEdit);
		TaskListParaSave taskListPara = TaskListParaSave.get(getActivity());
		HashMap<String, String> taskListParaMap = taskListPara.getTaskListParaMap();
		mTaskRepeatTimesEdit.setText(taskListParaMap.get(JSON_TASKREPEATTIMES));
		mStartTaskButton = (Button)v.findViewById(R.id.taskStartButton);
		mStartTaskButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doStart();
			}
		});
		mClearTaskButton = (Button)v.findViewById(R.id.taskClearButton);
		mClearTaskButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doClear();
			}
		});
		mSTCListView = (ListView)v.findViewById(R.id.selectedTestCaseListView);
		SelectedTestCase sTC = SelectedTestCase.get(getActivity());
		ArrayList<String> sTCArrayList = sTC.getTestCaseList();
		SelectedTestCaseAdapter sTCAdapter = new SelectedTestCaseAdapter(sTCArrayList);
		mSTCListView.setAdapter(sTCAdapter);
		mTaskListView = (ListView)v.findViewById(R.id.taskListView);
		TaskItemLab tIL = TaskItemLab.get(getActivity());
		ArrayList<TaskItem> taskItems = tIL.getTaskItems();
		TaskListAdapter tLAdapter = new TaskListAdapter(taskItems);
		mTaskListView.setAdapter(tLAdapter);
		return v;
	}
	
	private void doStart() {
		//get the task list repeat times
		int taskRepeatTimes = 1;
		try {
			taskRepeatTimes = Integer.parseInt(mTaskRepeatTimesEdit.getText().toString());
		} catch(NumberFormatException e) {
			Toast.makeText(getActivity(), R.string.toast_task_repeat_times_error, Toast.LENGTH_SHORT).show();
			return;
		}
		if (taskRepeatTimes <= 0) {
			Toast.makeText(getActivity(), R.string.toast_task_repeat_times_error, Toast.LENGTH_SHORT).show();
			return;
		}
		//write the task JSON file
		TaskItemLab tIL = TaskItemLab.get(getActivity());
		ArrayList<TaskItem> tIA = tIL.getTaskItems();
		if (tIA.size() == 0) {
			Toast.makeText(getActivity(), R.string.toast_tasklist_empty, Toast.LENGTH_SHORT).show();
			return;
		}
//		mJSON_TaskFileName = Environment.getExternalStorageDirectory().getPath()
//				+ "/" + getActivity().getString(R.string.JSON_FILEDIR_TASKLIST)
//				+ "/" + getActivity().getString(R.string.JSON_FILENAME_TASKLIST);
		JSONObject json;
		JSONArray taskListArray = new JSONArray();
		try {
			json = new JSONObject();
			json.put(JSON_TASKREPEATTIMES, Integer.toString(taskRepeatTimes));
			taskListArray.put(json);
			TaskItem ti;
			for (int i = 1; i <= tIA.size(); i++) {
				ti = tIA.get(i-1);
				json = new JSONObject();
				json.put(JSON_TASKCASENAME, ti.getTestCaseName());
				json.put(JSON_TASKCASEDELAYTIME, ti.getDelayTime());
				taskListArray.put(json);
			}
			boolean isWriteOK = Tools.writeJSONFile(taskListArray, mJSON_TaskFileName);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		//start a new thread to run task
		if (mThread == null) {
			mThread = new Thread(mRunnable);
			mThread.start();
		} else {
			Toast.makeText(getActivity(), R.string.toast_thread_started, Toast.LENGTH_SHORT).show();
		}
	}
	
	private class SelectedTestCaseAdapter extends ArrayAdapter<String> {
		
		public SelectedTestCaseAdapter(ArrayList<String> selectedTestCase) {
			super(getActivity(), 0, selectedTestCase);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem_selectedtestcase, null);
			}
			String str = getItem(position);
			switch(str) {
			case "TelCaseUI":
				str = "语音通话";
				break;
			case "SMSCaseUI":
				str = "发短信";
				break;
			case "SMSLCaseUI":
				str = "发长短信";
				break;
			case "WeiXinImageCase":
				str = "发送微信图片信息";
				break;
			case "WeiXinTextCase":
				str = "发送微信文本信息";
				break;
			}
			TextView testCaseName = (TextView)convertView.findViewById(R.id.testCaseNameTextView);
			testCaseName.setText(str);
			Button addButton = (Button)convertView.findViewById(R.id.addTaskButton);
			STCItemListener sTCItemListener = new STCItemListener(position);
			addButton.setOnClickListener(sTCItemListener);
			return convertView;
		}
		
	}
	
	private class STCItemListener implements OnClickListener {
		int mPosition;
		
		public STCItemListener(int inPosition) {
			mPosition = inPosition;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			View sTCItem = mSTCListView.getChildAt(mPosition);
			EditText delayTimeEdit = (EditText)sTCItem.findViewById(R.id.delayEditText);
			int delayTime = 0;
			try {
				delayTime = Integer.parseInt(delayTimeEdit.getText().toString());
			} catch(NumberFormatException e) {
				Toast.makeText(getActivity(), R.string.toast_delay_time_error, Toast.LENGTH_SHORT).show();
				return;
			}
			if (delayTime < 0) {
				Toast.makeText(getActivity(), R.string.toast_delay_time_error, Toast.LENGTH_SHORT).show();
				return;
			}
			//add the test case into task list
			ArrayList<TaskItem> taskItems = TaskItemLab.get(getActivity()).getTaskItems();
			String testCaseName = SelectedTestCase.get(getActivity()).getTestCaseList().get(mPosition);
			TaskItem tI = new TaskItem(testCaseName, Integer.toString(delayTime));
			taskItems.add(tI);
			TaskListAdapter tLAdapter = (TaskListAdapter) mTaskListView.getAdapter();
			tLAdapter.notifyDataSetChanged();
		}
				
	}
	
	private class TaskListAdapter extends ArrayAdapter<TaskItem> {
		
		public TaskListAdapter(ArrayList<TaskItem> taskItems) {
			super(getActivity(), 0, taskItems);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater().inflate(R.layout.listitem_task, null);
			}
			int p = position +1;
			TextView testCaseTitle = (TextView)convertView.findViewById(R.id.testCaseTitleTextView);
			testCaseTitle.setText(getActivity().getString(R.string.caption_testcase_title)
					+ " " + p + ":");
			TaskItem taskItem = getItem(position);
			String str = taskItem.getTestCaseName();
			switch(str){
			case "TelCaseUI":
				str = "语音通话";
				break;
			case "SMSCaseUI":
				str = "发短信";
				break;
			case "SMSLCaseUI":
				str = "发长短信";
				break;
			case "WeiXinImageCase":
				str = "发送微信图片信息";
				break;
			case "WeiXinTextCase":
				str = "发送微信文本信息";
				break;
			}
			TextView testCaseName = (TextView)convertView.findViewById(R.id.testCaseNameTextView);
			testCaseName.setText(str);
			TextView delayTime = (TextView)convertView.findViewById(R.id.delayTimeTextView);
			delayTime.setText(taskItem.getDelayTime());
			Button delButton = (Button)convertView.findViewById(R.id.delTaskButton);
			TaskListListener taskListListener = new TaskListListener(position);
			delButton.setOnClickListener(taskListListener);
			return convertView;
		}
		
	}
	
	private class TaskListListener implements OnClickListener {
		int mPosition;
		
		public TaskListListener(int inPosition) {
			mPosition = inPosition;
		}

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			ArrayList<TaskItem> taskItems = TaskItemLab.get(getActivity()).getTaskItems();
			taskItems.remove(mPosition);
			TaskListAdapter tLAdapter = (TaskListAdapter) mTaskListView.getAdapter();
			tLAdapter.notifyDataSetChanged();
		}
		
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		TaskListParaSave taskListPara = TaskListParaSave.get(getActivity());
		HashMap<String, String> taskListParaMap = taskListPara.getTaskListParaMap();
		taskListParaMap.put(JSON_TASKREPEATTIMES, mTaskRepeatTimesEdit.getText().toString());
		taskListPara.setTaskListParaMap(taskListParaMap);
	}
	
	private void doClear() {
		ArrayList<TaskItem> ti = TaskItemLab.get(getActivity()).getTaskItems();
		ti.removeAll(ti);
		TaskListAdapter tLAdapter = (TaskListAdapter)mTaskListView.getAdapter();
		tLAdapter.notifyDataSetChanged();
	}
	
	private class TaskHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what) {
			case MSG_RUNSUCESS:
				Toast.makeText(getActivity(), R.string.toast_task_run_ok, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			case MSG_RUNFAIL_1:
				Toast.makeText(getActivity(), R.string.toast_task_run_error_1, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			case MSG_RUNFAIL_2:
				Toast.makeText(getActivity(), R.string.toast_task_run_error_2, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			case MSG_RUNFAIL_3:
				Toast.makeText(getActivity(), R.string.toast_task_run_error_3, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			case MSG_RUNFAIL_4:
				Toast.makeText(getActivity(), R.string.toast_task_run_error_4, Toast.LENGTH_SHORT).show();
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
			case MSG_SETUPFILEEMPTY:
				Toast.makeText(getActivity(), R.string.toast_setup_file_empty, Toast.LENGTH_SHORT).show();
				mHandler.removeCallbacks(mRunnable);
				mThread = null;
				break;
			}
		}
		
	}
	
	private class TaskRunnable implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			//call task runner
			TaskRunner tRunner = new TaskRunner(getActivity(), mJSON_TaskFileName);
			int result = tRunner.run();
			switch(result) {
			case 0:
				mHandler.obtainMessage(MSG_RUNSUCESS).sendToTarget();
				break;
			case -1:
				mHandler.obtainMessage(MSG_RUNFAIL_1).sendToTarget();
				break;
			case -2:
				mHandler.obtainMessage(MSG_RUNFAIL_2).sendToTarget();
				break;
			case -3:
				mHandler.obtainMessage(MSG_RUNFAIL_3).sendToTarget();
				break;
			case -4:
				mHandler.obtainMessage(MSG_RUNFAIL_4).sendToTarget();
				break;
			case -5:
				mHandler.obtainMessage(MSG_NOSETUPFILE).sendToTarget();
				break;
			case -6:
				mHandler.obtainMessage(MSG_SETUPFILEERROR).sendToTarget();
				break;
			case -7:
				mHandler.obtainMessage(MSG_SETUPFILEEMPTY).sendToTarget();
				break;
			}
		}
		
	}

}

/*	Continuously run uiautomator test case, and delay between test cases.
//	//code1 tested ok!
//	cmd = new String[] {"su",
//			"uiautomator runtest /sdcard/testcase/testcase.jar -c com.testcase.TelCaseUI"
//			+ " -c com.testcase.WeiXinTextCase"
//	};
//	try {
//		Tools.execShellCMD(cmd, TAG);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		//e.printStackTrace();
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		//e.printStackTrace();
//	}
//	//code1 tested ok! end
//	//code2 tested ok!
//	cmd = new String[] {"su",
//			"uiautomator runtest /sdcard/testcase/testcase.jar -c com.testcase.TelCaseUI"
//	};
//	try {
//		Tools.execShellCMD(cmd, TAG);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		//e.printStackTrace();
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		//e.printStackTrace();
//	}
//	Tools.sleep(0);
//	cmd = new String[] {"su",
//			"uiautomator runtest /sdcard/testcase/testcase.jar -c com.testcase.WeiXinTextCase"
//	};
//	try {
//		Tools.execShellCMD(cmd, TAG);
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		//e.printStackTrace();
//	} catch (InterruptedException e) {
//		// TODO Auto-generated catch block
//		//e.printStackTrace();
//	}
//	//code2 tested ok! end
 */
