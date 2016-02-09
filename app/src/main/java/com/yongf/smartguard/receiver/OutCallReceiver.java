package com.yongf.smartguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.yongf.smartguard.db.dao.NumberAddressQueryUtils;

/**
 * Created by yongf-new on 2016/2/9 9:50.
 */
public class OutCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //取出拨出的电话号码
        String phoneNumber = getResultData();
        //查询数据库
        String address = NumberAddressQueryUtils.queryNumber(phoneNumber);
        Toast.makeText(context, address, Toast.LENGTH_LONG).show();
    }
}
