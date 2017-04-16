package com.cmlab.servicetest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SetUpServerFragment extends Fragment {
	public static final String TAG = "SetUpServer";
	private static final String JSON_SERVERURL = "ServerUrl";
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String DAYOFWEEK = "dayofweek";
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";
	private static final String SECOND = "second";
	
	private Button mSaveButton;
	private Button mPingButton;
	private EditText mServerUrlEdit;
	private TextView mPingResultTextView;
	private TextView mDisplayCurrentTimeText;
	private String mJSON_SETUP_FILENAME;
	private BroadcastReceiver mClockReceiver;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mJSON_SETUP_FILENAME = Environment.getExternalStorageDirectory().getPath()
				+ "/" + getActivity().getString(R.string.JSON_FILEDIR_SETUP)
				+ "/" + getActivity().getString(R.string.JSON_FILENAME_SETUP);
		//generate the clockreceiver to receive the broadcast time from clockservice
		mClockReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				int year = intent.getIntExtra(YEAR, 2015);
				int month = intent.getIntExtra(MONTH, 5);
				int day = intent.getIntExtra(DAY, 25);
				int dayofweek = intent.getIntExtra(DAYOFWEEK, 1);
				int hour = intent.getIntExtra(HOUR, 19);
				int minute = intent.getIntExtra(MINUTE, 12);
				int second =intent.getIntExtra(SECOND, 0);
				String str;
				switch(dayofweek) {
				case 1:
					str = "星期一";
					break;
				case 2:
					str = "星期二";
					break;
				case 3:
					str = "星期三";
					break;
				case 4:
					str = "星期四";
					break;
				case 5:
					str = "星期五";
					break;
				case 6:
					str = "星期六";
					break;
				default:
					str = "星期日";
				}
				String currentTime = year + "年" + month + "月" + day + "日 " + str
						+ " " + hour + ":" + minute + ":" + second;
				mDisplayCurrentTimeText.setText(currentTime);
			}
			
		};
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_serverurlsetup, container, false);
		mPingResultTextView = (TextView)v.findViewById(R.id.pingResultTextView);
		mDisplayCurrentTimeText = (TextView)v.findViewById(R.id.displayCurrentTimeTextView);
		mServerUrlEdit = (EditText)v.findViewById(R.id.serverUrlEdit);
		mSaveButton = (Button)v.findViewById(R.id.saveUrlButton);
		mPingButton = (Button)v.findViewById(R.id.pingButton);
		mSaveButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doSave();
			}
		});
		mPingButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				doPing();
			}
		});
		//get the server url from the setup.json file
		JSONArray array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
		JSONObject json;
		boolean isWriteOK;
		if (array == null) {
			//if setup.json file does not exist or is empty, then fill the edittext with default value
			mServerUrlEdit.setText(R.string.default_server_url);
			//create the setup.json file, and save the default server url
			try {
				json = new JSONObject();
				json.put(JSON_SERVERURL, getActivity().getString(R.string.default_server_url));
				array = new JSONArray();
				array.put(json);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
			isWriteOK = Tools.writeJSONFile(array, mJSON_SETUP_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
			}
		} else {
			//if setup.json file exists and is not empty, read the file and display the value
			try {
				json = array.getJSONObject(0);
				mServerUrlEdit.setText(json.getString(JSON_SERVERURL));
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				//if the key-values do not exist, use the default values and save them to file
				mServerUrlEdit.setText(R.string.default_server_url);
				try {
					json = array.getJSONObject(0);
					json.put(JSON_SERVERURL, getActivity().getString(R.string.default_server_url));
					array.put(0, json);
				} catch (JSONException ee) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				isWriteOK = Tools.writeJSONFile(array, mJSON_SETUP_FILENAME);
				if (!isWriteOK) {
					Toast.makeText(getActivity(), R.string.hint_update_jsonfile_error, Toast.LENGTH_SHORT).show();
				}
			}
		}
		return v;
	}
	
	private void doSave() {
		//check if the server url is empty
		if (mServerUrlEdit.getText().length() == 0) {
			Toast.makeText(getActivity(), R.string.toast_input_server_url, Toast.LENGTH_SHORT).show();
			return;
		}
		//write the server url into setup.json file
		JSONArray array;
		JSONObject json;
		boolean isWriteOK;
		try {
			array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
			json = array.getJSONObject(0);
			json.put(JSON_SERVERURL, mServerUrlEdit.getText().toString());
			array.put(0, json);
			isWriteOK = Tools.writeJSONFile(array, mJSON_SETUP_FILENAME);
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
	
	private void doPing() {
		String url = mServerUrlEdit.getText().toString();
		try {
			Process p = Runtime.getRuntime().exec("/system/bin/ping -c 4 " + url);
			int status = p.waitFor();
			if (status != 0) {
				mPingResultTextView.setText(getActivity().getString(R.string.ping_server_error1));
				p.destroy();
				return;
			}
			BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String output = new String();
	        String str = new String();
	        while((str=buf.readLine())!=null){
	            output = output + str + "\r\n";
	        }
	        if(output.length() == 0) {
	        	output = getActivity().getString(R.string.ping_server_error1);
	        }
	        mPingResultTextView.setText(output);
	        p.destroy();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			mPingResultTextView.setText("Ping command run error!");
		}
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter(ClockService.ACTION_UPDATE_UI_TIME);
		getActivity().registerReceiver(mClockReceiver, filter, ClockService.PERM_PRIVATE, null);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(mClockReceiver);
	}

}
