package com.yongf.smartguard.domain;

import android.graphics.drawable.Drawable;

/**
 * Description:
 * Site: blog.54yongf.com | blog.csdn.net/yongf2014
 * Copyright (C), 1996 - 2016, Scott Wang
 * This program is protected by copyright laws.
 * Project Name: SmartGuard
 * Date: 2016/2/16 18:20
 *
 * @author Scott Wang 1059613472@qq.com
 * @version 1.0
 *          Details:
 *          进程信息的业务类
 */
public class TaskInfo {

    /**
     * 进程对应的应用图标
     */
    private Drawable icon;

    /**
     * 进程对应的应用名称
     */
    private String appName;

    /**
     * 进程对应的包名称
     */
    private String packName;

    /**
     * 占用的内存大小
     */
    private long memSize;

    /**
     * true-用户进程，false-系统进程
     */
    private boolean isUserTask;

    public boolean isChecked() {
        return isChecked;
    }

    public void setIsChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    /**
     * 被选中

     */
    private boolean isChecked;

    @Override
    public String toString() {
        return "TaskInfo{" +
                "icon=" + icon +
                ", appName='" + appName + '\'' +
                ", packName='" + packName + '\'' +
                ", memSize=" + memSize +
                ", isUserTask=" + isUserTask +
                ", isChecked=" + isChecked +
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

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public long getMemSize() {
        return memSize;
    }

    public void setMemSize(long memSize) {
        this.memSize = memSize;
    }

    public boolean isUserTask() {
        return isUserTask;
    }

    public void setIsUserTask(boolean isUserTask) {
        this.isUserTask = isUserTask;
    }
}
