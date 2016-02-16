package com.yongf.smartguard.engine;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.yongf.smartguard.R;
import com.yongf.smartguard.domain.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Site: blog.54yongf.com | blog.csdn.net/yongf2014
 * Copyright (C), 1996 - 2016, Scott Wang
 * This program is protected by copyright laws.
 * Project Name: SmartGuard
 * Date: 2016/2/16 18:19
 *
 * @author Scott Wang 1059613472@qq.com
 * @version 1.0
 *          Details:
 *          提供手机里面的进程信息
 */
public class TaskInfoProvider {

    /**
     * 获取所有的进程信息
     * @param context 上下文
     * @return
     */
    public static List<TaskInfo> getTaskInfos(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        PackageManager pm = context.getPackageManager();
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        List<TaskInfo> taskInfos = new ArrayList<>();
        for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
            TaskInfo taskInfo = new TaskInfo();
            //应用程序的包名
            String packName = processInfo.processName;
            taskInfo.setPackName(packName);

            Debug.MemoryInfo[] memoryInfos = am.getProcessMemoryInfo(new int[]{processInfo.pid});
            long memSize = memoryInfos[0].getTotalPrivateDirty() * 1024;        //byte
            taskInfo.setMemSize(memSize);
            try {
                ApplicationInfo applicationInfo = pm.getApplicationInfo(packName, 0);
                Drawable icon = applicationInfo.loadIcon(pm);
                taskInfo.setIcon(icon);

                String name = applicationInfo.loadLabel(pm).toString();
                taskInfo.setAppName(name);

                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //用户进程
                    taskInfo.setIsUserTask(true);
                } else {
                    //系统进程
                    taskInfo.setIsUserTask(false);
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
                taskInfo.setAppName(packName);
            }

            taskInfos.add(taskInfo);
        }

        return taskInfos;
    }
}
