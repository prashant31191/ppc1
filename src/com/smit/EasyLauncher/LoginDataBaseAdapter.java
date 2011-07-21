package com.smit.EasyLauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public  class LoginDataBaseAdapter
{

	private static final String	TAG			= "LoginDataBaseAdapter";												
	public static final String	KEY_NUM		= "num";												
	public static final String	KEY_PASSWORD		= "password";
	public static final String	KEY_REMEMBER	= "remember";
	private static final String	DB_NAME			= "Login.db";
	private static final String	DB_TABLE		= "table1";
	private static final int	DB_VERSION		= 1;
	private Context				mContext		= null;
	
	//创建一个表
	private static final String	DB_CREATE		= "CREATE TABLE " + DB_TABLE + " (" + KEY_NUM + " TEXT,"+ KEY_PASSWORD +" TEXT,"+ KEY_REMEMBER + " TEXT)";

	private SQLiteDatabase		mSQLiteDatabase	= null;
	private DatabaseHelper		mDatabaseHelper	= null;
		
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		/* 构造函数-创建一个数据库 */
		DatabaseHelper(Context context)
		{
			//当调用getWritableDatabase() 
			//或 getReadableDatabase()方法时
			//则创建一个数据库
			super(context, DB_NAME, null, DB_VERSION);
			
			
		}

		/* 创建一个表 */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// 数据库没有表时创建一个
			db.execSQL(DB_CREATE);
		}

		/* 升级数据库 */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}
	
	/* 构造函数-取得Context */
	public LoginDataBaseAdapter(Context context)
	{
		mContext = context;
	}


	// 打开数据库，返回数据库对象
	public void open() throws SQLException
	{
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}


	// 关闭数据库
	public void close()
	{
		mDatabaseHelper.close();
	}

	/* 插入一条数据 */
	public long insertData(String num, String data , String remember)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NUM, num);
		initialValues.put(KEY_PASSWORD, data);
		initialValues.put(KEY_REMEMBER, remember);

		return mSQLiteDatabase.insert(DB_TABLE, null, initialValues);
	}

	/* 删除一条数据 */
	public boolean deleteData(String num)
	{
		return mSQLiteDatabase.delete(DB_TABLE, KEY_NUM + " = '" + num + "'", null) > 0;
	}

	/* 通过Cursor查询所有数据 */
	public Cursor fetchAllData()
	{
		return mSQLiteDatabase.query(DB_TABLE, new String[] { KEY_NUM, KEY_PASSWORD, KEY_REMEMBER}, null, null, null, null, null);
	}

	/* 查询指定数据 */
	public Cursor fetchData(String num) throws SQLException
	{

		Cursor mCursor =

		mSQLiteDatabase.query(true, DB_TABLE, new String[] { KEY_NUM, KEY_PASSWORD, KEY_REMEMBER}, KEY_NUM + " = '" + num + "'", null, null, null, null, null);

		if (mCursor != null)
		{
			mCursor.moveToFirst();
		}
		return mCursor;

	}

	/* 更新一条数据 */
	public boolean updateData(String num, String data, String remember)
	{
		ContentValues args = new ContentValues();

		args.put(KEY_PASSWORD, data);
		args.put(KEY_REMEMBER, remember);

		return mSQLiteDatabase.update(DB_TABLE, args, KEY_NUM + " = '" + num + "'", null) > 0;
	}
	
}

