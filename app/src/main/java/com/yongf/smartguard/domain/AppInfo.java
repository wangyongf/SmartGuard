package com.yongf.smartguard.domain;

import android.graphics.drawable.Drawable;

/**
 * Description:
 * Site: blog.54yongf.com | blog.csdn.net/yongf2014
 * Copyright (C), 1996 - 2016, Scott Wang
 * This program is protected by copyright laws.
 * Project Name: SmartGuard
 * Date: 2016/2/14 11:37
 * Details:
 * 应用程序信息的业务bean
 * @author Scott Wang 1059613472@qq.com
 * @version 1.0
 */
public class AppInfo {

    private Drawable icon;
    private String appName;
    private String packageName;
    private boolean inRam;
    private boolean userApp;

    @Override
    public String toString() {
        return "AppInfo{" +
                "icon=" + icon +
                ", appName='" + appName + '\'' +
                ", packageName='" + packageName + '\'' +
                ", inRam=" + inRam +
                ", userApp=" + userApp +
                '}';
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isInRam() {
        return inRam;
    }

    public void setInRam(boolean inRam) {
        this.inRam = inRam;
    }

    public boolean isUserApp() {
        return userApp;
    }

    public void setUserApp(boolean userApp) {
        this.userApp = userApp;
    }
}
