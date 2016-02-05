package com.yongf.smartguard;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class AntiTheftConfigureActivity1 extends BaseAntiTheftConfigureActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_configure1);
    }

    public void showNext() {
        Intent intent = new Intent(this, AntiTheftConfigureActivity2.class);
        startActivity(intent);

        finish();
        //要求在finish()、startActivity(intent)后面执行
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void showPre() {

    }
}
