package com.psyngo.michael.symondstimetableplus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Michael on 29/10/2014.
 */
public class DataHandler {
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String HTML = "html";

    private static final String TABLE_NAME = "atable";
    private static final String DATABASE_NAME = "accountsDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CREATE = "create table atable (username text not null, password text not null, html text not null);";

    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;
    public DataHandler(Context ctx){
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper{
        public DataBaseHelper(Context ctx){
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

                db.execSQL(TABLE_CREATE);


        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS atable ");
            onCreate(db);
        }
    }

    public DataHandler open()
    {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close(){
        dbhelper.close();
    }

    public long insertData(String username, String password, String html){

        ContentValues content = new ContentValues();
        content.put(USERNAME, username);
        content.put(PASSWORD, password);
        content.put(HTML, html);
        return db.insertOrThrow(TABLE_NAME, null, content);
    }

    public Cursor returnData(){
        return  db.query(TABLE_NAME, new String[]{USERNAME, PASSWORD, HTML}, null, null, null, null, null);
    }

}
