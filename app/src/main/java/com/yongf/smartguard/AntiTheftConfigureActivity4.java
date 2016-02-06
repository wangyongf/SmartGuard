package com.yongf.smartguard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class AntiTheftConfigureActivity4 extends BaseAntiTheftConfigureActivity {

    private SharedPreferences sp;

    private CheckBox cb_aatc4_protecting;
    private boolean protecting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anti_theft_configure4);

        cb_aatc4_protecting = (CheckBox) findViewById(R.id.cb_aatc4_protecting);

        sp = getSharedPreferences("config", MODE_PRIVATE);
        protecting = sp.getBoolean("protecting", false);
        if(protecting){
            //手机防盗已经开启了
            cb_aatc4_protecting.setText("手机防盗已经开启");
            cb_aatc4_protecting.setChecked(true);
        }else{
            //手机防盗没有开启
            cb_aatc4_protecting.setText("手机防盗没有开启");
            cb_aatc4_protecting.setChecked(false);

        }

        cb_aatc4_protecting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                if(isChecked){
                    cb_aatc4_protecting.setText("手机防盗已经开启");
                }else{
                    cb_aatc4_protecting.setText("手机防盗没有开启");
                }

                //保存选择的状态
                protecting = isChecked;
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("protecting", isChecked);
                editor.commit();


            }
        });
    }

    @Override
    public void showNext() {
        if (protecting == false) {
            Toast.makeText(this, "尚未开启防盗保护", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("configured", true);
        editor.commit();

        Intent intent = new Intent(this, LostFindActivity.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_in, R.anim.tran_out);
    }

    @Override
    public void showPre() {
        Intent intent = new Intent(this, AntiTheftConfigureActivity3.class);
        startActivity(intent);

        finish();
        overridePendingTransition(R.anim.tran_pre_in, R.anim.tran_pre_out);
    }
}
