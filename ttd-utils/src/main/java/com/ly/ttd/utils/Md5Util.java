package com.ly.ttd.utils;

import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author yong.li
 * @since 2026/5/20 16:03
 */
public class Md5Util {

    /**
     * MD5加密
     *
     * @param input
     * @return
     */
    public static String MD5(String input) {
        if (StringUtils.isBlank(input)) {
            return null;
        }
        try {
            // 1. 获取 MD5 摘要算法的实例
            MessageDigest md = MessageDigest.getInstance("MD5");

            // 2. 计算输入字符串的摘要（哈希值），得到字节数组
            byte[] messageDigest = md.digest(input.getBytes());

            // 3. 将字节数组转换为十六进制字符串
            // 使用 BigInteger 可以方便地处理大数转换，1 表示正数
            BigInteger no = new BigInteger(1, messageDigest);
            String hashText = no.toString(16);

            // 4. 如果 hashText 长度不足 32 位，前面补 0
            while (hashText.length() < 32) {
                hashText = "0" + hashText;
            }
            return hashText;

        } catch (NoSuchAlgorithmException e) {
            // 理论上 "MD5" 算法总是存在的，但规范要求处理异常
            throw new RuntimeException(e);
        }
    }

}
