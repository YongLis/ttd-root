package com.ly.ttd.utils;


import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日期工具类*
 */
public class DateUtil {
    public static String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss:SSS";
    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";


    private static final int[] TIME_FIELD_LEVELS =
            {Calendar.YEAR, Calendar.MONTH, Calendar.DATE, Calendar.HOUR_OF_DAY, Calendar.MINUTE, Calendar.SECOND,
                    Calendar.MILLISECOND};

    private static final Map<String, Integer> PERIOD_UNITS;

    private static final Pattern TERM_PATTERN = Pattern.compile("^[ ]*([0-9]+)[ ]*([Y|M|W|D|h|m|s]).*$");

    /**
     * 1分钟的毫秒数
     */
    public static final long ONE_MIN_MILLIS = 60 * 1000L;

    /**
     * 24小时的毫秒数
     */
    public static final long ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L;

    /**
     * 24小时的秒数
     */
    public static final int ONE_DAY_SECONDS = 24 * 60 * 60;

    /**
     * 日期格式 年月日时分秒
     */
    public static final String PATTERN_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    /**
     * 日期格式 年月日
     */
    public static final String PATTERN_YYYYMMDD = "yyyyMMdd";

    /**
     * 日期格式 年月
     */
    public static final String PATTERN_YYYYMM = "yyyyMM";

    /**
     * 日期格式 时分秒
     */
    public static final String PATTERN_HHMMSS = "HHmmss";

    /**
     * 日期格式集 年月日 + 年月日时分秒
     */
    public static final String[] PATTERNS_YYYYMMDD = {PATTERN_YYYYMMDD, PATTERN_YYYYMMDDHHMMSS};

    static {
        PERIOD_UNITS = new HashMap<>();
        PERIOD_UNITS.put("Y", Integer.valueOf(Calendar.YEAR));
        PERIOD_UNITS.put("M", Integer.valueOf(Calendar.MONTH));
        PERIOD_UNITS.put("W", Integer.valueOf(Calendar.WEEK_OF_MONTH));
        PERIOD_UNITS.put("D", Integer.valueOf(Calendar.DATE));
        PERIOD_UNITS.put("h", Integer.valueOf(Calendar.HOUR_OF_DAY));
        PERIOD_UNITS.put("m", Integer.valueOf(Calendar.MINUTE));
        PERIOD_UNITS.put("s", Integer.valueOf(Calendar.SECOND));
    }

    /**
     * 对齐日期对象到指定精度
     *
     * @param date  日期对象
     * @param field 要对齐的时间域，参考Calendar中field的定义
     * @return 对齐后的日期
     */
    public static Date roundDate(Date date, int field) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        roundDate(cal, field);

        return cal.getTime();
    }

    public static void roundDate(Calendar cal, int field) {
        boolean clearFlag = false;
        for (int i = 0; i < TIME_FIELD_LEVELS.length; i++) {
            if (clearFlag) {
                cal.set(TIME_FIELD_LEVELS[i], cal.getMinimum(TIME_FIELD_LEVELS[i]));
            } else if (TIME_FIELD_LEVELS[i] == field) {
                clearFlag = true;
            }
        }
    }

    /**
     * 调整日期对象
     *
     * @param date   日期对象
     * @param field  时间域，参考Calendar中field的定义
     * @param amount 调整数量
     * @return 调整后的日期对象
     */
    public static Date rollDate(Date date, int field, int amount) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.add(field, amount);
        return cal.getTime();
    }

    /**
     * 调整SQL日期对象
     *
     * @param date   日期对象
     * @param field  时间域，参考Calendar中field的定义
     * @param amount 调整数量
     * @return 调整后的日期对象
     */
    public static java.sql.Date rollDate(java.sql.Date date, int field, int amount) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.add(field, amount);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    public static void checkTerm(String term) {
        Matcher matcher = TERM_PATTERN.matcher(term);
        boolean matchFound = matcher.find();
        if (matchFound == false) {
            throw new IllegalArgumentException("Illegal term=[" + term + "].");
        }
    }

    /**
     * 调整时间
     *
     * @param date
     * @param term
     * @param incFlag true表示增加时间，false表示减少时间
     * @return
     */
    public static Date rollDate(Date date, String term, boolean incFlag) {
        String[] terms = parseDateTerm(term);

        int num = Integer.valueOf(terms[0], 10);
        String unit = terms[1];

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        if (incFlag) {
            cal.add(PERIOD_UNITS.get(unit), num);
        } else {
            cal.add(PERIOD_UNITS.get(unit), -num);
        }

        return cal.getTime();
    }

    /**
     * 解析日期期数字符串
     *
     * @param dateTerm
     * @return 返回 [期数, 期数类型]
     */
    public static String[] parseDateTerm(String dateTerm) {
        if (StringUtils.isBlank(dateTerm)) {
            throw new IllegalArgumentException("Date term cannot be blank");
        }

        Matcher matcher = TERM_PATTERN.matcher(dateTerm);
        if (matcher.find() == false || matcher.groupCount() != 2) {
            throw new IllegalArgumentException("Illegal date term, term=[" + dateTerm + "].");
        }

        String termNum = matcher.group(1);
        String termUnit = matcher.group(2);
        if (PERIOD_UNITS.containsKey(termUnit) == false) {
            throw new IllegalArgumentException("Illegal term unit, term=[" + dateTerm + "].");
        }

        return new String[]{termNum, termUnit};
    }

    /**
     * 获得日期对象的时间域值
     *
     * @param field 时间域，参考Calendar中field的定义
     * @return 对应时间域的值
     */
    public static int getDateField(Date date, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(field);
    }

    /**
     * 获得修改时间域值后的日期对象
     *
     * @param date  日期对象
     * @param field 时间域，参考Calendar中field的定义
     * @param value 时间域的值
     * @return 修改后的日期对象
     */
    public static Date setDateField(Date date, int field, int value) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(field, value);
        return cal.getTime();
    }

    /**
     * 获得修改时间域值后的日期对象
     *
     * @param date  日期对象
     * @param field 时间域，参考Calendar中field的定义
     * @param value 时间域的值
     * @return 修改后的日期对象
     */
    public static java.sql.Date setDateField(java.sql.Date date, int field, int value) {
        if (date == null) {
            return null;
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(field, value);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    /**
     * 根据给定参数打造时间对象, 毫秒数为零.
     *
     * @param year  年. 例 2009
     * @param month 月. 从1开始, 例 1
     * @param date  日. 从1开始, 例 10
     * @return 日期对象. 如果参数错误, 则返回null, 例 year=0; month=2, date=30
     */
    public static Date getDate(int year, int month, int date) {
        return getDate(year, month, date, 0, 0, 0);
    }

    /**
     * 根据给定参数打造时间对象, 毫秒数为零.
     *
     * @param year      年. 例 2009
     * @param month     月. 从1开始, 例 1
     * @param date      日. 从1开始, 例 10
     * @param hourOfDay 小时. 24小时制, 从0开始, 例 23
     * @param minute    分. 从0开始, 例 59
     * @param second    秒. 从0开始, 例 59
     * @return 日期对象. 如果参数错误, 则返回null, 例 year=0; month=2, date=30
     */
    public static Date getDate(int year, int month, int date, int hourOfDay, int minute, int second) {
        try {
            Calendar c = Calendar.getInstance();
            c.setLenient(false);
            c.clear();
            c.set(year, month - 1, date, hourOfDay, minute, second);

            return c.getTime();
        } catch (Exception ex) {
            return null;
        }
    }

    public static Date parse(String pattern, Locale locale, String str) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        try {
            return sdf.parse(str);
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Date parse(String pattern, String str) {
        return parse(pattern, Locale.getDefault(), str);
    }

    public static String formatQuietly(String pattern, Locale locale, Date date) {
        if (pattern == null || locale == null || date == null) {
            return null;
        }

        try {
            return format(pattern, locale, date);
        } catch (Exception ex) {
            return null;
        }
    }

    public static String formatQuietly(String pattern, Date date) {
        return formatQuietly(pattern, Locale.getDefault(), date);
    }

    public static String format(String pattern, Locale locale, Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
        return sdf.format(date);
    }

    public static String format(String pattern, Date date) {
        return format(pattern, Locale.getDefault(), date);
    }

    /**
     * 获取Date副本
     *
     * @param date 日期对象
     * @return 日期对象
     */
    public static Date getDate(Date date) {
        return (date != null ? roundDate(date, Calendar.DATE) : null);
    }

    /**
     * 获取java.sql.Date副本(自动重启)
     *
     * @param date 日期对象
     * @return 日期对象
     */
    public static java.sql.Date getSQLDate(Date date) {
        return (date != null ? new java.sql.Date(roundDate(date, Calendar.DATE).getTime()) : null);
    }

    /**
     * 根据给定参数打造java.sql.Date对象
     *
     * @param year
     * @param month
     * @param date
     * @return 日期对象. 如果参数错误, 则返回null
     */
    public static java.sql.Date getSQLDate(int year, int month, int date) {
        try {
            Calendar c = Calendar.getInstance();
            c.setLenient(false);
            c.clear();
            c.set(year, month - 1, date);

            return new java.sql.Date(c.getTimeInMillis());
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 在指定日期上增加指定时间(单位: 毫秒)
     *
     * @param date
     * @param millis
     * @return
     */
    public static Date addMillis(Date date, long millis) {
        return new Date(date.getTime() + millis);
    }

    /**
     * 获取两个日期间隔的秒数, 忽略毫秒, 返回正数代表date1大于date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int secondsBetween(Date date1, Date date2) {
        return secondsBetween(date1, date2, false);
    }

    /**
     * 获取两个日期间隔的秒数, 忽略毫秒
     *
     * @param date1
     * @param date2
     * @param abs   是否取绝对值
     * @return
     */
    public static int secondsBetween(Date date1, Date date2, boolean abs) {
        if (date1 == null) {
            throw new RuntimeException("Date1 cannot be null");

        } else if (date2 == null) {
            throw new RuntimeException("Date2 cannot be null");
        }

        int secDiff = (int) ((date1.getTime() - date2.getTime()) / 1000L);

        return (abs ? Math.abs(secDiff) : secDiff);
    }

    /**
     * 获取两个日期间隔的分钟数, 忽略秒, 返回正数代表date1大于date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int minutesBetween(Date date1, Date date2) {
        return minutesBetween(date1, date2, false);
    }

    /**
     * 获取两个日期间隔的分钟数, 忽略秒
     *
     * @param date1
     * @param date2
     * @param abs   是否取绝对值
     * @return
     */
    public static int minutesBetween(Date date1, Date date2, boolean abs) {
        if (date1 == null) {
            throw new RuntimeException("Date1 cannot be null");

        } else if (date2 == null) {
            throw new RuntimeException("Date2 cannot be null");
        }

        int minDiff = (int) ((date1.getTime() - date2.getTime()) / ONE_MIN_MILLIS);

        return (abs ? Math.abs(minDiff) : minDiff);
    }

    /**
     * 获取两个日期间隔的天数, 忽略时分秒, 返回正数代表date1大于date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int daysBetween(Date date1, Date date2) {
        return daysBetween(date1, date2, false);
    }

    /**
     * 获取两个日期间隔的天数, 忽略时分秒
     *
     * @param date1
     * @param date2
     * @param abs
     * @return
     */
    public static int daysBetween(Date date1, Date date2, boolean abs) {
        if (date1 == null) {
            throw new RuntimeException("Date1 cannot be null");

        } else if (date2 == null) {
            throw new RuntimeException("Date2 cannot be null");
        }

        date1 = roundDate(date1, Calendar.DATE);
        date2 = roundDate(date2, Calendar.DATE);
        int dayDiff = (int) ((date1.getTime() - date2.getTime()) / ONE_DAY_MILLIS);

        return (abs ? Math.abs(dayDiff) : dayDiff);
    }

    /**
     * 获取两个日期间隔的月份数, 忽略日时分秒, 返回正数代表date1大于date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int monthsBetween(Date date1, Date date2) {
        return monthsBetween(date1, date2, false);
    }

    /**
     * 获取两个日期间隔的月份数, 忽略日时分秒
     *
     * @param date1
     * @param date2
     * @param abs   返回结果是否去绝对值
     * @return
     */
    public static int monthsBetween(Date date1, Date date2, boolean abs) {
        if (date1 == null) {
            throw new RuntimeException("Date1 cannot be null");

        } else if (date2 == null) {
            throw new RuntimeException("Date2 cannot be null");
        }

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        int monthDiff = ((c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) * 12 +
                (c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH)));

        return (abs ? Math.abs(monthDiff) : monthDiff);
    }

    /**
     * 获取两个日期间隔的年数, 忽略月日时分秒，返回正数代表date1大于date2
     *
     * @param date1
     * @param date2
     * @return
     */
    public static int yearsBetween(Date date1, Date date2) {
        return yearsBetween(date1, date2, false);
    }

    /**
     * 获取两个日期间隔的年数, 忽略月日时分秒
     *
     * @param date1
     * @param date2
     * @param abs   返回结果是否去绝对值
     * @return
     */
    public static int yearsBetween(Date date1, Date date2, boolean abs) {
        if (date1 == null) {
            throw new RuntimeException("Date1 cannot be null");

        } else if (date2 == null) {
            throw new RuntimeException("Date2 cannot be null");
        }

        int yearDiff = (getDateField(date1, Calendar.YEAR) - getDateField(date2, Calendar.YEAR));

        return (abs ? Math.abs(yearDiff) : yearDiff);
    }

    public static int getCurrentQuarter(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        return month / 3;
    }

    public static Date nextQuarterFirstMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH);
        month = month % 3;
        cal.add(Calendar.MONTH, 3 - month);
        return cal.getTime();
    }

    public static boolean inTerm(String term, Date startTime, Date nowTime) {
        Matcher matcher = TERM_PATTERN.matcher(term);
        boolean matchFound = matcher.find();
        if (matchFound == false) {
            throw new IllegalArgumentException("Illegal term=[" + term + "].");
        }

        int num = Integer.valueOf(matcher.group(1), 10);
        String unit = matcher.group(2);

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(startTime.getTime());
        cal.add(PERIOD_UNITS.get(unit), num);

        return cal.getTime().compareTo(nowTime) >= 0;
    }

    /**
     * 是否在同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }

        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);

        return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 获取当天
     *
     * @return
     */
    public static Date getToday() {
        return roundDate(new Date(), Calendar.DATE);
    }

    /**
     * 获取下一天
     *
     * @return
     */
    public static Date getNextDay() {
        Date today = roundDate(new Date(), Calendar.DATE);
        return addMillis(today, ONE_DAY_MILLIS);
    }

    /**
     * 获取下N天
     *
     * @param date
     * @param days
     * @return
     */
    public static Date getNextDay(Date date, int days) {
        if (date == null) {
            date = new Date();
        }

        Date today = roundDate(date, Calendar.DATE);
        return addMillis(today, days * ONE_DAY_MILLIS);
    }

    /**
     * 获取上一天
     *
     * @return
     */
    public static Date getPrevDay() {
        Date today = roundDate(new Date(), Calendar.DATE);
        return addMillis(today, -ONE_DAY_MILLIS);
    }

    /**
     * 获取下N天
     *
     * @param date
     * @param days
     * @return
     */
    public static Date getPrevDay(Date date, int days) {
        if (date == null) {
            date = new Date();
        }

        Date today = roundDate(date, Calendar.DATE);
        return addMillis(today, days * -ONE_DAY_MILLIS);
    }

    /**
     * 获取指定日期的开始时间(00:00:00)
     *
     * @param date
     * @return
     */
    public static Date getStartTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * 获取指定日期的结束时间(23:59:59)
     *
     * @param date
     * @return
     */
    public static Date getEndTime(Date date) {
        if (date == null) {
            return null;
        }

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);

        return c.getTime();
    }

    /**
     * 获取日期数组中最小的日期
     *
     * @param dates
     * @return
     */
    public static Date min(Date... dates) {
        if (dates == null) {
            return null;
        }

        Date min = null;
        for (Date date : dates) {
            if (date == null) {
                continue;

            } else if (min == null || date.compareTo(min) < 0) {
                min = date;
            }
        }

        return min;
    }

    /**
     * 获取日期数组中最大的日期
     *
     * @param dates
     * @return
     */
    public static Date max(Date... dates) {
        if (dates == null) {
            return null;
        }

        Date max = null;
        for (Date date : dates) {
            if (date == null) {
                continue;

            } else if (max == null || date.compareTo(max) > 0) {
                max = date;
            }
        }

        return max;
    }


    /**
     * 将 java.util.Date 转换为 java.time.LocalDate
     */
    public static LocalDate dateToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 将 java.time.LocalDate 转换为 java.util.Date
     */
    public static Date localDateToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 java.util.Date 转换为 java.time.LocalDateTime
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 将 java.time.LocalDateTime 转换为 java.util.Date
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * 将 java.time.LocalDate 转换为指定格式的字符串
     */
    public static String format(LocalDate localDate, String pattern) {
        if (localDate == null) {
            throw new IllegalArgumentException("localDate must not be null");
        }
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be null or empty");
        }
        return localDate.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将 java.time.LocalDateTime 转换为指定格式的字符串
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            throw new IllegalArgumentException("localDateTime must not be null");
        }
        if (pattern == null || pattern.isEmpty()) {
            throw new IllegalArgumentException("pattern must not be null or empty");
        }
        return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
}