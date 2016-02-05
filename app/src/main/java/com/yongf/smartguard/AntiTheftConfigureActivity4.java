package com.yongf.smartguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class AntiTheftConfigureActivity4 extends BaseAntiTheftConfigureActivity {

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_configure4);

        sp = getSharedPreferences("config", MODE_PRIVATE);
    }

    @Override
    public void showNext() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configured", true);
        editor.commit();

        Intent intent = new Intent(this, LostFindActivity.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, AntiTheftConfigureActivity3.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }
}
