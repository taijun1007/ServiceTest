package com.cmlab.servicetest;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.cmlab.config.ConfigStatusCode;
import com.cmlab.config.ConfigTest;
import com.cmlab.util.AccessibilityUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * 微信文本，按照指定的业务参数发送微信文本信息，并在指定的sqlite数据库文件中记录log
 * 参数文件：setup.json，parameter.json
 * Created by hunt on 2017/4/21.
 */

public class WeiXinTextCase extends UiautomatorControlCase {

    private static final String TAG = "WeiXinTextCase";

    private String logPath;
    private String parameterFile;
    private String weiXin_Text_DestID;
    private int weiXin_Text_RptTimes;
    private String weiXin_Text_Content;
    private long weiXin_Text_RptInterval;

    public WeiXinTextCase() {
        //初始化
        ConfigTest.isSetupRead = false;
        ConfigTest.isParameterRead = false;
        ConfigTest.isInputSetted = false;
        ConfigTest.currentStatusCode = ConfigStatusCode.WEIXINTEXT_INIT;
        logPath = null;
        parameterFile = null;
        weiXin_Text_DestID = null;
        weiXin_Text_RptTimes = 0;
        weiXin_Text_Content = null;
        weiXin_Text_RptInterval = 0;
    }

    @Override
    public boolean execute(UiControlAccessibilityService context, AccessibilityEvent event) {
        String appPackageName = (String) event.getPackageName();
        //如果当前的事件来源于微信，则处理，否则不进行处理
        if (appPackageName.equals("com.tencent.mm")) {
            //第一次进入执行方法时需要从setup.json中读取配置，只需读取一次后，后续执行任务就不需要再读取了
            //直到下一次新的任务开始时再一次读取
            if (!ConfigTest.isSetupRead) {
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
                ConfigTest.isSetupRead = true;
            }
            //第一次进入执行方法时需要从parameter.json参数文件中读取业务参数，只需读取一次后，后续执行任务就不需要再读取了
            //直到下一次新的任务开始时再一次读取
            if (!ConfigTest.isParameterRead) {
                //如果不知道参数文件在哪则不能读取，且不能执行后续测试任务，返回
                if (parameterFile == null) {
                    File paraFile = new File(parameterFile);
                    if (paraFile.exists()) {
                        JSONArray paraArray = Tools.readJSONFile(parameterFile);
                        if (paraArray != null) {
                            try {
                                JSONObject paraJsonObject = paraArray.getJSONObject(0);
                                weiXin_Text_DestID = paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_TEXT_DESTID);
                                weiXin_Text_RptTimes = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_TEXT_RPTTIMES));
                                weiXin_Text_Content = paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_TEXT_CONTENT);
                                weiXin_Text_RptInterval = 1000 * Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_TEXT_RPTINTERVAL));
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
            }
            //第一次进入执行方法时，需要将输入法改为utf7输入法，避免影响ui操作和识别，设置一次即可，后续测试中不需要再设置
            //直到下一轮测试开始再进行设置
            if (!ConfigTest.isInputSetted) {
                ConfigTest.isInputSetted = setInput(context);
            }
            //打开log数据库文件，如果没有就建立，如果没有WeiXinTextLog数据库表就建立
            //execute方法结尾记得要关闭数据库文件
            SQLiteDatabase db = SQLiteDatabase.openDatabase(logPath + ".db", null, SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.CREATE_IF_NECESSARY);
            db.execSQL("CREATE TABLE IF NOT EXISTS WeiXinTextLog (mili VARCHAR, format VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, result VARCHAR, speed VARCHAR, Tx VARCHAR, Rx VARCHAR)");
            //下面开始执行微信文本发送操作并记录相应log，进行状态判断和迁移
            AccessibilityNodeInfo node = null;
            switch (ConfigTest.currentStatusCode) {
                case ConfigStatusCode.WEIXINTEXT_INIT:  //当前是初始状态
                    try {
                        Thread.sleep(300);  //等待微信完全打开，完成界面切换
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    node = AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/bgu", "微信");
                    if (node != null) {
                        node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        ConfigTest.currentStatusCode = ConfigStatusCode.WEIXINTEXT_TALKLIST;  //下一个状态：进入微信对话列表界面
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("点击左下方\"微信\"。当前状态：初始；下一状态：进入微信对话列表界面");
                        }
                    }
                    break;
                case ConfigStatusCode.WEIXINTEXT_TALKLIST:  //当前状态是进入微信对话列表界面
                    try {
                        Thread.sleep(300); //等完成切换到微信对话列表状态
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    boolean result = AccessibilityUtil.findAndPerformClickByText(context, weiXin_Text_DestID);
                    if (result) {
                        //找到目标人物的已有聊天，点击进入聊天界面
                        ConfigTest.currentStatusCode = ConfigStatusCode.WEIXINTEXT_CHAT;  //下一个状态：进入微信聊天界面
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("找到目标人物已有聊天，点击进入聊天。当前状态：微信对话列表界面；下一状态：进入微信聊天界面");
                        }
                    } else {
                        //在对话列表界面未找到目标人物的聊天记录，点击“通讯录”进入通讯录列表寻找目标人物
                        node = AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/bgu", "通讯录");
                        if (node != null) {
                            node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            ConfigTest.currentStatusCode = ConfigStatusCode.WEIXINTEXT_CONTACT;  //下一个状态：进入通讯录列表界面
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("未找到目标人物已有聊天，进入通讯录。当前状态：微信对话列表界面；下一状态：进入通讯录列表界面");
                            }
                        }
                    }
                    break;
                case ConfigStatusCode.WEIXINTEXT_CONTACT:  //当前状态是进入通讯录列表界面

                    break;
                case ConfigStatusCode.WEIXINTEXT_CHAT:  //当前状态是进入聊天界面

                    break;
            }
            //关闭数据库
            db.close();
            try {
                Thread.sleep(200);  //处理完事件后，等待一定时间，让界面完成响应，再进行后续处理
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 修改系统输入法为utf7输入法
     * @param context Context，用于toast显示信息
     * @return boolean型，true：修改成功；false：修改失败
     */
    private boolean setInput(Context context) {
        boolean result = true;
        //以下使用shell命令方式修改settings.db数据库，需要root权限
        Process settingInput = null;
        try {
            settingInput = Runtime.getRuntime().exec("su");
            OutputStream settingInputoutputStream = settingInput.getOutputStream();
            DataOutputStream settingInputdataOutputStream = new DataOutputStream(settingInputoutputStream);
            settingInputdataOutputStream.writeBytes("settings put secure default_input_method jp.jun_nama.test.utf7ime/.Utf7ImeService");
            settingInputdataOutputStream.flush();
            settingInputdataOutputStream.close();
            settingInputoutputStream.close();
            result = true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "输入法修改失败", Toast.LENGTH_SHORT).show();
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile(e.toString());
                Tools.writeLogFile("输入法修改失败");
            }
            result = false;
        } finally {
            if (settingInput != null) {
                settingInput.destroy();
            }
        }
        return result;
    }
}
