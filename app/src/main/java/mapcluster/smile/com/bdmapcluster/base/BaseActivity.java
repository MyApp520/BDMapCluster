package mapcluster.smile.com.bdmapcluster.base;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.tbruyelle.rxpermissions2.RxPermissions;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.Disposable;

/**
 * Created by xh_smile on 2020/3/26.
 */

abstract public class BaseActivity extends FragmentActivity {

    protected final String TAG = getClass().getSimpleName();
    private Unbinder mUnbinder;
    private Disposable permissionDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
        getPermission();
    }

    protected abstract int getLayoutId();
    protected abstract void initView();
    protected abstract void initEvent();

    /**
     * 请求权限
     */
    protected void getPermission() {
        permissionDisposable = new RxPermissions(this)
                .requestEachCombined(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.RECORD_AUDIO
                )
                .subscribe(grant -> {
                    if (grant.granted) {
                        initView();
                        initEvent();
                    } else {
                        Log.e(TAG, "getPermission: 应用权限申请失败，有可能影响使用");
                    }
                });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (permissionDisposable != null && !permissionDisposable.isDisposed()) {
            permissionDisposable.dispose();
        }
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
