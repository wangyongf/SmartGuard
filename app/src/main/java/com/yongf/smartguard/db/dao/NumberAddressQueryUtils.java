package com.yongf.smartguard.db.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by yongf-new on 2016/2/7 13:16.
 */
public class NumberAddressQueryUtils {

    private static String path = "data/data/com.yongf.smartguard/files/address.db";

    /**
     * 查询归属地
     * @param number 查询的号码
     * @return 号码归属地
     */
    public static String queryNumber(String number) {
        String address = number;
        //path 把address.db这个数据库拷贝到data/data/packageName/files/address.db下
        SQLiteDatabase database = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

        //手机号码 13 14 15  16 18
        //手机号码的正则表达式
        if (number.matches("^1[34568]\\d{9}$")) {
            //手机号码
            Cursor cursor = database.
                    rawQuery("select location from data2 where id = (select outkey from data1 where id = ?)", new String[]{number.substring(0, 7)});
            while (cursor.moveToNext()) {
                address = cursor.getString(0);
            }
            cursor.close();
        } else {
            //其他号码
            switch (number.length()) {
                case 3:
                    //110
                    address = "匪警号码";
                    break;
                case 4:
                    //110
                    address = "模拟器";
                    break;
                case 5:
                    //10086
                    address = "客服电话";
                    break;
                case 7:
                case 8:
                    //本地号码
                    address = "本地号码";
                    break;
                default:
                    //处理长途电话
                    if (number.length() > 10 && number.startsWith("0")) {
                        //010-59790386
                        Cursor cursor = database.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 3)});
                        while (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                        cursor.close();

                        //0855-5979303
                        database.rawQuery("select location from data2 where area = ?", new String[]{number.substring(1, 4)});
                        while (cursor.moveToNext()) {
                            address = cursor.getString(0);
                        }
                    }
                    break;
            }
        }



        return address;
    }
}
