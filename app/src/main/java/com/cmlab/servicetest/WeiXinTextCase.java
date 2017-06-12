package com.cmlab.servicetest;

import android.accessibilityservice.AccessibilityService;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
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
import java.text.SimpleDateFormat;

/**
 * 微信文本，按照指定的业务参数发送微信文本信息，并在指定的sqlite数据库文件中记录log
 * 参照微信图片的发送机制重新改写
 * 参数文件：setup.json，parameter.json
 * Created by hunt on 2017/6/10.
 */

public class WeiXinTextCase extends UiautomatorControlCase {

    private static final String TAG = "WeiXinTextCase";

    private String logPath;
    private String parameterFile;
    private String weiXin_Text_DestID;
    private int weiXin_Text_RptTimes;
    private String weiXin_Text_Content;
    private int weiXin_Text_RptInterval;
    private int weiXin_Text_Count;

    @Override
    public boolean execute(UiControlAccessibilityService context, AccessibilityEvent event) {
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
                        weiXin_Text_DestID = paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_TEXT_DESTID);
                        weiXin_Text_RptTimes = Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_TEXT_RPTTIMES));
                        weiXin_Text_Content = paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_TEXT_CONTENT);
                        weiXin_Text_RptInterval = 1000 * Integer.parseInt(paraJsonObject.getString(ConfigTest.JSON_KEY_WEIXIN_TEXT_RPTINTERVAL));
                        weiXin_Text_Count = 0;
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
        db.execSQL("CREATE TABLE IF NOT EXISTS WeiXinTextLog (mili VARCHAR, format VARCHAR, classname VARCHAR, sequence VARCHAR, level VARCHAR, result VARCHAR, speed VARCHAR, Tx VARCHAR, Rx VARCHAR)");
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
            } else {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到左下角的微信标签");
                }
            }
        }
        if (!isGoOn) {
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("10秒内未找到左下角的微信标签，未成功进入微信，终止测试！");
            }
            return false;
        }
        try {
            Thread.sleep(1000); //等完成切换到微信对话列表状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //删除聊天记录，避免下一次测试开始时检测ProgressBar状态出错（会产生误判，无网切换到有网，再次进行测试，就会将成功误判为超时），
        //初步分析，后续未处理的事件消息event会大量涌来，会持续一段时间，一般测试完后要等一段时间再进行下一轮测试，
        //顺便避免app发生无响应情况，无网时未发出的消息状态在恢复有网后，再进入微信时，虽然消息会变为发送成功，但之前未发送出去的状态可能会有影响，
        //有时候等很长一段时间后再进行测试就不会影响，为了避免需要未知的长时间等待，因此删除聊天记录
        int findCount = 1;  //如果聊天记录已被人工删除，则最多循环4次（2秒）就不再寻找
        while (AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/a_7", weiXin_Text_DestID) == null) {
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("未找到目标人物的聊天记录");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (findCount >= 4) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("2秒内未找到目标人物聊天记录，可能已被删除");
                }
                break;
            } else {
                findCount++;
            }
        }
        if (AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/a_7", weiXin_Text_DestID) != null) {
            //找到目标任务聊天记录，删除聊天记录
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("找到目标人物的聊天记录");
            }
            boolean isLongClicked = false;
            while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/do", "删除该聊天", ConfigTest.NODE_FATHER)) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到“删除该聊天”，窗口未弹出");
                }
                if (!isLongClicked) {
                    AccessibilityUtil.findAndPerformLongClickByIdAndText(context, "com.tencent.mm:id/a_7", weiXin_Text_DestID, ConfigTest.NODE_FATHER);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("长按目标人物的聊天记录");
                    }
                    isLongClicked = true;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/bgu", "微信") == null) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("窗口已弹出，看不见左下角微信标签");
                    }
                } else {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("窗口未弹出，可以看见左下角微信标签");
                    }
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("窗口已弹出，找到“删除该聊天”，点击");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/bgu", "微信") == null) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未完成删除操作，请稍候……");
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("已完成删除操作");
            }
        }
        if (AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/a_7", weiXin_Text_DestID, ConfigTest.NODE_FATHER)) {
            //找到目标人物的已有聊天，点击进入聊天界面
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("找到目标人物已有聊天，点击进入聊天");
            }
            try {
                Thread.sleep(1000);  //点击后必须等待一段时间等界面切换完成
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
                Thread.sleep(1000);  //等待完成切换到通讯录列表界面
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
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/gj", weiXin_Text_DestID, ConfigTest.NODE_FATHER)) {
                node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD); //未找到目标人物就往下滚动
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到目标人物，往下滚动");
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("找到目标人物，进入详细资料界面");
            }
            try {
                Thread.sleep(600);  //等待完成切换到详细资料界面
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/a7q", "发消息", ConfigTest.NODE_SELF);
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("在详细资料界面点击“发消息”按钮，进入聊天界面");
            }
            try {
                Thread.sleep(1000);  //点击后必须等待一段时间等界面切换完成
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //开始发送文本信息
        if (ConfigTest.DEBUG) {
            Tools.writeLogFile("-------------开始发送文本信息-----------------");
        }
        while (weiXin_Text_Count < weiXin_Text_RptTimes) {
            while (AccessibilityUtil.findNodeByIdAndClass(context, "com.tencent.mm:id/yq", "android.widget.EditText") == null) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到输入框");
                }
                if (!AccessibilityUtil.findAndPerformClickByIdAndContentDescContain(context, "com.tencent.mm:id/yo", "切换到键盘", ConfigTest.NODE_SELF)) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到“切换到键盘”按钮");
                    }
                } else {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("切换到键盘");
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    context.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("弹出键盘，按回退键使键盘消失");
                    }
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("找到输入框");
            }
            long startTotalTxBytes, startTotalRxBytes;
            long deltaTxBytes,deltaRxBytes;
            node = AccessibilityUtil.findNodeByIdAndClass(context, "com.tencent.mm:id/yq", "android.widget.EditText");
            // android>21 = 5.0时可以用ACTION_SET_TEXT
            // android>18 3.0.1可以通过复制的手段,先确定焦点，再粘贴ACTION_PASTE
            long timeStamp = System.currentTimeMillis();  //把时间戳添加到文本内容中，使发送内容具有唯一性，每次的发送内容都不同
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                //API level < 21
                // 使用剪切板
                ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("text", weiXin_Text_Content + "-" + String.valueOf(timeStamp) + "-" + String.valueOf(weiXin_Text_Count + 1) + "-");
                clipboardManager.setPrimaryClip(clipData);
                //焦点
                node.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("设置文本输入框焦点");
                }
                //粘贴
                node.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("在文本输入框中粘贴内容");
                }
            } else {
                //API level >= 21
                Bundle arguments = new Bundle();
                arguments.putString(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, weiXin_Text_Content + "-" + String.valueOf(timeStamp) + "-" + String.valueOf(weiXin_Text_Count + 1) + "-");
                node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("在文本输入框中输入内容");
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("输入文本："  + weiXin_Text_Content + "-" + String.valueOf(timeStamp) + "-" + String.valueOf(weiXin_Text_Count + 1) + "-");
            }
            startTotalTxBytes = TrafficStats.getTotalTxBytes();  //发送信息前设备端口Tx字节数
            startTotalRxBytes = TrafficStats.getTotalRxBytes();  //发送信息前设备端口Rx字节数
            while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/yw", "发送", ConfigTest.NODE_SELF)) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到发送按钮");
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            long startMili = System.currentTimeMillis();  //开始发送的时间戳
            long endMili = 0;
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("找到发送按钮，点击发送信息");
            }
            long waitEndTimeStamp = startMili + ConfigTest.WEIXIN_TEXT_WAIT_TIMEOUT * 1000;  //等待超时结束时间戳
            int resultCode = 0;  //发送结果码，0-成功；1-超时；2-失败。
            while (System.currentTimeMillis() <= waitEndTimeStamp) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                node = AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/gr", weiXin_Text_Content + "-" + String.valueOf(timeStamp) + "-" + String.valueOf(weiXin_Text_Count + 1) + "-");
                if (node != null) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("找到指定文本信息：" + weiXin_Text_Content + "-" + String.valueOf(timeStamp) + "-" + String.valueOf(weiXin_Text_Count + 1) + "-");
                    }
                    //遍历文本信息同级的节点，寻找是否存在ProgressBar或ImageView（重发）
                    resultCode = 0;
                    int childNum = node.getParent().getChildCount();
                    //测试代码，START
//                    if (ConfigTest.DEBUG) {
//                        Tools.writeLogFile("childNum: " + childNum);
//                        AccessibilityNodeInfo cn;
//                        for(int j = 0; j < childNum; j++) {
//                            cn = node.getParent().getChild(j);
//                            Tools.writeLogFile("child node " + j + " classname: " + cn.getClassName().toString());
//                            if (cn.getText() != null) {
//                                Tools.writeLogFile("child node " + j + " text: " + cn.getText().toString());
//                            }
//                            if (cn.getViewIdResourceName() != null) {
//                                Tools.writeLogFile("child node " + j + " id: " + cn.getViewIdResourceName().toString());
//                            }
//                            if (cn.getContentDescription() != null) {
//                                Tools.writeLogFile("child node " + j + " desc: " + cn.getContentDescription().toString());
//                            }
//                        }
//                    }
                    //测试代码，END
                    //与文本信息同级的节点总共可能有5个：
                    //1：时间（TextView，id为com.tencent.mm:id/k，内容如“19:22”）
                    //2：进度（ProgressBar，id为com.tencent.mm:id/a2y）
                    //3：重发图标（ImageView，id为com.tencent.mm:id/a2f）
                    //4：文本信息（TextView，id为com.tencent.mm:id/gr）
                    //5：头像（ImageView，id为com.tencent.mm:id/gp）
                    //进度和重发图标不会同时出现，所以同级节点数量可能为2、3或4
                    if (childNum == 2) {
                        //同级节点数最少为2，即文本信息和头像
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("同级节点只有文本信息和头像");
                        }
                        resultCode = 0;
                        break;
                    }
                    AccessibilityNodeInfo childNode;
                    for(int i = 0; i < childNum; i++) {
                        childNode = node.getParent().getChild(i);
                        if (childNode.getClassName().toString().equals("android.widget.ProgressBar")) {
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("找到了ProgressBar");
                            }
                            resultCode = 1;
                            break;
                        } else if (childNode.getClassName().toString().equals("android.widget.ImageView") &&
                                childNode.getContentDescription().toString().equals("重发")) {
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("找到了“重发”图标");
                            }
                            resultCode = 2;
                            break;
                        }
                    }
                    if ((resultCode == 2) || (resultCode == 0)) {
                        break;
                    }
                } else {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到刚发送的最新信息");
                    }
                }
            }
            //根据发送结果记录log
            endMili = System.currentTimeMillis();  //发送成功的时间戳
            deltaTxBytes =  TrafficStats.getTotalTxBytes() - startTotalTxBytes;
            deltaRxBytes =  TrafficStats.getTotalRxBytes() - startTotalRxBytes;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            switch (resultCode) {
                case 0:  //发送成功
                    db.execSQL("INSERT INTO WeiXinTextLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinText", String.valueOf(weiXin_Text_Count + 1), "info","Success",Long.toString(endMili - startMili),Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("发送成功，记录log！");
                    }
                    break;
                case 1:  //发送超时
                    db.execSQL("INSERT INTO WeiXinTextLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinText",String.valueOf(weiXin_Text_Count + 1), "Error","OutOfTime",Long.toString(endMili - startMili),Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("发送超时，记录log！");
                    }
                    break;
                case 2:  //发送失败
                    db.execSQL("INSERT INTO WeiXinTextLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinText",String.valueOf(weiXin_Text_Count + 1), "Error","Fail","",Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("发送失败，记录log！");
                    }
                    break;
            }
            //判断是否到达指定测试时长，如果到了则需要退出循环，如果未到时间则继续循环
            if (System.currentTimeMillis() >= ConfigTest.caseEndTime) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("到达指定测试时长，结束本次测试");
                }
                break;
            }
            //发送数+1并等待指定发送间隔
            weiXin_Text_Count++;
            try {
                Thread.sleep(weiXin_Text_RptInterval * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ConfigTest.caseEndTime = System.currentTimeMillis();  //实际测试结束时间
        db.close();
        //退出到微信聊天列表界面，为了后续点击“我”触发特征event
        context.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        Toast.makeText(context, context.getRootInActiveWindow().getPackageName(), Toast.LENGTH_SHORT).show();
        //退出操作改为在accessbilityService中进行，为了释放未处理的event
//        for(count = 0; count < 10; count++) { //退出微信，最多按10次回退键
//            node = context.getRootInActiveWindow();
//            if (node != null) {
//                if (node.getPackageName().equals("com.tencent.mm")) {
//                    context.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
//                    if (ConfigTest.DEBUG) {
//                        Tools.writeLogFile("仍是微信窗口，按回退键。count==" + count);
//                    }
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    if (ConfigTest.DEBUG) {
//                        Tools.writeLogFile("已退出微信窗口，结束。count==" + count);
//                    }
//                    break;
//                }
//            } else {
//                if (ConfigTest.DEBUG) {
//                    Tools.writeLogFile("获取不到窗口root节点，node==null。count==" + count);
//                    Tools.writeLogFile("微信正在退出最后窗口，切换中，可结束操作，完成！");
//                }
//                break;
//            }
//        }
        return true;
    }
}
