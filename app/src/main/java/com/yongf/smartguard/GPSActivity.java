package com.yongf.smartguard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class GPSActivity extends AppCompatActivity {

    /**
     * 用到位置服务
     */
    private LocationManager lm;

    private MyLocationListener myLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        lm = (LocationManager) getSystemService(LOCATION_SERVICE);

//        List<String> providers = lm.getAllProviders();
//        for (String l : providers) {
//            System.out.println(l);
//        }
        myLocationListener = new MyLocationListener();
        //注册监听位置服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        //给位置提供者设置条件
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);

        //设置参数细化
//        criteria.setAltitudeRequired(false);    //不要求海拔信息
//        criteria.setBearingRequired(false);     //不要求方位信息
//        criteria.setCostAllowed(true);      //是否允许付费
//        criteria.setPowerRequirement(Criteria.POWER_LOW);       //对耗电量的要求

        String bestProvider = lm.getBestProvider(criteria, true);
        lm.requestLocationUpdates(bestProvider, 60000, 50, myLocationListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消监听位置的服务
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        lm.removeUpdates(myLocationListener);
        myLocationListener = null;
    }

    class MyLocationListener implements LocationListener {

        /**
         * 当位置改变的时候回调
         * @param location
         */
        @Override
        public void onLocationChanged(Location location) {
            String longitude = "经度：" + location.getLongitude();     //经度
            String latitude = "纬度：" + location.getLatitude();        //纬度
            String accuracy = "精确度：" + location.getAccuracy();      //精确度
            TextView textView = new TextView(GPSActivity.this);
            textView.setText(longitude + "\n" + latitude + "\n" + accuracy);

            setContentView(textView);
        }

        /**
         * 当状态发生改变的时候回调：开启-关闭，关闭-开启
         * @param provider
         * @param status
         * @param extras
         */
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        /**
         * 某一个位置提供者可以使用了
         * @param provider
         */
        @Override
        public void onProviderEnabled(String provider) {

        }

        /**
         * 某一个位置提供者不可以使用了
         * @param provider
         */
        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}
