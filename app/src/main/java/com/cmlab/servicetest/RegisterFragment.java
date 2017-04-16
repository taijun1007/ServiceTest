package com.cmlab.servicetest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.Fragment;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Time;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DigitalClock;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterFragment extends Fragment {
	public static final String TAG = "Register";
	private static final String JSON_SERVERURL = "ServerUrl";
	private static final String JSON_CPULOG_INTERVAL = "CpuLogInterval";
	private static final String JSON_MEMLOG_INTERVAL = "MemLogInterval";
	private static final String JSON_SIGNALLOG_INTERVAL = "SignalLogInterval";
	private static final String JSON_NETWORKLOG_INTERVAL = "NetworkLogInterval";
	private static final String JSON_POWERLOG_INTERVAL = "PowerLogInterval";
	private static final String JSON_APMODE_SWITCHTIME = "FlyMode_SwitchTime";
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String DAYOFWEEK = "dayofweek";
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";
	private static final String SECOND = "second";
	private static final String SIGNALLOGFILE = "/sdcard/testcase/signallog.txt";
	private static final String CPULOGFILE = "/sdcard/testcase/cpulog.txt";
	private static final String MEMLOGFILE = "/sdcard/testcase/memlog.txt";
	private static final String NETWORKLOGFILE = "/sdcard/testcase/networklog.txt";
	private static final String POWERLOGFILE = "/sdcard/testcase/powerlog.txt";
	private static final String DEVICEINFOFILE = "/sdcard/testcase/deviceinfo.txt";
	private static final int MSG_UPDATE_GPS = 1;
	private static final int MSG_STOP = 2;
	private static final String JSON_PARAFILE = "testcaseparafile";
	
	private TextView mRegisterIDText;
	private TextView mDeviceInfoText;
	private TextView mDisplayCurrentTimeText;
	private TextView mDisplayGPSTimeText;
	private TextView mDisplayGPSInfoText;
	private TextView mGPSInfoText;
	private Button mRegisterButton;
	private Chronometer mDeviceInfoWatch;
	private String mJSON_SETUP_FILENAME;
	private String mTXT_DEVICEID_FILENAME;
	private String mJSON_PARA_FILENAME = "/sdcard/testcase/testcaseparameter.json";
	private String mIMEI;
	private String mIMSI;
	private String mMODEL;
	private String mManufacturer;
	private String mIMEISV;
	private String mSerial;
	private String mMSISDN;
	private String mMCCMNC;
	private String mNWOPName;
	private String mSIMMCCMNC;
	private String mSIMOPName;
	private String mSIMSerial;
	private String gpsProvider;
	private String mRadioSignalStrength = "???";  //for GSM/CDMA/EVDO
	private String mLteSignalStrength = "???";   //for LTE
	private String mLteRsrp = "???";   //for LTE
	private String mLteRsrq = "???";   //for LTE
	private String mLteRssnr = "???";   //for LTE
	private String mLteCqi = "???";   //for LTE
	private String mPowerSource = "???";
	private boolean mIsRoaming;
	private int mCellID;
	private int mCi = 268435455;  //28 bit MAX value: 0xFFFFFFF
	private int mLtePci = -1;
	private int mNetworkType;
	private int mPhoneType;
	private int mDataState;
	private int mBatteryTemp = -1;
	private int mBatteryLevel = -1;
	private int mCpuLogInterval = 1; //default 1s
	private int mMemLogInterval = 10; //default 10s
	private int mSignalLogInterval = 60; //default 60s
	private int mNetworkLogInterval = 60; //default 60s
	private int mPowerLogInterval = 600; //default 600s
	private int mCpuCount;
	private int mMemCount;
	private int mSignalCount;
	private int mNetworkCount;
	private int mPowerCount;
	private int mCellInfoNum = 0; //the number of cell info returned from getAllCellInfo method
	private int mGsmCid = -1;
	private int mRegisteredCellNum = 0;
	private long mLastTotalCPUTime = 0;
	private long mNowTotalCPUTime = 0;
	private long mLastIdleCPUTime = 0;
	private long mNowIdleCPUTime = 0;
	private long mLastTotalCPU0Time = 0;
	private long mNowTotalCPU0Time = 0;
	private long mLastIdleCPU0Time = 0;
	private long mNowIdleCPU0Time = 0;
	private long mLastTotalCPU1Time = 0;
	private long mNowTotalCPU1Time = 0;
	private long mLastIdleCPU1Time = 0;
	private long mNowIdleCPU1Time = 0;
	private long mLastTotalCPU2Time = 0;
	private long mNowTotalCPU2Time = 0;
	private long mLastIdleCPU2Time = 0;
	private long mNowIdleCPU2Time = 0;
	private long mLastTotalCPU3Time = 0;
	private long mNowTotalCPU3Time = 0;
	private long mLastIdleCPU3Time = 0;
	private long mNowIdleCPU3Time = 0;
	private long mLastTotalCPU4Time = 0;
	private long mNowTotalCPU4Time = 0;
	private long mLastIdleCPU4Time = 0;
	private long mNowIdleCPU4Time = 0;
	private long mLastTotalCPU5Time = 0;
	private long mNowTotalCPU5Time = 0;
	private long mLastIdleCPU5Time = 0;
	private long mNowIdleCPU5Time = 0;
	private long mLastTotalCPU6Time = 0;
	private long mNowTotalCPU6Time = 0;
	private long mLastIdleCPU6Time = 0;
	private long mNowIdleCPU6Time = 0;
	private long mLastTotalCPU7Time = 0;
	private long mNowTotalCPU7Time = 0;
	private long mLastIdleCPU7Time = 0;
	private long mNowIdleCPU7Time = 0;
	private long mTotalMem = 0;
	private long mTotalMemFromMI = 0;
	private long mFreeMem = 0;
	private long mMemUsageRatio = 0;
	private float mBatteryVolt = -1;
	private float mCPUUsageRatio = 0;
	private float mCPU0UsageRatio = 0;
	private float mCPU1UsageRatio = 0;
	private float mCPU2UsageRatio = 0;
	private float mCPU3UsageRatio = 0;
	private float mCPU4UsageRatio = 0;
	private float mCPU5UsageRatio = 0;
	private float mCPU6UsageRatio = 0;
	private float mCPU7UsageRatio = 0;
	private BroadcastReceiver mClockReceiver;
	private LocationManager GPSManager;
	private GPSLocationListener gpslListener;
	private GPSStatusListener gpsStatusListener;
	private long mLastSetSystemClock = 0;
	private Location mLastLocation = null;
	private MyPhoneStateListener mMyPhoneStateListener;
	private BatteryInfoReceiver mBatteryInfoReceiver;
	
	private GPSUpdateHandler mGPSUpdateHandler = new GPSUpdateHandler();
	private GPSUpdateThread mThread;
	private Process mProcess;
	private Tools mTool;
	
	private long startAPMTime = 0;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		mJSON_SETUP_FILENAME = Environment.getExternalStorageDirectory().getPath()
				+ "/" + getActivity().getString(R.string.JSON_FILEDIR_SETUP)
				+ "/" + getActivity().getString(R.string.JSON_FILENAME_SETUP);
		mTXT_DEVICEID_FILENAME = Environment.getExternalStorageDirectory().getPath()
				+ "/" + getActivity().getString(R.string.TXT_FILEDIR_DEVICEID)
				+ "/" + getActivity().getString(R.string.TXT_FILENAME_DEVICEID);
		try {
			getDeviceInfo();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
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
		//get the LocationManager
		GPSManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		//check if GPS is enable
		if (GPSManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getActivity(), "GPS已开启!", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(getActivity(), "请开启GPS!", Toast.LENGTH_SHORT).show();
			//if GPS is off, open the system GPS setting UI
			Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
			startActivityForResult(intent, 0);  //for when GPS setting finished, return this app
		}
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedRequired(false);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		gpsProvider = GPSManager.getBestProvider(criteria, true);
		//GPSManager.setTestProviderEnabled(gpsProvider, true);  //open gps
		Log.i("gpsprovider", gpsProvider);
		Log.i("gpsproviderlm", LocationManager.GPS_PROVIDER);
		//register phone state listener
		TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		mMyPhoneStateListener = new MyPhoneStateListener();
		tm.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		//check and prepare the log files
		//---cpu log file
		ArrayList<String> als;
		String titleLine;
		als = Tools.readTXTFile(CPULOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms) DateTime CPU(%) CPU0 CPU1(%) CPU2(%) CPU3(%) CPU4(%) CPU5(%) CPU6(%) CPU7(%)";
			als = new ArrayList<String>();
			als.add(titleLine);
			Tools.writeTXTFile(als, CPULOGFILE);
		}
		//---memory log file
		als = Tools.readTXTFile(MEMLOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms) DateTime TotalMem(MB) FreeMem(MB) MemUsageRatio(%)";
			als = new ArrayList<String>();
			als.add(titleLine);
			Tools.writeTXTFile(als, MEMLOGFILE);
		}
		//---signal log file
		als = Tools.readTXTFile(SIGNALLOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms) DateTime GSM(dbm) LTE(dbm) LTE-RSRP LTE-RSRQ LTE-RSSNR LTE-CQI";
			als = new ArrayList<String>();
			als.add(titleLine);
			Tools.writeTXTFile(als, SIGNALLOGFILE);
		}
		//---network log file
		als = Tools.readTXTFile(NETWORKLOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms) DateTime MSISDN MCCMNC NWOPName CellID NetworkType PhoneType DataState Roaming";
			als = new ArrayList<String>();
			als.add(titleLine);
			Tools.writeTXTFile(als, NETWORKLOGFILE);
		}
		//---power log file
		als = Tools.readTXTFile(POWERLOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms) DateTime PowerSource BatteryLevel(%) BatteryVolt(V) BatteryTemp(C)";
			als = new ArrayList<String>();
			als.add(titleLine);
			Tools.writeTXTFile(als, POWERLOGFILE);
		}
		//---device info file
		titleLine = "Manufacturer Model AndroidVersion Serial IMEI IMEI/SV IMSI SIMMCCMNC SIMOPName SIMSerial";
		mMODEL = mMODEL.replaceAll(" ", "-");
		String contentLine = mManufacturer + " "
				+ mMODEL + " "
				+ Build.VERSION.RELEASE + " "
				+ mSerial + " "
				+ mIMEI + " "
				+ mIMEISV + " "
				+ mIMSI + " "
				+ mSIMMCCMNC + " "
				+ mSIMOPName + " "
				+ mSIMSerial;
		als = new ArrayList<String>();
		als.add(titleLine);
		als.add(contentLine);
		Tools.writeTXTFile(als, DEVICEINFOFILE);
		//read the log record interval from setup.json
		JSONArray array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
		if (array == null) {
			Toast.makeText(getActivity(), "setup.json文件不存在或为空！", Toast.LENGTH_SHORT).show();
		} else {
			try {
				JSONObject json = array.getJSONObject(0);
				mCpuLogInterval = json.getInt(JSON_CPULOG_INTERVAL);
				mMemLogInterval = json.getInt(JSON_MEMLOG_INTERVAL);
				mSignalLogInterval = json.getInt(JSON_SIGNALLOG_INTERVAL);
				mNetworkLogInterval = json.getInt(JSON_NETWORKLOG_INTERVAL);
				mPowerLogInterval = json.getInt(JSON_POWERLOG_INTERVAL);
				mJSON_PARA_FILENAME = json.getString(JSON_PARAFILE);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				Toast.makeText(getActivity(), "使用默认log采样间隔！", Toast.LENGTH_SHORT).show();
			}
		}
		//initial the log interval time counter
		mCpuCount = 0;
		mMemCount = 0;
		mSignalCount = 0;
		mNetworkCount = 0;
		mPowerCount = 0;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_register, container, false);
		mRegisterIDText = (TextView)v.findViewById(R.id.registerIDTextView);
		mDeviceInfoText = (TextView)v.findViewById(R.id.deviceInfoTextView);
		mDeviceInfoText.setMovementMethod(new ScrollingMovementMethod());
		mDisplayCurrentTimeText = (TextView)v.findViewById(R.id.displayCurrentTimeTextView);
		mGPSInfoText = (TextView)v.findViewById(R.id.gpsInfoTextView);
		mDisplayGPSTimeText = (TextView)v.findViewById(R.id.displayGpsTimeTextView);
		mDisplayGPSInfoText = (TextView)v.findViewById(R.id.displayGpsInfoTextView);
		mDisplayGPSInfoText.setMovementMethod(new ScrollingMovementMethod());
		mRegisterButton = (Button)v.findViewById(R.id.registerButton);
		mRegisterButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					doRegister();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		});
		//read deviceid.txt file to get the device ID and display it
		ArrayList<String> deviceId = Tools.readTXTFile(mTXT_DEVICEID_FILENAME);
		if (deviceId == null) {
			mRegisterIDText.setText(getActivity().getString(R.string.no_id));
		} else {
			//One device only has one device ID (only one line in deviceid.txt file)
			mRegisterIDText.setText(deviceId.get(0));
		}
		//show the device info
		showDeviceInfo();
		//set gps location listener
		Location location = GPSManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		showGPSInfo(location);  //show the last GPS information
		mLastLocation = location;
		gpslListener = new GPSLocationListener();
		//GPSManager.requestLocationUpdates(gpsProvider, 1000, 0, gpslListener);
		GPSManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpslListener);
		gpsStatusListener = new GPSStatusListener();
		GPSManager.addGpsStatusListener(gpsStatusListener);
		//use a chronometer timer to periodly update the device information
		mDeviceInfoWatch = (Chronometer)v.findViewById(R.id.chronometer1);
		mDeviceInfoWatch.setBase(System.currentTimeMillis());
		mDeviceInfoWatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
			
			@Override
			public void onChronometerTick(Chronometer chronometer) {
				// TODO Auto-generated method stub
				try {
					getDeviceInfo();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
				showDeviceInfo();
				writeLogFile();
			}
		});
		mDeviceInfoWatch.start();
		//SAMSUNG DO NOT NEED
//		mThread = new GPSUpdateThread();
//		mThread.start();
		//SAMSUNG DO NOT NEED end
		//test2 airplanemode
		@SuppressWarnings("deprecation")
		boolean isAirPlaneModeEnabled = Settings.System.getInt(getActivity().getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) == 1;
		if (isAirPlaneModeEnabled) {
			mRegisterButton.setText("飞行模式开启");
		} else {
			mRegisterButton.setText("飞行模式关闭");
		}
		//test2 airplanemode end
		return v;
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) 
	private void doRegister() throws JSONException {
//		//read setup.json file to get server url
//		JSONArray array = Tools.readJSONFile(mJSON_SETUP_FILENAME);
//		String url = null;
//		try {
//			if (array == null) {
//				Toast.makeText(getActivity(), R.string.toast_setup_file_empty, Toast.LENGTH_SHORT).show();
//				return;
//			} else {
//				JSONObject json = array.getJSONObject(0);
//				url = json.getString(JSON_SERVERURL);
//			}
//		} catch (JSONException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			Toast.makeText(getActivity(), R.string.toast_read_server_url_fail, Toast.LENGTH_SHORT).show();
//			return;
//		}
//		//get server url successfully, then ...
		//open or close airplane mode
//		boolean isEnabled = Settings.Global.getInt(getActivity().getContentResolver(),
//				Settings.Global.AIRPLANE_MODE_ON, 0) == 1;
//		Settings.Global.putInt(getActivity().getContentResolver(),
//				Settings.Global.AIRPLANE_MODE_ON, isEnabled?0:1);
//		Intent i=new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);  
//		i.putExtra("state", !isEnabled);  
//		getActivity().sendBroadcast(i);
		//test1 airplanemode
		JSONObject json = new JSONObject();
		boolean isWriteOK;
		JSONArray apmArray = Tools.readJSONFile(mJSON_PARA_FILENAME);
		if (apmArray == null) {
			json.put(JSON_APMODE_SWITCHTIME, System.currentTimeMillis()+20000);  //switch APM 20 seconds from press the button
			apmArray = new JSONArray();
			apmArray.put(json);
			isWriteOK = Tools.writeJSONFile(apmArray, mJSON_PARA_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_write_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		} else {
			json = apmArray.getJSONObject(0);
			json.put(JSON_APMODE_SWITCHTIME, System.currentTimeMillis()+20000);  //switch APM 20 seconds from press the button
			apmArray.put(0, json);
			isWriteOK = Tools.writeJSONFile(apmArray, mJSON_PARA_FILENAME);
			if (!isWriteOK) {
				Toast.makeText(getActivity(), R.string.hint_update_jsonfile_error, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		if (mAPMThread == null) {
			mAPMThread = new APMThread();
			mAPMThread.start();
			mRegisterButton.setEnabled(false);
			startAPMTime = System.currentTimeMillis();
		}
		//test1 airplanemode end
	}
	
	private APMThread mAPMThread;
	
	private class APMThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String[] cmd = new String[] {//"su",
					"uiautomator runtest /sdcard/testcase/testcase.jar -c com.testcase.AirPlaneMode"
			};
			try {
				mTool = new Tools();
				mProcess = mTool.getProcess();
				mTool.execShellCMD(cmd, TAG);
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				mProcess.destroy();
				mTool.killProcess("uiautomator");
				mProcess = null;
				mTool = null;
				//return;
			}
			mProcess = null;
			mTool = null;
			mAPMHandler.obtainMessage(0).sendToTarget();
		}
		
	}
	
	private APMHandler mAPMHandler = new APMHandler();
	
	private class APMHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch(msg.what) {
			case 0:
				Toast.makeText(getActivity(), "飞行模式设置成功！", Toast.LENGTH_SHORT).show();
				boolean isAPMEnabled = Settings.System.getInt(getActivity().getContentResolver(),
						Settings.System.AIRPLANE_MODE_ON, 0) == 1;
				if (isAPMEnabled) {
					mRegisterButton.setText("飞行模式开启");
				} else {
					mRegisterButton.setText("飞行模式关闭");
				}
				mRegisterButton.setEnabled(true);
				mAPMThread = null;
				long duration = System.currentTimeMillis()-startAPMTime;
				mRegisterButton.setText(mRegisterButton.getText() + String.valueOf(duration/1000) + "秒");
				break;
			}
		}
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter(ClockService.ACTION_UPDATE_UI_TIME);
		getActivity().registerReceiver(mClockReceiver, filter, ClockService.PERM_PRIVATE, null);
		//register battery state receiver
		mBatteryInfoReceiver = new BatteryInfoReceiver();
		getActivity().registerReceiver(mBatteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getActivity().unregisterReceiver(mClockReceiver);
		//unregister battery state receiver
		if (mBatteryInfoReceiver != null) {
			getActivity().unregisterReceiver(mBatteryInfoReceiver);
			mBatteryInfoReceiver = null;
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		//SAMSUNG DO NOT NEED
//		mThread.handler.obtainMessage(MSG_STOP).sendToTarget();
//		try {
//			Thread.currentThread().sleep(100);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
//		mThread = null;
		//SAMSUNG DO NOT NEED END
		GPSManager.removeUpdates(gpslListener);  //remove listener
		GPSManager.removeGpsStatusListener(gpsStatusListener);
		//GPSManager.setTestProviderEnabled(gpsProvider, false);  //close gps
		NotificationManager notificationManager = (NotificationManager)
				getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(3721);
		//unregister phone state listener
		if (mMyPhoneStateListener != null) {
			TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
			tm.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
			tm = null;
			mMyPhoneStateListener = null;
		}
		super.onDestroy();
	}

	private class GPSLocationListener implements LocationListener {

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub
			Log.i("gps", "location changed");
			showGPSInfo(location);  //show GPS information
			setSystemClock(location);  //set the system clock
		}

		@SuppressWarnings("deprecation")
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
			Log.i("gps", "status changed");
			Intent intent = new Intent(getActivity(), RegisterActivity.class);
			PendingIntent pi = PendingIntent.getActivity(getActivity(), 3721, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			String tickerText = "GPS";
			String nTitle = "测试吧提醒您";
			String nMSG;
			Notification notification;
			NotificationManager notificationManager = (NotificationManager)
					getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			switch(status) {
			case LocationProvider.AVAILABLE:
				notification = new Notification(
						android.R.drawable.ic_menu_mylocation, tickerText, System.currentTimeMillis()
						);
				notification.flags |= Notification.FLAG_ONGOING_EVENT;
				nMSG = "GPS已定位";
				notification.setLatestEventInfo(getActivity(), nTitle, nMSG, pi);
				notificationManager.notify(3721, notification);
				break;
			case LocationProvider.OUT_OF_SERVICE:
				notification = new Notification(
						android.R.drawable.ic_delete, tickerText, System.currentTimeMillis()
						);
				notification.flags |= Notification.FLAG_ONGOING_EVENT;
				nMSG = "GPS无服务";
				notification.setLatestEventInfo(getActivity(), nTitle, nMSG, pi);
				notificationManager.notify(3721, notification);
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				notification = new Notification(
						android.R.drawable.ic_delete, tickerText, System.currentTimeMillis()
						);
				notification.flags |= Notification.FLAG_ONGOING_EVENT;
				nMSG = "GPS暂时不可用";
				notification.setLatestEventInfo(getActivity(), nTitle, nMSG, pi);
				notificationManager.notify(3721, notification);
				break;
			default:
				
			}
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
			Log.i("gps", "provider enabled");
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			Log.i("gps", "provider disabled");
		}
		
	}
	
	private void showGPSInfo(Location location) {
		if (location != null) {
			//show gps information
			String gpsInfo = "精度：" + location.getAccuracy() + "\r\n"
					+ "高度/海拔：" + "\r\n" + String.format("%.10f", location.getAltitude()) + "\r\n"
					+ "导向：" + String.format("%.1f", location.getBearing()) + "\r\n"
					+ "速度：" + location.getSpeed() + "\r\n"
					+ "纬度：" + "\r\n" + String.format("%.10f",  location.getLatitude()) + "\r\n"
					+ "经度：" + "\r\n" + String.format("%.10f",  location.getLongitude()) + "\r\n"
					+ "时间戳：" + "\r\n" + location.getTime();
			mDisplayGPSInfoText.setText(gpsInfo);
			//show gps time
			SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
			Date date = new Date(location.getTime());
			String gpsTime = df.format(date);
			mDisplayGPSTimeText.setText(gpsTime);
		} else {
			mDisplayGPSInfoText.setText("暂无GPS信息");
			mDisplayGPSTimeText.setText("");
		}
	}
	
	private void setSystemClock (Location location) {
		if (location != null) {
			if (mLastLocation == null) {
//				String deviceModel = Tools.getDeviceModel();
//				String deviceManufacturer = Tools.getDeviceManufacturer();
//				if (deviceManufacturer.contains("samsung") && deviceModel.contains("GT-I950")) {
//					//for SAMSUNG S4 (Android 4.2.2)
//					String[] cmd = new String[] {//"su",
//							"chmod 666 /dev/alarm"
//					};
//					try {
//						Tools.execShellCMD(cmd, TAG);
//					} catch (IOException | InterruptedException e) {
//						// TODO Auto-generated catch block
//						//e.printStackTrace();
//					}
//					SystemClock.setCurrentTimeMillis(location.getTime());
//					Toast.makeText(getActivity(), "SAMSUNG S4", Toast.LENGTH_SHORT).show();
//				} else {
					//for other devices (>=Android 4.3)
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.HHmmss");
					Date date = new Date(location.getTime());
					String setTime = df.format(date);
					String[] cmd = new String[] {//"su",
							"date -s " + setTime
					};
					try {
						mTool = new Tools();
						mProcess = mTool.getProcess();
						mTool.execShellCMD(cmd, TAG);
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
						mProcess.destroy();
						mTool.killProcess("uiautomator");
						mProcess = null;
						mTool = null;
						Toast.makeText(getActivity(), "SET TIME WRONG", Toast.LENGTH_SHORT).show();
					}
					mProcess = null;
					mTool = null;
//					mRegisterButton.setText(setTime);//test
//				}
				mLastSetSystemClock = location.getTime();
				mLastLocation = location;
			} else {
				if (mLastLocation.getTime() != location.getTime()) {
					if (((location.getTime() - mLastSetSystemClock) >= 86400000)
							|| (Math.abs(location.getTime()-System.currentTimeMillis()) > 1000)) {
						//86400000 milliseconds = 1 day
						//adjust the system clock 1 time each day
//						String[] cmd = new String[] {//"su",
//								"chmod 666 /dev/alarm"
//						};
//						try {
//							Tools.execShellCMD(cmd, TAG);
//						} catch (IOException | InterruptedException e) {
//							// TODO Auto-generated catch block
//							//e.printStackTrace();
//						}
//						SystemClock.setCurrentTimeMillis(location.getTime());
						SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd.HHmmss");
						Date date = new Date(location.getTime());
						String setTime = df.format(date);
						String[] cmd = new String[] {//"su",
								"date -s " + setTime
						};
						try {
							mTool = new Tools();
							mProcess = mTool.getProcess();
							mTool.execShellCMD(cmd, TAG);
						} catch (IOException | InterruptedException e) {
							// TODO Auto-generated catch block
							//e.printStackTrace();
							mProcess.destroy();
							mTool.killProcess("uiautomator");
							mProcess = null;
							mTool = null;
							Toast.makeText(getActivity(), "SET TIME WRONG", Toast.LENGTH_SHORT).show();
						}
						mProcess = null;
						mTool = null;
						mLastSetSystemClock = location.getTime();
						mLastLocation = location;
					}
				}
			}
		}
	}
	
	private class GPSStatusListener implements GpsStatus.Listener {

		@Override
		public void onGpsStatusChanged(int event) {
			// TODO Auto-generated method stub
			GpsStatus status = GPSManager.getGpsStatus(null);
			if (status == null) {
				mGPSInfoText.setText(getActivity().getString(R.string.caption_gps_info)
						+ "卫星数：0");
			} else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS){
				int maxSatellites = status.getMaxSatellites();
				Iterator<GpsSatellite> iters = status.getSatellites().iterator();
				int count = 0;
				while(iters.hasNext() && count <= maxSatellites) {
					GpsSatellite s = iters.next();
					count++;
				}
				mGPSInfoText.setText(getActivity().getString(R.string.caption_gps_info)
						+ "卫星数：" + count);
			}
		}
		
	}
	
	private class GPSUpdateThread extends Thread {
		private boolean isStop = false;
		public Handler handler  = new Handler() {
			
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				if (msg.what == MSG_STOP) {
					isStop = true;
				}
			}
		};
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while (!isStop) {
				try {
					mGPSUpdateHandler.obtainMessage(MSG_UPDATE_GPS).sendToTarget();
					for(int i = 0; i < 50; i++) {
						if (!isStop) {
							Thread.currentThread().sleep(100);
						} else {
							break;
						}
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		
	}
	
	private class GPSUpdateHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if (msg.what == MSG_UPDATE_GPS) {
				Location location = GPSManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				showGPSInfo(location);  //show GPS information
				setSystemClock(location);  //set the system clock
			}
		}
		
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) 
	private void getDeviceInfo() throws IOException {
		//get the device infomation
		TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation gcl = (GsmCellLocation)tm.getCellLocation();
		//get phone IMEI
		mIMEI = tm.getDeviceId();  //SAMSUNG NOTE 2 OK! HUAWEI MATE 2 FAIL!
		//mIMEI = android.os.SystemProperties.get("gsm.imei");
		//get phone IMSI
		mIMSI = tm.getSubscriberId();  //SAMSUNG NOTE 2 OK! HUAWEI MATE 2 FAIL!
		//mIMSI = android.os.SystemProperties.get("gsm.sim.imsi");
		//get phone model
		mMODEL = Tools.getDeviceModel();
		//get phone manufacture
		mManufacturer = Tools.getDeviceManufacturer();
		//get phone IMEI SV
		mIMEISV = tm.getDeviceSoftwareVersion();
		//get phone serial
		mSerial = Tools.getDeviceSerial();
		//get phone MSISDN
		mMSISDN = tm.getLine1Number();
		//get phone MCC+MNC
		mMCCMNC = tm.getNetworkOperator();
		//get phone Network Operator Name
		mNWOPName = tm.getNetworkOperatorName();
		//get cell id
		if (gcl != null) {
			mCellID = gcl.getCid();
		} else {
			mCellID = -1;
		}
		//get SIM MCC+MNC
		mSIMMCCMNC = tm.getSimOperator();
		//get SIM operator name
		mSIMOPName = tm.getSimOperatorName();
		//get SIM serial
		mSIMSerial = tm.getSimSerialNumber();
		//get phone network roaming state
		mIsRoaming = tm.isNetworkRoaming();
		//get network type
		mNetworkType = tm.getNetworkType();
		//get phone type
		mPhoneType = tm.getPhoneType();
		//get data connection state
		mDataState = tm.getDataState();
		//-----------------------------------------start
		//get LTE PCI
		mRegisteredCellNum = 0;
		List<CellInfo> ciList = tm.getAllCellInfo();
		if (ciList != null) {
			CellInfo cellInfo;
			mCellInfoNum = ciList.size();
			for (int i = 0; i <= ciList.size()-1; i++) {
				cellInfo = ciList.get(i);
				if (cellInfo.isRegistered()) {
					mRegisteredCellNum++;
					try {
						//try parse cellInfo to CellInfoLte
						CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
						CellIdentityLte cellIdentityLte = cellInfoLte.getCellIdentity();
						mCi = cellIdentityLte.getCi();
						mLtePci = cellIdentityLte.getPci();
					} catch(Exception e1) {
						try {
							//try parse cellInfo to CellInfoGsm
							CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
							CellIdentityGsm cellIdentityGsm = cellInfoGsm.getCellIdentity();
							mGsmCid = cellIdentityGsm.getCid();
						} catch(Exception e2) {
							//TODO
						}
					}
				}
			}
		}
		//-----------------------------------------end
		//read the CPU usage time from the file: /proc/stat
		File file = new File("/proc/stat");
		FileReader fr = new FileReader(file);
		BufferedReader reader = new BufferedReader(fr);
		String[] sa = new String[9];
		for (int i = 0; i < 9; i++) {
			sa[i] = reader.readLine();
		}
		reader.close();
		String[] cpuInfo;
		String[] cpuInfo0;
		String[] cpuInfo1;
		String[] cpuInfo2;
		String[] cpuInfo3;
		String[] cpuInfo4;
		String[] cpuInfo5;
		String[] cpuInfo6;
		String[] cpuInfo7;
		cpuInfo = sa[0].split("\\s+");
		cpuInfo0 = sa[1].split("\\s+");
		cpuInfo1 = sa[2].split("\\s+");
		cpuInfo2 = sa[3].split("\\s+");
		cpuInfo3 = sa[4].split("\\s+");
		cpuInfo4 = sa[5].split("\\s+");
		cpuInfo5 = sa[6].split("\\s+");
		cpuInfo6 = sa[7].split("\\s+");
		cpuInfo7 = sa[8].split("\\s+");
		//parse total CPU usage ratio
		if ((cpuInfo[0].contains("cpu")) && (cpuInfo[0].length() <= 4)) {
			mNowTotalCPUTime = Long.parseLong(cpuInfo[1]) + Long.parseLong(cpuInfo[2])
					+ Long.parseLong(cpuInfo[3]) + Long.parseLong(cpuInfo[4])
					+ Long.parseLong(cpuInfo[5]) + Long.parseLong(cpuInfo[6])
					+ Long.parseLong(cpuInfo[7]);
			mNowIdleCPUTime = Long.parseLong(cpuInfo[4]); //cpu idle time
			mCPUUsageRatio = (((float)(mNowTotalCPUTime -mLastTotalCPUTime) - 
					(float)(mNowIdleCPUTime -mLastIdleCPUTime))*100)
					/ (float)(mNowTotalCPUTime - mLastIdleCPUTime);
			mLastTotalCPUTime = mNowTotalCPUTime;
			mLastIdleCPUTime = mNowIdleCPUTime;
		}
		//parse CPU0 usage ratio
		if ((cpuInfo0[0].contains("cpu")) && (cpuInfo0[0].length() <= 4)) {
			mNowTotalCPU0Time = Long.parseLong(cpuInfo0[1]) + Long.parseLong(cpuInfo0[2])
					+ Long.parseLong(cpuInfo0[3]) + Long.parseLong(cpuInfo0[4])
					+ Long.parseLong(cpuInfo0[5]) + Long.parseLong(cpuInfo0[6])
					+ Long.parseLong(cpuInfo0[7]);
			mNowIdleCPU0Time = Long.parseLong(cpuInfo0[4]); //cpu idle time
			mCPU0UsageRatio = (((float)(mNowTotalCPU0Time -mLastTotalCPU0Time) - 
					(float)(mNowIdleCPU0Time -mLastIdleCPU0Time))*100)
					/ (float)(mNowTotalCPU0Time - mLastIdleCPU0Time);
			mLastTotalCPU0Time = mNowTotalCPU0Time;
			mLastIdleCPU0Time = mNowIdleCPU0Time;
		} else {
			mNowTotalCPU0Time = 0;
			mNowIdleCPU0Time = 0;
			mLastTotalCPU0Time = 0;
			mLastIdleCPU0Time = 0;
			mCPU0UsageRatio = 0;
		}
		//parse CPU1 usage ratio
		if ((cpuInfo1[0].contains("cpu")) && (cpuInfo1[0].length() <= 4)) {
			mNowTotalCPU1Time = Long.parseLong(cpuInfo1[1]) + Long.parseLong(cpuInfo1[2])
					+ Long.parseLong(cpuInfo1[3]) + Long.parseLong(cpuInfo1[4])
					+ Long.parseLong(cpuInfo1[5]) + Long.parseLong(cpuInfo1[6])
					+ Long.parseLong(cpuInfo1[7]);
			mNowIdleCPU1Time = Long.parseLong(cpuInfo1[4]); //cpu idle time
			mCPU1UsageRatio = (((float)(mNowTotalCPU1Time -mLastTotalCPU1Time) - 
					(float)(mNowIdleCPU1Time -mLastIdleCPU1Time))*100)
					/ (float)(mNowTotalCPU1Time - mLastIdleCPU1Time);
			mLastTotalCPU1Time = mNowTotalCPU1Time;
			mLastIdleCPU1Time = mNowIdleCPU1Time;
		} else {
			mNowTotalCPU1Time = 0;
			mNowIdleCPU1Time = 0;
			mLastTotalCPU1Time = 0;
			mLastIdleCPU1Time = 0;
			mCPU1UsageRatio = 0;
		}
		//parse CPU2 usage ratio
		if ((cpuInfo2[0].contains("cpu")) && (cpuInfo2[0].length() <= 4)) {
			mNowTotalCPU2Time = Long.parseLong(cpuInfo2[1]) + Long.parseLong(cpuInfo2[2])
					+ Long.parseLong(cpuInfo2[3]) + Long.parseLong(cpuInfo2[4])
					+ Long.parseLong(cpuInfo2[5]) + Long.parseLong(cpuInfo2[6])
					+ Long.parseLong(cpuInfo2[7]);
			mNowIdleCPU2Time = Long.parseLong(cpuInfo2[4]); //cpu idle time
			mCPU2UsageRatio = (((float)(mNowTotalCPU2Time -mLastTotalCPU2Time) - 
					(float)(mNowIdleCPU2Time -mLastIdleCPU2Time))*100)
					/ (float)(mNowTotalCPU2Time - mLastIdleCPU2Time);
			mLastTotalCPU2Time = mNowTotalCPU2Time;
			mLastIdleCPU2Time = mNowIdleCPU2Time;
		} else {
			mNowTotalCPU2Time = 0;
			mNowIdleCPU2Time = 0;
			mLastTotalCPU2Time = 0;
			mLastIdleCPU2Time = 0;
			mCPU2UsageRatio = 0;
		}
		//parse CPU3 usage ratio
		if ((cpuInfo3[0].contains("cpu")) && (cpuInfo3[0].length() <= 4)) {
			mNowTotalCPU3Time = Long.parseLong(cpuInfo3[1]) + Long.parseLong(cpuInfo3[2])
					+ Long.parseLong(cpuInfo3[3]) + Long.parseLong(cpuInfo3[4])
					+ Long.parseLong(cpuInfo3[5]) + Long.parseLong(cpuInfo3[6])
					+ Long.parseLong(cpuInfo3[7]);
			mNowIdleCPU3Time = Long.parseLong(cpuInfo3[4]); //cpu idle time
			mCPU3UsageRatio = (((float)(mNowTotalCPU3Time -mLastTotalCPU3Time) - 
					(float)(mNowIdleCPU3Time -mLastIdleCPU3Time))*100)
					/ (float)(mNowTotalCPU3Time - mLastIdleCPU3Time);
			mLastTotalCPU3Time = mNowTotalCPU3Time;
			mLastIdleCPU3Time = mNowIdleCPU3Time;
		} else {
			mNowTotalCPU3Time = 0;
			mNowIdleCPU3Time = 0;
			mLastTotalCPU3Time = 0;
			mLastIdleCPU3Time = 0;
			mCPU3UsageRatio = 0;
		}
		//parse CPU4 usage ratio
		if ((cpuInfo4[0].contains("cpu")) && (cpuInfo4[0].length() <= 4)) {
			mNowTotalCPU4Time = Long.parseLong(cpuInfo4[1]) + Long.parseLong(cpuInfo4[2])
					+ Long.parseLong(cpuInfo4[3]) + Long.parseLong(cpuInfo4[4])
					+ Long.parseLong(cpuInfo4[5]) + Long.parseLong(cpuInfo4[6])
					+ Long.parseLong(cpuInfo4[7]);
			mNowIdleCPU4Time = Long.parseLong(cpuInfo4[4]); //cpu idle time
			mCPU4UsageRatio = (((float)(mNowTotalCPU4Time -mLastTotalCPU4Time) - 
					(float)(mNowIdleCPU4Time -mLastIdleCPU4Time))*100)
					/ (float)(mNowTotalCPU4Time - mLastIdleCPU4Time);
			mLastTotalCPU4Time = mNowTotalCPU4Time;
			mLastIdleCPU4Time = mNowIdleCPU4Time;
		} else {
			mNowTotalCPU4Time = 0;
			mNowIdleCPU4Time = 0;
			mLastTotalCPU4Time = 0;
			mLastIdleCPU4Time = 0;
			mCPU4UsageRatio = 0;
		}
		//parse CPU5 usage ratio
		if ((cpuInfo5[0].contains("cpu")) && (cpuInfo5[0].length() <= 4)) {
			mNowTotalCPU5Time = Long.parseLong(cpuInfo5[1]) + Long.parseLong(cpuInfo5[2])
					+ Long.parseLong(cpuInfo5[3]) + Long.parseLong(cpuInfo5[4])
					+ Long.parseLong(cpuInfo5[5]) + Long.parseLong(cpuInfo5[6])
					+ Long.parseLong(cpuInfo5[7]);
			mNowIdleCPU5Time = Long.parseLong(cpuInfo5[4]); //cpu idle time
			mCPU5UsageRatio = (((float)(mNowTotalCPU5Time -mLastTotalCPU5Time) - 
					(float)(mNowIdleCPU5Time -mLastIdleCPU5Time))*100)
					/ (float)(mNowTotalCPU5Time - mLastIdleCPU5Time);
			mLastTotalCPU5Time = mNowTotalCPU5Time;
			mLastIdleCPU5Time = mNowIdleCPU5Time;
		} else {
			mNowTotalCPU5Time = 0;
			mNowIdleCPU5Time = 0;
			mLastTotalCPU5Time = 0;
			mLastIdleCPU5Time = 0;
			mCPU5UsageRatio = 0;
		}
		//parse CPU6 usage ratio
		if ((cpuInfo6[0].contains("cpu")) && (cpuInfo6[0].length() <= 4)) {
			mNowTotalCPU6Time = Long.parseLong(cpuInfo6[1]) + Long.parseLong(cpuInfo6[2])
					+ Long.parseLong(cpuInfo6[3]) + Long.parseLong(cpuInfo6[4])
					+ Long.parseLong(cpuInfo6[5]) + Long.parseLong(cpuInfo6[6])
					+ Long.parseLong(cpuInfo6[7]);
			mNowIdleCPU6Time = Long.parseLong(cpuInfo6[4]); //cpu idle time
			mCPU6UsageRatio = (((float)(mNowTotalCPU6Time -mLastTotalCPU6Time) - 
					(float)(mNowIdleCPU6Time -mLastIdleCPU6Time))*100)
					/ (float)(mNowTotalCPU6Time - mLastIdleCPU6Time);
			mLastTotalCPU6Time = mNowTotalCPU6Time;
			mLastIdleCPU6Time = mNowIdleCPU6Time;
		} else {
			mNowTotalCPU6Time = 0;
			mNowIdleCPU6Time = 0;
			mLastTotalCPU6Time = 0;
			mLastIdleCPU6Time = 0;
			mCPU6UsageRatio = 0;
		}
		//parse CPU7 usage ratio
		if ((cpuInfo7[0].contains("cpu")) && (cpuInfo7[0].length() <= 4)) {
			mNowTotalCPU7Time = Long.parseLong(cpuInfo7[1]) + Long.parseLong(cpuInfo7[2])
					+ Long.parseLong(cpuInfo7[3]) + Long.parseLong(cpuInfo7[4])
					+ Long.parseLong(cpuInfo7[5]) + Long.parseLong(cpuInfo7[6])
					+ Long.parseLong(cpuInfo7[7]);
			mNowIdleCPU7Time = Long.parseLong(cpuInfo7[4]); //cpu idle time
			mCPU7UsageRatio = (((float)(mNowTotalCPU7Time -mLastTotalCPU7Time) - 
					(float)(mNowIdleCPU7Time -mLastIdleCPU7Time))*100)
					/ (float)(mNowTotalCPU7Time - mLastIdleCPU7Time);
			mLastTotalCPU7Time = mNowTotalCPU7Time;
			mLastIdleCPU7Time = mNowIdleCPU7Time;
		} else {
			mNowTotalCPU7Time = 0;
			mNowIdleCPU7Time = 0;
			mLastTotalCPU7Time = 0;
			mLastIdleCPU7Time = 0;
			mCPU7UsageRatio = 0;
		}
		//get totle memory in MB
		file = new File("/proc/meminfo");
		fr = new FileReader(file);
		reader = new BufferedReader(fr);
		String line = reader.readLine();     //read the first line which contains the total mem
		String[] totalMemInfo = line.split("\\s+");
		mTotalMem = Long.valueOf(totalMemInfo[1]) / 1024; //total mem in MB
		//get free memory in MB
		ActivityManager am = (ActivityManager)getActivity().getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo mi = new MemoryInfo();
		am.getMemoryInfo(mi);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
			mTotalMemFromMI = mi.totalMem / (1024 * 1024);  //B -> MB
		} else {
			mTotalMemFromMI = 0;
		}
		mFreeMem = mi.availMem / (1024 * 1024);  //B -> MB
		//get memory usage ratio
		if (mTotalMem > 0) {
			mMemUsageRatio = ((mTotalMem - mFreeMem) * 100) / mTotalMem;
		} else {
			mMemUsageRatio = 0;
		}
//		Time time = new Time();
//		time.setToNow();
//		long timeStamp = time.toMillis(false);
//		String dateTime = Tools.timeStamp2DateTime(time, true);
//		ArrayList<String> al = new ArrayList<String>();
		//write cpu log
//		al.add("TimeStamp: " + String.valueOf(timeStamp));
//		al.add("DateTime: " + dateTime);
//		al.add(getActivity().getString(R.string.title_cpu_usage) + String.format("%.5f", mCPUUsageRatio) + "%");
//		al.add(getActivity().getString(R.string.title_cpu0_usage) + String.format("%.5f", mCPU0UsageRatio) + "%");
//		al.add(getActivity().getString(R.string.title_cpu1_usage) + String.format("%.5f", mCPU1UsageRatio) + "%");
//		al.add(getActivity().getString(R.string.title_cpu2_usage) + String.format("%.5f", mCPU2UsageRatio) + "%");
//		al.add(getActivity().getString(R.string.title_cpu3_usage) + String.format("%.5f", mCPU3UsageRatio) + "%");
//		al.add(getActivity().getString(R.string.title_cpu4_usage) + String.format("%.5f", mCPU4UsageRatio) + "%");
//		al.add(getActivity().getString(R.string.title_cpu5_usage) + String.format("%.5f", mCPU5UsageRatio) + "%");
//		al.add(getActivity().getString(R.string.title_cpu6_usage) + String.format("%.5f", mCPU6UsageRatio) + "%");
//		al.add(getActivity().getString(R.string.title_cpu7_usage) + String.format("%.5f", mCPU7UsageRatio) + "%");
//		Tools.appendTXTFile(al, CPULOGFILE);
		//write mem log
//		al = new ArrayList<String>();
//		al.add("TimeStamp: " + String.valueOf(timeStamp));
//		al.add("DateTime: " + dateTime);
//		al.add(getActivity().getString(R.string.title_total_mem) + mTotalMem + "MB");
//		al.add(getActivity().getString(R.string.title_free_mem) + mFreeMem + "MB");
//		al.add(getActivity().getString(R.string.title_mem_usage_ratio) + mMemUsageRatio + "%");
//		Tools.appendTXTFile(al, MEMLOGFILE);
	}
	
	@SuppressWarnings("deprecation")
	private void showDeviceInfo() {
		if (mDeviceInfoText != null) {
			//get current time
			Time time = new Time();
			time.setToNow();
			int year = time.year;
			int month = time.month + 1;
			int day = time.monthDay;
			int hour = time.hour;
			int min = time.minute;
			int sec = time.second;
			String daytime = year + "." + month + "." + day + " " + hour + ":" + min + ":" + sec;
			//parse the network type
			String networkType = parseNetworkType(mNetworkType);
			//parse the phone type
			String phoneType = parsePhoneType(mPhoneType);
			//get current data connection state
			String dataState = parseDataState(mDataState);
			//calculate cell ID
			String cellID = Tools.CalcCellID(changeNetworkType(mNetworkType), mCellID);
			if (cellID == null) {
				cellID = "unknown";
			}
			String gsmCid = Tools.CalcCellID(changeNetworkType(mNetworkType), mGsmCid);
			if (gsmCid == null) {
				gsmCid = "unknown";
			}
			//show the device information in the textview
			String deviceInfo = getActivity().getString(R.string.title_device_info) + "\r\n"
					+ daytime + "\r\n"
					+ getActivity().getString(R.string.devision) + "\r\n"
					+ getActivity().getString(R.string.title_manufacturer) + mManufacturer + "\r\n"
					+ getActivity().getString(R.string.title_model) + mMODEL + "\r\n"
					+ getActivity().getString(R.string.title_android_version) + Build.VERSION.RELEASE + "\r\n"
					+ getActivity().getString(R.string.title_serial) + mSerial + "\r\n"
					+ getActivity().getString(R.string.title_IMEI) + mIMEI + "\r\n"
					+ getActivity().getString(R.string.title_IMEISV) + mIMEISV + "\r\n"
					+ getActivity().getString(R.string.title_IMSI) + mIMSI + "\r\n"
					+ getActivity().getString(R.string.title_SIMMCCMNC) + mSIMMCCMNC + "\r\n"
					+ getActivity().getString(R.string.title_SIMOPName) + mSIMOPName + "\r\n"
					+ getActivity().getString(R.string.title_SIMSerial) + mSIMSerial + "\r\n"
					+ getActivity().getString(R.string.title_MSISDN) + mMSISDN + "\r\n"
					+ getActivity().getString(R.string.title_MCCMNC) + mMCCMNC + "\r\n"
					+ getActivity().getString(R.string.title_NWOPName) + mNWOPName + "\r\n"
					+ getActivity().getString(R.string.title_CellID) + cellID + "\r\n"
					+ "---LteCi: " + Tools.CalcCellID(mNetworkType, mCi) + "\r\n"
					+ "---获取CellInfo数量: " + mCellInfoNum + "\r\n"
					+ "---RegisteredCellNum: " + mRegisteredCellNum + "\r\n"
					+ "---LTE PCI: " + mLtePci + "\r\n"
					+ "---GSM CID: " + gsmCid + "\r\n"
					+ getActivity().getString(R.string.title_isroaming) + mIsRoaming + "\r\n"
					+ getActivity().getString(R.string.title_network_type) + networkType + "\r\n"
					+ getActivity().getString(R.string.title_phone_type) + phoneType + "\r\n"
					+ getActivity().getString(R.string.title_data_state) + dataState + "\r\n"
					+ getActivity().getString(R.string.title_radio_signal_strength) + mRadioSignalStrength + "dbm\r\n"
					+ getActivity().getString(R.string.title_lte_signal_strength) + mLteSignalStrength + "dbm\r\n"
					+ getActivity().getString(R.string.title_lte_rsrp) + mLteRsrp + "\r\n"
					+ getActivity().getString(R.string.title_lte_rsrq) + mLteRsrq + "\r\n"
					+ getActivity().getString(R.string.title_lte_rssnr) + mLteRssnr + "\r\n"
					+ getActivity().getString(R.string.title_lte_cqi) + mLteCqi + "\r\n"
					+ getActivity().getString(R.string.title_power_source) + mPowerSource + "\r\n"
					+ getActivity().getString(R.string.title_battery_level) + mBatteryLevel + "%\r\n"
					+ getActivity().getString(R.string.title_battery_volt) + String.format("%.3f", mBatteryVolt) + "V\r\n"
					+ getActivity().getString(R.string.title_battery_temp) + mBatteryTemp + "C\r\n"
					+ getActivity().getString(R.string.title_cpu_usage) + String.format("%.5f", mCPUUsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_cpu0_usage) + String.format("%.5f", mCPU0UsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_cpu1_usage) + String.format("%.5f", mCPU1UsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_cpu2_usage) + String.format("%.5f", mCPU2UsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_cpu3_usage) + String.format("%.5f", mCPU3UsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_cpu4_usage) + String.format("%.5f", mCPU4UsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_cpu5_usage) + String.format("%.5f", mCPU5UsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_cpu6_usage) + String.format("%.5f", mCPU6UsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_cpu7_usage) + String.format("%.5f", mCPU7UsageRatio) + "%\r\n"
					+ getActivity().getString(R.string.title_total_mem) + mTotalMem + "MB\r\n"
					+ getActivity().getString(R.string.title_total_mem_mi) + mTotalMemFromMI + "MB\r\n"
					+ getActivity().getString(R.string.title_free_mem) + mFreeMem + "MB\r\n"
					+ getActivity().getString(R.string.title_mem_usage_ratio) + mMemUsageRatio + "%\r\n"
					+ getActivity().getString(R.string.devision) + "\r\n";
			mDeviceInfoText.setText(deviceInfo);
			//write network log
//			Time t = new Time();
//			t.setToNow();
//			long timeStamp = t.toMillis(false);
//			String dateTime = Tools.timeStamp2DateTime(time, true);
//			ArrayList<String> al = new ArrayList<String>();
//			al.add("TimeStamp: " + String.valueOf(timeStamp));
//			al.add("DateTime: " + dateTime);
//			al.add(getActivity().getString(R.string.title_MSISDN) + mMSISDN);
//			al.add(getActivity().getString(R.string.title_MCCMNC) + mMCCMNC);
//			al.add(getActivity().getString(R.string.title_NWOPName) + mNWOPName);
//			al.add(getActivity().getString(R.string.title_CellID) + mCellID);
//			al.add(getActivity().getString(R.string.title_network_type) + networkType);
//			al.add(getActivity().getString(R.string.title_phone_type) + phoneType);
//			al.add(getActivity().getString(R.string.title_data_state) + dataState);
//			Tools.appendTXTFile(al, NETWORKLOGFILE);
			//write signal log
//			Time time = new Time();
//			time.setToNow();
//			long timeStamp = time.toMillis(false);
//			al = new ArrayList<String>();
//			al.add("TimeStamp: " + String.valueOf(timeStamp));
//			al.add("DateTime: " + dateTime);
//			al.add(getActivity().getString(R.string.title_radio_signal_strength) + mRadioSignalStrength + "dbm");
//			al.add(getActivity().getString(R.string.title_lte_signal_strength) + mLteSignalStrength + "dbm");
//			al.add(getActivity().getString(R.string.title_lte_rsrp) + mLteRsrp);
//			al.add(getActivity().getString(R.string.title_lte_rsrq) + mLteRsrq);
//			al.add(getActivity().getString(R.string.title_lte_rssnr) + mLteRssnr);
//			al.add(getActivity().getString(R.string.title_lte_cqi) + mLteCqi);
//			Tools.appendTXTFile(al, SIGNALLOGFILE);
		}
	}
	
	@SuppressWarnings("deprecation")
	private void writeLogFile() {
		//called 1 time per second
		Time t = new Time();
		t.setToNow();
		long timeStamp = t.toMillis(false);
		String dateTime = Tools.timeStamp2DateTime(t, true);
		ArrayList<String> al;
		//write cpu log file
		if (mCpuCount == 0) {
			al = new ArrayList<String>();
			String s = String.valueOf(timeStamp) + " "
					+ dateTime + " "
					+ String.format("%.5f", mCPUUsageRatio) + " "
					+ String.format("%.5f", mCPU0UsageRatio) + " "
					+ String.format("%.5f", mCPU1UsageRatio) + " "
					+ String.format("%.5f", mCPU2UsageRatio) + " "
					+ String.format("%.5f", mCPU3UsageRatio) + " "
					+ String.format("%.5f", mCPU4UsageRatio) + " "
					+ String.format("%.5f", mCPU5UsageRatio) + " "
					+ String.format("%.5f", mCPU6UsageRatio) + " "
					+ String.format("%.5f", mCPU7UsageRatio);
			al.add(s);
			Tools.appendTXTFile(al, CPULOGFILE);
		}
		mCpuCount++;
		if (mCpuCount >= mCpuLogInterval) {
			mCpuCount = 0;
		}
		//write mem log file
		if (mMemCount == 0) {
			al = new ArrayList<String>();
			String s = String.valueOf(timeStamp) + " "
					+ dateTime + " "
					+ mTotalMem + " "
					+ mFreeMem + " "
					+ mMemUsageRatio;
			al.add(s);
			Tools.appendTXTFile(al, MEMLOGFILE);
		}
		mMemCount++;
		if (mMemCount >= mMemLogInterval) {
			mMemCount = 0;
		}
		//write signal log file
		if (mSignalCount == 0) {
			al = new ArrayList<String>();
			String s = String.valueOf(timeStamp) + " "
					+ dateTime + " "
					+ mRadioSignalStrength + " "
					+ mLteSignalStrength + " "
					+ mLteRsrp + " "
					+ mLteRsrq + " "
					+ mLteRssnr + " "
					+ mLteCqi;
			al.add(s);
			Tools.appendTXTFile(al, SIGNALLOGFILE);
		}
		mSignalCount++;
		if (mSignalCount >= mSignalLogInterval) {
			mSignalCount = 0;
		}
		//write network log file
		if (mNetworkCount == 0) {
			al = new ArrayList<String>();
			//calculate cell ID
			String cellID = Tools.CalcCellID(changeNetworkType(mNetworkType), mCellID);
			if (cellID == null) {
				cellID = "unknown";
			}
			String s = String.valueOf(timeStamp) + " "
					+ dateTime + " "
					+ mMSISDN + " "
					+ mMCCMNC + " "
					+ mNWOPName + " "
					+ cellID + " "
					+ parseNetworkType(mNetworkType) + " "
					+ parsePhoneType(mPhoneType) + " "
					+ parseDataState(mDataState) + " "
					+ mIsRoaming;
			al.add(s);
			Tools.appendTXTFile(al, NETWORKLOGFILE);
		}
		mNetworkCount++;
		if (mNetworkCount >= mNetworkLogInterval) {
			mNetworkCount = 0;
		}
		//write power log file
		if (mPowerCount == 0) {
			al = new ArrayList<String>();
			String s = String.valueOf(timeStamp) + " "
					+ dateTime + " "
					+ mPowerSource + " "
					+ mBatteryLevel + " "
					+ String.format("%.3f", mBatteryVolt) + " "
					+ mBatteryTemp;
			al.add(s);
			Tools.appendTXTFile(al, POWERLOGFILE);
		}
		mPowerCount++;
		if (mPowerCount >= mPowerLogInterval) {
			mPowerCount = 0;
		}
	}
	
	private String parseNetworkType(int nwtype) {
		String networkType;
		switch(nwtype) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			networkType = getActivity().getString(R.string.network_type_1xrtt);
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:
			networkType = getActivity().getString(R.string.network_type_cdma);
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			networkType = getActivity().getString(R.string.network_type_edge);
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			networkType = getActivity().getString(R.string.network_type_ehrpd);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			networkType = getActivity().getString(R.string.network_type_evdo_0);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			networkType = getActivity().getString(R.string.network_type_evdo_a);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			networkType = getActivity().getString(R.string.network_type_evdo_b);
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			networkType = getActivity().getString(R.string.network_type_gprs);
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			networkType = getActivity().getString(R.string.network_type_hsdpa);
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			networkType = getActivity().getString(R.string.network_type_hspa);
			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			networkType = getActivity().getString(R.string.network_type_hspap);
			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			networkType = getActivity().getString(R.string.network_type_hsupa);
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			networkType = getActivity().getString(R.string.network_type_iden);
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:
			networkType = getActivity().getString(R.string.network_type_lte);
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			networkType = getActivity().getString(R.string.network_type_umts);
			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			networkType = getActivity().getString(R.string.network_type_unknown);
			break;
		default:
			networkType = "???";
		}
		return networkType;
	}
	
	private String parsePhoneType(int ptype) {
		String phoneType;
		switch(ptype) {
		case TelephonyManager.PHONE_TYPE_CDMA:
			phoneType = getActivity().getString(R.string.phone_type_cdma);
			break;
		case TelephonyManager.PHONE_TYPE_GSM:
			phoneType = getActivity().getString(R.string.phone_type_gsm);
			break;
		case TelephonyManager.PHONE_TYPE_NONE:
			phoneType = getActivity().getString(R.string.phone_type_none);
			break;
		case TelephonyManager.PHONE_TYPE_SIP:
			phoneType = getActivity().getString(R.string.phone_type_sip);
			break;
		default:
			phoneType = "???";
		}
		return phoneType;
	}
	
	private String parseDataState(int dstate) {
		String dataState;
		switch(dstate) {
		case TelephonyManager.DATA_CONNECTED:
			dataState = getActivity().getString(R.string.data_connected);
			break;
		case TelephonyManager.DATA_CONNECTING:
			dataState = getActivity().getString(R.string.data_connecting);
			break;
		case TelephonyManager.DATA_DISCONNECTED:
			dataState = getActivity().getString(R.string.data_disconnected);
			break;
		case TelephonyManager.DATA_SUSPENDED:
			dataState = getActivity().getString(R.string.data_suspended);
			break;
		default:
			dataState = "???";
		}
		return dataState;
	}
	
	private int changeNetworkType(int nwtype) {
		//2/4G双待手机LTE网络下用GsmCellLocation.getCid()读取的仍是GSM的cellid，而不是LTE的cellid
		//但用TelephonyManager.getNetworkType()读取的是LTE网络类型，因此计算cellid前需要更换网络类型
		//单待的手机不需要转换
		int networkType;
		String model = Tools.getDeviceModel();
		switch(model) {
		case "SM-N7508V"://SAMSUNG NOTE 3 Lite 4G
			networkType = TelephonyManager.NETWORK_TYPE_EDGE;
			break;
		default:
			networkType = nwtype;
		}
		return networkType;
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			// TODO Auto-generated method stub
			super.onSignalStrengthsChanged(signalStrength);
			String radioSignalStrength = String.valueOf(0);
			int lteSignalStrength = 0;
			int lteRsrp = 0;
			int lteRsrq = 0;
			int lteRssnr = 0;
			int lteCqi = 0;
			TelephonyManager tm = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
			if (signalStrength.isGsm() || (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE)) {
				//get GSM signal strength
				int gsmSignalStrength = signalStrength.getGsmSignalStrength();
				if (signalStrength.isGsm() && (gsmSignalStrength != 99)) {
					radioSignalStrength = String.valueOf(-113 + (2 * gsmSignalStrength)); //dbm
				} else {
					radioSignalStrength = "???";
				}
				mRadioSignalStrength = radioSignalStrength;
				//get LTE signal strength
				lteSignalStrength = Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
						signalStrength, "mLteSignalStrength"));
				lteRsrp = Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
						signalStrength, "mLteRsrp"));
				lteRsrq = Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
						signalStrength, "mLteRsrq"));
				lteRssnr = Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
						signalStrength, "mLteRssnr"));
				lteCqi = Integer.parseInt(getSpecifiedFieldValues(SignalStrength.class,
						signalStrength, "mLteCqi"));
				mLteSignalStrength = String.valueOf(-113 + (2 * lteSignalStrength)); //dbm
				mLteRsrp = String.valueOf(lteRsrp);
				mLteRsrq = String.valueOf(lteRsrq);
				mLteRssnr = String.format("%.1f", (float)lteRssnr/10);
				mLteCqi = String.valueOf(lteCqi);
			} else if (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_CDMA) {
				mRadioSignalStrength = String.valueOf(signalStrength.getCdmaDbm());
				mLteSignalStrength = "???";
				mLteRsrp = "???";
				mLteRsrq = "???";
				mLteRssnr = "???";
				mLteCqi = "???";
			} else if ((tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_0)
					|| (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_A)
					|| (tm.getNetworkType() == TelephonyManager.NETWORK_TYPE_EVDO_B)) {
				mRadioSignalStrength = String.valueOf(signalStrength.getEvdoDbm());
				mLteSignalStrength = "???";
				mLteRsrp = "???";
				mLteRsrq = "???";
				mLteRssnr = "???";
				mLteCqi = "???";
			}
			//write signal log
//			Time time = new Time();
//			time.setToNow();
//			long timeStamp = time.toMillis(false);
//			ArrayList<String> al = new ArrayList<String>();
//			al.add("TimeStamp: " + String.valueOf(timeStamp));
//			al.add(getActivity().getString(R.string.title_radio_signal_strength) + mRadioSignalStrength + "dbm");
//			al.add(getActivity().getString(R.string.title_lte_signal_strength) + mLteSignalStrength + "dbm");
//			al.add(getActivity().getString(R.string.title_lte_rsrp) + mLteRsrp);
//			al.add(getActivity().getString(R.string.title_lte_rsrq) + mLteRsrq);
//			al.add(getActivity().getString(R.string.title_lte_rssnr) + mLteRssnr);
//			al.add(getActivity().getString(R.string.title_lte_cqi) + mLteCqi);
//			Tools.appendTXTFile(al, SIGNALLOGFILE);
		}
		
	}
	
	private final String getSpecifiedFieldValues(Class<?> mClass, Object mInstance, String fieldName) {

		String fieldValue = "";

		if (mClass == null || mInstance == null || fieldName == null)
			return fieldValue;

		try {
			final Field field = mClass.getDeclaredField(fieldName);

			if (field != null) {
				field.setAccessible(true);
				fieldValue = field.get(mInstance).toString();
			}

		} catch (NoSuchFieldException exp) {
			fieldValue = "";
		} catch (IllegalAccessException ile) {
			fieldValue = "";
		}

		return fieldValue;
	}
	
	private class BatteryInfoReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mBatteryTemp = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10; //C
			int rawlevel;
			int scale;
			rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			if ((rawlevel >= 0) && (scale > 0)) {
				mBatteryLevel = (rawlevel * 100) / scale;
			} else {
				mBatteryLevel = -1;
			}
			mBatteryVolt = ((float)intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)) / 1000; //V
			int status = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
			if (status != -1) {
				switch(status) {
				case 0:
					mPowerSource = "电池供电";
					break;
				case BatteryManager.BATTERY_PLUGGED_AC:
					mPowerSource = "AC供电";
					break;
				case BatteryManager.BATTERY_PLUGGED_USB:
					mPowerSource = "USB供电";
					break;
				}
			}
		}
		
	}

}
