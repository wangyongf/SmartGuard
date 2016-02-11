package com.yongf.smartguard.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Scott Wang
 * @Description:
 * @date 2016/2/11 19:54
 * @Project SmartGuard
 */
public class BlackListDBOpenHelper extends SQLiteOpenHelper {
    /**
     * 数据库创建的构造方法
     * @param context 上下文
     */
    public BlackListDBOpenHelper(Context context) {
        super(context, "blackList.db", null, 1);
    }

    /**
     * 初始化数据库的表结构
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacklist (_id integer primary key autoincrement, number varchar(20), mode varchar(2))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
