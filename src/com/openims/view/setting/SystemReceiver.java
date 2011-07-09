package com.openims.view.setting;

import com.openims.utility.LogUtil;
import com.openims.utility.PushServiceUtil;

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
			ComponentName service =context.startService(new Intent(PushServiceUtil.ACTION_SERVICE_CONNECT));
			if (null == service){
			    Log.e(LOGTAG, "Could not start service " + PushServiceUtil.ACTION_SERVICE_CONNECT);
			   }
		}
	}
	
}