package com.cmlab.servicetest;

import android.accessibilityservice.AccessibilityService;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.cmlab.config.ConfigTest;
import com.cmlab.util.AccessibilityUtil;

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
        int count;
        boolean isGoOn = false;
        for(count = 0; count < 10; count++) {
            try {
                Thread.sleep(1000);  //休眠1秒，等微信完全打开
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/bgu", "微信", ConfigTest.NODE_FATHER)) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到左下角的微信标签，点击");
                }
                isGoOn = true;
                break;
            }
        }
        if (!isGoOn) {
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("10秒内未找到左下角的微信标签，未成功进入微信，终止测试！");
            }
            return false;
        }
        try {
            Thread.sleep(500); //等完成切换到微信对话列表状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/a_7", weiXin_Image_DestID, ConfigTest.NODE_FATHER)) {
            //找到目标人物的已有聊天，点击进入聊天界面
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("找到目标人物已有聊天，点击进入聊天");
            }
            try {
                Thread.sleep(500);  //点击后必须等待一段时间等界面切换完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            //在对话列表界面未找到目标人物的聊天记录，点击“通讯录”进入通讯录列表寻找目标人物
            AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/bgu", "通讯录", ConfigTest.NODE_FATHER);
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("未找到目标人物已有聊天，进入通讯录");
            }
            try {
                Thread.sleep(300);  //等待完成切换到通讯录列表界面
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            node = AccessibilityUtil.findNodeByIdAndClass(context, "com.tencent.mm:id/fp", "android.widget.ListView"); //找到ListView
            while (AccessibilityUtil.findNodeByText(context, "新的朋友") == null) {
                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD); //滚动到最顶端
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("滚动到最顶端");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/gj", weiXin_Image_DestID, ConfigTest.NODE_FATHER)) {
                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD); //未找到目标人物就往下滚动
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到目标人物，往下滚动");
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("找到目标人物，进入详细资料界面");
            }
            try {
                Thread.sleep(300);  //等待完成切换到详细资料界面
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/a7q", "发消息", ConfigTest.NODE_SELF);
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("在详细资料界面点击“发消息”按钮，进入聊天界面");
            }
            try {
                Thread.sleep(500);  //点击后必须等待一段时间等界面切换完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        db.close();
//        Toast.makeText(context, context.getRootInActiveWindow().getPackageName(), Toast.LENGTH_SHORT).show();
        for(count = 0; count < 10; count++) { //退出微信，最多按10次回退键
            node = context.getRootInActiveWindow();
            if (node != null) {
                if (node.getPackageName().equals("com.tencent.mm")) {
                    context.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("仍是微信窗口，按回退键。count==" + count);
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("已退出微信窗口，结束。count==" + count);
                    }
                    break;
                }
            } else {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("获取不到窗口root节点，node==null。count==" + count);
                    Tools.writeLogFile("微信正在退出最后窗口，切换中，可结束操作，完成！");
                }
                break;
            }
        }
        return true;
    }
}
