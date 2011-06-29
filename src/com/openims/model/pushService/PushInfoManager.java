package com.openims.model.pushService;

import com.openims.utility.LogUtil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PushInfoManager{
	
	private static final String LOGTAG = LogUtil.makeLogTag(PushInfoManager.class);
	private static final String TAG = LogUtil.makeTag(PushInfoManager.class);
		
	private static final String DATABASE_NAME = "pushRegInfo.db";
	private static final int DATABASE_VERSION = 2;
	private static final String TABLE_NAME = "pushRegInfo";
	
	private static final String USER = "user";
	private static final String DEVELOPER = "developer";
	private static final String PUSH_NAME_KEY = "pushNameKey";
	private static final String PUSHID = "pushID";
	private static final String REC_PACKAGENAME = "packageName";
	private static final String REC_CLASSNAME = "className";
	private static final String TYPE = "type";
	private static final String FLAG = "flag";
	
	private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+
	USER+" text not null," +
	DEVELOPER + " text not null, " +
	PUSHID+" text," + 
	PUSH_NAME_KEY+" text not null,"+
	REC_PACKAGENAME+" text not null,"+
	REC_CLASSNAME+" text not null,"+
	TYPE+" text,"+
	FLAG+" text "+ ");";
	
	private static DatabaseHelper databaseHelper;
	
	public PushInfoManager(Context context){
		databaseHelper = new DatabaseHelper(context);
	}
	public void close(){
		if(databaseHelper != null){
			databaseHelper.close();
		}
	}
	public boolean insertPushInfotoDb(String user,
									  String developer,
									  String pushName,								  
									  String packageName,
									  String className){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String sql = "insert into " + TABLE_NAME + " ("+
		USER +","+ DEVELOPER +","+ PUSH_NAME_KEY +","+REC_PACKAGENAME+","+REC_CLASSNAME+
		") values('" + 		
		user+"','"+
		developer+"','"+
		pushName+ "','"+		
		packageName+ "','"+
		className+ "'"+
		");";
		Log.d(LOGTAG,TAG+sql);
		try {
			db.execSQL(sql);
		} catch (SQLException e) {			
			e.printStackTrace();
			Log.d(LOGTAG,TAG+"execSQL error:"+e.getMessage());
			return false;
		}
		
		return true;
	}
	public boolean getPushInfo(String pushID,StringBuilder packageName,StringBuilder className){
		
		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		
		String where =PUSHID + "='"+pushID+"'";
		String col[]={REC_PACKAGENAME,REC_CLASSNAME};
		Cursor cursor = db.query(TABLE_NAME, col, where, null, null, null, null);
		int n = cursor.getCount();
		cursor.moveToFirst();
		String packageN = cursor.getString(0);
		String classN = cursor.getString(1);
		if(packageN != null && n==1){
			packageName.append(packageN);
			className.append(classN);
			return true;
		}else{
			Log.e(LOGTAG,TAG+"getPushInfo error");
			return false;
		}
	}
	public boolean updatePushID(String pushName,String user,String pushID){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String where = PUSH_NAME_KEY + "='"+pushName+"' AND " + USER + "='" + user + "'";
		ContentValues values = new ContentValues();
		values.put(PUSHID, pushID);
		int n = db.update(TABLE_NAME, values, where, null);
		if(n != 1){
			Log.e(LOGTAG,TAG+"updatePushID error");
			return false;
		}else
			return true;
	}
	public boolean deletePushInfoInDb(String pushName,String user){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String where = PUSH_NAME_KEY + "='"+pushName+"' AND " + USER + "='" + user + "'";
		int n = db.delete(TABLE_NAME, where,null);
		if(n == 1){
			Log.d(LOGTAG,TAG+"Del push inf succuss where pushName:"+pushName);
			return true;
		}
		Log.e(LOGTAG,TAG+"delete Push Inf fail where pushName=='"+pushName+"'");		
		return false;
	}
	
	public boolean isRegPush(String pushName,String user){
		
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String col[]={PUSHID};
		String where = PUSH_NAME_KEY + "='"+pushName+"' AND " + USER + "='" + user + "'";
		Cursor cursor = db.query(TABLE_NAME, col, where, null, null, null, null);
		int n = cursor.getCount();
		cursor.moveToFirst();
		if(n==1){
			String pushId = cursor.getString(0);
			if(pushId != null){
				return true;
			}
		}
		db.delete(TABLE_NAME, where, null);
		Log.i(LOGTAG,TAG+"delete " + where);
		return false;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			db.execSQL(CREATE_TABLE);
			Log.d(LOGTAG,TAG+"Recreat table success");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void reCreateTable() {
		Log.d(LOGTAG,TAG+"CreateTable");
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		
		try {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			db.execSQL(CREATE_TABLE);
			Log.d(LOGTAG,TAG+"Recreat table success");
		} catch (SQLException e) {
			Log.e(LOGTAG,TAG+"Recreat table failure");
		}
	}
	
	
}