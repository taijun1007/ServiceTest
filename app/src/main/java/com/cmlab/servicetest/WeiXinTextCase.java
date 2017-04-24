package com.cmlab.servicetest;

import android.content.Context;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;

import com.cmlab.config.ConfigTest;

/**
 * Created by hunt on 2017/4/21.
 */

public class WeiXinTextCase extends UiautomatorControlCase {
    @Override
    public boolean execute(Context context, AccessibilityEvent event) {
        boolean result = true;

        if (System.currentTimeMillis() >= ConfigTest.caseEndTime) {
            ConfigTest.isCaseRunning = false;
            //退出微信APP

            //弹回ServiceTest
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        }

        return result;
    }
}
