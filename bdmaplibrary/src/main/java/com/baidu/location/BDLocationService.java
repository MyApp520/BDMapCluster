package com.baidu.location;

import android.content.Context;
import android.util.Log;

import com.baidu.location.LocationClientOption.LocationMode;

/**
 * Created by xh_peng on 2017/10/18.
 */
public class BDLocationService {
    private final String TAG = BDLocationService.class.getSimpleName();
    private LocationClient client = null;
    private LocationClientOption mOption, DIYoption;
    private Object objLock = new Object();

    /***
     * @param locationContext
     */
    public BDLocationService(Context locationContext) {
        synchronized (objLock) {
            if (client == null) {
                client = new LocationClient(locationContext);
                client.setLocOption(getDefaultLocationClientOption());
            }
        }
    }

    /***
     * @param listener
     * @return
     */
    public boolean registerListener(BDLocationListener listener) {
        boolean isSuccess = false;
        if (listener != null) {
            client.registerLocationListener(listener);
            isSuccess = true;
        }
        return isSuccess;
    }

    public void unregisterListener(BDLocationListener listener) {
        if (listener != null) {
            client.unRegisterLocationListener(listener);
        }
    }

    /***
     * @param option
     * @return isSuccessSetOption
     */
    public boolean setLocationOption(LocationClientOption option) {
        boolean isSuccess = false;
        if (option != null && client != null) {
            if (client.isStarted())
                client.stop();
            DIYoption = option;
            client.setLocOption(option);
        }
        return isSuccess;
    }

    /**
     *
     * @return DIYOption 自定义Option设置
     */
    public LocationClientOption getOption(){
        if(DIYoption == null) {
            DIYoption = new LocationClientOption();
        }
        return DIYoption;
    }

    /***
     * @return DefaultLocationClientOption
     */
    public LocationClientOption getDefaultLocationClientOption() {
        if (mOption == null) {
            mOption = new LocationClientOption();
            mOption.setLocationMode(LocationMode.Hight_Accuracy);//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            mOption.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            mOption.setScanSpan(1200);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
            mOption.setTimeOut(12000);
            mOption.setOpenGps(true); // 打开gps
            mOption.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
            mOption.setNeedDeviceDirect(false);//可选，设置是否需要设备方向结果
            mOption.setLocationNotify(false);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            mOption.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            mOption.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            mOption.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            mOption.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        }
        return mOption;
    }

    public void startBDLocation() {
        synchronized (objLock) {
            if (client != null && !client.isStarted()) {
                Log.e(TAG, TAG +"---start()准备启动百度地图定位");
                client.start();
            }
        }
    }

    public void stopBDLocation() {
        synchronized (objLock) {
            if (client != null && client.isStarted()) {
                Log.e(TAG, TAG +"---stop()准备停止百度地图定位");
                client.stop();
            }
        }
    }

    /**
     * 重启定位SDK,后台常驻运行的APP可以尝试在回到前台的情况下重启定位SDK,防止因长时间后台运行被系统回收定位权限造成定位失败
     */
    public void resStartBDLocation() {
        synchronized (objLock) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    stopBDLocation();
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startBDLocation();
                }
            }).start();
        }
    }

    /**
     * @return 0：离线定位请求成功 1:service没有启动 2：无监听函数 6：两次请求时间太短
     */
    public int requestOfflineLocation() {
        synchronized (objLock) {
            if (client == null) {
                return -1;
            }
            return client.requestOfflineLocation();
        }
    }
}
