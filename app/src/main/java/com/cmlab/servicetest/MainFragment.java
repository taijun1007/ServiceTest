package com.cmlab.servicetest;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainFragment extends Fragment {
	public static final String TAG = "MainFragment";
	private static final String JSON_DIALNUMBER = "Call_Send_DestID";
	private static final String JSON_DIALDURATION = "Call_Send_HoldTime";
	private static final String JSON_DIALREPEATTIMES = "Call_Send_ShortRptTimes";
	private static final String JSON_DIALTASKCHECK = "DialTaskCheck";
	private static final String JSON_TASKREPEATTIMES = "TaskRepeatTimes";
	private static final String JSON_SMSNUMBER = "SMSNumber";
	private static final String JSON_SMSCONTENT = "SMSContent";
	private static final String JSON_SMSLONGCHECK = "SMSLongCheck";
	private static final String JSON_SMSREPEATTIMES = "SMSRepeatTimes";
	private static final String JSON_SMSTASKCHECK = "SMSTaskCheck";
	private static final String JSON_WEIXINUSERNAMETEXT = "WeiXin_Text_DestID";
	private static final String JSON_WEIXINUSERNAMEIMAGE = "WeiXin_Image_DestID";
	private static final String JSON_WEIXINIMAGEINDEX = "WeiXin_Image_Num";
	private static final String JSON_WEIXINIMAGEMSGREPEATTIMES = "WeiXin_Image_RptTimes";
	private static final String JSON_WEIXINIMAGETASKCHECK = "WeiXinImageTaskCheck";
	private static final String JSON_WEIXINTEXTMSGCONTENT = "WeiXin_Text_Content";
	private static final String JSON_WEIXINTEXTMSGREPEATTIMES = "WeiXin_Text_RptTimes";
	private static final String JSON_WEIXINTEXTTASKCHECK = "WeiXinTextTaskCheck";
	private static final String JSON_WEIXINTEXTLONGCHECK = "WeiXinTextLongCheck";
	private static final String JSON_PING_URL = "Ping_Send_DestIP";
	private static final String JSON_PING_COUNT = "Ping_Send_Packages";
	private static final String JSON_PINGTASKCHECK = "PingTaskCheck";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		//generate selected test case arraylist
		ArrayList<String> al = SelectedTestCase.get(getActivity()).getTestCaseList();
		al.removeAll(al);
		//generate task list items arraylist
		ArrayList<TaskItem> ti = TaskItemLab.get(getActivity()).getTaskItems();
		ti.removeAll(ti);
		//generate task list parameter save hashmap, and initiate the value
		TaskListParaSave taskListPara = TaskListParaSave.get(getActivity());
		HashMap<String, String> taskListParaMap = taskListPara.getTaskListParaMap();
		taskListParaMap.put(JSON_TASKREPEATTIMES, getActivity().getString(R.string.task_repeat_times));
		taskListPara.setTaskListParaMap(taskListParaMap);
		//generate xxx test case parameter save hashmap, and initiate the value
		//---call test case
		CallTestCaseParaSave callPara = CallTestCaseParaSave.get(getActivity());
		HashMap<String, String> callParaMap = callPara.getCallTestCaseParaMap();
		callParaMap.put(JSON_DIALNUMBER, getActivity().getString(R.string.default_dial_number));
		callParaMap.put(JSON_DIALDURATION, getActivity().getString(R.string.default_call_duration));
		callParaMap.put(JSON_DIALREPEATTIMES, getActivity().getString(R.string.default_repeat_times));
		callParaMap.put(JSON_DIALTASKCHECK, "false");
		callPara.setCallTestCaseParaMap(callParaMap);
		//---SMS test case
		SMSTestCaseParaSave smsPara = SMSTestCaseParaSave.get(getActivity());
		HashMap<String, String> smsParaMap = smsPara.getSMSTestCaseParaMap();
		smsParaMap.put(JSON_SMSNUMBER, getActivity().getString(R.string.default_phone_number));
		smsParaMap.put(JSON_SMSCONTENT, getActivity().getString(R.string.default_text_content));
		smsParaMap.put(JSON_SMSLONGCHECK, "false");
		smsParaMap.put(JSON_SMSREPEATTIMES, getActivity().getString(R.string.default_repeat_times));
		smsParaMap.put(JSON_SMSTASKCHECK, "false");
		smsPara.setSMSTestCaseParaMap(smsParaMap);
		//---wei xin image message test case
		WeiXinImageMsgTestCaseParaSave wxImagePara = WeiXinImageMsgTestCaseParaSave.get(getActivity());
		HashMap<String, String> wxImageParaMap = wxImagePara.getWeiXinImageMsgTestCaseParaMap();
		wxImageParaMap.put(JSON_WEIXINUSERNAMEIMAGE, getActivity().getString(R.string.default_weixin_username));
		wxImageParaMap.put(JSON_WEIXINIMAGEINDEX, getActivity().getString(R.string.default_image_index));
		wxImageParaMap.put(JSON_WEIXINIMAGEMSGREPEATTIMES, getActivity().getString(R.string.default_repeat_times));
		wxImageParaMap.put(JSON_WEIXINIMAGETASKCHECK, "false");
		wxImagePara.setWeiXinImageMsgTestCaseParaMap(wxImageParaMap);
		//---wei xin text message test case
		WeiXinTextMsgTestCaseParaSave wxTextPara = WeiXinTextMsgTestCaseParaSave.get(getActivity());
		HashMap<String, String> wxTextParaMap = wxTextPara.getWeiXinTextMsgTestCaseParaMap();
		wxTextParaMap.put(JSON_WEIXINUSERNAMETEXT, getActivity().getString(R.string.default_weixin_username));
		wxTextParaMap.put(JSON_WEIXINTEXTMSGCONTENT, getActivity().getString(R.string.default_text_content));
		wxTextParaMap.put(JSON_WEIXINTEXTLONGCHECK, "false");
		wxTextParaMap.put(JSON_WEIXINTEXTMSGREPEATTIMES, getActivity().getString(R.string.default_repeat_times));
		wxTextParaMap.put(JSON_WEIXINTEXTTASKCHECK, "false");
		//---ping test case
		PingTestCaseParaSave pingPara = PingTestCaseParaSave.get(getActivity());
		HashMap<String, String> pingParaMap = pingPara.getPingTestCaseParaMap();
		pingParaMap.put(JSON_PING_URL, getActivity().getString(R.string.default_ping_url));
		pingParaMap.put(JSON_PING_COUNT, getActivity().getString(R.string.default_ping_count));
		pingParaMap.put(JSON_PINGTASKCHECK, "false");
		//generate and initialize the flag, start ClockService
		Intent intent;
//		FlagLab.get(getActivity()).setClockServiceRun(false);
		if (ClockService.getServiceObj() == null) {
			intent = new Intent(getActivity(), ClockService.class);
			getActivity().startService(intent);
//			FlagLab.get(getActivity()).setClockServiceRun(true);
		}
		//start the ListenerService
		if (ListenerService.getServiceObj() == null) {
			intent = new Intent(getActivity(), ListenerService.class);
			getActivity().startService(intent);
		}
		try {
			//check if the dir(/sdcard/testcase/) exists, if not, then create it.
			File file = new File(Environment.getExternalStorageDirectory().getPath()
					+ "/" + getActivity().getString(R.string.JSON_FILEDIR_PARAMETER));
			if (!file.isDirectory()) {
				file.mkdirs();
			}
			//automatically get root authority
			Runtime.getRuntime().exec("su");
			Toast.makeText(getActivity(), R.string.toast_launch_info, Toast.LENGTH_SHORT).show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_main, container, false);
		GridView gv = (GridView)v.findViewById(R.id.MainGridView);
		//set up the adapter for the GridView
		ArrayList<GridItem> gridItems = MainGridItemLab.get(getActivity()).getGridItems();
		GridItemAdapter giAdapter = new GridItemAdapter(gridItems);
		gv.setAdapter(giAdapter);
		//add listener
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
//				Toast.makeText(getActivity(), " "+position, Toast.LENGTH_SHORT).show();
				Intent intent;
				switch(position) 
				{
				case 0:
					intent = new Intent(getActivity(), CallActivity.class);
					startActivity(intent);
					break;
				case 1:
					intent = new Intent(getActivity(), SMSActivity.class);
					startActivity(intent);
					break;
				case 2:
					intent = new Intent(getActivity(), PingActivity.class);
					startActivity(intent);
					break;
				case 3:
					intent = new Intent(getActivity(), AppsActivity.class);
					startActivity(intent);
					break;
				case 4:
					intent = new Intent(getActivity(), TaskListActivity.class);
					startActivity(intent);
					break;
				case 5: //manual get root authority
					try {
						Runtime.getRuntime().exec("su");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
					break;
				case 6:
					intent = new Intent(getActivity(), SetUpActivity.class);
					startActivity(intent);
					break;
				case 7:
					intent = new Intent(getActivity(), SetUpServerActivity.class);
					startActivity(intent);
					break;
				case 8:
					intent = new Intent(getActivity(), RegisterActivity.class);
					startActivity(intent);
					break;
				case 9:
					intent = new Intent(getActivity(), MapActivity.class);
					startActivity(intent);
					break;
				default:
					Toast.makeText(getActivity(), R.string.main_switch_default, Toast.LENGTH_SHORT).show();
				}
			}
		});
		return v;
	}
	
	private class GridItemAdapter extends ArrayAdapter<GridItem> {
		public GridItemAdapter(ArrayList<GridItem> gridItems) {
			super(getActivity(), 0, gridItems);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = getActivity().getLayoutInflater()
						.inflate(R.layout.gridview_item_applist, null);
			}
			GridItem gi = getItem(position);
			ImageView appImage = (ImageView)convertView.findViewById(R.id.appImage);
			appImage.setImageResource(gi.getIconId());
			TextView appTitle = (TextView)convertView.findViewById(R.id.appTitle);
			appTitle.setText(gi.getTitleId());
			return convertView;
		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//stop ClockService
//		if (FlagLab.get(getActivity()).isClockServiceRun() == true) {
			if (ClockService.getServiceObj() != null) {
				getActivity().stopService(new Intent(getActivity(), ClockService.class));
			}
//			FlagLab.get(getActivity()).setClockServiceRun(false);
//		}
		//stop ListenerService
		if (ListenerService.getServiceObj() != null) {
			getActivity().stopService(new Intent(getActivity(), ListenerService.class));
		}
	}

}
