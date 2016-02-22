package com.yongf.smartguard;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.List;

public class TrafficManagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_traffic_manager);

        //1. 获取一个包管理器
        PackageManager pm = getPackageManager();
        //2. 遍历手机操作系统，获取所有的应用程序的uid
        List<ApplicationInfo> infos = pm.getInstalledApplications(0);
        for (ApplicationInfo info : infos) {
            int uid = info.uid;
//            File rcvFile = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
//            File sndFile = new File("/proc/uid_stat/" + uid + "/tcp_snd");
            long txBytes = TrafficStats.getUidTxBytes(uid);     //发送的 上传的流量byte
            long rxBytes = TrafficStats.getUidRxBytes(uid);     //下载的流量byte
            //方法返回值 -1 代表的是应用程序没有产生流量，或者操作系统不支持流量统计
        }
        TrafficStats.getMobileTxBytes();    //获取手机3g/2g网络上传的总流量
        TrafficStats.getMobileRxBytes();    //手机2G/3G下载的总流量

        TrafficStats.getTotalTxBytes();     //手机全部网络接口，包括wifi,3g,2g上传的总流量
        TrafficStats.getTotalRxBytes();     //手机全部网络接口，包括wifi,3g,2g下载的总流量


    }
}
