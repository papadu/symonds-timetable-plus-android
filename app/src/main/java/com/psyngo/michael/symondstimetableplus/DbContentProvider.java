package com.psyngo.michael.symondstimetableplus;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

/**
 * Created by Michael on 27/11/2014.
 */
public class DbContentProvider extends ContentProvider {
    DataHandler dbHelper;
    public static final String AUTHORITY = "com.psyngo.michael.symondstimetableplus.user_accounts";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri ATABLE_URI = Uri.withAppendedPath(DbContentProvider.CONTENT_URI, "atable");

    @Override
    public boolean onCreate() {
        dbHelper = new DataHandler(getContext());
        return true;
    }

    @Override
    public int delete(Uri uri, String where, String[] args) {
        String table = getTableName(uri);
        dbHelper.open();
        int i = dbHelper.db.delete(table, where, args);
        return i;
    }

    @Override
    public String getType(Uri arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        String table = getTableName(uri);
        dbHelper.open();
        long value = dbHelper.db.insert(table, null, initialValues);
        return Uri.withAppendedPath(CONTENT_URI, String.valueOf(value));
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table =getTableName(uri);
        dbHelper.open();
        Cursor cursor = dbHelper.db.query(table,  projection, selection, selectionArgs, null, null, sortOrder);
        return cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause,
                      String[] whereArgs) {
        String table = getTableName(uri);
        dbHelper.open();
        int i = dbHelper.db.update(table, values, whereClause, whereArgs);
        return i;
    }

    public static String getTableName(Uri uri){
        String value = uri.getPath();
        value = value.replace("/", "");//we need to remove '/'
        return value;
    }
}
