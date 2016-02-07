package com.yongf.smartguard;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yongf.smartguard.utils.MD5Utils;

public class HomeActivity extends AppCompatActivity {

    private GridView list_home;

    private MyAdapter adapter;

    private static String[] names = {
            "手机防盗", "通讯卫士", "软件管理",
            "进程管理", "流量统计", "手机杀毒",
            "缓存清理", "高级工具", "设置中心"
    };

    private static int[] ids = {
            R.mipmap.safe, R.mipmap.callmsgsafe, R.mipmap.app,
            R.mipmap.taskmanager, R.mipmap.netmanager, R.mipmap.trojan,
            R.mipmap.sysoptimize, R.mipmap.atools, R.mipmap.settings
    };

    private SharedPreferences sp;

    private EditText et_set_pwd;
    private EditText et_pwd_confirm;
    private Button btn_ok;
    private Button btn_cancel;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        list_home = (GridView) findViewById(R.id.list_home);
        adapter = new MyAdapter();
        list_home.setAdapter(adapter);
        list_home.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 8:     //进入设置中心
                        Intent intent = new Intent(HomeActivity.this, SettingActivity.class);
                        startActivity(intent);

                        break;
                    case 0:     //进入手机防盗页面
                        showLostFindDialog();
                        break;
                    case 7:     //进入高级工具
                        Intent intent1 = new Intent(HomeActivity.this, AtoolsActivity.class);
                        startActivity(intent1);

                        break;
                }
            }
        });
    }

    private void showLostFindDialog() {
        //判断是否设置过密码
        if (isPasswordSet()) {
            //已经设置密码了，弹出的是输入框
            showInputPwdDialog();
        } else {
            //没有设置密码，弹出的是设置密码对话框
            showSetPwdDialog();
        }
    }

    /**
     * 设置密码对话框
     */
    private void showSetPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        //自定义一个布局文件
        View view = View.inflate(HomeActivity.this, R.layout.dialog_set_password, null);
        et_set_pwd = (EditText) view.findViewById(R.id.et_set_pwd);
        et_pwd_confirm = (EditText) view.findViewById(R.id.et_pwd_confirm);
        btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把对话框取消掉
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取出密码
                String pwd = et_set_pwd.getText().toString().trim();
                String pwd_confirm = et_pwd_confirm.getText().toString().trim();
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                //判断两次输入的密码是否一致
                if (pwd.equals(pwd_confirm)) {
                    //一致的话，就保存密码，把对话框消掉，进入手机防盗页面
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("password", MD5Utils.md5Password(pwd));      //保存加密后的密码
                    editor.commit();

                    alertDialog.dismiss();

                    System.out.println("一致的话，就保存密码，把对话框消掉，进入手机防盗页面");

                    Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();
    }

    /**
     * 输入密码对话框
     */
    private void showInputPwdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        //自定义一个布局文件
        View view = View.inflate(HomeActivity.this, R.layout.dialog_enter_password, null);
        et_set_pwd = (EditText) view.findViewById(R.id.et_set_pwd);
        btn_ok = (Button) view.findViewById(R.id.btn_ok);
        btn_cancel = (Button) view.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //把对话框取消掉
                alertDialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取出密码
                String pwd = et_set_pwd.getText().toString().trim();
                String savedPwd = sp.getString("password", "");     //取出加密后的密码
                if (TextUtils.isEmpty(pwd)) {
                    Toast.makeText(HomeActivity.this, "密码为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (MD5Utils.md5Password(pwd).equals(savedPwd)) {
                    //密码正确
                    alertDialog.dismiss();
                    System.out.println("把对话框消掉，进入手机防盗页面");
                    Intent intent = new Intent(HomeActivity.this, LostFindActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                    et_set_pwd.setText("");
                    return;
                }
            }
        });

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();
    }

    /**
     * 判断是否设置过密码
     * @return 设置过密码返回true；否则返回false
     */
    private boolean isPasswordSet() {
//        String password = sp.getString("password", null);
//        if (TextUtils.isEmpty(password)) {
//            return false;
//        } else {
//            return true;
        return !TextUtils.isEmpty(sp.getString("password", null));
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return names.length;
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
            View view = View.inflate(HomeActivity.this, R.layout.list_item_home, null);
            ImageView iv_item = (ImageView) view.findViewById(R.id.iv_item);
            TextView tv_item = (TextView) view.findViewById(R.id.tv_item);

            tv_item.setText(names[position]);
            iv_item.setImageResource(ids[position]);
            return view;
        }
    }
}
