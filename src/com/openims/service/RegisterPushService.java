package com.openims.service;

import com.openims.test.TestDb;
import com.openims.utility.LogUtil;

import android.app.PendingIntent;
import android.app.Service;
import android.app.PendingIntent.CanceledException;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class RegisterPushService extends Service{
	private static final String LOGTAG = LogUtil.makeLogTag(RegisterPushService.class);
    private static final String TAG = RegisterPushService.class.getSimpleName()+"--";
    
	public RegisterPushService(){
		
	}
	
	@Override
	public void onCreate(){
		Log.d(LOGTAG, TAG + "onCreate()...");
		
	}
	@Override
    public void onStart(Intent intent, int startId) {
        Log.d(LOGTAG, TAG + "onStart()...");
        // TODO-ANDREW unconsider multiple user rigister in the same time
        PendingIntent appIntent = (PendingIntent)intent.getParcelableExtra("app");
        String user = intent.getStringExtra("user");
        Log.i(LOGTAG, TAG + "user:" + user);
        Log.i(LOGTAG, TAG + "targetPackage:" + appIntent.getTargetPackage());
        
        

        
        TestDb.run(this);
    }

    @Override
    public void onDestroy() {
        Log.d(LOGTAG, TAG + "onDestroy()...");
        
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, TAG + "onBind()...");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOGTAG, TAG + "onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
    	Log.d(LOGTAG, TAG + "onUnbind()...");
        return true;
    }
    
}