package com.yongf.smartguard.utils;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by yongf-new on 2016/2/4.
 */
public class MD5Utils {

    public static String getTextMD5Signature(String password) {
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

    /**
     * 获取文件的SHA1签名
     *
     * @param path 文件路径
     * @return 文件的SHA1签名
     */
    public static String getFileSHA1Signature(String path) {
        //获取一个文件的特征信息，签名信息
        File file = new File(path);

        //SHA1
        try {
            MessageDigest digest = MessageDigest.getInstance("sha1");
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = -1;
            while ((len = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] result = digest.digest();
            StringBuffer sb = new StringBuffer();
            for (byte b : result) {
                //与运算
                int number = b & 0xff;      //加盐
                String str = Integer.toHexString(number);
                if (str.length() == 1) {
                    sb.append("0");
                }
                sb.append(str);
            }

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";
    }
}
