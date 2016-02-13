package com.yongf.smartguard.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Xml;

import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Scott Wang
 * @Description:
 * 短信工具类
 * @date 2016/2/13 17:42
 * @Project SmartGuard
 */
public class SMSUtils {

    /**
     * 备份短信的回调接口
     */
    public interface BackupCallBack {
        /**
         * 开始备份的时候，设置进度的最大值
         * @param max 总进度
         */
        public void beforeBackup(int max);

        /**
         * 备份过程中，增加进度
         * @param progress 当前进度
         */
        public void onSMSBackup(int progress);
    }

    /**
     * 备份用户的短信
     * @param context 上下文
     * @param  callBack 备份短信的回调接口
     */
    public static void backupSMS(Context context, BackupCallBack callBack) throws IOException, InterruptedException {
        ContentResolver resolver = context.getContentResolver();
        File file = new File(Environment.getExternalStorageDirectory(), "backup.xml");
        FileOutputStream fos = new FileOutputStream(file);
        //把用户的短信逐条读出来，按照一定的格式写到文件中
        //格式
        /*
        <? xml version encoding>
        <smss>
            <sms>
                <body>你好啊</body>
                <date>1155556456456</date>
                <type>1</type>
                <sender>5556</sender>
            </sms>
        </smss>
         */
        XmlSerializer serializer = Xml.newSerializer();     //获取xml文件的序列化（将内存中的东西写到文件中）器
        //初始化生成器
        serializer.setOutput(fos, "utf-8");
        serializer.startDocument("utf-8", true);
        serializer.startTag(null, "smss");

        Uri uri = Uri.parse("content://sms/");
        Cursor cursor = resolver.query(uri, new String[]{"body", "address", "type", "date"}, null, null, null);
        //开始备份的时候，设置进度条的最大值
        int max = cursor.getCount();
//        pd.setMax(max);
        callBack.beforeBackup(max);
        serializer.attribute(null, "max", max + "");

        int process = 0;
        while (cursor.moveToNext()) {
            Thread.sleep(500);
            String body = cursor.getString(0);
            String address = cursor.getString(1);
            String type = cursor.getString(2);
            String date = cursor.getString(3);
            serializer.startTag(null, "sms");

            serializer.startTag(null, "body");
            serializer.text(body);
            serializer.endTag(null, "body");

            serializer.startTag(null, "address");
            serializer.text(address);
            serializer.endTag(null, "address");

            serializer.startTag(null, "type");
            serializer.text(type);
            serializer.endTag(null, "type");

            serializer.startTag(null, "date");
            serializer.text(date);
            serializer.endTag(null, "date");

            serializer.endTag(null, "sms");

            //备份过程中，增加进度
            process++;
//            pd.setProgress(process);
            callBack.onSMSBackup(process);
        }
        cursor.close();

        serializer.endTag(null, "smss");
        serializer.endDocument();
        fos.close();
    }

    /**
     * 还原短信
     * @param context 上下文
     * @param flag 是否清理原来的短信
     */
    public static void restoreSMS(Context context, boolean flag) {
        Uri uri = Uri.parse("content://sms/");
        if (flag) {
            context.getContentResolver().delete(uri, null, null);
        }

        //1. 读取sd卡上的xml文件


        //2. 读取max

        //3. 读取每一条短信信息， body date type address

        //4. 把短信插入到系统短信应用
        Uri restoreUri = Uri.parse("content://sms/");
        ContentValues values = new ContentValues();
        values.put("body", "我是短信的内容");
        values.put("date", "11111");
        values.put("type", "1");
        values.put("address", "15221382253");
        context.getContentResolver().insert(restoreUri, values);
    }
}
