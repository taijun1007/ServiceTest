package com.cmlab.servicetest;

import android.view.accessibility.AccessibilityEvent;

import com.cmlab.config.ConfigTest;
import com.cmlab.util.AccessibilityUtil;

/**
 * 打电话（主叫）测试例辅助功能测试例。在打电话（主叫）测试例（MOCallCase）运行期间，配合处理弹框等事件。非测试任务期间不执行。
 * Created by hunt on 2017/6/23.
 */

public class MOCallCaseAccess extends UiautomatorControlCase {

    public static final String TAG = "MOCallCaseAccess";

    @Override
    public boolean execute(UiControlAccessibilityService context, AccessibilityEvent event) {
        try {
            Thread.sleep(300);  //等待被监测窗口完成变化，稳定下来后再进行操作
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (AccessibilityUtil.findAndPerformClickByIdAndPackage(context, "android:id/button1", "com.android.contacts", ConfigTest.NODE_SELF)) {
            //N2在飞行模式开启时或无网络时拨打电话时的弹框，点击“确定”按钮
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("成功处理N2在飞行模式开启时或无网络时拨打电话的弹框");
            }
        } else {
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("未找到N2在飞行模式开启时或无网络时拨打电话的弹框");
            }
        }
        if (AccessibilityUtil.findAndPerformClickByIdAndPackage(context, "android:id/button1", "com.android.dialer", ConfigTest.NODE_SELF)) {
            //N1在无网络时拨打电话时的弹框，点击“确定”按钮
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("成功处理N1在无网络时拨打电话的弹框");
            }
        } else {
            if (ConfigTest.DEBUG) {
                Tools.writeLogFile("未找到N1在无网络时拨打电话的弹框");
            }
        }
        return true;
    }

}
