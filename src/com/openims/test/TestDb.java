package com.openims.test;

import android.content.Context;
import android.util.Log;

import com.openims.model.pushService.PushInfoManager;
import com.openims.utility.LogUtil;

public class TestDb{
	
	private static final String LOGTAG = LogUtil.makeLogTag(TestDb.class);
	private static final String TAG = LogUtil.makeTag(TestDb.class);
	
	
	public static void run(Context context){
		Log.i(LOGTAG,TAG+"begin to run test");
		
		PushInfoManager pushInfoManager = new PushInfoManager(context);
		boolean b =false;
		//pushInfoManager.CreateTable();
		b = pushInfoManager.insertPushInfotoDb("chenyz@smit","andrew", 
				"com.pushWidget", "com.openims", "com.openims.rec");
		Log.i(LOGTAG,TAG+"return b=true:"+String.valueOf(b));
		
		b = pushInfoManager.isRegPush("com.pushWidget","andrew");
		Log.i(LOGTAG,TAG+"return b=false:"+String.valueOf(b));			
		
		b = pushInfoManager.updatePushID("com.pushWidget","andrew", "pushid123");
		Log.i(LOGTAG,TAG+"return b=ture:"+String.valueOf(b));
		
		b = pushInfoManager.isRegPush("com.pushWidget","andrew");
		Log.i(LOGTAG,TAG+"return b=true:"+String.valueOf(b));	
		
		StringBuilder packageName = new StringBuilder();
		StringBuilder className = new StringBuilder();
		b = pushInfoManager.getPushInfo("pushid123", packageName, className);
		Log.i(LOGTAG,TAG+"return b=ture:"+String.valueOf(b));
		Log.i(LOGTAG,TAG+"packageName="+packageName+",className="+className);
		
		//b = pushInfoManager.deletePushInfoInDb("com.pushWidget");
		//Log.i(LOGTAG,TAG+"return b=ture:"+String.valueOf(b));
	}
}