package com.openims.test;

import android.test.AndroidTestCase;

import com.openims.R;
import com.openims.model.pushService.PushContent;
import com.openims.model.pushService.PushContentDB;

public class TestTest extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testData(){
		String uread = getContext().getResources().getString(R.string.pushcontent_uread);
		PushContentDB db = new PushContentDB(getContext());
		db.reCreateTable();
		
		for(int i=0;i<10;i++){
			PushContent pc = new PushContent();
			pc.setSize("10K"+String.valueOf(i));
			pc.setContent("hello I love you");
			pc.setLocalPath("sdcard/img");
			pc.setTime("2011-4-1 11:22:55");
			pc.setType("img");			
			pc.setStatus(uread);
			pc.setFlag("111");
			
			db.insertItem(pc);
		}
	}

}
