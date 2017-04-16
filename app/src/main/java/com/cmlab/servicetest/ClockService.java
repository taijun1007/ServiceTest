package com.cmlab.servicetest;

import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.format.Time;

@SuppressWarnings("deprecation")
public class ClockService extends IntentService {
	private static final String TAG = "ClockService";
	private static final String YEAR = "year";
	private static final String MONTH = "month";
	private static final String DAY = "day";
	private static final String DAYOFWEEK = "dayofweek";
	private static final String HOUR = "hour";
	private static final String MINUTE = "minute";
	private static final String SECOND = "second";
	private static final int ONGOING_NOTIFICATION = 3932;
	public static final String ACTION_UPDATE_UI_TIME =
			"com.cmlab.servicetest.ClockService.OneSecTimer";
	public static final String PERM_PRIVATE = "com.cmlab.servicetest.PRIVATE";
	private static final String CELLIDLOGFILE = "/sdcard/testcase/cellIDlog.txt";
	
	private static ClockService mClockService;
	
	private boolean isOneSecFinish;
	private long lastTickMillis;
	private long currentMillis;
	private boolean isOver;
	
	public static ClockService getServiceObj() {
		return mClockService;
	}

	public ClockService() {
		super(TAG);
		// TODO Auto-generated constructor stub
		isOneSecFinish = true;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		mClockService = this;
		isOver = false;
		ArrayList<String> als = new ArrayList<String>();
		String titleLine = "TimeStamp(ms) DateTime NetworkType CellID";
		als.add(titleLine);
		Tools.writeTXTFile(als, CELLIDLOGFILE);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		while(!isOver) {
			if (isOneSecFinish == true) {
				isOneSecFinish = false;
				lastTickMillis = SystemClock.elapsedRealtime();
				//broadcast the current time
				Time time = new Time();
				time.setToNow();
				int year = time.year;
				int month = time.month + 1;  //0-11 to 1-12
				int day = time.monthDay;
				int dayofweek = time.weekDay;
				int hour = time.hour;
				int minute = time.minute;
				int second = time.second;
				Intent myIntent = new Intent(ACTION_UPDATE_UI_TIME);
				myIntent.putExtra(YEAR, year);
				myIntent.putExtra(MONTH, month);
				myIntent.putExtra(DAY, day);
				myIntent.putExtra(DAYOFWEEK, dayofweek);
				myIntent.putExtra(HOUR, hour);
				myIntent.putExtra(MINUTE, minute);
				myIntent.putExtra(SECOND, second);
				sendBroadcast(myIntent, PERM_PRIVATE);
				//test1
				TelephonyManager tm = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
				GsmCellLocation gcl = (GsmCellLocation)tm.getCellLocation();
				//get cell id
				int cellID;
				if (gcl != null) {
					cellID = gcl.getCid();
				} else {
					cellID = -1;
				}
				//get network type
				int networkType = tm.getNetworkType();
				String cid = Tools.CalcCellID(changeNetworkType(networkType), cellID);
				if (cid == null) {
					cid = "???";
				}
				//write cellIDlog.txt
				Time t = new Time();
				t.setToNow();
				long timeStamp = t.toMillis(false);
				String dateTime = Tools.timeStamp2DateTime(t, true);
				ArrayList<String> al = new ArrayList<String>();
				String s = String.valueOf(timeStamp) + " "
						+ dateTime + " "
						+ parseNetworkType(networkType) + " "
						+ cid;
				al.add(s);
				Tools.appendTXTFile(al, CELLIDLOGFILE);
				//test1 end
				currentMillis = SystemClock.elapsedRealtime();
				if ((currentMillis - lastTickMillis) < 1000) {
					isOneSecFinish = false;
				} else {
					isOneSecFinish = true;
				}
			} else {
				currentMillis = SystemClock.elapsedRealtime();
				if ((currentMillis - lastTickMillis) < 1000) {
					isOneSecFinish = false;
				} else {
					isOneSecFinish = true;
				}
			}
		}
	}
	
	//test2
	private String parseNetworkType(int nwtype) {
		String networkType;
		switch(nwtype) {
		case TelephonyManager.NETWORK_TYPE_1xRTT:
			networkType = getString(R.string.network_type_1xrtt);
			break;
		case TelephonyManager.NETWORK_TYPE_CDMA:
			networkType = getString(R.string.network_type_cdma);
			break;
		case TelephonyManager.NETWORK_TYPE_EDGE:
			networkType = getString(R.string.network_type_edge);
			break;
		case TelephonyManager.NETWORK_TYPE_EHRPD:
			networkType = getString(R.string.network_type_ehrpd);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_0:
			networkType = getString(R.string.network_type_evdo_0);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_A:
			networkType = getString(R.string.network_type_evdo_a);
			break;
		case TelephonyManager.NETWORK_TYPE_EVDO_B:
			networkType = getString(R.string.network_type_evdo_b);
			break;
		case TelephonyManager.NETWORK_TYPE_GPRS:
			networkType = getString(R.string.network_type_gprs);
			break;
		case TelephonyManager.NETWORK_TYPE_HSDPA:
			networkType = getString(R.string.network_type_hsdpa);
			break;
		case TelephonyManager.NETWORK_TYPE_HSPA:
			networkType = getString(R.string.network_type_hspa);
			break;
		case TelephonyManager.NETWORK_TYPE_HSPAP:
			networkType = getString(R.string.network_type_hspap);
			break;
		case TelephonyManager.NETWORK_TYPE_HSUPA:
			networkType = getString(R.string.network_type_hsupa);
			break;
		case TelephonyManager.NETWORK_TYPE_IDEN:
			networkType = getString(R.string.network_type_iden);
			break;
		case TelephonyManager.NETWORK_TYPE_LTE:
			networkType = getString(R.string.network_type_lte);
			break;
		case TelephonyManager.NETWORK_TYPE_UMTS:
			networkType = getString(R.string.network_type_umts);
			break;
		case TelephonyManager.NETWORK_TYPE_UNKNOWN:
			networkType = getString(R.string.network_type_unknown);
			break;
		default:
			networkType = "???";
		}
		return networkType;
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
	//test2 end

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			//API level < 16
			Notification notification = new Notification(R.drawable.cmlab, TAG, System.currentTimeMillis());
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			notification.defaults = Notification.DEFAULT_LIGHTS;
			notification.ledARGB = Color.BLUE;
			notification.ledOnMS = 5000;
			Intent notificationIntent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			String notificationTitle = "测试吧提醒您";
			String notificationMSG = "ClockService正在运行...";
			notification.setLatestEventInfo(this, notificationTitle, notificationMSG, pendingIntent);
			startForeground(ONGOING_NOTIFICATION, notification);
		} else {
			//API level >= 16
			Intent notificationIntent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			Notification notification = new Notification.Builder(this)
				.setSmallIcon(R.drawable.cmlab)
				.setTicker(TAG)
				.setContentTitle("测试吧提醒您")
				.setContentText("ClockService正在运行...")
				.setContentIntent(pendingIntent)
				.build();
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			notification.flags |= Notification.FLAG_SHOW_LIGHTS;
			notification.defaults = Notification.DEFAULT_LIGHTS;
			notification.ledARGB = Color.BLUE;
			notification.ledOnMS = 5000;
			startForeground(ONGOING_NOTIFICATION, notification);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopForeground(true);
		mClockService = null;
		isOver = true;
	}

}
