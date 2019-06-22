package com.example.weatherforecast;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBUtils {
    private static DBUtils instance = null;
    private static SQLiteHelper helper;
    private static SQLiteDatabase db;

    public DBUtils(Context context) {
        helper = new SQLiteHelper(context);
        db = helper.getWritableDatabase();
    }

    public static DBUtils getInstance(Context context) {
        if (instance == null) {
            instance = new DBUtils(context);
        }
        return instance;
    }

    public boolean userIsExist(String username) {
        boolean result = false;
        String sql = "SELECT * FROM " + SQLiteHelper.U_USERINFO + "  WHERE userName=?";
        Cursor cursor = db.rawQuery(sql, new String[]{username});
        if (cursor.getCount() > 0) {
            result = true;
        }
        return result;
    }


    public boolean userRegister(String username, String password) {
        boolean result = false;
        ContentValues cv = new ContentValues();
        cv.put("userName", username);
        cv.put("password", password);
        long id = db.insert(SQLiteHelper.U_USERINFO, null, cv);
        if (id > 0) {
            result = true;
        }
        return result;
    }

    public boolean userLogin(String username, String password) {
        boolean result = false;
        String sql = "select * from "+ SQLiteHelper.U_USERINFO +" where username = ? and password = ?";
        Cursor cursor = db.rawQuery(sql, new String[]{username, password});
        if (cursor.getCount() > 0) {
            result = true;
        }
        return result;
    }


}
