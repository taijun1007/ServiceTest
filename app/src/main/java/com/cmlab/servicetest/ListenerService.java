package com.cmlab.servicetest;

import java.io.IOException;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.format.Time;

	/**
	 * Listen to the SMS & CALL event during ServiceTest runs. 
	 * Log the event and the time stamp into smslog.txt and calllog.txt files.
	 * @author hunt
	 *
	 */

public class ListenerService extends Service {
	private static final String TAG = "ListenerService";
	private static final int ONGOING_NOTIFICATION = 502;
	private static final int MSG_RUNSUCESS = 0;
	private static final int MSG_RUNFAIL = -1;
	private static final String CALLLOGFILE = "/sdcard/testcase/calllog.txt";
	private static final String SMSLOGFILE = "/sdcard/testcase/smslog.txt";
	
	private static ListenerService mListenerService;
	
	private SMSObserver mSMSObserver;
	private MMSObserver mMMSObserver;
	private ContentResolver mResolver;
	private Handler mHandler = new Handler();
	private AnswerCallThread mAnswerCallThread;
	private AnswerCallHandler mAnswerCallHandler = new AnswerCallHandler();
	private MyPhoneStateListener mMyPhoneStateListener = new MyPhoneStateListener();
	private Process mProcess;
	private Tools mTool;
	
	public static ListenerService getServiceObj() {
		return mListenerService;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@SuppressWarnings("deprecation")
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) 
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			//API level < 16
			Notification notification = new Notification(R.drawable.cmlab, TAG, System.currentTimeMillis());
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			Intent notificationIntent = new Intent(this, MainActivity.class);
			PendingIntent pendingIntent = PendingIntent.getService(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			String notificationTitle = "测试吧提醒您";
			String notificationMSG = "正在监听SMS & CALL...";
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
				.setContentText("正在监听SMS & CALL...")
				.setContentIntent(pendingIntent)
				.build();
			notification.flags |= Notification.FLAG_ONGOING_EVENT;
			startForeground(ONGOING_NOTIFICATION, notification);
		}
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		stopForeground(true);
		mResolver.unregisterContentObserver(mSMSObserver);
//		mResolver.unregisterContentObserver(mMMSObserver);
		mListenerService = null;
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_NONE);
		tm = null;
		mMyPhoneStateListener = null;
		mAnswerCallHandler = null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//check and prepare the log files
		//---call log file
		ArrayList<String> als;
		String titleLine;
		als = Tools.readTXTFile(CALLLOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms) DateTime STATUS";
			als = new ArrayList<String>();
			als.add(titleLine);
			Tools.writeTXTFile(als, CALLLOGFILE);
		}
		//---sms log file
		als = Tools.readTXTFile(SMSLOGFILE);
		if (als == null) {
			titleLine = "TimeStamp(ms) DateTime Phone SMS/MMS Content";
			als = new ArrayList<String>();
			als.add(titleLine);
			Tools.writeTXTFile(als, SMSLOGFILE);
		}
		mListenerService = this;
		mResolver = getContentResolver();
		mSMSObserver = new SMSObserver(mListenerService, mResolver, mHandler);
		Uri uri = Uri.parse(SMSObserver.MMS_LISTEN_URI);
		mResolver.registerContentObserver(uri, true, mSMSObserver);
//		mMMSObserver = new MMSObserver(mListenerService, mResolver, mHandler);
//		uri = Uri.parse(MMSObserver.MMS_LISTEN_URI);
//		mResolver.registerContentObserver(uri, false, mMMSObserver);
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		tm.listen(mMyPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
		if (mAnswerCallThread != null) {
			mAnswerCallThread = null;
		}
	}
	
	private class AnswerCallThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			String[] cmd = new String[] {//"su",
					"uiautomator runtest /sdcard/testcase/testcase.jar -c com.testcase.TelAnswerCase"
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
				mAnswerCallHandler.obtainMessage(MSG_RUNFAIL).sendToTarget();
			}
			mProcess = null;
			mTool = null;
			mAnswerCallHandler.obtainMessage(MSG_RUNSUCESS).sendToTarget();
		}
		
	}
	
	private class AnswerCallHandler extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what) {
			case MSG_RUNSUCESS:
				mAnswerCallThread = null;
				break;
			case MSG_RUNFAIL:
				mAnswerCallThread = null;
				break;
			
			}
		}
		
	}
	
	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// TODO Auto-generated method stub
			super.onCallStateChanged(state, incomingNumber);
			Time time;
			long timeStamp;
			String dateTime;
			ArrayList<String> al;
			String contentLine;
			switch(state) {
			case TelephonyManager.CALL_STATE_IDLE:
				time = new Time();
				time.setToNow();
				timeStamp = time.toMillis(false);
				dateTime = Tools.timeStamp2DateTime(time, true);
				al = new ArrayList<String>();
//				al.add("IDLE:    " + String.valueOf(timeStamp));
//				al.add("IDLE(DT):" + dateTime);
				contentLine = String.valueOf(timeStamp) + " "
						+ dateTime + " "
						+ "IDLE";
				al.add(contentLine);
				Tools.appendTXTFile(al, CALLLOGFILE);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				time = new Time();
				time.setToNow();
				timeStamp = time.toMillis(false);
				dateTime = Tools.timeStamp2DateTime(time, true);
				al = new ArrayList<String>();
//				al.add("OFFHOOK: " + String.valueOf(timeStamp));
//				al.add("OFFHOOK(DT):" + dateTime);
				contentLine = String.valueOf(timeStamp) + " "
						+ dateTime + " "
						+ "OFFHOOK";
				al.add(contentLine);
				Tools.appendTXTFile(al, CALLLOGFILE);
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				time = new Time();
				time.setToNow();
				timeStamp = time.toMillis(false);
				dateTime = Tools.timeStamp2DateTime(time, true);
				al = new ArrayList<String>();
//				al.add("Ringing: " + String.valueOf(timeStamp));
//				al.add("Ringing(DT):" + dateTime);
				contentLine = String.valueOf(timeStamp) + " "
						+ dateTime + " "
						+ "Ringing";
				al.add(contentLine);
				Tools.appendTXTFile(al, CALLLOGFILE);
				if (mAnswerCallThread == null) {
					mAnswerCallThread = new AnswerCallThread();
					mAnswerCallThread.start();
				}
				break;
			}
		}
		
	}

}
