package com.yongf.smartguard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.yongf.smartguard.db.dao.BlackListDao;
import com.yongf.smartguard.domain.BlackListInfo;

import java.util.List;

public class CallSMSSafeActivity extends AppCompatActivity {

    private ListView lv_call_sms_safe;

    private List<BlackListInfo> infos;

    private BlackListDao dao;

    private CallSmsSafeAdapter adapter;
    private ProgressBar pb_loading;
    private int offset = 0;
    private int length = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);

        pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
        lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
        dao = new BlackListDao(this);
        fillData();

        //给listview注册一个滚动事件的监听器
        lv_call_sms_safe.setOnScrollListener(new AbsListView.OnScrollListener() {

            /**
             * 当滚动的状态发生变化的时候
             * @param view
             * @param scrollState
             */
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                switch (scrollState) {
                    case AbsListView.OnScrollListener.SCROLL_STATE_IDLE:    //空闲状态
                        System.out.println("空闲状态");
                        //判断当前listview滚动的位置
                        //获取最后一个可见条目在集合里面的位置
                        int lastPosition = lv_call_sms_safe.getLastVisiblePosition();
                        //集合里面有20个item，位置从0开始的，最后一个是19
                        if (lastPosition == (infos.size() - 1)) {
                            System.out.println("lastPosition = " + lastPosition + "加载更多的数据");
                            offset += length;
                            fillData();
                        }
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:    //手指触摸滚动
                        System.out.println("手指触摸滚动");
                        break;
                    case AbsListView.OnScrollListener.SCROLL_STATE_FLING:   //惯性滑行状态
                        System.out.println("惯性滑行状态");
                        break;
                }
            }

            /**
             * 滚动的时候调用的方法
             * @param view
             * @param firstVisibleItem
             * @param visibleItemCount
             * @param totalItemCount
             */
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

    }

    private void fillData() {
        pb_loading.setVisibility(View.VISIBLE);

        new Thread(){
            @Override
            public void run() {
                super.run();
                if (infos == null) {
                    infos = dao.findSome(offset, length);
                } else {
                    //说明原来已经加载过数据了
                    infos.addAll(dao.findSome(offset, length));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pb_loading.setVisibility(View.INVISIBLE);
                        if (adapter == null) {
                            adapter = new CallSmsSafeAdapter();
                            lv_call_sms_safe.setAdapter(adapter);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }.start();
    }

    private class CallSmsSafeAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //有多少个条目被显示，这个方法就会被调用多少次
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view;
            ViewHolder holder;

            //1. 减少内存中view对象创建的个数
            if (convertView == null) {
                //把一个布局文件转换为view对象，非常消耗资源
                view = View.inflate(getApplicationContext(), R.layout.list_item_call_sms, null);
                //2. 减少孩子查询的次数，效率提升5%左右
                holder = new ViewHolder();
                holder.tv_number = (TextView) view.findViewById(R.id.tv_black_number);
                holder.tv_mode = (TextView) view.findViewById(R.id.tv_black_mode);
                holder.iv_delete_black_number = (ImageView) view.findViewById(R.id.iv_delete_black_number);
                //当孩子出生时找到他们的引用，存放在记事本，放在父亲的口袋里
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (ViewHolder) view.getTag();
            }

            holder.tv_number.setText(infos.get(position).getNumber());

            String mode = infos.get(position).getMode();
            switch (mode) {
                case "1":
                    holder.tv_mode.setText("电话拦截");
                    break;
                case "2":
                    holder.tv_mode.setText("短信拦截");
                    break;
                case "3":
                    holder.tv_mode.setText("全部拦截");
                    break;
            }

            holder.iv_delete_black_number.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSMSSafeActivity.this);
                    builder.setTitle("警告");
                    builder.setMessage("确定要删除这条记录吗?");
                    builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除数据库的内容
                            dao.delete(infos.get(position).getNumber());
                            //更新界面
                            infos.remove(position);
                            //通知listview数据适配器更新
                            adapter.notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }
            });

            return view;
        }
    }

    /**
     * view对象的容器
     * 记录孩子的内存地址
     * 相当于一个记事本
     */
    static class ViewHolder {
        TextView tv_number;
        TextView tv_mode;
        ImageView iv_delete_black_number;
    }

    private EditText et_black_number;
    private CheckBox cb_phone_mode;
    private CheckBox cb_sms_mode;
    private Button btn_ok;
    private Button btn_cancel;

    public void addBlackNumber(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog dialog = builder.create();
        View contentView = View.inflate(this, R.layout.dialog_add_black_number, null);

        et_black_number = (EditText) contentView.findViewById(R.id.et_black_number);
        cb_phone_mode = (CheckBox) contentView.findViewById(R.id.cb_phone_mode);
        cb_sms_mode = (CheckBox) contentView.findViewById(R.id.cb_sms_mode);
        btn_ok = (Button) contentView.findViewById(R.id.btn_ok);
        btn_cancel = (Button) contentView.findViewById(R.id.btn_cancel);

        dialog.setView(contentView, 0, 0, 0, 0);
        dialog.show();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String blackNumber = et_black_number.getText().toString().trim();
                if (TextUtils.isEmpty(blackNumber)) {
                    Toast.makeText(getApplicationContext(), "黑名单号码不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mode;
                if (cb_phone_mode.isChecked() && cb_sms_mode.isChecked()) {
                    //全部拦截
                    mode = "3";
                } else if (cb_phone_mode.isChecked()) {
                    //电话拦截
                    mode = "1";
                } else if (cb_sms_mode.isChecked()) {
                    //短信拦截
                    mode = "2";
                } else {
                    Toast.makeText(getApplicationContext(), "请选择拦截模式", Toast.LENGTH_SHORT).show();
                    return;
                }
                //数据被添加到数据库
                dao.insert(blackNumber, mode);
                //更新listview的内容
                BlackListInfo info = new BlackListInfo();
                info.setMode(mode);
                info.setNumber(blackNumber);
                infos.add(0, info);
                //通知listview数据适配器更新数据
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

    }
}
