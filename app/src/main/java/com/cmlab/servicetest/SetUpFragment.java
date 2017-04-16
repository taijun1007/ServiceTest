package com.cmlab.servicetest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetUpFragment extends Fragment {
	public static final String TAG = "SetUp";
	private static final String JSON_SDCARD_PATH = "SDCardPath";
	private static final String JSON_TESTCASE_PATH = "TestCasePath";
	private static final String JSON_TESTCASE_FILENAME = "TestCaseFileName";
	private static final String JSON_PARA_PATH = "ParaPath";
	private static final String JSON_PARA_FILENAME = "ParaFileName";
	private static final String JSON_TASK_PATH = "TaskPath";
	private static final String JSON_TASK_FILENAME = "TaskFileName";

	private String mJSON_FILENAME;
	private EditText mSDCardPathEdit;
	private EditText mTestCasePathEdit;
	private EditText mTestCaseFileNameEdit;
	private EditText mParaPathEdit;
	private EditText mParaFileNameEdit;
	private EditText mTaskPathEdit;
	private EditText mTaskFileNameEdit;
	private Button mSetUpSaveButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		//get the set up file (setup.json) path
		mJSON_FILENAME = Environment.getExternalStorageDirectory().getPath()
				+ "/" + getActivity().getString(R.string.JSON_FILEDIR_SETUP)
				+ "/" + getActivity().getString(R.string.JSON_FILENAME_SETUP);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_setup, container, false);
		mSDCardPathEdit = (EditText)v.findViewById(R.id.setUpSDCardPathEdit);
		mTestCasePathEdit = (EditText)v.findViewById(R.id.setUpTestCasePathEdit);
		mTestCaseFileNameEdit = (EditText)v.findViewById(R.id.setUpTestCaseFileNameEdit);
		mParaPathEdit = (EditText)v.findViewById(R.id.setUpParameterPathEdit);
		mParaFileNameEdit = (EditText)v.findViewById(R.id.setUpParameterFileNameEdit);
		mTaskPathEdit = (EditText)v.findViewById(R.id.setUpTaskPathEdit);
		mTaskFileNameEdit = (EditText)v.findViewById(R.id.setUpTaskFileNameEdit);
		mSetUpSaveButton = (Button)v.findViewById(R.id.setUpSaveButton);
		mSetUpSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doSave(v);
			}
		});
		//get the path and file name from the setup.json file
		JSONArray array = Tools.readJSONFile(mJSON_FILENAME);
		JSONObject json;
		boolean isWriteOK;
		if (array == null) {
			//if setup.json file does not exist or is empty, then fill the edittexts with default value
			mSDCardPathEdit.setText(R.string.JSON_SDCARD_PATH);
			mTestCasePathEdit.setText(R.string.JSON_FILEDIR_TESTCASE);
			mTestCaseFileNameEdit.setText(R.string.JSON_FILENAME_TESTCASE);
			mParaPathEdit.setText(R.string.JSON_FILEDIR_PARAMETER);
			mParaFileNameEdit.setText(R.string.JSON_FILENAME_PARAMETER);
			mTaskPathEdit.setText(R.string.JSON_FILEDIR_TASKLIST);
			mTaskFileNameEdit.setText(R.string.JSON_FILENAME_TASKLIST);
			//create the setup.json file, and save the default values
			try {
				json = new JSONObject();
				json.put(JSON_SDCARD_PATH, getActivity().getString(R.string.JSON_SDCARD_PATH));
				json.put(JSON_TESTCASE_PATH, getActivity().getString(R.string.JSON_FILEDIR_TESTCASE));
				json.put(JSON_TESTCASE_FILENAME, getActivity().getString(R.string.JSON_FILENAME_TESTCASE));
				json.put(JSON_PARA_PATH, getActivity().getString(R.string.JSON_FILEDIR_PARAMETER));
				json.put(JSON_PARA_FILENAME, getActivity().getString(R.string.JSON_FILENAME_PARAMETER));
				json.put(JSON_TASK_PATH, getActivity().getString(R.string.JSON_FILEDIR_TASKLIST));
				json.put(JSON_TASK_FILENAME, getActivity().getString(R.string.JSON_FILENAME_TASKLIST));
				array = new JSONArray();
				array.put(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			isWriteOK = Tools.writeJSONFile(array, mJSON_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
			}
		} else {
			//if setup.json file exists and is not empty, read the file and display the value
			try {
				json = array.getJSONObject(0);
				mSDCardPathEdit.setText(json.getString(JSON_SDCARD_PATH));
				mTestCasePathEdit.setText(json.getString(JSON_TESTCASE_PATH));
				mTestCaseFileNameEdit.setText(json.getString(JSON_TESTCASE_FILENAME));
				mParaPathEdit.setText(json.getString(JSON_PARA_PATH));
				mParaFileNameEdit.setText(json.getString(JSON_PARA_FILENAME));
				mTaskPathEdit.setText(json.getString(JSON_TASK_PATH));
				mTaskFileNameEdit.setText(json.getString(JSON_TASK_FILENAME));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//if the key-values do not exist, use the default values and save them to file
				mSDCardPathEdit.setText(R.string.JSON_SDCARD_PATH);
				mTestCasePathEdit.setText(R.string.JSON_FILEDIR_TESTCASE);
				mTestCaseFileNameEdit.setText(R.string.JSON_FILENAME_TESTCASE);
				mParaPathEdit.setText(R.string.JSON_FILEDIR_PARAMETER);
				mParaFileNameEdit.setText(R.string.JSON_FILENAME_PARAMETER);
				mTaskPathEdit.setText(R.string.JSON_FILEDIR_TASKLIST);
				mTaskFileNameEdit.setText(R.string.JSON_FILENAME_TASKLIST);
				try {
					json = array.getJSONObject(0);
					json.put(JSON_SDCARD_PATH, getActivity().getString(R.string.JSON_SDCARD_PATH));
					json.put(JSON_TESTCASE_PATH, getActivity().getString(R.string.JSON_FILEDIR_TESTCASE));
					json.put(JSON_TESTCASE_FILENAME, getActivity().getString(R.string.JSON_FILENAME_TESTCASE));
					json.put(JSON_PARA_PATH, getActivity().getString(R.string.JSON_FILEDIR_PARAMETER));
					json.put(JSON_PARA_FILENAME, getActivity().getString(R.string.JSON_FILENAME_PARAMETER));
					json.put(JSON_TASK_PATH, getActivity().getString(R.string.JSON_FILEDIR_TASKLIST));
					json.put(JSON_TASK_FILENAME, getActivity().getString(R.string.JSON_FILENAME_TASKLIST));
					array.put(0, json);
				} catch (JSONException ee) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				isWriteOK = Tools.writeJSONFile(array, mJSON_FILENAME);
				if (!isWriteOK) {
					Toast.makeText(getActivity(), R.string.hint_update_jsonfile_error, Toast.LENGTH_SHORT).show();
				}
			}
		}
		return v;
	}
	
	private void doSave(View view) {
		//check if the sdcard path is empty
		if (mSDCardPathEdit.getText().length() == 0) {
			Toast.makeText(getActivity(), R.string.toast_input_sdcard_path, Toast.LENGTH_SHORT).show();
			return;
		}
		//check if the testcase path is empty
		if (mTestCasePathEdit.getText().length() == 0) {
			Toast.makeText(getActivity(), R.string.toast_input_testcase_path, Toast.LENGTH_SHORT).show();
			return;
		}
		//check if the testcase file name is empty
		if (mTestCaseFileNameEdit.getText().length() == 0) {
			Toast.makeText(getActivity(), R.string.toast_input_testcase_filename, Toast.LENGTH_SHORT).show();
			return;
		}
		//check if the parameter path is empty
		if (mParaPathEdit.getText().length() == 0) {
			Toast.makeText(getActivity(), R.string.toast_input_para_path, Toast.LENGTH_SHORT).show();
			return;
		}
		//check if the parameter file name is empty
		if (mParaFileNameEdit.getText().length() == 0) {
			Toast.makeText(getActivity(), R.string.toast_input_para_filename, Toast.LENGTH_SHORT).show();
			return;
		}
		//check if the task path is empty
		if (mTaskPathEdit.getText().length() == 0) {
			Toast.makeText(getActivity(), R.string.toast_input_task_path, Toast.LENGTH_SHORT).show();
			return;
		}
		//check if the task file name is empty
		if (mTaskFileNameEdit.getText().length() == 0) {
			Toast.makeText(getActivity(), R.string.toast_input_task_filename, Toast.LENGTH_SHORT).show();
			return;
		}
		//write the path and file name into setup.json file
		JSONArray array;
		JSONObject json;
		boolean isWriteOK;
		try {
			array = Tools.readJSONFile(mJSON_FILENAME);
			json = array.getJSONObject(0);
			json.put(JSON_SDCARD_PATH, mSDCardPathEdit.getText().toString());
			json.put(JSON_TESTCASE_PATH, mTestCasePathEdit.getText().toString());
			json.put(JSON_TESTCASE_FILENAME, mTestCaseFileNameEdit.getText().toString());
			json.put(JSON_PARA_PATH, mParaPathEdit.getText().toString());
			json.put(JSON_PARA_FILENAME, mParaFileNameEdit.getText().toString());
			json.put(JSON_TASK_PATH, mTaskPathEdit.getText().toString());
			json.put(JSON_TASK_FILENAME, mTaskFileNameEdit.getText().toString());
			array.put(0, json);
			isWriteOK = Tools.writeJSONFile(array, mJSON_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getActivity(), R.string.toast_para_saved, Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

}
