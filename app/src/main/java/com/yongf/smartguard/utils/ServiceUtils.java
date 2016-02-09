package com.yongf.smartguard.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

/**
 * Created by yongf-new on 2016/2/9 8:29.
 */
public class ServiceUtils {

    /**
     * 校验某个服务是否存活
     * @param context 上下文
     * @param serviceName 服务名称
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        //校验服务是否还活着
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> infos = am.getRunningServices(100);
        for (ActivityManager.RunningServiceInfo info : infos) {
            String name = info.service.getClassName();
            if (serviceName.equals(name)) {
                return true;
            }
        }

        return false;
    }
}
