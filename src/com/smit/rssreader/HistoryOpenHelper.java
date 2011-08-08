package com.smit.rssreader;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HistoryOpenHelper extends SQLiteOpenHelper {
	public static final String DATABASE = "rss_history";
	public static final String TAB_HISTORY = "tab_history";
	public static final String ID = "_id";
	public static final String CATEGORY = "category";
	public static final String TITLE = "title" ;
	public static final String HTTPADDRESS = "httpaddress" ;
	public static final String FAVORITE = "favorite" ;  //标记为“1”代表感兴趣的文章

	public HistoryOpenHelper(Context context){
		super(context, DATABASE, null, 1);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL("create table if not exists " + TAB_HISTORY + " (" 
				+ ID + " integer primary key autoincrement," 
				+ CATEGORY + " varchar,"
				+ TITLE + " varchar,"
				+ HTTPADDRESS+ " varchar," 
				+ FAVORITE + " integer)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

    public Cursor queryWithCH(String cate, String http){
    	SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_HISTORY,null,CATEGORY + "='" + cate + "'" + " AND "
				+ HTTPADDRESS + "='" + http + "'", null, null,null,null);	
    }	
    
    public Cursor queryFavorite(int flag){
    	SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_HISTORY,null,FAVORITE + "='" + flag + "'", null, HTTPADDRESS,null,null);	
    }
    
	public Cursor queryWithCHF(String cate, String http, int flag) {
		SQLiteDatabase db = this.getReadableDatabase();
		return db.query(TAB_HISTORY, null, CATEGORY + "='" + cate + "'"
				+ " AND " + HTTPADDRESS + "='" + http + "'" + " AND "
				+ FAVORITE + "='" + flag + "'", null, null, null, null);
	}
    
	public void insertItem(String cate, String title, String http, int flag){
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(CATEGORY, cate);
		values.put(TITLE, title);
		values.put(HTTPADDRESS, http);
		values.put(FAVORITE, flag);
		db.insert(TAB_HISTORY, null, values);
		db.close();

	}
	
	public void updateFavorite(String cate, String http, int flag) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(FAVORITE, flag);
		db.update(TAB_HISTORY, values, CATEGORY + "='" + cate + "'" + " AND "
				+ HTTPADDRESS + "='" + http + "'", null);
	}
	
	public void deleteFavorite(String http){
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete (TAB_HISTORY, HTTPADDRESS + "='" + http + "'", null);
		db.close();
	}
	
}
