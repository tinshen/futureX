package com.songbai.wrapres.utils;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class FinanceUtil {
    public static final String UNIT_WANG = "万";
    public static final String UNIT_YI = "亿";

    private static final int DEFAULT_SCALE = 2;

    /**
     * 格式化 double 数据成百分数格式
     *
     * @param value
     * @return
     */
    public static String formatToPercentage(double value) {
        BigDecimal bigDecimal = multiply(value, 100d);
        return formatWithScale(bigDecimal.doubleValue(), DEFAULT_SCALE) + "%";
    }

    /**
     * 格式化 double 数据成百分数格式
     *
     * @param value
     * @param scale
     * @return
     */
    public static String formatToPercentage(double value, int scale) {
        BigDecimal bigDecimal = multiply(value, 100d);
        return formatWithScale(bigDecimal.doubleValue(), scale) + "%";
    }

    /**
     * 格式化 double 数据成百分数格式
     *
     * @param value
     * @param roundingMode
     * @return
     */
    public static String formatToPercentage(double value, RoundingMode roundingMode) {
        BigDecimal bigDecimal = multiply(value, 100d);
        return formatWithScale(bigDecimal.doubleValue(), DEFAULT_SCALE, roundingMode) + "%";
    }

    /**
     * 格式化 double 数据成百分数格式
     *
     * @param value
     * @param scale
     * @param roundingMode
     * @return
     */
    public static String formatToPercentage(double value, int scale, RoundingMode roundingMode) {
        BigDecimal bigDecimal = multiply(value, 100d);
        return formatWithScale(bigDecimal.doubleValue(), scale, roundingMode) + "%";
    }

    /**
     * 添加大额单位，默认 roundingMode 是 down
     *
     * @param value
     * @return
     */
    public static String unitize(int value) {
        return unitize(value, DEFAULT_SCALE);
    }

    /**
     * 添加大额单位，默认 roundingMode 是 down
     *
     * @param value
     * @return
     */
    public static String unitize(int value, int unitizeScale) {
        if (value >= 100000000 || value <= -100000000) {
            return unitizeWithScale(value, unitizeScale, UNIT_YI);
        } else if (value >= 10000 || value <= -10000) {
            return unitizeWithScale(value, unitizeScale, UNIT_WANG);
        }
        return unitizeWithScale(value, unitizeScale, "");
    }

    /**
     * 添加大额单位，默认 roundingMode 是 down
     *
     * @param value
     * @return
     */
    public static String unitize(double value) {
        return unitize(value, DEFAULT_SCALE);
    }

    /**
     * 添加大额单位，默认 roundingMode 是 down
     *
     * @param value
     * @return
     */
    public static String unitize(double value, int unitizeScale) {
        if (value >= 100000000 || value <= -100000000) {
            return unitizeWithScale(value, unitizeScale, UNIT_YI);
        } else if (value >= 10000 || value <= -10000) {
            return unitizeWithScale(value, unitizeScale, UNIT_WANG);
        }
        return unitizeWithScale(value, unitizeScale, "");
    }

    /**
     * 单位化数字，使用 roundingMode 是 down，不满足格式化条件的数据直接返回
     *
     * @param value
     * @param unitizeScale 单位化后的数据小数位数
     * @param unit
     * @return
     */
    private static String unitizeWithScale(int value, int unitizeScale, String unit) {
        return unitizeWithScale(value, unitizeScale, unit, 0);
    }

    /**
     * 单位化数字，使用 roundingMode 是 down，不满足格式化条件的数据直接用 scale 格式数据小数位数
     *
     * @param value
     * @param unitizeScale 单位化后的数据小数位数
     * @param unit
     * @return
     */
    private static String unitizeWithScale(double value, int unitizeScale, String unit) {
        return unitizeWithScale(value, unitizeScale, unit, unitizeScale);
    }

    /**
     * 单位化数字，使用 roundingMode 是 down，不满足格式化条件的数据直接用 scale 格式数据小数位数
     *
     * @param value
     * @param unitizeScale 单位化后的数据小数位数
     * @param unit
     * @param scale
     * @return
     */
    private static String unitizeWithScale(double value, int unitizeScale, String unit, int scale) {
        if (unit == UNIT_WANG) {
            BigDecimal newValue = divide(value, 10000.000, unitizeScale, RoundingMode.DOWN);
            return newValue.toString() + unit;
        } else if (unit == UNIT_YI) {
            BigDecimal newValue = divide(value, 100000000.000, unitizeScale, RoundingMode.DOWN);
            return newValue.toString() + unit;
        }
        return formatWithScale(value, scale, RoundingMode.DOWN);
    }

    /**
     * 格式化 String 数据, 精确（保留）到小数点后两位
     *
     * @param value
     * @return
     */
    public static String formatWithScale(String value, int scale) {
        double v = 0;
        if (!TextUtils.isEmpty(value)) {
            try {
                v = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return formatWithScale(v, scale);
    }

    /**
     * 格式化 String 数据, 并使用‘银行家算法’精确（保留）到小数点后两位
     *
     * @param value
     * @return
     */
    public static String formatWithScale(String value) {
        return formatWithScale(value, DEFAULT_SCALE);
    }

    /**
     * 精确（保留）到小数点后 2 位，默认 roundingMode: half_even
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String formatWithScale(double value) {
        return formatWithScale(value, DEFAULT_SCALE);
    }

    /**
     * 精确（保留）到小数点后 scale 位，默认 roundingMode: half_even
     *
     * @param value
     * @param scale 小数位数
     * @return 处理后的字符串
     */
    public static String formatWithScale(double value, int scale) {
        return formatWithScale(value, scale, RoundingMode.HALF_EVEN);
    }

    /**
     * 精确（保留）到小数点后 scale 位, 使用 roundingMode
     *
     * @param value
     * @param scale
     * @param roundingMode
     * @return
     */
    public static String formatWithScale(double value, int scale, RoundingMode roundingMode) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
        decimalFormat.setMaximumFractionDigits(scale);
        decimalFormat.setMinimumFractionDigits(scale);
        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setGroupingUsed(false);
        decimalFormat.setRoundingMode(roundingMode);
        String v = decimalFormat.format(value);
        return v;
    }

    /**
     * 使用千位分隔符分割 double 数据，默认 roundingMode 是 half_even
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String formatWithThousandsSeparator(int value) {
        return formatWithThousandsSeparator(value, 0);
    }

    /**
     * 使用千位分隔符分割 double 数据，默认 roundingMode 是 half_even
     *
     * @param value
     * @return 处理后的字符串
     */
    public static String formatWithThousandsSeparator(double value) {
        return formatWithThousandsSeparator(value, DEFAULT_SCALE);
    }

    /**
     * 使用千位分隔符分割 double 数据，默认 roundingMode 是 half_even
     *
     * @param value
     * @param scale 小数位数
     * @return 处理后的字符串
     */
    public static String formatWithThousandsSeparator(double value, int scale) {
        return formatWithThousandsSeparator(value, scale, RoundingMode.HALF_EVEN);
    }

    /**
     * 使用千位分隔符分割 double 数据
     *
     * @param value
     * @param scale        小数位数
     * @param roundingMode
     * @return
     */
    public static String formatWithThousandsSeparator(double value, int scale, RoundingMode roundingMode) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
        decimalFormat.setMaximumFractionDigits(scale);
        decimalFormat.setMinimumFractionDigits(scale);
        decimalFormat.setMinimumIntegerDigits(1);
        decimalFormat.setRoundingMode(roundingMode);
        decimalFormat.setGroupingSize(3);
        return decimalFormat.format(value);
    }

    /**
     * 格式化 double 数据，去除末尾的小数后多余的 0
     *
     * @param value
     * @return
     */
    public static String trimTrailingZero(double value) {
        DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getInstance();
        decimalFormat.applyPattern("#.#");
        return decimalFormat.format(value);
    }

    /**
     * 处理 Double 的大数据减法
     *
     * @param minuend    被减数
     * @param subtrahend 减数
     * @return 减法结果
     */
    public static BigDecimal subtraction(double minuend, double subtrahend) {
        BigDecimal bigMinuend = BigDecimal.valueOf(minuend);
        BigDecimal bigSubtrahend = BigDecimal.valueOf(subtrahend);
        return bigMinuend.subtract(bigSubtrahend);
    }

    /**
     * 处理 Double 的大数据减法
     *
     * @param minuend    被减数
     * @param subtrahend 减数
     * @return 减法结果
     */
    public static BigDecimal subtraction(Double minuend, Double subtrahend) {
        if (minuend == null || subtrahend == null) {
            throw new NullPointerException("Minuend or subtrahend is null");
        }
        return subtraction(minuend.doubleValue(), subtrahend.doubleValue());
    }

    /**
     * 处理 Double 的大数据除法
     *
     * @param dividend     被除数
     * @param divisor      除数
     * @param scale        保留小数位数
     * @param roundingMode 保留位数方式
     * @return 除法结果
     */
    public static BigDecimal divide(double dividend, double divisor, int scale, RoundingMode roundingMode) {
        BigDecimal bigDividend = BigDecimal.valueOf(dividend);
        BigDecimal bigDivisor = BigDecimal.valueOf(divisor);
        return bigDividend.divide(bigDivisor, scale, roundingMode);
    }

    /**
     * 处理 Double 的大数据除法
     *
     * @param dividend     被除数
     * @param divisor      除数
     * @param scale        保留小数位数
     * @param roundingMode 保留位数方式
     * @return 除法结果
     */
    public static BigDecimal divide(Double dividend, Double divisor, int scale, RoundingMode roundingMode) {
        if (dividend == null && divisor == null) {
            throw new NullPointerException("Dividend or divisor is null");
        }
        return divide(dividend.doubleValue(), divisor.doubleValue(), scale, roundingMode);

    }

    /**
     * 处理 Double 的大数据除法，默认使用‘银行家算法’保留2位小数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    保留小数位数
     * @return 除法结果
     */
    public static BigDecimal divide(double dividend, double divisor, int scale) {
        return divide(dividend, divisor, scale, RoundingMode.HALF_EVEN);
    }

    /**
     * 处理 Double 的大数据除法，默认使用‘银行家算法’保留2位小数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @param scale    保留小数位数
     * @return 除法结果
     */
    public static BigDecimal divide(Double dividend, Double divisor, int scale) {
        return divide(dividend, divisor, scale, RoundingMode.HALF_EVEN);
    }

    /**
     * 处理 Double 的大数据除法，默认使用‘银行家算法’保留2位小数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 除法结果
     */
    public static BigDecimal divide(double dividend, double divisor) {
        return divide(dividend, divisor, DEFAULT_SCALE, RoundingMode.HALF_EVEN);
    }

    /**
     * 处理 Double 的大数据除法，默认使用‘银行家算法’保留2位小数
     *
     * @param dividend 被除数
     * @param divisor  除数
     * @return 除法结果
     */
    public static BigDecimal divide(Double dividend, Double divisor) {
        return divide(dividend, divisor, DEFAULT_SCALE, RoundingMode.HALF_EVEN);
    }

    /**
     * 处理 Double 的大数据乘法
     *
     * @param multiplicand 被乘数
     * @param multiplier   乘数
     * @param scale        保留小数位数
     * @param roundingMode 保留位数方式
     * @return 乘法结果
     */
    public static BigDecimal multiply(double multiplicand, double multiplier, int scale, RoundingMode roundingMode) {
        BigDecimal bigMultiplicand = BigDecimal.valueOf(multiplicand);
        BigDecimal bigMultiplier = BigDecimal.valueOf(multiplier);
        return bigMultiplier.multiply(bigMultiplicand).setScale(scale, roundingMode);
    }

    /**
     * 处理 Double 的大数据乘法
     *
     * @param multiplicand 被乘数
     * @param multiplier   乘数
     * @param scale        保留小数位数
     * @param roundingMode 保留位数方式
     * @return 乘法结果
     */
    public static BigDecimal multiply(Double multiplicand, Double multiplier, int scale, RoundingMode roundingMode) {
        if (multiplicand == null || multiplier == null) {
            throw new NullPointerException("multiplicand or multiplier is null");
        }
        return multiply(multiplier.doubleValue(), multiplicand.doubleValue(), scale, roundingMode);
    }

    /**
     * 处理 Double 的大数据乘法
     *
     * @param multiplicand 被乘数
     * @param multiplier   乘数
     * @return 乘法结果
     */
    public static BigDecimal multiply(double multiplicand, double multiplier) {
        BigDecimal bigMultiplicand = BigDecimal.valueOf(multiplicand);
        BigDecimal bigMultiplier = BigDecimal.valueOf(multiplier);
        return bigMultiplier.multiply(bigMultiplicand);
    }

    /**
     * 处理 Double 的大数据乘法
     *
     * @param multiplicand 被乘数
     * @param multiplier   乘数
     * @return 乘法结果
     */
    public static BigDecimal multiply(Double multiplicand, Double multiplier) {
        if (multiplicand == null || multiplier == null) {
            throw new NullPointerException("multiplicand or multiplier is null");
        }
        return multiply(multiplicand.doubleValue(), multiplier.doubleValue());
    }

    /**
     * 处理 Double 的大数据加法
     *
     * @param summand 被加数
     * @param addend  加数
     * @return 加法结果
     */
    public static BigDecimal add(double summand, double addend) {
        BigDecimal bigSummand = BigDecimal.valueOf(summand);
        BigDecimal bigAddend = BigDecimal.valueOf(addend);
        return bigSummand.add(bigAddend);
    }

    /**
     * 处理 Double 的大数据加法
     *
     * @param summand 被加数
     * @param addend  加数
     * @return 加法结果
     */
    public static BigDecimal add(Double summand, Double addend) {
        if (summand == null || addend == null) {
            throw new NullPointerException("multiplicand or multiplier is null");
        }
        return add(summand.doubleValue(), addend.doubleValue());
    }
}
