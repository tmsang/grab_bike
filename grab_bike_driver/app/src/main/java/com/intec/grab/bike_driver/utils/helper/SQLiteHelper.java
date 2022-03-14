package com.intec.grab.bike_driver.utils.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.intec.grab.bike_driver.configs.Constants;

import org.json.JSONArray;
import org.json.JSONObject;

public class SQLiteHelper extends SQLiteOpenHelper {

    public SQLiteHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        //3rd argument to be passed is CursorFactory instance
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE IF NOT EXISTS configs(id INTEGER PRIMARY KEY, name TEXT, value TEXT, description TEXT)";
        db.execSQL(sql);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        //db.execSQL("DROP TABLE IF EXISTS configs");

        // Create tables again
        //onCreate(db);
    }

    public void action(String actionSql) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.execSQL(actionSql);
        } catch (Exception e) {
            Log.d("SQL action fail: ", e.getMessage());
        }
        db.close();
    }

    public String scalar(String scalarSql) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( scalarSql, null );
        if (cursor.getCount() <= 0) return null;

        cursor.moveToFirst();
        String result = cursor.getString(0);
        db.close();

        return result;
    }

    public JSONArray select(String sql) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =  db.rawQuery( sql, null );

        JSONArray rows = new JSONArray();
        JSONObject row;
        cursor.moveToFirst();

        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            row = new JSONObject();

            for (int index = 0; index < totalColumn; index++) {
                if (cursor.getColumnName(index) == null) continue;
                try {
                    row.put(cursor.getColumnName(index), cursor.getString(index));
                } catch (Exception e) {
                    Log.d("SQL select fail: ", e.getMessage());
                }
            }
            rows.put(row);

            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return rows;
    }
}
