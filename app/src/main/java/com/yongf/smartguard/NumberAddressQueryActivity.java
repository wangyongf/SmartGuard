package com.yongf.smartguard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yongf.smartguard.db.dao.NumberAddressQueryUtils;

public class NumberAddressQueryActivity extends AppCompatActivity {

    private EditText et_number_query;

    private TextView tv_query_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_number_address_query);

        et_number_query = (EditText) findViewById(R.id.et_number_query);
        tv_query_result = (TextView) findViewById(R.id.tv_query_result);
    }

    /**
     * 号码归属地开启查询
     * @param view
     */
    public void numberAddressQuery(View view) {
        String phoneNum = et_number_query.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNum)) {
            Toast.makeText(NumberAddressQueryActivity.this, "号码为空", Toast.LENGTH_SHORT).show();
            return;
        }

        //去数据库查询号码归属地
        System.out.println("去数据库查询号码归属地");
        //1. 网络查询

        //2. 本地查询
        //写一个工具类，去查询数据库
        String address = NumberAddressQueryUtils.queryNumber(phoneNum);
        tv_query_result.setText(address);
    }
}
