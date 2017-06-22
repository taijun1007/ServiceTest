package com.cmlab.servicetest;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.cmlab.config.ConfigTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;

/**
 * 打电话语音主叫测试例。根据下发参数，按指定时间或指定次数，以指定通话时长和通话间隔拨打电话，并记录log。
 * Created by hunt on 2017/6/16.
 */

public class MOCallCase {

    private static final String TAG = "MOCallCase";

    private String logPath;
    private String parameterFile;
    private String dialNumber = null;  //电话号码
    private int dialDuration = 0;  //通话时长，单位：秒
    private int dialRepeatTimes = 0;  //重复次数
    private int dialRptInterval = 0;  //重复呼叫的间隔，单位：秒
    private int dialMaxFailure = 0;  //最大失败次数
    private String dialType = null;  //主叫测试类型，0-长呼/1-按次数短呼/2-按时间短呼
    private int dialTime = 0;  //测试总时长，单位：秒
    private int dialWaitingTolerance = 0;  //等待接通忍耐时长，单位：秒

    public boolean execute(Context context) {
        //读取setup.json文件
        File setupFile = new File(ConfigTest.setupJsonFile);
        if (setupFile.exists()) {
            JSONArray array = Tools.readJSONFile(ConfigTest.setupJsonFile);
            if (array != null) {
                try {
                    JSONObject jsonObject = array.getJSONObject(0);
                    logPath = jsonObject.getString(ConfigTest.JSON_KEY_LOGPATH);
                    parameterFile = jsonObject.getString(ConfigTest.JSON_KEY_PARAFILE);
                } catch (JSONException e) {
                    e.printStackTrace();
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("setup.json文件解析错误！");
                        Tools.writeLogFile(e.toString());
                    }
                    return false;
                }
                if (ConfigTest.DEBUG) {
                    Log.i(TAG, "setup.json文件解析成功");
                    Tools.writeLogFile("setup.json文件解析成功");
                }
            } else {
                Toast.makeText(context, "setup.json文件内容错误", Toast.LENGTH_SHORT).show();
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("setup.json文件内容错误");
                }
                return false;
            }
        } else {
            Toast.makeText(context,"setup.json文件不存在！", Toast.LENGTH_SHORT).show();
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("setup.json文件不存在！");
            }
            return false;
        }
        //读取业务参数文件
        if (parameterFile != null) {
            File paraFile = new File(parameterFile);
            if (paraFile.exists()) {
                JSONArray paraArray = Tools.readJSONFile(parameterFile);
                if (paraArray != null) {
                    try {
                        JSONObject paraJsonObject = paraArray.getJSONObject(0);
                        dialNumber = paraJsonObject.getString(ConfigTest.JSON_KEY_DIAL_NUMBER);
                        dialDuration = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_DIAL_DURATION));
                        dialRepeatTimes = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_DIAL_REPEAT_TIMES));
                        dialRptInterval = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_DIAL_REPEAT_INTERVAL));
                        dialMaxFailure = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_DIAL_MAX_FAILURE));
                        dialType = paraJsonObject.getString(ConfigTest.JSON_KEY_DIAL_TYPE);
                        dialTime = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_DIAL_TIME));
                        dialWaitingTolerance = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_DIAL_WAITING_TOLERANCE));
                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("参数文件内容解析错误！");
                            Tools.writeLogFile(e.toString());
                        }
                        return false;
                    }
                    if (ConfigTest.DEBUG) {
                        Log.i(TAG, "业务参数文件解析成功！");
                        Tools.writeLogFile("业务参数文件解析成功！");
                    }
                } else {
                    Toast.makeText(context, "任务参数文件内容错误！", Toast.LENGTH_SHORT).show();
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("任务参数文件内容错误！");
                    }
                    return false;
                }
            } else {
                Toast.makeText(context, "任务参数文件不存在！", Toast.LENGTH_SHORT).show();
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("任务参数文件不存在！");
                }
                return false;
            }
            ConfigTest.isParameterRead = true;
        } else {
            Toast.makeText(context, "没有可用参数文件！", Toast.LENGTH_SHORT).show();
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("没有可用参数文件！");
            }
            return false;
        }
        //建立或打开log数据库文件，如果没有WeiXinImageLog数据库表就建立
        //退出execute方法时要关闭数据库
        SQLiteDatabase db = SQLiteDatabase.openDatabase(logPath + ".db", null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
        db.execSQL("CREATE TABLE IF NOT EXISTS sendCallLog (TimeStamp VARCHAR, DateTime VARCHAR, Status VARCHAR, DurationRequired VARCHAR, Repeat VARCHAR, Interval VARCHAR, Succes VARCHAR, MaxFailure VARCHAR, CallType VARCHAR)");
        //开始执行测试任务
        long startTime;
        long logTime;
        long dialingLogTime;
        long alertingLogTime;
        long activeLogTime;
        long idleLogTime;
        long dialTimeoutTime;  //dialingLogTime+dialWaitingTolerance*1000, answer timeout
        GregorianCalendar gc = new GregorianCalendar();
        int year = gc.get(GregorianCalendar.YEAR);
        String[] sa;
        String logTimeStr;
        String line;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        SimpleDateFormat logdtformat = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss.SSS");
        int failureCount = 0;
        boolean isEndTest = false;
        String manufacturer = Tools.getDeviceManufacturer();
        String model = Tools.getDeviceModel();
        String logcatCMD;
        switch(manufacturer) {
            case "HUAWEI":
            case "CMDC":
                switch(model) {
                    case "HUAWEI MT7-TL00":  //华为MATE7
                    case "M821":  //N1
                        logcatCMD = "logcat -v time -b radio -b main";
                        break;
                    default:
                        logcatCMD = "logcat -v time -b radio";
                }
                break;
            default:
                logcatCMD = "logcat -v time -b radio";
                break;
        }
        switch (dialType) {
            case "1":  //按次数短呼
                for(int i = 1; i <= dialRepeatTimes; i++) {
                    //call start time
                    startTime = System.currentTimeMillis();
                    dialingLogTime = startTime;
                    alertingLogTime = startTime;
                    activeLogTime = startTime;
                    idleLogTime = startTime;
                    //执行拨打电话操作
                    Uri uri = Uri.parse("tel:" + dialNumber);
                    Intent callIntent = new Intent(Intent.ACTION_CALL, uri);
                    context.startActivity(callIntent);
                    Process p = null;
                    try {
                        p = Runtime.getRuntime().exec(logcatCMD);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        boolean isDialing = false;
                        boolean isAlerting = false;
                        boolean isActive = false;
                        boolean isIdle = false;
                        boolean isSuccess = false;
                        boolean isWaitTimeout = false;
                        Date date = null;
                        //get DIALING time
                        while(!isDialing) {
                            if(d.isScreenOn() == false) {
                                d.wakeUp();
                                if(DEBUG) {
                                    System.out.println("dialing wakeup");
                                }
                            }
                            line = reader.readLine();
                            if(line == null) {
                                continue;
                            }
                            sa = line.split("\\s+");
                            logTimeStr = year + "-" + sa[0] + " " + sa[1];
                            try {
                                date = format.parse(logTimeStr);
                            } catch (ParseException e) {
                                // TODO: handle exception
                                if(DEBUG) {
                                    System.out.println("Parse log timestamp ERROR! logTimeStr = " + logTimeStr);
                                }
                                continue;
                            }
                            logTime = date.getTime();
                            if(logTime >= startTime) {
                                if((line.contains("GsmConnection") &&
                                        line.contains("DIALING")) ||
                                        (line.contains("IMSSenderRxr") &&
                                                line.contains("DIALING")) ||
                                        (line.contains("ImsPhoneCallTracker") &&  //N1 max VoLTE
                                                line.contains("dial")) ||
                                        (line.contains("GET_CURRENT_CALLS") &&
                                                line.contains("DIALING")) ||
                                        (line.contains("ImsPhoneCallTracker") &&  //ZTE Axon MINI
                                                line.contains("OFFHOOK")) ||
                                        (line.contains("RLOG-RILJ_IMS") &&  //HUAWEI MATE 7
                                                line.contains("DIALING"))) {
                                    dialingLogTime = logTime;
                                    isDialing = true;
                                    if(DEBUG) {
                                        System.out.println("Status: DIALING");
                                    }
                                } else if((line.contains("GsmConnection") &&
                                        line.contains("onDisconnect")) ||
                                        (line.contains("IMSSenderRxr") &&
                                                line.contains("HANGUP_FOREGROUND_RESUME_BACKGROUND")) ||
                                        (line.contains("ImsPhoneCallTracker") &&  //N1 max VoLTE
                                                line.contains("DISCONNECTED")) ||
                                        (line.contains("RLOG-IMSCONN") && //HTC M8t, no SIM
                                                line.contains("onDisconnect")) ||
                                        (line.contains("ImsPhoneConnection") && //ZTE Axon MINI
                                                line.contains("onDisconnect")) ||
                                        (line.contains("RLOG-RILJ_IMS") &&  //HUAWEI MATE7 主叫挂机
                                                line.contains("HW_IMS_HANGUP_FOREGROUND_RESUME_BACKGROUND")) ||
                                        (line.contains("RLOG-AT") &&  //HUAWEI MATE7 被叫挂机或网络掉话
                                                line.contains("CEND"))) {
                                    isDialing = true;
                                    isAlerting = true;
                                    isActive = true;
                                    activeLogTime = logTime;
                                    idleLogTime = logTime;
                                    isIdle = true;
                                    isSuccess = false;

                                    if(DEBUG) {
                                        System.out.println("Status: DIALING endcall");
                                    }
                                    clickBombBox();
                                    break;
                                }
                            }
                            if(((System.currentTimeMillis() - startTime) >= 60000) && !isDialing) {//60秒内如果没有发起呼叫，则本次失败，进行下一轮呼叫，比如处于飞行模式时发起呼叫会失败，且没有DIALING状态log，此时计时60秒，超时则本轮呼叫失败，开始下一轮
                                isDialing = true;
                                isAlerting = true;
                                isActive = true;
//						activeLogTime = logTime;
//						idleLogTime = logTime;
                                isIdle = true;
                                isSuccess = false;
                                if(DEBUG) {
                                    System.out.println("Status: wait for answer timeout");
                                }
                                clickBombBox();
                                break;
                            }
                        }
                        dialTimeoutTime = dialingLogTime + dialWaitingTolerance * 1000;
                        //get ALERTING time
                        while(!isAlerting) {
                            if(d.isScreenOn() == false) {
                                d.wakeUp();
                                if(DEBUG) {
                                    System.out.println("alerting wakeup");
                                }
                            }
                            if(System.currentTimeMillis() >= dialTimeoutTime) {
                                isAlerting = true;
                                isActive = true;
                                isWaitTimeout = true;
                                isSuccess = false;
                                if(DEBUG) {
                                    System.out.println("Status: wait for answer timeout");
                                }
                                break;
                            }
                            line = reader.readLine();
                            if(line == null) {
                                continue;
                            }
                            sa = line.split("\\s+");
                            logTimeStr = year + "-" + sa[0] + " " + sa[1];
                            try {
                                date = format.parse(logTimeStr);
                            } catch (ParseException e) {
                                // TODO: handle exception
                                if(DEBUG) {
                                    System.out.println("Parse log timestamp ERROR! logTimeStr = " + logTimeStr);
                                }
                                continue;
                            }
                            logTime = date.getTime();
                            if((line.contains("GsmConnection") &&
                                    line.contains("ALERTING")) ||
                                    (line.contains("IMSSenderRxr") &&
                                            line.contains("ALERTING")) ||
                                    (line.contains("ImsPhoneCallTracker") &&  //N1 max VoLTE,  ZTE Axon MINI
                                            line.contains("ALERTING")) ||
                                    (line.contains("GET_CURRENT_CALLS") &&
                                            line.contains("ALERTING")) ||
                                    (line.contains("RLOG-RILJ_IMS") &&  //HUAWEI MATE 7
                                            line.contains("ALERTING"))) {
                                alertingLogTime = logTime;
                                isAlerting = true;
                                if(DEBUG) {
                                    System.out.println("Status: ALERTING");
                                }
                            } else if((line.contains("GsmConnection") &&
                                    line.contains("onDisconnect")) ||
                                    (line.contains("IMSSenderRxr") &&
                                            line.contains("HANGUP_FOREGROUND_RESUME_BACKGROUND")) ||
                                    (line.contains("ImsPhoneCallTracker") &&  //N1 max VoLTE
                                            line.contains("DISCONNECTED")) ||
                                    (line.contains("RLOG-IMSCONN") && //HTC M8t, no SIM
                                            line.contains("onDisconnect")) ||
                                    (line.contains("ImsPhoneConnection") && //ZTE Axon MINI
                                            line.contains("onDisconnect")) ||
                                    (line.contains("RLOG-RILJ_IMS") &&  //HUAWEI MATE7 主叫挂机
                                            line.contains("HW_IMS_HANGUP_FOREGROUND_RESUME_BACKGROUND")) ||
                                    (line.contains("RLOG-AT") &&  //HUAWEI MATE7 被叫挂机或网络掉话
                                            line.contains("CEND"))) {
                                isAlerting = true;
                                isActive = true;
                                activeLogTime = logTime;
                                idleLogTime = logTime;
                                isIdle = true;
                                isSuccess = false;
                                if(DEBUG) {
                                    System.out.println("Status: ALERTING endcall");
                                }
                                clickBombBox();
                                break;
                            }
                        }
                        //get ACTIVE time
                        while(!isActive) {
                            if(d.isScreenOn() == false) {
                                d.wakeUp();
                                if(DEBUG) {
                                    System.out.println("active wakeup");
                                }
                            }
                            if(System.currentTimeMillis() >= dialTimeoutTime) {
                                isActive = true;
                                isWaitTimeout = true;
                                isSuccess = false;
                                if(DEBUG) {
                                    System.out.println("Status: wait for answer timeout");
                                }
                                break;
                            }
                            line = reader.readLine();
                            if(line == null) {
                                continue;
                            }
                            sa = line.split("\\s+");
                            logTimeStr = year + "-" + sa[0] + " " + sa[1];
                            try {
                                date = format.parse(logTimeStr);
                            } catch (ParseException e) {
                                // TODO: handle exception
                                if(DEBUG) {
                                    System.out.println("Parse log timestamp ERROR! logTimeStr = " + logTimeStr);
                                }
                                continue;
                            }
                            logTime = date.getTime();
                            if((line.contains("GsmConnection") &&
                                    line.contains("ACTIVE")) ||
                                    (line.contains("IMSSenderRxr") &&
                                            line.contains("ACTIVE")) ||
                                    (line.contains("ImsPhoneCallTracker") &&  //N1 max VoLTE,  ZTE Axon MINI
                                            line.contains("ACTIVE")) ||
                                    (line.contains("GET_CURRENT_CALLS") &&  //N1 max handover to 2G
                                            line.contains("ACTIVE")) ||
                                    (line.contains("RLOG-RILJ_IMS") &&  //HUAWEI MATE 7
                                            line.contains("ACTIVE"))) {
                                activeLogTime = logTime;
                                isActive = true;
                                if(DEBUG) {
                                    System.out.println("Status: ACTIVE");
                                    System.out.println("Get ACTIVE log time: " + String.valueOf(System.currentTimeMillis()));
                                    System.out.println("activeLogTime: " + String.valueOf(activeLogTime));
                                }
                            } else if((line.contains("GsmConnection") &&
                                    line.contains("onDisconnect")) ||
                                    (line.contains("IMSSenderRxr") &&
                                            line.contains("HANGUP_FOREGROUND_RESUME_BACKGROUND")) ||
                                    (line.contains("ImsPhoneCallTracker") &&  //N1 max VoLTE
                                            line.contains("DISCONNECTED")) ||
                                    (line.contains("RLOG-IMSCONN") && //HTC M8t, no SIM
                                            line.contains("onDisconnect")) ||
                                    (line.contains("ImsPhoneConnection") && //ZTE Axon MINI
                                            line.contains("onDisconnect")) ||
                                    (line.contains("RLOG-RILJ_IMS") &&  //HUAWEI MATE7 主叫挂机
                                            line.contains("HW_IMS_HANGUP_FOREGROUND_RESUME_BACKGROUND")) ||
                                    (line.contains("RLOG-AT") &&  //HUAWEI MATE7 被叫挂机或网络掉话
                                            line.contains("CEND"))) {
                                isActive = true;
                                activeLogTime = logTime;
                                idleLogTime = logTime;
                                isIdle = true;
                                isSuccess = false;
                                if(DEBUG) {
                                    System.out.println("Status: ACTIVE endcall");
                                }
                                clickBombBox();
                                break;
                            }
                        }
                        boolean isHangUpButtonPressed = false;
                        System.out.println("Check idle start time: " + String.valueOf(System.currentTimeMillis()));
                        long wait4IdleTimeout = 0;  //等待获取挂机idle状态的超时时间，挂机操作后60秒内如果还没获取挂机特征日志，则认为异常，结束本次呼叫，进入下一轮
                        //get IDLE time
                        while(!isIdle) {
                            if(d.isScreenOn() == false) {
                                d.wakeUp();
                                if(DEBUG) {
                                    System.out.println("idle wakeup");
                                }
                            }
                            if(isWaitTimeout) {
                                if(isHangUpButtonPressed == false) {
                                    hangUp(manufacturer, model, d);
                                    isHangUpButtonPressed = true;
                                    wait4IdleTimeout = System.currentTimeMillis() + 60 * 1000;  //挂机操作后60秒超时
                                }
                                if(System.currentTimeMillis() >= wait4IdleTimeout) {
                                    //超时没有接通后，且挂机操作完成60秒后，仍未获取到挂机idle特征日志的，则异常
                                    isIdle = true;
                                    isSuccess = false;
                                    clickBombBox();
                                    break;
                                }
                            } else if(System.currentTimeMillis() >= (activeLogTime + ((long)dialDuration)*1000)) {
                                if(DEBUG) {
                                    System.out.println("CurrentTimeMillis: " + String.valueOf(System.currentTimeMillis()));
                                    System.out.println("activeLogTime: " + String.valueOf(activeLogTime));
                                }
                                if(isHangUpButtonPressed == false) {
                                    hangUp(manufacturer, model, d);
                                    isHangUpButtonPressed = true;
                                    wait4IdleTimeout = System.currentTimeMillis() + 60 * 1000;  //挂机操作后60秒超时
                                    if(DEBUG) {
                                        System.out.println("hangup actively!");
                                    }
                                }
                                if(DEBUG) {
                                    System.out.println("CurrentTimeMillis: " + String.valueOf(System.currentTimeMillis()));
                                }

                                if(System.currentTimeMillis() >= wait4IdleTimeout) {
                                    //超时没有接通后，且挂机操作完成60秒后，仍未获取到挂机idle特征日志的，则异常
                                    isIdle = true;
                                    isSuccess = false;
                                    clickBombBox();
                                    break;
                                }
                            }
                            line = reader.readLine();
                            if(line == null) {
                                continue;
                            }
                            sa = line.split("\\s+");
                            logTimeStr = year + "-" + sa[0] + " " + sa[1];
                            try {
                                date = format.parse(logTimeStr);
                            } catch (ParseException e) {
                                // TODO: handle exception
                                if(DEBUG) {
                                    System.out.println("Parse log timestamp ERROR! logTimeStr = " + logTimeStr);
                                }
                                continue;
                            }
                            logTime = date.getTime();
                            if((line.contains("GsmConnection") &&
                                    line.contains("onDisconnect")) ||
                                    (line.contains("IMSSenderRxr") &&
                                            line.contains("HANGUP_FOREGROUND_RESUME_BACKGROUND")) ||
                                    (line.contains("ImsPhoneCallTracker") &&  //N1 max VoLTE
                                            line.contains("DISCONNECTED")) ||
                                    (line.contains("RLOG-IMSCONN") && //HTC M8t, no SIM
                                            line.contains("onDisconnect")) ||
                                    (line.contains("ImsPhoneConnection") && //ZTE Axon MINI
                                            line.contains("onDisconnect")) ||
                                    (line.contains("RLOG-RILJ_IMS") &&  //HUAWEI MATE7 主叫挂机
                                            line.contains("HW_IMS_HANGUP_FOREGROUND_RESUME_BACKGROUND")) ||
                                    (line.contains("RLOG-AT") &&  //HUAWEI MATE7 被叫挂机或网络掉话
                                            line.contains("CEND"))) {
                                idleLogTime = logTime;
                                isIdle = true;
                                if(DEBUG) {
                                    System.out.println("Status: ENDCALL");
                                }
                            }
                        }
                        if((!isWaitTimeout) && isHangUpButtonPressed &&
                                ((idleLogTime - activeLogTime) >= (((long)dialDuration)*1000))) {
                            isSuccess = true;
                        } else {
                            isSuccess = false;
                            failureCount++;
//					if (failureCount >= dialMaxFailure) {
//						isEndTest = true;
//					}
                        }
//				sleep(((long)dialDuration)*1000);  //change time from second to millisecond
                        mTool = null;
//				mProcess = null;
                        reader.close();
                        p.destroy();
                        //write send call log
                        als = new ArrayList<String>();
                        //---DIALING log
                        als.add(String.valueOf(dialingLogTime) + ","
                                + logdtformat.format(dialingLogTime) + ","
                                + "DIALING" + ",,,,,");
                        db.execSQL("INSERT INTO sendCallLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ String.valueOf(dialingLogTime),logdtformat.format(dialingLogTime),"DIALING","","","","","","" });
                        //---ALERTING log
                        als.add(String.valueOf(alertingLogTime) + ","
                                + logdtformat.format(alertingLogTime) + ","
                                + "ALERTING" + ",,,,,");
                        db.execSQL("INSERT INTO sendCallLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ String.valueOf(alertingLogTime),logdtformat.format(alertingLogTime),"ALERTING","","","","","","" });
                        //---ACTIVE log
                        als.add(String.valueOf(activeLogTime) + ","
                                + logdtformat.format(activeLogTime) + ","
                                + "ACTIVE" + ",,,,,");
                        db.execSQL("INSERT INTO sendCallLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ String.valueOf(activeLogTime),logdtformat.format(activeLogTime),"ACTIVE","","","","","","" });
                        //---IDLE log
                        als.add(String.valueOf(idleLogTime) + ","
                                + logdtformat.format(idleLogTime) + ","
                                + "IDLE,"
                                + String.valueOf((idleLogTime-activeLogTime)/1000) + "/" + dialDuration + ","
                                + i + ","
                                + dialRptInterval + ","
                                + isSuccess + ","
                                + dialMaxFailure);
                        db.execSQL("INSERT INTO sendCallLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ String.valueOf(idleLogTime),logdtformat.format(idleLogTime),"IDLE",String.valueOf((idleLogTime-activeLogTime)/1000) + "/" + dialDuration,String.valueOf(i),dialRptInterval,isSuccess,dialMaxFailure,dialType });

                        //数据库版本注释以下内容
                        //Tools.appendTXTFile(als, sendCallLogFile);

                        if(isEndTest) {
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("logcat命令执行异常");
                        }
                        //挂机操作

                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("挂机");
                        }
                    }
                    //repeat interval wait
                    long sleepTime = (idleLogTime + dialRptInterval*1000) - System.currentTimeMillis();
                    if(sleepTime > 0) {
                        sleep(sleepTime);
                    }
                }
                break;
            case "2":  //按时间短呼

                break;
            default:  //长呼

        }

        ConfigTest.caseEndTime = System.currentTimeMillis();  //实际测试结束时间
        db.close();
        return true;
    }

    /**
     * 挂机操作。采用反射的方法进行挂机。
     */
    private void hangUp() {
        Class<TelephonyManager> classTM = TelephonyManager.class;
        try {
            Method getITelephonyMethod = classTM.getDeclaredMethod("getITelephony", (Class[]) null);
            getITelephonyMethod.setAccessible(true);
            ITelephony iTelephony = null;
            iTelephony = (ITelephony) getITelephonyMethod.invoke(telephonyManager, (Object[]) null);
            iTelephony.endCall();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
