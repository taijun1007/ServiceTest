package com.cmlab.servicetest;

import android.content.Context;
import android.view.accessibility.AccessibilityEvent;

/**
 * 抽象类，业务测试例的基类
 * 业务处理逻辑在抽象方法execute中实现，继承时必须实现该方法
 *
 * Created by hunt on 2017/4/21.
 */

public abstract class UiautomatorControlCase {

    /**
     * 抽象方法，执行业务处理逻辑，根据输入的事件，执行相应的操作
     *
     * @param context Context，一般为UiControlAccessibilityService类型
     * @param event 事件，根据事件内容执行业务逻辑
     *
     * @return boolean型，true表示执行成功，false表示执行失败
     */
    public abstract boolean execute(Context context, AccessibilityEvent event);

}
