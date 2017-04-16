package com.cmlab.servicetest;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by hunt on 2017/4/16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(this);
    }
}
