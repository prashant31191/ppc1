package com.openims.model.pushService;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.openims.utility.LogUtil;

public class PushContentDB {

	private static final String LOGTAG = LogUtil.makeLogTag(PushContentDB.class);
	private static final String TAG = LogUtil.makeTag(PushContentDB.class);
		
	private static final String DATABASE_NAME = "pushContent.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "pushContent";
	private static final String INDEX = "index";
	private static final String SIZE = "size";
	private static final String CONTENT = "content";
	private static final String LOCAL_PATH = "localPath";
	private static final String TIME = "time";
	private static final String TYPE = "type";
	private static final String FLAG = "flag";
	
	private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+
	INDEX+" text not null," +
	SIZE+" text," + 
	CONTENT+" text,"+
	LOCAL_PATH+" text,"+
	TIME+" text not null,"+
	TYPE+" text,"+
	FLAG+" text "+ ");";
	
	private static DatabaseHelper databaseHelper;
	
	public PushContentDB(Context context){
		databaseHelper = new DatabaseHelper(context);
	}
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		@Override
		public void onCreate(SQLiteDatabase db) {			
			Log.d(LOGTAG,TAG+"onCreate table:" + CREATE_TABLE);
			try {
				db.execSQL(CREATE_TABLE);
			} catch (SQLException e) {				
				e.printStackTrace();
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	
}
