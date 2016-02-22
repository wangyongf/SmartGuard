package com.yongf.smartguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yongf.smartguard.db.AppLockDBOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * Site: blog.54yongf.com | blog.csdn.net/yongf2014
 * Copyright (C), 1996 - 2016, Scott Wang
 * This program is protected by copyright laws.
 * Project Name: SmartGuard
 * Date: 2016/2/20 22:51
 *
 * @author Scott Wang 1059613472@qq.com
 * @version 1.0
 *          Details:
 *          程序锁的DAO
 */
public class AppLockDao {

    private AppLockDBOpenHelper helper;
    private Context context;

    /**
     * 构造方法
     *
     * @param context 上下文
     */
    public AppLockDao(Context context) {
        helper = new AppLockDBOpenHelper(context);
        this.context = context;
    }

    /**
     * 添加一个要锁定应用的包名
     *
     * @param packName 要锁定的应用程序的包名
     */
    public void add(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("packname", packName);
        db.insert("applock", null, values);

        db.close();

        sendBroadcast();
    }

    private void sendBroadcast() {
        Intent intent = new Intent();
        intent.setAction("com.yongf.smartguard.applockchange");
        context.sendBroadcast(intent);
    }

    /**
     * 删除一个要锁定应用的包名
     *
     * @param packName 要锁定的应用程序的包名
     */
    public void delete(String packName) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("applock", "packname = ?", new String[]{packName});

        db.close();

        sendBroadcast();
    }

    /**
     * 查询程序是否被锁
     *
     * @param packName
     * @return
     */
    public boolean find(String packName) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", null, "packname = ?", new String[]{packName}, null, null, null);
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();

        return result;
    }

    /**
     * 查询全部的包名
     *
     * @return
     */
    public List<String> findAll() {
        List<String> protectPackNames = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.query("applock", new String[]{"packname"}, null, null, null, null, null);
        while (cursor.moveToNext()) {
            protectPackNames.add(cursor.getString(0));
        }
        cursor.close();
        db.close();

        return protectPackNames;
    }
}
