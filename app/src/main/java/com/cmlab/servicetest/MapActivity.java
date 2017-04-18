package com.cmlab.servicetest;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;

/**
 * Created by hunt on 2017/4/16.
 */

public class MapActivity extends Activity {
    public static final String TAG = "MapActivity";

    MapView mMapView = null;
    ImageView mMapTypeImageView = null;
    ImageView mTrafficImageView = null;
    boolean isShiliangmap;
    boolean isTrafficNo;
    BaiduMap mBaiduMap = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidumap);
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapTypeImageView = (ImageView) findViewById(R.id.satellite);
        mTrafficImageView = (ImageView) findViewById(R.id.traffic);
        mMapTypeImageView.setImageResource(R.drawable.shiliangmap);
        mTrafficImageView.setImageResource(R.drawable.trafficno);
        isShiliangmap = true;
        isTrafficNo = true;
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setTrafficEnabled(false);
        mMapTypeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShiliangmap == true) {
                    isShiliangmap = false;
                    mMapTypeImageView.setImageResource(R.drawable.weixingmap);
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                } else {
                    isShiliangmap = true;
                    mMapTypeImageView.setImageResource(R.drawable.shiliangmap);
                    mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
            }
        });
        mTrafficImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTrafficNo == true) {
                    isTrafficNo = false;
                    mTrafficImageView.setImageResource(R.drawable.trafficyes);
                    mBaiduMap.setTrafficEnabled(true);
                } else {
                    isTrafficNo = true;
                    mTrafficImageView.setImageResource(R.drawable.trafficno);
                    mBaiduMap.setTrafficEnabled(false);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }
}
