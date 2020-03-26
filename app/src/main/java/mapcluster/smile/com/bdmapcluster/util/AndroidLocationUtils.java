package mapcluster.smile.com.bdmapcluster.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import mapcluster.smile.com.bdmapcluster.R;


/**
 * Created by xh_peng on 2017/11/10.
 */
public class AndroidLocationUtils {

    private Context mContext;
    private final static String TAG = "AndroidLocationUtils";
    private LocationManager locationManager;

    public AndroidLocationUtils(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    /**
     * 检测GPS是否打开
     * @return
     */
    public static boolean checkGPSIsOpen(Context mContext) {
        if (mContext == null) {
            return false;
        }
        boolean isOpen = false;
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            isOpen = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return isOpen;
    }

    /**
     * 跳转GPS设置
     */
    public static AlertDialog openGPSSettings(final Activity mActivity, AlertDialog dialog, final int requestCode) {
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
        //没有打开则弹出对话框
        AlertDialog tipsAlertDialog = new AlertDialog.Builder(mActivity)
                .setTitle(R.string.warm_tip)
                .setMessage(R.string.gpsNotifyMsg)
                .setNegativeButton(R.string.exit_cancle,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })

                .setPositiveButton(R.string.exit_setting,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //跳转GPS设置界面
                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                mActivity.startActivityForResult(intent, requestCode);
                                dialog.dismiss();
                            }
                        })

                .setCancelable(false)
                .show();
        return tipsAlertDialog;
    }
}
