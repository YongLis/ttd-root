package com.ly.ttd.utils;

import com.alibaba.fastjson.TypeReference;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericStaticFunctions {

    public static final String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
    public static final Pattern datePattern = Pattern.compile("^date\\s*\\('([0-9]+)'\\s*,\\s*'(\\S+)'\\)$");
    public static final int HOURS = 3600000;
    public static final int MINUTE = 60000;
    public static final int DAY = 86400000;
    public static final double EARTH_RADIUS = 6378137.0;

    public GenericStaticFunctions() {
    }

    // = "转换成日期"
    public static Date parseDate(Object value) {
        if (value == null) {
            return null;
        } else if (value instanceof Date) {
            return (Date) value;
        } else if (value instanceof String) {
            return parseDate((String) value);
        } else if (value instanceof Long) {
            return new Date((Long) value);
        } else {
            throw new RuntimeException("parse Date error" + value);
        }
    }

    // "加减小时"

    public static Date addHours(Date self, int hours) {
        return DateUtils.addHours(self, hours);
    }


    // "加减小时"
    public static List<Date> addHours(List<Date> self, int hours) {
        List<Date> values = new ArrayList<>();

        for (Date d : self) {
            if (d != null) {
                values.add(addHours(d, hours));
            }
        }

        return values;
    }


    // "加减月份"
    public static Date addMonths(Date self, int months) {
        return DateUtils.addMonths(self, months);
    }


    // "加减月份"
    public static List<Date> addMonths(List<Date> self, int months) {
        List<Date> values = new ArrayList<>();

        for (Date d : self) {
            if (d != null) {
                values.add(addMonths(d, months));
            }
        }

        return values;
    }


    // "加减天"
    public static Date addDays(Date self, int days) {
        return DateUtils.addDays(self, days);
    }


    // "加减天"
    public static List<Date> addDays(List<Date> self, int days) {
        List<Date> values = new ArrayList<>();

        for (Date d : self) {
            if (d != null) {
                values.add(addDays(d, days));
            }
        }

        return values;
    }


    // "加减年"
    public static Date addYears(Date self, int years) {
        return DateUtils.addYears(self, years);
    }


    // "加减年"
    public static List<Date> addYears(List<Date> self, int years) {
        List<Date> values = new ArrayList<>();

        for (java.util.Date d : self) {
            if (d != null) {
                values.add(addYears(d, years));
            }
        }

        return values;
    }


    // "加减周"
    public static Date addWeeks(Date self, int weeks) {
        return DateUtils.addWeeks(self, weeks);
    }


    // "加减周"
    public static List<Date> addWeeks(List<Date> self, int weeks) {
        List<Date> values = new ArrayList<>();

        for (java.util.Date d : self) {
            if (d != null) {
                values.add(addWeeks(d, weeks));
            }
        }

        return values;
    }


    // "加减分钟"
    public static Date addMinutes(Date self, int minutes) {
        return DateUtils.addMinutes(self, minutes);
    }


    // "加减分钟"
    public static List<Date> addMinutes(List<Date> self, int minutes) {
        List<Date> values = new ArrayList<>();

        for (java.util.Date d : self) {
            if (d != null) {
                values.add(addMinutes(d, minutes));
            }
        }

        return values;
    }


    // "加减秒"
    public static Date addSeconds(Date self, int seconds) {
        return DateUtils.addSeconds(self, seconds);
    }


    // "加减秒"
    public static List<Date> addSeconds(List<Date> self, int seconds) {
        List<Date> values = new ArrayList<>();

        for (Date d : self) {
            if (d != null) {
                values.add(addSeconds(d, seconds));
            }
        }

        return values;
    }


    // "获取目标时间当日小时数"
    public static Integer hours(Date self) {
        return DateUtils.toCalendar(self).get(Calendar.HOUR_OF_DAY);
    }


    // "获取目标时间当日小时数"
    public static List<Integer> hours(List<Date> self) {
        List<Integer> values = new ArrayList<>();

        for (java.util.Date d : self) {
            if (d != null) {
                values.add(hours(d));
            }
        }

        return values;
    }


    // "获取目标时间号数"
    public static Integer days(Date self) {
        return DateUtils.toCalendar(self).get(Calendar.DAY_OF_MONTH);
    }


    // "获取目标时间号数"
    public static List<Integer> days(List<Date> self) {
        List<Integer> values = new ArrayList<>();

        for (Date date : self) {
            if (date != null) {
                values.add(days(date));
            }
        }

        return values;
    }


    // "获取目标时间星期数"
    public static Integer weeks(Date self) {
        int w = DateUtils.toCalendar(self).get(Calendar.DAY_OF_WEEK);
        return w == 1 ? 7 : w - 1;
    }


    // "获取目标时间星期数"
    public static List<Integer> weeks(List<Date> self) {
        List<Integer> values = new ArrayList<>();

        for (Date date : self) {
            if (date != null) {
                values.add(weeks(date));
            }
        }

        return values;
    }


    // "获取目标时间当年月数"
    public static Integer months(Date self) {
        return DateUtils.toCalendar(self).get(Calendar.MONTH) + 1;
    }


    // "获取目标时间当年月数"
    public static List<Integer> months(List<Date> self) {
        List<Integer> values = new ArrayList<>();

        for (Date date : self) {
            if (date != null) {
                values.add(months(date));
            }
        }

        return values;
    }


    // "获取目标时间年数"
    public static Integer years(Date self) {
        return DateUtils.toCalendar(self).get(Calendar.YEAR);
    }

    public static List<Integer> years(List<Date> self) {
        List<Integer> values = new ArrayList<>();

        for (Date date : self) {
            if (date != null) {
                values.add(years(date));
            }
        }

        return values;
    }


    // "获取目标时间分钟数"
    public static Integer minutes(Date self) {
        return DateUtils.toCalendar(self).get(Calendar.MINUTE);
    }


    // "获取目标时间分钟数"
    public static List<Integer> minutes(List<Date> self) {
        List<Integer> values = new ArrayList<>();

        for (Date date : self) {
            if (date != null) {
                values.add(minutes(date));
            }
        }

        return values;
    }


    // "获取目标时间秒数"
    public static List<Integer> seconds(List<Date> self) {
        List<Integer> values = new ArrayList<>();

        for (Date date : self) {
            if (date != null) {
                values.add(seconds(date));
            }
        }

        return values;
    }


    // "获取目标时间秒数"
    public static Integer seconds(Date self) {
        return DateUtils.toCalendar(self).get(Calendar.SECOND);
    }


    // "获取最大值"
    public static <T extends Object & Comparable<? super T>> T max(Collection<? extends T> coll) {
        return Collections.max(coll);
    }


    // "获取最小值"
    public static <T extends Object & Comparable<? super T>> T min(Collection<? extends T> coll) {
        return Collections.min(coll);
    }


    // "self与other的秒数差"
    public static Integer diffSeconds(Date self, Date other) {
        long milliseconds = Math.abs(other.getTime() - self.getTime());
        long seconds = milliseconds / 1000L;
        return (int) seconds;
    }


    // "self与other的秒数差，other格式为yyyyMMdd或yyyyMMddHHmmss"
    public static Integer diffSeconds(Date self, String dateStr) {
        return diffSeconds(self, parseDate(dateStr));
    }


    // "self与other的秒数差，pattern为other的日期格式"
    public static Integer diffSeconds(Date self, String dateStr, String pattern) {
        return diffSeconds(self, DateUtil.parse(pattern, dateStr));
    }


    // "self与other的秒数差"
    public static List<Integer> diffSeconds(List<Date> self, Date other) {
        List<Integer> values = new ArrayList<>();

        for (Date d : self) {
            if (d != null) {
                values.add(diffSeconds(d, other));
            }
        }

        return values;
    }


    // "self与other的秒数差，other格式为yyyyMMdd或yyyyMMddHHmmss"
    public static List<Integer> diffSeconds(List<Date> self, String dateStr) {
        return diffSeconds(self, parseDate(dateStr));
    }


    // "self与other的秒数差，pattern为other的日期格式"
    public static List<Integer> diffSeconds(List<Date> self, String dateStr, String pattern) {
        return diffSeconds(self, DateUtil.parse(pattern.trim(), dateStr.trim()));
    }


    // "self与other的秒数差"
    public static Integer diffNowSeconds(Date self) {
        return diffSeconds(self, new Date());
    }


    // "self与other的秒数差"
    public static List<Integer> diffNowSeconds(List<Date> self) {
        return diffSeconds(self, new Date());
    }


    // "self与other的分钟差"
    public static Integer diffMinute(Date self, Date other) {
        long milliseconds = Math.abs(other.getTime() - self.getTime());
        long seconds = milliseconds / 60000L;
        return (int) seconds;
    }


    // "self与other的分钟差，other格式为yyyyMMdd或yyyyMMddHHmmss"
    public static Integer diffMinute(Date self, String dateStr) {
        return diffMinute(self, parseDate(dateStr));
    }


    // "self与other的分钟差，pattern为other的日期格式"
    public static Integer diffMinute(Date self, String dateStr, String pattern) {
        return diffMinute(self, DateUtil.parse(pattern, dateStr));
    }


    // "self与other的分钟差"
    public static List<Integer> diffMinute(List<Date> self, Date other) {
        List<Integer> values = new ArrayList<>();

        for (Date d : self) {
            if (d != null) {
                values.add(diffMinute(d, other));
            }
        }

        return values;
    }


    // "self与other的分钟差，other格式为yyyyMMdd或yyyyMMddHHmmss"
    public static List<Integer> diffMinute(List<Date> self, String dateStr) {
        return diffMinute(self, parseDate(dateStr));
    }


    // "self与other的分钟差，pattern为other的日期格式"
    public static List<Integer> diffMinute(List<Date> self, String dateStr, String pattern) {
        return diffMinute(self, DateUtil.parse(pattern.trim(), dateStr.trim()));
    }


    // "self与当前的分钟差"
    public static Integer diffNowMinute(Date self) {
        return diffMinute(self, new Date());
    }


    // "self与的分钟差"
    public static List<Integer> diffNowMinute(List<Date> self) {
        return diffMinute(self, new Date());
    }


    // "self与other的小时差"
    public static Integer diffHours(Date self, Date other) {
        long milliseconds = Math.abs(other.getTime() - self.getTime());
        long hours = milliseconds / 3600000L;
        return (int) hours;
    }


    // "self与other的小时差，other格式为yyyyMMdd或yyyyMMddHHmmss"
    public static Integer diffHours(Date self, String dateStr) {
        return diffHours(self, parseDate(dateStr));
    }


    // "self与other的小时差，pattern为other的日期格式"
    public static Integer diffHours(Date self, String dateStr, String pattern) {
        return diffHours(self, DateUtil.parse(pattern, dateStr));
    }


    // "self与other的小时差"
    public static List<Integer> diffHours(List<Date> self, Date other) {
        List<Integer> values = new ArrayList<>();

        for (Date d : self) {
            if (d != null) {
                values.add(diffHours(d, other));
            }
        }

        return values;
    }


    // "self与other的小时差，other格式为yyyyMMdd或yyyyMMddHHmmss"
    public static List<Integer> diffHours(List<Date> self, String dateStr) {
        return diffHours(self, parseDate(dateStr.trim()));
    }


    // "self与other的小时差，pattern为other的日期格式"
    public static List<Integer> diffHours(List<Date> self, String dateStr, String pattern) {
        return diffHours(self, DateUtil.parse(pattern, dateStr.trim()));
    }


    // "self与当前时间的小时差"
    public static Integer diffNowHours(Date self) {
        return diffHours(self, new Date());
    }


    // "self与当前时间的小时差"
    public static List<Integer> diffNowHours(List<Date> self) {
        return diffHours(self, new Date());
    }


    // "self与other的天数差"
    public static Integer diffDays(Date self, Date other) {
        long milliseconds = Math.abs(other.getTime() - self.getTime());
        long days = milliseconds / 86400000L;
        return (int) days;
    }


    // "self与other的天数差，other格式为yyyyMMdd或yyyyMMddHHmmss"
    public static Integer diffDays(Date self, String dateStr) {
        return diffDays(self, parseDate(dateStr));
    }

    // "self与other的天数差，pattern为other的日期格式"

    public static Integer diffDays(Date self, String dateStr, String pattern) {
        return diffDays(self, DateUtil.parse(pattern, dateStr));
    }

    // "self与other的天数差"

    public static List<Integer> diffDays(List<Date> self, Date other) {
        List<Integer> values = new ArrayList<>();

        for (Date d : self) {
            if (d != null) {
                values.add(diffDays(d, other));
            }
        }

        return values;
    }

    // "self与other的天数差，other格式为yyyyMMdd或yyyyMMddHHmmss"

    public static List<Integer> diffDays(List<Date> self, String dateStr) {
        return diffDays(self, parseDate(dateStr));
    }

    // "self与other的天数差，pattern为other的日期格式"

    public static List<Integer> diffDays(List<Date> self, String dateStr, String pattern) {
        return diffDays(self, DateUtil.parse(pattern.trim(), dateStr.trim()));
    }

    // "self与当前时间的天数差"

    public static Integer diffNowDays(Date self) {
        return diffDays(self, new Date());
    }

    // "self与当前时间的天数差"

    public static List<Integer> diffNowDays(List<Date> self) {
        return diffDays(self, new Date());
    }

    // "self是不是在dateStr与dateStr2之间，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean datesIn(Date self, String dateStr, String dateStr2) {
        Date date1 = parseDate(dateStr.trim());
        Date date2 = parseDate(dateStr2.trim());
        return self.after(date1) && self.before(date2);
    }

    // "self是不是有在dateStr与dateStr2之间，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean datesIn(List<Date> self, String dateStr, String dateStr2) {

        for (Date d : self) {
            if (d != null && datesIn(d, dateStr, dateStr2)) {
                return true;
            }
        }

        return false;
    }

    // "时间段selfStart到selfEnd和dateStart到dateEnd没有交集"

    public static boolean datesNotIn(Date selfStart, Date selfEnd, Date dateStart, Date dateEnd) {
        return selfStart.after(dateEnd) || selfEnd.before(dateStart);
    }

    // "时间段selfStart到selfEnd和dateStart到dateEnd没有交集"

    public static boolean datesAllNotIn(Date selfStart, Date selfEnd, List<Date> dateStr, List<Date> dateStr2) {
        if (dateStr != null && dateStr2 != null && dateStr.size() == dateStr2.size()) {
            for (int i = 0; i < dateStr.size(); ++i) {
                if (!datesNotIn(selfStart, selfEnd, dateStr.get(i), dateStr2.get(i))) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public static boolean datesAllIn(Date self, String dateStr, String dateStr2) {
        return datesIn(self, dateStr, dateStr2);
    }

    // "self是不是都在dateStr与dateStr2之间，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean datesAllIn(List<Date> self, String dateStr, String dateStr2) {

        for (Date d : self) {
            if (d == null || !datesIn(d, dateStr, dateStr2)) {
                return false;
            }
        }

        return true;
    }

    // "self是不是有在[year1,year2]之间"

    public static boolean yeasIn(Date self, int year1, int year2) {
        return years(self) >= year1 && years(self) <= year2;
    }

    // "self是不是有在[year1,year2]之间"

    public static boolean yeasIn(List<Date> self, int year1, int year2) {

        for (Date d : self) {
            if (d != null && yeasIn(d, year1, year2)) {
                return true;
            }
        }

        return false;
    }

    public static boolean yeasAllIn(Date self, int year1, int year2) {
        return yeasIn(self, year1, year2);
    }

    // "self是不是都在[year1,year2]之间"

    public static boolean yeasAllIn(List<Date> self, int year1, int year2) {

        for (Date d : self) {
            if (d == null || !yeasIn(d, year1, year2)) {
                return false;
            }
        }

        return true;
    }

    // "self是不是有在[month1,month2]之间"

    public static boolean monthsIn(Date self, int month1, int month2) {
        return months(self) >= month1 && months(self) <= month2;
    }

    // "self是不是有在[month1,month2]之间"

    public static boolean monthsIn(List<Date> self, int month1, int month2) {

        for (Date d : self) {
            if (d != null && monthsIn(d, month1, month2)) {
                return true;
            }
        }

        return false;
    }

    public static boolean monthsAllIn(Date self, int month1, int month2) {
        return monthsIn(self, month1, month2);
    }

    // "self是不是都在[month1,month2]之间"

    public static boolean monthsAllIn(List<Date> self, int month1, int month2) {

        for (Date d : self) {
            if (d == null || !monthsIn(d, month1, month2)) {
                return false;
            }
        }

        return true;
    }

    // "self是不是有在[day1,day2]之间,范围[1,31]"

    public static boolean daysIn(Date self, int day1, int day2) {
        if (day1 >= 0 && day2 >= 0) {
            if (day1 <= day2) {
                return days(self) >= day1 && days(self) <= day2;
            } else {
                return days(self) >= day1 || days(self) <= day2;
            }
        } else {
            return false;
        }
    }

    // "self是不是有在[day1,day2]之间,范围[1,31]"

    public static boolean daysIn(List<Date> self, int day1, int day2) {

        for (Date d : self) {
            if (d != null && daysIn(d, day1, day2)) {
                return true;
            }
        }

        return false;
    }

    public static boolean daysAllIn(Date self, int day1, int day2) {
        return daysIn(self, day1, day2);
    }

    // "self是不是都在[day1,day2]之间,范围[1,31]"

    public static boolean daysAllIn(List<Date> self, int day1, int day2) {

        for (Date d : self) {
            if (d == null || !daysIn(d, day1, day2)) {
                return false;
            }
        }

        return true;
    }

    // "self是不是有在[hour1,hour2]之间,24小时制,范围[0,24]"

    public static boolean hoursIn(Date self, int hour1, int hour2) {
        if (hour1 >= 0 && hour2 >= 0) {
            if (hour1 <= hour2) {
                return hours(self) >= hour1 && hours(self) <= hour2;
            } else {
                return hours(self) >= hour1 || hours(self) <= hour2;
            }
        } else {
            return false;
        }
    }

    // "self是不是有在[hour1,hour2]之间"

    public static boolean hoursIn(List<Date> self, int hour1, int hour2) {

        for (Date d : self) {
            if (d != null && hoursIn(d, hour1, hour2)) {
                return true;
            }
        }

        return false;
    }

    public static boolean hoursAllIn(Date self, int hour1, int hour2) {
        return hoursIn(self, hour1, hour2);
    }

    // "self是不是有在[hour1,hour2]之间"

    public static boolean hoursAllIn(List<Date> self, int hour1, int hour2) {

        for (Date d : self) {
            if (d == null || !hoursIn(d, hour1, hour2)) {
                return false;
            }
        }

        return true;
    }

    // "当other是self子集时返回true，或者任意一个参数为nothing时，返回false"

    public static <T> boolean allIn(Object self, Object other) {
        if (self != null && other != null) {
            if (self instanceof Collection) {
                return other instanceof Collection ? ((Collection) self).containsAll((Collection) other) : ((Collection) self).contains(other);
            } else if (other instanceof Collection other1) {
                if (other1.isEmpty()) {
                    return false;
                } else {
                    for (Object o : other1) {
                        if (!self.equals(o)) {
                            return false;
                        }
                    }

                    return true;
                }
            } else {
                return self.equals(other);
            }
        } else {
            return false;
        }
    }

    // "当self与other没有交集或者任意一个参数为nothing时，返回false"

    public static <T> boolean anyIn(Object self, Object other) {
        if (self != null && other != null) {
            if (self instanceof Collection) {
                return other instanceof Collection ? anyInList((Collection) self, (Collection) other) : anyInList((Collection) self, List.of(other));
            } else {
                return other instanceof Collection ? anyInList(List.of(self), (Collection) other) : self.equals(other);
            }
        } else {
            return false;
        }
    }

    private static <T> boolean anyInList(Collection<T> self, Collection<T> other) {
        if (self != null && other != null) {
            if (self.isEmpty() && other.isEmpty()) {
                return true;
            } else {
                Iterator it;
                if (self.size() < other.size()) {
                    for (T item : self) {
                        if (other.contains(item)) {
                            return true;
                        }
                    }
                } else {
                    for (T item : other) {
                        if (self.contains(item)) {
                            return true;
                        }
                    }
                }

                return false;
            }
        } else {
            return false;
        }
    }

    // "当self与other有交集返回false,任意一个为nothing时返回true"

    public static <T> boolean notIn(Object self, Object other) {
        return !anyIn(self, other);
    }

    // "忽略大小写，str1是否包含str2,任意一个为nothing时返回false"

    public static boolean contains(String str1, String str2) {
        return StringUtils.containsIgnoreCase(str1, str2);
    }

    public static boolean contains(String str1, List<String> str2) {
        if (CollectionUtils.isNotEmpty(str2) && StringUtils.isNotBlank(str1)) {

            for (String s : str2) {
                if (StringUtils.containsIgnoreCase(str1, s)) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    // "忽略大小写，集合str1至少有一个元素是否包含str2"

    public static boolean contains(List<String> str1, String str2) {
        if (CollectionUtils.isNotEmpty(str1) && StringUtils.isNotBlank(str2)) {

            for (String s : str1) {
                if (StringUtils.containsIgnoreCase(s, str2)) {
                    return true;
                }
            }

            return false;
        }
        return false;
    }

    // "忽略大小写，集合str1至少有一个元素是否包含集合str2一个元素"

    public static boolean contains(List<String> str1, List<String> str2) {
        if (CollectionUtils.isNotEmpty(str1) && CollectionUtils.isNotEmpty(str2)) {

            for (String s : str1) {
                for (String s2 : str2) {
                    if (StringUtils.containsIgnoreCase(s, s2)) {
                        return true;
                    }
                }
            }

            return false;
        }
        return false;
    }

    // "忽略大小写，集合str1是否都包含str2"

    public static boolean containsAll(List<String> str1, String str2) {
        if (CollectionUtils.isNotEmpty(str1) && StringUtils.isNotBlank(str2)) {

            for (String s : str1) {
                if (!StringUtils.containsIgnoreCase(s, str2)) {
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    public static boolean containsAll(String str1, String str2) {
        return contains(str1, str2);
    }

    public static boolean containsAll(String str1, List<String> str2) {
        if (CollectionUtils.isNotEmpty(str2) && StringUtils.isNotEmpty(str1)) {

            for (String s : str2) {
                if (!StringUtils.containsIgnoreCase(str1, s)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    // "集合str1是否都包含集合str2"

    public static boolean containsAll(List<String> str1, List<String> str2) {
        if (CollectionUtils.isNotEmpty(str1) && CollectionUtils.isNotEmpty(str2)) {

            for (String s : str1) {
                boolean found = false;
                for (String s2 : str2) {
                    if (StringUtils.containsIgnoreCase(s, s2)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static String substring(int start, String str1) {
        return StringUtils.substring(str1, start);
    }

    public static String substring(int begin, int end, String str1) {
        return StringUtils.substring(str1, begin, end);
    }

    // "截取字符串，start>0为字符串前，start<0为字符串后"

    public static List<String> substring(int start, Object... str1) {
        return substring(toListString(str1), start);
    }

    // "字符串是否相等"

    public static boolean equals(String str1, String str2) {
        return StringUtils.equals(str1, str2);
    }

    public static boolean equals(List<String> str1, String str2) {
        if (CollectionUtils.isEmpty(str1)) {
            return false;
        } else {
            for (String s : str1) {
                if (equals(s, str2)) {
                    return true;
                }
            }

            return false;
        }
    }

    // "字符串是否相等，忽略大小写"

    public static boolean equalsIgnoreCase(String str1, String str2) {
        return StringUtils.equalsIgnoreCase(str1, str2);
    }

    // "字符串是否相等，忽略大小写"

    public static boolean equalsIgnoreCase(List<String> str1, String str2) {
        if (CollectionUtils.isNotEmpty(str1) && str2 != null) {
            for (String s : str1) {
                if (equalsIgnoreCase(s, str2)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    // "数字是否相等"

    public static boolean equals(Number num1, Number num2) {
        if (num1 != null && num2 != null) {
            return num1.doubleValue() == num2.doubleValue();
        } else {
            return false;
        }
    }

    // "数字是否有相等"

    public static boolean equals(List<Number> num1, Number num2) {
        if (CollectionUtils.isEmpty(num1)) {
            return false;
        } else {
            for (Number s : num1) {
                if (equals(s, num2)) {
                    return true;
                }
            }

            return false;
        }
    }

    // "日期是否有相等"

    public static boolean equals(Date date1, Date date2) {
        if (date1 != null && date2 != null) {
            return date1.compareTo(date2) == 0;
        } else {
            return false;
        }
    }

    // "日期是否有相等"

    public static boolean equals(List<Date> date1, Date date2) {
        if (CollectionUtils.isEmpty(date1)) {
            return false;
        } else {
            for (Date s : date1) {
                if (equals(s, date2)) {
                    return true;
                }
            }

            return false;
        }
    }

    // "元素个数"

    public static <T> int length(Collection<T> d) {
        return CollectionUtils.size(d);
    }

    // "字符串长度"

    public static int length(String d) {
        return d == null ? 0 : d.length();
    }

    // "数字求和"

    public static BigDecimal sum(Object... obj) {
        List<Number> d = new ArrayList<>();
        Object[] var2 = obj;
        int var3 = obj.length;
        for (int var4 = 0; var4 < var3; ++var4) {
            Object o = var2[var4];
            if (o != null) {
                if (o instanceof List) {
                    d.addAll((List) o);
                } else {
                    d.add((Number) o);
                }
            }
        }

        return NumberUtil.sum(d);
    }

    // "数字相除,保留4位小数点，四舍五入"

    public static BigDecimal divide(Number num1, Number num2) {
        return divide(num1, num2, 4);
    }

    // "数字相除，四舍五入"

    public static BigDecimal divide(Number num1, Number num2, int i) {
        BigDecimal bigDecimal1 = NumberUtil.asBigDecimal(num1, BigDecimal.ZERO);
        BigDecimal bigDecimal2 = NumberUtil.asBigDecimal(num2);
        return bigDecimal1.divide(bigDecimal2, i, RoundingMode.HALF_UP);
    }

    // "数字相乘"

    public static BigDecimal multiply(Number num1, Number num2) {
        BigDecimal bigDecimal1 = NumberUtil.asBigDecimal(num1, BigDecimal.ZERO);
        BigDecimal bigDecimal2 = NumberUtil.asBigDecimal(num2);
        return bigDecimal1.multiply(bigDecimal2);
    }

    // "tList是不是有大于t"

    public static boolean greaterThan(List<Number> tList, Number t) {
        if (tList == null) {
            return false;
        } else {
            if (t == null) {
                t = 0;
            }

            for (Number n : tList) {
                if (n.doubleValue() > t.doubleValue()) {
                    return true;
                }
            }

            return false;
        }
    }

    // "n是不是有大于t"

    public static boolean greaterThan(Number n, Number t) {
        return n.doubleValue() > t.doubleValue();
    }

    // "date是不是在t之后，t时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean greaterThan(Date date, String t) {
        return date != null && StringUtils.isNotEmpty(t) && date.after(parseDate(t.trim()));
    }

    // "date是不是有在t之后，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean greaterThan(List<Date> tList, String t) {
        if (tList != null && StringUtils.isNotEmpty(t) && !tList.isEmpty()) {
            Date date1 = parseDate(t.trim());
            for (Date date : tList) {
                if (date != null && date.after(date1)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    // "tList是不是大于等于t"

    public static boolean greaterThanEqual(List<Number> tList, Number t) {
        if (tList == null) {
            return false;
        } else {
            if (t == null) {
                t = 0;
            }

            for (Number n : tList) {
                if (n != null && n.doubleValue() >= t.doubleValue()) {
                    return true;
                }
            }

            return false;
        }
    }

    // "n是不是大于等于t"

    public static boolean greaterThanEqual(Number n, Number t) {
        return n.doubleValue() >= t.doubleValue();
    }

    // "tList是不是大于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean greaterThanEqual(List<Date> tList, String t) {
        if (tList == null) {
            return false;
        } else if (StringUtils.isBlank(t)) {
            return false;
        } else {
            Date d1 = parseDate(t.trim());
            if (d1 == null) {
                return false;
            } else {
                for (Date n : tList) {
                    if (n != null && n.getTime() >= d1.getTime()) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    // "n是不是大于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean greaterThanEqual(Date n, String t) {
        Date date = parseDate(t.trim());
        if (date == null) {
            return false;
        } else {
            return n.getTime() >= date.getTime();
        }
    }

    // "tList是不是全大于t"

    public static boolean greaterThanAll(List<Number> tList, Number t) {
        if (tList == null) {
            return false;
        } else {
            if (t == null) {
                t = 0;
            }

            for (Number n : tList) {
                if (n == null || n.doubleValue() <= t.doubleValue()) {
                    return false;
                }
            }

            return true;
        }
    }

    // "tList是不是全大于t"

    public static boolean greaterThanAll(Number n, Number t) {
        return greaterThan(n, t);
    }

    // "tList是不是全大于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean greaterThanAll(List<Date> tList, String t) {
        if (tList != null && StringUtils.isNotEmpty(t) && !tList.isEmpty()) {
            Date date1 = parseDate(t.trim());
            for (Date date : tList) {
                if (date == null || !date.after(date1)) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    // "date是不是全大于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean greaterThanAll(Date date, String t) {
        if (date != null && StringUtils.isNotEmpty(t)) {
            return date.after(parseDate(t.trim()));
        } else {
            return false;
        }
    }

    // "tList是不是全大于等于t"

    public static boolean greaterThanEqualAll(List<Number> tList, Number t) {
        if (tList == null) {
            return false;
        } else {
            if (t == null) {
                t = 0;
            }

            for (Number n : tList) {
                if (n == null || n.doubleValue() < t.doubleValue()) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean greaterThanEqualAll(Number n, Number t) {
        return greaterThanEqual(n, t);
    }

    // "tList是不是全大于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean greaterThanEqualAll(List<Date> tList, String t) {
        if (tList == null) {
            return false;
        } else if (StringUtils.isBlank(t)) {
            return false;
        } else {
            Date d1 = parseDate(t.trim());
            if (d1 == null) {
                return false;
            } else {
                for (Date n : tList) {
                    if (n == null || n.getTime() < d1.getTime()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    // "tList是不是全大于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean greaterThanEqualAll(Date n, String t) {
        return greaterThanEqual(n, t);
    }

    // "tList是不是小于t"

    public static boolean lessThan(List<Number> tList, Number t) {
        if (tList != null && !tList.isEmpty()) {
            for (Number n : tList) {
                if (lessThan(n, t)) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    // "n是不是小于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean lessThan(Date n, String t) {
        return n == null || n.before(parseDate(t));
    }

    // "n是不是小于t"

    public static boolean lessThan(Number n, Number t) {
        if (n == null) {
            return true;
        } else {
            return t != null && n.doubleValue() < t.doubleValue();
        }
    }

    // "tList是不是小于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean lessThan(List<Date> tList, String t) {
        if (tList == null) {
            return false;
        } else if (StringUtils.isBlank(t)) {
            return false;
        } else {
            Date d = parseDate(t);
            for (Date n : tList) {
                if (n.before(d)) {
                    return true;
                }
            }

            return false;
        }
    }

    // "tList是不是小于等于t"

    public static boolean lessThanEqual(List<Number> tList, Number t) {
        if (tList != null && !tList.isEmpty()) {
            for (Number n : tList) {
                if (n.doubleValue() <= t.doubleValue()) {
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    // "n是不是小于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean lessThanEqual(Date n, String t) {
        Date date = parseDate(t);
        if (date == null) {
            return false;
        } else {
            return n.getTime() <= date.getTime();
        }
    }

    // "n是不是小于等于t"

    public static boolean lessThanEqual(Number n, Number t) {
        return n.doubleValue() <= t.doubleValue();
    }

    // "tList是不是小于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean lessThanEqual(List<Date> tList, String t) {
        if (tList == null) {
            return false;
        } else if (StringUtils.isBlank(t)) {
            return false;
        } else {
            Date d = parseDate(t);
            if (d == null) {
                return false;
            } else {
                for (Date n : tList) {
                    if (n.getTime() <= d.getTime()) {
                        return true;
                    }
                }

                return false;
            }
        }
    }

    // "tList是不是全小于t"

    public static boolean lessThanAll(List<Number> tList, Number t) {
        if (tList != null && !tList.isEmpty()) {
            for (Number n : tList) {
                if (n == null || n.doubleValue() >= t.doubleValue()) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    // "n是不是全小于t"

    public static boolean lessThanAll(Number n, Number t) {
        return lessThan(n, t);
    }

    // "tList是不是全小于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean lessThanAll(List<Date> tList, String t) {
        if (tList == null) {
            return false;
        } else if (StringUtils.isBlank(t)) {
            return false;
        } else {
            Date d = parseDate(t);
            if (d == null) {
                return false;
            } else {
                for (Date n : tList) {
                    if (n == null || n.getTime() > d.getTime()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    // "n是不是全小于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean lessThanAll(Date n, String t) {
        return lessThan(n, t);
    }

    // "tList是不是全小于等于t"

    public static boolean lessThanEqualAll(List<Number> tList, Number t) {
        if (tList != null && !tList.isEmpty()) {
            for (Number n : tList) {
                if (n == null || n.doubleValue() > t.doubleValue()) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    // "n是不是全小于等于t"

    public static boolean lessThanEqualAll(Number n, Number t) {
        return lessThanEqual(n, t);
    }

    // "tList是不是全小于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean lessThanEqualAll(List<Date> tList, String t) {
        if (tList != null && !tList.isEmpty()) {
            if (StringUtils.isBlank(t)) {
                return false;
            } else {
                Date d1 = parseDate(t.trim());
                if (d1 == null) {
                    return false;
                } else {
                    for (Date n : tList) {
                        if (n == null || n.getTime() > d1.getTime()) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        } else {
            return false;
        }
    }

    // "n是不是全小于等于t，时间格式为yyyyMMdd或yyyyMMddHHmmss"

    public static boolean lessThanEqualAll(Date n, String t) {
        return lessThanEqual(n, t);
    }

    // "字符串替换,在obj中用replacement替换searchString"

    public static List<String> replace(Collection<String> strs, String searchString, String replacement) {
        if (strs == null) {
            return null;
        } else {
            List<String> data = new ArrayList<>();

            for (String s : strs) {
                String ss = StringUtils.replace(s, searchString, replacement);
                data.add(ss);
            }

            return data;
        }
    }

    // "替换所有特殊字符"

    public static String replaceAllSpeChars(String str, String repalceStr) {
        return StringUtils.isBlank(str) ? str : str.replaceAll("[^0-9a-zA-z一-龥]|[\\ud800\\udc00-\\udbff\\udfff\\ud800-\\udfff]|[\\\\]", repalceStr);
    }

    // "特殊字符替换"

    public static String replace(String str, String oldIndex, String newIndex) {
        return StringUtils.isBlank(str) ? "" : str.replace(oldIndex, newIndex);
    }

    // "特殊字符替换"

    public static String replace(String str, List<String> oldIndexList, String newIndex) {
        if (StringUtils.isBlank(str)) {
            return "";
        } else {
            for (String s : oldIndexList) {
                if (StringUtils.isNotBlank(s)) {
                    str = str.replace(s, newIndex);
                }
            }

            return str;
        }
    }

    // "获取身份证年龄"

    public static int idAge(String idNo) {
        return IdCardUtil.getAgeByIdCard(idNo);
    }

    // "获取身份证年龄(周岁)"

    public static int getAge(String idNo) {
        int age = IdCardUtil.getAgeByIdCard(idNo);
        Calendar cal = Calendar.getInstance();
        int m = cal.get(Calendar.MONTH) + 1;
        int month = IdCardUtil.getMonthByIdCard(idNo);
        if (m < month) {
            --age;
        } else if (m == month) {
            int d = cal.get(Calendar.DATE);
            int day = IdCardUtil.getDateByIdCard(idNo);
            if (d < day) {
                --age;
            }
        }

        return age;
    }

    // "获取身份证年龄"

    public static List<Integer> idAge(List<String> idNos) {
        List<Integer> data = new ArrayList<>();

        for (String no : idNos) {
            data.add(idAge(no));
        }

        return data;
    }

    // "获取身份证性别"

    public static String idGender(String idNo) {
        return IdCardUtil.getGenderByIdCard(idNo);
    }

    // "获取身份证性别"

    public static List<String> idGender(List<String> idNos) {
        List<String> data = new ArrayList<>();

        for (String no : idNos) {
            data.add(idGender(no));
        }

        return data;
    }

    // "获取身份证生日，格式yyyyMMdd"

    public static String idBirth(String idNo) {
        return IdCardUtil.getBirthByIdCard(idNo);
    }

    public static Integer idBirthDay(String idNo) {
        return Integer.valueOf(IdCardUtil.getBirthByIdCard(idNo));
    }

    // "获取身份证生日,格式yyyyMMdd"

    public static List<String> idBirth(List<String> idNos) {
        List<String> data = new ArrayList<>();

        for (String no : idNos) {
            data.add(idBirth(no));
        }

        return data;
    }

    // "获取身份证出生年"

    public static Short idYears(String idNo) {
        return IdCardUtil.getYearByIdCard(idNo);
    }

    // "获取身份证出生年"

    public static List<Short> idYears(List<String> idNos) {
        List<Short> data = new ArrayList<>();

        for (String no : idNos) {
            data.add(idYears(no));
        }

        return data;
    }

    // "获取身份证出生月"

    public static int idMonths(String idNo) {
        return IdCardUtil.getMonthByIdCard(idNo);
    }

    // "获取身份证出生月"

    public static List<Integer> idMonths(List<String> idNos) {
        List<Integer> data = new ArrayList<>();

        for (String no : idNos) {
            data.add(idMonths(no));
        }

        return data;
    }

    // "获取身份证归属地省码"

    public static String idProvince(String idNo) {
        return "CHN" + idNo.substring(0, 2);
    }

    // "获取身份证归属地省码"

    public static List<String> idProvince(List<String> idNos) {
        List<String> data = new ArrayList<>();

        for (String no : idNos) {
            data.add(idProvince(no));
        }

        return data;
    }

    // "获取身份证国家归属地"

    public static String idCountryAddress(String idNos) {
        return "CHN" + IdCardUtil.getCityByIdCard(idNos);
    }

    // "获取身份证国家归属地"

    public static List<String> idCountryAddress(List<String> idNos) {
        List<String> data = new ArrayList<>();

        for (String no : idNos) {
            data.add(idCountryAddress(no));
        }

        return data;
    }

    // "获取身份证归属地"

    public static String idAddress(String idNos) {
        return "CHN" + IdCardUtil.getCityByIdCard(idNos);
    }

    // "获取身份证归属地"

    public static List<String> idAddress(List<String> idNos) {
        List<String> data = new ArrayList<>();

        for (String no : idNos) {
            data.add(idAddress(no));
        }

        return data;
    }

    // "取最后一个元素"

    public static <T> T tail(List<T> list) {
        return list == null ? null : list.get(list.size() - 1);
    }

    // "取第一个元素"

    public static <T> T head(List<T> list) {
        return list == null ? null : list.get(0);
    }

    public static <T> T head(T t) {
        return t;
    }

    public static <T> T tail(T t) {
        return t;
    }


    public static <T extends Comparable> List<T> distinct(T values) {
        List<T> data = new ArrayList<>();
        if (values != null) {
            data.add(values);
        }

        return removeNothing(data);
    }

    // "values移除item元素"

    public static <T extends Comparable> List<T> remove(List<T> values, T item) {
        if (values == null) {
            return new ArrayList<>();
        } else if (item == null) {
            return values;
        } else {
            values.remove(item);
            return removeNothing(values);
        }
    }

    // "values移除items集合"

    public static <T extends Comparable> List<T> remove(List<T> values, List<T> items) {
        if (values == null) {
            return new ArrayList<>();
        } else if (items == null) {
            return values;
        } else {
            values.removeAll(items);
            return removeNothing(values);
        }
    }

    // "values和items的交集"

    public static <T> List<T> intersect(List<T> values, List<T> items) {
        if (values != null && items != null && !values.isEmpty() && !items.isEmpty()) {
            values.retainAll(items);
            return removeNothing(values);
        } else {
            return new ArrayList<>();
        }
    }

    // "values和items的交集"

    public static <T> List<T> intersect(List<T> values, final T items) {
        return (List) (values != null && items != null && !values.isEmpty() && values.contains(items) ? Arrays.asList(new Object[]{items}) : new ArrayList<>());
    }

    // "元素是否为空"

    public static <T> boolean isNothing(T value) {
        if (null == value) {
            return true;
        } else {
            return value instanceof String && StringUtils.isBlank((String) value);
        }
    }

    // "如果value为空函数返回defaultValue,否则返回value"

    public static Object isNothingDefault(Object value, Object defaultValue) {
        return isNothing(value) ? defaultValue : value;
    }

    // "元素是否不为空"

    public static <T> boolean nonNothing(T value) {
        return !isNothing(value);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    // "计算data1和data2经纬度距离差"

    public static Double distance(String data1, String data2) {
        if (data1 != null && data2 != null) {
            String[] geo1 = data1.split(",");
            String[] geo2 = data2.split(",");
            return getDistance(Double.parseDouble(geo1[0]), Double.parseDouble(geo1[1]), Double.parseDouble(geo2[0]), Double.parseDouble(geo2[1]));
        } else {
            return null;
        }
    }

    // "val转为int"

    public static int toInt(Object val) {
        BigDecimal data;
        if (val instanceof String) {
            data = NumberUtils.createBigDecimal((String) val);
        } else {
            if (!(val instanceof Number)) {
                throw new NumberFormatException(val == null ? "null" : val.getClass().getName() + " object is not a valid number");
            }

            data = BigDecimal.valueOf(((Number) val).doubleValue());
        }

        return data.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    // "字符串连接"

    public static String join(Object o) {
        if (o instanceof Collection) {
            return StringUtils.join((Collection) o, ",");
        } else {
            return o instanceof String ? (String) o : o.toString();
        }
    }

    public static String join(Object o, String separator) {
        if (o instanceof Collection) {
            return StringUtils.join((Collection) o, separator);
        } else {
            return o instanceof String ? (String) o : o.toString();
        }
    }

    private static double getDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2.0 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2.0), 2.0) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2.0), 2.0)));
        s *= 6378137.0;
        s /= 1000.0;
        return BigDecimal.valueOf(Double.valueOf(s).doubleValue()).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    // "日期格式化"

    public static Date parseDate(String dateStr) {
        if (StringUtils.isBlank(dateStr)) {
            return null;
        } else {
            String stringValue = StringUtils.trim(dateStr);
            if (stringValue.startsWith("date('") && stringValue.endsWith("')")) {
                Matcher matcher = datePattern.matcher(stringValue);
                if (matcher.find() && matcher.groupCount() == 2) {
                    return DateUtil.parse(matcher.group(2), matcher.group(1));
                }
            }

            int length = stringValue.length();
            if (length <= "yyyyMMddHHmmssSSS".length()) {
                return DateUtil.parse("yyyyMMddHHmmssSSS".substring(0, length), stringValue);
            } else {
                return null;
            }
        }
    }

    private static List<String> toListString(Object[] obj) {
        List<String> data = new ArrayList<>();
        Object[] var2 = obj;
        int var3 = obj.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Object o = var2[var4];
            if (o != null) {
                if (o instanceof List) {
                    data.addAll((List) o);
                } else {
                    data.add((String) o);
                }
            }
        }

        return data;
    }

    private static List<String> substring(List<String> str1, int len) {
        List<String> data = new ArrayList<>();
        if (len > 0) {
            for (String s : str1) {
                if (s != null) {
                    data.add(StringUtils.substring(s, 0, len));
                }
            }
        } else {
            for (String s : str1) {
                if (s != null) {
                    data.add(StringUtils.substring(s, len));
                }
            }
        }

        return data;
    }

    private static <T> List<T> removeNothing(List<T> data) {
        data.removeIf(GenericStaticFunctions::isNothing);
        return data;
    }

    // "Object转float"

    public static float toFloat(Object val) {
        BigDecimal data;
        if (val instanceof String) {
            data = NumberUtils.createBigDecimal((String) val);
        } else {
            if (!(val instanceof Number)) {
                throw new NumberFormatException(val == null ? "null" : val.getClass().getName() + " object is not a valid number");
            }

            data = BigDecimal.valueOf(((Number) val).doubleValue());
        }

        return data.setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    // "BigDecimal m减去n"

    public static BigDecimal subtract(BigDecimal m, BigDecimal n) {
        if (m == null) {
            m = BigDecimal.ZERO;
        }

        if (n == null) {
            n = BigDecimal.ZERO;
        }

        return m.subtract(n);
    }

    // "String split"

    public static String[] splitString(String str, String index) {
        return StringUtils.isBlank(str) ? new String[0] : str.split(index);
    }

    // "计算Object的length"

    public static int calObjectLength(Object val) {
        if (val != null) {
            if (val instanceof String) {
                return ((String) val).trim().length();
            }

            if (val.getClass().isArray()) {
                return ((String[]) val).length;
            }

            if (val instanceof List) {
                return ((List) val).size();
            }

            if (val instanceof Map) {
                return ((Map) val).size();
            }

            if (val instanceof Set) {
                return ((Set) val).size();
            }
        }

        return 0;
    }

    // "计算对象长度大小"

    public static int len(Object val) {
        if (val != null) {
            if (val instanceof String) {
                return ((String) val).trim().length();
            } else if (val.getClass().isArray()) {
                return ((String[]) val).length;
            } else if (val instanceof Collection) {
                return ((Collection) val).size();
            } else {
                return val instanceof Map ? ((Map) val).size() : 1;
            }
        } else {
            return 0;
        }
    }

    // "多个数中取最小值"

    public static Number min(Object... obj) {
        Float temp = null;

        for (int i = 0; i < obj.length; ++i) {
            if (obj[i] != null) {
                Object number;
                if (obj[i] instanceof String) {
                    number = Float.valueOf((String) obj[i]);
                } else {
                    number = obj[i];
                }

                if (temp == null) {
                    temp = ((Number) number).floatValue();
                }

                if (((Number) number).floatValue() < temp) {
                    temp = ((Number) number).floatValue();
                }
            }
        }

        return temp;
    }

    // "合并两个Map"

    public static void merge(Map<String, Object> targetMap, Map<String, Object> output) {
        for (Map.Entry<String, Object> entry : output.entrySet()) {
            Object value = targetMap.get(entry.getKey());
            if (value instanceof Map) {
                if (entry.getValue() instanceof Map) {
                    merge((Map) value, (Map) entry.getValue());
                } else {
                    ((Map) value).put(entry.getKey(), entry.getValue());
                }
            } else if (value instanceof Collection) {
                if (entry.getValue() instanceof Collection) {
                    ((Collection) value).addAll((Collection) entry.getValue());
                } else {
                    ((Collection) value).add(entry.getValue());
                }
            } else {
                targetMap.put(entry.getKey(), entry.getValue());
            }
        }

    }

    private static Number createNumber(Object value) {
        if (value != null && !"null".equals(value)) {
            if (value instanceof Number) {
                return (Number) value;
            } else if (value instanceof String) {
                return StringUtils.isNotBlank((String) value) ? NumberUtils.createBigDecimal((String) value) : null;
            } else {
                String var10002 = value.getClass().getName();
                throw new NumberFormatException(var10002 + "[" + value + "] is not a valid number");
            }
        } else {
            return null;
        }
    }

    // "去除前后空白字符"

    public static String trim(String value) {
        return StringUtils.trim(value);
    }

    // "删除所有空白字符"

    public static String deleteWhitespace(String value) {
        return StringUtils.deleteWhitespace(value);
    }

    // "值不存在返回默认值"

    public static String getString(Object value, String defValue) {
        if (value == null) {
            return defValue;
        } else {
            return StringUtils.isNotBlank(value.toString()) ? value.toString() : defValue;
        }
    }

    // "值不存在返回默认值"

    public static Integer getInt(Object value, Integer defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : number.intValue();
    }

    // "值不存在返回默认值"

    public static Long getLong(Object value, Long defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : number.longValue();
    }

    // "值不存在返回默认值"

    public static BigDecimal getBigDecimal(Object value, BigDecimal defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : NumberUtil.asBigDecimal(number);
    }

    // "值不存在返回默认值"

    public static BigDecimal getBigDecimal(Object value, int scale, BigDecimal defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : NumberUtil.asBigDecimal(number).setScale(scale, RoundingMode.HALF_UP);
    }

    // "值不存在返回默认值"

    public static Double getDouble(Object value, Double defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : number.doubleValue();
    }

    // "值不存在返回默认值"

    public static Double getDouble(Object value, int scale, Double defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : NumberUtil.asBigDecimal(number).setScale(scale, RoundingMode.HALF_UP).doubleValue();
    }

    // "值不存在返回默认值"

    public static Short getShort(Object value, Short defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : number.shortValue();
    }

    // "值不存在返回默认值"

    public static Float getFloat(Object value, Float defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : number.floatValue();
    }

    // "值不存在返回默认值"

    public static Float getFloat(Object value, int scale, Float defValue) {
        Number number = createNumber(value);
        return number == null ? defValue : NumberUtil.asBigDecimal(number).setScale(scale, RoundingMode.HALF_UP).floatValue();
    }

    // "值不存在返回默认值"

    public static Boolean getBoolean(Object value, Boolean defValue) {
        if (value != null && !"null".equals(value)) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            } else if (value instanceof String) {
                Boolean aBoolean = BooleanUtils.toBooleanObject((String) value);
                if (aBoolean == null) {
                    throw new IllegalArgumentException("The String did not match either specified value:" + value);
                } else {
                    return aBoolean;
                }
            } else {
                String var10002 = value.getClass().getName();
                throw new IllegalArgumentException("The " + var10002 + " did not match either specified value:" + value);
            }
        } else {
            return defValue;
        }
    }

    // "值不存在返回默认值"

    public static Date getDate(Object value, Date defValue) {
        return value != null && !"null".equals(value) ? parseDate(value) : defValue;
    }

    // "字符串转Map"

    public static Map<String, Object> toMap(String json) {
        return TextUtils.toMapString(json);
    }

    // "字符串转List"

    public static List<Object> toList(String json) {
        return TextUtils.toListString(json);
    }

    // "字符串转XPath"

    public static DataPath toXPath(String json) {
        return new DataPath(json);
    }

    // "Map转XPath"

    public static DataPath toXPath(Map<String, Object> map) {
        return new DataPath(map);
    }

    // "2次反序列化字符串转Map"

    public static Map<String, Object> toMaps(String json) {
        return TextUtils.toMapString(TextUtils.parseObject(json, new TypeReference<String>() {
        }));
    }

    // "2次反序列化字符串转List"

    public static List<Object> toLists(String json) {
        return TextUtils.toListString(TextUtils.parseObject(json, new TypeReference<String>() {
        }));
    }

    // "2次反序列化字符串转XPath"

    public static DataPath toXPaths(String json) {
        return new DataPath(TextUtils.parseObject(json, new TypeReference<String>() {
        }));
    }

    // "对象转字符串"

    public static String toJSONString(Object object) {
        return TextUtils.toJSONString(object);
    }

    // "判断IPV4是外网地址"

    public static boolean isWANIpV4(String ip) {
        if (!isNothing(ip) && !ip.startsWith("192.168.") && !ip.startsWith("10.") && !ip.startsWith("127.")) {
            if (ip.startsWith("172.")) {
                int b = getInt(ip.split("\\.")[1], 16);
                return b <= 15 || b >= 32;
            }

            return true;
        } else {
            return false;
        }
    }

    public static boolean validIpV6(String ip) {
        try {
            InetAddress byName = InetAddress.getByName(ip);
            if (byName instanceof Inet6Address) {
                return true;
            }
        } catch (UnknownHostException var2) {
        }

        return false;
    }

    // "判断IP是外网地址"

    public static boolean isWANIp(String ip) {
        if (isNothing(ip)) {
            return false;
        } else if (ip.contains(":") && ip.contains(".")) {
            return true;
        } else {
            return ip.contains(":") && StringUtils.startsWithAny(ip, "2", "3") || isWANIpV4(ip);
        }
    }

    // "集合c按照predicate规则过滤"

    public static List<Object> filter(List<Object> c, Predicate<Object> predicate) {
        List<Object> objects = new ArrayList<>();
        if (CollectionUtils.isEmpty(c)) {
            return objects;
        } else {
            for (Object o : c) {
                if (predicate.test(o)) {
                    objects.add(o);
                }
            }

            return objects;
        }
    }

    // "对象c按照transform规则转换"

    public static List<Object> transform(Object c, Function<Object, Object> transform) {
        List<Object> objects = new ArrayList<>();
        if (c == null) {
            return objects;
        } else {
            if (c instanceof Collection) {
                for (Object o : (Collection) c) {
                    objects.add(transform.apply(o));
                }
            } else {
                objects.add(transform.apply(c));
            }

            return objects;
        }
    }

    // "集合c按照predicate规则过滤"

    public static List<Object> filter(Object c, Predicate<Object> predicate) {
        List<Object> objects = new ArrayList<>();
        if (c == null) {
            return objects;
        } else {
            if (predicate.test(c)) {
                objects.add(c);
            }

            return objects;
        }
    }


    // "按指定长度拆分字符串"
    public static List<String> spiltStr(String data, int length) {
        if (StringUtils.isNotEmpty(data) && length > 0) {
            ArrayList list;
            char[] strArr;
            int i;
            if (length == 1) {
                list = new ArrayList<>();
                strArr = data.toCharArray();
                int var7 = strArr.length;

                for (i = 0; i < var7; ++i) {
                    char tmp = strArr[i];
                    list.add(String.valueOf(tmp));
                }

                return list;
            } else if (data.length() <= length) {
                return List.of(data);
            } else {
                list = new ArrayList<>();
                strArr = data.toCharArray();
                StringBuffer buffer = new StringBuffer();

                for (i = 0; i < strArr.length; ++i) {
                    buffer.append(strArr[i]);
                    if (i > 0 && i % length == length - 1 || i == strArr.length - 1) {
                        list.add(buffer.toString());
                        buffer = new StringBuffer();
                    }
                }

                return list;
            }
        } else {
            return null;
        }
    }

    /**
     * 是否是国内身份证号码
     * 国内身份证前2位：11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,81,82,83
     * 长度15位或者18位
     */
    public static boolean isChinaIdCardNo(String idCardNo) {
        if (StringUtils.isBlank(idCardNo) || idCardNo.length() < 15 || idCardNo.length() > 18) {
            return false;
        }
        String innerCodeStr = "11,12,13,14,15,21,22,23,31,32,33,34,35,36,37,41,42,43,44,45,46,50,51,52,53,54,61,62,63,64,65,81,82,83";
        List<String> codeList = Arrays.asList(innerCodeStr.split(","));

        return codeList.contains(idCardNo.substring(0, 2)) && (idCardNo.length() == 15 || idCardNo.length() == 18);
    }

    public String findListFirst(List<String> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }

    /**
     * 获取指定索引的元素
     *
     * @param waitDeal 等待处理的字符串
     * @param splitStr 分割符
     * @param index    索引
     */
    public static String findIndexWithSplit(String waitDeal, String splitStr, Integer index) {
        if (StringUtils.isBlank(waitDeal) || StringUtils.isBlank(splitStr) || !waitDeal.contains(splitStr)) {
            return null;
        }
        String[] splitArray = waitDeal.split(splitStr);
        if (splitArray.length > index) {
            return splitArray[index];
        }
        return null;
    }
}
