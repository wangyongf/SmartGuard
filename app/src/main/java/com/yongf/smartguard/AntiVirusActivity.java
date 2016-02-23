package com.yongf.smartguard;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yongf.smartguard.db.dao.AntiVirusDao;
import com.yongf.smartguard.utils.MD5Utils;

import java.util.List;

public class AntiVirusActivity extends AppCompatActivity {

    private static final int SCANNING = 0;
    private static final int FINISH = 2;
    private ImageView iv_scan;
    private ProgressBar pb_scan;
    private PackageManager pm;
    private TextView tv_scan_status;
    private LinearLayout ll_scanner_status;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SCANNING:      //正在扫描
                    ScanInfo scanInfo = (ScanInfo) msg.obj;
                    tv_scan_status.setText("正在扫描" + scanInfo.appName);
                    TextView tv = new TextView(getApplicationContext());
                    if (scanInfo.isVirus) {
                        tv.setTextColor(Color.RED);
                        tv.setText("发现病毒:" + scanInfo.appName);
                    } else {
                        tv.setTextColor(Color.BLACK);
                        tv.setText("扫描安全:" + scanInfo.appName);
                    }
                    ll_scanner_status.addView(tv, 0);
                    break;
                case FINISH:    //扫描完毕
                    tv_scan_status.setText("扫描完毕");
                    iv_scan.clearAnimation();

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);

        tv_scan_status = (TextView) findViewById(R.id.tv_scan_status);
        ll_scanner_status = (LinearLayout) findViewById(R.id.ll_scanner_status);
        iv_scan = (ImageView) findViewById(R.id.iv_scan);
//        iv_scan.getBackground().setAlpha(100);
        AlphaAnimation aa = new AlphaAnimation(0, 255);
        aa.setDuration(1000);
        aa.setRepeatMode(Animation.INFINITE);
        iv_scan.startAnimation(aa);

        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        ra.setDuration(1000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_scan.startAnimation(ra);

        pb_scan = (ProgressBar) findViewById(R.id.pb_scan);

        scanVirus();

//        pb_scan.setMax(100);
//        new Thread() {
//            @Override
//            public void run() {
//                super.run();
//                for (int i = 0; i < 100; i++) {
//                    try {
//                        Thread.sleep(100);
//                        pb_scan.setProgress(i);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
    }

    /**
     * 扫描病毒
     */
    private void scanVirus() {
        pm = getPackageManager();
        tv_scan_status.setText("正在初始化保护伞杀毒引擎...");

        new Thread() {
            @Override
            public void run() {
                super.run();
                List<PackageInfo> infos = pm.getInstalledPackages(0);

                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                pb_scan.setMax(infos.size());
                int progress = 0;
                for (PackageInfo info : infos) {
                    String dataDir = info.applicationInfo.dataDir;
                    //apk文件的完整路径
                    String sourceDir = info.applicationInfo.sourceDir;      //apk apk图片
                    //zip
//                    ZipFile zipFile = new ZipFile(file);
//                    zipFile.getEntry("AndroidManifest.xml");

                    String signature = MD5Utils.getFileMD5Signature(sourceDir);
                    System.out.println(info.applicationInfo.loadLabel(pm) + ": " + signature);

                    ScanInfo scanInfo = new ScanInfo();
                    scanInfo.appName = info.applicationInfo.loadLabel(pm).toString();
                    scanInfo.packName = info.packageName;

                    //查询SHA1信息，是否在病毒数据库里面存在
                    if (AntiVirusDao.isVirus(signature)) {
                        //发现病毒
                        scanInfo.isVirus = true;
                    } else {
                        //扫描安全
                        scanInfo.isVirus = false;
                    }
                    Message msg = Message.obtain();
                    msg.obj = scanInfo;
                    msg.what = SCANNING;
                    handler.sendMessage(msg);

                    progress++;
                    pb_scan.setProgress(progress);

                    System.out.println("sourceDir = " + sourceDir);
                    System.out.println("dataDir = " + dataDir);
                    System.out.println("-------------------------------------");

//                    try {
//                        Thread.sleep(300);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }
                Message message = Message.obtain();
                message.what = FINISH;
                handler.sendMessage(message);
            }
        }.start();
    }

    /**
     * 扫描信息的内部类
     */
    class ScanInfo {
        String packName;
        String appName;
        boolean isVirus;
    }
}
