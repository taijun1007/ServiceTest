package com.cmlab.servicetest;

import android.accessibilityservice.AccessibilityService;
import android.database.sqlite.SQLiteDatabase;
import android.net.TrafficStats;
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
            Thread.sleep(1000); //等完成切换到微信对话列表状态
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/a_7", weiXin_Image_DestID, ConfigTest.NODE_FATHER)) {
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
            while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/gj", weiXin_Image_DestID, ConfigTest.NODE_FATHER)) {
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
        //利用填充的机会，获取目标图片文件夹的图片数量
        boolean isGetPicNum = false;  //是否已经获取图片数量
        int picNum = 0; //目标文件夹中的图片数量
        //发送之前的填充，3张图片
        for(int l = 0; l < 3; l++) {
            while (!AccessibilityUtil.findAndPerformClickByIdAndContentDesc(context, "com.tencent.mm:id/yv", "更多功能按钮，已折叠", ConfigTest.NODE_SELF)) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到-更多功能按钮，已折叠");
                }
                if (!AccessibilityUtil.findAndPerformClickByIdAndContentDesc(context, "com.tencent.mm:id/yv", "更多功能按钮，已展开", ConfigTest.NODE_SELF)) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到-更多功能按钮，已展开");
                    }
                } else {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("找到-更多功能按钮，已展开-点击");
                    }
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("找到-更多功能按钮，已折叠-点击");
            }
            while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/hb", "图片", ConfigTest.NODE_FATHER)) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到图片选项");
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("点击图片");
            }
            while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/c1_", "图片和视频", ConfigTest.NODE_FATHER)) {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("未找到图片和视频选项");
                }
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("点击图片和视频");
            }
            while (AccessibilityUtil.findScrollableNode(context, "android.widget.ListView") == null) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            node = AccessibilityUtil.findScrollableNode(context, "android.widget.ListView");//找可滚动的ListView，滚动寻找指定size的图片文件夹
            if (node != null) {
                //找到了可滚动的ListView，滚动寻找指定size的图片文件夹
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到可滚动的ListView");
                }
                boolean isFoundDestPicFolder = false;
                AccessibilityNodeInfo destFolderNode = null;
                while (!isFoundDestPicFolder) {
                    destFolderNode = AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/c1g", weiXin_Image_Size);
                    if (destFolderNode == null) {
                        node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("未找到指定size的图片文件夹，向下滚动一次");
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (!isGetPicNum) {
                            String picNumString = destFolderNode.getParent().getChild(1).getText().toString();
                            String[] strings = picNumString.split("张");
                            picNum = Integer.valueOf(strings[0]);  //获取目标文件夹中的图片数量
                            isGetPicNum = true;
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("已获取目标文件夹中图片数量：" + picNum);
                            }
                        }
                        destFolderNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        isFoundDestPicFolder = true;
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到指定size的图片文件夹，点击进入");
                }
                //寻找要填充的图片并点击
                while (!AccessibilityUtil.findAndPerformClickByIdAndContentDescContain(context, "com.tencent.mm:id/xe", "图片 " + String.valueOf(l + 1) + ",", ConfigTest.NODE_FATHER)) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到填充图片 " + String.valueOf(l + 1));
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到填充图片 " + String.valueOf(l + 1) + "，点击");
                }
                //是否要发送原图
                if (weiXin_Image_Origin == 1) {
                    //需要发送原图
                    while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/az6", "原图", ConfigTest.NODE_SELF)) {
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("未找到原图文字");
                        }
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("需要发送原图，点击原图文字");
                    }
                }
                while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/ee", "发送", ConfigTest.NODE_SELF)) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到发送按钮");
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("点击发送按钮，发送填充图片 " + String.valueOf(l + 1));
                }
            } else {
//                Toast.makeText(context, "没找到ListView", Toast.LENGTH_SHORT).show();
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("没找到ListView");
                }
            }
        }
        //开始正式发送微信图片
        if (ConfigTest.DEBUG) {
            Tools.writeLogFile("--------------开始正式发送微信图片----------------");
        }
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        db.execSQL("INSERT INTO WeiXinImageLog VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new Object[]{ System.currentTimeMillis(),dateFormat2.format(System.currentTimeMillis()),"com.WenXinImage", "0", "info","WeiXinImage-Test-Started","","","" });
        for(int j = 0; j < weiXin_Image_RptTimes; j++) {
            if (j < (picNum - 3)) {
                //因为当前策略是在图片列表中如果翻到了第j+4张图片出现的话，那么就发送第j+1张图片，因此最后三张图片是不会被发送的
                while (!AccessibilityUtil.findAndPerformClickByIdAndContentDesc(context, "com.tencent.mm:id/yv", "更多功能按钮，已折叠", ConfigTest.NODE_SELF)) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到-更多功能按钮，已折叠");
                    }
                    if (!AccessibilityUtil.findAndPerformClickByIdAndContentDesc(context, "com.tencent.mm:id/yv", "更多功能按钮，已展开", ConfigTest.NODE_SELF)) {
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("未找到-更多功能按钮，已展开");
                        }
                    } else {
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("找到-更多功能按钮，已展开-点击");
                        }
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到-更多功能按钮，已折叠-点击");
                }
                while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/hb", "图片", ConfigTest.NODE_FATHER)) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到图片选项");
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("点击图片");
                }
                while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/c1_", "图片和视频", ConfigTest.NODE_FATHER)) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到图片和视频选项");
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("点击图片和视频");
                }
                while (AccessibilityUtil.findScrollableNode(context, "android.widget.ListView") == null) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                node = AccessibilityUtil.findScrollableNode(context, "android.widget.ListView");//找可滚动的ListView，滚动寻找指定size的图片文件夹
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到可滚动的ListView");
                }
                while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/c1g", weiXin_Image_Size, ConfigTest.NODE_FATHER)) {
                    node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到指定size的图片文件夹，向下滚动一次");
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到指定size的图片文件夹，点击进入");
                }
                //寻找要发送的正式图片并点击
                //注，这里要按照看到第4个再发第一个的策略来发送图片，和jar包策略保持一致，找不到就滚动
                while (AccessibilityUtil.findNodeByIdAndClass(context, "com.tencent.mm:id/c17", "android.widget.GridView") == null) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到图片列表GridView");
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到了图片列表GridView，准备寻找图片发送");
                }
                node = AccessibilityUtil.findNodeByIdAndClass(context, "com.tencent.mm:id/c17", "android.widget.GridView");
                while (AccessibilityUtil.findNodeByIdAndContentDescContain(context, "com.tencent.mm:id/xe", "图片 " + String.valueOf(j + 4) + ",") == null) {
                    node.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到定位图片 " + String.valueOf(j + 4) + "，向下滚动一次");
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                AccessibilityUtil.findAndPerformClickByIdAndContentDescContain(context, "com.tencent.mm:id/xe", "图片 " + String.valueOf(j + 1) + ",", ConfigTest.NODE_FATHER);
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到定位图片 " + String.valueOf(j + 4) + "，点击待发送图片 " + String.valueOf(j + 1));
                }
                //是否要发送原图
                if (weiXin_Image_Origin == 1) {
                    //需要发送原图
                    while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/az6", "原图", ConfigTest.NODE_SELF)) {
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("未找到原图文字");
                        }
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("需要发送原图，点击原图文字");
                    }
                }
                long startTotalTxBytes = TrafficStats.getTotalTxBytes();
                long startTotalRxBytes = TrafficStats.getTotalRxBytes();
                long deltaTxBytes, deltaRxBytes;
                while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/ee", "发送", ConfigTest.NODE_SELF)) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到发送按钮");
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("点击发送按钮，发送待发送图片 " + String.valueOf(j + 1));
                }
                long sendStartTime = System.currentTimeMillis();  //发送图片的时间
                while (AccessibilityUtil.findNodeByIdAndContentDesc(context, "com.tencent.mm:id/a13", "图片") == null) {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("未找到聊天列表中的已发送图片");
                    }
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("找到已发送图片控件，已进入聊天界面");
                }
                int resultCode = 0;  //发送结果码，0-成功；1-超时；2-失败。
                while ((System.currentTimeMillis() - sendStartTime) <= 10 * 1000) {
                    //10秒内如果ProgressBar消失，且没有出现“重发”图标，则成功
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    AccessibilityNodeInfo lastPicNode = findLastPicNode(context);  //找到刚刚发送的最新的图片控件
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("找到了刚刚发送的最新图片控件");
                    }
                    if (lastPicNode.getParent().getChildCount() == 3) {
                        //如果FrameLayout含有3个子节点，即说明出现了ProgressBar
                        //test start
                        /*AccessibilityNodeInfo father = lastPicNode.getParent();
                        AccessibilityNodeInfo c1 = father.getChild(0);
                        Tools.writeLogFile("child 0 classname: " + c1.getClassName().toString());
                        if (c1.getViewIdResourceName() != null) {
                            Tools.writeLogFile("child 0 id: " + c1.getViewIdResourceName().toString());
                        }
                        if (c1.getContentDescription() != null) {
                            Tools.writeLogFile("child 0 desc; " + c1.getContentDescription().toString());
                        }
                        AccessibilityNodeInfo c2 = father.getChild(1);
                        Tools.writeLogFile("child 1 classname: " + c2.getClassName().toString());
                        if (c2.getViewIdResourceName() != null) {
                            Tools.writeLogFile("child 1 id: " + c2.getViewIdResourceName().toString());
                        }
                        if (c2.getContentDescription() != null) {
                            Tools.writeLogFile("child 1 desc; " + c2.getContentDescription().toString());
                        }
                        AccessibilityNodeInfo c3 = father.getChild(2);
                        Tools.writeLogFile("child 2 classname: " + c3.getClassName().toString());
                        if (c3.getViewIdResourceName() != null) {
                            Tools.writeLogFile("child 2 id: " + c3.getViewIdResourceName().toString());
                        }
                        if (c3.getContentDescription() != null) {
                            Tools.writeLogFile("child 2 desc; " + c3.getContentDescription().toString());
                        }*/
                        //test end
                        resultCode = 1;
                    } else if (lastPicNode.getParent().getParent().getChildCount() == 3) {
                        //如果RelativeLayout含有3个子节点，即说明出现了“重发”图片
                        resultCode = 2;
                        break;
                    } else if ((lastPicNode.getParent().getChildCount() == 1) && (lastPicNode.getParent().getParent().getChildCount() == 2)) {
                        //如果FrameLayout含有1个子节点且同时RelativeLayout含有2个子节点，说明不存在ProgressBar和“重发”，发送成功
                        resultCode = 0;
                        break;
                    }
                }
                long endTime = System.currentTimeMillis();  //结束时间
                deltaTxBytes =  TrafficStats.getTotalTxBytes() - startTotalTxBytes;
                deltaRxBytes =  TrafficStats.getTotalRxBytes() - startTotalRxBytes;
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                switch (resultCode) {
                    case 0:  //发送成功
                        db.execSQL("INSERT INTO WeiXinImageLog VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinImage", String.valueOf(j), "info","Success",Long.toString(endTime - sendStartTime), Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("图片发送成功");
                        }
                        break;
                    case 1:  //发送超时
                        db.execSQL("INSERT INTO WeiXinImageLog VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinImage",String.valueOf(j), "Error","OutOfTime",Long.toString(endTime - sendStartTime), Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("图片发送超时");
                        }
                        break;
                    case 2: //发送失败
                        db.execSQL("INSERT INTO WeiXinImageLog VALUES (?, ?, ?, ?, ?, ?, ?, ? ,?)", new Object[]{ System.currentTimeMillis(),dateFormat.format(System.currentTimeMillis()),"com.WenXinImage",String.valueOf(j), "Error","Fail","", Long.toString(deltaTxBytes), Long.toString(deltaRxBytes) });
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("图片发送失败");
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
                //延时指定发送时间间隔
                try {
                    Thread.sleep(weiXin_Image_RptInterval * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                //目标文件夹的可发送图片全部都已发送，结束测试
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("目标文件夹下所有可发送图片都已发送，测试结束");
                }
                break;
            }
        }
        ConfigTest.caseEndTime = System.currentTimeMillis();  //实际测试结束时间
        db.close();
        //删除聊天记录
        context.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
        while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/bgu", "微信", ConfigTest.NODE_FATHER)) {
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("未找到左下角的微信标签");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (ConfigTest.DEBUG) {
            Tools.writeLogFile("找到了左下角的微信标签，点击");
        }
        while (AccessibilityUtil.findNodeByIdAndText(context, "com.tencent.mm:id/a_7", weiXin_Image_DestID) == null) {
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("未找到目标人物的聊天记录");
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (ConfigTest.DEBUG) {
            Tools.writeLogFile("找到目标人物的聊天记录");
        }
        boolean isLongClicked = false;
        while (!AccessibilityUtil.findAndPerformClickByIdAndText(context, "com.tencent.mm:id/do", "删除该聊天", ConfigTest.NODE_FATHER)) {
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("未找到“删除该聊天”，窗口未弹出");
            }
            if (!isLongClicked) {
                AccessibilityUtil.findAndPerformLongClickByIdAndText(context, "com.tencent.mm:id/a_7", weiXin_Image_DestID, ConfigTest.NODE_FATHER);
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

    /**
     * 寻找并返回刚刚发送的最新图片控件（处于屏幕最下方的同类图片控件，它上面的节点RelaytiveLayout的ID是最大的，即聊天内容ListView中的child index最大）
     *
     * @param context UiControlAccessibilityService类型的context
     *
     * @return AccessibilityNodeInfo型，图片控件节点
     */
    private AccessibilityNodeInfo findLastPicNode(UiControlAccessibilityService context) {
        AccessibilityNodeInfo picNode = null;
        AccessibilityNodeInfo lvNode = AccessibilityUtil.findNodeByIdAndClass(context, "com.tencent.mm:id/y1", "android.widget.ListView");//找到聊天内容ListView
        if (lvNode != null) {
            AccessibilityNodeInfo rlNode = lvNode.getChild(lvNode.getChildCount() - 1);  //找到index最大的那个RelativeLayout
            int rlNodeChildNum = rlNode.getChildCount();
            AccessibilityNodeInfo flNode = null;
            int i;
            for(i = 0; i < rlNodeChildNum; i++) {
                if (rlNode.getChild(i).getClassName().toString().equals("android.widget.FrameLayout")) {
                    //找到RelativeLayout下FrameLayout子节点
                    flNode = rlNode.getChild(i);
                    break;
                }
            }
            int flNodeChildNum = flNode.getChildCount();
            for(i = 0; i < flNodeChildNum; i++) {
                if (flNode.getChild(i).getClassName().toString().equals("android.widget.ImageView")) {
                    //找到FrameLayout下图片控件
                    picNode = flNode.getChild(i);
                    break;
                }
            }
            return picNode;
        } else {
            return null;
        }
    }
}

/*发送微信图片的聊天界面的层级关系：
        正常：
        ListView
          RelativeLayout
            FrameLayout
              ImageView（图片）
            ImageView（头像）

        等待发送：
        ListView
          RelativeLayout
            FrameLayout
              ImageView（图片）
              ProgressBar
              TextView
            ImageView（头像）

        发送失败：
        ListView
          RelativeLayout
            ImageView（重发）
            FrameLayout
              ImageView（图片）
            ImageView（头像）*/

    /*int j = lvNode.getChildCount();
    String s = rlNode.getClassName().toString();
    int i = rlNode.getChildCount();
            if (rlNode.getContentDescription() != null) {
                    String c = rlNode.getContentDescription().toString();
                    }
                    String c0 = rlNode.getChild(0).getClassName().toString();
                    AccessibilityNodeInfo c0node = rlNode.getChild(0);
                    String c1 = rlNode.getChild(1).getClassName().toString();
                    String c2 = rlNode.getChild(2).getClassName().toString();
                    AccessibilityNodeInfo c2node = rlNode.getChild(2);
                    AccessibilityNodeInfo flnode = rlNode.getChild(1);
                    int fj = flnode.getChildCount();
                    AccessibilityNodeInfo cf0node = flnode.getChild(0);
                    String cfc0 = cf0node.getClassName().toString();
                    AccessibilityNodeInfo cf1node = flnode.getChild(1);
                    String cfc1 = cf1node.getClassName().toString();
                    AccessibilityNodeInfo cf2node = flnode.getChild(2);
                    String cfc2 = cf2node.getClassName().toString();

                    AccessibilityNodeInfo ll1Node = rlNode.getChild(rlNode.getChildCount() - 1);  //找到下一级的LinearLayout
                    s = ll1Node.getClassName().toString();
                    i = ll1Node.getChildCount();
                    if (ll1Node.getContentDescription() != null) {
                    String c = ll1Node.getContentDescription().toString();
                    }
                    AccessibilityNodeInfo ll2Node = ll1Node.getChild(1);  //找到下一级的LinearLayout
                    s = ll2Node.getClassName().toString();
                    i = ll2Node.getChildCount();
                    if (ll2Node.getContentDescription() != null) {
                    String c = ll2Node.getContentDescription().toString();
                    }
                    AccessibilityNodeInfo ll3Node = ll2Node.getChild(0);  //找到下一级的LinearLayout（index固定为0）
                    s = ll3Node.getClassName().toString();
                    i = ll3Node.getChildCount();
                    if (ll3Node.getContentDescription() != null) {
                    String c = ll3Node.getContentDescription().toString();
                    }
                    AccessibilityNodeInfo flNode = ll3Node.getChild(ll3Node.getChildCount() - 1);  //找到下一级的FrameLayout
                    s = flNode.getClassName().toString();
                    i = flNode.getChildCount();
                    if (flNode.getContentDescription() != null) {
                    String c = flNode.getContentDescription().toString();
                    }
                    picNode = flNode.getChild(1);  //找到下一级的图片控件ImageView(index固定为1），即我们所需要的最新发送的图片控件*/