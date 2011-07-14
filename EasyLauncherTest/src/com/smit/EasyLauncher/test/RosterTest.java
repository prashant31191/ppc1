package com.smit.EasyLauncher.test;

import android.database.Cursor;
import android.provider.ContactsContract.Presence;
import android.test.AndroidTestCase;
import android.util.Log;

import com.openims.model.chat.RosterDataBase;

public class RosterTest extends AndroidTestCase {

	RosterDataBase mr;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mr = new RosterDataBase(this.getContext(),"test2");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testDrop(){
		
		mr.dropTable();
	}
	public void testInsert(){
		
		int n = mr.insert( "test@smit", "test", "friend","unavailable");	
		n = mr.insert( "test1@smit", "test1", "friend","unavailable");
		n = mr.insert("test2@smit", "test2", "friend2","unavailable");
		n = mr.insert("test3@smit", "test3", "friend","unavailable");
		n = mr.insert("test4@smit", "test4", "friend2","unavailable");
		n = mr.insert("test5@smit", "test5", "friend","unavailable");
		Log.i("OpenIMSTest","n=" + n);
	}
	
	public void testUpdatePresence(){
		mr.updatePresence("test3@smit", "available");
	}
	public void testQuery(){
		
		Cursor c = mr.queryAll();
		int n = c.getCount();
		
		int columnIndexToId = c.getColumnIndex(RosterDataBase.JID);
		int columnIndexContent = c.getColumnIndex(RosterDataBase.GROUP_NAME);
		int columnIndexId = c.getColumnIndex(RosterDataBase.ID);
		
		String id;
		String toId;
		String content;
		c.moveToFirst();
		for(int i=0; i<n; i++){
			toId = c.getString(columnIndexToId);
			content = c.getString(columnIndexContent);
			id = c.getString(columnIndexId);
			Log.i("OpenIMSTest","id = " + id + " JID=" + toId + " GROUP_NAME="
					+ content);
			c.moveToNext();
		}
	}
	public void testQueryById(){
		Cursor c = mr.queryById(14);

		int n = c.getCount();
		
		int columnIndexToId = c.getColumnIndex(RosterDataBase.JID);
		int columnIndexContent = c.getColumnIndex(RosterDataBase.GROUP_NAME);
		int columnIndexId = c.getColumnIndex(RosterDataBase.ID);
		
		String id;
		String toId;
		String content;
		c.moveToFirst();
		for(int i=0; i<n; i++){
			toId = c.getString(columnIndexToId);
			content = c.getString(columnIndexContent);
			id = c.getString(columnIndexId);
			Log.i("OpenIMSTest","id = " + id + " JID=" + toId + " GROUP_NAME="
					+ content);
			c.moveToNext();
		}
		
	}
	
	public void testUpdate(){
		mr.updateColumn("test1@smit", RosterDataBase.GROUP_NAME, "group1");
		mr.updateColumn("test1@smit", RosterDataBase.NEW_MSG_UREAD, "1");
		mr.updateColumn("test1@smit", RosterDataBase.VCARD, "<card></card>");
	}
	
	public void testQ(){
		mr.queryHaveNewMsgRoster();
	}

}
