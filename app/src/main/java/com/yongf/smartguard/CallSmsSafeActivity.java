package com.yongf.smartguard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.yongf.smartguard.db.dao.BlackListDao;
import com.yongf.smartguard.domain.BlackListInfo;

import java.util.List;

public class CallSmsSafeActivity extends AppCompatActivity {

    private ListView lv_call_sms_safe;

    private List<BlackListInfo> infos;

    private BlackListDao dao;

    private CallSmsSafeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);

        lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
        dao = new BlackListDao(this);
        infos = dao.findAll();
        adapter = new CallSmsSafeAdapter();
        lv_call_sms_safe.setAdapter(adapter);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(CallSmsSafeActivity.this);
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
