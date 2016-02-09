package com.yongf.smartguard.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.yongf.smartguard.R;
import com.yongf.smartguard.db.dao.NumberAddressQueryUtils;

public class AddressService extends Service {
	
	/**
	 * 窗体管理者
	 */
	private WindowManager wm;
	private View view;

	/**
	 * 电话服务
	 */

	private TelephonyManager tm;
	private MyListenerPhone listenerPhone;
	
	private OutCallReceiver receiver;

    private SharedPreferences sp;

    @Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	// 服务里面的内部类
	//广播接收者的生命周期和服务一样
	class OutCallReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 这就是我们拿到的播出去的电话号码
			String phone = getResultData();
			// 查询数据库
			String address = NumberAddressQueryUtils.queryNumber(phone);
			
//			Toast.makeText(context, address, 1).show();
			myToast(address);
		}

	}

	private class MyListenerPhone extends PhoneStateListener {

		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			// state：状态，incomingNumber：来电号码
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_RINGING:// 来电铃声响起
				// 查询数据库的操作
				String address = NumberAddressQueryUtils
						.queryNumber(incomingNumber);
				
//				Toast.makeText(getApplicationContext(), address, 1).show();
				myToast(address);

				break;
				
			case TelephonyManager.CALL_STATE_IDLE://电话的空闲状态：挂电话、来电拒绝
				//把这个View移除
				if(view != null ){
					wm.removeView(view);
				}
			
				
				break;

			default:
				break;
			}
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

		// 监听来电
		listenerPhone = new MyListenerPhone();
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_CALL_STATE);
		
		//用代码去注册广播接收者
		receiver = new OutCallReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
		registerReceiver(receiver, filter);
		
		//实例化窗体
		wm = (WindowManager) getSystemService(WINDOW_SERVICE);
	}

    private WindowManager.LayoutParams params;

    long[] mHits = new long[2];

	/**
	 * 自定义土司
	 * @param address
	 */
	public void myToast(String address) {
	     view =   View.inflate(this, R.layout.address_show, null);
	    final TextView textview  = (TextView) view.findViewById(R.id.tv_address);
        //给view对象设置一个触摸监听器
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                System.out.println("控件被点击了");
                System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
                mHits[mHits.length - 1] = SystemClock.uptimeMillis();
                if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
                    //双击居中了
                    params.x = wm.getDefaultDisplay().getWidth() / 2 - view.getWidth() / 2;
                    wm.updateViewLayout(view, params);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putInt("lastX", params.x);
                    editor.commit();
                }
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            //定义手指的初始化位置
            int startX;
            int startY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:       //手指按下屏幕
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        System.out.println("开始位置：" + startX + ", " + startY);
                        break;
                    case MotionEvent.ACTION_MOVE:       //手指在屏幕上移动
                        int newX = (int) event.getRawX();
                        int newY = (int) event.getRawY();
                        System.out.println("新位置：" + newX + ", " + newY);
                        int dx = newX - startX;
                        int dy = newY - startY;
                        System.out.println("手指的偏移量：" + dx + ", " + dy);
                        params.x += dx;
                        params.y += dy;
                        //考虑边界问题
                        if (params.x < 0) {
                            params.x = 0;
                        }
                        if (params.y < 0) {
                            params.y = 0;
                        }
                        if (params.x > (wm.getDefaultDisplay().getWidth() - view.getWidth())) {
                            params.x = wm.getDefaultDisplay().getWidth() - view.getWidth();
                        }
                        if (params.y > (wm.getDefaultDisplay().getHeight() - view.getHeight())) {
                            params.y = wm.getDefaultDisplay().getHeight() - view.getHeight();
                        }

                        wm.updateViewLayout(view, params);
                        //重新初始化手指的开始结束位置
                        startX = (int) event.getRawX();
                        startY = (int) event.getRawY();
                        break;
                    case MotionEvent.ACTION_UP:             //手指离开屏幕一瞬间
                        //记录控件距离屏幕左上角的坐标
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("lastX", params.x);
                        editor.putInt("lastY", params.y);
                        editor.commit();
                        break;
                }

                return true;    //事件处理完毕了，不要让父控件，父布局响应触摸事件
            }
        });
	    
	    //"半透明","活力橙","卫士蓝","金属灰","苹果绿"
	    int [] ids = {R.drawable.call_locate_white,R.drawable.call_locate_orange,R.drawable.call_locate_blue
	    ,R.drawable.call_locate_gray,R.drawable.call_locate_green};
	    sp = getSharedPreferences("config", MODE_PRIVATE);
	    view.setBackgroundResource(ids[sp.getInt("which", 0)]);
	    textview.setText(address);
		//窗体的参数就设置好了
        params = new WindowManager.LayoutParams();
		 
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        //与窗体左上角对齐
        params.gravity = Gravity.TOP + Gravity.LEFT;
        //可以设置为屏幕正中间的位置
        params.x = sp.getInt("lastX", 100);
        params.y = sp.getInt("lastY", 100);
         
         params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                 | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
         params.format = PixelFormat.TRANSLUCENT;
         params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;      //android里面具有电话优先级的一种窗体类型，记得添加权限
		wm.addView(view, params);
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消监听来电
		tm.listen(listenerPhone, PhoneStateListener.LISTEN_NONE);
		listenerPhone = null;
		
		//用代码取消注册广播接收者
		unregisterReceiver(receiver);
		receiver = null;

	}

}
