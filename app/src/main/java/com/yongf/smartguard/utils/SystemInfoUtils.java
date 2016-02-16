package com.yongf.smartguard.utils;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Description:
 * Site: blog.54yongf.com | blog.csdn.net/yongf2014
 * Copyright (C), 1996 - 2016, Scott Wang
 * This program is protected by copyright laws.
 * Project Name: SmartGuard
 * Date: 2016/2/16 12:32
 *
 * @author Scott Wang 1059613472@qq.com
 * @version 1.0
 *          Details:
 *          系统信息的工具类
 */
public class SystemInfoUtils {

    /**
     * 获取正在运行的进程的数量
     * @param context
     * @return
     */
    public static int getRunningProcessCount(Context context) {
        //包管理器，相当于程序管理器，管理静态的内容
//        PackageManager
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> infos = am.getRunningAppProcesses();

        return infos.size();
    }

    /**
     * 获取手机可用的剩余内存
     * @param context 上下文
     * @return
     */
    public static long getAvailMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);

        return outInfo.availMem;
    }

    /**
     * 获取手机可用的总内存
     * @param context 上下文
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMem(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(outInfo);

        return outInfo.totalMem;
    }

    /**
     * 获取手机可用的总内存--适用于Android2.3
     * @param context 上下文
     * @return long byte
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static long getTotalMemVersion2(Context context) {
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
//        am.getMemoryInfo(outInfo);
//
//        return outInfo.totalMem;
        try {
            File file = new File("/proc/meminfo");
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            String line = br.readLine();
            //MemTotal:     513000KB
            StringBuilder sb = new StringBuilder();
            for (char c : line.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    sb.append(c);
                }
            }

            return Long.parseLong(sb.toString()) * 1024;        //返回long byte
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
