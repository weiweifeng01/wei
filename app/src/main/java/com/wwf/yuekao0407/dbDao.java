package com.wwf.yuekao0407;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * author:Created by WeiWeifeng on 2018/4/7.
 */

public class dbDao {

    private final db helep;

    public dbDao(Context context) {
        helep = new db(context);
    }

    //增加
    public void insert(String url, String json) {
        //增加前把旧数据删除
        SQLiteDatabase sd = helep.getWritableDatabase();
        sd.delete("info", "url=?", new String[]{url});
        //添加
        ContentValues values = new ContentValues();
        values.put("url", url);
        values.put("json", json);
        sd.insert("info", null, values);
    }
    //查询
    public String query(String url){
        SQLiteDatabase sd = helep.getWritableDatabase();
        String s = "";
        Cursor cursor = sd.query("info", null, "url=?", new String[]{url}, null, null, null);
        while (cursor.moveToNext()){
            s=cursor.getString(cursor.getColumnIndex("json"));
        }
        return s;
    }

}
