package com.yongf.smartguard;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AntiTheftConfigureActivity3 extends BaseAntiTheftConfigureActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_configure3);
    }

    @Override
    public void showNext() {
        Intent intent = new Intent(this, AntiTheftConfigureActivity4.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, AntiTheftConfigureActivity2.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

}
