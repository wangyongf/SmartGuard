package com.yongf.smartguard;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EnterAppLockPWDActivity extends AppCompatActivity {

    private EditText et_password;
    private String packName;
    private TextView tv_app_name;
    private ImageView iv_app_icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_app_lock_pwd);

        et_password = (EditText) findViewById(R.id.et_password);
        tv_app_name = (TextView) findViewById(R.id.tv_app_name);
        iv_app_icon = (ImageView) findViewById(R.id.iv_app_icon);

        Intent intent = getIntent();
        //当前要保护的应用程序的包名
        packName = intent.getStringExtra("packName");

        PackageManager pm = getPackageManager();
        try {
            ApplicationInfo info = pm.getApplicationInfo(packName, 0);
            tv_app_name.setText(info.loadLabel(pm));
            iv_app_icon.setImageDrawable(info.loadIcon(pm));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        //回桌面
        Intent intent = new Intent();
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.HOME");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addCategory("android.intent.category.MONKEY");

        startActivity(intent);
        //所有的activity最小化，不会执行onDestroy，只执行onStop()
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    /**
     * 点击输入密码
     *
     * @param view
     */
    public void enter(View view) {
        String pwd = et_password.getText().toString().trim();
        if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(EnterAppLockPWDActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //假设密码是123
        if ("123".equals(pwd)) {
            //告诉看门狗这个程序密码输入正确了，可以临时停止保护了。
            //自定义的广播，临时停止保护
            Intent intent = new Intent();
            intent.setAction("com.yongf.smartguard.tempstop");
            intent.putExtra("packName", packName);
            sendBroadcast(intent);

            finish();
        } else {
            Toast.makeText(EnterAppLockPWDActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
            et_password.setText("");
            return;
        }
    }
}
