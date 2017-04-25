package com.cmlab.config;

import com.cmlab.servicetest.WeiXinTextCase;

/**
 * 保存测试相关的全局变量
 * Created by hunt on 2017/4/21.
 */

public class ConfigTest {

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

    /**
     * 测试例名称
     */
    public static String caseName = null;

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
     * 测试任务开始时间戳，单位ms
     */
    public static long caseStartTime;

    /**
     * 测试任务结束时间戳，单位ms
     */
    public static long caseEndTime;

    /**
     * 微信文本测试例执行实例
     */
    public static WeiXinTextCase weiXinTextCase = null;

}
