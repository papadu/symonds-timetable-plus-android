package com.psyngo.michael.symondstimetableplus;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Michael on 29/10/2014.
 */
public class DataHandler {
    private static final String lessonTime = "time";
    private static final String lessonSubject = "subject";
    private static final String lessonTeacher = "teacher";
    private static final String lessonRoom = "room";
    private static final int length = 1;
    private static final String nextTime = "nexttime";
    private static final String whoElseFree = "whoelsefree";
    private static final int backgroundColor = 0;

    private static final String DATABASE_NAME = "timetableDatabase";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CREATE = "create table mytable (time text not null, subject text not null, teacher text not null, room text not null, length int not null, nexttime text not null, whoelsefree text, backgroundcolour int not null);";

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
            db.execSQL("DROP TABLE IF EXISTS mytable ");
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

    public long insertData(String time, String subjectName, String teacher, String room, int length, String nextTime, String whoElseFree, int backgroundColor){
        return 3;
    }

}
