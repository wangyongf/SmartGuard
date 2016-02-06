package com.yongf.smartguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AntiTheftConfigureActivity3 extends BaseAntiTheftConfigureActivity {

    private EditText et_aatc3_phone_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_configure3);

        et_aatc3_phone_number = (EditText) findViewById(R.id.et_aatc3_phone_number);

        et_aatc3_phone_number.setText(sp.getString("safeNumber", ""));
    }

    @Override
    public void showNext() {
        //应该保存一下安全号码
        String phoneNumber = et_aatc3_phone_number.getText().toString().trim();
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(this, "安全号码还没设置", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putString("safeNumber", phoneNumber);
        editor.commit();

        Intent intent = new Intent(this, AntiTheftConfigureActivity4.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, AntiTheftConfigureActivity2.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }

    /**
     * 选择联系人的点击事件
     * @param view
     */
    public void selectContact(View view) {
        Intent intent = new Intent(this, SelectContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        String phone = data.getStringExtra("phone").replace("-", "");
        et_aatc3_phone_number.setText(phone);
    }
}
