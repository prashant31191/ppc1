package com.openims.model.chat;

import org.jivesoftware.smackx.packet.VCard;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.openims.utility.LogUtil;

public class VCardDataBase {
	
	private static final String TAG = LogUtil.makeLogTag(VCardDataBase.class);
	private static final String PRE = "Class VCardDataBase--";
		
	private static final String DATABASE_NAME = "vcard.db";
	private static final String TABLE_NAME = "vcard";
	private static final int DATABASE_VERSION = 1;
	
	public static final String ID = "_id";
	public static final String ADMIN = "admin";	
	public static final String JID = "jid";
	public static final String USER_NAME = "user_name";
	public static final String NICK = "nick";
	public static final String SEX = "sex";
	public static final String BIRTHDAY = "birthday";
	public static final String STATE = "state";
	public static final String PROVINCE = "province";
	public static final String CITY = "city";
	public static final String OFFICE = "office";
	public static final String MOB = "mob";
	public static final String EMAIL = "email";
	public static final String WEIBO = "weibo";
	public static final String NOTE = "note";
	public static final String AvaterUrl = "avaterUrl";
	public static final String Avater = "avater";
	public static final String FLAG = "FLAG";
	
	
	private static DatabaseHelper dbHelper;	
	
	private String mAdmin;
	public VCardDataBase(Context context, String mAdmin){	
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
	public long insert(String jid){
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		ContentValues values = new ContentValues();
		values.put(JID, jid);
		values.put(ADMIN, mAdmin);
		
		return db.insert(TABLE_NAME, null, values);
		
	}
	public long insert(String jid, VCard vcard){
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		ContentValues values = new ContentValues();
		values.put(JID, jid);
		values.put(ADMIN, mAdmin);
		values.put(USER_NAME, vcard.getFirstName());
		values.put(NICK, vcard.getNickName());
		values.put(SEX, vcard.getNickName());
		values.put(BIRTHDAY, "null");
		values.put(STATE, vcard.getOrganization());
		values.put(PROVINCE, "null");
		values.put(CITY, "null");
		values.put(OFFICE, vcard.getPhoneWork("CELL"));
		values.put(MOB, vcard.getPhoneHome("CELL"));
		values.put(EMAIL, vcard.getEmailWork());
		values.put(WEIBO, vcard.getEmailHome());
		values.put(NOTE, "null");
		values.put(AvaterUrl, "null");
		values.put(Avater, vcard.getAvatar());
		
		return db.insert(TABLE_NAME, null, values);
		
	}
	
	public int updateColumn(String jid, String columnName, int value){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(columnName, value);
		return db.update(TABLE_NAME, values, ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"", null);
	}
	public int updateColumn(String jid, String columnName, String value){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(columnName, value);
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
	private String getCreateTableSQL(){
		
		StringBuilder sql = new StringBuilder();
		sql.append("CREATE TABLE ");
		
		
		sql.append(TABLE_NAME+"("+
		ID+" INTEGER PRIMARY KEY," +
		ADMIN+" TEXT,"+
		JID+" TEXT not null,"+
		USER_NAME+" TEXT,"+		
		NICK+" TEXT,"+
		SEX+" INTEGER,"+
		BIRTHDAY+" TEXT,"+
		STATE+" TEXT,"+
		PROVINCE + " TEXT," +		
		CITY + " TEXT," +
		OFFICE + " TEXT," +
		MOB + " TEXT," +
		EMAIL + " TEXT," +
		WEIBO + " TEXT," +
		NOTE + " TEXT," +
		AvaterUrl + " TEXT," +
		Avater + " BLOB," +
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
				db.execSQL(getCreateTableSQL());
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
				db.execSQL(getCreateTableSQL());
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
