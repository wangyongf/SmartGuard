package com.yongf.smartguard;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;

public class TaskManagerSettingActivity extends AppCompatActivity {

    private CheckBox cb_show_system;
    private CheckBox cb_auto_clean;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager_setting);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        cb_show_system = (CheckBox) findViewById(R.id.cb_show_system);
        cb_auto_clean = (CheckBox) findViewById(R.id.cb_auto_clean);

        cb_show_system.setChecked(sp.getBoolean("showSystem", false));
        cb_show_system.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("showSystem", isChecked);
                editor.commit();
            }
        });

        //秒表的一种实现
        CountDownTimer cdt = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println("millisUntilFinished = " + millisUntilFinished);
            }

            @Override
            public void onFinish() {
                System.out.println("finish");
            }
        };
        cdt.start();
    }
}
