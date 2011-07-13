package com.smit.DeskView.commonclass;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

public class HistoryCityContentProvider extends ContentProvider {
    public static SQLiteDatabase     sqlDB;
    public  static DatabaseHelper dbHelper;
    private static final String  DATABASE_NAME     = "cityhistory.db";
    private static final int        DATABASE_VERSION         = 1;
    private static final String TABLE_NAME   = "cityhistory";
    public static final Uri CONTENT_URI  = Uri.parse("content://com.smit.EasyLauncher");
    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            //create table
        	
        	 db.execSQL("CREATE TABLE  IF NOT EXISTS "+TABLE_NAME+ "( "+
                     "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                     "_city_pinyin TEXT NOT NULL" +
                     ");");
        	
        	  // insert default alarms
            /* String insertMe = "INSERT INTO cityhistory " +
                     "(_city_pinyin) " +
                     "VALUES ";*/
             //db.execSQL(insertMe + "('MySmartABC', 'http://mySmartABC.com');");
             //db.execSQL(insertMe + "('Google', 'http://google.com');");
             //db.execSQL(insertMe + "('Yahoo', 'http://yahoo.com');");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }
    @Override
    public int delete(Uri uri, String s, String[] as) {
    	SQLiteDatabase db= dbHelper.getWritableDatabase();
    	return db.delete(TABLE_NAME, s,as);
    }
    @Override
    public String getType(Uri uri) {
        return null;
    }
    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {
        sqlDB = dbHelper.getWritableDatabase();
        Log.i("===","INSERT");
        long rowId = sqlDB.insert(TABLE_NAME,"", contentvalues);
        if (rowId > 0) {
        	 Log.i("===","ROWID");
            Uri rowUri = ContentUris.appendId(CONTENT_URI.buildUpon(), rowId).build();
            Log.i("===","ROWURI");
            getContext().getContentResolver().notifyChange(rowUri, null);
            return rowUri;
        }
        Log.i("===","ATTER");
        throw new SQLException("Failed to insert row into " + uri);
    }
    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
     return (dbHelper == null) ? false : true;
    }
    
    
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        qb.setTables(TABLE_NAME);
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }
    @Override
    public int update(Uri uri, ContentValues values, String s, String[] as) {
    	SQLiteDatabase db= dbHelper.getWritableDatabase();
    	return db.update(TABLE_NAME, values, s, as);
    }
}

