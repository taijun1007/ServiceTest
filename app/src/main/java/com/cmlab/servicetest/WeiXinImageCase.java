package com.cmlab.servicetest;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.cmlab.config.ConfigTest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * 微信图片，按照指定的业务参数发送微信图片，并在指定的sqlite数据库文件中记录log
 * 参数文件：setup.json，parameter.json
 * Created by hunt on 2017/6/1.
 */

public class WeiXinImageCase extends UiautomatorControlCase {

    private static final String TAG = "WeiXinImageCase";

    private String logPath = null;
    private String parameterFile = null;
    private String weiXin_Image_DestID = null;
    private int weiXin_Image_RptTimes = 0;
    private String weiXin_Image_Size = null;
    private int weiXin_Image_Num = 0;
    private long weiXin_Image_RptInterval = 0;
    private int weiXin_Image_Origin = 0;  //0-非原图；1-原图

    @Override
    public boolean execute(UiControlAccessibilityService context, AccessibilityEvent event) {
        String appPackageName = event.getPackageName().toString();
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
                        weiXin_Image_DestID = paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_IMAGE_DESTID);
                        weiXin_Image_Num = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_IMAGE_NUM));
                        weiXin_Image_Origin = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_IMAGE_ORIGIN));
                        weiXin_Image_RptInterval = 1000 * Long.parseLong(paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_IMAGE_RPTINTERVAL));
                        weiXin_Image_RptTimes = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_IMAGE_RPTTIMES));
                        weiXin_Image_Size = paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_IMAGE_SIZE);
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
        db.execSQL("CREATE TABLE IF NOT EXISTS WeiXinImageLog (mili VARCHAR, format VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, result VARCHAR, speed VARCHAR, Tx VARCHAR, Rx VARCHAR)");
        //开始执行测试任务
        AccessibilityNodeInfo node = null;

        db.close();
        return true;
    }
}
