package com.yongf.smartguard.db.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Description:
 * Site: blog.54yongf.com | blog.csdn.net/yongf2014
 * Copyright (C), 1996 - 2016, Scott Wang
 * This program is protected by copyright laws.
 * Project Name: SmartGuard
 * Date: 2016/2/22 22:59
 *
 * @author Scott Wang 1059613472@qq.com
 * @version 1.0
 *          Details:
 *          病毒数据库查询业务类
 */
public class AntiVirusDao {

    /**
     * 查询一个md5是否在病毒数据库中存在
     *
     * @param md5 md5值
     * @return
     */
    public static boolean isVirus(Context context, String md5) {
        String path = context.getFilesDir() + "/antivirus.db";
        boolean result = false;
        //打开病毒数据库文件
        SQLiteDatabase db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = db.rawQuery("select * from datable where md5 = ?", new String[]{md5});
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();

        return result;
    }
}
