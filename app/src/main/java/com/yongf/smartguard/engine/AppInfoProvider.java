package com.yongf.smartguard.engine;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.yongf.smartguard.domain.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Site: blog.54yongf.com | blog.csdn.net/yongf2014
 * Copyright (C), 1996 - 2016, Scott Wang
 * This program is protected by copyright laws.
 * Project Name: SmartGuard
 * Date: 2016/2/14 11:35
 * Details:
 * 业务方法，用来提供手机里面安装的所有的应用程序信息
 * @author Scott Wang 1059613472@qq.com
 * @version 1.0
 */
public class AppInfoProvider {

    /**
     * 获取所有的安装的应用程序的信息
     * @param  context 上下文
     * @return
     */
    public static List<AppInfo> getAppInfos(Context context) {
        PackageManager pm = context.getPackageManager();
        //所有的安装在系统上的应用程序包信息
        List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
        List<AppInfo> appInfos = new ArrayList<>();
        for (PackageInfo packageInfo : packageInfos) {
            AppInfo appInfo = new AppInfo();
            //packInfo 相当于一个应用程序apk包的清单文件
            String packageName = packageInfo.packageName;
            Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
            String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
            int flags = packageInfo.applicationInfo.flags;      //应用程序信息的标记，相当于用户提交的答卷
            if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                //用户程序
                appInfo.setUserApp(true);
            } else {
                //系统程序
                appInfo.setUserApp(false);
            }
            if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
                //手机内存
                appInfo.setInRam(true);
            } else {
                //手机外存
                appInfo.setInRam(false);
            }

            appInfo.setPackageName(packageName);
            appInfo.setIcon(icon);
            appInfo.setAppName(appName);

            appInfos.add(appInfo);
        }

        return appInfos;
    }
}
