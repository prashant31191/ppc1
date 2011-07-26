package com.openims.model.chat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

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
	public static final String VCARD = "vcard";
	public static final String NEW_MSG_UREAD = "umradmsg";
	public static final String NEW_MSG_START_ID= "newMsgStartId";
	public static final String NEW_MSG_TIME = "newMsgTime";
	public static final String MSG_BOX_SHOW = "msgBoxShow";  // 0 will ignore
	public static final String FLAG = "FLAG";
	
	private DatabaseHelper dbHelper;	
	
	private String mAdmin;
	public RosterDataBase(Context context, String mAdmin){	
		this.mAdmin = mAdmin;
		dbHelper = new DatabaseHelper(context,DATABASE_NAME);		
		
	}
	public synchronized void close(){
		if(dbHelper != null){
			dbHelper.close();
		}
	}
	public void deleteRoster(String jid){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.delete(TABLE_NAME, ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"", null);
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
		values.put(NEW_MSG_UREAD, 0);
		values.put(NEW_MSG_START_ID, 0);
		values.put(NEW_MSG_TIME, 0);
		values.put(MSG_BOX_SHOW, 0);
		return (int)db.insert(TABLE_NAME, null, values);
		
	}	
	public int updatePresence(String jid, String presence){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(PRESENCE, presence);
		return db.update(TABLE_NAME, values, ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"", null);
	}
	public String getPresence(String jid){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
	
		Cursor c = db.query(TABLE_NAME, new String[]{PRESENCE}, ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"",
				null, null, null, null);
		String pr = null;
		if(c.moveToFirst()){
			pr = c.getString(0);
		}
		return pr;
	}
	public int updateVcard(String jid, String vcard){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(VCARD, vcard);
		return db.update(TABLE_NAME, values, ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"", null);
	}
	public void clearCachData(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(VCARD, 0);
		db.update(TABLE_NAME, values, ADMIN + "=\"" + mAdmin + "\" ", null);
	
	}
	/**
	 * 
	 * @param jid
	 * @param unReadMsg
	 * @return
	 */	
	public int updateUnReadMsg(String jid, long unReadMsg,long msgStartId){
		Integer startId = 0;
		long time = 0;
		String where = ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"";
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Cursor c = db.query(TABLE_NAME, new String[]{NEW_MSG_TIME,NEW_MSG_START_ID,NEW_MSG_UREAD},
					where, null, null, null, null);
		c.moveToFirst();
		time = c.getLong(0);
		startId = c.getInt(1);
		
		unReadMsg = unReadMsg + c.getInt(2);		
		
		ContentValues values = new ContentValues();
		values.put(NEW_MSG_UREAD, unReadMsg);
		
		values.put(MSG_BOX_SHOW, 1);
		if(startId == 0){
			values.put(NEW_MSG_START_ID, msgStartId);
		}
		if(time == 0){
			time = System.currentTimeMillis();
			values.put(NEW_MSG_TIME, time);
		}
		return db.update(TABLE_NAME, values, where, null);
	}
	public int updateColumn(String jid, String columnName, long value){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(columnName, value);
		return db.update(TABLE_NAME, values, ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"", null);
	}
	/**
	 * 
	 * @param jid can set to null for change all column
	 * @param columnName
	 * @param value
	 * @return
	 */
	public int updateColumn(String jid, String columnName, String value){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(columnName, value);
		String where = ADMIN + "=\"" + mAdmin + "\" AND " + JID+"=\""+jid+"\"";
		if(jid == null){
			where = ADMIN + "=\"" + mAdmin + "\"";
		}
		return db.update(TABLE_NAME, values, where, null);
	}
	
	public int updateGroupName(String groupName,String oldGroupName){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(GROUP_NAME, groupName);
		return db.update(TABLE_NAME, values, ADMIN + "=\"" + mAdmin + "\" AND " + GROUP_NAME+"=\""+oldGroupName+"\"", null);
	
	}
	public Cursor queryHaveNewMsgRoster(){
		String where = ADMIN + "=\"" + mAdmin + "\" AND " 
				+ NEW_MSG_TIME + ">" + 1;
		String order = NEW_MSG_TIME + " DESC";
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, where,
				null,JID,null,order);
		return cursor;
	}
	public void removeAll(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();	
		db.delete(TABLE_NAME, ADMIN + "=\"" + mAdmin + "\"", null);
	}
	public Cursor queryAll(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, ADMIN + "=\"" + mAdmin + "\"",
				null,null,null,GROUP_NAME + " DESC");
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
		Cursor cursor = db.query(TABLE_NAME, null, ADMIN + "=\"" + mAdmin + "\" AND " + JID + "=\"" + jid + "\"",
				null,null,null,null);
		return cursor;
	}
	public boolean isUserExist(String jid){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{ID}, ADMIN + "=\"" + mAdmin + "\" AND " + JID + "=\"" + jid + "\"",
				null,null,null,null);
		if( cursor.getCount()==-1 || cursor.getCount()==0){
			return false;
		}
		return true;
	}
	public boolean isGroupNameExist(String groupName){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{ID}, ADMIN + "=\"" + mAdmin + "\" AND " + GROUP_NAME + "=\"" + groupName + "\"",
				null,null,null,null);
		if( cursor.getCount()==-1 || cursor.getCount()==0){
			return false;
		}
		return true;
	}
	public String[] getGroups(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, new String[]{GROUP_NAME}, ADMIN + "=\"" + mAdmin + "\"",
				null,GROUP_NAME,null,null);
		int n = cursor.getCount();
		if(n == 0){
			return null;
		}
		String[] groups = new String[n];
		cursor.moveToFirst();
		for(int i=0; i<n; i++){
			groups[i] = cursor.getString(0);
			cursor.moveToNext();
		}
		return groups;
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
		VCARD + " BLOB," +
		NEW_MSG_UREAD + " INTEGER," +
		NEW_MSG_START_ID + " INTEGER," +
		NEW_MSG_TIME + " INTEGER," +
		MSG_BOX_SHOW + " INTEGER," +
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
