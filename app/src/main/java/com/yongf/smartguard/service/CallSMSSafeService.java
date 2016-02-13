package com.yongf.smartguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
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

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:       //铃响状态
                    String result = dao.findMode(incomingNumber);
                    if ("1" . equals(result) || "3" . equals(result)) {
                        Log.i(TAG, "挂断电话。。。");
                        //删除通话记录
                        //另外一个应用程序联系人的应用的私有数据库
                        //观察呼叫记录数据库内容的变化
                        Uri uri = Uri.parse("content://call_log/calls");
                        getContentResolver().registerContentObserver(uri, true, new CallLogObserver(incomingNumber, new Handler()));
                        deleteCallLog(incomingNumber);

                        endCall();      //在另外一个进程里面运行的远程服务的方法，挂断电话方法调用后，呼叫记录可能还没有生成
                    }

                    break;
            }
            super.onCallStateChanged(state, incomingNumber);
        }
     }

    private class CallLogObserver extends ContentObserver {

        private String inComingNumber;

        /**
         * Creates a content observer.
         *
         * @param handler The handler to run {@link #onChange} on, or null if none.
         */
        public CallLogObserver(String inComingNumber, Handler handler) {
            super(handler);
            this.inComingNumber = inComingNumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            Log.i(TAG, "数据库的内容变化了，产生了呼叫记录");
            getContentResolver().unregisterContentObserver(this);
            deleteCallLog(inComingNumber);
            super.onChange(selfChange);
        }
    }

    /**
     * 利用内容提供者删除呼叫记录
     * @param incomingNumber
     */
    private void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        //呼叫记录uri的路径
        Uri uri = Uri.parse("content://call_log/calls");
        resolver.delete(uri, "number = ?", new String[]{incomingNumber});
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
