package com.yongf.smartguard.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;

import com.yongf.smartguard.R;

/**
 * Created by yongf-new on 2016/2/6.
 */
public class SMSReceiver extends BroadcastReceiver {

    private SharedPreferences sp;

    @Override
    public void onReceive(Context context, Intent intent) {
        //接收短信的代码
        Object[] pdus = (Object[]) intent.getExtras().get("pdus");
        sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);

        for (Object b : pdus) {
            //具体的某一条短信
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) b);
            //发送者
            String sender = sms.getOriginatingAddress();

            String safeNumber = sp.getString("safeNumber", "");     //15221382253

            String body = sms.getMessageBody();

            //sender.contains(safeNumber)
            if (true) {
                switch (body) {
                    case "#*location*#":
                        System.out.println("得到手机的GPS");
                        //把这个广播终止掉
                        abortBroadcast();
                        break;
                    case "#*alarm*#":
                        System.out.println("播放报警音乐");
                        MediaPlayer player = MediaPlayer.create(context, R.raw.alarm);
                        player.setLooping(true);
                        player.setVolume(1.0f, 1.0f);
                        player.start();

                        //把这个广播终止掉
                        abortBroadcast();
                        break;
                    case "#*wipedata*#":
                        System.out.println("远程清除数据");
                        //把这个广播终止掉
                        abortBroadcast();
                        break;
                    case "#*lockscreen*#":
                        System.out.println("远程锁屏");
                        //把这个广播终止掉
                        abortBroadcast();
                        break;
                }
            }
        }
    }
}
