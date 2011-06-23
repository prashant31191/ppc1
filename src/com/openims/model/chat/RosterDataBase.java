package com.openims.model.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.openims.utility.DataAccessException;
import com.openims.utility.LogUtil;

public class RosterDataBase {
	
	private static final String TAG = LogUtil.makeLogTag(RosterDataBase.class);
	private static final String PRE = "Class RosterDataBase--";
		
	private static final String DATABASE_NAME = "userInf.db";
	private static final String TABLE_NAME = "roster";
	private static final int DATABASE_VERSION = 1;
	
	public static final String ID = "_id";
	public static final String ADMIN = "admin";
	public static final String ROSTER_ID = "roster_id";
	public static final String USER_NAME = "user_name";
	public static final String JID = "jid";
	public static final String SUB = "sub";
	public static final String NICK = "nick";
	public static final String RANK = "rank";
	public static final String GROUP_NAME = "group_name";
	public static final String PRESENCE = "presence";
	public static final String FLAG = "FLAG";
	
	private static DatabaseHelper dbHelper;	
	
	private String mAdmin;
	public RosterDataBase(Context context, String mAdmin){	
		this.mAdmin = mAdmin;
		dbHelper = new DatabaseHelper(context,DATABASE_NAME);		
		
	}
	public void close(){
		if(dbHelper != null){
			dbHelper.close();
		}
	}
	
	public Cursor queryItems(int startId,int nNum,boolean bSmall){
		
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		String where;
		String orderBy;
		String limit;
		if(bSmall){
			where = ID + "<=" + startId;
			orderBy = ID + " DESC";
		}else{
			where = ID + ">=" + startId;
			orderBy = ID + " ASC";
		}
		if(nNum == -1){
			limit = null;
		}else{
			limit = String.valueOf(nNum);
		}
		if(startId == -1){
			where = null;
		}
		
		return db.query(TABLE_NAME,null,where,null,null,null,
				orderBy,limit);
	}
	
	public int insert(String jid, String username, String groupname, String presence){
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		ContentValues values = new ContentValues();
		values.put(JID, jid);
		values.put(ADMIN, mAdmin);
		values.put(USER_NAME, username);
		values.put(GROUP_NAME, groupname);
		values.put(PRESENCE, presence);
		return (int)db.insert(TABLE_NAME, null, values);
		
	}	
	public int updatePresence(String jid, String presence){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PRESENCE, presence);
		return db.update(TABLE_NAME, values, ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"", null);
	}
	public void removeAll(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();	
		db.delete(TABLE_NAME, ADMIN + "=\"" + mAdmin + "\"", null);
	}
	public Cursor queryAll(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ADMIN + "=\"" + mAdmin + "\"",
				null,null,null,null);
		return cursor;
	}
	public Cursor queryById(long id){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ID + "=" + id,
				null,null,null,null);
		return cursor;
	}
	public Cursor queryByJId(String jid){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, JID + "=\"" + jid + "\"",
				null,null,null,null);
		return cursor;
	}
	public void dropTable(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}
	/**
	 * 
	 * @param always false add if not exists in SQL
	 * @return
	 * @throws Exception
	 */
	private String getCreateTableSQL(boolean always){
		
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ");
		
		if(always == false){
			sql.append("IF NOT EXISTS ");
		}
		
		sql.append(TABLE_NAME+"("+
		ID+" INTEGER PRIMARY KEY," +
		ADMIN+" TEXT,"+
		ROSTER_ID+" INTEGER," + 
		USER_NAME+" TEXT,"+
		JID+" TEXT not null,"+
		SUB+" INTEGER,"+
		NICK+" TEXT,"+
		RANK+" INTEGER,"+
		GROUP_NAME+" TEXT,"+
		PRESENCE + " TEXT," +
		FLAG+" TEXT "+ ");" );
		Log.i(TAG,PRE +"create table--" + sql);
		return sql.toString();
	}
	private class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context, String name) {
			super(context, name, null, DATABASE_VERSION);
			Log.i(TAG,PRE + "DatabaseHelper");
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			try {
				db.execSQL(getCreateTableSQL(true));
			} catch (SQLException e) {				
				e.printStackTrace();
				Log.e(TAG,TAG+"create table fail:");
			} catch (Exception e){
				e.printStackTrace();
			}
			
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			try {
				db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
				db.execSQL(getCreateTableSQL(true));
			} catch (SQLException e) {	
				Log.e(TAG,PRE + "upgrade table fail");
				e.printStackTrace();				
			} catch (Exception e){
				Log.e(TAG, PRE + e.getMessage());
				e.printStackTrace();				
			}			
		}		
	}
	

}
