package com.dingning.cardzs.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Allen on 2016/12/14.
 */

public class Md5Utils {

    private static final String ALGORITHM_MD5 = "MD5";
    private static final String UTF_8 = "UTF-8";

    public static String MD5_32bit(String str) {
        try {
            MessageDigest bmd5 = MessageDigest.getInstance(ALGORITHM_MD5);
            bmd5.update(str.getBytes());
            int i;
            StringBuffer buf = new StringBuffer();
            byte[] b = bmd5.digest();// 加密
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
