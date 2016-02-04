package com.yongf.smartguard;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.yongf.smartguard.ui.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    private SettingItemView siv_update;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        siv_update = (SettingItemView) this.findViewById(R.id.siv_update);

        boolean update = sp.getBoolean("update", false);
        if (update) {
            //自动更新已经开启
            siv_update.setChecked(true);
//            siv_update.setDesc("自动更新已经开启");
        } else {
            //自动更新已经关闭
            siv_update.setChecked(false);
//            siv_update.setDesc("自动更新已经关闭");
        }

        siv_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                //判断是否选中
                if (siv_update.isChecked()) {
                    //已经打开自动升级了
                    siv_update.setChecked(false);
//                    siv_update.setDesc("自动更新已经关闭");
                    editor.putBoolean("update", false);
                } else {
                    //没有打开自动升级
                    siv_update.setChecked(true);
//                    siv_update.setDesc("自动更新已经开启");
                    editor.putBoolean("update", true);
                }

                editor.commit();
            }
        });
    }
}
