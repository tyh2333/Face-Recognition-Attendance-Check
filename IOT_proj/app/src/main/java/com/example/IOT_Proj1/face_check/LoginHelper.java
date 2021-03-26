package com.example.IOT_Proj1.face_check;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
public class LoginHelper extends SQLiteOpenHelper {
    private SQLiteDatabase db;
    public LoginHelper(Context context) { super(context, "logindb.db", null, 1); }
    /* in SQLiteOpenHelper.java中，only onCreat and onUpgrade are abstract function*/

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String log_table="create table login_table(username text primary key ,password text,phone text)";
        sqLiteDatabase.execSQL(log_table);
    }
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {}

    /* @parameter: username,
     * @parameter: password,
     * @parameter: phone number*/
    public void insertLogin(String username, String password, String phone)
    {
        /* SQLiteDatabase db; */
        db = getWritableDatabase();
        /* 插入数据:
         *   首先需要new一个ContentValues，内容值对象 cv --> 就是一个K,V 键值对，K指明字段名称即列名称，
         * V指明字段值，即单元格内容。
         *   然后将这个键值对放到ContentValues的对象 cv 里面，再把携带着键值对的对象 cv 插入表 */
        ContentValues cValue=new ContentValues();
        cValue.put("username",username);
        cValue.put("password",password);
        cValue.put("phone",phone);
        db.insert("login_table",null, cValue);
    }

    public Cursor selectLogin()
    {
        db = getReadableDatabase();
        Cursor cursor = db.query("login_table",null,null,
                null,null,null,null);
        return cursor;
    }
}
