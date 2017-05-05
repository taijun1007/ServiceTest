package com.cmlab.config;

import com.cmlab.servicetest.WeiXinTextCase;

/**
 * 保存测试相关的全局变量
 * Created by hunt on 2017/4/21.
 */

public class ConfigTest {

    //版本及调试相关配置--------------------------------------------------------------
    /**
     * 版本标志，true：debug版本（会输出log文件）；false：发布版（不会输出log文件）
     */
    public static final boolean DEBUG = true;

    /**
     * debug版本输出log文件
     */
    public static final String logFile = "/sdcard/ServiceTestLog.txt";

    /**
     * 定位位置记录文件
     */
    public static final String locationFile = "/sdcard/ServiceTestLocation.txt";

    //测试相关配置----------------------------------------------------------------------
    //各被监控APP的包名
    /**
     * 微信包名
     */
    public static final String weiXinPackageName = "com.tencent.mm";

    /**
     * 百度音乐包名
     */
    public static final String baiduMusicPackageName = "com.ting.mp3.android";

    /**
     * 斗鱼包名
     */
    public static final String douyuPackageName = "air.tv.douyu.android";

    /**
     * 电话包名1
     */
    public static final String phonePackageName1 = "com.android.phone";

    /**
     * 电话包名2
     */
    public static final String phonePackageName2 = "com.android.incallui";

    /**
     * 电话包名3
     */
    public static final String phonePackageName3 = "com.android.dialer";

    /**
     * 电话包名4
     */
    public static final String phonePackageName4 = "com.android.contacts";

    /**
     * 浏览器包名
     */
    public static final String webBrowserPackageName = "me.android.browser";

    /**
     * UC浏览器包名
     */
    public static final String ucWebBrowserPackageName = "com.UCMobile";

    //测试任务执行控制参数
    /**
     * 测试例名称
     */
    public static String caseName = null;

    /**
     * setup.json参数文件路径
     */
    public static final String setupJsonFile = "/sdcard/testcase/setup.json";

    /**
     * setup.json文件中对应业务日志保存路径的key
     */
    public static final String JSON_KEY_LOGPATH = "logpath";

    /**
     * setup.json文件中对应业务执行参数文件的key
     */
    public static final String JSON_KEY_PARAFILE = "testcaseparafile";

    /**
     * 是否正在执行测试任务
     * true：正在执行测试任务
     * false：未执行/空闲
     */
    public static boolean isCaseRunning = false;

    /**
     * 执行测试任务的被监控APP是否在前台
     * true：APP在前台未退出
     * false：APP已退出
     */
    public static boolean isAppForeground = false;

    /**
     * 是否已读取setup.json文件
     * true：已读取setup.json文件
     * false：未读取setup.json文件
     */
    public static boolean isSetupRead = false;

    /**
     * 是否已读取参数parameter.json文件
     * true：已读取parameter.json文件
     * false：未读取parameter.json文件
     */
    public static boolean isParameterRead = false;

    /**
     * 是否已设置了utf7输入法
     * true：已设置为utf7输入法
     * false：未设置为utf7输入法
     */
    public static boolean isInputSetted = false;

    /**
     * 测试任务开始时间戳，单位ms
     */
    public static long caseStartTime;

    /**
     * 测试任务结束时间戳，单位ms
     */
    public static long caseEndTime;

    /**
     * 上次执行的动作，如click、longclick、scroll等
     */
    public static String lastAction;

    /**
     * 状态机当前状态编码
     */
    public static int currentStatusCode;

    //各种操作识别名称
    /**
     * 点击action
     */
    public static final String ACTION_CLICK = "click";

    /**
     * 长按action
     */
    public static final String ACTION_LONGCLICK = "longclick";

    /**
     * 滚动action
     */
    public static final String ACTION_SCROLL = "scroll";

    /**
     * 按回退键action
     */
    public static final String ACTION_BACK = "back";

    //微信文本业务参数
    /**
     * 业务参数文件中对应微信文本业务的发送对象的key
     */
    public static final String JSON_KEY_WEIXIN_TEXT_DESTID = "WeiXin_Text_DestID";

    /**
     * 业务参数文件中对应微信文本业务的发送重复次数的key
     */
    public static final String JSON_KEY_WEIXIN_TEXT_RPTTIMES = "WeiXin_Text_RptTimes";

    /**
     * 业务参数文件中对应微信文本业务的发送内容的key
     */
    public static final String JSON_KEY_WEIXIN_TEXT_CONTENT = "WeiXin_Text_Content";

    /**
     * 业务参数文件中对应微信文本业务的发送时间间隔的key
     */
    public static final String JSON_KEY_WEIXIN_TEXT_RPTINTERVAL = "WeiXin_Text_RptInterval";

    //操作节点的级别
    /**
     * 匹配节点自身
     */
    public static final int NODE_SELF = 0;

    /**
     * 匹配节点的父节点
     */
    public static final int NODE_FATHER = 1;

    /**
     * 匹配节点的爷爷节点
     */
    public static final int NODE_GRANDFATHER = 2;

    /**
     * 匹配节点的子节点
     */
    public static final int NODE_SON = -1;

    //各种业务执行实体
    /**
     * 微信文本测试例执行实例
     */
    public static WeiXinTextCase weiXinTextCase = null;

}
