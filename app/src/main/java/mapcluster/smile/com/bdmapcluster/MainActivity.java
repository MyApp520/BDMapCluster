package mapcluster.smile.com.bdmapcluster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.BaiduMapManager;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.BDLocationService;
import com.baidu.mapapi.clusterutil.clustering.Cluster;
import com.baidu.mapapi.clusterutil.clustering.ClusterItem;
import com.baidu.mapapi.clusterutil.clustering.ClusterManager;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.OnClick;
import mapcluster.smile.com.bdmapcluster.base.BaseActivity;
import mapcluster.smile.com.bdmapcluster.util.AndroidLocationUtils;
import mapcluster.smile.com.bdmapcluster.util.ShowToast;
import mapcluster.smile.com.bdmapcluster.util.UIUtils;

public class MainActivity extends BaseActivity {

    @BindView(R.id.map_view)
    MapView mMapView;
    @BindView(R.id.image_screen_center)
    ImageView imageScreenCenter;
    @BindView(R.id.image_location)
    ImageView imageLocation;

    private final int GPS_REQUEST_CODE = 0x02;

    private Context mContext;
    private AlertDialog tipsAlertDialog;

    private BaiduMap mBaiduMap;
    private BDLocationService mBDLocationService;
    /**
     * 百度地图聚合点管理类
     */
    private ClusterManager<BaiDuMapMarkerItem> mClusterManager;

    private String locationAddress = "";
    private LatLng locationLatlng;//保存定位接口回调结果的经纬度，用于添加一个固定不变的标记(marker)
    private double screenCenterLongitude, screenCenterLatitude;//屏幕中心点经纬度

    private boolean isFirstFinishLocation;
    private boolean isFirstFinishMapChange;
    private float mapCurrentZoom = 15.0f;//实时记录地图层级 默认15
    private LatLng rightTopLatlng, leftBottomLatlng;
    private HashMap<Integer, Integer> mapZoomScale = new HashMap<>();// 地图缩放层级比例尺对应的距离

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initView() {
        mContext = getApplicationContext();
    }

    @Override
    protected void initEvent() {
        initBDMapView();
        initClusterManager();
    }

    private void initBDMapView() {
        Log.e(TAG, TAG + "---initBDmapView() mMapView = " + mMapView);
        if (mMapView == null) {
            return;
        }
        isFirstFinishMapChange = true;
        mapZoomScale = BaiduMapManager.getInstance().initMapZoomScale();
        mBaiduMap = BaiduMapManager.getInstance().initBaiduMapView(mMapView, mBaiDuMapLoadedCallback);

        // 设置地图监听，当地图状态发生改变时，进行点聚合运算
        // ClusterManager类已经实现了BaiduMap.OnMapStatusChangeListener, BaiduMap.OnMarkerClickListener两个接口
        initClusterManager();//初始化聚合点功能

        mBaiduMap.setOnMapStatusChangeListener(mClusterManager);
        // 设置maker点击时的响应
        mBaiduMap.setOnMarkerClickListener(mClusterManager);
    }

    /**
     * 初始化聚合点功能
     */
    private void initClusterManager() {
        // 定义点聚合管理类ClusterManager
        mClusterManager = new ClusterManager<>(mContext, mBaiduMap);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<BaiDuMapMarkerItem>() {
            @Override
            public boolean onClusterClick(Cluster<BaiDuMapMarkerItem> cluster) {
                ShowToast.showToast(mContext, "有" + cluster.getSize() + "个点");
                Log.e(TAG, TAG + "---onClusterClick 发生了");
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<BaiDuMapMarkerItem>() {
            @Override
            public boolean onClusterItemClick(BaiDuMapMarkerItem item) {

                return false;
            }
        });
        mClusterManager.setMapStatusChangeFinishListener(new ClusterManager.OnMapStatusChangeListener() {
            @Override
            public void onClusterFindMapStatusChangeStart(MapStatus mapStatus) {
            }

            @Override
            public void onClusterFindMapStatusChangeFinish(MapStatus mapStatus) {
                if (mMapView == null || mBaiduMap == null) {
                    return;
                }
                mapCurrentZoom = mapStatus.zoom;
                leftBottomLatlng = mapStatus.bound.southwest;
                rightTopLatlng = mapStatus.bound.northeast;

                Log.e(TAG, "地图状态变化结束 MapStatus=" + mapStatus.toString());
                Log.e(TAG, "地图状态变化结束 rightTopLatlng = " + rightTopLatlng + ", leftBottomLatlng = " + leftBottomLatlng);
                //target为地图操作的中心点经纬度坐标
//                BaiduMapManager.getInstance().reGeoCode(mGeoCoder, mapStatus.target.latitude, mapStatus.target.longitude);
                startJumpAnimation();

                double mapMoveDistance = 0;
                double temp_longitude = mapStatus.target.longitude;
                double temp_latitude = mapStatus.target.latitude;
                if (temp_longitude > 108 && temp_latitude > 20 && screenCenterLongitude > 108 && screenCenterLatitude > 20) {
                    // 计算地图移动距离
                    mapMoveDistance = DistanceUtil.getDistance(new LatLng(temp_latitude, temp_longitude), new LatLng(screenCenterLatitude, screenCenterLongitude));
                }

                screenCenterLongitude = temp_longitude;
                screenCenterLatitude = temp_latitude;
                int tempZoomInteger = Math.round((float) Math.floor(mapCurrentZoom)); //取整
                int mapCurrentScale = mapZoomScale.get(tempZoomInteger);
                double mustMoveDistance = mapCurrentZoom / tempZoomInteger * mapCurrentScale;
                if (tempZoomInteger > 18) {
                    mustMoveDistance = mustMoveDistance * 2;
                }

                Log.e(TAG, "地图移动距离 mapMoveDistance = " + mapMoveDistance + ", mustMoveDistance = " + mustMoveDistance
                        + "\n tempZoomInteger = " + tempZoomInteger + ", mapCurrentScale = " + mapCurrentScale);
                if (isFirstFinishMapChange) {
                    isFirstFinishMapChange = false;
                }
            }
        });
    }

    /**
     * 启动百度地图定位
     */
    private void startBaiDuMapLocation() {
        if (AndroidLocationUtils.checkGPSIsOpen(mContext)) {
            if (mBDLocationService == null) {
                //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
                mBDLocationService = BaiduMapManager.getInstance().getBDLocationService();
                mBDLocationService.registerListener(mBaiDuMapLocationListener);
            }
            if (mBDLocationService != null) {
                //重启定位SDK,后台常驻运行的APP可以尝试在回到前台的情况下重启定位SDK,防止因长时间后台运行被系统回收定位权限造成定位失败
                mBDLocationService.resStartBDLocation();
            }
        } else {
            tipsAlertDialog = AndroidLocationUtils.openGPSSettings(this, tipsAlertDialog, GPS_REQUEST_CODE);
        }
    }

    /**
     * 停止百度地图定位
     */
    private void stopBaiDuMapLocation() {
        if (mBDLocationService != null) {
            mBDLocationService.stopBDLocation();
        }
    }

    /**
     * 屏幕中心marker(ImageView) 跳动
     */
    private void startJumpAnimation() {
        Log.e(TAG, "屏幕中心marker(ImageView) 跳动---来个动画");
        Animation animation = new TranslateAnimation(0, 0, -UIUtils.dp2px(mContext, 68), 0);
        //越来越快
        animation.setInterpolator(new AccelerateInterpolator());
        //整个移动所需要的时间
        animation.setDuration(200);
        //设置动画
        imageScreenCenter.setAnimation(animation);
        //开始动画
        imageScreenCenter.startAnimation(animation);
        imageScreenCenter.postDelayed(new Runnable() {
            @Override
            public void run() {
                animation.cancel();
                imageScreenCenter.clearAnimation();
            }
        }, 210);
    }

    @OnClick({R.id.image_location})
    public void onViewClicked(View view) {
        if (R.id.image_location == view.getId()) {
            startBaiDuMapLocation();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult---resultCode=" + resultCode);
        switch (requestCode) {
            case GPS_REQUEST_CODE:
                startBaiDuMapLocation();
                break;
        }
    }

    private BaiduMap.OnMapLoadedCallback mBaiDuMapLoadedCallback = new BaiduMap.OnMapLoadedCallback() {
        @Override
        public void onMapLoaded() {
            //必须在地图加载完后才能去设置，否则设置无效
            View zoomView = mMapView.getChildAt(2);//获取缩放控件
            int x = mMapView.getWidth() - zoomView.getWidth() - UIUtils.dp2px(mContext, 10);
            int y = mMapView.getHeight() - zoomView.getHeight() - UIUtils.dp2px(mContext, 10);
            mMapView.setZoomControlsPosition(new Point(x, y));
            y = mMapView.getHeight() - imageLocation.getHeight() - UIUtils.dp2px(mContext, 10);
            UIUtils.setViewLayout(imageLocation, UIUtils.dp2px(mContext, 6), y);
            Log.e(TAG, "地图加载完成 width = " + zoomView.getWidth() + ", height = " + zoomView.getHeight()
                    + ", " + imageLocation.getHeight());
        }
    };

    private BDLocationListener mBaiDuMapLocationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            Log.e(TAG, "定位结果回调 location = " + location);
            stopBaiDuMapLocation();
            if (mMapView == null || mBaiduMap == null) {
                return;
            }
            if (location == null) {
                ShowToast.showToast(mContext, "地图定位功能返回空的数据");
            } else if (location.getLocType() == BDLocation.TypeGpsLocation || location.getLocType() == BDLocation.TypeOffLineLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {
                StringBuffer sb = new StringBuffer(256);
                sb.append("time : ");
                /**
                 * 时间也可以使用systemClock.elapsedRealtime()方法 获取的是自从开机以来，每次回调的时间；
                 * location.getTime() 是指服务端出本次结果的时间，如果位置不发生变化，则时间不变
                 */
                sb.append(location.getTime());
                sb.append("\nlocType : ");// 定位类型
                sb.append(location.getLocType());
                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
                sb.append(location.getLocationDescribe());
                sb.append("\nlatitude : ");// 纬度
                sb.append(location.getLatitude());
                sb.append("\nlontitude : ");// 经度
                sb.append(location.getLongitude());
                sb.append("\naddr : ");// 地址信息
                sb.append(location.getAddrStr());
                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
                Log.e(TAG, sb.toString());
                locationLatlng = new LatLng(location.getLatitude(), location.getLongitude());
                if (location.getAddress() != null) {
                    locationAddress = location.getAddress().city + location.getAddress().district + location.getAddress().street;
                }

                Log.e(TAG, TAG + "---location.getLatitude() = " + location.getLatitude() + "  location.getLongitude() = " + location.getLongitude());
                BaiduMapManager.getInstance().jumpToMapSpecifiedLocation(mBaiduMap, location);
                ShowToast.showToast(mContext, "地图定位成功 locType = " + location.getLocType());
            } else {
                ShowToast.showToast(mContext, "地图定位失败，请检查网络：locType = " + location.getLocType());
                if (isFirstFinishLocation) {
                    BaiduMapManager.getInstance().goToTargetLocation(mBaiduMap, new LatLng(22.587564, 114.122458), 18.0f);
                }
            }
            isFirstFinishLocation = false;
        }
    };

    /**
     * 每个Marker点，包含Marker点坐标以及图标  方便聚合功能使用
     */
    private class BaiDuMapMarkerItem implements ClusterItem {
        private LatLng latlng;
        private int markerType;
        private String num;
        private String imageHeaderUrl;

        public BaiDuMapMarkerItem(LatLng latlng) {
            this.latlng = latlng;
        }

        public LatLng getLatlng() {
            return latlng;
        }

        public void setLatlng(LatLng latlng) {
            this.latlng = latlng;
        }

        public int getMarkerType() {
            return markerType;
        }

        public void setMarkerType(int markerType) {
            this.markerType = markerType;
        }

        public String getNum() {
            return num;
        }

        public void setNum(String num) {
            this.num = num;
        }

        public void setImageHeaderUrl(String imageHeaderUrl) {
            this.imageHeaderUrl = imageHeaderUrl;
        }

        public String getImageHeaderUrl() {
            return imageHeaderUrl;
        }

        @Override
        public LatLng getPosition() {
            return latlng;
        }

        @Override
        public BitmapDescriptor getBitmapDescriptor() {
            View markerView = LayoutInflater.from(mContext).inflate(R.layout.view_map_marker_people, null);
            TextView tv_people_count = markerView.findViewById(R.id.tv_people_count);
            ImageView image_people_header = markerView.findViewById(R.id.image_people_header);
            tv_people_count.setText(num);
            Glide.with(MainActivity.this).load(getImageHeaderUrl()).into(image_people_header);
            return BitmapDescriptorFactory.fromView(markerView);
        }
    }
}
