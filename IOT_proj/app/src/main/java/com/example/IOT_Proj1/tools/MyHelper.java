package com.example.IOT_Proj1.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper {
//    create facedata.db for save check info
    public MyHelper(Context context) {
        super(context, "facedata.db", null, 2);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String stu_table = "create table name_id (id text primary key ,name text)"; //name----id: Face Info
        String stu_table2 = "create table time_id(id text ,time text)"; //time----id : Time - id
        db.execSQL(stu_table);
        db.execSQL(stu_table2);
    }
    @Override
    // (2) inherit from virtual base class, must be implemented;
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

    /// (3) insert into table : name-id
    public void Insert(SQLiteDatabase db, String table, String name, String id) {   ///db,table,name,id
        ContentValues cValue = new ContentValues();
        cValue.put("id", id);
        cValue.put("name", name);
        db.insert(table, null, cValue);
    }

    /// (4) insert into table : time-id
    public void Insert_two(SQLiteDatabase db, String table, String time, String id) {   ///db,table,time,id
        ContentValues cValue = new ContentValues();
        cValue.put("id", id);
        cValue.put("time", time);
        db.insert(table, null, cValue); // table是形参和id, time一起传入
    }
    public void Delete(SQLiteDatabase db, String table, String message) {
        String sql = "delete from " + table + " where  " + message;
        db.execSQL(sql);
    }
}
