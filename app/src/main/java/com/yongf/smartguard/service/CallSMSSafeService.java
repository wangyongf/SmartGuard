package com.yongf.smartguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.RemoteException;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;
import com.yongf.smartguard.db.dao.BlackListDao;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CallSMSSafeService extends Service {

    private static final String TAG = "CallSMSSafeService";

    private BlackListDao dao;

    private InnerSMSReceiver receiver;

    private TelephonyManager tm;

    private MyListener listener;

    public CallSMSSafeService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class InnerSMSReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "内部广播接收者，短信到来了");
            //检查发件人是否是黑名单号码，设置短信拦截|全部拦截
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
                String sender = smsMessage.getOriginatingAddress();     //发件人
                String result = dao.findMode(sender);
                if ("2" . equals(result) || "3" . equals(result)) {
                    Log.i(TAG, "拦截短信");
                    abortBroadcast();
                }
                //演示代码
                //语言分词技术处理之后，才能进行拦截处理，避免误判
                String body = smsMessage.getMessageBody();
                if (body.contains("fapiao")) {
                    Log.i(TAG, "拦截掉");
                }
            }
        }
    }

    @Override
    public void onCreate() {
        dao = new BlackListDao(this);
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new MyListener();
        tm.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
        receiver = new InnerSMSReceiver();
        IntentFilter filter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        //手动设置优先级，默认清单文件的广播接收者比代码注册的要高！
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        registerReceiver(receiver, filter);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(receiver);
        receiver = null;

        tm.listen(listener, PhoneStateListener.LISTEN_NONE);

        super.onDestroy();
    }

    private class MyListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:       //铃响状态
                    String result = dao.findMode(incomingNumber);
                    if ("1" . equals(result) || "3" . equals(result)) {
                        Log.i(TAG, "挂断电话。。。");
                        endCall();
                    }

                    break;
            }
        }
    }

    private void endCall() {
//        IBinder binder = ServiceManager.getService(TELEPHONY_SERVICE);
        //采用反射的方式得到IBinder
        try {
            //加载ServiceManager的字节码
            Class clazz = CallSMSSafeService.class.getClassLoader().loadClass("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder ibinder = (IBinder) method.invoke(null, TELEPHONY_SERVICE);
            ITelephony.Stub.asInterface(ibinder).endCall();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
