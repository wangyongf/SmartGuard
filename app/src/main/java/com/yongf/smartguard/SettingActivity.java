package com.yongf.smartguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yongf.smartguard.ui.SettingClickView;
import com.yongf.smartguard.ui.SettingItemView;

public class SettingActivity extends AppCompatActivity {

    final String[] items = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};
    /**
     * 设置是否开启自动更新
     */
    private SettingItemView siv_update;
    private SharedPreferences sp;
    /**
     * 设置是否开启来电显示号码归属地
     */
    private SettingItemView siv_show_address;
    private Intent showAddress;
    /**
     * 黑名单拦截的设置
     */
    private SettingItemView siv_call_sms_safe;
    private Intent callSMSSafeIntent;
    /**
     * 程序锁的设置
     */
    private SettingItemView siv_watch_dog;
    private Intent watchDogIntent;
    /**
     * 设置归属地显示框背景
     */
    private SettingClickView scv_changebg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);


    }
}
