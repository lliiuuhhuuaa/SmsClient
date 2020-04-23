package com.lh.sms.client.framing.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class SecurityUtil {
    /**
     * @do 生成随机字符
     * @author liuhua
     * @date 2020/4/20 10:53 PM
     */
    public static String randomStr(int num) {
        char[] randomMetaData = new char[] { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o',
                'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4',
                '5', '6', '7', '8', '9' };
        Random random = new Random();
        String tNonceStr = "";
        for (int i = 0; i < num; i++) {
            tNonceStr += (randomMetaData[random.nextInt(randomMetaData.length - 1)]);
        }
        return tNonceStr;
    }
    /**
     * @do MD5 32为加密
     * @author liuhua
     * @date 2020/4/20 10:55 PM
     */
    public static String md5(byte[] str) {
        if (str == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.reset();
            md.update(str);
            byte[] encodedPassword = md.digest();
            for (int i = 0; i < encodedPassword.length; i++) {
                if ((encodedPassword[i] & 0xff) < 0x10) {
                    buf.append("0");
                }
                buf.append(Long.toString(encodedPassword[i] & 0xff, 16));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        return buf.toString();
    }
}
