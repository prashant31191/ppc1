package com.smit.EasyLauncher;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public  class LoginDataBaseAdapter
{
	// ���ڴ�ӡlog
	private static final String	TAG			= "LoginDataBaseAdapter";												

	// ����һ�����ݵ�����
	public static final String	KEY_NUM		= "num";												

	// ����һ�����ݵ�id
	public static final String	KEY_PASSWORD		= "password";

	// ����һ�����ݵ�����
	public static final String	KEY_REMEMBER	= "remember";
	
	// ���ݿ�����Ϊdata
	private static final String	DB_NAME			= "Login.db";
	
	// ���ݿ����
	private static final String	DB_TABLE		= "table1";
	
	// ���ݿ�汾
	private static final int	DB_VERSION		= 1;

	// ����Context����
	private Context				mContext		= null;
	
	//����һ����
	private static final String	DB_CREATE		= "CREATE TABLE " + DB_TABLE + " (" + KEY_NUM + " TEXT,"+ KEY_PASSWORD +" TEXT,"+ KEY_REMEMBER + " TEXT)";

	// ִ��open���������ݿ�ʱ�����淵�ص����ݿ����
	private SQLiteDatabase		mSQLiteDatabase	= null;

	// ��SQLiteOpenHelper�̳й���
	private DatabaseHelper		mDatabaseHelper	= null;
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper
	{
		/* ���캯��-����һ�����ݿ� */
		DatabaseHelper(Context context)
		{
			//������getWritableDatabase() 
			//�� getReadableDatabase()����ʱ
			//�򴴽�һ�����ݿ�
			super(context, DB_NAME, null, DB_VERSION);
			
			
		}

		/* ����һ���� */
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// ���ݿ�û�б�ʱ����һ��
			db.execSQL(DB_CREATE);
		}

		/* �������ݿ� */
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			db.execSQL("DROP TABLE IF EXISTS notes");
			onCreate(db);
		}
	}
	
	/* ���캯��-ȡ��Context */
	public LoginDataBaseAdapter(Context context)
	{
		mContext = context;
	}


	// �����ݿ⣬�������ݿ����
	public void open() throws SQLException
	{
		mDatabaseHelper = new DatabaseHelper(mContext);
		mSQLiteDatabase = mDatabaseHelper.getWritableDatabase();
	}


	// �ر����ݿ�
	public void close()
	{
		mDatabaseHelper.close();
	}

	/* ����һ������ */
	public long insertData(String num, String data , String remember)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_NUM, num);
		initialValues.put(KEY_PASSWORD, data);
		initialValues.put(KEY_REMEMBER, remember);

		return mSQLiteDatabase.insert(DB_TABLE, null, initialValues);
	}

	/* ɾ��һ������ */
	public boolean deleteData(String num)
	{
		return mSQLiteDatabase.delete(DB_TABLE, KEY_NUM + " = '" + num + "'", null) > 0;
	}

	/* ͨ��Cursor��ѯ�������� */
	public Cursor fetchAllData()
	{
		return mSQLiteDatabase.query(DB_TABLE, new String[] { KEY_NUM, KEY_PASSWORD, KEY_REMEMBER}, null, null, null, null, null);
	}

	/* ��ѯָ������ */
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

	/* ����һ������ */
	public boolean updateData(String num, String data, String remember)
	{
		ContentValues args = new ContentValues();

		args.put(KEY_PASSWORD, data);
		args.put(KEY_REMEMBER, remember);

		return mSQLiteDatabase.update(DB_TABLE, args, KEY_NUM + " = '" + num + "'", null) > 0;
	}
	
}

