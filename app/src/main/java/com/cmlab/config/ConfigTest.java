package com.cmlab.config;

import com.cmlab.servicetest.MOCallCase;
import com.cmlab.servicetest.MOCallCaseAccess;
import com.cmlab.servicetest.WeiXinImageCase;
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
    //各被监控APP的包名和应用名
    /**
     * 微信包名和应用名
     */
    public static final String weiXinPackageName = "com.tencent.mm";
    public static final String weiXinAPPName = "微信";

    /**
     * 百度音乐包名和应用名
     */
    public static final String baiduMusicPackageName = "com.ting.mp3.android";
    public static final String baiduMusicAPPName = "百度音乐";

    /**
     * 斗鱼包名和应用名
     */
    public static final String douyuPackageName = "air.tv.douyu.android";
    public static final String douyuAPPName = "斗鱼";

    /**
     * 电话包名和应用名1
     */
    public static final String phonePackageName1 = "com.android.phone";
    public static final String phoneAPPName1 = "电话";

    /**
     * 电话包名和应用名2
     */
    public static final String phonePackageName2 = "com.android.incallui";
    public static final String phoneAPPName2 = "电话";

    /**
     * 电话包名和应用名3
     */
    public static final String phonePackageName3 = "com.android.dialer";
    public static final String phoneAPPName3 = "电话";

    /**
     * 电话包名和应用名4
     */
    public static final String phonePackageName4 = "com.android.contacts";
    public static final String phoneAPPName4 = "电话";

    /**
     * 浏览器包名和应用名
     */
    public static final String webBrowserPackageName = "me.android.browser";
    public static final String webBrowserAPPName = "浏览器";

    /**
     * UC浏览器包名和应用名
     */
    public static final String ucWebBrowserPackageName = "com.UCMobile";
    public static final String ucWebBrowserAPPName = "UC浏览器";

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
     * 是否收到平台下发的主动停止命令
     * true：收到命令，停止测试
     * false：未收到命令，继续测试
     */
    public static boolean isStopCMDReceived = false;

    /**
     * 是否正在执行测试任务
     * true：正在执行测试任务
     * false：未执行/空闲
     */
    public static boolean isCaseRunning = false;

    /**
     * 是否正在退出APP
     * true：正在退出APP
     * false：空闲
     */
    public static boolean isExitingAPP = false;

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

    //测试任务CaseName
    /**
     * 微信文本CaseName
     */
    public static final String WEIXIN_TEXT_CASENAME = "WeiXinText";

    /**
     * 微信图片CaseName
     */
    public static final String WEIXIN_IMAGE_CASENAME = "WeiXinImage";

    /**
     * 打电话（主叫）CaseName
     */
    public static final String MOCALL_CASENAME = "mocall";

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

    /**
     * 微信文本等待发送成功、超时或失败的超时时间，单位：秒
     */
    public static final int WEIXIN_TEXT_WAIT_TIMEOUT = 10;

    //微信图片业务参数
    /**
     * 业务参数文件中对应微信图片业务的发送对象的key
     */
    public static final String JSON_KEY_WEIXIN_IMAGE_DESTID = "WeiXin_Image_DestID";

    /**
     * 业务参数文件中对应微信图片业务的发送次数的key
     */
    public static final String JSON_KEY_WEIXIN_IMAGE_RPTTIMES = "WeiXin_Image_RptTimes";

    /**
     * 业务参数文件中对应微信图片业务的图片大小/尺寸的key
     */
    public static final String JSON_KEY_WEIXIN_IMAGE_SIZE = "WeiXin_Image_Size";

    /**
     * 业务参数文件中对应微信图片业务的图片数量、编号或范围的key
     */
    public static final String JSON_KEY_WEIXIN_IMAGE_NUM = "WeiXin_Image_Num";

    /**
     * 业务参数文件中对应微信图片业务的发送间隔的key
     */
    public static final String JSON_KEY_WEIXIN_IMAGE_RPTINTERVAL = "WeiXin_Image_RptInterval";

    /**
     * 业务参数文件中对应微信图片业务的是否发送原图的key
     */
    public static final String JSON_KEY_WEIXIN_IMAGE_ORIGIN = "WeiXin_Image_Origin";

    //打电话（主叫）业务参数
    /**
     * 业务参数文件中对应打电话（主叫）业务的电话号码的key
     */
    public static final String JSON_KEY_DIAL_NUMBER = "Call_Send_DestID";

    /**
     * 业务参数文件中对应打电话（主叫）业务的通话时长的key
     */
    public static final String JSON_KEY_DIAL_DURATION = "Call_Send_HoldTime";

    /**
     * 业务参数文件中对应打电话（主叫）业务的重复次数的key
     */
    public static final String JSON_KEY_DIAL_REPEAT_TIMES = "Call_Send_ShortRptTimes";

    /**
     * 业务参数文件中对应打电话（主叫）业务的重复呼叫间隔的key
     */
    public static final String JSON_KEY_DIAL_REPEAT_INTERVAL = "Call_Send_RptInterval";

    /**
     * 业务参数文件中对应打电话（主叫）业务的最大失败次数的key
     */
    public static final String JSON_KEY_DIAL_MAX_FAILURE = "Call_Send_MaxFailure";

    /**
     * 业务参数文件中对应打电话（主叫）业务的呼叫类型的key
     */
    public static final String JSON_KEY_DIAL_TYPE = "Call_Send_Type";

    /**
     * 业务参数文件中对应打电话（主叫）业务的测试总时长的key
     */
    public static final String JSON_KEY_DIAL_TIME = "Call_Send_ShortDuration";

    /**
     * 业务参数文件中对应打电话（主叫）业务的等待接通忍耐时长的key
     */
    public static final String JSON_KEY_DIAL_WAITING_TOLERANCE = "Call_Send_WaitingTolerance";

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

    /**
     * 微信图片测试例执行实例
     */
    public static WeiXinImageCase weiXinImageCase = null;

    /**
     * 打电话（主叫）测试例执行实例
     */
    public static MOCallCase moCallCase = null;

    /**
     * 打电话（主叫）执行子线程
     */
    public static Thread moCallThread = null;

    /**
     * 打电话（主叫）测试例辅助功能执行实例
     */
    public static MOCallCaseAccess moCallCaseAccess = null;

}
