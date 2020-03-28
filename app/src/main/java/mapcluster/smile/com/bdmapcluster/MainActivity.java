package mapcluster.smile.com.bdmapcluster;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
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
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import mapcluster.smile.com.bdmapcluster.base.BaseActivity;
import mapcluster.smile.com.bdmapcluster.model.response.GetAlarmListResponse;
import mapcluster.smile.com.bdmapcluster.util.AndroidLocationUtils;
import mapcluster.smile.com.bdmapcluster.util.PeopleMapTrackUtil;
import mapcluster.smile.com.bdmapcluster.util.ShowPictureUtil;
import mapcluster.smile.com.bdmapcluster.util.ShowToast;
import mapcluster.smile.com.bdmapcluster.util.UIUtils;
import mapcluster.smile.com.bdmapcluster.widget.badge_view.BadgeViewPro;
import mapcluster.smile.com.bdmapcluster.widget.circle_imageview.SWImageView;

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
    private ClusterManager<BaiDuMapClusterItem> mClusterManager;

    private String locationAddress = "";
    private LatLng locationLatlng;//保存定位接口回调结果的经纬度，用于添加一个固定不变的标记(marker)
    private double screenCenterLongitude, screenCenterLatitude;//屏幕中心点经纬度

    private boolean isFirstFinishLocation;
    private boolean isFirstFinishMapChange;
    private float mapCurrentZoom = 15.0f;//实时记录地图层级 默认15
    private LatLng rightTopLatlng, leftBottomLatlng;
    private HashMap<Integer, Integer> mapZoomScale = new HashMap<>();// 地图缩放层级比例尺对应的距离

    private ArrayList<BaiDuMapClusterItem> mapMarkerItemList = new ArrayList<>();

    /**
     * 记录显示marker时，使用的ImageView和数据对象MarkerBean
     */
    private HashMap<ImageView, MarkerBean> hashMapMarkerView = new HashMap<>();

    /**
     * 图片加载完成次数，无论成功与否
     */
    private int hasShowPictureFinishCount;

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
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<BaiDuMapClusterItem>() {
            @Override
            public boolean onClusterClick(Cluster<BaiDuMapClusterItem> cluster) {
                ShowToast.showToast(mContext, "有" + cluster.getSize() + "个点");
                Log.e(TAG, TAG + "---onClusterClick 发生了");
                return false;
            }
        });
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<BaiDuMapClusterItem>() {
            @Override
            public boolean onClusterItemClick(BaiDuMapClusterItem item) {

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

    /**
     * 重新添加聚合点marker
     */
    private void reloadClusterMarker() {
        Log.e(TAG, "开始聚合了 mapMarkerItemList = " + mapMarkerItemList.size());
        mClusterManager.clearItems();//清除所有的items
        //        mClusterManager.getMarkerCollection().clear();
        //        mClusterManager.getClusterMarkerCollection().clear();
        mClusterManager.addItems(mapMarkerItemList);
        // 算法计算聚合，并显示
        // 类似于通知地图刷新聚合点marker
        mClusterManager.cluster();
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
            int y = mMapView.getHeight() - zoomView.getHeight() - UIUtils.dp2px(mContext, 20);
            // 设置缩放控件的位置
            mMapView.setZoomControlsPosition(new Point(x, y));

            x = mMapView.getWidth() - zoomView.getWidth() - UIUtils.dp2px(mContext, 13);
            y = y - imageLocation.getHeight() - UIUtils.dp2px(mContext, 10);
            UIUtils.setViewLayout(imageLocation, x, y);
            Log.e(TAG, "地图加载完成 width = " + zoomView.getWidth() + ", height = " + zoomView.getHeight()
                    + ", " + imageLocation.getHeight());

            List<GetAlarmListResponse.DataBean.ListBean> dataBeanList = PeopleMapTrackUtil.getAlarmListResponse().getData().getList();
            BaiduMapManager.getInstance().goToTargetLocation(mBaiduMap
                    , dataBeanList.get(dataBeanList.size() - dataBeanList.size() / 2).getBaiDuLatLng()
                    , 16);

            BaiDuMapClusterItem mapMarkerItem;
            List<LatLng> markerList = new ArrayList<>();

            GetAlarmListResponse.DataBean.ListBean listBean;
            for (int i = 0; i < dataBeanList.size(); i++) {
                listBean = dataBeanList.get(i);

                mapMarkerItem = new BaiDuMapClusterItem(listBean.getBaiDuLatLng(), listBean.getUrl());
                mapMarkerItemList.add(mapMarkerItem);
                markerList.add(listBean.getBaiDuLatLng());

                // 构建Marker图标对应的view
                View markerView = LayoutInflater.from(mContext).inflate(R.layout.view_map_marker_people, null);
                // 小红点
                BadgeViewPro tvPeopleCount = markerView.findViewById(R.id.tv_people_count);
                // 圆形头像
                SWImageView imagePeopleHeader = markerView.findViewById(R.id.image_people_header);

                if (i == 0) {
                    // 终点
                    tvPeopleCount.setText("1");
                    imagePeopleHeader.setBorder_color(ContextCompat.getColor(getApplicationContext(), R.color.color_last_marker));
                } else if (i == dataBeanList.size() - 1) {
                    // 起点
                    tvPeopleCount.setShape(3);
                    tvPeopleCount.setText("99+");
                    imagePeopleHeader.setBorder_color(ContextCompat.getColor(getApplicationContext(), R.color.color_first_marker));
                } else {
                    Random random = new Random();
                    tvPeopleCount.setText(String.valueOf(random.nextInt(50 ) + 50));
                    imagePeopleHeader.setBorder_color(ContextCompat.getColor(getApplicationContext(), R.color.color_period_marker));
                }
                tvPeopleCount.setTextBgShape();

                // 构建Marker需要的数据对象
                MarkerBean markerBean = new MarkerBean(listBean.getBaiDuLatLng(), markerView);
                hashMapMarkerView.put(imagePeopleHeader, markerBean);

                // 加载单个marker图片的网络回调监听
                ShowPictureUtil.setShowPictureListener(new ShowPictureUtil.ShowPictureListener() {
                    @Override
                    public void showPictureResult(Drawable resourceDrawable, ImageView imageView) {
                        // 图片加载结果监听回调
                        hasShowPictureFinishCount++;
                        // 构建Marker图标
                        BitmapDescriptor markerBitmap = BitmapDescriptorFactory.fromView(hashMapMarkerView.get(imageView).getMarkerView());
                        // 构建MarkerOption，用于在地图上添加Marker
                        OverlayOptions overlayOptions = new MarkerOptions()
                                .position(hashMapMarkerView.get(imageView).getLatlng())
                                .icon(markerBitmap)
                                .zIndex(8)
                                .draggable(true);
                        // 3、开始给地图添加marker，这里是一个一个添加，也可以选择批量添加
                        mBaiduMap.addOverlay(overlayOptions);

                        if (hasShowPictureFinishCount >= markerList.size()) {
                            // 所有图片加载完成
                            // 4、设置marker连线的参数
                            startPolyline(markerList);
                        }
                    }
                });
                // 开始加载图片
                ShowPictureUtil.showImage(MainActivity.this, listBean.getUrl(), imagePeopleHeader);
            }
        }
    };

    private void addAllMarker(List<GetAlarmListResponse.DataBean.ListBean> beanList) {
        // 1、起点位置
        LatLng startLatlng = beanList.get(0).getBaiDuLatLng();
        // 构建Marker图标对应的view
        View startMarkerView = LayoutInflater.from(mContext).inflate(R.layout.view_map_marker_people, null);
        TextView startTvPeopleCount = startMarkerView.findViewById(R.id.tv_people_count);
        ImageView startImagePeopleHeader = startMarkerView.findViewById(R.id.image_people_header);
        ShowPictureUtil.showImage(MainActivity.this, beanList.get(0).getUrl(), startImagePeopleHeader);
        // 构建Marker图标
        BitmapDescriptor startMarkerBitmap = BitmapDescriptorFactory.fromView(startMarkerView);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(startLatlng)
                .icon(startMarkerBitmap).zIndex(8).draggable(true);


        // 2、终点位置
        LatLng endLatlng = beanList.get(beanList.size() - 1).getBaiDuLatLng();
        // 构建Marker图标对应的view
        View endMarkerView = LayoutInflater.from(mContext).inflate(R.layout.view_map_marker_people, null);
        TextView endTvPeopleCount = endMarkerView.findViewById(R.id.tv_people_count);
        ImageView endImagePeopleHeader = endMarkerView.findViewById(R.id.image_people_header);
        ShowPictureUtil.showImage(MainActivity.this, beanList.get(beanList.size() - 1).getUrl(), endImagePeopleHeader);
        // 构建Marker图标
        BitmapDescriptor endMarkerBitmap = BitmapDescriptorFactory.fromView(endMarkerView);
        // 构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option1 = new MarkerOptions()
                .position(endLatlng)
                .icon(endMarkerBitmap).zIndex(8).draggable(true);

        // 3、在地图上添加Marker，并显示
        List<OverlayOptions> overlay = new ArrayList<>();
        overlay.add(option);
        overlay.add(option1);
        mBaiduMap.addOverlays(overlay);

        // 4、设置连线的参数
        startPolyline(new ArrayList<>());
    }

    /**
     * 设置marker连线的参数
     *
     * @param markerList marker点位集合
     */
    private void startPolyline(List<LatLng> markerList) {
        PolylineOptions polylineOptions = new PolylineOptions()
                .width(5)
                .dottedLine(false)//false：实线， true：虚线
                .color(ContextCompat.getColor(getApplicationContext(), R.color.color_marker_line))
                .points(markerList);// 添加点位集合数据
        // 绘制连接marker坐标点的线
        mBaiduMap.addOverlay(polylineOptions);
    }

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
     * marker数据bean对象
     */
    private class MarkerBean {
        private LatLng latlng;
        private View markerView;

        public MarkerBean(LatLng latlng, View markerView) {
            this.latlng = latlng;
            this.markerView = markerView;
        }

        public LatLng getLatlng() {
            return latlng;
        }

        public void setLatlng(LatLng latlng) {
            this.latlng = latlng;
        }

        public View getMarkerView() {
            return markerView;
        }

        public void setMarkerView(View markerView) {
            this.markerView = markerView;
        }
    }

    /**
     * 每个聚合的点，包含点坐标以及图标  方便聚合功能使用
     */
    private class BaiDuMapClusterItem implements ClusterItem {
        private LatLng latlng;
        private int markerType;
        private String num;
        private String imageHeaderUrl;

        public BaiDuMapClusterItem(LatLng latlng, String imageHeaderUrl) {
            this.latlng = latlng;
            this.imageHeaderUrl = imageHeaderUrl;
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
