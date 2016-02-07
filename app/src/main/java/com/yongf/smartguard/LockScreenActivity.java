package com.yongf.smartguard;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class LockScreenActivity extends AppCompatActivity {

    /**
     * 设备策略服务
     */
    private DevicePolicyManager dpm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);

        /**
         * 一键锁屏的功能已经实现了，不过有问题
         */
        dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
        dpm.lockNow();
        finish();
    }

    /**
     * 获取管理员权限
     * @param view
     */
    public void getAdmin(View view) {
        //创建一个Intent
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        //激活的对象
        ComponentName mDeviceAdmin = new ComponentName(this, MyAdmin.class);

        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdmin);
        //劝说用户开启管理员权限
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "哥们开启我可以一键锁屏，你的按钮就不会经常失灵");
        startActivity(intent);
    }

    /**
     * 一键锁屏
     * @param view
     */
    public void lockScreen(View view) {
        ComponentName mDeviceAdmin = new ComponentName(this, MyAdmin.class);
        if (dpm.isAdminActive(mDeviceAdmin)) {
            dpm.lockNow();      //锁屏
            dpm.resetPassword("", 0);        //重置锁屏密码

            //清除SD卡上的数据
//        dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
            //恢复出厂设置
//        dpm.wipeData(0);
        } else {
            Toast.makeText(this, "请先打开管理员权限", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    /**
     * 卸载当前软件
     * @param view
     */
    public void unistall(View view) {
        ComponentName mDeviceAdmin = new ComponentName(this, MyAdmin.class);
        //1. 先清除管理员权限
        dpm.removeActiveAdmin(mDeviceAdmin);

        //2. 普通应用的卸载
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivity(intent);
    }
}
