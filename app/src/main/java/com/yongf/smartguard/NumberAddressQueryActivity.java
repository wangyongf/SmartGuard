package com.yongf.smartguard;

import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yongf.smartguard.db.dao.NumberAddressQueryUtils;

public class NumberAddressQueryActivity extends AppCompatActivity {

    private EditText et_number_query;

    private TextView tv_query_result;

    /**
     * 系统提供的振动服务
     */
    private Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        et_number_query = (EditText) findViewById(R.id.et_number_query);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);
        et_number_query.addTextChangedListener(new TextWatcher() {
            /**
             * 在文本发生变化之前回调
             * @param s
             * @param start
             * @param count
             * @param after
             */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            /**
             * 当文本发生变化的时候回调
             * @param s
             * @param start
             * @param before
             * @param count
             */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 3) {
                    //查询数据库，并且显示结果
                    String address = NumberAddressQueryUtils.queryNumber(getApplicationContext(), s.toString());
                    tv_query_result.setText(address);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 号码归属地开启查询
     * @param view
     */
    public void numberAddressQuery(View view) {
        String phoneNum = et_number_query.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(NumberAddressQueryActivity.this, "号码为空", Toast.LENGTH_SHORT).show();
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_number_query.startAnimation(shake);

            //当电话号码为空的时候，就去振动手机提醒用户
//            vibrator.vibrate(1000);
            long[] pattern = {200, 200, 300, 300, 1000, 2000};
            //-1不重复 0循环振动
            vibrator.vibrate(pattern, -1);
            return;
        }

        //去数据库查询号码归属地
        System.out.println("去数据库查询号码归属地");
        //1. 网络查询

        //2. 本地查询
        //写一个工具类，去查询数据库
        String address = NumberAddressQueryUtils.queryNumber(getApplicationContext(), phoneNum);
        tv_query_result.setText(address);
    }
}
