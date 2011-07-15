package com.openims.model.pushService;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.openims.utility.LogUtil;
/**
 * 
 * @author Andrew Chan
 * @version 1.0
 * @time 2011-4-1 9:50:40
 * @description this is the class for manage push content database
 */
public class PushContentDB {
	
	private static final String LOGTAG = LogUtil.makeLogTag(PushContentDB.class);
	private static final String TAG = LogUtil.makeTag(PushContentDB.class);
		
	private static final String DATABASE_NAME = "pushContent.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "pushContent";
	
	public static final String INDEX = "_id";
	public static final String SIZE = "size";
	public static final String CONTENT = "content";
	public static final String LOCAL_PATH = "localPath";
	public static final String TIME = "time";
	public static final String TYPE = "type";
	public static final String STATUS = "status";
	public static final String FLAG = "flag";

	
	private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+"("+
	INDEX+" INTEGER PRIMARY KEY," +
	SIZE+" text," + 
	CONTENT+" text,"+
	LOCAL_PATH+" text,"+
	TIME+" text not null,"+
	TYPE+" text,"+
	STATUS+" text,"+
	FLAG+" text "+ ");";
	
	private static DatabaseHelper databaseHelper;
	
	public PushContentDB(Context context){
		databaseHelper = new DatabaseHelper(context);
	}
	public void close(){
		if(databaseHelper != null){
			databaseHelper.close();
		}
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
				Log.e(LOGTAG,TAG+"create table fail:" + CREATE_TABLE);
			}
		}
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
	public boolean reCreateTable(){
		Log.d(LOGTAG,TAG+"CreateTable:" + TABLE_NAME);
		SQLiteDatabase db = databaseHelper.getWritableDatabase();		
		try {
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
			db.execSQL(CREATE_TABLE);
			Log.d(LOGTAG,TAG+"Recreat table success");
		} catch (SQLException e) {
			Log.e(LOGTAG,TAG+"Recreat table failure");
			return false;
		}
		db.close();
		return true;
	}
	// and/insert
	public boolean insertItem(PushContent pushContent){
		
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String sql = "insert into " + TABLE_NAME + " ("+
		INDEX +","+SIZE +","+CONTENT +","+LOCAL_PATH+","+TIME+
		","+TYPE +
		","+STATUS +
		","+FLAG+
		") values(" +
		null+",'"+ 
		pushContent.getSize()+"','"+ 
		pushContent.getContent()+ "','"+		
		pushContent.getLocalPath()+ "','"+
		pushContent.getTime()+ "','"+
		pushContent.getType()+ "','"+
		pushContent.getStatus()+ "','"+
		pushContent.getFlag()+ "'"+
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
	/**
	 *  delete one item
	 *  @param index for the primary key
	 *  @return true for success
	 */		
	public boolean deleteItem(String index){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		String where = INDEX + "='"+index+"'";
		int n = db.delete(TABLE_NAME, where,null);
		if(n == 1){
			Log.d(LOGTAG,TAG+"Del push content succuss where index:"+index);
			return true;
		}
		Log.e(LOGTAG,TAG+"delete Push Inf fail where index:'"+index+"'");		
		return false;
	}		
	/**
	 *  query some items form the database
	 *  
	 *  @param num the number of the latest items to return. 
	 *  If num=-1 return all items
	 */
	public List<PushContent> queryItems(int pageID,int pageSize){
		List<PushContent> list = new ArrayList<PushContent>();
		SQLiteDatabase db = databaseHelper.getReadableDatabase();

//		String sql = "select * from " + TABLE_NAME +       
//        " Limit "+String.valueOf((pageID-1)*pageSize)+" ,"+String.valueOf((pageID)*pageSize);  
//        Cursor cursor = db.rawQuery(sql, null);
		// TODO some system bug here
		Cursor cursor = db.query(TABLE_NAME, null, null,
				null, null, null, null,null);			

		cursor.moveToFirst();	
		int nCount = 1;
		while(cursor.isLast()==false){
			int nCol = cursor.getColumnCount();
			PushContent pushContent = new PushContent();
			for(int i=0;i<nCol;i++){
				String columnName = cursor.getColumnName(i);
				if(INDEX.equals(columnName)){
					pushContent.setIndex(cursor.getString(i));
				}else if(SIZE.equals(columnName)){
					pushContent.setSize(cursor.getString(i));
				}else if(CONTENT.equals(columnName)){
					pushContent.setContent(cursor.getString(i));
				}else if(LOCAL_PATH.equals(columnName)){
					pushContent.setLocalPath(cursor.getString(i));
				}else if(TYPE.equals(columnName)){
					pushContent.setType(cursor.getString(i));
				}else if(TIME.equals(columnName)){
					pushContent.setTime(cursor.getString(i));
				}else if(FLAG.equals(columnName)){
					pushContent.setFlag(cursor.getString(i));
				}else if(STATUS.equals(columnName)){
					pushContent.setStatus(cursor.getString(i));
				}
			}
			list.add(pushContent);
			if(pageSize!=-1 && nCount>=pageSize)
				break;
			nCount++;
			cursor.moveToNext();		
		}
		return list;
	}
	
	public Cursor queryItems(){

		SQLiteDatabase db = databaseHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, null, null,
				null, null, null, null,null);
		return cursor;
	}
	
	// 更新状态
	public boolean updateStatus(long id, String status){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(STATUS, status);
		db.update(TABLE_NAME, values, INDEX+"="+id, null);
		return true;
	}
	// 更新路径
	public boolean updatePath(long id, String path){
		SQLiteDatabase db = databaseHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(LOCAL_PATH, path);
		db.update(TABLE_NAME, values, INDEX+"="+id, null);
		return true;
	}
}
