package com.smit.EasyLauncher.test;

import android.test.AndroidTestCase;

import com.openims.model.chat.VCardDataBase;

public class VCardTest extends AndroidTestCase {

	VCardDataBase mr;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mr = new VCardDataBase(this.getContext(),"test2@smit");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	public void testDrop(){
		
		mr.dropTable();
	}
	public void testInsert(){
		mr.insert("test@smit1");
	}
	
}
