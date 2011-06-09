package com.openims.test;

import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.openims.R;
import com.openims.model.pushService.PushContent;
import com.openims.model.pushService.PushContentDB;
import com.openims.utility.LogUtil;

public class PushContentDBTest {

	private static final String LOGTAG = LogUtil.makeLogTag(PushContentDBTest.class);
	private static final String TAG = LogUtil.makeTag(PushContentDBTest.class);
	private Context context;
	
	PushContentDBTest(Context context){
		this.context = context;
	}
	public void testRecreateTable(){
		PushContentDB db = new PushContentDB(context);
		db.reCreateTable();
		
	}
	// test insert 
	public void testInsert(int num){
		String uread = context.getResources().getString(R.string.pushcontent_uread);
		for(int i=0;i<num;i++){
			PushContent pc = new PushContent();
			pc.setSize("10K"+String.valueOf(i));
			pc.setContent("hello I love you");
			pc.setLocalPath("sdcard/img");
			pc.setTime("2011-4-1 11:22:55");
			pc.setType("img");			
			pc.setStatus(uread);
			pc.setFlag("111");
			PushContentDB db = new PushContentDB(context);
			boolean b = db.insertItem(pc);
			if(b){
				Log.d(LOGTAG,TAG+"insert item success");
			}else{
				Log.e(LOGTAG,TAG+"insert item fail");
			}
		}		
	}
	
	public void testDelete(){
		List<PushContent> list = null;
		PushContentDB db = new PushContentDB(context);
		int num = 1;
		list = db.queryItems(0,num);
		PushContent pc = list.get(0);
		db.deleteItem(String.valueOf(pc.getIndex()));
		
	}
	// test query 
	public void testQuery(){
		List<PushContent> list = null;
		PushContentDB db = new PushContentDB(context);
		
		int num = 2;
		Log.d(LOGTAG,TAG+"begin to query "+ String.valueOf(num)+" items");
		list = db.queryItems(0,num);
		printListPushContent(list);
		list = null;
		
		num = 10;
		Log.d(LOGTAG,TAG+"begin to query "+ String.valueOf(num)+" items");
		list = db.queryItems(0,num);
		printListPushContent(list);
		list = null;
		
		num = -1;
		Log.d(LOGTAG,TAG+"begin to query "+ String.valueOf(num)+" items");
		list = db.queryItems(0,num);
		printListPushContent(list);
		return;
	}
	
	private void printListPushContent(List<PushContent> list){
		Iterator<PushContent> it = list.iterator();
		while(it.hasNext()){
			PushContent pc = it.next();
			Log.d(LOGTAG,TAG+pc.toString());			
		}
	}

}
