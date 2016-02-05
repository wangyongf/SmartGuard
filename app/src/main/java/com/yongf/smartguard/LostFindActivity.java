package com.yongf.smartguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LostFindActivity extends AppCompatActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //判断一下，是否做过设置向导，如果没有做过，就跳转到设置页面，否则停留在当前页面
        sp = getSharedPreferences("config", MODE_PRIVATE);
        boolean configured = sp.getBoolean("configured", false);
        if (configured) {
            //就在手机防盗页面
            setContentView(R.layout.activity_lost_find);
        } else {
            //还没有做过设置向导
            Intent intent = new Intent(this, AntiTheftConfigureActivity1.class);
            startActivity(intent);

            //关闭当前页面
            finish();
        }
    }

    /**
     * 重新进入手机防盗设置向导
     * @param view
     */
    public void reConfigure(View view) {
        //还没有做过设置向导
        Intent intent = new Intent(this, AntiTheftConfigureActivity1.class);
        startActivity(intent);

        //关闭当前页面
        finish();
    }
}
