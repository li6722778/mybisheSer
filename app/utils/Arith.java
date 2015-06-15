package utils;

import java.math.BigDecimal;

/**
 *
 * @author huiqi
 */
public class Arith {

    //默认除法运算精度
    private static final int DEF_DIV_SCALE = 10;


    public static float add(float v1, float v2) {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        return b1.add(b2).floatValue();
    }

    public static float sub(float v1, float v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).setScale(3, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static float mul(float v1, float v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).floatValue();
    }

    public static float div(float v1, float v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    public static float div(float v1, float v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }

     /**
    * 提供精确的加法运算。
    * @param v1 被加数
    * @param v2 加数
    * @return 两个参数的和
    */
    public static double add(double v1, double v2) {
        BigDecimal b1 = new BigDecimal("" + v1);
        BigDecimal b2 = new BigDecimal("" + v2);
        return b1.add(b2).doubleValue();
    }

    /**
      * 提供精确的减法运算。
      * @param v1 被减数
      * @param v2 减数
      * @return 两个参数的差
      */
    public static double sub(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.subtract(b2).doubleValue();
    }

    /**
      * 提供精确的乘法运算。
      * @param v1 被乘数
      * @param v2 乘数
      * @return 两个参数的积
      */
    public static double mul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.multiply(b2).doubleValue();
    }

    /**
      * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
      * 小数点以后10位，以后的数字四舍五入。
      * @param v1 被除数
      * @param v2 除数
      * @return 两个参数的商
      */
    public static double div(double v1, double v2) {
        return div(v1, v2, DEF_DIV_SCALE);
    }

    /**
      * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
      * 定精度，以后的数字四舍五入。
      * @param v1 被除数
      * @param v2 除数
      * @param scale 表示表示需要精确到小数点以后几位。
      * @return 两个参数的商
      */
    public static double div(double v1, double v2, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b1 = new BigDecimal(v1);
        BigDecimal b2 = new BigDecimal(v2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入到小数点后scale位
     * @param v
     * @param scale
     * @return 
     */
    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static float round(float v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The scale must be a positive integer or zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).floatValue();
    }
    
    /**
     * 向上取整
     * @param v
     * @return 
     */
    public static double ceil(double v) {
        int intv = (int) v;
        double douv = 0;

        if (v - intv < 0.00001) {
            douv = v - 0.1;
        } else {
            douv = v;
        }
        return Math.ceil(douv);
    }

    public static double decimalPrice(float v) {
        return new BigDecimal(Float.toString(v)).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static float decimal(float v, int num) {
        return new BigDecimal(Float.toString(v)).setScale(num, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    public static double decimalPrice(double v) {
        return new BigDecimal(Double.toString(v)).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double decimal(double v, int num) {
        return new BigDecimal(Double.toString(v)).setScale(num, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
 
}
