package mapcluster.smile.com.bdmapcluster;

import android.app.Application;

import com.baidu.BaiduMapManager;
import com.baidu.location.BDLocationService;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

/**
 * Created by xh_smile on 2020/3/26.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initBDMap();
    }

    private void initBDMap() {
        /***
         * 初始化定位sdk，建议在Application中创建BDLocationService
         */
        BaiduMapManager.getInstance().setBDLocationService(new BDLocationService(getApplicationContext()));
        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
    }
}
