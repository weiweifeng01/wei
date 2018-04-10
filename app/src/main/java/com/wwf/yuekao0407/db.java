package com.wwf.yuekao0407;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * author:Created by WeiWeifeng on 2018/4/7.
 */

public class db extends SQLiteOpenHelper{
    public db(Context context) {
        super(context, "test.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //创建表
        sqLiteDatabase.execSQL("create table info(id integer primary key autoincrement,url text not null,json text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
