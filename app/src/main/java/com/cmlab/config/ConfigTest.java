package com.cmlab.config;

import com.cmlab.servicetest.WeiXinTextCase;

/**
 * 保存测试相关的全局变量
 * Created by hunt on 2017/4/21.
 */

public class ConfigTest {

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
