package com.cmlab.servicetest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.widget.Toast;

public class SMSObserver extends ContentObserver {
	private static final String TAG = "SMSObserver";
	private static final String SMSLOGFILE = "/sdcard/testcase/smslog.txt";
	
	public static final String SMS_CONTENT_URI = "content://sms/inbox";
	public static final String SMS_LISTEN_URI = "content://sms";
	public static final String MMS_CONTENT_URI = "content://mms/inbox";
	public static final String MMS_LISTEN_URI = "content://mms-sms";
	
	private static final String[] PROJECTION = new String[] {
		"address",//0
		"body",//1
		"date",//2
	};
	
	private static final int ColIndex_address = 0;
	private static final int ColIndex_body = 1;
	private static final int ColIndex_date = 2;
	
	private ContentResolver mResolver;
	private Handler mHandler;
	private Context mContext;

	public SMSObserver(Context context, ContentResolver resolver, Handler handler) {
		super(handler);
		// TODO Auto-generated constructor stub
		mContext = context;
		mResolver = resolver;
		mHandler = handler;
	}

	@Override
	public void onChange(boolean selfChange) {
		// TODO Auto-generated method stub
		super.onChange(selfChange);
		//check and get unread sms
		Uri smsUri = Uri.parse(SMS_CONTENT_URI);  //收件箱
		String selection = "read = 0";//未读短信
		String sortOrder = "date asc";//按日期升序排列
		int smsCount = 0;
		Cursor smsCursor = mResolver.query(smsUri, PROJECTION, selection, null, sortOrder);
		if (smsCursor == null) {
			return;
		} else {
			String smsAddress;
			String smsBody;
			String smsDate; //timestamp
			String smsDateTime;  //datetime
			ArrayList<String> smsInfoList = new ArrayList<String>();
			//Toast.makeText(mContext, "收到 " + smsCursor.getCount() + " 条新信息", Toast.LENGTH_SHORT).show();
			smsCount = smsCursor.getCount();
			while(smsCursor.moveToNext()) {
				//write the unread sms info into smslog.txt
				smsAddress = smsCursor.getString(ColIndex_address);
				smsBody = smsCursor.getString(ColIndex_body);
				smsDate = smsCursor.getString(ColIndex_date); //millis
				SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
				smsDateTime = format.format(Long.valueOf(smsDate));
//				smsInfoList.add("TimeStamp: " + smsDate);
//				smsInfoList.add("DateTime: " + smsDateTime);
//				smsInfoList.add("Phone: " + smsAddress);
//				smsInfoList.add("SMS or MMS: SMS");
//				smsInfoList.add("Content: " + smsBody);
				String contentLine = smsDate + " "
						+ smsDateTime + " "
						+ smsAddress + " "
						+ "SMS "
						+ smsBody;
				smsInfoList.add(contentLine);
			}
			boolean isOK = Tools.appendTXTFile(smsInfoList, SMSLOGFILE);
			if (!isOK) {
				Toast.makeText(mContext, "写入smslog文件失败！", Toast.LENGTH_SHORT).show();
			}
			//change unread sms to read
			ContentValues smsValues = new ContentValues();
			smsValues.put("read", 1);
			String where = "read = 0";
			mResolver.update(smsUri, smsValues, where, null);
			smsCursor.close();
		}
		//check and get unread mms
		Uri mmsUri = Uri.parse(MMS_CONTENT_URI);  //收件箱
		selection = "read = 0";//未读彩信
		sortOrder = "date asc";//按日期升序排列
		int mmsCount = 0;
		Cursor mmsCursor = mResolver.query(mmsUri, null, selection, null, sortOrder);
		if (mmsCursor == null) {
			return;
		} else {
			String mmsAddress;
			String mmsDate;  //timestamp(second)
			String mmsDateTime;  //date time
			int mmsId;
			ArrayList<String> mmsInfoList = new ArrayList<String>();
			//Toast.makeText(mContext, "收到 " + mmsCursor.getCount() + " 条新彩信", Toast.LENGTH_SHORT).show();
			mmsCount = mmsCursor.getCount();
			while(mmsCursor.moveToNext()) {
				//write the unread mms info into smslog.txt
				mmsDate = mmsCursor.getString(mmsCursor.getColumnIndex("date"));  //second
				SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
				mmsDateTime = format.format(Long.valueOf(mmsDate) * 1000);  //sec -> millis
//				mmsInfoList.add("TimeStamp: " + (Long.valueOf(mmsDate) * 1000));
//				mmsInfoList.add("DateTime: " + mmsDateTime);
				mmsId = mmsCursor.getInt(mmsCursor.getColumnIndex("_id"));
				String selectionAdd = "msg_id = " +mmsId;
				Uri addUri = Uri.parse("content://mms/" + mmsId + "/addr");
				Cursor cAdd = mResolver.query(addUri, null, selectionAdd, null, null);
				if (cAdd.moveToFirst()) {
					mmsAddress = cAdd.getString(cAdd.getColumnIndex("address"));
//					mmsInfoList.add("Phone: " + mmsAddress);
				} else {
					mmsAddress = "unknown";
				}
				cAdd.close();
//				mmsInfoList.add("SMS or MMS: MMS");
				String contentLine = (Long.valueOf(mmsDate) * 1000) + " "
						+ mmsDateTime + " "
						+ mmsAddress + " "
						+ "MMS";
				mmsInfoList.add(contentLine);
			}
			boolean isOK = Tools.appendTXTFile(mmsInfoList, SMSLOGFILE);
			if (!isOK) {
				Toast.makeText(mContext, "写入smslog文件失败！", Toast.LENGTH_SHORT).show();
			}
			//change unread mms to read
			ContentValues values = new ContentValues();
			values.put("read", 1);
			String where = "read = 0";
			mResolver.update(mmsUri, values, where, null);
			mmsCursor.close();
		}
		Toast.makeText(mContext, "收到 " + smsCount + " 条新信息和 "+ mmsCount + " 条新彩信",
				Toast.LENGTH_SHORT).show();
	}

}
