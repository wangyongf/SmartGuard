package com.yongf.smartguard.service;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.RemoteViews;

import com.yongf.smartguard.R;
import com.yongf.smartguard.receiver.MyWidget;
import com.yongf.smartguard.utils.SystemInfoUtils;

import java.util.Timer;
import java.util.TimerTask;

public class UpdateWidgetService extends Service {

    private ScreenOffReceiver offReceiver;
    private ScreenOnReceiver onReceiver;
    private ActivityManager am;

    private static final String TAG = "UpdateWidgetService";
    private Timer timer;
    private TimerTask task;

    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "锁屏了。。。");

            stopTimer();
        }
    }

    private class ScreenOnReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "屏幕解锁了。。。");

            startTimer();
        }
    }

    /**
     * widget管理器
     */
    private AppWidgetManager awm;

    @Override
    public void onCreate() {
        onReceiver = new ScreenOnReceiver();
        offReceiver = new ScreenOffReceiver();
        registerReceiver(onReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(offReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        awm = AppWidgetManager.getInstance(this);
        startTimer();
        super.onCreate();
    }

    private void startTimer() {
        if (timer != null && task != null) {
            return;
        }
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, "更新widget");
                ComponentName provider = new ComponentName(UpdateWidgetService.this, MyWidget.class);
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.my_widget);

                views.setTextViewText(R.id.tv_active_process_count,
                        "正在运行的进程:" + SystemInfoUtils.getRunningProcessCount(getApplicationContext()));

                long size = SystemInfoUtils.getAvailMem(getApplicationContext());
                views.setTextViewText(R.id.tv_process_memory,
                        "可用内存:" + Formatter.formatFileSize(getApplicationContext(), size));

                //自定义一个广播
                Intent intent = new Intent();
                intent.setAction("com.yongf.smartguard.clear");
                //描述一个动作，这个动作是由另外一个应用程序执行的
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

                awm.updateAppWidget(provider, views);
            }
        };
        timer.schedule(task, 0, 3000);
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(offReceiver);
        unregisterReceiver(onReceiver);
        offReceiver = null;
        onReceiver = null;
        stopTimer();
        super.onDestroy();
    }

    private void stopTimer() {
        if (timer != null && task != null) {
            timer.cancel();
            task.cancel();
            timer = null;
            task = null;
        }
    }

    public UpdateWidgetService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
