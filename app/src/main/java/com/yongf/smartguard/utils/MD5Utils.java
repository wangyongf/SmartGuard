package com.yongf.smartguard.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yongf-new on 2016/2/4.
 */
public class MD5Utils {

    public static String md5Password(String password) {
        try {
            //得到一个信息摘要器
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] result = digest.digest(password.getBytes());
            StringBuilder buffer = new StringBuilder();
            //把每一个byte做一个与运算oxff
            for(byte b : result) {
                //与运算
                int number = b & 0xff;		//加盐
                String str = Integer.toHexString(number);
                System.out.println(str);
                if (str.length() == 1) {
                    buffer.append("0");
                }
                buffer.append(str);
            }

            //得到的是md5加密后的结果
            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();

            return "";
        }
    }
}
