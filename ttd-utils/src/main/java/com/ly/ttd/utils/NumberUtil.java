package com.ly.ttd.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;

/**
 * @author yong.li
 * @since 2026/7/8 10:38
 */
public final class NumberUtil {
    /**
     * Byte类型 0 常量值
     */
    public static final Byte BYTE_ZERO = (byte) 0;

    /**
     * Byte类型 1 常量值
     */
    public static final Byte BYTE_ONE = (byte) 1;

    /**
     * Short类型 0 常量值
     */
    public static final Short SHORT_ZERO = (short) 0;

    /**
     * Short类型 1 常量值
     */
    public static final Short SHORT_ONE = (short) 1;

    /**
     * Integer类型 0 常量值
     */
    public static final Integer INTEGER_ZERO = 0;

    /**
     * Integer类型 1 常量值
     */
    public static final Integer INTEGER_ONE = 1;

    /**
     * Long类型 0 常量值
     */
    public static final Long LONG_ZERO = 0L;

    /**
     * Long类型 1 常量值
     */
    public static final Long LONG_ONE = 1L;

    /**
     * Float类型 0 常量值
     */
    public static final Float FLOAT_ZERO = (float) 0;

    /**
     * Float类型 1 常量值
     */
    public static final Float FLOAT_ONE = (float) 1L;

    /**
     * Double类型 0 常量值
     */
    public static final Double DOUBLE_ZERO = (double) 0L;

    /**
     * Double类型 1 常量值
     */
    public static final Double DOUBLE_ONE = (double) 1L;

    /**
     * 获取Byte值, 如果number为null, 则返回0
     *
     * @param number Byte
     * @return 数值
     */
    public static Byte defaultNumber(Byte number) {
        return defaultNumber(number, BYTE_ZERO);
    }

    /**
     * 获取Short值, 如果number为null, 则返回0
     *
     * @param number Short
     * @return 数值
     */
    public static Short defaultNumber(Short number) {
        return defaultNumber(number, SHORT_ZERO);
    }

    /**
     * 获取Integer值, 如果number为null, 则返回0
     *
     * @param number Integer
     * @return 数值
     */
    public static Integer defaultNumber(Integer number) {
        return defaultNumber(number, INTEGER_ZERO);
    }

    /**
     * 获取Long值, 如果number为null, 则返回0
     *
     * @param number Long
     * @return 数值
     */
    public static Long defaultNumber(Long number) {
        return defaultNumber(number, LONG_ZERO);
    }

    /**
     * 获取Float值, 如果number为null, 则返回0
     *
     * @param number Float
     * @return 数值
     */
    public static Float defaultNumber(Float number) {
        return defaultNumber(number, FLOAT_ZERO);
    }

    /**
     * 获取Double值, 如果number为null, 则返回0
     *
     * @param number Double
     * @return 数值
     */
    public static Double defaultNumber(Double number) {
        return defaultNumber(number, DOUBLE_ZERO);
    }

    /**
     * 获取BigDecimal值, 如果number为null, 则返回0
     *
     * @param number BigDecimal
     * @return 数值
     */
    public static BigDecimal defaultNumber(BigDecimal number) {
        return defaultNumber(number, BigDecimal.ZERO);
    }

    /**
     * 获取BigInteger值, 如果number为null, 则返回0
     *
     * @param number BigInteger
     * @return 数值
     */
    public static BigInteger defaultNumber(BigInteger number) {
        return defaultNumber(number, BigInteger.ZERO);
    }

    /**
     * 获取Number值, 如果number为null, 则返回defNumber
     *
     * @param number Number
     * @return 数值
     */
    public static <T extends Number> T defaultNumber(T number, T defNumber) {
        return (number != null ? number : defNumber);
    }

    /**
     * 对numbers中所有数字求和
     *
     * @param numbers 整数数组
     * @return 求和结果
     */
    public static int sum(int[] numbers) {
        int rst = 0;
        if (numbers != null) {
            for (int number : numbers) {
                rst += number;
            }
        }

        return rst;
    }

    /**
     * 对numbers中所有数字求和
     *
     * @param numbers 长整数数组
     * @return 求和结果
     */
    public static long sum(long[] numbers) {
        long rst = 0;
        if (numbers != null) {
            for (long number : numbers) {
                rst += number;
            }
        }

        return rst;
    }

    /**
     * 对numbers中所有不为null的数字求和
     *
     * @param numbers 可变参数数字列表
     * @return 求和结果
     */
    public static BigDecimal sum(Number... numbers) {
        BigDecimal rst = BigDecimal.ZERO;
        if (numbers != null) {
            for (Number num : numbers) {
                if (num != null) {
                    rst = rst.add(asBigDecimal(num));
                }
            }
        }

        return rst;
    }

    /**
     * 对numbers中所有不为null的数字求和
     *
     * @param numbers 数字集合
     * @return 求和结果
     */
    public static BigDecimal sum(Collection<? extends Number> numbers) {
        BigDecimal rst = BigDecimal.ZERO;
        if (numbers != null) {
            for (Number num : numbers) {
                if (num != null) {
                    rst = rst.add(asBigDecimal(num));
                }
            }
        }

        return rst;
    }

    /**
     * 用 base 依次减去numbers中所有数字
     *
     * @param base    起始值
     * @param numbers 整数数组（减数）
     * @return 相减结果
     */
    public static int subtract(int base, int[] numbers) {
        int rst = base;
        if (numbers != null) {
            for (int num : numbers) {
                rst -= num;
            }
        }

        return rst;
    }

    /**
     * 用 base 依次减去numbers中所有数字
     *
     * @param base    起始值
     * @param numbers 长整数数组（减数）
     * @return 相减结果
     */
    public static long subtract(long base, long[] numbers) {
        long rst = base;
        if (numbers != null) {
            for (long num : numbers) {
                rst -= num;
            }
        }

        return rst;
    }

    /**
     * 用 base 依次减去numbers中所有不为null的数字
     *
     * @param base    起始值
     * @param numbers 可变参数数字列表（减数）
     * @return 相减结果
     */
    public static BigDecimal subtract(Number base, Number... numbers) {
        BigDecimal rst = BigDecimal.ZERO;
        if (base != null) {
            rst = rst.add(asBigDecimal(base));
        }

        if (numbers != null) {
            for (Number num : numbers) {
                if (num != null) {
                    rst = rst.subtract(asBigDecimal(num));
                }
            }
        }

        return rst;
    }

    /**
     * 用 base 依次减去numbers中所有不为null的数字
     *
     * @param base    起始值
     * @param numbers 数字集合（减数）
     * @return 相减结果
     */
    public static BigDecimal subtract(Number base, Collection<? extends Number> numbers) {
        BigDecimal rst = BigDecimal.ZERO;
        if (base != null) {
            rst = rst.add(asBigDecimal(base));
        }

        if (numbers != null) {
            for (Number num : numbers) {
                if (num != null) {
                    rst = rst.subtract(asBigDecimal(num));
                }
            }
        }

        return rst;
    }

    /**
     * 判断字符串是否只由数字组成，可包含负号
     *
     * @param number 字符串
     * @return true: 纯数字; false: 非纯数字或null
     */
    public static boolean isDigits(String number) {
        return (number != null && number.matches("-?\\d+"));
    }

    /**
     * 将字符串转换成Double，失败返回null
     *
     * @param number 数字字符串
     * @return Double值或null
     */
    public static Double parseDouble(String number) {
        return parseDouble(number, null);
    }

    /**
     * 将字符串转换成Double，失败返回默认值
     *
     * @param number 数字字符串
     * @param defNum 默认值
     * @return Double值或默认值
     */
    public static Double parseDouble(String number, Double defNum) {
        try {
            return (number != null && number.length() > 0 ? Double.valueOf(number) : defNum);
        } catch (Exception ex) {
            return defNum;
        }
    }

    /**
     * 将字符串转换成Long，失败返回null
     *
     * @param number 数字字符串
     * @return Long值或null
     */
    public static Long parseLong(String number) {
        return parseLong(number, null);
    }

    /**
     * 将字符串转换成Long，失败返回默认值
     *
     * @param number 数字字符串
     * @param defNum 默认值
     * @return Long值或默认值
     */
    public static Long parseLong(String number, Long defNum) {
        try {
            return (number != null && number.length() > 0 ? Long.valueOf(number) : defNum);
        } catch (Exception ex) {
            return defNum;
        }
    }

    /**
     * 将字符串转换成Integer，失败返回null
     *
     * @param number 数字字符串
     * @return Integer值或null
     */
    public static Integer parseInteger(String number) {
        return parseInteger(number, null);
    }


    /**
     * 将字符串转换成Integer，失败返回默认值
     *
     * @param number 数字字符串
     * @param defNum 默认值
     * @return Integer值或默认值
     */
    public static Integer parseInteger(String number, Integer defNum) {
        try {
            return (number != null && number.length() > 0 ? Integer.valueOf(number) : defNum);
        } catch (Exception ex) {
            return defNum;
        }
    }

    /**
     * 将字符串转换成BigDecimal，失败返回null
     *
     * @param number 数字字符串
     * @return BigDecimal值或null
     */
    public static BigDecimal parseBigDecimal(String number) {
        return parseBigDecimal(number, null);
    }

    /**
     * 将字符串转换成BigDecimal，失败返回默认值
     *
     * @param number 数字字符串
     * @param defNum 默认值
     * @return BigDecimal值或默认值
     */
    public static BigDecimal parseBigDecimal(String number, BigDecimal defNum) {
        try {
            return (number != null && number.length() > 0 ? new BigDecimal(number) : defNum);
        } catch (Exception ex) {
            return defNum;
        }
    }

    /**
     * 将数字转换为Integer，null返回null
     *
     * @param number 数字
     * @return Integer值或null
     */
    public static Integer asInteger(Number number) {
        return asInteger(number, null);
    }

    /**
     * 将数字转换为Integer，null返回默认值
     *
     * @param number       数字
     * @param defValIfNull 默认值
     * @return Integer值或默认值
     */
    public static Integer asInteger(Number number, Integer defValIfNull) {
        if (number == null) {
            return defValIfNull;

        } else if (number instanceof Integer) {
            return (Integer) number;
        }

        return number.intValue();
    }

    /**
     * 将数字转换为Long，null返回null
     *
     * @param number 数字
     * @return Long值或null
     */
    public static Long asLong(Number number) {
        return asLong(number, null);
    }

    /**
     * 将数字转换为Long，null返回默认值
     *
     * @param number       数字
     * @param defValIfNull 默认值
     * @return Long值或默认值
     */
    public static Long asLong(Number number, Long defValIfNull) {
        if (number == null) {
            return defValIfNull;

        } else if (number instanceof Long) {
            return (Long) number;
        }

        return number.longValue();
    }

    /**
     * 将数字转换为Double，null返回null
     *
     * @param number 数字
     * @return Double值或null
     */
    public static Double asDouble(Number number) {
        return asDouble(number, null);
    }

    /**
     * 将数字转换为Double，null返回默认值
     *
     * @param number       数字
     * @param defValIfNull 默认值
     * @return Double值或默认值
     */
    public static Double asDouble(Number number, Double defValIfNull) {
        if (number == null) {
            return defValIfNull;

        } else if (number instanceof Double) {
            return (Double) number;
        }

        return Double.valueOf(number.toString());
    }

    /**
     * 将数字转换为BigDecimal，null返回null
     *
     * @param number 数字
     * @return BigDecimal值或null
     */
    public static BigDecimal asBigDecimal(Number number) {
        return asBigDecimal(number, null);
    }

    /**
     * 将数字转换为BigDecimal，null返回默认值
     *
     * @param number       数字
     * @param defValIfNull 默认值
     * @return BigDecimal值或默认值
     */
    public static BigDecimal asBigDecimal(Number number, BigDecimal defValIfNull) {
        if (number == null) {
            return defValIfNull;

        } else if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }

        return new BigDecimal(number.toString());
    }

    /**
     * num1是否大于num2，任一为null返回false
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return true: num1 > num2; false: 不大于或任一为null
     */
    public static boolean greaterThan(BigDecimal num1, BigDecimal num2) {
        return (num1 != null && num2 != null && num1.compareTo(num2) > 0);
    }

    /**
     * num1是否大于num2，任一为null返回false
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return true: num1 > num2; false: 不大于或任一为null
     */
    public static boolean greaterThan(Integer num1, Integer num2) {
        return (num1 != null && num2 != null && num1.compareTo(num2) > 0);
    }

    /**
     * num1是否大于num2，任一为null返回false
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return true: num1 > num2; false: 不大于或任一为null
     */
    public static boolean greaterThan(Number num1, Number num2) {
        return (num1 != null && num2 != null && asBigDecimal(num1).compareTo(asBigDecimal(num2)) > 0);
    }

    /**
     * num1是否小于num2，任一为null返回false
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return true: num1 < num2; false: 不小于或任一为null
     */
    public static boolean lessThan(BigDecimal num1, BigDecimal num2) {
        return (num1 != null && num2 != null && num1.compareTo(num2) < 0);
    }

    /**
     * num1是否小于num2，任一为null返回false
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return true: num1 < num2; false: 不小于或任一为null
     */
    public static boolean lessThan(Integer num1, Integer num2) {
        return (num1 != null && num2 != null && num1.compareTo(num2) < 0);
    }

    /**
     * num1是否小于num2，任一为null返回false
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return true: num1 < num2; false: 不小于或任一为null
     */
    public static boolean lessThan(Number num1, Number num2) {
        return (num1 != null && num2 != null && asBigDecimal(num1).compareTo(asBigDecimal(num2)) < 0);
    }

    /**
     * 获取nums中的最大值
     *
     * @param nums BigDecimal可变参数
     * @return 最大值，nums为null则返回null
     */
    public static BigDecimal max(BigDecimal... nums) {
        if (null == nums) {
            return null;
        }

        BigDecimal max = null;
        for (BigDecimal num : nums) {
            if (max == null || (num != null && num.compareTo(max) > 0)) {
                max = num;
            }
        }

        return max;
    }

    /**
     * 获取nums中的最小值
     *
     * @param nums BigDecimal可变参数
     * @return 最小值，nums为null则返回null
     */
    public static BigDecimal min(BigDecimal... nums) {
        if (null == nums) {
            return null;
        }

        BigDecimal min = null;
        for (BigDecimal num : nums) {
            if (min == null || (num != null && num.compareTo(min) < 0)) {
                min = num;
            }
        }

        return min;
    }

    /**
     * 取负数
     *
     * @param num BigDecimal
     * @return 负值或null
     */
    public static BigDecimal negate(BigDecimal num) {
        return (num != null ? num.negate() : null);
    }

    /**
     * 取负数
     *
     * @param num Integer
     * @return 负值或null
     */
    public static Integer negate(Integer num) {
        return (num != null ? -num : null);
    }

    /**
     * 取负数
     *
     * @param num Long
     * @return 负值或null
     */
    public static Long negate(Long num) {
        return (num != null ? -num : null);
    }

    /**
     * 将数字转换为字符串
     *
     * @param number 数字
     * @return 字符串或null
     */
    public static String toString(Number number) {
        return (number != null ? number.toString() : null);
    }

    /**
     * 判断数字是否为整数值
     *
     * @param num 数字
     * @return true: 整数; false: 非整数或null
     */
    public static boolean isIntegral(Number num) {
        return isIntegral(asBigDecimal(num));
    }

    /**
     * 判断BigDecimal是否为整数值
     *
     * @param num BigDecimal
     * @return true: 整数; false: 非整数或null
     */
    public static boolean isIntegral(BigDecimal num) {
        if (num == null) {
            return false;
        }

        // 无小数 或 去除小数的结尾0后无小数
        return (getScaleExact(num) == 0);
    }

    /**
     * 获取数字的精确标度
     *
     * @param num 数字
     * @return 标度值，默认0
     */
    public static int getScaleExact(Number num) {
        return getScaleExact(asBigDecimal(num));
    }

    /**
     * 获取BigDecimal的精确标度（负数返回0）
     *
     * @param num BigDecimal
     * @return 大于等于0的标度值
     */
    public static int getScaleExact(BigDecimal num) {
        if (num == null) {
            return 0;
        }

        int scale = num.scale();
        if (scale > 0) {
            // 标度大于0，尝试去除结尾0
            scale = num.stripTrailingZeros().scale();
        }

        // BigDecimal(10).scale = 0
        // BigDecimal(10.00).scale = -1
        // 本方法负数统一为0
        return Math.max(scale, 0);
    }

    /**
     * 取绝对值
     *
     * @param num Long
     * @return 绝对值或null
     */
    public static Long abs(Long num) {
        if (num == null) {
            return null;
        }

        return Math.abs(num);
    }

    /**
     * 取绝对值
     *
     * @param num Integer
     * @return 绝对值或null
     */
    public static Integer abs(Integer num) {
        if (num == null) {
            return null;
        }

        return Math.abs(num);
    }

    /**
     * 取绝对值
     *
     * @param num BigDecimal
     * @return 绝对值或null
     */
    public static BigDecimal abs(BigDecimal num) {
        if (num == null) {
            return null;
        }

        return num.abs();
    }

    /**
     * 构造函数
     */
    private NumberUtil() {
    }
}
