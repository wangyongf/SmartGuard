package com.yongf.smartguard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;
import android.widget.Toast;

import com.yongf.smartguard.utils.StreamTools;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class SplashActivity extends AppCompatActivity {

    private static final int SHOW_UPDATE_DIALOG = 0;
    private static final int ENTER_HOME = 1;
    private static final int URL_ERROR = 2;
    private static final int NETWORK_ERROR = 3;
    private static final int JSON_ERROR = 4;
    private static final int PROTOCOL_ERROR = 5;

    private TextView tv_splash_version;

    /**
     * 新版本描述信息
     */
    private String description;

    /**
     * 新版本下载地址
     */
    private String apkUrl;

    private TextView tv_update_progress;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        sp = getSharedPreferences("config", MODE_PRIVATE);

        tv_splash_version = (TextView) findViewById(R.id.tv_splash_version);
        tv_splash_version.setText("版本号：" + getVersionName());

        tv_update_progress = (TextView) findViewById(R.id.tv_update_progress);

        //拷贝数据库
        copyDB();

        boolean update = sp.getBoolean("update", false);
        if (update) {
            //检查更新
            checkUpdate();
        } else {
            //自动更新已经关闭
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //进入主页面
                   enterHome();
                }
            }, 2000);
        }


        AlphaAnimation alphaAnimation = new AlphaAnimation(0.2f, 1.0f);
        alphaAnimation.setDuration(1000);
        findViewById(R.id.rl_root_splash).startAnimation(alphaAnimation);
    }

    /**
     * //path 把address.db这个数据库拷贝到data/data/packageName/files/address.db
     * 这里拷贝失败，为什么？
     */
    private void copyDB() {
        //只要拷贝过一次就可
        try {
            File file = new File(getFilesDir(), "address.db");
            if (file.exists() && file.length() > 0) {
                System.out.println("已经拷贝过了");
                return;
            }

            InputStream is = getAssets().open("address.db");
            FileOutputStream fos = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = is.read(buffer)) != -1) {
                fos.write(buffer, 0, len);
            }
            is.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_UPDATE_DIALOG:    //显示升级的对话框
                    System.out.println("SplashActivity.handleMessage" + "显示升级的对话框");
                    showUpdateDialog();
                    break;
                case ENTER_HOME:    //进入主界面
                    enterHome();
                    break;
                case URL_ERROR:     //URL错误
                    enterHome();
                    Toast.makeText(getApplicationContext(), "URL错误", Toast.LENGTH_SHORT).show();
                    break;
                case NETWORK_ERROR:     //网络错误
                    enterHome();
                    Toast.makeText(getApplicationContext(), "网络异常", Toast.LENGTH_SHORT).show();
                    break;
                case JSON_ERROR:    //JSON解析出错
                    enterHome();
                    Toast.makeText(getApplicationContext(), "JSON解析出错", Toast.LENGTH_SHORT).show();
                    break;
                case PROTOCOL_ERROR:    //协议异常
                    enterHome();
                    Toast.makeText(SplashActivity.this, "协议异常", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * 弹出升级对话框
     */
    private void showUpdateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新版本来了！");
//        builder.setCancelable(false);       //强制升级
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //进入主页面
                enterHome();
                dialog.dismiss();
            }
        });
        builder.setMessage(description);
        builder.setPositiveButton("火速升级", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载APK，并且替换安装
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    //SDcard存在
                    //afinal
                    FinalHttp finalHttp = new FinalHttp();
                    finalHttp.download(apkUrl, Environment.getExternalStorageDirectory().getAbsolutePath() + "/SmartGuard2.0.apk", new AjaxCallBack<File>() {
                        @Override
                        public void onLoading(long count, long current) {
                            super.onLoading(count, current);
                            tv_update_progress.setVisibility(View.VISIBLE);
                            //当前下载百分比
                            int progress = (int) (current * 100 / count);

                            tv_update_progress.setText("下载进度：" + progress + "%");
                        }

                        @Override
                        public void onSuccess(File file) {
                            tv_update_progress.setVisibility(View.GONE);

                            super.onSuccess(file);

                            installAPK(file);
                        }

                        /**
                         * 安装APK
                         * @param file
                         */
                        private void installAPK(File file) {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            intent.addCategory("android.intent.category.DEFAULT");
                            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");

                            startActivity(intent);
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo, String strMsg) {
                            tv_update_progress.setVisibility(View.GONE);

                            t.printStackTrace();
                            Toast.makeText(getApplicationContext(), "下载失败", Toast.LENGTH_LONG).show();
                            super.onFailure(t, errorNo, strMsg);
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "没有SD卡，请安装后重试", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });
        builder.setNegativeButton("残忍拒绝", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //暂不升级，进入主页面
                dialog.dismiss();
                enterHome();
            }
        });
        builder.show();
    }

    private void enterHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);

        //关闭当前页面
        finish();
    }

    /**
     * 检查是否有新版本，如果有就提示升级
     */
    private void checkUpdate() {
        new Thread() {
            @Override
            public void run() {
                //URL: http://10.0.2.2:8080/Android/SmartGuardServer/updateinfo.json
                Message message = Message.obtain();
                long startTime = System.currentTimeMillis();
                try {
                    URL url = new URL(getString(R.string.serverurl));
                    //联网
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setConnectTimeout(4000);

                    conn.connect();

                    int responseCode = conn.getResponseCode();
                    if (responseCode == 200) {
                        //联网成功
                        InputStream is = conn.getInputStream();
                        //把流转成String
                        String result = StreamTools.readFromStream(is);

                        System.out.println("SplashActivity.run");

                        //json解析
                        JSONObject object = new JSONObject(result);
                        String version = (String) object.get("version");

                        description = (String) object.get("description");
                        apkUrl = (String) object.get("apkurl");

                        //校验是否有新版本
                        if (version.equals(getVersionName())) {
                            //版本一致，没有新版本，进入主界面
                            message.what = ENTER_HOME;
                        } else {
                            //有新版本，弹出升级对话框
                            message.what = SHOW_UPDATE_DIALOG;
                        }
                    }
                } catch (MalformedURLException e) {
                    message.what = URL_ERROR;
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    message.what = PROTOCOL_ERROR;
                    e.printStackTrace();
                } catch (JSONException e) {
                    message.what = JSON_ERROR;
                    e.printStackTrace();
                } catch (IOException e) {
                    message.what = NETWORK_ERROR;
                    e.printStackTrace();
                } finally {
                    long endTime = System.currentTimeMillis();
                    //花费的时间
                    long dTime = endTime - startTime;
                    //2000
                    if (dTime < 2000) {
                        try {
                            Thread.sleep(2000 - dTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    handler.sendMessage(message);
                }
                super.run();
            }
        }.start();
    }

    /**
     * 得到应用程序的版本信息
     * @return 版本
     */
    private String getVersionName() {
        //用来管理手机的APK
        PackageManager packageManager = getPackageManager();
        //得到指定APK的功能清单文件
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return "";
    }
}
