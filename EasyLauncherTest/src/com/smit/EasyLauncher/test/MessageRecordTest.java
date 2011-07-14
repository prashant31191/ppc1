package com.smit.EasyLauncher.test;

import android.database.Cursor;
import android.test.AndroidTestCase;
import android.util.Log;

import com.openims.model.chat.MessageRecord;

public class MessageRecordTest extends AndroidTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testDrop(){
		MessageRecord mr = new MessageRecord(this.getContext(),"TB_555_77");
		mr.dropTable();
	}
	public void testInsert(){
		MessageRecord mr = new MessageRecord(this.getContext(),"TB_test1__smit_test2__smit");
		mr.insert("555", "11", "hello222", "2452452452");
		for(int i=0; i<5; i++){
			mr.insert("555", "11", "hello222--" + i, "2452452452", "111", "mm", "read");
		}
		
		
		MessageRecord mr2 = new MessageRecord(this.getContext(),"TB_555_77");		
		for(int i=0; i<50; i++){
			mr2.insert("555", "11", "hello222--" + i, "2452452452", "111", "mm", "read");
		}
	}
	
	public void testQuery(){
		MessageRecord mr = new MessageRecord(this.getContext(),"TB_555_77");
		Cursor c = mr.queryItems(20, 10, true);
		int n = c.getCount();
		assertEquals(10, n);
		
		int columnIndexToId = c.getColumnIndex(MessageRecord.TO);
		int columnIndexContent = c.getColumnIndex(MessageRecord.CONTENT);
		int columnIndexId = c.getColumnIndex(MessageRecord.ID);
		
		String id;
		String toId;
		String content;
		c.moveToFirst();
		for(int i=0; i<n; i++){
			toId = c.getString(columnIndexToId);
			content = c.getString(columnIndexContent);
			id = c.getString(columnIndexId);
			Log.i("OpenIMSTest","id = " + id + " toId=" + toId + " content="
					+ content);
			c.moveToNext();
		}
	}

}
