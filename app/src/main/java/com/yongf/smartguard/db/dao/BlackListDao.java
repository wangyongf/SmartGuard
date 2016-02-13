package com.yongf.smartguard.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yongf.smartguard.db.BlackListDBOpenHelper;
import com.yongf.smartguard.domain.BlackListInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Scott Wang
 * @Description:
 * 黑名单数据库的增删改查业务类
 * @date 2016/2/11 20:12
 * @Project SmartGuard
 */
public class BlackListDao  {
    private BlackListDBOpenHelper helper;

    /**
     * 构造方法
     * @param context 上下文
     */
    public BlackListDao(Context context) {
        helper = new BlackListDBOpenHelper(context);
    }

    /**
     * 查询黑名单号码是否存在
     * @param number 号码
     * @return 存在返回true;否则返回false
     */
    public boolean findOne(String number) {
        boolean result = false;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from blacklist where number = ?", new String[]{number});
        if (cursor.moveToNext()) {
            result = true;
        }
        cursor.close();
        db.close();

        return result;
    }

    /**
     * 查询黑名单号码的拦截模式
     * @param number 号码
     * @return 返回号码的拦截模式，不是黑名单号码返回null
     */
    public String findMode(String number) {
        String result = null;
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select mode from blacklist where number = ?", new String[]{number});
        if (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        cursor.close();
        db.close();

        return result;
    }

    /**
     * 查询全部黑名单号码
     * @return 返回BlackListInfo类型的List结果集
     */
    public List<BlackListInfo> findAll() {
        List<BlackListInfo> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacklist order by _id desc", null);
        if (cursor.moveToNext()) {
            BlackListInfo info = new BlackListInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            result.add(info);
        }
        cursor.close();
        db.close();

        return result;
    }

    /**
     * 查询部分的黑名单号码
     * @param offset 从哪个位置开始获取数据
     * @param length 一次获取的记录数
     * @return 返回BlackListInfo类型的List结果集
     */
    public List<BlackListInfo> findSome(int offset, int length) {
        List<BlackListInfo> result = new ArrayList<>();
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select number,mode from blacklist order by _id desc limit ? offset ?",
                new String[]{String.valueOf(length), String.valueOf(offset)});
        if (cursor.moveToNext()) {
            BlackListInfo info = new BlackListInfo();
            String number = cursor.getString(0);
            String mode = cursor.getString(1);
            info.setNumber(number);
            info.setMode(mode);
            result.add(info);
        }
        cursor.close();
        db.close();

        return result;
    }

    /**
     * 添加黑名单号码
     * @param number 要添加的黑名单号码
     * @param mode 拦截模式：1. 电话拦截 2. 短信拦截 3. 全部拦截
     */
    public void insert(String number, String mode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("number", number);
        values.put("mode", mode);
        db.insert("blacklist", null, values);
        db.close();
    }

    /**
     * 修改黑名单号码的拦截模式
     * @param number 要修改的黑名单号码
     * @param newMode 新的拦截模式
     */
    public void update(String number, String newMode) {
        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("mode", newMode);
        db.update("blacklist", values, "number = ?", new String[]{number});
        db.close();
    }

    /**
     * 删除黑名单号码
     * @param number 黑名单号码
     */
    public void delete(String number) {
        SQLiteDatabase db = helper.getWritableDatabase();
        db.delete("blackl8ist", "number = ?", new String[]{number});
        db.close();
    }
}
