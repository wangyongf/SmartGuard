package com.yongf.smartguard;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yongf.smartguard.domain.AppInfo;
import com.yongf.smartguard.engine.AppInfoProvider;

import java.util.ArrayList;
import java.util.List;

public class AppManagerActivity extends AppCompatActivity {

    private TextView tv_avail_ram;
    private TextView tv_avail_sd;
    private ListView lv_app_manager;
    private LinearLayout ll_loading;

    /**
     * 所有的应用程序包信息
     */
    private List<AppInfo> appInfos;

    /**
     * 用户应用程序的集合
     */
    private List<AppInfo> userAppInfos;

    /**
     * 系统应用程序的集合
     */
    private List<AppInfo> systemAppInfos;

    /**
     * 当前程序信息的状态
     */
    private TextView tv_status;

    /**
     * 弹出的悬浮窗体
     */
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_manager);

        tv_avail_ram = (TextView) findViewById(R.id.tv_avail_ram);
        tv_avail_sd = (TextView) findViewById(R.id.tv_avail_sd);
        lv_app_manager = (ListView) findViewById(R.id.lv_app_manager);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_status = (TextView) findViewById(R.id.tv_status);

        long sdSize = getAvailSpace(Environment.getExternalStorageDirectory().getAbsolutePath());
        long ramSize = getAvailSpace(Environment.getDataDirectory().getAbsolutePath());

        tv_avail_sd.setText("SD卡可用：" + android.text.format.Formatter.formatFileSize(this, sdSize));
        tv_avail_ram.setText("内存可用：" + Formatter.formatFileSize(this, ramSize));

         ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                //这个操作可能非常耗时！
                appInfos = AppInfoProvider.getAppInfos(AppManagerActivity.this);
                userAppInfos = new ArrayList<AppInfo>();
                systemAppInfos = new ArrayList<AppInfo>();
                for (AppInfo info : appInfos) {
                    if (info.isUserApp()) {
                        userAppInfos.add(info);
                    } else {
                        systemAppInfos.add(info);
                    }
                }

                //加载listview的数据适配器
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        lv_app_manager.setAdapter(new AppManagerApapter());
                    }
                });
            }
        }.start();

        //给listview注册一个滚动监听器
        lv_app_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            //滚动的时候调用的方法
            //firstVisibleItem 第一个可见条目在listview集合里面的位置
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                dismissPopupWindow();

                /*
                注意！userAppInfos是在子线程中获取的！
                 */
                if (userAppInfos == null || systemAppInfos == null) {
                    return;
                }

                if (firstVisibleItem > userAppInfos.size()) {
                    tv_status.setText("系统程序(" + systemAppInfos.size() + ")");
                } else {
                    tv_status.setText("用户程序(" + userAppInfos.size() + ")");
                }
            }
        });

        //设置listview的点击事件
        lv_app_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppInfo appInfo;
                int newPosition;

                if (position == 0 ||
                        position == (userAppInfos.size() + 1)) {
                    return;
                }
                if (position <= userAppInfos.size()) {
                    //用户程序
                    newPosition = position - 1;
                    appInfo = userAppInfos.get(newPosition);
                } else {
                    //系统程序
                    newPosition = position - 1 - userAppInfos.size() - 1;
                    appInfo = systemAppInfos.get(newPosition);
                }
//                System.out.println(appInfo.getPackageName());
                dismissPopupWindow();


                TextView contentView = new TextView(getApplicationContext());
                contentView.setText(appInfo.getPackageName());
                contentView.setTextColor(Color.BLACK);
                popupWindow = new PopupWindow(contentView, 200, 100);
                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.RED));
                int[] location = new int[2];
                view.getLocationInWindow(location);
                popupWindow.showAtLocation(parent, Gravity.LEFT | Gravity.TOP, location[0], location[1]);
            }
        });
    }

    private void dismissPopupWindow() {
        //把旧的弹出窗体关闭掉
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private class AppManagerApapter extends BaseAdapter {

        /**
         * 控制listview有多少个条目
         * @return
         */
        @Override
        public int getCount() {
//            return appInfos.size();
            return userAppInfos.size() + 1 + systemAppInfos.size() + 1;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;
            AppInfo appInfo;
            int newPosition;
//            TextView tv = new TextView(getApplicationContext());
//            tv.setText(appInfos.get(position).toString());
            TextView tv = new TextView(getApplicationContext());
            tv.setTextColor(Color.WHITE);
            tv.setBackgroundColor(Color.GRAY);

            if (position == 0) {
                //显示的是用户程序小标签
                tv.setText("用户程序(" + userAppInfos.size() + ")");
                return tv;
            }
            if (position == (userAppInfos.size() + 1)) {
                //显示的是系统程序小标签
                tv.setText("系统程序(" + systemAppInfos.size() + ")");
                return tv;
            }
            if (position <= userAppInfos.size()) {
                //这些位置是留给用户程序显示的
                newPosition = position - 1;
                appInfo = userAppInfos.get(newPosition);
            } else {
                //这些位置是留给系统程序显示的
                newPosition = position - 1 - userAppInfos.size() - 1;
                appInfo = systemAppInfos.get(newPosition);
            }

            if (convertView != null && convertView instanceof RelativeLayout) {
                //不仅需要检查是否为空，还要判断是否是合适的类型去复用
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.list_item_app_info, null);
                holder = new ViewHolder();
                holder.iv_app_icon = (ImageView) view.findViewById(R.id.iv_app_icon);
                holder.tv_app_name = (TextView) view.findViewById(R.id.tv_app_name);
                holder.tv_app_location = (TextView) view.findViewById(R.id.tv_app_location);
                view.setTag(holder);
            }
            holder.iv_app_icon.setImageDrawable(appInfo.getIcon());
            holder.tv_app_name.setText(appInfo.getAppName());
//            holder.tv_app_location.setText(appInfo.getl);
            if (appInfo.isInRam()) {
                holder.tv_app_location.setText("手机内存");
            } else {
                holder.tv_app_location.setText("外部存储");
            }

            return view;
        }
    }

    static class ViewHolder {
        TextView tv_app_name;
        TextView tv_app_location;
        ImageView iv_app_icon;
    }

    /**
     * 获取某个目录的可用空间
     * @param path
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private long getAvailSpace(String path) {
        StatFs statFs = new StatFs(path);
        long size = statFs.getBlockSize();      //获取分区的大小
        long count = statFs.getAvailableBlocks();        //获取可用的区块的个数

        return size * count;
    }

    @Override
    protected void onDestroy() {
        dismissPopupWindow();
        super.onDestroy();
    }
}
