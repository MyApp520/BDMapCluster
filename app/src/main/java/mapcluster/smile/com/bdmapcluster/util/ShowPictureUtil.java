package mapcluster.smile.com.bdmapcluster.util;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;

import mapcluster.smile.com.bdmapcluster.R;

/**
 * Created by xh_smile on 2020/3/27.
 */

public class ShowPictureUtil {

    private static final String TAG = "ShowPictureUtil";
    private static ShowPictureListener mShowPictureListener;

    public static void setShowPictureListener(ShowPictureListener mShowPictureListener) {
        ShowPictureUtil.mShowPictureListener = mShowPictureListener;
    }

    public static void showImage(Activity activity, String url, ImageView imageView) {

//        RequestOptions requestOptions = new RequestOptions()
//                .placeholder(R.mipmap.ic_launcher)    //加载成功之前占位图
//                .error(R.mipmap.ic_launcher)    //加载错误之后的错误图
//                .override(100, 100)    //指定图片的尺寸
//                .fitCenter()   //指定图片的缩放类型为fitCenter （等比例缩放图片，宽或者是高等于ImageView的宽或者是高。是指其中一个满足即可不会一定铺满 imageview）
//                .centerCrop()//指定图片的缩放类型为centerCrop （等比例缩放图片，直到图片的宽高都大于等于ImageView的宽度，然后截取中间的显示。）
//                .skipMemoryCache(true)    //不使用内存缓存
//                .diskCacheStrategy(DiskCacheStrategy.ALL)    //缓存所有版本的图像
//                .diskCacheStrategy(DiskCacheStrategy.NONE)    //不使用硬盘本地缓存
//                .diskCacheStrategy(DiskCacheStrategy.DATA)    //只缓存原来分辨率的图片
//                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)   //只缓存最终的图片
//                .transform(new GlideCircleWithBorder(activity.getApplicationContext(), 2, Color.RED));

        RequestOptions requestOptions = new RequestOptions()
                .skipMemoryCache(true)    //不使用内存缓存
                .diskCacheStrategy(DiskCacheStrategy.NONE)    //不使用硬盘本地缓存
                .override(180, 180);

        File file = new File(Environment.getExternalStorageDirectory() + "/test/", "test.jpg");
        Log.e(TAG, "showImage: 文件是否存在 = " + file.exists());

        Glide.with(activity)
                .load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1585386955685&di=ab028ee3c60bbd6a0279d74b75e785fb&imgtype=0&src=http%3A%2F%2F5b0988e595225.cdn.sohucs.com%2Fimages%2F20171115%2F9795a9136fec442f9c997349be9d0ddb.jpeg")
                .apply(requestOptions)
                .into(new SimpleTarget<Drawable>() {
                    @Override
                    public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                        Log.e(TAG, "onResourceReady: 头像加载完成 resource = " + resource
                                + ", imageView = " + imageView);

                        if (resource != null) {
                            imageView.setImageDrawable(resource);
                        } else {
                            imageView.setImageResource(R.mipmap.icon_user_pratrai);
                        }
                        if (mShowPictureListener != null) {
                            mShowPictureListener.showPictureResult(resource, imageView);
                        }
                    }
                });
    }

    public interface ShowPictureListener {
        void showPictureResult(Drawable resourceDrawable, ImageView imageView);
    }
}
