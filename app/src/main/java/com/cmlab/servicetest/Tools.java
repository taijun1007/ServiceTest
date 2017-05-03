package com.cmlab.servicetest;

import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.format.Time;
import android.util.Log;

import com.cmlab.config.ConfigTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

public class Tools {
	private Process mProcess;
	
	public Process getProcess() {
		return mProcess;
	}
	
	public Tools() {
		try {
			mProcess = Runtime.getRuntime().exec("su");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			mProcess = null;
		}
	}

	public void execShellCMD(String[] s, String TAG) throws IOException, InterruptedException {
		if (s.length != 0) {
			//Process p = Runtime.getRuntime().exec(s[0]);
			//mProcess = p;
			OutputStream outputStream = mProcess.getOutputStream();
			DataOutputStream dataOutputStream = new DataOutputStream(outputStream);
			int i = 0;
			while(i <= s.length-1) {
				dataOutputStream.writeBytes(s[i]);
				i = i+1;
			}
			dataOutputStream.flush();
			dataOutputStream.close();
			outputStream.close();
			if (mProcess.waitFor()!=0) {
				Log.w(TAG, "Shell command exec ERROR!");
			}
		}
	}
	
	public String getPID(String processName) throws IOException, InterruptedException {
		String pid = null;
		final Process process = Runtime.getRuntime().exec("ps " + processName);
		final InputStreamReader inputStream = new InputStreamReader(process.getInputStream());
		final BufferedReader reader = new BufferedReader(inputStream);
		String line = null;
		try {
			int read;
			final char[] buffer = new char[4096];
			final StringBuffer output = new StringBuffer();
			while ((read = reader.read(buffer)) > 0) {
				output.append(buffer, 0, read);
			}
			// Waits for the command to finish.
			process.waitFor();
			//no need to destroy the process since waitFor() will wait until all subprocesses exit
			line = output.toString();
		} finally {
			try {
				reader.close();
				inputStream.close();
				reader.close();
			} catch (Exception e){
				
			}
		}
		String[] rows = line.split("\\n");
		if (rows[0].startsWith("USER")) {
			final String row = rows[1];
			final String[] values_item = row.split("\\s+");
			int itemNum = 1; // second column contains PID
			if (values_item[itemNum].length() > 0) {
				pid = values_item[itemNum];
			}
		}
		return pid;
	}
	
	public void killProcess(String processName) {
		Process sh = null;
		DataOutputStream os = null;
		String pid = null;
		try {
			pid = getPID(processName);
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		if (pid != null) {
			try {
				sh = Runtime.getRuntime().exec("su");
				os = new DataOutputStream(sh.getOutputStream());
				final String Command = "kill -9 " + pid + "\n";
				os.writeBytes(Command);
				os.flush();
				sh.waitFor();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} finally {
				try {
					os.close();
				} catch (IOException e) {
				}
				sh.destroy();
			}
		}
	}
	
	public synchronized static JSONArray readJSONFile(String fileNameWithPath) {
		JSONArray array = null;
		BufferedReader reader = null;
		try {
			File file = new File(fileNameWithPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileReader fr = new FileReader(file);
			reader = new BufferedReader(fr);
			StringBuilder jsonString = new StringBuilder();
			String line = null;
			boolean hasContent = false;
			while((line = reader.readLine()) != null) {
				jsonString.append(line);
				hasContent = true;
			}
			if (hasContent) {
				array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		return array;
	}
	
	public synchronized static boolean writeJSONFile(JSONArray array, String fileNameWithPath) {
		boolean isOK = true;
		BufferedWriter writer = null;
		try {
			File file = new File(fileNameWithPath);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			writer = new BufferedWriter(fw);
			writer.write(array.toString());
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			isOK = false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		return isOK;
	}
	
	public synchronized static ArrayList<String> readTXTFile(String fileName) {
		ArrayList<String> s = null;
		BufferedReader reader = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				return s;
			}
			FileReader fr = new FileReader(file);
			reader = new BufferedReader(fr);
			String line = null;
			s = new ArrayList<String>();
			boolean hasContent = false;
			while((line = reader.readLine()) != null) {
				s.add(line);
				hasContent = true;
			}
			if (hasContent == false) {
				s = null;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		return s;
	}
	
	public synchronized static boolean writeTXTFile(ArrayList<String> s, String fileName) {
		boolean isOK = true;
		BufferedWriter writer = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, false);
			writer = new BufferedWriter(fw);
			int i;
			for (i = 0; i <= s.size() - 1; i++) {
				writer.write(s.get(i));
				writer.write("\r\n");
			}
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			isOK = false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		return isOK;
	}
	
	public synchronized static boolean appendTXTFile(ArrayList<String> s, String fileName) {
		boolean isOK = true;
		BufferedWriter writer = null;
		try {
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file, true);
			writer = new BufferedWriter(fw);
			int i;
			for (i = 0; i <= s.size() - 1; i++) {
				writer.write(s.get(i));
				writer.write("\r\n");
			}
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			isOK = false;
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				}
			}
		}
		return isOK;
	}
	
	//get the device model
	public static String getDeviceModel() {
		Build b = new Build();
		String model = b.MODEL;
		return model;
	}
	
	//get the device manufacturer
	public static String getDeviceManufacturer() {
		Build b = new Build();
		String manufacturer = b.MANUFACTURER;
		return manufacturer;
	}
	
	//get the device serial
	public static String getDeviceSerial() {
		Build b = new Build();
		String serial = b.SERIAL;
		return serial;
	}
	
	public static void sleep(int delay) {
		try {
			Thread.currentThread().sleep(delay);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
	}

	/**
	 * 时间戳转换为日期，格式：年.月.日-星期几-时:分:秒（.毫秒）
	 * @param time Time类型，时间，API22开始废弃
	 * @param isMills boolean类型，true：输出毫秒；false：不输出毫秒
	 *
	 * @return String类型，日期
	 */
	public static String timeStamp2DateTime(Time time, boolean isMills) {
		int year = time.year;
		int month = time.month + 1;
		int day = time.monthDay;
		int dayofweek = time.weekDay;
		int hour = time.hour;
		int min = time.minute;
		int sec = time.second;
		String str;
		switch(dayofweek) {
		case 1:
			str = "MON";
			break;
		case 2:
			str = "TUE";
			break;
		case 3:
			str = "WED";
			break;
		case 4:
			str = "THU";
			break;
		case 5:
			str = "FRI";
			break;
		case 6:
			str = "SAT";
			break;
		default:
			str = "SUN";
		}
		String dateTime = year + "." + month + "." + day + "-" + str + "-" 
				+ hour + ":" + min + ":" + sec;
		if (isMills) {
			long timeStamp = time.toMillis(false);
			int millis = (int)(timeStamp % 1000);
			dateTime = dateTime + "." +  millis;
		}
		return dateTime;
	}

	/**
	 * 时间戳转换为日期，格式：年.月.日-星期几-时:分:秒（.毫秒）
	 * @param gregorianCalendar GregorianCalendar类型，时间，start from API22
	 * @param isMills boolean类型，true：输出毫秒；false：不输出毫秒
	 *
	 * @return String类型，日期
	 */
	public static String timeStamp2DateTime(GregorianCalendar gregorianCalendar, boolean isMills) {
		int year = gregorianCalendar.get(GregorianCalendar.YEAR);
		int month = gregorianCalendar.get(GregorianCalendar.MONTH) + 1;
		int day = gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH);
		int dayofweek = gregorianCalendar.get(GregorianCalendar.DAY_OF_WEEK);
		int hour = gregorianCalendar.get(GregorianCalendar.HOUR_OF_DAY);
		int min = gregorianCalendar.get(GregorianCalendar.MINUTE);
		int sec = gregorianCalendar.get(GregorianCalendar.SECOND);
		String[] week = {"", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
		String dateTime = year + "." + month + "." + day + "-" +week[dayofweek] + "-"
				+ hour + ":" + min + ":" + sec;
		if (isMills) {
			long timeStamp = gregorianCalendar.getTimeInMillis();
			int mills = (int) (timeStamp % 1000);
			dateTime = dateTime + "." + mills;
		}
		return dateTime;
	}

	/**
	 * 时间戳转换为日期，格式：yyyy-MM-dd HH:mm:ss
	 * @param timeStamp 时间戳
	 *
	 * @return String类型，日期
	 */
	public static String timeStamp2DateTime(long timeStamp) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String dateTime = format.format(timeStamp);
		return dateTime;
	}

	/**
	 * 日期转换为时间戳
	 * @param dateTime 日期，String类型，格式：“yyyy-MM-dd HH:mm:ss”
	 *
	 * @return long型，时间戳
	 */
	public static long dateTime2TimeStamp(String dateTime) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = format.parse(dateTime);
		return date.getTime();
	}
	
	public static String CalcCellID(int netWorkType, int cid) {
		//cid = GsmCellLocation.getCid()
		//GsmCellLocation = TelephonyManager.getCellLocation()
		String cellID = null;
		String binarycid;
		char[] cidCharArray;
		char[] cellIDCharArray;
		char[] ca1;//before char'-'
		char[] ca2;//after char'-'
		String ca1str;
		String ca2str;
		if (cid > 0) {
			switch(netWorkType) {
			//2G network
			case TelephonyManager.NETWORK_TYPE_EDGE:
			case TelephonyManager.NETWORK_TYPE_GPRS:
				cellID = String.valueOf(cid);
				break;
			//3G network
			case TelephonyManager.NETWORK_TYPE_HSDPA:
			case TelephonyManager.NETWORK_TYPE_HSPA:
			case TelephonyManager.NETWORK_TYPE_HSPAP:
			case TelephonyManager.NETWORK_TYPE_HSUPA:
				binarycid = Integer.toBinaryString(cid);
				cidCharArray = binarycid.toCharArray();
				int ca1Length = cidCharArray.length - 16;
				ca1 = new char[ca1Length];
				ca2 = new char[16];
				for (int m = 0; m < ca1Length; m++) {
					ca1[m] = cidCharArray[m];
				}
				for (int m = 0; m < 16; m++) {
					ca2[m] = cidCharArray[m + ca1Length];
				}
				ca1str = "";
				for (int m = 0; m < ca1Length; m++) {
					ca1str = ca1str + ca1[m];
				}
				ca2str = "";
				for (int m = 0; m < 16; m++) {
					ca2str = ca2str + ca2[m];
				}
				cellID = Integer.parseInt(ca1str, 2) + "-" + Integer.parseInt(ca2str, 2);
				break;
			//4G LTE network
			case TelephonyManager.NETWORK_TYPE_LTE:
				binarycid = Integer.toBinaryString(cid);
				cidCharArray = binarycid.toCharArray();
				cellIDCharArray = new char[28];
				int length = cidCharArray.length;
				if (length <= 28) {
					for (int m = 0; m < 28-length; m++) {
						cellIDCharArray[m] = '0';
					}
					for (int m = 28-length; m < 28; m++) {
						cellIDCharArray[m] = cidCharArray[m+length-28];
					}
					ca1 = new char[20];
					ca2 = new char[8];
					for (int m = 0; m < 20; m++) {
						ca1[m] = cellIDCharArray[m];
					}
					for (int m = 0; m < 8; m++) {
						ca2[m] = cellIDCharArray[m + 20];
					}
					ca1str = "";
					for (int m = 0; m < 20; m++) {
						ca1str = ca1str + ca1[m];
					}
					ca2str = "";
					for (int m = 0; m < 8; m++) {
						ca2str = ca2str + ca2[m];
					}
					cellID = Integer.parseInt(ca1str, 2) + "-" + Integer.parseInt(ca2str, 2);
				}
				break;
			}
		}
		return cellID;
	}

	/**
	 * 以追加的方式记录log到指定的日志文件中，日志文件由全局常量ConfigTest.logFile指定
	 *
	 * @param msg String类型，log信息
	 */
	public static void writeLogFile(String msg) {
		GregorianCalendar gc = new GregorianCalendar();
		String dateTime = Tools.timeStamp2DateTime(gc, true);
		ArrayList<String> strings = new ArrayList<String>();
		strings.add(gc.getTimeInMillis() + " " + dateTime + " " + msg);
		appendTXTFile(strings, ConfigTest.logFile);
	}

	/**
	 * 以追加的方式记录定位位置信息到指定的文件中，定位位置信息文件由全局变量ConfigTest.locationFile指定
	 *
	 * @param location String类型，定位位置信息
	 */
	public static void writeLocationFile(String location) {
		GregorianCalendar gc = new GregorianCalendar();
		String dateTime = Tools.timeStamp2DateTime(gc, true);
		ArrayList<String> strings = new ArrayList<String>();
		strings.add("--------------------------------------------------");
		strings.add(gc.getTimeInMillis() + " " + dateTime);
		strings.add(location);
		appendTXTFile(strings, ConfigTest.locationFile);
	}
	
}