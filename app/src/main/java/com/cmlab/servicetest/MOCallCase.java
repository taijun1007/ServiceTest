package com.cmlab.servicetest;

/**
 * 打电话语音主叫测试例。根据下发参数，按指定时间或指定次数，以指定通话时长和通话间隔拨打电话，并记录log。
 * Created by hunt on 2017/6/16.
 */

public class MOCallCase {

    private static final String TAG = "MOCallCase";

    private String dialNumber = null;  //电话号码
    private int dialDuration = 0;  //通话时长

    public boolean execute() {

        return true;
    }

}
