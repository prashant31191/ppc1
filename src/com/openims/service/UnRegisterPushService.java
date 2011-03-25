package com.openims.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.openims.utility.LogUtil;

public class UnRegisterPushService extends Service{
	private static final String LOGTAG = LogUtil.makeLogTag(RegisterPushService.class);
    private static final String tag = UnRegisterPushService.class.getSimpleName();
    
	public UnRegisterPushService(){
		
	}
	
	@Override
	public void onCreate(){
		Log.d(LOGTAG, tag + "--onCreate()...");
		
	}
	@Override
    public void onStart(Intent intent, int startId) {
        Log.d(LOGTAG, tag + "--onStart()...");
    }

    @Override
    public void onDestroy() {
        Log.d(LOGTAG, tag + "--onDestroy()...");
        
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(LOGTAG, tag + "--onBind()...");
        return null;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(LOGTAG, tag + "--onRebind()...");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(LOGTAG, tag + "--onUnbind()...");
        return true;
    }
}