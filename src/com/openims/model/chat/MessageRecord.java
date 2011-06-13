package com.openims.model.chat;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.openims.utility.DataAccessException;
import com.openims.utility.LogUtil;

public class MessageRecord {
	
	private static final String TAG = LogUtil.makeLogTag(MessageRecord.class);
	private static final String PRE = "Class MessageRecord--";
		
	private static final String DATABASE_NAME = "messageRecord.db";
	private static final int DATABASE_VERSION = 1;
	
	public static final String ID = "_id";
	public static final String FROM = "FROMID";
	public static final String TO = "TOID";
	public static final String CONTENT = "CONTENT";
	public static final String DATE = "DATE";
	public static final String GROUPCODE = "GROUPCODE";
	public static final String SENDER = "SENDER";
	public static final String FLAG = "FLAG";
	
	private static DatabaseHelper dbHelper;
	

	private String tableName = null;
	
	public MessageRecord(Context context, String tableName){
		
		this.tableName = tableName;
		
		dbHelper = new DatabaseHelper(context,DATABASE_NAME);
		// create table if not exist
		SQLiteDatabase db = dbHelper.getWritableDatabase();		
		
		try {
			String sql = getCreateTableSQL(false);
			db.execSQL(sql);
		}  catch (Exception e) {			
			e.printStackTrace();
			Log.e(TAG,PRE+"execSQL error:"+e.getMessage());	
			DataAccessException dataException = new DataAccessException("insert");
			dataException.setErrorType(DataAccessException.TYPE_CREATE);
			throw dataException; 
		}
		
	}
	/**
	 * provider a good way to create data table name
	 * @param myId 	 your unique id
	 * @param yourId your is the object you chat with, such as your friend or group
	 * @return table name
	 */
	static public String getMessageRecordTableName(String myId, String yourId){
		return "TB_" + myId + "_" + yourId;
	}
	/**
	 * 
	 * @param startId include startId
	 * @param nNum  if nNum == -1 return all
	 * @param bSmall
	 * @return
	 */
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
		
		return db.query(tableName,null,where,null,null,null,
				orderBy,limit);
	}
	public void insert(String from, String to, String content, String date){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "insert into " + tableName + " ("+
		FROM +","+ TO +","+ CONTENT +","+DATE+
		") values('" + 		
		from+"','"+
		to+"','"+
		content+ "','"+		
		date+ "'"+
		");";
		Log.d(TAG,PRE+sql);
		try {
			db.execSQL(sql);
		} catch (SQLException e) {			
			e.printStackTrace();
			Log.d(TAG,PRE+"execSQL error:"+e.getMessage());	
			DataAccessException dataException = new DataAccessException("insert");
			dataException.setErrorType(DataAccessException.TYPE_INSERT);
			throw dataException; 
		}
		
	}
	public void insert(String from, String to, String content, String date,
			String groupcode, String sender, String flag){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		String sql = "insert into " + tableName + " ("+
		FROM +","+ TO +","+ 
		CONTENT +","+
		GROUPCODE +","+
		SENDER +","+
		FLAG +","+
		DATE+
		") values('" + 		
		from+"','"+
		to+"','"+
		content+ "','"+	
		groupcode+ "','"+		
		sender+ "','"+		
		flag+ "','"+		
		date+ "'"+
		");";
		Log.d(TAG,PRE+sql);
		try {
			db.execSQL(sql);
		} catch (SQLException e) {			
			e.printStackTrace();
			Log.d(TAG,PRE+"execSQL error:"+e.getMessage());	
			DataAccessException dataException = new DataAccessException("insert");
			dataException.setErrorType(DataAccessException.TYPE_INSERT);
			throw dataException; 
		}
	}
	public Cursor queryAll(){
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, null,
				null, null, null, null,null);
		return cursor;
	}
	public void dropTable(){
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + tableName);
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
		
		sql.append(tableName+"("+
		ID+" INTEGER PRIMARY KEY," +
		FROM+" text not null," + 
		TO+" text not null,"+
		CONTENT+" text,"+
		DATE+" text not null,"+
		GROUPCODE+" text,"+
		SENDER+" text,"+
		FLAG+" text "+ ");" );
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
				db.execSQL("DROP TABLE IF EXISTS " + tableName);
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
