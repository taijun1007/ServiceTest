package com.cmlab.config;

/**
 * 这里定义了各类APP在测试过程中的各状态的编码
 * 测试控制流程采用状态机的概念，界面操作触发事件处理，控制状态迁移，将APP的操作流程转化为状态的迁移
 * 目前暂定采用8位二进制状态编码，共256个状态，应该够用了
 * 所有的初始状态编码均为0x00（即二进制00000000b）
 * Created by hunt on 2017/5/4.
 */

public class ConfigStatusCode {

    //微信文本状态编码
    /**
     * 初始状态
     */
    public static final int WEIXINTEXT_INIT = 0x00;

    /**
     * 微信对话列表
     */
    public static final int WEIXINTEXT_TALKLIST = 0x01;

    /**
     * 微信聊天界面
     */
    public static final int WEIXINTEXT_CHAT = 0x02;

    /**
     * 通讯录列表
     */
    public static final int WEIXINTEXT_CONTACT = 0x03;

}
