package com.yongf.smartguard;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class DoubleClickActivity extends AppCompatActivity {

    long firstClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_double_click);
    }

    public void doubleClick(View view) {
        if (firstClick > 0) {
            long secondClick = SystemClock.uptimeMillis();
            long dTime = secondClick - firstClick;
            if (dTime < 500) {
                Toast.makeText(DoubleClickActivity.this, "双击了", Toast.LENGTH_SHORT).show();
            } else {
                firstClick = secondClick;
            }

            return;
        }

        firstClick = SystemClock.uptimeMillis();
    }

    public void click(View view) {
        long[] mHits = new long[4];
        //点击事件
        System.arraycopy(mHits, 1, mHits, 0, mHits.length - 1);
        mHits[mHits.length - 1] = SystemClock.uptimeMillis();
        if (mHits[0] >= (SystemClock.uptimeMillis() - 500)) {
            Toast.makeText(DoubleClickActivity.this, "恭喜你，连续点击四次了", Toast.LENGTH_SHORT).show();
        }
    }
}
