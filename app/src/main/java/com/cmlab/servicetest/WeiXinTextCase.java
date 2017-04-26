package com.cmlab.servicetest;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

/**
 * Created by hunt on 2017/4/21.
 */

public class WeiXinTextCase extends UiautomatorControlCase {
    @Override
    public boolean execute(Context context, AccessibilityEvent event) {
        boolean result = true;

        try {
            Thread.sleep(200);  //处理完事件后，等待一定时间，让界面完成响应，再进行后续处理
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return result;
    }
}
