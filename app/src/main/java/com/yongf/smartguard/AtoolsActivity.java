package com.yongf.smartguard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.yongf.smartguard.utils.SMSUtils;

public class AtoolsActivity extends AppCompatActivity {

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atools);
    }

    /**
     * 点击事件，进入号码归属地查询的页面
     * @param view
     */
    public void numberQuery(View view) {
        Intent intent = new Intent(this, NumberAddressQueryActivity.class);
        startActivity(intent);
    }

    /**
     * 点击事件，短信的备份逻辑
     * @param view
     */
    public void smsBackup(View view) {
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setMessage("正在备份短信");
        pd.show();

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    SMSUtils.backupSMS(AtoolsActivity.this, new SMSUtils.BackupCallBack() {
                        @Override
                        public void beforeBackup(int max) {
                            pd.setMax(max);
                        }

                        @Override
                        public void onSMSBackup(int progress) {
                            pd.setProgress(progress);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "备份成功", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AtoolsActivity.this, "备份失败", Toast.LENGTH_SHORT).show();
                        }
                    });
                } finally {
                    pd.dismiss();
                }
            }
        }.start();
    }

    /**
     * 点击事件，短信的还原逻辑
     * @param view
     */
    public void smsRestore(View view) {
        SMSUtils.restoreSMS(this, true);

    }
}
