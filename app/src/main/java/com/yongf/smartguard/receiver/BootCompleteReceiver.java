package com.yongf.smartguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.widget.Toast;

/**
 * Created by yongf-new on 2016/2/5.
 */
public class BootCompleteReceiver extends BroadcastReceiver {

    private SharedPreferences sp;

    private TelephonyManager tm;

    @Override
    public void onReceive(Context context, Intent intent) {

        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        //读取之前保存的SIM卡信息
        String saveSim = sp.getString("sim", "");

        //读取当前的SIM信息
        String currentSim = tm.getSimSerialNumber();

        //比较是否一样
        if (saveSim.equals(currentSim)) {
            //SIM卡没有变更
        } else {
            //SIM卡已经变更，采取相应操作（发短信给安全号码）
            System.out.println("SIM卡已经变更");
            Toast.makeText(context, "SIM卡已经变更", Toast.LENGTH_LONG).show();
        }
    }
}
