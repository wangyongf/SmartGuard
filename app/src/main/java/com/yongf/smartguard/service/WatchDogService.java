package com.yongf.smartguard.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.yongf.smartguard.EnterAppLockPWDActivity;
import com.yongf.smartguard.db.dao.AppLockDao;
import com.yongf.smartguard.utils.SystemInfoUtils;

import java.util.List;

/**
 * 看门狗代码，监视系统程序的运行状态
 *
 * @author Scott Wang ScottWang1996@gmail.com
 */
public class WatchDogService extends Service {

    private static final String TAG = "WatchDogService";
    private ActivityManager am;
    private boolean flag;
    private AppLockDao dao;
    private InnerReceiver innerReceiver;
    private String tempStopProtectPackName;
    private DataChangeReceiver dataChangeReceiver;

    private List<String> protectPackNames;
    private Intent intent;

    public WatchDogService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        innerReceiver = new InnerReceiver();
        registerReceiver(innerReceiver, new IntentFilter("com.yongf.smartguard.tempstop"));

        dataChangeReceiver = new DataChangeReceiver();
        registerReceiver(dataChangeReceiver, new IntentFilter("com.yongf.smartguard.applockchange"));

        dao = new AppLockDao(this);
        am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        flag = true;
        protectPackNames = dao.findAll();
        //当前应用需要保护。弹出来一个输入密码的界面
        intent = new Intent(getApplicationContext(), EnterAppLockPWDActivity.class);
        //服务是没有任务栈信息的，在服务开启activity，要指定这个activity运行的任务栈
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        new Thread() {
            @Override
            public void run() {
                while (flag) {
                    //noinspection deprecation,deprecation
                    List<ActivityManager.RunningTaskInfo> infos = am.getRunningTasks(1);
                    String packName = infos.get(0).topActivity.getPackageName();
//                    System.out.println("packName = " + packName);
                    List<String> homes = SystemInfoUtils.getHomes(WatchDogService.this);

                    //一旦返回桌面，清空tempStopProtectPackName
                    if (SystemInfoUtils.isHome(WatchDogService.this, homes)) {
                        tempStopProtectPackName = null;
                    }

//                    if (dao.find(packName)) {   //查询数据库太慢了，并且消耗资源，改成查询内存
                    if (protectPackNames.contains(packName)) {      //查询内存，效率高很多！

                        //判断这个应用程序是否需要临时停止保护
                        //noinspection StatementWithEmptyBody
                        if (packName.equals(tempStopProtectPackName)) {

                        } else {
                            //设置要保护的程序的包名
                            intent.putExtra("packName", packName);
                            startActivity(intent);
                        }

                    }

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        flag = false;

        unregisterReceiver(innerReceiver);
        innerReceiver = null;

        unregisterReceiver(dataChangeReceiver);
        dataChangeReceiver = null;

        super.onDestroy();
    }

    private class DataChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            protectPackNames = dao.findAll();
            System.out.println("数据库的内容变化了");
        }
    }

    private class InnerReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "接收到了临时停止保护的广播事件");
            tempStopProtectPackName = intent.getStringExtra("packName");
        }
    }
}
