package com.ly.ttd.utils;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * 身份证号工具类，支持 18 位和 15 位身份证号的解析与校验
 *
 * @author yong.li
 * @since 2026/7/8 10:40
 */
public class IdCardUtil {

    private static final Pattern PATTERN_18 = Pattern.compile("^[1-9]\\d{5}(19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$");
    private static final Pattern PATTERN_15 = Pattern.compile("^[1-9]\\d{5}\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}$");

    /**
     * 加权因子（前 17 位）
     */
    private static final int[] WEIGHT = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};
    /**
     * 校验码映射表
     */
    private static final char[] CHECK_CODE = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * 校验身份证号是否合法
     */
    public static boolean isValid(String idNo) {
        if (idNo == null || idNo.isEmpty()) {
            return false;
        }
        idNo = idNo.trim();
        if (PATTERN_18.matcher(idNo).matches()) {
            return validateCheckCode(idNo);
        }
        if (PATTERN_15.matcher(idNo).matches()) {
            return true;
        }
        return false;
    }

    /**
     * 15 位身份证号转 18 位
     */
    public static String convert15To18(String idNo) {
        if (idNo == null || idNo.length() != 15) {
            return idNo;
        }
        StringBuilder sb = new StringBuilder(idNo);
        sb.insert(6, "19");
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (sb.charAt(i) - '0') * WEIGHT[i];
        }
        sb.append(CHECK_CODE[sum % 11]);
        return sb.toString();
    }

    /**
     * 获取出生日期（LocalDate），非法返回 null
     */
    public static LocalDate getBirthDate(String idNo) {
        String birth = getBirthByIdCard(idNo);
        if (birth == null) {
            return null;
        }
        try {
            return LocalDate.parse(birth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据身份证号计算年龄（周岁），非法返回 -1
     */
    public static int getAgeByIdCard(String idNo) {
        LocalDate birthDate = getBirthDate(idNo);
        if (birthDate == null) {
            return -1;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    /**
     * 获取出生月份（1-12），非法返回 -1
     */
    public static int getMonthByIdCard(String idNo) {
        LocalDate birthDate = getBirthDate(idNo);
        if (birthDate == null) {
            return -1;
        }
        return birthDate.getMonthValue();
    }

    /**
     * 获取出生日期（当月第几天 1-31），非法返回 -1
     */
    public static int getDateByIdCard(String idNo) {
        LocalDate birthDate = getBirthDate(idNo);
        if (birthDate == null) {
            return -1;
        }
        return birthDate.getDayOfMonth();
    }

    /**
     * 获取性别，返回 "MALE" / "FEMALE"，非法返回 null
     */
    public static String getGenderByIdCard(String idNo) {
        String normalized = normalize(idNo);
        if (normalized == null) {
            return null;
        }
        int genderDigit = normalized.charAt(16) - '0';
        return (genderDigit & 1) == 1 ? "MALE" : "FEMALE";
    }

    /**
     * 获取出生日期字符串（yyyy-MM-dd），非法返回 null
     */
    public static String getBirthByIdCard(String idNo) {
        String normalized = normalize(idNo);
        if (normalized == null) {
            return null;
        }
        int year = Integer.parseInt(normalized.substring(6, 10));
        int month = Integer.parseInt(normalized.substring(10, 12));
        int day = Integer.parseInt(normalized.substring(12, 14));
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    /**
     * 获取出生年份，非法返回 null
     */
    public static Short getYearByIdCard(String idNo) {
        String normalized = normalize(idNo);
        if (normalized == null) {
            return null;
        }
        return Short.parseShort(normalized.substring(6, 10));
    }

    /**
     * 获取地区编码（前 6 位），非法返回 null
     */
    public static String getCityByIdCard(String idNo) {
        String normalized = normalize(idNo);
        if (normalized == null) {
            return null;
        }
        return normalized.substring(0, 6);
    }

    // ==================== 内部辅助方法 ====================

    /**
     * 统一转换为 18 位（15 位自动补全），非法返回 null
     */
    private static String normalize(String idNo) {
        if (idNo == null || idNo.isEmpty()) {
            return null;
        }
        String input = idNo.trim();
        if (input.length() == 15) {
            if (!PATTERN_15.matcher(input).matches()) {
                return null;
            }
            input = convert15To18(input);
        }
        if (input.length() != 18 || !PATTERN_18.matcher(input).matches()) {
            return null;
        }
        if (!validateCheckCode(input)) {
            return null;
        }
        return input.toUpperCase();
    }

    /**
     * 校验 18 位身份证的校验码
     */
    private static boolean validateCheckCode(String idNo) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idNo.charAt(i) - '0') * WEIGHT[i];
        }
        char expected = CHECK_CODE[sum % 11];
        return expected == Character.toUpperCase(idNo.charAt(17));
    }
}
