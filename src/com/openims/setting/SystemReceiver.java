package com.openims.setting;

import com.openims.demo.PushServiceUtil;
import com.openims.utility.LogUtil;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;



public class SystemReceiver extends BroadcastReceiver{
	
	private static final String LOGTAG = LogUtil.makeLogTag(SystemReceiver.class);
    private static final String tag = SystemReceiver.class.getSimpleName()+"--";
    
	public SystemReceiver(){
		
	}
	
	@Override
    public void onReceive(Context context, Intent intent) {
		
		//TODO-ANDREW ���Եȴ�������ͨ����������
		if( "android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			Log.d(LOGTAG,tag+"ϵͳ���� push service");
			ComponentName service =context.startService(new Intent(PushServiceUtil.ACTION_IMSERVICE));
			if (null == service){
			    Log.e(LOGTAG, "Could not start service " + PushServiceUtil.ACTION_IMSERVICE);
			   }
		}
	}
	
}