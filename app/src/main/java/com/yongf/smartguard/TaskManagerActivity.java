package com.yongf.smartguard;

import android.app.ActivityManager;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Toast;

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
    private int runningProcessCount;
    private long availMem;
    private long totalMem;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        tv_mem_info = (TextView) findViewById(R.id.tv_mem_info);
        tv_active_process_count = (TextView) findViewById(R.id.tv_active_process_count);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        lv_task_manager = (ListView) findViewById(R.id.lv_task_manager);
        tv_status = (TextView) findViewById(R.id.tv_status);

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
                if (firstVisibleItem > userTaskInfos.size()) {
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
                if (getPackageName().equals(taskInfo.getPackName())) {
                    return;
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

    private void setTitle() {
        runningProcessCount = SystemInfoUtils.getRunningProcessCount(this);
        availMem = SystemInfoUtils.getAvailMem(this);
        totalMem = SystemInfoUtils.getTotalMemVersion2(this);

        tv_active_process_count.setText("运行中的进程(" + runningProcessCount + ")");
        tv_mem_info.setText("剩余/总内存(" + Formatter.formatFileSize(this, availMem) + "/" +
                Formatter.formatFileSize(this, totalMem) + ")");
    }

    /**
     * 填充数据
     */
    private void fillData() {
        ll_loading.setVisibility(View.VISIBLE);
        new Thread() {
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

                        setTitle();
                    }
                });
            }
        }.start();
    }

    private class TaskManagerAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (sp.getBoolean("showSystem", false)) {
                return userTaskInfos.size() + 1 + systemTaskInfos.size() + 1;
            } else {
                return userTaskInfos.size() + 1;      //这样，就只会显示用户进程
            }
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

            if (getPackageName().equals(taskInfo.getPackName())) {
                holder.cb_status.setVisibility(View.INVISIBLE);
            } else {
                holder.cb_status.setVisibility(View.VISIBLE);
            }

            return view;
        }
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_memsize;
        CheckBox cb_status;
    }

    /**
     * 全选
     *
     * @param view
     */
    public void selectAll(View view) {
        for (TaskInfo info : taskInfos) {
            if (getPackageName().equals(info.getPackName())) {
                continue;
            }

            info.setIsChecked(true);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 反选
     *
     * @param view
     */
    public void selectOpposite(View view) {
        for (TaskInfo info : taskInfos) {
            if (getPackageName().equals(info.getPackName())) {
                continue;
            }

            info.setIsChecked(!info.isChecked());
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * 一键清理
     *
     * @param view
     */
    public void killAll(View view) {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int count = 0;
        long savedMem = 0;
        List<TaskInfo> killedTaskInfos = new ArrayList<>();

        for (TaskInfo info : taskInfos) {
            //被勾选的，杀死这个进程
            if (info.isChecked()) {
                am.killBackgroundProcesses(info.getPackName());

                if (info.isUserTask()) {
                    userTaskInfos.remove(info);
                } else {
                    systemTaskInfos.remove(info);
                }

                count++;
                savedMem += info.getMemSize();
                killedTaskInfos.add(info);
            }
        }

        taskInfos.removeAll(killedTaskInfos);

        adapter.notifyDataSetChanged();
        Toast.makeText(TaskManagerActivity.this, "杀死了" + count + "个进程，释放了" +
                Formatter.formatFileSize(this, savedMem) + "的内存", Toast.LENGTH_SHORT).show();

        runningProcessCount -= count;
        availMem += savedMem;

        tv_active_process_count.setText("运行中的进程(" + runningProcessCount + ")");
        tv_mem_info.setText("剩余/总内存(" + Formatter.formatFileSize(this, availMem) + "/" +
                Formatter.formatFileSize(this, totalMem) + ")");

//        fillData();
    }

    /**
     * 进入设置
     *
     * @param view
     */
    public void enterSetting(View view) {
        Intent intent = new Intent(this, TaskManagerSettingActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adapter.notifyDataSetChanged();
        super.onActivityResult(requestCode, resultCode, data);
    }
}
