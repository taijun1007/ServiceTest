package com.cmlab.servicetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Intent;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.cmlab.config.ConfigTest;
import com.cmlab.util.AccessibilityUtil;

import static com.cmlab.config.ConfigTest.weiXinImageCase;

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
        if (event.getSource() != null) {
            int eventType = event.getEventType();
            String eventTypeStr;
            if (ConfigTest.DEBUG) {
                switch (eventType) {
                    case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                        eventTypeStr = "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                        eventTypeStr = "TYPE_VIEW_ACCESSIBILITY_FOCUSED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_CLICKED:
                        eventTypeStr = "TYPE_VIEW_CLICKED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                        eventTypeStr = "TYPE_VIEW_FOCUSED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                        eventTypeStr = "TYPE_VIEW_HOVER_ENTER";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                        eventTypeStr = "TYPE_VIEW_HOVER_EXIT";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                        eventTypeStr = "TYPE_VIEW_LONG_CLICKED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                        eventTypeStr = "TYPE_VIEW_SCROLLED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_SELECTED:
                        eventTypeStr = "TYPE_VIEW_SELECTED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                        eventTypeStr = "TYPE_VIEW_TEXT_CHANGED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                        eventTypeStr = "TYPE_VIEW_TEXT_SELECTION_CHANGED";
                        break;
                    case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                        eventTypeStr = "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY";
                        break;
                    case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                        eventTypeStr = "TYPE_WINDOW_CONTENT_CHANGED";
                        break;
                    case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                        eventTypeStr = "TYPE_WINDOW_STATE_CHANGED";
                        break;
                    case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                        eventTypeStr = "TYPE_WINDOWS_CHANGED";
                        break;
                    case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                        eventTypeStr = "TYPE_ANNOUNCEMENT";
                        break;
                    case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                        eventTypeStr = "TYPE_GESTURE_DETECTION_END";
                        break;
                    case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                        eventTypeStr = "TYPE_GESTURE_DETECTION_START";
                        break;
                    case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                        eventTypeStr = "TYPE_NOTIFICATION_STATE_CHANGED";
                        break;
                    case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                        eventTypeStr = "TYPE_TOUCH_EXPLORATION_GESTURE_END";
                        break;
                    case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                        eventTypeStr = "TYPE_TOUCH_EXPLORATION_GESTURE_START";
                        break;
                    case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                        eventTypeStr = "TYPE_TOUCH_INTERACTION_END";
                        break;
                    case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                        eventTypeStr = "TYPE_TOUCH_INTERACTION_START";
                        break;
                    default:
                        eventTypeStr = "default-no-message";
                }
                Log.i(TAG, "当前事件：" + eventTypeStr);
                Tools.writeLogFile("=================================================");
                Tools.writeLogFile("当前事件：" + eventTypeStr);
            }
            //验证各种操作和变化会引起的事件类型
            switch (eventType) {
                case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                    Toast.makeText(this, "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                    Toast.makeText(this, "TYPE_VIEW_ACCESSIBILITY_FOCUSED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_CLICKED:
                    //屏幕上被监控的APP中可以点击的控件被点击时发生此事件，要点：被监控APP控件可见并可点击
                    //不可点击的控件（比如微信启动时的背景图片）不会触发此事件
//                Toast.makeText(this, "TYPE_VIEW_CLICKED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                    //屏幕上被监控的APP中焦点变到某个控件时发生此事件，比如点击输入框时，焦点变到输入框中
                    //注意：如果屏幕上一开始就显示某个输入框已是焦点，已经可以输入了，此时再点击时，焦点不会变化
                    //这样就只会触发TYPE_VIEW_CLICKED事件，不会触发TYPE_VIEW_FOCUSED事件
                    //此事件只会在焦点发生变化的时候触发
//                Toast.makeText(this, "TYPE_VIEW_FOCUSED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                    Toast.makeText(this, "TYPE_VIEW_HOVER_ENTER", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                    Toast.makeText(this, "TYPE_VIEW_HOVER_EXIT", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                    //屏幕上被监控的APP中可见的允许长按的控件被长按时会触发此事件，比如在文本输入框中长按
                    //输入框中长按时若有文字，还会触发TYPE_VIEW_TEXT_SELECTION_CHANGED事件、TYPE_WINDOW_CONTENT_CHANGED事件
                    //输入框中长按时若没有文字，则只会触发此事件
//                Toast.makeText(this, "TYPE_VIEW_LONG_CLICKED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                    //屏幕上被监控的APP中可滚动的控件发生滚动时触发此事件，无论是APP自己在滚动还是人工滚动都会触发此事件
                    //比如百度音乐的广告bar上就会左右自己滚动，此时也触发此事件
                    //一般发生滚动事件时都会触发TYPE_WINDOW_CONTENT_CHANGED事件
//                Toast.makeText(this, "TYPE_VIEW_SCROLLED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_SELECTED:
                    Toast.makeText(this, "TYPE_VIEW_SELECTED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                    //当输入框中输入或删除文字时触发此事件，每输入或删除一个字符都会触发一次此事件
                    //此事件发生时，一般TYPE_VIEW_TEXT_SELECTION_CHANGED事件会伴随发生
//                Toast.makeText(this, "TYPE_VIEW_TEXT_CHANGED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                    //在输入框中输入、删除、长按选中、取消选中文字时触发此事件
                    //其他非输入框的控件上的文字如果有匹配选择的变化时触发此事件，比如拨号界面输入电话号码时，
                    //号码列表中匹配过滤电话号码，被匹配的数字会像选中一样有颜色的变化
//                Toast.makeText(this, "TYPE_VIEW_TEXT_SELECTION_CHANGED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                    Toast.makeText(this, "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                    //屏幕上被监控的APP显示的内容发生变化时触发此事件
                    //一般触发此事件的操作：打开APP，切换APP的activity，activity的内容变化，控件的变化（颜色、文字、状态等
//                Toast.makeText(this, "TYPE_WINDOW_CONTENT_CHANGED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                    //屏幕上被监控的APP窗口（activity）在打开、切换时，
                    // 弹出对话框和关闭对话框时（相当于activity被挡在对话框后面和activity重新回到前台）触发此事件
                    //注意：关闭APP时不会触发此事件
//                Toast.makeText(this, "TYPE_WINDOW_STATE_CHANGED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                    Toast.makeText(this, "TYPE_WINDOWS_CHANGED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                    Toast.makeText(this, "TYPE_ANNOUNCEMENT", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                    Toast.makeText(this, "TYPE_GESTURE_DETECTION_END", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                    Toast.makeText(this, "TYPE_GESTURE_DETECTION_START", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                    //被监控的APP在顶部下拉消息栏中notification出现和消失，或notification内容变化时触发此事件
//                Toast.makeText(this, "TYPE_NOTIFICATION_STATE_CHANGED", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                    Toast.makeText(this, "TYPE_TOUCH_EXPLORATION_GESTURE_END", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                    Toast.makeText(this, "TYPE_TOUCH_EXPLORATION_GESTURE_START", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                    Toast.makeText(this, "TYPE_TOUCH_INTERACTION_END", Toast.LENGTH_SHORT).show();
                    break;
                case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                    Toast.makeText(this, "TYPE_TOUCH_INTERACTION_START", Toast.LENGTH_SHORT).show();
                    break;
            }
            //分APP进行处理
            String appPackageName = (String) event.getPackageName();
            if (ConfigTest.DEBUG) {
                switch (appPackageName) {
                    case "com.ting.mp3.android": //百度音乐
                        appName = "百度音乐";
                        break;
                    case "air.tv.douyu.android":  //斗鱼
                        appName = "斗鱼";
                        break;
                    case "com.tencent.mm":  //微信
                        appName = "微信";
                        break;
                    case "com.android.phone":  //电话
                        appName = "电话";
                        break;
                    case "com.android.incallui":  //电话
                        appName = "电话";
                        break;
                    case "com.android.dialer":  //电话
                        appName = "电话";
                        break;
                    case "com.android.contacts":  //电话
                        appName = "电话";
                        break;
                    case "me.android.browser":  //WEB浏览器
                        appName = "WEB浏览器";
                        break;
                    case "com.UCMobile":  //UC浏览器
                        appName = "UC浏览器";
                        break;
                    default:
                        appName = "未知APP";
                }
                Log.i(TAG, "当前事件来源：" + appName);
                Tools.writeLogFile("当前事件来源：" + appName);
            }
            switch (appPackageName) {
                case "com.ting.mp3.android": //百度音乐
//                Toast.makeText(this, "百度音乐", Toast.LENGTH_SHORT).show();
                    break;
                case "air.tv.douyu.android":  //斗鱼
//                Toast.makeText(this, "斗鱼", Toast.LENGTH_SHORT).show();
                    break;
                case "com.tencent.mm":  //微信
//                Toast.makeText(this, "微信", Toast.LENGTH_SHORT).show();
                    break;
                case "com.android.phone":  //电话
//                Toast.makeText(this, "电话", Toast.LENGTH_SHORT).show();
                    break;
                case "com.android.incallui":  //电话
//                Toast.makeText(this, "电话", Toast.LENGTH_SHORT).show();
                    break;
                case "com.android.dialer":  //电话
//                Toast.makeText(this, "电话", Toast.LENGTH_SHORT).show();
                    break;
                case "com.android.contacts":  //电话
//                Toast.makeText(this, "电话", Toast.LENGTH_SHORT).show();
                    break;
                case "me.android.browser":  //WEB浏览器
//                Toast.makeText(this, "WEB浏览器", Toast.LENGTH_SHORT).show();
                    break;
                case "com.UCMobile":  //UC浏览器
//                Toast.makeText(this, "UC浏览器", Toast.LENGTH_SHORT).show();
                    break;
            }
            //测试代码-20170518-0959，START，对每一个event都查看其来源界面的全部控件
//            if (ConfigTest.DEBUG) {
//                Tools.writeLogFile("EVENT事件信息------------------------");
//                Tools.writeLogFile("event Class Name: " + event.getClassName());
//                Tools.writeLogFile("event Text: " + event.getText());
//                Tools.writeLogFile("event Package Name: " + event.getPackageName());
//                Tools.writeLogFile("event Content Desc: " + event.getContentDescription());
//                Tools.writeLogFile("event WindowID: " + event.getWindowId());
//                Tools.writeLogFile("event source Class Name: " + event.getSource().getClassName());
//                Tools.writeLogFile("event source Text: " + event.getSource().getText());
//                Tools.writeLogFile("event source ID: " + event.getSource().getViewIdResourceName());
//                Tools.writeLogFile("event source Package Name: " + event.getSource().getPackageName());
//                Tools.writeLogFile("event source Content Desc: " + event.getSource().getContentDescription());
//                Tools.writeLogFile("event source WindowID: " + event.getSource().getWindowId());
//                AccessibilityNodeInfo rootNodeInfo = getRootInActiveWindow();
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (rootNodeInfo == null) {
//                    Tools.writeLogFile("Root Node is NULL!");
//                } else {
//                    recycle(rootNodeInfo);
//                }
//            }
            //测试代码-20170518-0959，END
            //按测试例业务处理
            //处理方法对应uiautomator的jar包测试例，一一对应，一个测试例（比如微信文本）对应一个处理方法
            //处理方法可单独用类及其方法实现，这里只是调用业务处理类的入口方法（可统一名称，可使用抽象类），在相应类中实现业务的全部处理功能
            //建议采用此方法开发，这样可以便于从uiautomator测试例移植代码
            if ((ConfigTest.caseName != null) && ((ConfigTest.isCaseRunning == true) || (ConfigTest.isAppForeground == true))) {
                switch (ConfigTest.caseName) {
                    case "WeiXinText":  //微信文本
                        //判断是否在测试任务执行过程中，若是，则处理事件，否则就是已经完成测试任务但未退出微信，此时不能处理事件
                        if (ConfigTest.isCaseRunning == true) {
                            if (ConfigTest.weiXinTextCase == null) {
                                ConfigTest.weiXinTextCase = new WeiXinTextCase();
                                if (ConfigTest.DEBUG) {
                                    Log.i(TAG, "new WeiXinTextCase()");
                                    Tools.writeLogFile("new WeiXinTextCase()");
                                }
                            }
                            ConfigTest.weiXinTextCase.execute(this, event);
                            if (ConfigTest.DEBUG) {
                                Log.i(TAG, "调用weiXinTextCase.execute方法处理事件");
                                Tools.writeLogFile("调用weiXinTextCase.execute方法处理事件");
                            }
                            //判断是否到了测试任务指定结束时间，若是，则停止执行测试任务，准备退出微信并返回ServiceTest
                            if (System.currentTimeMillis() >= ConfigTest.caseEndTime) {
                                ConfigTest.isCaseRunning = false;
                                if (ConfigTest.DEBUG) {
                                    Log.i(TAG, "到指定任务结束时间，结束测试任务");
                                    Tools.writeLogFile("到指定任务结束时间，结束测试任务");
                                }
                                try {
                                    Thread.sleep(1000);  //任务结束前的最后处理操作可能会引起界面Activity更新切换，时间较长，按回退键前等待一定时间
                                    if (ConfigTest.DEBUG) {
                                        Log.i(TAG, "点击回退键前等待1秒");
                                        Tools.writeLogFile("点击回退键前等待1秒");
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                AccessibilityNodeInfo weixinNode = AccessibilityUtil.findNodeByIdAndText(this, "com.tencent.mm:id/bgu", "微信");
                                if (weixinNode != null) {
                                    weixinNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                    if (ConfigTest.DEBUG) {
                                        Tools.writeLogFile("找到了下方的微信标签，不能点击回退键，改为点击微信标签");
                                    }
                                } else {
                                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                    if (ConfigTest.DEBUG) {
                                        Log.i(TAG, "第一次点击回退键");
                                        Tools.writeLogFile("第一次点击回退键");
                                    }
                                }
                                try {
                                    Thread.sleep(1000);  //同时，按回退键也可能会引起界面Activity更新切换，按回退键后也要等待一定时间
                                    if (ConfigTest.DEBUG) {
                                        Log.i(TAG, "点击后等待1秒");
                                        Tools.writeLogFile("点击后等待1秒");
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            if (isWeiXinItemClicked == false) {
                                /*try {
                                    Thread.sleep(100);  //操作前后的延时似乎不会影响响应事件的多少，动作过后事件已经产生，处于事件队列中等待处理???之前的测试任务操作和回退操作等可能会引起窗口切换，耗时较长，需等待切换完成再找控件，否则可能会因在窗口切换过程中找不到控件而执行多余的回退操作，这样会把ServiceTest也退出
                                    if (ConfigTest.DEBUG) {
                                        Log.i(TAG, "休眠0.1秒");
                                        Tools.writeLogFile("休眠0.1秒");
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }*/
                                AccessibilityNodeInfo serviceTestNode = AccessibilityUtil.findNodeByIdAndPackage(this, "com.cmlab.servicetest:id/appImage", "com.cmlab.servicetest");
                                if (serviceTestNode != null) {
                                    //如果找到了ServiceTest的控件，说明微信已经退出了，不需要再按回退键和弹出ServiceTest了，可以结束本次测试了
                                    ConfigTest.isAppForeground = false;
                                    isWeiXinItemClicked = false;
                                    if (ConfigTest.DEBUG) {
                                        Log.i(TAG, "找到ServiceTest控件，ServiceTest已到前台");
                                        Tools.writeLogFile("找到ServiceTest控件，ServiceTest已到前台");
                                    }
                                } else {
                                    AccessibilityNodeInfo node = AccessibilityUtil.findNodeByIdAndText(this, "com.tencent.mm:id/bgu", "微信");
                                    if (node == null) {
                                        //未找到“微信”菜单就点击回退键
                                        try {
                                            Thread.sleep(1000);  //任务结束前的最后处理操作可能会引起界面Activity更新切换，时间较长，按回退键前等待一定时间
                                            if (ConfigTest.DEBUG) {
                                                Log.i(TAG, "点击回退键前等待1秒");
                                                Tools.writeLogFile("点击回退键前等待1秒");
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                        if (ConfigTest.DEBUG) {
                                            Log.i(TAG, "未找到微信菜单，点击回退键");
                                            Tools.writeLogFile("未找到微信菜单，点击回退键");
                                        }
                                        try {
                                            Thread.sleep(1000);  //同时，按回退键也可能会引起界面Activity更新切换，按回退键后也要等待一定时间
                                            if (ConfigTest.DEBUG) {
                                                Log.i(TAG, "点击回退键后等待1秒");
                                                Tools.writeLogFile("点击回退键后等待1秒");
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    } else {
                                        //找到了“微信”菜单就点击一下“微信”菜单
                                        //"微信"这个菜单TextView是不可点击的，需要点击它的父节点才有效，才能触发TYPE_VIEW_CLICKED事件
                                        //但是要注意，它的父节点clickable属性也是false，它的父节点的父节点（即向上2级）clickable属性是true
                                        // 但是点击它和它的爷爷节点都无效，只有点击它的父节点才有效，很奇怪！！！
                                        node.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                                        ConfigTest.lastAction = ConfigTest.ACTION_CLICK;
                                        isWeiXinItemClicked = true;
                                        if (ConfigTest.DEBUG) {
                                            Log.i(TAG, "找到微信菜单，点击");
                                            Tools.writeLogFile("找到微信菜单，点击");
                                        }
                                    }
                                }
                            } else {
                                //退出微信APP
                                if ((ConfigTest.lastAction == ConfigTest.ACTION_CLICK) &&
                                        (event.getEventType() == AccessibilityEvent.TYPE_VIEW_CLICKED)) {
                                    //一个action可能会引起一系列event，其中有一个必有的event，其他可能都是连锁event
                                    //我们希望一个action对应一个event，从而进行一次处理，不要因一个action导致进行多次处理
                                    //因此需要忽略一些event
                                    try {
                                        Thread.sleep(500);  //等待点击后可能会引起的窗口切换完成，这里也可以不用等待
                                        if (ConfigTest.DEBUG) {
                                            Log.i(TAG, "最后一次点击回退键前等待0.5秒");
                                            Tools.writeLogFile("最后一次点击回退键前等待0.5秒");
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                                    ConfigTest.lastAction = ConfigTest.ACTION_BACK;
                                    ConfigTest.isAppForeground = false;
                                    isWeiXinItemClicked = false;
                                    if (ConfigTest.DEBUG) {
                                        Log.i(TAG, "最后一次点击回退键，退出微信");
                                        Tools.writeLogFile("最后一次点击回退键，退出微信");
                                    }
                                    try {
                                        Thread.sleep(500);  //同时，按回退键也可能会引起界面Activity更新切换，按回退键后也要等待一定时间
                                        if (ConfigTest.DEBUG) {
                                            Log.i(TAG, "最后一次点击回退键后等待0.5秒");
                                            Tools.writeLogFile("最后一次点击回退键后等待0.5秒");
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    //弹回ServiceTest
                                    Intent intent = new Intent(this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  //在Activity之外调用startActivity需要设定FLAG_ACTIVITY_NEW_TASK标志
                                    startActivity(intent);
                                    if (ConfigTest.DEBUG) {
                                        Log.i(TAG, "startActivity, 弹出ServiceTest");
                                        Tools.writeLogFile("startActivity, 弹出ServiceTest");
                                    }
                                }
                            }
                        }
                        break;
                    case "WeiXinImage":  //微信图片
                        //采用第一个触发事件来的时候进入处理，等全部测试任务处理完后再退出，即只进来处理一次就完成全部任务
                        //只需要判断是否在测试任务进行期间即可，即ConfigTest.isCaseRunning是否为true，如果此方案可行，
                        //以后 if ((ConfigTest.caseName != null) && ((ConfigTest.isCaseRunning == true) || (ConfigTest.isAppForeground == true)))
                        //可改为 if (ConfigTest.isCaseRunning == true)
                        //任务结束时修改 ConfigTest.isCaseRunning 为 false
                        if (weiXinImageCase == null) {
                            weiXinImageCase = new WeiXinImageCase();
                            if (ConfigTest.DEBUG) {
                                Tools.writeLogFile("new WeiXinImageCase()");
                            }
                        }
                        if (ConfigTest.DEBUG) {
                            Tools.writeLogFile("开始调用WeiXinImageCase.execute方法执行测试任务...");
                        }
                        boolean result = ConfigTest.weiXinImageCase.execute(this, event);
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
                        break;
                }
            }
        }
    }

    @Override
    public void onInterrupt() {

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
