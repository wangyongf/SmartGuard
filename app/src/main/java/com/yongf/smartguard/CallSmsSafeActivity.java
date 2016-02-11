package com.yongf.smartguard;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.yongf.smartguard.db.dao.BlackListDao;
import com.yongf.smartguard.domain.BlackListInfo;

import java.util.List;

public class CallSmsSafeActivity extends AppCompatActivity {

    private ListView lv_call_sms_safe;

    private List<BlackListInfo> infos;

    private BlackListDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_sms_safe);

        lv_call_sms_safe = (ListView) findViewById(R.id.lv_call_sms_safe);
        dao = new BlackListDao(this);
        infos = dao.findAll();
        lv_call_sms_safe.setAdapter(new CallSmsSafeAdapter());
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
        public View getView(int position, View convertView, ViewGroup parent) {
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
    }
}
