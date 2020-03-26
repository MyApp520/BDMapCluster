package mapcluster.smile.com.bdmapcluster.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

/**
 * Created by xh_peng on 2017/11/22.
 */
public class UIUtils {
    public static int dp(Context context, int dp) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dp, context.getResources().getDisplayMetrics()) + 0.5F);
    }

    // 获取字符串
    public static String getString(Context mContext, int id) {
        return mContext.getResources().getString(id);
    }

    // 获取图片
    public static Drawable getDrawable(Context mContext, int id) {
        return mContext.getResources().getDrawable(id);
    }

    // 获取颜色
    public static int getColor(Context mContext, int id) {
        return mContext.getResources().getColor(id);
    }

    // 获取颜色的状态选择器
    public static ColorStateList getColorStateList(Context mContext, int id) {
        return mContext.getResources().getColorStateList(id);
    }

    // 获取尺寸
    public static int getDimen(Context mContext, int id) {
        return mContext.getResources().getDimensionPixelSize(id);// 返回像素
    }

    /**
     * convert px to its equivalent dp
     * <p/>
     * 将px转换为与之相等的dp
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }


    /**
     * convert dp to its equivalent px
     * <p/>
     * 将dp转换为与之相等的px
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2sp(Context mContext, float pxValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static int sp2px(Context mContext, float spValue) {
        final float fontScale = mContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static float[] getScreenWidthAndHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return new float[]{dm.widthPixels, dm.heightPixels, dm.density};
    }

    /**
     * 设置控件所在的位置YY (采用方式：通过改变控件上下左右的边距实现，但不改变控件本身的宽高)
     * @param view  目标控件
     * @param x 左边距    右边距 = x + view.getWidth()
     * @param y 上半句    下边距 = y + view.getHeight()
     */
    public static void setViewLayout(View view, int x, int y) {
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, y, x + margin.width, y + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

}
