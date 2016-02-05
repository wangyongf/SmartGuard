package com.yongf.smartguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.yongf.smartguard.ui.SettingItemView;

public class AntiTheftConfigureActivity2 extends BaseAntiTheftConfigureActivity {

    private SettingItemView siv_config2_sim;

    /**
     * 读取手机SIM卡的信息
     */
    private TelephonyManager tm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_configure2);

        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        siv_config2_sim = (SettingItemView) findViewById(R.id.siv_config2_sim);

        String saveSim = sp.getString("sim", null);
        if (TextUtils.isEmpty(saveSim)) {
            //没有绑定
            siv_config2_sim.setChecked(false);
        } else {
            //已经绑定
            siv_config2_sim.setChecked(true);
        }

        siv_config2_sim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sim = tm.getSimSerialNumber();
                SharedPreferences.Editor editor = sp.edit();

                if (siv_config2_sim.isChecked()) {
                    siv_config2_sim.setChecked(false);
                    editor.putString("sim", null);
                } else {
                    siv_config2_sim.setChecked(true);
                    //保存SIM卡的序列号
                    editor.putString("sim", sim);
                }
                editor.commit();
            }
        });
    }

    @Override
    public void showNext() {
        //取出是否绑定SIM卡
        String sim = sp.getString("sim", null);
        if (TextUtils.isEmpty(sim)) {
            //没有绑定
            Toast.makeText(this, "SIM卡没有绑定哦", Toast.LENGTH_LONG).show();
            return;
        }

        Intent intent = new Intent(this, AntiTheftConfigureActivity3.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, AntiTheftConfigureActivity1.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

}
