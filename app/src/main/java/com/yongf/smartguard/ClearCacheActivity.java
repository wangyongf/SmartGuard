package com.yongf.smartguard;

import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDataObserver;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class ClearCacheActivity extends AppCompatActivity {

    private LinearLayout ll_container;
    private ProgressBar pb_scan;
    private TextView tv_scan_status;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_cache);

        ll_container = (LinearLayout) findViewById(R.id.ll_container);
        pb_scan = (ProgressBar) findViewById(R.id.pb_scan);
        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);

        scanCache();
    }

    /**
     * 扫描手机中所有应用程序的缓存信息
     */
    private void scanCache() {
        pm = getPackageManager();

        new Thread() {
            @Override
            public void run() {
                super.run();
                List<PackageInfo> packInfos = pm.getInstalledPackages(0);

                //获取PackageManager的所有的方法
                Method getPackageSizeInfo = null;
                Method[] methods = PackageManager.class.getMethods();
                for (Method method : methods) {
//                    System.out.println("method = " + method.getName());
                    if ("getPackageSizeInfo".equals(method.getName())) {
                        getPackageSizeInfo = method;
                    }
                }
                pb_scan.setMax(packInfos.size());
                int progress = 0;
                for (PackageInfo info : packInfos) {
                    try {
                        getPackageSizeInfo.invoke(pm, info.packageName, new MyDataObserver());
                        Thread.sleep(50);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    progress++;
                    pb_scan.setProgress(progress);
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_scan_status.setText("扫描完毕");
                    }
                });
            }
        }.start();
    }

    /**
     * 清理手机的全部缓存
     *
     * @param view
     */
    public void clearAll(View view) {
        Method[] methods = PackageManager.class.getMethods();
        for (Method method : methods) {
            if ("freeStorageAndNotify".equals(method.getName())) {
                try {
                    method.invoke(pm, Integer.MAX_VALUE, new MyPackDataObserver());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                //更新UI
                ll_container.removeAllViews();

                return;
            }
        }
    }

    private class MyDataObserver extends IPackageStatsObserver.Stub {

        @Override
        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
            final long cacheSize = pStats.cacheSize;
            long codeSize = pStats.codeSize;
            long dataSize = pStats.dataSize;
            final String packName = pStats.packageName;
            final ApplicationInfo appInfo;
            try {
                appInfo = pm.getApplicationInfo(packName, 0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_scan_status.setText("正在扫描:" + appInfo.loadLabel(pm));
                        if (cacheSize > 0) {
//                            TextView tv = new TextView(getApplicationContext());
//                            tv.setText(appInfo.loadLabel(pm) + "-缓存大小:" + Formatter.formatFileSize(getApplicationContext(),
//                                    cacheSize));
//                            tv.setTextColor(Color.BLACK);
                            View view = View.inflate(getApplicationContext(), R.layout.list_item_cacheinfo, null);

                            TextView tv_cache_size = (TextView) view.findViewById(R.id.tv_cache_size);
                            tv_cache_size.setText("缓存大小:" + Formatter.formatFileSize(getApplicationContext(), cacheSize));

                            TextView tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                            tv_app_name.setText(appInfo.loadLabel(pm));

                            ImageView iv_clear_cache = (ImageView) view.findViewById(R.id.iv_clear_cache);
                            iv_clear_cache.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //deleteApplicationCacheFiles()
                                    try {
                                        Method method = PackageManager.class.getMethod("deleteApplicationCacheFiles", String.class,
                                                IPackageDataObserver.class);
                                        method.invoke(pm, packName, new MyPackDataObserver());
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            ll_container.addView(view, 0);
                        }
                    }
                });

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

//            System.out.println("dataSize = " + Formatter.formatFileSize(getApplicationContext(), dataSize));
//            System.out.println("codeSize = " + Formatter.formatFileSize(getApplicationContext(), codeSize));
//            System.out.println("cacheSize = " + Formatter.formatFileSize(getApplicationContext(), cacheSize));
//
//            String packageName = pStats.packageName;
//            System.out.println("packageName = " + packageName);
//            System.out.println("------------------------------------------------");
        }
    }

    private class MyPackDataObserver extends IPackageDataObserver.Stub {

        @Override
        public void onRemoveCompleted(String packageName, boolean succeeded) throws RemoteException {
            System.out.println(packageName + succeeded);
        }
    }
}
