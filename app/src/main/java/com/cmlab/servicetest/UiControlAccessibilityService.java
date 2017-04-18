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
        asInfo.notificationTimeout = 50;
        asInfo.packageNames = new String[] {"com.ting.mp3.android","air.tv.douyu.android","com.tencent.mm"};
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
                Toast.makeText(this, "TYPE_VIEW_CLICKED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_FOCUSED:
                Toast.makeText(this, "TYPE_VIEW_FOCUSED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_ENTER:
                Toast.makeText(this, "TYPE_VIEW_HOVER_ENTER", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_HOVER_EXIT:
                Toast.makeText(this, "TYPE_VIEW_HOVER_EXIT", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_LONG_CLICKED:
                Toast.makeText(this, "TYPE_VIEW_LONG_CLICKED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_SCROLLED:
                Toast.makeText(this, "TYPE_VIEW_SCROLLED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_SELECTED:
                Toast.makeText(this, "TYPE_VIEW_SELECTED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_CHANGED:
                Toast.makeText(this, "TYPE_VIEW_TEXT_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED:
                Toast.makeText(this, "TYPE_VIEW_TEXT_SELECTION_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY:
                Toast.makeText(this, "TYPE_VIEW_TEXT_TRAVERSED_AT_MOVEMENT_GRANULARITY", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED:
                Toast.makeText(this, "TYPE_WINDOW_CONTENT_CHANGED", Toast.LENGTH_SHORT).show();
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                Toast.makeText(this, "TYPE_WINDOW_STATE_CHANGED", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(this, "TYPE_NOTIFICATION_STATE_CHANGED", Toast.LENGTH_SHORT).show();
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
