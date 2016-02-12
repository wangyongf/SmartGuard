package com.yongf.smartguard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yongf.smartguard.service.AddressService;
import com.yongf.smartguard.service.CallSMSSafeService;
import com.yongf.smartguard.ui.SettingClickView;
import com.yongf.smartguard.ui.SettingItemView;
import com.yongf.smartguard.utils.ServiceUtils;

public class SettingActivity extends AppCompatActivity {

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
     * 设置归属地显示框背景
     */
    private SettingClickView scv_changebg;

    final String[] items = {"半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿"};

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

        //设置来电显示号码归属地
        siv_show_address = (SettingItemView) findViewById(R.id.siv_show_address);
        showAddress = new Intent(this, AddressService.class);

        boolean isAddressServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, "com.yongf.smartguard.service.AddressService");
        siv_show_address.setChecked(isAddressServiceRunning);


        siv_show_address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_show_address.isChecked()) {
                    //变为非选中状态
                    siv_show_address.setChecked(false);
                    stopService(showAddress);
                } else {
                    //变为选中状态
                    siv_show_address.setChecked(true);
                    startService(showAddress);

                }
            }
        });

        //黑名单拦截的设置
        siv_call_sms_safe = (SettingItemView) findViewById(R.id.siv_call_sms_safe);
        callSMSSafeIntent = new Intent(this, CallSMSSafeService.class);

        boolean isCallSMSSafeServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, "com.yongf.smartguard.service.CallSMSSafeService");
        siv_call_sms_safe.setChecked(isCallSMSSafeServiceRunning);

        siv_call_sms_safe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (siv_call_sms_safe.isChecked()) {
                    //变为非选中状态
                    siv_call_sms_safe.setChecked(false);
                    stopService(callSMSSafeIntent);
                } else {
                    //变为选中状态
                    siv_call_sms_safe.setChecked(true);
                    startService(callSMSSafeIntent);

                }
            }
        });

        //设置号码归属地的背景
        scv_changebg = (SettingClickView) findViewById(R.id.scv_changebg);
        scv_changebg.setTitle("归属地提示框风格");
        final int which = sp.getInt("which", 0);
        scv_changebg.setDesc(items[which]);
        scv_changebg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //弹出一个对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
                builder.setTitle("归属地提示框风格");
                builder.setSingleChoiceItems(items, sp.getInt("which", 0), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //保存选择参数
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("which", which);
                        editor.commit();

                        scv_changebg.setDesc(items[which]);

                        //取消对话框
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showAddress = new Intent(this, AddressService.class);
        boolean isAddressServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, "com.yongf.smartguard.service.AddressService");
        siv_show_address.setChecked(isAddressServiceRunning);

        boolean isCallSMSSafeServiceRunning = ServiceUtils.isServiceRunning(SettingActivity.this, "com.yongf.smartguard.service.CallSMSSafeService");
        siv_call_sms_safe.setChecked(isCallSMSSafeServiceRunning);
    }
}
