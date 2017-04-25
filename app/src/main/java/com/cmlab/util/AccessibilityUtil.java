package com.cmlab.util;

import android.view.accessibility.AccessibilityNodeInfo;

import com.cmlab.servicetest.UiControlAccessibilityService;

import java.util.List;

/**
 * Accessibility工具类，提供AccessibilityService的各项基本操作，比如点击、滚动等操作
 * Created by hunt on 2017/4/25.
 */

public class AccessibilityUtil {

//----------------------------------寻找控件--------------------------------------------------------
    /**
     * 根据控件的text和resource-id属性找到指定的控件。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text       控件的text属性
     * @param resourceid 控件的resource-id属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByTextAndId(UiControlAccessibilityService context, String text, String resourceid) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if (node.getViewIdResourceName() != null) {
                if (node.getViewIdResourceName().equals(resourceid) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据控件的text和class属性找到指定的控件。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text       控件的text属性
     * @param className 控件的class属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByTextAndClass(UiControlAccessibilityService context, String text, String className) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if (node.getClassName() != null) {
                if (node.getClassName().equals(className) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据控件的text和package属性找到指定的控件。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text       控件的text属性
     * @param packageName 控件的package属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByTextAndPackage(UiControlAccessibilityService context, String text, String packageName) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if (node.getPackageName() != null) {
                if (node.getPackageName().equals(packageName) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据控件的text和content-desc属性找到指定的控件。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text       控件的text属性
     * @param contentDesc 控件的content-desc属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByTextAndContentDesc(UiControlAccessibilityService context, String text, String contentDesc) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByText(text);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if (node.getContentDescription() != null) {
                if (node.getContentDescription().equals(contentDesc) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据控件的resource-id和class属性找到指定的控件。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param resourceid 控件的resource-id属性
     * @param className       控件的class属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByIdAndClass(UiControlAccessibilityService context, String resourceid, String className) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceid);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if (node.getClassName() != null) {
                if (node.getClassName().equals(className) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据控件的resource-id和package属性找到指定的控件。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param resourceid 控件的resource-id属性
     * @param packageName       控件的package属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByIdAndPackage(UiControlAccessibilityService context, String resourceid, String packageName) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceid);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if (node.getPackageName() != null) {
                if (node.getPackageName().equals(packageName) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据控件的resource-id和content-desc属性找到指定的控件。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param resourceid 控件的resource-id属性
     * @param contentDesc       控件的content-desc属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByIdAndContentDesc(UiControlAccessibilityService context, String resourceid, String contentDesc) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceid);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if (node.getContentDescription() != null) {
                if (node.getContentDescription().equals(contentDesc) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据控件的text、resource-id和class属性找到指定的控件。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text 控件的text属性
     * @param resourceid 控件的resource-id属性
     * @param className       控件的class属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByTextAndIdAndClass(UiControlAccessibilityService context, String text, String resourceid, String className) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceid);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if ((node.getText() != null) && (node.getClassName() != null)) {
                if (node.getText().equals(text) && node.getClassName().equals(className) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

    /**
     * 根据控件的全部常用属性找到指定的控件。常用属性：text、resource-id、class、package。
     * content-desc由于经常为空，所以不进行匹配
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text 控件的text属性
     * @param resourceid 控件的resource-id属性
     * @param className       控件的class属性
     * @param packageName       控件的package属性
     *
     * @return AccessibilityNodeInfo 找到的控件，若为null，则未找到指定控件
     */
    public static AccessibilityNodeInfo findNodeByFullInfo(UiControlAccessibilityService context, String text, String resourceid, String className, String packageName) {
        AccessibilityNodeInfo rootNode = context.getRootInActiveWindow();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodes = rootNode.findAccessibilityNodeInfosByViewId(resourceid);
        if (nodes == null) {
            return null;
        }
        AccessibilityNodeInfo node;
        for(int i = 0; i < nodes.size(); i++) {
            node = nodes.get(i);
            if ((node.getText() != null) && (node.getClassName() != null) && (node.getPackageName() != null)) {
                if (node.getText().equals(text) && node.getClassName().equals(className) && node.getPackageName().equals(packageName) && node.isEnabled()) {
                    return node;
                }
            }
        }
        return null;
    }

//----------------------------------点击------------------------------------------------------------
    /**
     * 点击。根据控件的text和resource-id属性找到指定的控件执行点击操作。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text       控件的text属性
     * @param resourceid 控件的resource-id属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByTextAndId(UiControlAccessibilityService context, String text, String resourceid) {
        AccessibilityNodeInfo node = findNodeByTextAndId(context, text, resourceid);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击。根据控件的text和class属性找到指定的控件执行点击操作。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text       控件的text属性
     * @param className 控件的class属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByTextAndClass(UiControlAccessibilityService context, String text, String className) {
       AccessibilityNodeInfo node = findNodeByTextAndClass(context, text, className);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击。根据控件的text和package属性找到指定的控件执行点击操作。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text       控件的text属性
     * @param packageName 控件的package属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByTextAndPackage(UiControlAccessibilityService context, String text, String packageName) {
        AccessibilityNodeInfo node = findNodeByTextAndPackage(context, text, packageName);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击。根据控件的text和content-desc属性找到指定的控件执行点击操作。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text       控件的text属性
     * @param contentDesc 控件的content-desc属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByTextAndContentDesc(UiControlAccessibilityService context, String text, String contentDesc) {
        AccessibilityNodeInfo node = findNodeByTextAndContentDesc(context, text, contentDesc);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击。根据控件的resource-id和class属性找到指定的控件执行点击操作。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param resourceid 控件的resource-id属性
     * @param className       控件的class属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByIdAndClass(UiControlAccessibilityService context, String resourceid, String className) {
        AccessibilityNodeInfo node = findNodeByIdAndClass(context, resourceid, className);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击。根据控件的resource-id和package属性找到指定的控件执行点击操作。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param resourceid 控件的resource-id属性
     * @param packageName       控件的package属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByIdAndPackage(UiControlAccessibilityService context, String resourceid, String packageName) {
        AccessibilityNodeInfo node = findNodeByIdAndPackage(context, resourceid, packageName);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击。根据控件的resource-id和content-desc属性找到指定的控件执行点击操作。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param resourceid 控件的resource-id属性
     * @param contentDesc       控件的content-desc属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByIdAndContentDesc(UiControlAccessibilityService context, String resourceid, String contentDesc) {
        AccessibilityNodeInfo node = findNodeByIdAndContentDesc(context, resourceid, contentDesc);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击。根据控件的text、resource-id和class属性找到指定的控件执行点击操作。
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text 控件的text属性
     * @param resourceid 控件的resource-id属性
     * @param className       控件的class属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByTextAndIdAndClass(UiControlAccessibilityService context, String text, String resourceid, String className) {
        AccessibilityNodeInfo node = findNodeByTextAndIdAndClass(context,text, resourceid, className);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 点击。根据控件的全部常用属性找到指定的控件执行点击操作。常用属性：text、resource-id、class、package。
     * content-desc由于经常为空，所以不进行匹配
     *
     * @param context UiControlAccessibilityService类型的context
     * @param text 控件的text属性
     * @param resourceid 控件的resource-id属性
     * @param className       控件的class属性
     * @param packageName       控件的package属性
     *
     * @return true：执行成功；false：未找到控件，执行失败
     */
    public static boolean findAndPerformClickByFullInfo(UiControlAccessibilityService context, String text, String resourceid, String className, String packageName) {
       AccessibilityNodeInfo node = findNodeByFullInfo(context,text, resourceid, className, packageName);
        if (node != null) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            return true;
        } else {
            return false;
        }
    }

}
