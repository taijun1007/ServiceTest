package com.cmlab.servicetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.cmlab.config.ConfigTest;
import com.cmlab.util.AccessibilityUtil;

/**
 * Created by hunt on 2017/4/17.
 */

public class UiControlAccessibilityService extends AccessibilityService {
    private static final String TAG = "UCAS";

    private boolean isWeiXinItemClicked = false;  //微信界面中最下部的“微信”栏目是否被点击，true：被点击；false：未被点击
    private String appName;

    @Override

    protected void onServiceConnected() {
        //初始化配置
        super.onServiceConnected();
        AccessibilityServiceInfo asInfo = new AccessibilityServiceInfo();
        asInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;  //全部事件类型
        asInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        asInfo.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;  //获取控件的resourceid，默认配置是不报的，需要设置
        asInfo.notificationTimeout = 100;
        asInfo.packageNames = new String[]{ConfigTest.baiduMusicPackageName, ConfigTest.douyuPackageName, ConfigTest.weiXinPackageName,
                ConfigTest.phonePackageName1, ConfigTest.phonePackageName2, ConfigTest.phonePackageName3, ConfigTest.phonePackageName4,
                ConfigTest.webBrowserPackageName, ConfigTest.ucWebBrowserPackageName};
        setServiceInfo(asInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //按测试例业务处理
        //处理方法对应uiautomator的jar包测试例，一一对应，一个测试例（比如微信文本）对应一个处理方法
        //处理方法可单独用类及其方法实现，这里只是调用业务处理类的入口方法（可统一名称，可使用抽象类），在相应类中实现业务的全部处理功能
        //建议采用此方法开发，这样可以便于从uiautomator测试例移植代码
        //整个运行期间包括2个阶段：
        //1-测试任务执行阶段（启动APP执行操作并获取指标log，最后回到APP的一个标准界面，如微信的聊天列表界面，此时不退出APP，
        //  在执行阶段结束前，在标准界面执行一个标准动作，如在微信的聊天列表界面，点击一下右下角的“我”标签，触发一个唯一可识别的event，
        //  即该event在整个测试阶段都不会出现，这个event一般应该是未处理event队列中的最后一个，若有几个连锁反应的跟随event也无所谓。）
        //2-退出APP阶段（在APP的标准界面等待捕获这个唯一可识别event，之前的其他所有event都不进行处理，只有当捕获到唯一可识别的event时，
        //  开始进行退出APP操作（如点返回键），即使后续还有event，也不多了，应该不会使得切换APP时的黑屏时间太长。）
        //只有开始测试任务后，才会依次进入执行阶段和退出APP阶段，平时手动操作APP时产生的event不会进入任何阶段，全部被忽略。
        if (ConfigTest.isCaseRunning == true) { //1-测试任务执行阶段
            boolean result;
            switch (ConfigTest.caseName) {
                case ConfigTest.WEIXIN_TEXT_CASENAME:  //微信文本
                    //修改微信文本的执行方式，不再使用状态机模式，改为微信图片的模式
                    if (ConfigTest.weiXinTextCase == null) {
                        ConfigTest.weiXinTextCase = new WeiXinTextCase();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("new WeiXinTextCase()");
                        }
                    }
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("开始调用WeiXinTextCase.execute方法执行测试任务...");
                    }
                    result = ConfigTest.weiXinTextCase.execute(this, event);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("测试任务执行完毕！");
                    }
                    if (result) {
                        Toast.makeText(this, "测试任务执行成功！", Toast.LENGTH_SHORT).show();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("测试任务执行成功！");
                        }
                    } else {
                        Toast.makeText(this, "测试任务执行失败！", Toast.LENGTH_SHORT).show();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("测试任务执行失败！");
                        }
                    }
                    ConfigTest.isCaseRunning = false;
                    ConfigTest.isExitingAPP = true;
                    //点击右下角的“我”，触发唯一可识别的event
                    while (!AccessibilityUtil.findAndPerformClickByIdAndText(this, "com.tencent.mm:id/bgu", "我", ConfigTest.NODE_FATHER)) {
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("未找到“我”标签");
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(this, "进入退出APP阶段", Toast.LENGTH_SHORT).show();
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("找到了“我”标签，点击，触发唯一可识别event，准备进入退出APP阶段");
                    }
                    break;
                case ConfigTest.WEIXIN_IMAGE_CASENAME:  //微信图片
                    //采用第一个触发事件来的时候进入处理，等全部测试任务处理完后再退出，即只进来处理一次就完成全部任务
                    //只需要判断是否在测试任务进行期间即可，即ConfigTest.isCaseRunning是否为true，如果此方案可行，
                    //以后 if ((ConfigTest.caseName != null) && ((ConfigTest.isCaseRunning == true) || (ConfigTest.isAppForeground == true)))
                    //可改为 if (ConfigTest.isCaseRunning == true)
                    //任务结束时修改 ConfigTest.isCaseRunning 为 false
                    if (ConfigTest.weiXinImageCase == null) {
                        ConfigTest.weiXinImageCase = new WeiXinImageCase();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("new WeiXinImageCase()");
                        }
                    }
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("开始调用WeiXinImageCase.execute方法执行测试任务...");
                    }
                    result = ConfigTest.weiXinImageCase.execute(this, event);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("测试任务执行完毕！");
                    }
                    if (result) {
                        Toast.makeText(this, "测试任务执行成功！", Toast.LENGTH_SHORT).show();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("测试任务执行成功！");
                        }
                    } else {
                        Toast.makeText(this, "测试任务执行失败！", Toast.LENGTH_SHORT).show();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("测试任务执行失败！");
                        }
                    }
                    ConfigTest.isCaseRunning = false;
                    ConfigTest.isExitingAPP = true;
                    //点击右下角的“我”，触发唯一可识别的event
                    while (!AccessibilityUtil.findAndPerformClickByIdAndText(this, "com.tencent.mm:id/bgu", "我", ConfigTest.NODE_FATHER)) {
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("未找到“我”标签");
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(this, "进入退出APP阶段", Toast.LENGTH_SHORT).show();
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("找到了“我”标签，点击，触发唯一可识别event，准备进入退出APP阶段");
                    }
                    break;
                case ConfigTest.MOCALL_CASENAME:  //打电话（主叫）
                    if (ConfigTest.moCallCaseAccess == null) {
                        ConfigTest.moCallCaseAccess = new MOCallCaseAccess();
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("new MOCallCaseAccess()");
                        }
                    }
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("开始调用MOCallCaseAccess.execute方法处理打电话时的弹框...");
                    }
                    ConfigTest.moCallCaseAccess.execute(this, event);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("弹框处理完毕！");
                    }
                    break;
            }
        } else if (ConfigTest.isExitingAPP == true) {//2-退出APP阶段
            AccessibilityNodeInfo eventNode, childNode;
            switch (ConfigTest.caseName) {
                case ConfigTest.WEIXIN_TEXT_CASENAME:  //微信文本
                    eventNode = event.getSource();
                    if (eventNode != null) {
                        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                            //点击“我”标签会产生一个click事件
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("当前event是特征事件（click）");
                            }
                            int childNum = eventNode.getChildCount();
                            for(int i = 0; i < childNum; i++) {
                                childNode = eventNode.getChild(i);
                                if (childNode != null) {
                                    if ((childNode.getClassName() != null) &&
                                            (childNode.getViewIdResourceName() != null) &&
                                            (childNode.getText() != null)) {
                                        if ((childNode.getClassName().toString().equals("android.widget.TextView")) &&
                                                (childNode.getViewIdResourceName().equals("com.tencent.mm:id/bgu")) &&
                                                (childNode.getText().toString().equals("我"))) {
                                            //找到了触发的唯一可识别event
                                            if (ConfigTest.DEBUG) {
                                                Tools.writeLogFile("找到了唯一可识别event（点击“我”标签）");
                                            }
                                            exitAPP(ConfigTest.caseName);
                                            ConfigTest.isExitingAPP = false;
                                            if (ConfigTest.DEBUG) {
                                                Tools.writeLogFile("退出APP阶段结束");
                                            }
                                            Toast.makeText(this, "测试结束并安全退出 微信", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                } else {
                                    if (ConfigTest.DEBUG) {
                                        Tools.writeLogFile("child node " + i + " 为NULL");
                                    }
                                }
                            }
                        } else {
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("当前event不是特征event（click事件）");
                            }
                        }
                    } else {
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("Event 来源为 NULL");
                        }
                    }
                    break;
                case ConfigTest.WEIXIN_IMAGE_CASENAME:  //微信图片
                    eventNode = event.getSource();
                    if (eventNode != null) {
                        if (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED) {
                            //点击“我”标签会产生一个click事件
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("当前event是特征事件（click）");
                            }
                            int childNum = eventNode.getChildCount();
                            for(int i = 0; i < childNum; i++) {
                                childNode = eventNode.getChild(i);
                                if (childNode != null) {
                                    if ((childNode.getClassName() != null) &&
                                            (childNode.getViewIdResourceName() != null) &&
                                            (childNode.getText() != null)) {
                                        if ((childNode.getClassName().toString().equals("android.widget.TextView")) &&
                                                (childNode.getViewIdResourceName().equals("com.tencent.mm:id/bgu")) &&
                                                (childNode.getText().toString().equals("我"))) {
                                            //找到了触发的唯一可识别event
                                            if (ConfigTest.DEBUG) {
                                                Tools.writeLogFile("找到了唯一可识别event（点击“我”标签）");
                                            }
                                            exitAPP(ConfigTest.caseName);
                                            ConfigTest.isExitingAPP = false;
                                            if (ConfigTest.DEBUG) {
                                                Tools.writeLogFile("退出APP阶段结束");
                                            }
                                            Toast.makeText(this, "测试结束并安全退出 微信", Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                    }
                                } else {
                                    if (ConfigTest.DEBUG) {
                                        Tools.writeLogFile("child node " + i + " 为NULL");
                                    }
                                }
                            }
                        } else {
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("当前event不是特征event（click事件）");
                            }
                        }
                    } else {
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("Event 来源为 NULL");
                        }
                    }
                    break;
            }
        }
    }
    //N2在结束退出微信的过程中，完成第一个事件处理后，切换过程中会出现黑屏的阶段，这个过程的时间随着测试任务的时间变化，
    //测试任务时间越长，黑屏的时间越长，如1分钟的任务黑屏2秒，5分钟的任务黑屏5秒，10分钟的任务黑屏14秒，
    //初步分析可能是后续未处理的大量event事件在退出微信的切换过程中大量涌入处理，处理事件的操作耗时影响拖慢了微信的退出，造成了黑屏，
    //可能存在的风险就是过长时间如果不能完成退出微信，可能会被系统认为程序无响应，从而会弹框，造成程序崩溃
    //需要在退出微信前完成大量event事件的处理，初步方法：
    //1、取消非任务阶段的所有log输出，所有event事件全部无操作通过
    //2、退出微信的最后阶段，在微信界面，采用类似状态机的方式，处理完大部分event，可以留一部分event不处理，减少黑屏时间，识别特定event
    //3、...

    @Override
    public void onInterrupt() {

    }

    /**
     * 退出APP。根据测试任务名退出相应的APP，退出期间释放掉事件队列中未处理的大量event，只在捕获到唯一可识别的event后才进行退出APP操作，如按返回键。
     *
     * @param caseName String型，测试任务名
     *
     * @return boolean型，true：退出成功；false：退出失败
     */
    private boolean exitAPP(String caseName) {
        boolean result;
        if (caseName != null) {
            String packageName; //APP的包名
            String appName; //APP的应用名
            switch (caseName) {
                case ConfigTest.WEIXIN_TEXT_CASENAME:
                case ConfigTest.WEIXIN_IMAGE_CASENAME:
                    packageName = ConfigTest.weiXinPackageName;
                    appName = ConfigTest.weiXinAPPName;
                    pressBackToExit(packageName, appName);
                    result = true;
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("成功退出微信");
                    }
                    break;
                default:
                    result = false;
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("输入的测试任务名有误");
                    }
            }
        } else {
            result = false;
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("输入的APP名为NULL");
            }
        }
        return result;
    }

    /**
     * 按返回键退出APP。适用于只需要按返回键就可以退出的APP，如微信、斗鱼，最多按10下返回键。
     *
     * @param packageName String型，APP的包名
     * @param appName     String型，APP的应用名
     */
    private void pressBackToExit(String packageName, String appName) {
        AccessibilityNodeInfo node;
        int count;
        for(count = 0; count < 10; count++) { //最多按10次回退键
            node = getRootInActiveWindow();
            if (node != null) {
                if (node.getPackageName().equals(packageName)) {
                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("仍是 " + appName + " 窗口，按回退键。count==" + count);
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (ConfigTest.DEBUG) {
                        Tools.writeLogFile("已退出 " + appName + " 窗口，结束。count==" + count);
                    }
                    break;
                }
            } else {
                if (ConfigTest.DEBUG) {
                    Tools.writeLogFile("获取不到窗口root节点，node==null。count==" + count);
                    Tools.writeLogFile(appName + " 正在退出最后窗口，切换中，可结束操作，完成！");
                }
                break;
            }
        }
    }

    private void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            Tools.writeLogFile("Child Widget------------------------------------");
            Tools.writeLogFile("Class Name: " + info.getClassName());
            Tools.writeLogFile("Text: " + info.getText());
            Tools.writeLogFile("ID: " + info.getViewIdResourceName());
            Tools.writeLogFile("Package Name: " + info.getPackageName());
            Tools.writeLogFile("Content Desc: " + info.getContentDescription());
            Tools.writeLogFile("WindowID: " + info.getWindowId());
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if(info.getChild(i)!=null){
                    recycle(info.getChild(i));
                }
            }
        }
    }

}
