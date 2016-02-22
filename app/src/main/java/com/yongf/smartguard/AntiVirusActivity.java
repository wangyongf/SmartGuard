package com.yongf.smartguard;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.yongf.smartguard.utils.MD5Utils;

import java.util.List;

public class AntiVirusActivity extends AppCompatActivity {

    private ImageView iv_scan;
    private ProgressBar pb_scan;
    private PackageManager pm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_virus);

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

        pb_scan.setMax(100);
        new Thread() {
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 100; i++) {
                    try {
                        Thread.sleep(100);
                        pb_scan.setProgress(i);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    /**
     * 扫描病毒
     */
    private void scanVirus() {
        pm = getPackageManager();
        List<PackageInfo> infos = pm.getInstalledPackages(0);
        for (PackageInfo info : infos) {
            String dataDir = info.applicationInfo.dataDir;
            //apk文件的完整路径
            String sourceDir = info.applicationInfo.sourceDir;
            String signature = MD5Utils.getFileSHA1Signature(sourceDir);
            System.out.println(info.applicationInfo.loadLabel(pm) + ": " + signature);

            //查询SHA1信息，是否在病毒数据库里面存在


            System.out.println("sourceDir = " + sourceDir);
            System.out.println("dataDir = " + dataDir);
            System.out.println("-------------------------------------");
        }
    }
}
