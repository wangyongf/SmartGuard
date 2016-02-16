package com.yongf.smartguard;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yongf.smartguard.domain.TaskInfo;
import com.yongf.smartguard.engine.TaskInfoProvider;
import com.yongf.smartguard.utils.SystemInfoUtils;

import java.util.ArrayList;
import java.util.List;

public class TaskManagerActivity extends AppCompatActivity {

    private TextView tv_mem_info;
    private TextView tv_active_process_count;
    private LinearLayout ll_loading;
    private ListView lv_task_manager;
    private TextView tv_status;

    private List<TaskInfo> taskInfos;
    private List<TaskInfo> userTaskInfos;
    private List<TaskInfo> systemTaskInfos;
    private TaskInfo taskInfo;

    private TaskManagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
        tv_active_process_count = (TextView) findViewById(R.id.tv_active_process_count);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        tv_status = (TextView) findViewById(R.id.tv_status);

        int runningProcessCount = SystemInfoUtils.getRunningProcessCount(this);
        tv_active_process_count.setText("运行中的进程(" + runningProcessCount + ")");
        long availMem = SystemInfoUtils.getAvailMem(this);
        long totalMem = SystemInfoUtils.getTotalMemVersion2(this);
        tv_mem_info.setText("剩余/总内存(" + Formatter.formatFileSize(this, availMem) + "/" +
                Formatter.formatFileSize(this, totalMem) + ")");

        fillData();

        lv_task_manager.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (userTaskInfos == null || systemTaskInfos == null) {
                    return;
                }
                if (firstVisibleItem >= userTaskInfos.size()) {
                    tv_status.setText("系统进程(" + systemTaskInfos.size() + ")");
                } else {
                    tv_status.setText("用户进程(" + userTaskInfos.size() + ")");
                }
            }
        });

        lv_task_manager.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //用户/系统进程的标签
                if (position == 0 || position == (userTaskInfos.size() + 1)) {
                    return;
                }
                if (position <= userTaskInfos.size()) {
                    taskInfo = userTaskInfos.get(position - 1);
                } else {
                    taskInfo = systemTaskInfos.get(position - 1 - userTaskInfos.size() - 1);
                }
                ViewHolder holder = (ViewHolder) view.getTag();
                if (taskInfo.isChecked()) {
                    taskInfo.setIsChecked(false);
                    holder.cb_status.setChecked(false);
                } else {
                    taskInfo.setIsChecked(true);
                    holder.cb_status.setChecked(true);
                }

            }
        });
    }

    /**
     * 填充数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread(){
            @Override
            public void run() {
                taskInfos = TaskInfoProvider.getTaskInfos(getApplicationContext());
                userTaskInfos = new ArrayList<TaskInfo>();
                systemTaskInfos = new ArrayList<TaskInfo>();
                for (TaskInfo info : taskInfos) {
                    if (info.isUserTask()) {
                        userTaskInfos.add(info);
                    } else {
                        systemTaskInfos.add(info);
                    }
                }

                //更新设置界面
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ll_loading.setVisibility(View.INVISIBLE);
                        if (adapter == null) {
                            adapter = new TaskManagerAdapter();
                            lv_task_manager.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return userTaskInfos.size() + 1 +systemTaskInfos.size() + 1;
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
            //用户进程的标签
            if (position == 0) {
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("用户进程(" + userTaskInfos.size() + ")");

                return tv;
            }
            if (position == (userTaskInfos.size() + 1)) {
                TextView tv = new TextView(getApplicationContext());
                tv.setBackgroundColor(Color.GRAY);
                tv.setTextColor(Color.WHITE);
                tv.setText("系统进程(" + systemTaskInfos.size() + ")");

                return tv;
            }
            if (position <= userTaskInfos.size()) {
                taskInfo = userTaskInfos.get(position - 1);
            } else {
                taskInfo = systemTaskInfos.get(position - 1 - userTaskInfos.size() - 1);
            }

            View view;
            ViewHolder holder;
            if (convertView != null && convertView instanceof RelativeLayout) {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            } else {
                view = View.inflate(getApplicationContext(), R.layout.list_item_taskinfo, null);
                holder = new ViewHolder();
                holder.iv_icon = (ImageView) view.findViewById(R.id.ic_task_icon);
                holder.tv_name = (TextView) view.findViewById(R.id.tv_task_name);
                holder.tv_memsize = (TextView) view.findViewById(R.id.tv_task_memsize);
                holder.cb_status = (CheckBox) view.findViewById(R.id.cb_status);
                view.setTag(holder);
            }
            holder.iv_icon.setImageDrawable(taskInfo.getIcon());
            holder.tv_name.setText(taskInfo.getAppName());
            holder.tv_memsize.setText("内存占用(" + Formatter.formatFileSize(getApplicationContext(), taskInfo.getMemSize()) + ")");
            holder.cb_status.setChecked(taskInfo.isChecked());

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox cb_status;
    }
}
