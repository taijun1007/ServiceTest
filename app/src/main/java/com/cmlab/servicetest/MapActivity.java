package com.cmlab.servicetest;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;

/**
 * Created by hunt on 2017/4/16.
 */

public class MapActivity extends Activity {
    public static final String TAG = "MapActivity";

    MapActivity mapActivity;
    MapView mMapView = null;
    ImageView mMapTypeImageView = null;
    ImageView mTrafficImageView = null;
    boolean isShiliangmap;
    boolean isTrafficNo;
    BaiduMap mBaiduMap = null;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            StringBuffer sb = new StringBuffer(256);
            sb.append("时间: " + bdLocation.getTime() + "\n"); //获取定位时间
            sb.append("错误码: " + bdLocation.getLocType() + "\n"); //获取定位类型
            sb.append("纬度: " + bdLocation.getLatitude() + "\n"); //获取纬度
            sb.append("经度: " + bdLocation.getLongitude() + "\n"); //获取经度
            sb.append("定位半径: " + bdLocation.getRadius() + "\n"); //获取定位精准度
            switch (bdLocation.getLocType()) {
                case BDLocation.TypeGpsLocation: //GPS定位结果
                    sb.append("速度: " + bdLocation.getSpeed() + "km/h\n"); //获取速度
                    sb.append("卫星数: " + bdLocation.getSatelliteNumber() + "\n"); //获取卫星数
                    sb.append("海拔: " + bdLocation.getAltitude() + "米\n"); //获取海拔高度
                    sb.append("方向: " + bdLocation.getDirection() + "度\n"); //获取方向
                    sb.append("地址: " + bdLocation.getAddrStr() + "\n"); //获取地址
                    sb.append("描述: GPS定位成功\n");
                    break;
                case BDLocation.TypeNetWorkLocation: //网络定位结果
                    sb.append("地址: " + bdLocation.getAddrStr() + "\n"); //获取地址
                    String operator = null;
                    switch (bdLocation.getOperators()) {
                        case BDLocation.OPERATORS_TYPE_MOBILE: //中国移动
                            operator = "中国移动";
                            break;
                        case BDLocation.OPERATORS_TYPE_UNICOM: //中国联通
                            operator = "中国联通";
                            break;
                        case BDLocation.OPERATORS_TYPE_TELECOMU: //中国电信
                            operator = "中国电信";
                            break;
                        case BDLocation.OPERATORS_TYPE_UNKONW: //未知运营商
                            operator = "未知运营商";
                            break;
                    }
                    sb.append("运营商: " + operator + "\n"); //获取运营商信息
                    sb.append("描述: 网络定位成功\n");
                    break;
                case BDLocation.TypeOffLineLocation:  //离线定位结果
                    sb.append("描述: 离线定位成功\n");
                    break;
                case BDLocation.TypeServerError: //服务端网络定位失败
                    sb.append("描述: 服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因\n");
                    break;
                case BDLocation.TypeNetWorkException: //网络不同导致定位失败
                    sb.append("描述: 网络不同导致定位失败，请检查网络是否通畅\n");
                    break;
                case BDLocation.TypeCriteriaException: //无法获取有效定位依据
                    sb.append("描述: 无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机\n");
                    break;
            }
            sb.append("位置语义化信息: " + bdLocation.getLocationDescribe() + "\n");  //获取位置语义化信息
//            Toast.makeText(mapActivity, sb.toString(), Toast.LENGTH_SHORT).show();
            Tools.writeLocationFile(sb.toString());
            MyLocationData locationData = new MyLocationData.Builder()
                    .accuracy(bdLocation.getRadius())
                    .direction(bdLocation.getDirection())
                    .latitude(bdLocation.getLatitude())
                    .longitude(bdLocation.getLongitude())
                    .build();
            mBaiduMap.setMyLocationData(locationData);

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baidumap);
        mapActivity = this;
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
        mLocationClient = new LocationClient(getApplicationContext());
        mLocationClient.registerLocationListener(myListener);
        initLocation();
        mLocationClient.start();
        mBaiduMap.setMyLocationEnabled(true);
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
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
    }

    /**
     *
     */
    private void initLocation() {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy); //高精度定位模式
        option.setCoorType("bd09ll"); //设置百度经纬度坐标系
        option.setScanSpan(1000); //发起定位请求的间隔为1000ms
        option.setIsNeedAddress(true); //需要地址信息
        option.setOpenGps(true); //需要GPS
        option.setLocationNotify(true); //当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true); //需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true); //需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false); //在stop的时候杀死定位SDK这个独立Service进程
        option.SetIgnoreCacheException(false); //收集CRASH信息
        option.setEnableSimulateGps(false); //需要过滤GPS仿真结果
        mLocationClient.setLocOption(option);
    }
}
