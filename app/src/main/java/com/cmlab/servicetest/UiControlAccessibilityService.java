package com.cmlab.servicetest;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

/**
 * Created by hunt on 2017/4/17.
 */

public class UiControlAccessibilityService extends AccessibilityService {

    @Override
    protected void onServiceConnected() {
        //初始化配置
        super.onServiceConnected();
        AccessibilityServiceInfo asInfo = new AccessibilityServiceInfo();
        asInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        asInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        asInfo.notificationTimeout = 100;
        asInfo.packageNames = new String[] {"com.ting.mp3.android","air.tv.douyu.android","com.tencent.mm",
                "com.android.phone", "com.android.incallui", "com.android.dialer", "com.android.contacts",
                "me.android.browser", "com.UCMobile"};
        setServiceInfo(asInfo);
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        int eventType = event.getEventType();
        switch (eventType) {
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED:
                Toast.makeText(this, "TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED:
                Toast.makeText(this, "TYPE_VIEW_ACCESSIBILITY_FOCUSED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_CLICKED:
                //屏幕上被监控的APP中可以点击的控件被点击时发生此事件，要点：被监控APP控件可见并可点击
                //不可点击的控件（比如微信启动时的背景图片）不会触发此事件
//                Toast.makeText(this, "TYPE_VIEW_CLICKED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                //屏幕上被监控的APP中焦点变到某个控件时发生此事件，比如点击输入框时，焦点变到输入框中
                //注意：如果屏幕上一开始就显示某个输入框已是焦点，已经可以输入了，此时再点击时，焦点不会变化
                //这样就只会触发TYPE_VIEW_CLICKED事件，不会触发TYPE_VIEW_FOCUSED事件
                //此事件只会在焦点发生变化的时候触发
//                Toast.makeText(this, "TYPE_VIEW_FOCUSED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                Toast.makeText(this, "TYPE_VIEW_HOVER_ENTER", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                Toast.makeText(this, "TYPE_VIEW_HOVER_EXIT", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                //屏幕上被监控的APP中可见的允许长按的控件被长按时会触发此事件，比如在文本输入框中长按
                //输入框中长按时若有文字，还会触发TYPE_VIEW_TEXT_SELECTION_CHANGED事件、TYPE_WINDOW_CONTENT_CHANGED事件
                //输入框中长按时若没有文字，则只会触发此事件
//                Toast.makeText(this, "TYPE_VIEW_LONG_CLICKED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                //屏幕上被监控的APP中可滚动的控件发生滚动时触发此事件，无论是APP自己在滚动还是人工滚动都会触发此事件
                //比如百度音乐的广告bar上就会左右自己滚动，此时也触发此事件
                //一般发生滚动事件时都会触发TYPE_WINDOW_CONTENT_CHANGED事件
//                Toast.makeText(this, "TYPE_VIEW_SCROLLED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                Toast.makeText(this, "TYPE_VIEW_SELECTED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                //当输入框中输入或删除文字时触发此事件，每输入或删除一个字符都会触发一次此事件
                //此事件发生时，一般TYPE_VIEW_TEXT_SELECTION_CHANGED事件会伴随发生
//                Toast.makeText(this, "TYPE_VIEW_TEXT_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                //在输入框中输入、删除、长按选中、取消选中文字时触发此事件
                //其他非输入框的控件上的文字如果有匹配选择的变化时触发此事件，比如拨号界面输入电话号码时，
                //号码列表中匹配过滤电话号码，被匹配的数字会像选中一样有颜色的变化
//                Toast.makeText(this, "TYPE_VIEW_TEXT_SELECTION_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                Toast.makeText(this, "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                //屏幕上被监控的APP显示的内容发生变化时触发此事件
                //一般触发此事件的操作：打开APP，切换APP的activity，activity的内容变化，控件的变化（颜色、文字、状态等
//                Toast.makeText(this, "TYPE_WINDOW_CONTENT_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                //屏幕上被监控的APP窗口（activity）在打开、切换时，
                // 弹出对话框和关闭对话框时（相当于activity被挡在对话框后面和activity重新回到前台）触发此事件
                //注意：关闭APP时不会触发此事件
//                Toast.makeText(this, "TYPE_WINDOW_STATE_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_WINDOWS_CHANGED:
                Toast.makeText(this, "TYPE_WINDOWS_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_ANNOUNCEMENT:
                Toast.makeText(this, "TYPE_ANNOUNCEMENT", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_END:
                Toast.makeText(this, "TYPE_GESTURE_DETECTION_END", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_GESTURE_DETECTION_START:
                Toast.makeText(this, "TYPE_GESTURE_DETECTION_START", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:
                //被监控的APP在顶部下拉消息栏中notification出现和消失，或notification内容变化时触发此事件
//                Toast.makeText(this, "TYPE_NOTIFICATION_STATE_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_END:
                Toast.makeText(this, "TYPE_TOUCH_EXPLORATION_GESTURE_END", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_TOUCH_EXPLORATION_GESTURE_START:
                Toast.makeText(this, "TYPE_TOUCH_EXPLORATION_GESTURE_START", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_END:
                Toast.makeText(this, "TYPE_TOUCH_INTERACTION_END", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_TOUCH_INTERACTION_START:
                Toast.makeText(this, "TYPE_TOUCH_INTERACTION_START", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public void onInterrupt() {

    }
}
