package mapcluster.smile.com.bdmapcluster.util;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * Created by xh_peng on 2017/11/21.
 */
public class ArithmeticUtil {

    /**
     * 提供精确加法计算的add方法
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确减法运算的sub方法
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double subtract(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算的mul方法
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积
     */
    public static double multiply(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.valueOf(value1));
        BigDecimal b2 = new BigDecimal(Double.valueOf(value2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 提供精确的除法运算方法div
     *
     * @param value1 被除数
     * @param value2 除数
     * @param scale  精确范围
     * @return 两个参数的商
     * @throws IllegalAccessException
     */
    public static double divide(double value1, double value2, int scale) {
        //如果精确范围小于0，抛出异常信息
        try {
            if (scale < 0) {
                String tempResult = new DecimalFormat("0.00").format(value1 / value2);
                return Double.parseDouble(tempResult);
            }
            BigDecimal b1 = new BigDecimal(Double.toString(value1));
            BigDecimal b2 = new BigDecimal(Double.toString(value2));
            //默认保留两位会有错误，这里设置保留小数点后4位
            return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 1.0;
        }
    }

    /**
     * String 转 int
     *
     * @param stringData
     * @return
     */
    public static int StringToInt(String stringData) {
        if (TextUtils.isEmpty(stringData)) {
            return 0;
        }
        try {
            return Integer.parseInt(stringData);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * String 转 long
     *
     * @param stringData
     * @return
     */
    public static long StringToLong(String stringData) {
        if (TextUtils.isEmpty(stringData)) {
            return 0;
        }
        try {
            return Long.parseLong(stringData);
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0;
        }
    }

    /**
     * String 转 double
     *
     * @param stringData
     * @return
     */
    public static double stringToDouble(String stringData) {
        return stringToDouble(stringData, 13);
    }

    /**
     * String 转 double
     *
     * @param stringData
     * @param scale      保留几位小数
     * @return
     */
    public static double stringToDouble(String stringData, int scale) {
        if (TextUtils.isEmpty(stringData)) {
            return 0.0;
        }
        if (scale < 1) {
            scale = 1;
        }
        if (scale > 13) {
            scale = 13;
        }
        try {
            double doubleData = Double.parseDouble(stringData);
            BigDecimal bigDecimal = new BigDecimal(doubleData);
            return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * double 保留几位小数点
     *
     * @param srcDoubleData 源数据
     * @param scale         保留几位小数
     * @return
     */
    public static double doubleBigDecimal(double srcDoubleData, int scale) {
        if (scale < 1) {
            scale = 1;
        }
        if (scale > 13) {
            scale = 13;
        }
        try {
            BigDecimal bigDecimal = new BigDecimal(srcDoubleData);
            return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0;
        }
    }

    /**
     * String 转 float
     *
     * @param stringData
     * @param scale      保留几位小数
     * @return
     */
    public static float StringToFloat(String stringData, int scale) {
        if (TextUtils.isEmpty(stringData)) {
            return 0.0f;
        }
        if (scale < 1) {
            scale = 1;
        }
        if (scale > 13) {
            scale = 13;
        }
        try {
            Float floatData = Float.parseFloat(stringData);
            BigDecimal bigDecimal = new BigDecimal(floatData);
            return bigDecimal.setScale(scale, BigDecimal.ROUND_HALF_UP).floatValue();
        } catch (Exception ex) {
            ex.printStackTrace();
            return 0.0f;
        }
    }

    /**
     * 百分比字符串转换为double类型
     *
     * @param percentData
     * @return
     */
    public static double percentStringToDouble(String percentData) {
        double desData = 0.0;
        if (!TextUtils.isEmpty(percentData)) {
            NumberFormat numberFormat = NumberFormat.getPercentInstance();
            try {
                desData = numberFormat.parse(percentData).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return desData;
    }

    /**
     * double转百分比字符串
     *
     * @param resData
     * @param isMaxOneHundred       最大值为是否为100%
     * @param maximumFractionDigits 百分数最多显示几位小数点
     * @return
     */
    public static String doubleToPercentString(double resData, boolean isMaxOneHundred, int maximumFractionDigits) {
        if (resData <= 0) {
            return "0%";
        }
        if (isMaxOneHundred && resData >= 100) {
            return "100%";
        }
        NumberFormat numberFormat = NumberFormat.getPercentInstance();
        numberFormat.setMaximumFractionDigits(maximumFractionDigits);
        return numberFormat.format(resData);
    }

    /**
     * 判断浮点数（double）
     *
     * @param str
     * @return
     */
    public static boolean isDouble(String str) {
        if (TextUtils.isEmpty(str) || "".equals(str.trim())) {
            return false;
        }
        if (!str.contains(".")) {
            return false;
        }
        Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");
        return pattern.matcher(str).matches();
    }
}
