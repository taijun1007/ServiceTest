package com.cmlab.servicetest;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony.MmsSms;
import android.widget.Toast;

public class MMSObserver extends ContentObserver {
	private static final String TAG = "MMSObserver";
	private static final String SMSLOGFILE = "/sdcard/testcase/smslog.txt";
	
	public static final String MMS_CONTENT_URI = "content://mms/inbox";
	public static final String MMS_LISTEN_URI = "content://mms-sms";
	
//	private static final String[] PROJECTION = new String[] {
//		"address",//0
//		"date"//1
//	};
	
//	private static final int ColIndex_address = 0;
//	private static final int ColIndex_date = 1;
	
	private ContentResolver mResolver;
	private Handler mHandler;
	private Context mContext;

	public MMSObserver(Context context, ContentResolver resolver, Handler handler) {
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
		Uri uri = Uri.parse(MMS_CONTENT_URI);  //收件箱
		String selection = "read = 0";//未读彩信
		String sortOrder = "date asc";//按日期升序排列
		Cursor cursor = mResolver.query(uri, null, selection, null, sortOrder);
		if (cursor == null) {
			return;
		} else {
			String address;
			String date;
			int id;
//			String body;
			ArrayList<String> mmsInfoList = new ArrayList<String>();
			Toast.makeText(mContext, "收到 " + cursor.getCount() + " 条新彩信", Toast.LENGTH_SHORT).show();
			while(cursor.moveToNext()) {
				//write the unread mms info into smslog.txt
//				address = cursor.getString(cursor.getColumnIndex("address"));
				date = cursor.getString(cursor.getColumnIndex("date"));
				mmsInfoList.add("TimeStamp: " + date);
				id = cursor.getInt(cursor.getColumnIndex("_id"));
				String selectionAdd = "msg_id = " +id;
				Uri addUri = Uri.parse("content://mms/" + id + "/addr");
				Cursor cadd = mResolver.query(addUri, null, selectionAdd, null, null);
				if (cadd.moveToFirst()) {
					address = cadd.getString(cadd.getColumnIndex("address"));
					mmsInfoList.add("Phone: " + address);
				}
//				body = cursor.getString(cursor.getColumnIndex("body"));
				mmsInfoList.add("SMS or MMS: MMS");
//				mmsInfoList.add("Text: " + body);
			}
			boolean isOK = Tools.appendTXTFile(mmsInfoList, SMSLOGFILE);
			if (!isOK) {
				Toast.makeText(mContext, "写入smslog文件失败！", Toast.LENGTH_SHORT).show();
			}
			//change unread mms to read
			ContentValues values = new ContentValues();
			values.put("read", 1);
			String where = "read = 0";
			mResolver.update(uri, values, where, null);
			cursor.close();
		}
	}

}
