package com.ly.ttd.utils;

import com.alibaba.fastjson2.util.DateUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

/**
 * @author yong.li
 * @since 2026/5/20 16:50
 */
public class DateFormatUtils {
    public static String S_YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static String YYYY_MM_DD = "yyyy-MM-dd";
    public static String YYYY_MM = "yyyy-MM";
    public static String YYYY = "yyyy";

    public static String format(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        return DateUtils.format(date, pattern);
    }

    /**
     * 获取日期当月第一天时间
     */
    public static Date getMonthFirstDate(Date date) {
        return parse(format(date, "yyyy-MM") + "-01", YYYY_MM_DD);
    }

    /**
     * 获取日期当年第一天时间
     */
    public static Date getYearFirstDate(Date date) {
        return parse(format(date, "yyyy") + "-01-01", YYYY_MM_DD);
    }

    /**
     * 字符串转日期
     */
    public static Date parse(String dateStr, String pattern) {
        if (StringUtils.isEmpty(dateStr)) {
            return null;
        }
        return DateUtils.parseDate(dateStr, pattern);
    }

}
