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
    private static final String DATABASE_NAME = "accountsDatabase";
    private static final int DATABASE_VERSION = 1;


    private static final String ACCOUNTS_TABLE_NAME = "atable";
    private static final String ACCOUNTS_TABLE_CREATE = "create table atable (username text not null, password text not null, html text not null, date text not null, uptodate int not null);";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String HTML = "html";
    private static final String UPTODATE = "uptodate";

    private static final String FRIENDS_TABLE_NAME = "friendstable";


    private static final String FRIENDS_TABLE_CREATE = "create table friendstable (username text not null, name text not null, date text not null, monday text, tuesday text, wednesday text, thursday text, friday text);";
    private static final String NAME = "name";
    private static final String DATE = "date";
    private static final String MONDAY = "monday";
    private static final String TUESDAY = "tuesday";
    private static final String WEDNESDAY = "wednesday";
    private static final String THURSDAY = "thursday";
    private static final String FRIDAY = "friday";

    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;

    public DataHandler(Context ctx) {
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper {
        public DataBaseHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(ACCOUNTS_TABLE_CREATE);
            db.execSQL(FRIENDS_TABLE_CREATE);

        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS atable ");
            db.execSQL("DROP TABLE IF EXISTS friendstable ");
            onCreate(db);
        }
    }

    public DataHandler open() {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close() {
        dbhelper.close();
    }

    public long insertData(String username, String password, String html, String date, int uptodate) {

        ContentValues content = new ContentValues();
        content.put(USERNAME, username);
        content.put(PASSWORD, password);
        content.put(HTML, html);
        content.put(DATE, date);
        content.put(UPTODATE, uptodate);
        return db.insertOrThrow(ACCOUNTS_TABLE_NAME, null, content);
    }

    public long insertFriendData(String username, String name, String date, String monday, String tuesday, String wednesday, String thursday, String friday) {

        ContentValues content = new ContentValues();
        content.put(USERNAME, username);
        content.put(NAME, name);
        content.put(DATE, date);

        content.put(MONDAY, monday);
        content.put(TUESDAY, tuesday);
        content.put(WEDNESDAY, wednesday);
        content.put(THURSDAY, thursday);
        content.put(FRIDAY, friday);

        return db.insertOrThrow(FRIENDS_TABLE_NAME, null, content);
    }

    public Cursor returnData() {
        return db.query(ACCOUNTS_TABLE_NAME, new String[]{USERNAME, PASSWORD, HTML, DATE, UPTODATE}, null, null, null, null, null);
    }

    public Cursor returnFriendData() {
        return db.query(FRIENDS_TABLE_NAME, new String[]{USERNAME, NAME, DATE, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY}, null, null, null, null, null);
    }

}
