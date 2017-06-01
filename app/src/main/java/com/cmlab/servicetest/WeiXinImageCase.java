package com.cmlab.servicetest;

import android.view.accessibility.AccessibilityEvent;

/**
 * 微信图片，按照指定的业务参数发送微信图片，并在指定的sqlite数据库文件中记录log
 * 参数文件：setup.json，parameter.json
 * Created by hunt on 2017/6/1.
 */

public class WeiXinImageCase extends UiautomatorControlCase {

    private static final String TAG = "WeiXinImageCase";

    private String logPath;
    private String parameterFile;
    private String WeiXin_Image_DestID;
    private int WeiXin_Image_RptTimes;
    private String WeiXin_Image_Size;
    private int WeiXin_Image_Num;
    private long WeiXin_Image_RptInterval;
    private boolean WeiXin_Image_Origin;

    @Override
    public boolean execute(UiControlAccessibilityService context, AccessibilityEvent event) {
        return false;
    }
}
